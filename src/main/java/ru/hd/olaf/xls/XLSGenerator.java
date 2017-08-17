package ru.hd.olaf.xls;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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

    public static ByteArrayOutputStream getXLSByteArray(List<Client> clients) throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("364-P");

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
        createDate.setCellValue("Дата заведения клиента");
        createDate.setCellStyle(styleBorder);

        Cell updateDate = row.createCell(2);
        updateDate.setCellValue("Дата обновления анкеты");
        updateDate.setCellStyle(styleBorder);

        Cell branchCode = row.createCell(3);
        branchCode.setCellValue("Код подразделения");
        branchCode.setCellStyle(styleBorder);

        int count = 1;
        for (Client client : clients) {
            logger.debug(String.format("Формируем строку в файле для клиента %s", client.getName()));

            row = sheet.createRow(count++);
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
                updateDate.setCellValue(client.getUpdateDate());

            if (client.getBranch() != null)
                branchCode.setCellValue(client.getBranch().getCode());

            name.setCellStyle(styleBorder);
            branchCode.setCellStyle(styleBorder);
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
}
