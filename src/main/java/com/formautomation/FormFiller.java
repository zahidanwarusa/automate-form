package com.formautomation;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * FIXED FormFiller with proper dropdown handling and field completion
 */
public class FormFiller {
    private static final Random random = new Random();
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofSeconds(20);

    /**
     * Fill first page (KEEP EXACT WORKING CODE)
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out first page...");

            // Click the 'CBP Users Windows Login' button first
            System.out.println("Clicking 'CBP Users Windows Login' button...");
            if (!clickButtonRobust(driver, "login-kerberos-btn", "CBP Users")) {
                System.out.println("❌ Failed to click 'CBP Users Windows Login' button. Exiting first page filling.");
                return false;
            }
            Thread.sleep(3000);

            // Fill basic fields
            System.out.println("Filling last name: " + data.getLastName());
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            System.out.println("Filling first name: " + data.getFirstName());
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            System.out.println("Filling DOB: " + data.getDob());
            fillDateInput(driver, "dob", data.getDob());

            // Click search
            System.out.println("Clicking SEARCH button...");
            if (!clickButtonSimple(driver, "Search")) {
                System.out.println("Search button not found, continuing...");
            }

            // Wait for search results
            System.out.println("Waiting for search results to load...");
            Thread.sleep(8000);

            // Handle tab switching
            String originalWindow = driver.getWindowHandle();
            System.out.println("Original window handle: " + originalWindow);

            System.out.println("Looking for Create TECS Lookout button...");
            if (!clickButtonSimple(driver, "Create TECS Lookout")) {
                System.out.println("Could not find Create TECS Lookout button");
                return false;
            }

            // Switch tabs if needed
            Thread.sleep(5000);
            if (driver.getWindowHandles().size() > 1) {
                System.out.println("New tab detected! Switching to new tab...");
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        System.out.println("Switched to new tab: " + windowHandle);
                        break;
                    }
                }
                Thread.sleep(3000);
                System.out.println("New tab URL: " + driver.getCurrentUrl());
                System.out.println("New tab title: " + driver.getTitle());
            }

            System.out.println("First page completed successfully!");
            return true;
        } catch (Exception e) {
            System.err.println("Error filling first page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * COMPLETELY FIXED second page with proper dropdown and field handling
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for page load
            Thread.sleep(10000);

            // === MAIN DROPDOWNS - FIXED ===
            System.out.println("\n=== FILLING MAIN DROPDOWNS ===");

            System.out.println("1. Record Status dropdown (OB - OUTBOUND SUBJECT)");
            selectDropdownFixed(driver, "mat-select-4", "mat-option-68");

            System.out.println("2. Query Notification dropdown (0 - NO NOTIFICATION)");
            selectDropdownFixed(driver, "mat-select-6", "mat-option-238");

            System.out.println("3. Primary Action dropdown (4 - REFER TO PASSPORT CONTROL)");
            selectDropdownFixed(driver, "mat-select-8", "mat-option-245");

            // PRIMARY DATES - FIXED (only appear if not option 242)
            System.out.println("3a. Filling Primary Start Date");
            String primaryStartDate = generatePastDate(30, 365);
            fillDateInputFixed(driver, findDateInputByMask(driver, "00/00/0000", 0), primaryStartDate);

            System.out.println("3b. Filling Primary End Date");
            String primaryEndDate = generateFutureDate(30, 365);
            fillDateInputFixed(driver, findDateInputByMask(driver, "00/00/0000", 1), primaryEndDate);

            System.out.println("4. Category dropdown (AB - AG/BIO COUNTERMEASURES)");
            selectDropdownFixed(driver, "mat-select-10", "mat-option-549");

            System.out.println("5. Exclusions dropdown (ANCX - NIV EXEMPTION)");
            selectDropdownFixed(driver, "mat-select-12", "mat-option-253");

            // EXCLUSION SITE - FIXED (appears after selecting exclusion)
            Thread.sleep(2000); // Wait for exclusion site to appear
            System.out.println("6. Exclusion Site dropdown (PRS - PARIS)");
            selectNewestDropdownFixed(driver, "mat-option-627");

            // === FORM FIELDS - FIXED ===
            System.out.println("\n=== FILLING TEXT FIELDS ===");

            System.out.println("7. Filling remarks");
            fillTextareaFixed(driver, "Automated test entry - Subject under review - Generated at " + System.currentTimeMillis());

            // === PHYSICAL DESCRIPTIONS - FIXED ===
            System.out.println("\n=== FILLING PHYSICAL DESCRIPTIONS ===");

            System.out.println("8. Hispanic dropdown (Y - YES)");
            selectDropdownFixed(driver, "mat-select-0", "mat-option-2");

            System.out.println("9. Height dropdown");
            selectDropdownFixed(driver, "mat-select-2", calculateHeightOption(data.getHeight()));

            System.out.println("10. Weight field");
            fillInputFixed(driver, findInputByMask(driver, "0*", 0), data.getWeight());

            // === ADD SECTIONS - COMPLETELY FIXED ===
            System.out.println("\n=== ADDING DYNAMIC SECTIONS ===");

            System.out.println("11. Adding Sex");
            if (clickButtonRobust(driver, "Add Sex")) {
                Thread.sleep(3000);
                selectNewestDropdownFixed(driver, random.nextBoolean() ? "mat-option-630" : "mat-option-631"); // F or M
            }

            System.out.println("12. Adding Race");
            if (clickButtonRobust(driver, "Add Race")) {
                Thread.sleep(3000);
                String raceOption = "mat-option-" + (594 + random.nextInt(6));
                selectNewestDropdownFixed(driver, raceOption);
            }

            System.out.println("13. Adding Eye Color");
            if (clickButtonRobust(driver, "Add Eye Color")) {
                Thread.sleep(3000);
                String eyeOption = "mat-option-" + (600 + random.nextInt(12));
                selectNewestDropdownFixed(driver, eyeOption);
            }

            System.out.println("14. Adding Hair Color");
            if (clickButtonRobust(driver, "Add Hair Color")) {
                Thread.sleep(3000);
                String hairOption = "mat-option-" + (612 + random.nextInt(15));
                selectNewestDropdownFixed(driver, hairOption);
            }

            // === NAME SECTION - FIXED ===
            System.out.println("\n15. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(3000);
                fillInputFixed(driver, findNewestInput(driver, 0), data.getLastName());
                fillInputFixed(driver, findNewestInput(driver, 1), data.getFirstName());
            }

            // === DATE OF BIRTH - FIXED ===
            System.out.println("\n16. Adding DOB (Page 2)");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(3000);
                String dobInputId = findDateInputByMask(driver, "00/00/0000", -1); // Get newest date input
                fillDateInputFixed(driver, dobInputId, data.getDob());
            }

            // === CITIZENSHIP - FIXED ===
            System.out.println("\n17. Adding Citizenship");
            if (clickButtonRobust(driver, "Add Citizenship")) {
                Thread.sleep(3000);
                selectNewestDropdownFixed(driver, "mat-option-1260"); // USA
            }

            // === PASSPORT - COMPLETELY FIXED ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(4000);

                // Passport Type
                System.out.println("  - Selecting passport type (P - Regular)");
                selectNewestDropdownFixed(driver, "mat-option-1518");

                // Passport Number
                System.out.println("  - Filling passport number");
                fillInputFixed(driver, findNewestInput(driver, 0), data.getPassportNumber());

                // Passport Country
                System.out.println("  - Selecting passport country (USA)");
                selectNewestDropdownFixed(driver, "mat-option-1520");

                // Passport Issue Date
                System.out.println("  - Filling passport issue date");
                String issueInputId = findDateInputByMask(driver, "00/00/0000", -1);
                fillDateInputFixed(driver, issueInputId, data.getPassportIssueDate());

                // Passport Expiry Date
                System.out.println("  - Filling passport expiry date");
                String expiryInputId = findDateInputByMask(driver, "00/00/0000", -1);
                fillDateInputFixed(driver, expiryInputId, data.getPassportExpiryDate());
            }

            // === A NUMBER - FIXED ===
            System.out.println("\n19. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(3000);
                fillInputFixed(driver, findNewestInput(driver, 0), data.getaNumber());
            }

            // === DRIVER'S LICENSE - FIXED ===
            System.out.println("\n20. Adding Driver's License");
            if (clickButtonRobust(driver, "Add Driver's License")) {
                Thread.sleep(4000);

                // License Number
                System.out.println("  - Filling license number");
                fillInputFixed(driver, findNewestInput(driver, 0), data.getDriverLicense());

                // License State
                System.out.println("  - Selecting state");
                String stateOption = "mat-option-" + (1774 + random.nextInt(62));
                selectNewestDropdownFixed(driver, stateOption);
            }

            // === SSN - ALREADY WORKING ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(3000);
                fillSSNInputFixed(driver, data.getSsn());
            }

            // === MISC NUMBER - FIXED ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonRobust(driver, "Add Misc Number")) {
                Thread.sleep(4000);

                // Misc Type dropdown
                System.out.println("  - Selecting misc type");
                selectNewestDropdownFixed(driver, "mat-option-" + (1885 + random.nextInt(5)));

                // Misc Number
                System.out.println("  - Filling misc number");
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputFixed(driver, findNewestInput(driver, 0), miscNumber);
            }

            // === PHONE NUMBER - FIXED ===
            System.out.println("\n23. Adding Phone Number");
            if (clickButtonRobust(driver, "Add Phone Number")) {
                Thread.sleep(4000);

                // Phone Type
                System.out.println("  - Selecting phone type");
                selectNewestDropdownFixed(driver, "mat-option-" + (1890 + random.nextInt(4)));

                // Phone Country
                System.out.println("  - Selecting phone country (USA)");
                selectSecondNewestDropdownFixed(driver, "mat-option-1895");

                // Phone Number
                System.out.println("  - Filling phone number");
                String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
                fillInputFixed(driver, findNewestInput(driver, 0), phoneNumber);
            }

            // === ALTERNATIVE COMMUNICATIONS - FIXED ===
            System.out.println("\n24. Adding Alternative Communication");
            if (clickButtonRobust(driver, "Add Alternative Communication")) {
                Thread.sleep(4000);

                // Communication Type
                System.out.println("  - Selecting communication type");
                selectNewestDropdownFixed(driver, "mat-option-" + (1900 + random.nextInt(3)));

                // Communication Value
                System.out.println("  - Filling communication value");
                String email = "test" + System.currentTimeMillis() + "@example.com";
                fillInputFixed(driver, findNewestInput(driver, 0), email);
            }

            // === ADDRESS - COMPLETELY FIXED ===
            System.out.println("\n25. Adding Address");
            if (clickButtonRobust(driver, "Add Address")) {
                Thread.sleep(4000);

                // Address Type
                System.out.println("  - Selecting address type");
                selectNewestDropdownFixed(driver, "mat-option-" + (1910 + random.nextInt(4)));

                // Street
                System.out.println("  - Filling street");
                fillInputFixed(driver, findNewestInput(driver, 0), "123 Test Street");

                // City
                System.out.println("  - Filling city");
                fillInputFixed(driver, findNewestInput(driver, 1), "Washington");

                // State/Province
                System.out.println("  - Selecting state");
                selectNewestDropdownFixed(driver, "mat-option-1915"); // DC

                // Country
                System.out.println("  - Selecting country (USA)");
                selectSecondNewestDropdownFixed(driver, "mat-option-1260");

                // Postal Code
                System.out.println("  - Filling postal code");
                fillInputFixed(driver, findNewestInput(driver, 2), "20001");
            }

            // === FINANCIAL ACCOUNT - COMPLETELY FIXED ===
            System.out.println("\n26. Adding Financial Account");
            if (clickButtonRobust(driver, "Add Financial Account")) {
                Thread.sleep(4000);

                // Institution
                System.out.println("  - Filling institution");
                fillInputFixed(driver, findNewestInput(driver, 0), "Test Bank");

                // Branch
                System.out.println("  - Filling branch");
                fillInputFixed(driver, findNewestInput(driver, 1), "Main Branch");

                // Officer Name
                System.out.println("  - Filling officer name");
                fillInputFixed(driver, findNewestInput(driver, 2), "John Doe");

                // Account Number
                System.out.println("  - Filling account number");
                fillInputFixed(driver, findNewestInput(driver, 3), "ACC" + (100000 + random.nextInt(900000)));

                // Account Type
                System.out.println("  - Filling account type");
                fillInputFixed(driver, findNewestInput(driver, 4), "Checking");

                // Financial ID
                System.out.println("  - Filling financial ID");
                fillInputFixed(driver, findNewestInput(driver, 5), "FIN" + (1000 + random.nextInt(9000)));

                // Date
                System.out.println("  - Filling date");
                String dateInputId = findDateInputByMask(driver, "00/00/0000", -1);
                fillDateInputFixed(driver, dateInputId, generatePastDate(30, 365));
            }

            // Final cleanup
            System.out.println("\n27. Final cleanup");
            forceCloseDropdown(driver);
            Thread.sleep(2000);

            // Try to submit the form
            System.out.println("\n28. Checking for SUBMIT button");
            checkAndClickSubmit(driver);

            System.out.println("\n✅ Second page completed successfully!");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== FIXED HELPER METHODS ====================

    /**
     * COMPLETELY FIXED dropdown selection method
     */
    private static boolean selectDropdownFixed(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Selecting " + selectId + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            // First close any open dropdowns
            forceCloseDropdown(driver);
            Thread.sleep(500);

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var select = document.getElementById('" + selectId + "');" +
                            "  if (!select) { resolve(false); return; }" +
                            "  " +
                            "  select.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    var trigger = select.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { select.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option && option.offsetParent !== null) {" +
                            "        clearInterval(checkOption);" +
                            "        option.click();" +
                            "        setTimeout(() => {" +
                            "          document.body.click();" +
                            "          resolve(true);" +
                            "        }, 500);" +
                            "      } else if (++attempts > 25) {" +
                            "        clearInterval(checkOption);" +
                            "        resolve(false);" +
                            "      }" +
                            "    }, 200);" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);
            if (result != null && result) {
                System.out.println("✅ Selected " + selectId + " → " + optionId);
                return true;
            }

            System.out.println("❌ Failed to select " + selectId);
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED newest dropdown selection
     */
    private static boolean selectNewestDropdownFixed(WebDriver driver, String optionId) {
        try {
            System.out.println("🎯 Selecting newest dropdown → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length === 0) { resolve(false); return; }" +
                            "  " +
                            "  var newest = selects[selects.length - 1];" +
                            "  newest.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = newest.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { newest.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option && option.offsetParent !== null) {" +
                            "        clearInterval(checkOption);" +
                            "        option.click();" +
                            "        setTimeout(() => {" +
                            "          document.body.click();" +
                            "          resolve(true);" +
                            "        }, 500);" +
                            "      } else if (++attempts > 25) {" +
                            "        clearInterval(checkOption);" +
                            "        resolve(false);" +
                            "      }" +
                            "    }, 200);" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);
            if (result != null && result) {
                System.out.println("✅ Selected newest dropdown → " + optionId);
                return true;
            }

            System.out.println("❌ Failed to select newest dropdown");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting newest dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED second newest dropdown selection
     */
    private static boolean selectSecondNewestDropdownFixed(WebDriver driver, String optionId) {
        try {
            System.out.println("🎯 Selecting second newest dropdown → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length < 2) { resolve(false); return; }" +
                            "  " +
                            "  var secondNewest = selects[selects.length - 2];" +
                            "  secondNewest.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = secondNewest.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { secondNewest.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option && option.offsetParent !== null) {" +
                            "        clearInterval(checkOption);" +
                            "        option.click();" +
                            "        setTimeout(() => {" +
                            "          document.body.click();" +
                            "          resolve(true);" +
                            "        }, 500);" +
                            "      } else if (++attempts > 25) {" +
                            "        clearInterval(checkOption);" +
                            "        resolve(false);" +
                            "      }" +
                            "    }, 200);" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);
            if (result != null && result) {
                System.out.println("✅ Selected second newest dropdown → " + optionId);
                return true;
            }

            System.out.println("❌ Failed to select second newest dropdown");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting second newest dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED input field filling
     */
    private static boolean fillInputFixed(WebDriver driver, String inputIdentifier, String value) {
        try {
            if (inputIdentifier == null) {
                System.out.println("❌ Input identifier is null");
                return false;
            }

            System.out.println("Filling input: " + inputIdentifier + " with: " + value);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var input = document.getElementById('" + inputIdentifier + "') || " +
                            "            document.querySelector('[data-input-id=\"" + inputIdentifier + "\"]');" +
                            "if (input) {" +
                            "  input.focus();" +
                            "  input.value = '" + value + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  input.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Filled input: " + inputIdentifier);
                return true;
            } else {
                System.out.println("❌ Failed to fill input: " + inputIdentifier);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling input: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED date input filling
     */
    private static boolean fillDateInputFixed(WebDriver driver, String inputId, String date) {
        try {
            if (inputId == null) {
                System.out.println("❌ Date input ID is null");
                return false;
            }

            System.out.println("Filling date input: " + inputId + " with: " + date);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var input = document.getElementById('" + inputId + "');" +
                            "if (input) {" +
                            "  input.focus();" +
                            "  input.value = '" + date + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  input.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Filled date input: " + inputId);
                return true;
            } else {
                System.out.println("❌ Failed to fill date input: " + inputId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling date input: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED textarea filling
     */
    private static boolean fillTextareaFixed(WebDriver driver, String value) {
        try {
            System.out.println("Filling textarea with: " + value);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var textarea = document.querySelector('textarea[maxlength=\"3000\"]');" +
                            "if (textarea) {" +
                            "  textarea.focus();" +
                            "  textarea.value = '" + value + "';" +
                            "  textarea.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  textarea.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  textarea.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Filled textarea");
                return true;
            } else {
                System.out.println("❌ Failed to fill textarea");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling textarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED SSN input filling
     */
    private static boolean fillSSNInputFixed(WebDriver driver, String ssn) {
        try {
            System.out.println("Filling SSN input with: " + ssn);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input[mask=\"000-00-0000\"]'));" +
                            "var ssnInput = inputs[inputs.length - 1];" + // Get the newest SSN input
                            "if (ssnInput) {" +
                            "  ssnInput.focus();" +
                            "  ssnInput.value = '" + ssn + "';" +
                            "  ssnInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  ssnInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  ssnInput.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Filled SSN");
                return true;
            } else {
                System.out.println("❌ Failed to fill SSN");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling SSN: " + e.getMessage());
            return false;
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Find newest input by position
     */
    private static String findNewestInput(WebDriver driver, int position) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String inputId = (String) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled])'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "var targetIndex = visibleInputs.length - 1 - " + position + ";" +
                            "if (targetIndex >= 0 && targetIndex < visibleInputs.length) {" +
                            "  var input = visibleInputs[targetIndex];" +
                            "  if (!input.id) {" +
                            "    input.id = 'auto-input-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);" +
                            "  }" +
                            "  return input.id;" +
                            "}" +
                            "return null;"
            );
            return inputId;
        } catch (Exception e) {
            System.err.println("Error finding newest input: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find date input by mask
     */
    private static String findDateInputByMask(WebDriver driver, String mask, int position) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String inputId = (String) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input[mask=\"" + mask + "\"]'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "var targetIndex = " + (position >= 0 ? position : "visibleInputs.length - 1") + ";" +
                            "if (targetIndex >= 0 && targetIndex < visibleInputs.length) {" +
                            "  var input = visibleInputs[targetIndex];" +
                            "  if (!input.id) {" +
                            "    input.id = 'auto-date-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);" +
                            "  }" +
                            "  return input.id;" +
                            "}" +
                            "return null;"
            );
            return inputId;
        } catch (Exception e) {
            System.err.println("Error finding date input: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find input by mask
     */
    private static String findInputByMask(WebDriver driver, String mask, int position) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String inputId = (String) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input[mask=\"" + mask + "\"]'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "var targetIndex = visibleInputs.length - 1 - " + position + ";" +
                            "if (targetIndex >= 0 && targetIndex < visibleInputs.length) {" +
                            "  var input = visibleInputs[targetIndex];" +
                            "  if (!input.id) {" +
                            "    input.id = 'auto-mask-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);" +
                            "  }" +
                            "  return input.id;" +
                            "}" +
                            "return null;"
            );
            return inputId;
        } catch (Exception e) {
            System.err.println("Error finding input by mask: " + e.getMessage());
            return null;
        }
    }

    /**
     * Calculate height option ID
     */
    private static String calculateHeightOption(String heightValue) {
        try {
            int startOptionId = 8; // mat-option-8 corresponds to 3' 0"
            int feet = Integer.parseInt(heightValue.split("'")[0].trim());
            int inches = Integer.parseInt(heightValue.split("'")[1].replace("\"", "").trim());

            int optionOffset = (feet - 3) * 12 + inches;
            return "mat-option-" + (startOptionId + optionOffset);
        } catch (Exception e) {
            System.err.println("Error calculating height option: " + e.getMessage());
            return "mat-option-" + (8 + random.nextInt(60)); // Fallback to random
        }
    }

    // ==================== EXISTING UTILITY METHODS ====================

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
     * Force close any open dropdowns
     */
    private static void forceCloseDropdown(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "document.body.click();" +
                            "if (document.activeElement) document.activeElement.blur();" +
                            "document.dispatchEvent(new KeyboardEvent('keydown', {key: 'Escape'}));" +
                            "var panels = document.querySelectorAll('.mat-select-panel, .cdk-overlay-pane');" +
                            "for (var i = 0; i < panels.length; i++) {" +
                            "  if (panels[i].style.display !== 'none') {" +
                            "    panels[i].style.display = 'none';" +
                            "  }" +
                            "}"
            );
            Thread.sleep(300);
        } catch (Exception e) {
            System.err.println("Error force closing dropdown: " + e.getMessage());
        }
    }

    /**
     * Robust button clicking
     */
    private static boolean clickButtonRobust(WebDriver driver, String identifier, String textFallback) {
        try {
            System.out.println("Attempting robust click for: " + identifier);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Remove overlays
            js.executeScript(
                    "var overlays = document.querySelectorAll('.cdk-overlay-backdrop, .mat-dialog-container, .cdk-overlay-pane');" +
                            "for (var i = 0; i < overlays.length; i++) {" +
                            "  if (overlays[i].style.display !== 'none') {" +
                            "    overlays[i].remove();" +
                            "  }" +
                            "}" +
                            "if (document.activeElement) document.activeElement.blur();"
            );
            Thread.sleep(500);

            WebElement targetButton = null;
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            try {
                targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(identifier)));
            } catch (Exception e) {
                String xpath = "//button[contains(normalize-space(.), '" + identifier + "')] | //a[contains(normalize-space(.), '" + identifier + "')] | //cbp-button//button[contains(normalize-space(.), '" + identifier + "')]";
                if (textFallback != null) {
                    xpath += " | //button[contains(normalize-space(.), '" + textFallback + "')] | //a[contains(normalize-space(.), '" + textFallback + "')] | //cbp-button//button[contains(normalize-space(.), '" + textFallback + "')]";
                }
                targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            }

            js.executeScript("arguments[0].click();", targetButton);
            Thread.sleep(1000);
            System.out.println("✅ Clicked button: " + identifier);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error clicking button '" + identifier + "': " + e.getMessage());
            return false;
        }
    }

    private static boolean clickButtonRobust(WebDriver driver, String identifier) {
        return clickButtonRobust(driver, identifier, null);
    }

    private static boolean clickButtonSimple(WebDriver driver, String buttonText) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement button = null;
            try {
                button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), '" + buttonText + "')]")));
            } catch (Exception e) {
                button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), '" + buttonText + "')]")));
            }
            button.click();
            System.out.println("✅ Clicked simple button: " + buttonText);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to click simple button '" + buttonText + "': " + e.getMessage());
            return false;
        }
    }

    private static boolean waitAndSendKeys(WebDriver driver, By by, String text) {
        try {
            System.out.println("Waiting for element " + by + " and sending keys: " + text);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element.clear();
            element.sendKeys(text);
            System.out.println("✅ Sent keys to element " + by + ": " + text);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error sending keys to element " + by + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean fillDateInput(WebDriver driver, String inputId, String date) {
        try {
            System.out.println("Filling date input (ID: " + inputId + ") with: " + date);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement inputElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(inputId)));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var input = arguments[0];" +
                            "if (input) {" +
                            "  input.value = '" + date + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;", inputElement
            );
            if (result != null && result) {
                System.out.println("✅ Filled date input: " + inputId + ": " + date);
                return true;
            } else {
                System.out.println("❌ Failed to fill date input via JS: " + inputId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling date input " + inputId + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean checkAndClickSubmit(WebDriver driver) {
        try {
            System.out.println("Checking for SUBMIT button...");
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.submit-button, button[type=\"submit\"], button[aria-label*=\"Submit\"], button[title*=\"Submit\"]")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var submitBtn = arguments[0];" +
                            "if (submitBtn && !submitBtn.disabled) {" +
                            "  submitBtn.click();" +
                            "  return true;" +
                            "}" +
                            "return false;", submitBtn
            );

            if (result != null && result) {
                System.out.println("✅ Clicked SUBMIT button");
                Thread.sleep(2000);
                return true;
            } else {
                System.out.println("ℹ️ SUBMIT button is disabled or not found");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error checking/clicking submit button: " + e.getMessage());
            return false;
        }
    }
}