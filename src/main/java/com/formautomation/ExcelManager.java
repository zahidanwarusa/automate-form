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
 * FIXED: Utility class to handle Excel operations with proper TECS ID tracking
 */
public class ExcelManager {

    private static final String EXCEL_FILE_NAME = "form_data.xlsx";
    private static int currentRowNumber = 1; // Track which row we're working on

    /**
     * Save the generated person data to an Excel file (initial save without TECS ID)
     * Only used for the FIRST run
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

            // Create data row for first run
            currentRowNumber = 1; // First data row
            Row dataRow = sheet.createRow(currentRowNumber);

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
            System.out.println("✅ Initial data saved to " + EXCEL_FILE_NAME + " at row " + currentRowNumber);

        } catch (Exception e) {
            System.out.println("❌ Error saving initial data to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * FIXED: Update the Excel file with TECS ID for the CURRENT ROW being processed
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
                System.out.println("❌ Excel file doesn't exist when trying to update TECS ID");
                return;
            }

            // FIXED: Update the CURRENT row, not always row 1
            Row dataRow = sheet.getRow(currentRowNumber);
            if (dataRow == null) {
                System.out.println("❌ No data row found at row " + currentRowNumber);
                return;
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
            System.out.println("✅ Excel row " + currentRowNumber + " updated with TECS ID: " + personData.getTecsId());

        } catch (Exception e) {
            System.out.println("❌ Error updating Excel with TECS ID: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * FIXED: Add a new row of data to existing Excel file (for runs 2+)
     * This now sets the currentRowNumber so updateExcelWithTecsId knows which row to update
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

            // FIXED: Find next available row and set currentRowNumber
            currentRowNumber = sheet.getLastRowNum() + 1;
            Row dataRow = sheet.createRow(currentRowNumber);

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
            System.out.println("✅ New data row appended to " + EXCEL_FILE_NAME + " at row " + currentRowNumber);

        } catch (Exception e) {
            System.out.println("❌ Error appending data to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reset the row counter (for testing or if you want to start fresh)
     */
    public static void resetRowCounter() {
        currentRowNumber = 1;
        System.out.println("🔄 Row counter reset to 1");
    }

    /**
     * Get the current row number being processed
     */
    public static int getCurrentRowNumber() {
        return currentRowNumber;
    }
}