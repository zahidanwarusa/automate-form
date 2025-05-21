package com.formautomation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle Excel operations
 */
public class ExcelManager {

    /**
     * Save the generated person data to an Excel file
     * @param personData The data to save
     */
    public static void saveDataToExcel(PersonData personData) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Form Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "firstName", "lastName", "dob", "passportNumber",
                    "passportIssueDate", "passportExpiryDate",
                    "driverLicense", "ssn", "aNumber"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Create data row
            Row dataRow = sheet.createRow(1);

            // Use reflection to get all getter methods from PersonData
            Map<String, String> dataMap = new HashMap<>();
            Method[] methods = PersonData.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);

                    // Special case for aNumber since its getter is getaNumber() not getANumber()
                    if (method.getName().equals("getaNumber")) {
                        fieldName = "aNumber";
                    }

                    Object value = method.invoke(personData);
                    if (value != null) {
                        dataMap.put(fieldName, value.toString());
                    }
                }
            }

            // Fill in the data row
            for (int i = 0; i < headers.length; i++) {
                Cell cell = dataRow.createCell(i);
                cell.setCellValue(dataMap.getOrDefault(headers[i], ""));
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream("form_data.xlsx")) {
                workbook.write(outputStream);
            }

            workbook.close();
            System.out.println("Data saved to form_data.xlsx");

        } catch (Exception e) {
            System.out.println("Error saving data to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}