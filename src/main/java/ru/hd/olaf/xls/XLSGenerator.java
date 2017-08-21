package ru.hd.olaf.xls;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hd.olaf.entities.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by d.v.hozyashev on 17.08.2017.
 */
public class XLSGenerator {

    private static final Logger logger = LoggerFactory.getLogger(XLSGenerator.class);

    public static ByteArrayOutputStream createXLSByteArray(List<Client> clientsOverdue,
                                                           List<Client> clientsRisk,
                                                           List<Client> clientsRating) throws IOException {
        Workbook book = new HSSFWorkbook();

        fillSheet(book, "Просроченные анкеты", clientsOverdue);
        fillSheet(book, "Некорректная степень риска", clientsRisk);
        fillSheet(book, "Некорректная оценка риска", clientsRating);

        ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream();
        book.write(bytesOutput);

        logger.debug("Сохранили полученный xls файл в память.");
        return bytesOutput;
    }

    private static void fillSheet(Workbook book,
                                  String sheetName,
                                  List<Client> clients) {

        Sheet sheet = book.createSheet(sheetName);

        //стили для отображения дат
        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);

        //стили с границами таблицы
        CellStyle styleBorder = book.createCellStyle();
        styleBorder.setBorderTop(BorderStyle.THIN);
        styleBorder.setBorderBottom(BorderStyle.THIN);
        styleBorder.setBorderLeft(BorderStyle.THIN);
        styleBorder.setBorderRight(BorderStyle.THIN);

        Row row = sheet.createRow(0);

        Cell name = row.createCell(0);
        name.setCellValue("Наименование клиента");
        name.setCellStyle(styleBorder);

        Cell createDate = row.createCell(1);
        createDate.setCellValue("Даты заполнения анкеты");
        createDate.setCellStyle(styleBorder);

        Cell updateDate = row.createCell(2);
        updateDate.setCellValue("Дата обновления анкеты");
        updateDate.setCellStyle(styleBorder);

        Cell risk = row.createCell(3);
        risk.setCellValue("Степень риска");
        risk.setCellStyle(styleBorder);

        Cell rating = row.createCell(4);
        rating.setCellValue("Оценка риска");
        rating.setCellStyle(styleBorder);

        Cell branchCode = row.createCell(5);
        branchCode.setCellValue("Код подразделения");
        branchCode.setCellStyle(styleBorder);

        int count = 1;
        for (Client client : clients) {
            logger.debug(String.format("Формируем строку в файле для клиента %s", client.getName()));

            row = sheet.createRow(count++);
            name = row.createCell(0);
            createDate = row.createCell(1);
            updateDate = row.createCell(2);
            risk = row.createCell(3);
            rating = row.createCell(4);
            branchCode = row.createCell(5);

            name.setCellValue(client.getName());

            createDate.setCellStyle(dateStyle);
            updateDate.setCellStyle(dateStyle);

            if (client.getCreateDate() != null)
                createDate.setCellValue(client.getCreateDate());
            if (client.getUpdateDate() != null)
                updateDate.setCellValue(client.getUpdateDate());
            if (client.getRisk() != null)
                risk.setCellValue(client.getRisk());
            if (client.getRating() != null)
                rating.setCellValue(client.getRating());
            if (client.getBranch() != null)
                branchCode.setCellValue(client.getBranch().getCode());

            name.setCellStyle(styleBorder);
            risk.setCellStyle(styleBorder);
            rating.setCellStyle(styleBorder);
            branchCode.setCellStyle(styleBorder);
        }
        // Меняем размер столбца
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(5);
    }
}
