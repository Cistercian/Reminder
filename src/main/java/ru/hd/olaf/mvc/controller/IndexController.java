package ru.hd.olaf.mvc.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hd.olaf.entities.Branch;
import ru.hd.olaf.entities.Client;
import ru.hd.olaf.mail.SenderEmail;
import ru.hd.olaf.mvc.service.BranchService;
import ru.hd.olaf.mvc.service.ClientService;
import ru.hd.olaf.mvc.service.SettingService;
import ru.hd.olaf.util.JsonResponse;
import ru.hd.olaf.util.LogUtil;
import ru.hd.olaf.util.ErrorsCountEntity;
import ru.hd.olaf.xls.XLSGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Controller
public class IndexController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private SettingService settingService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class); //логгер

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getViewIndex(Model model) {
        logger.debug(LogUtil.getMethodName());

        model.addAttribute("countTotal", clientService.getCountTotal());
        model.addAttribute("countOverdue", clientService.getCountErrors());

        return "index";
    }

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse sendEmail(@RequestParam(value = "address") String address) {
        logger.debug(LogUtil.getMethodName());

        JsonResponse response;
        try {
            Map<String, String> settings = settingService.getSettings();

            InputStream inputStream = new ByteArrayInputStream(
                    XLSGenerator.createXLSByteArray(
                            clientService.getOverdueClients(),
                            clientService.getRiskClients(),
                            clientService.getRatingClients()
                    ).toByteArray());

            response = new JsonResponse(SenderEmail.sendEmail(address, inputStream, settings));
        } catch (IOException e) {
            String message = String.format("Возникла ошибка при попытке создать письмо: %s", e.getMessage());
            logger.debug(message);
            response = new JsonResponse(message);
        }

        return response;
    }

    @RequestMapping(value = "/data/import", method = RequestMethod.POST)
    public String importData(@RequestParam("file") MultipartFile file, Model model) {
        logger.debug(LogUtil.getMethodName());

        String response = "";
        if (!file.isEmpty())
            response = parsingExcelFile(file);
        else
            response = "Не найден импортируемый файл.";

        model.addAttribute("countTotal", clientService.getCountTotal());
        model.addAttribute("countOverdue", clientService.getCountErrors());
        model.addAttribute("response", response);

        return "index";
    }

    @RequestMapping(value = "/getStats", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ErrorsCountEntity> getStats(@RequestParam(value = "type") String type) {
        logger.debug(LogUtil.getMethodName());

        List<ErrorsCountEntity> errors = new ArrayList<ErrorsCountEntity>();

        if ("all".equals(type))
            errors = clientService.getStats();
        else if ("overdue".equals(type))
            errors = clientService.getErrorsOverdue();
        else if ("risk".equals(type))
            errors = clientService.getErrorsRisk();
        else if ("rating".equals(type))
            errors = clientService.getErrorsRating();

        sortErrors(errors);
        return errors;
    }

    /**
     * Служебная функция для корректного парсинга дат
     *
     * @param binder WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }

    @Deprecated
    private String parsingCSVFile(MultipartFile file) {
        logger.debug(LogUtil.getMethodName());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            while (reader.ready()) {
                String line = reader.readLine();
                logger.debug(String.format("Считали строку: %s", line));
                String[] data = line.split(";");

                        /*
                        data[0] - наименование клиента
                        data[1] - дата заведения клиента
                        data[2] - дата обновления клиента
                        data[3] - степень риска
                        data[4] - оценка риска
                        data[5] - код подразделения
                        */
                Branch branch = branchService.getExistOrCreate(data[5]);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Client client = new Client(
                            data[0],
                            sdf.parse(data[1]),
                            data[2] != null && data[2].length() > 0 ? sdf.parse(data[2]) : null,
                            data[3],
                            data[4],
                            branch
                    );

                    clientService.createOrUpdate(client);
                } catch (ParseException e) {
                    logger.debug(String.format("Возникла ошибка при парсинге даты: %s", e.getMessage()));
                }
            }
        } catch (IOException e) {
            logger.debug(String.format("Возникла ошибка при чтении файла: %s", e.getMessage()));
        }
        return null;
    }

    private String parsingExcelFile(MultipartFile file) {
        logger.debug(LogUtil.getMethodName());

        StringBuilder message = new StringBuilder();
        String text;

        if (!checkSettings()) {
            text = "Ошибка: нет настроек структуры загружаемых файлов.<p> Перейдите на страницу \"Настройки\" и укажите номера считываемых столбцов";
            logger.debug(text);
            return text;
        }

        Map<String, Branch> branches = new HashMap<String, Branch>();
        Map<String, String> settings = settingService.getSettings();
        int errors = 0;
        int total;

        try {
            //удаление старых данных
            try {
                clientService.deleteAllData();
            } catch (Exception e) {
                text = String.format("Ошибка удаления текущих данных БД: %s <p>",
                        ExceptionUtils.getRootCause(e).getMessage());

                logger.debug(text);
                return text;
            }

            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);

            total = sheet.getLastRowNum();

            for (int numRow = sheet.getFirstRowNum(); numRow <= total; numRow++) {
                logger.debug(String.format("Парсинг строки %d из %d", numRow, total));
                XSSFRow row = sheet.getRow(numRow);
                try {
                    int column = Integer.parseInt(settings.get("columnBranchCode")) - 1;
                    String branchName = row.getCell(column).getStringCellValue();

                    if (!branches.containsKey(branchName)) {
                        Branch branch = branchService.getExistOrCreate(branchName);
                        if (branch != null)
                            branches.put(branchName, branch);
                        else
                            throw new NullPointerException(String.format("Ошибка поиска подразделения: %s", branchName));
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                    Date createDate;
                    column = Integer.parseInt(settings.get("columnCreateDate")) - 1;
                    if (row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        createDate = row.getCell(column).getDateCellValue();
                    else
                        createDate = row.getCell(column).getStringCellValue() != null && row.getCell(column).getStringCellValue().length() > 0 ?
                                sdf.parse(row.getCell(column).getStringCellValue()) :
                                null;

                    Date updateDate;
                    column = Integer.parseInt(settings.get("columnUpdateDate")) - 1;
                    if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        updateDate = row.getCell(column).getDateCellValue();
                    else
                        updateDate = row.getCell(column).getStringCellValue() != null && row.getCell(column).getStringCellValue().length() > 0 ?
                                sdf.parse(row.getCell(column).getStringCellValue()) :
                                null;

                    Client client = new Client(
                            row.getCell(Integer.parseInt(settings.get("columnName")) - 1).getStringCellValue(),
                            createDate,
                            updateDate,
                            row.getCell(Integer.parseInt(settings.get("columnRisk")) - 1).getStringCellValue(),
                            row.getCell(Integer.parseInt(settings.get("columnRating")) - 1).getStringCellValue(),
                            branches.get(branchName)
                    );

                    clientService.createOrUpdate(client);

                } catch (ParseException e) {
                    text = String.format("Строка: %d: Возникла ошибка при разборе столбца с датой: %s <p>", numRow + 1, e.getMessage());
                    errors++;

                    logger.debug(text);
                    message.append(text);
                } catch (NullPointerException e) {
                    text = String.format("Строка: %d: Ошибка настроек: NullPointExceptions %s <p>", numRow + 1, e.getMessage());
                    errors++;

                    logger.debug(text);
                    message.append(text);
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    text = String.format("Строка: %d: Ошибка настроек: %s <p>", numRow + 1, e.getMessage());
                    errors++;

                    logger.debug(text);
                    message.append(text);
                } catch (RuntimeException e) {
                    text = String.format("Строка: %d: ошибка при сохранении клиента в БД: %s <p>", numRow + 1, e.getMessage());
                    errors++;

                    logger.debug(text);
                    message.append(text);
                }
            }

            text = String.format("Импорт завершен. Импортировано %d объектов, всего строк в файле: %d <p><p>",
                            total - errors + 1, total + 1);
            message.insert(0, text);

        } catch (IOException e) {
            text = String.format("Возникла ошибка при чтении файла: %s <p>", e.getMessage());
            logger.debug(text);
            message.append(text);
        } catch (NotOfficeXmlFileException e) {
            text = String.format("Некорректный формат файла: %s <p>", e.getMessage());
            logger.debug(text);
            message.append(text);
        }

        return message.toString();
    }

    private boolean checkSettings(){
        for (String setting : settingService.getNamesColumns()) {
            if (settingService.getByName(setting) == null)
                return false;
        }

        return true;
    }

    private void sortErrors(List<ErrorsCountEntity> errors){
        Collections.sort(errors, new Comparator<ErrorsCountEntity>() {
            public int compare(ErrorsCountEntity o1, ErrorsCountEntity o2) {
                return o2.getCount().compareTo(o1.getCount());
            }
        });
    }
}
