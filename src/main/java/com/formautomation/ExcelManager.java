package com.formautomation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle Excel operations with TECS ID support
 */
public class ExcelManager {

    private static final String EXCEL_FILE_NAME = "form_data.xlsx";

    /**
     * Save the generated person data to an Excel file (initial save without TECS ID)
     * @param personData The data to save
     */
    public static void saveDataToExcel(PersonData personData) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Form Data");

            // Create header row - TECS ID as first column
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "tecsId", "firstName", "lastName", "dob", "passportNumber",
                    "passportIssueDate", "passportExpiryDate",
                    "driverLicense", "ssn", "aNumber", "height", "weight"
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

            // Fill in the data row with proper order
            for (int i = 0; i < headers.length; i++) {
                Cell cell = dataRow.createCell(i);
                String value = dataMap.getOrDefault(headers[i], "");
                if (headers[i].equals("tecsId") && (value == null || value.trim().isEmpty())) {
                    value = "PENDING"; // Placeholder until TECS ID is captured
                }
                cell.setCellValue(value);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(EXCEL_FILE_NAME)) {
                workbook.write(outputStream);
            }

            workbook.close();
            System.out.println("✅ Initial data saved to " + EXCEL_FILE_NAME);

        } catch (Exception e) {
            System.out.println("❌ Error saving initial data to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update the Excel file with TECS ID after form submission
     * @param personData The complete data including TECS ID
     */
    public static void updateExcelWithTecsId(PersonData personData) {
        try {
            File excelFile = new File(EXCEL_FILE_NAME);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (excelFile.exists()) {
                // Load existing workbook
                try (FileInputStream inputStream = new FileInputStream(excelFile)) {
                    workbook = new XSSFWorkbook(inputStream);
                    sheet = workbook.getSheet("Form Data");
                }
            } else {
                // Create new workbook if file doesn't exist
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Form Data");

                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {
                        "tecsId", "firstName", "lastName", "dob", "passportNumber",
                        "passportIssueDate", "passportExpiryDate",
                        "driverLicense", "ssn", "aNumber", "height", "weight"
                };

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }
            }

            // Update the data row (row 1, since row 0 is header)
            Row dataRow = sheet.getRow(1);
            if (dataRow == null) {
                dataRow = sheet.createRow(1);
            }

            // Get data using reflection
            Map<String, String> dataMap = new HashMap<>();
            Method[] methods = PersonData.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);

                    // Special case for aNumber
                    if (method.getName().equals("getaNumber")) {
                        fieldName = "aNumber";
                    }

                    Object value = method.invoke(personData);
                    if (value != null) {
                        dataMap.put(fieldName, value.toString());
                    }
                }
            }

            // Update all columns with current data
            String[] headers = {
                    "tecsId", "firstName", "lastName", "dob", "passportNumber",
                    "passportIssueDate", "passportExpiryDate",
                    "driverLicense", "ssn", "aNumber", "height", "weight"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = dataRow.getCell(i);
                if (cell == null) {
                    cell = dataRow.createCell(i);
                }
                String value = dataMap.getOrDefault(headers[i], "");
                cell.setCellValue(value);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(EXCEL_FILE_NAME)) {
                workbook.write(outputStream);
            }

            workbook.close();
            System.out.println("✅ Excel updated with TECS ID: " + personData.getTecsId());
            System.out.println("✅ Complete data saved to " + EXCEL_FILE_NAME);

        } catch (Exception e) {
            System.out.println("❌ Error updating Excel with TECS ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add a new row of data to existing Excel file (for multiple entries)
     * @param personData The data to add
     */
    public static void appendDataToExcel(PersonData personData) {
        try {
            File excelFile = new File(EXCEL_FILE_NAME);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (excelFile.exists()) {
                // Load existing workbook
                try (FileInputStream inputStream = new FileInputStream(excelFile)) {
                    workbook = new XSSFWorkbook(inputStream);
                    sheet = workbook.getSheet("Form Data");
                }
            } else {
                // Create new workbook
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Form Data");

                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {
                        "tecsId", "firstName", "lastName", "dob", "passportNumber",
                        "passportIssueDate", "passportExpiryDate",
                        "driverLicense", "ssn", "aNumber", "height", "weight"
                };

                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }
            }

            // Find next available row
            int nextRowNum = sheet.getLastRowNum() + 1;
            Row dataRow = sheet.createRow(nextRowNum);

            // Get data using reflection
            Map<String, String> dataMap = new HashMap<>();
            Method[] methods = PersonData.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                    String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);

                    if (method.getName().equals("getaNumber")) {
                        fieldName = "aNumber";
                    }

                    Object value = method.invoke(personData);
                    if (value != null) {
                        dataMap.put(fieldName, value.toString());
                    }
                }
            }

            // Fill data row
            String[] headers = {
                    "tecsId", "firstName", "lastName", "dob", "passportNumber",
                    "passportIssueDate", "passportExpiryDate",
                    "driverLicense", "ssn", "aNumber", "height", "weight"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = dataRow.createCell(i);
                String value = dataMap.getOrDefault(headers[i], "");
                cell.setCellValue(value);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(EXCEL_FILE_NAME)) {
                workbook.write(outputStream);
            }

            workbook.close();
            System.out.println("✅ New data row appended to " + EXCEL_FILE_NAME);

        } catch (Exception e) {
            System.out.println("❌ Error appending data to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}