package com.formautomation;

import org.openqa.selenium.WebDriver;
import java.util.Random;

/**
 * Simplified FormFiller using label-based element targeting and standard Selenium methods
 * This approach is more reliable and maintainable than ID-based targeting
 */
public class SimplifiedFormFiller {

    private static final Random random = new Random();

    /**
     * Fill first page using simplified approach
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("=== FILLING FIRST PAGE ===");

            // Click the CBP Users Windows Login button
            System.out.println("1. Clicking CBP Users Windows Login...");
            if (!ElementHelper.clickButtonByText(driver, "CBP Users")) {
                System.out.println("❌ Failed to click CBP Users button");
                return false;
            }
            Thread.sleep(3000);

            // Fill basic information fields by their IDs (these are stable)
            System.out.println("2. Filling basic information...");
            if (!ElementHelper.fillInputByLabel(driver, "Last Name", data.getLastName())) {
                // Fallback to ID if label doesn't work
                if (!fillInputById(driver, "lastName", data.getLastName())) {
                    System.out.println("❌ Failed to fill last name");
                    return false;
                }
            }

            if (!ElementHelper.fillInputByLabel(driver, "First Name", data.getFirstName())) {
                if (!fillInputById(driver, "firstName", data.getFirstName())) {
                    System.out.println("❌ Failed to fill first name");
                    return false;
                }
            }

            if (!ElementHelper.fillDateByLabel(driver, "Date of Birth", data.getDob())) {
                if (!fillInputById(driver, "dob", data.getDob())) {
                    System.out.println("❌ Failed to fill DOB");
                    return false;
                }
            }

            // Click search button
            System.out.println("3. Clicking Search button...");
            if (!ElementHelper.clickButtonByText(driver, "Search")) {
                System.out.println("❌ Failed to click Search button");
                return false;
            }

            // Wait for results and look for TECS Lookout button
            System.out.println("4. Waiting for search results...");
            Thread.sleep(8000);

            System.out.println("5. Looking for Create TECS Lookout button...");
            if (!ElementHelper.clickButtonContaining(driver, "Create TECS Lookout")) {
                System.out.println("❌ Failed to find Create TECS Lookout button");
                return false;
            }

            // Handle tab switching
            Thread.sleep(5000);
            String originalWindow = driver.getWindowHandle();
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    System.out.println("✅ Switched to new tab");
                    break;
                }
            }

            System.out.println("✅ First page completed successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error in first page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill second page using simplified approach
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("=== FILLING SECOND PAGE ===");
            Thread.sleep(5000); // Wait for page to load

            // Close any open dropdowns first
            ElementHelper.closeDropdowns(driver);

            // === MAIN FORM DROPDOWNS ===
            System.out.println("\n--- Main Form Dropdowns ---");

            System.out.println("1. Record Status...");
            if (!ElementHelper.selectDropdownByLabel(driver, "Record Status", "OB - OUTBOUND SUBJECT")) {
                System.out.println("⚠️ Record Status selection failed, continuing...");
            }
            Thread.sleep(2000);

            System.out.println("2. Query Notification...");
            if (!ElementHelper.selectDropdownByLabel(driver, "Query Notification", "0 - NO NOTIFICATION")) {
                System.out.println("⚠️ Query Notification selection failed, continuing...");
            }
            Thread.sleep(2000);

            System.out.println("3. Primary Action...");
            if (!ElementHelper.selectDropdownByLabel(driver, "Primary Action", "4 - REFER TO PASSPORT CONTROL")) {
                System.out.println("⚠️ Primary Action selection failed, continuing...");
            }
            Thread.sleep(2000);

            // Fill primary dates if they appear
            System.out.println("4. Filling Primary Dates...");
            String primaryStartDate = generatePastDate(30, 365);
            String primaryEndDate = generateFutureDate(30, 365);

            ElementHelper.fillDateByLabel(driver, "Primary Start Date", primaryStartDate);
            Thread.sleep(1000);
            ElementHelper.fillDateByLabel(driver, "Primary End Date", primaryEndDate);
            Thread.sleep(2000);

            System.out.println("5. Category...");
            if (!ElementHelper.selectDropdownByLabel(driver, "Category", "AB - AG/BIO COUNTERMEASURES")) {
                System.out.println("⚠️ Category selection failed, continuing...");
            }
            Thread.sleep(2000);

            System.out.println("6. Exclusions...");
            if (!ElementHelper.selectDropdownByLabel(driver, "Exclusions", "ANCX - NIV EXEMPTION")) {
                System.out.println("⚠️ Exclusions selection failed, continuing...");
            }
            Thread.sleep(3000);

            System.out.println("7. Exclusion Site...");
            if (!ElementHelper.selectDropdownByLabel(driver, "Exclusion Site", "PRS - PARIS")) {
                System.out.println("⚠️ Exclusion Site selection failed, continuing...");
            }
            Thread.sleep(2000);

            // === REMARKS ===
            System.out.println("\n--- Remarks ---");
            String remarks = "Automated test entry - Subject under review - Generated at " + System.currentTimeMillis();
            if (!ElementHelper.fillRemarksTextarea(driver, remarks)) {
                System.out.println("⚠️ Remarks filling failed, continuing...");
            }
            Thread.sleep(2000);

            // === PHYSICAL DESCRIPTIONS ===
            System.out.println("\n--- Physical Descriptions ---");

            System.out.println("8. Hispanic...");
            ElementHelper.selectDropdownByLabel(driver, "Hispanic", "Y - YES");
            Thread.sleep(2000);

            System.out.println("9. Height...");
            String heightOption = calculateHeightText(data.getHeight());
            ElementHelper.selectDropdownByLabel(driver, "Height", heightOption);
            Thread.sleep(2000);

            System.out.println("10. Weight...");
            ElementHelper.fillInputByLabel(driver, "Weight", data.getWeight());
            Thread.sleep(2000);

            // === ADD DYNAMIC SECTIONS ===
            System.out.println("\n--- Adding Dynamic Sections ---");

            // Add Sex
            System.out.println("11. Adding Sex...");
            if (ElementHelper.clickButtonContaining(driver, "Add Sex")) {
                Thread.sleep(3000);
                String sex = random.nextBoolean() ? "M - MALE" : "F - FEMALE";
                ElementHelper.selectDropdownByLabel(driver, "Sex", sex);
                Thread.sleep(2000);
            }

            // Add Race
            System.out.println("12. Adding Race...");
            if (ElementHelper.clickButtonContaining(driver, "Add Race")) {
                Thread.sleep(3000);
                String[] races = {"A - ASIAN", "B - BLACK", "W - WHITE", "I - AMERICAN INDIAN"};
                String selectedRace = races[random.nextInt(races.length)];
                ElementHelper.selectDropdownByLabel(driver, "Race", selectedRace);
                Thread.sleep(2000);
            }

            // Add Eye Color
            System.out.println("13. Adding Eye Color...");
            if (ElementHelper.clickButtonContaining(driver, "Add Eye Color")) {
                Thread.sleep(3000);
                String[] eyeColors = {"BL - BLUE", "BR - BROWN", "GR - GREEN", "HZ - HAZEL"};
                String selectedEye = eyeColors[random.nextInt(eyeColors.length)];
                ElementHelper.selectDropdownByLabel(driver, "Eye Color", selectedEye);
                Thread.sleep(2000);
            }

            // Add Hair Color
            System.out.println("14. Adding Hair Color...");
            if (ElementHelper.clickButtonContaining(driver, "Add Hair Color")) {
                Thread.sleep(3000);
                String[] hairColors = {"BL - BLOND", "BR - BROWN", "BK - BLACK", "GR - GRAY"};
                String selectedHair = hairColors[random.nextInt(hairColors.length)];
                ElementHelper.selectDropdownByLabel(driver, "Hair Color", selectedHair);
                Thread.sleep(2000);
            }

            // === NAME SECTION ===
            System.out.println("\n--- Name Section ---");
            System.out.println("15. Adding Name...");
            if (ElementHelper.clickButtonContaining(driver, "Add Name")) {
                Thread.sleep(3000);
                // Fill the newest name inputs
                ElementHelper.fillNewestInput(driver, "text", data.getLastName());
                Thread.sleep(1000);
                ElementHelper.fillNewestInput(driver, "text", data.getFirstName());
                Thread.sleep(2000);
            }

            // === DATE OF BIRTH ===
            System.out.println("\n--- Date of Birth ---");
            System.out.println("16. Adding DOB...");
            if (ElementHelper.clickButtonContaining(driver, "Add DOB")) {
                Thread.sleep(3000);
                ElementHelper.fillNewestInput(driver, "00/00/0000", data.getDob());
                Thread.sleep(2000);
            }

            // === CITIZENSHIP ===
            System.out.println("\n--- Citizenship ---");
            System.out.println("17. Adding Citizenship...");
            if (ElementHelper.clickButtonContaining(driver, "Add Citizenship")) {
                Thread.sleep(3000);
                ElementHelper.selectDropdownByLabel(driver, "Citizenship", "USA - UNITED STATES OF AMERICA");
                Thread.sleep(2000);
            }

            // === PASSPORT ===
            System.out.println("\n--- Passport ---");
            System.out.println("18. Adding Passport...");
            if (ElementHelper.clickButtonContaining(driver, "Add Passport")) {
                Thread.sleep(4000);

                // Passport Type
                ElementHelper.selectDropdownByLabel(driver, "Passport Type", "P - Regular");
                Thread.sleep(1000);

                // Passport Number
                ElementHelper.fillInputByLabel(driver, "Passport #", data.getPassportNumber());
                Thread.sleep(1000);

                // Passport Country
                ElementHelper.selectDropdownByLabel(driver, "Passport Country", "USA - UNITED STATES OF AMERICA");
                Thread.sleep(1000);

                // Passport Issue Date
                ElementHelper.fillDateByLabel(driver, "Passport Issue Date", data.getPassportIssueDate());
                Thread.sleep(1000);

                // Passport Expiry Date
                ElementHelper.fillDateByLabel(driver, "Passport Expiration Date", data.getPassportExpiryDate());
                Thread.sleep(2000);
            }

            // === A NUMBER ===
            System.out.println("\n--- A Number ---");
            System.out.println("19. Adding A#...");
            if (ElementHelper.clickButtonContaining(driver, "Add A#")) {
                Thread.sleep(3000);
                ElementHelper.fillInputByLabel(driver, "A #", data.getaNumber());
                Thread.sleep(2000);
            }

            // === DRIVER'S LICENSE ===
            System.out.println("\n--- Driver's License ---");
            System.out.println("20. Adding Driver's License...");
            if (ElementHelper.clickButtonContaining(driver, "Add Driver")) {
                Thread.sleep(4000);

                // License Number
                ElementHelper.fillInputByLabel(driver, "Driver's License #", data.getDriverLicense());
                Thread.sleep(1000);

                // License State
                String[] states = {"CA - CALIFORNIA", "NY - NEW YORK", "TX - TEXAS", "FL - FLORIDA"};
                String selectedState = states[random.nextInt(states.length)];
                ElementHelper.selectDropdownByLabel(driver, "Driver's License State", selectedState);
                Thread.sleep(2000);
            }

            // === SSN ===
            System.out.println("\n--- SSN ---");
            System.out.println("21. Adding SSN...");
            if (ElementHelper.clickButtonContaining(driver, "Add SSN")) {
                Thread.sleep(3000);
                ElementHelper.fillInputByLabel(driver, "SSN", data.getSsn());
                Thread.sleep(2000);
            }

            // === PHONE NUMBER ===
            System.out.println("\n--- Phone Number ---");
            System.out.println("22. Adding Phone Number...");
            if (ElementHelper.clickButtonContaining(driver, "Add Phone Number")) {
                Thread.sleep(4000);

                // Phone Type
                ElementHelper.selectDropdownByLabel(driver, "Phone Type", "Home");
                Thread.sleep(1000);

                // Phone Country
                ElementHelper.selectDropdownByLabel(driver, "Phone Country", "US");
                Thread.sleep(1000);

                // Phone Number
                String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
                ElementHelper.fillInputByLabel(driver, "Phone #", phoneNumber);
                Thread.sleep(2000);
            }

            // === ADDRESS ===
            System.out.println("\n--- Address ---");
            System.out.println("23. Adding Address...");
            if (ElementHelper.clickButtonContaining(driver, "Add Address")) {
                Thread.sleep(4000);

                // Address Type
                ElementHelper.selectDropdownByLabel(driver, "Type", "Home");
                Thread.sleep(1000);

                // Street
                ElementHelper.fillInputByLabel(driver, "Street", "123 Test Street");
                Thread.sleep(1000);

                // City
                ElementHelper.fillInputByLabel(driver, "City", "Washington");
                Thread.sleep(1000);

                // State (this will be a dropdown)
                ElementHelper.selectDropdownByLabel(driver, "State", "DC - DISTRICT OF COLUMBIA");
                Thread.sleep(1000);

                // Country
                ElementHelper.selectDropdownByLabel(driver, "Country", "USA - UNITED STATES OF AMERICA");
                Thread.sleep(1000);

                // Postal Code
                ElementHelper.fillInputByLabel(driver, "Postal Code", "20001");
                Thread.sleep(2000);
            }

            // === FINAL CLEANUP ===
            System.out.println("\n--- Final Steps ---");
            ElementHelper.closeDropdowns(driver);
            Thread.sleep(2000);

            // Try to submit if submit button is available and enabled
            System.out.println("24. Checking for submit button...");
            ElementHelper.clickButtonByText(driver, "SUBMIT");

            System.out.println("✅ Second page completed successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error in second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ================ HELPER METHODS ================

    /**
     * Fallback method to fill input by ID when label-based approach fails
     */
    private static boolean fillInputById(WebDriver driver, String id, String value) {
        try {
            org.openqa.selenium.WebElement element = driver.findElement(org.openqa.selenium.By.id(id));
            element.clear();
            element.sendKeys(value);
            System.out.println("✅ Filled input by ID: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to fill input by ID: " + id + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate a past date string
     */
    private static String generatePastDate(int minDaysAgo, int maxDaysAgo) {
        java.time.LocalDate date = java.time.LocalDate.now()
                .minusDays(minDaysAgo + random.nextInt(maxDaysAgo - minDaysAgo));
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    /**
     * Generate a future date string
     */
    private static String generateFutureDate(int minDaysAhead, int maxDaysAhead) {
        java.time.LocalDate date = java.time.LocalDate.now()
                .plusDays(minDaysAhead + random.nextInt(maxDaysAhead - minDaysAhead));
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    /**
     * Convert height from format like "5' 10\"" to dropdown text
     */
    private static String calculateHeightText(String heightValue) {
        try {
            // Input format: "5' 10""
            // Output format: "5' 10\""
            return heightValue.replace("\"", "\\\"");
        } catch (Exception e) {
            return "5' 8\""; // Default height
        }
    }
}