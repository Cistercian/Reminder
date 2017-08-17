package ru.hd.olaf.mvc.controller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
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
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.entities.Branch;
import ru.hd.olaf.entities.Client;
import ru.hd.olaf.mail.SenderEmail;
import ru.hd.olaf.mvc.service.BranchService;
import ru.hd.olaf.mvc.service.ClientService;
import ru.hd.olaf.util.JsonResponse;
import ru.hd.olaf.util.LogUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Controller
public class IndexController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private BranchService branchService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class); //логгер

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getViewIndex(Model model){
        logger.debug(LogUtil.getMethodName());

        model.addAttribute("countTotal", clientService.getCountTotal());
        model.addAttribute("countOverdue", clientService.getCountOverdue());

        return "index";
    }

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public @ResponseBody JsonResponse sendEmail(@RequestParam(value = "address") String address){
        logger.debug(LogUtil.getMethodName());

        JsonResponse response = new JsonResponse(SenderEmail.sendEmail(address));

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

        return "redirect:/";
    }

    @RequestMapping(value = "/data/overdue", method = RequestMethod.GET)
    public void getRepost(HttpServletResponse response) {
        logger.debug(LogUtil.getMethodName());

        try {
            ByteArrayOutputStream bytesOutput = getXLSByteArray();

            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(bytesOutput.toByteArray()));
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());

            response.setHeader("Content-Disposition", "attachment; filename=\"overdue.xls\"");
            response.flushBuffer();

            logger.debug("Обработка завершена - сформированные данные переданы в response");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteArrayOutputStream getXLSByteArray() throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("364-P");

        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

        Row row = sheet.createRow(0);
        Cell name = row.createCell(0);
        name.setCellValue("Наименование клиента");

        Cell createDate = row.createCell(1);
        createDate.setCellValue("Дата заведения клиента");

        Cell updateDate = row.createCell(2);
        updateDate.setCellValue("Дата обновления анкеты");

        Cell branchCode = row.createCell(3);
        branchCode.setCellValue("Код подразделения");

        for (Client client : clientService.getOverdueClients()){
            logger.debug(String.format("Формируем строку в файле для клиента %s", client.getName()));

            row = sheet.createRow(1);
            name = row.createCell(0);
            createDate = row.createCell(1);
            updateDate = row.createCell(2);
            branchCode = row.createCell(3);

            name.setCellValue(client.getName());

            createDate.setCellStyle(dateStyle);
            updateDate.setCellStyle(dateStyle);

            if (client.getCreateDate() != null)
                createDate.setCellValue(client.getCreateDate());
            if (client.getUpdateDate() != null)
                createDate.setCellValue(client.getUpdateDate());

            if (client.getBranch() != null)
                branchCode.setCellValue(client.getBranch().getCode());
        }
        // Меняем размер столбца
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream();

        book.write(bytesOutput);

        logger.debug("Сохранили полученный xls файл в память.");
        return bytesOutput;
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
        int total = 0;
        String message = "";

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);


            total = sheet.getLastRowNum();

            for (int numRow = sheet.getFirstRowNum(); numRow <= total; numRow++) {
                logger.debug(String.format("Парсинг строки %d из %d", numRow, total));
                XSSFRow row = sheet.getRow(numRow);

                Branch branch = branchService.getExistOrCreate(row.getCell(5).getStringCellValue());
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                    Date createDate;
                    if (row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        createDate = row.getCell(1).getDateCellValue();
                    else
                        createDate = sdf.parse(row.getCell(1).getStringCellValue());

                    Date updateDate;
                    if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
                        updateDate = row.getCell(2).getDateCellValue();
                    else
                        updateDate = row.getCell(2).getStringCellValue() != null && row.getCell(2).getStringCellValue().length() > 0 ?
                                sdf.parse(row.getCell(2).getStringCellValue()) :
                                null;

                    Client client = new Client(
                            row.getCell(0).getStringCellValue(),
                            createDate,
                            updateDate,
                            row.getCell(3).getStringCellValue(),
                            row.getCell(4).getStringCellValue(),
                            branch
                    );

                    clientService.createOrUpdate(client);

                } catch (ParseException e) {
                    logger.debug(String.format("Возникла ошибка при парсинге даты: %s", e.getMessage()));
                    errors++;
                }
            }

            message = String.format("Импорт завершен. Импортировано %d объектов, всего строк в файле: %d",
                    total - errors, total);

        } catch (IOException e) {
            message = String.format("Возникла ошибка при чтении файла: %s", e.getMessage());
            logger.debug(String.format(message));
        }
        return message;
    }
}
