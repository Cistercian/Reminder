package ru.hd.olaf.mvc.controller;

import org.apache.poi.hssf.usermodel.HSSFCell;
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
import ru.hd.olaf.util.OverdueGroupEntity;
import ru.hd.olaf.xls.XLSGenerator;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("countOverdue", clientService.getCountOverdue());

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

            InputStream inputStream = new ByteArrayInputStream(XLSGenerator
                    .getXLSByteArray(clientService.getOverdueClients()).toByteArray());

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
        model.addAttribute("countOverdue", clientService.getCountOverdue());
        model.addAttribute("response", response);

        return "index";
    }

    @RequestMapping(value = "/data/overdue", method = RequestMethod.GET)
    public void getRepost(HttpServletResponse response) {
        logger.debug(LogUtil.getMethodName());

        try {
            ByteArrayOutputStream bytesOutput = XLSGenerator.getXLSByteArray(clientService.getOverdueClients());

            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytesOutput.toByteArray()));

            response.setContentType("application/x-download");
            response.setContentLength(bytesOutput.size());
            response.setHeader("Content-disposition", "attachment; filename=\"overdue1.xls\"");

            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.getOutputStream().close();

            logger.debug("Обработка завершена - сформированные данные переданы в response");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/getStats", method = RequestMethod.GET)
    public
    @ResponseBody
    List<OverdueGroupEntity> getStats() {
        logger.debug(LogUtil.getMethodName());
        return clientService.getStats();
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

        int errors = 0;
        int total;
        String message;

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);

            total = sheet.getLastRowNum();

            for (int numRow = sheet.getFirstRowNum(); numRow <= total; numRow++) {
                logger.debug(String.format("Парсинг строки %d из %d", numRow, total));
                XSSFRow row = sheet.getRow(numRow);
                try {
                    int column = Integer.parseInt(settingService.getByName("columnBranchCode").getValue());
                    Branch branch = branchService.getExistOrCreate(row.getCell(column).getStringCellValue());

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                    Date createDate;
                    column = Integer.parseInt(settingService.getByName("columnCreateDate").getValue());
                    if (row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        createDate = row.getCell(column).getDateCellValue();
                    else
                        createDate = sdf.parse(row.getCell(column).getStringCellValue());

                    Date updateDate;
                    column = Integer.parseInt(settingService.getByName("columnUpdateDate").getValue());
                    if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        updateDate = row.getCell(column).getDateCellValue();
                    else
                        updateDate = row.getCell(column).getStringCellValue() != null && row.getCell(column).getStringCellValue().length() > 0 ?
                                sdf.parse(row.getCell(column).getStringCellValue()) :
                                null;

                    column = Integer.parseInt(settingService.getByName("columnName").getValue());
                    Client client = new Client(
                            row.getCell(column).getStringCellValue(),
                            createDate,
                            updateDate,
                            //row.getCell(3).getStringCellValue(),
                            //row.getCell(4).getStringCellValue(),
                            branch
                    );

                    clientService.createOrUpdate(client);

                } catch (ParseException e) {
                    logger.debug(String.format("Возникла ошибка при парсинге даты: %s", e.getMessage()));
                    errors++;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    logger.debug(String.format("Ошибка настроек: NullPointExceptions %s", e.getMessage()));
                    errors++;
                } catch (IllegalStateException e) {
                    logger.debug(String.format("Ошибка настроек: %s", e.getMessage()));
                    errors++;
                }
            }

            message = String.format("Импорт завершен. Импортировано %d объектов, всего строк в файле: %d",
                    total - errors, total);

        } catch (IOException e) {
            message = String.format("Возникла ошибка при чтении файла: %s", e.getMessage());
            logger.debug(message);
        }
        return message;
    }
}
