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
import ru.hd.olaf.mvc.service.BranchService;
import ru.hd.olaf.mvc.service.ClientService;
import ru.hd.olaf.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Controller
public class DataController {

    @Autowired
    private BranchService branchService;
    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public String getViewData(Model model) {
        logger.debug(LogUtil.getMethodName());

        return "/data/data";
    }

    @RequestMapping(value = "/data/import", method = RequestMethod.POST)
    public
    @ResponseBody
    String importData(@RequestParam("file") MultipartFile file) {
        logger.debug(LogUtil.getMethodName());

        if (!file.isEmpty()) {
            parsingExcelFile(file);
            return "You successfully uploaded file=";
        }
        return "Error";
    }

    @RequestMapping(value = "/data/report", method = RequestMethod.GET)
    public String getRepost(Model model) {
        logger.debug(LogUtil.getMethodName());



        return "index";
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
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int numRow = sheet.getFirstRowNum(); numRow <= sheet.getLastRowNum(); numRow++) {
                logger.debug(String.format("Парсинг строки %d из %d", numRow, sheet.getLastRowNum()));
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
                }
            }
        } catch (IOException e) {
            logger.debug(String.format("Возникла ошибка при чтении файла: %s", e.getMessage()));
        }
        return null;
    }
}
