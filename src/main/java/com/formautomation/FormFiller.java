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
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofSeconds(5);

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

            // EXCLUSION SITE - WAIT AND RETRY FIXED
            Thread.sleep(3000); // Wait longer for exclusion site to appear
            System.out.println("6. Exclusion Site dropdown (PRS - PARIS)");
            // Try multiple times as this dropdown appears after the exclusion selection
            boolean exclusionSiteSelected = false;
            for (int i = 0; i < 3; i++) {
                if (selectDropdownSimple(driver, "mat-select-25", "mat-option-627")) {
                    exclusionSiteSelected = true;
                    break;
                }
                Thread.sleep(1000);
            }
            if (!exclusionSiteSelected) {
                System.out.println("❌ Failed to select exclusion site after retries");
            }

            // === FORM FIELDS - FIXED ===
            System.out.println("\n=== FILLING TEXT FIELDS ===");

            System.out.println("7. Filling remarks");
            fillTextareaFixed(driver, "Automated test entry - Subject under review - Generated at " + System.currentTimeMillis());

            // === PHYSICAL DESCRIPTIONS - FIXED ===
            System.out.println("\n=== FILLING PHYSICAL DESCRIPTIONS ===");

            System.out.println("8. Hispanic dropdown (Y - YES)");
            selectDropdownFixed(driver, "mat-select-0", "mat-option-2");

            System.out.println("9. Height dropdown");
            String heightOption = calculateHeightOption(data.getHeight());
            System.out.println("   Calculated height option: " + heightOption + " for height: " + data.getHeight());
            selectDropdownFixed(driver, "mat-select-2", heightOption);

            System.out.println("10. Weight field");
            fillWeightField(driver, data.getWeight());

            // === ADD SECTIONS - FIXED WITH DIRECT TARGETING ===
            System.out.println("\n=== ADDING DYNAMIC SECTIONS ===");

            System.out.println("11. Adding Sex");
            if (clickButtonRobust(driver, "Add Sex")) {
                Thread.sleep(4000);
                String sexOption = random.nextBoolean() ? "mat-option-630" : "mat-option-631"; // F or M
                // Direct selection for newest sex dropdown
                selectNewestDropdownDirect(driver, sexOption);
            }

            System.out.println("12. Adding Race");
            if (clickButtonRobust(driver, "Add Race")) {
                Thread.sleep(4000);
                String raceOption = "mat-option-" + (594 + random.nextInt(6));
                selectNewestDropdownDirect(driver, raceOption);
            }

            System.out.println("13. Adding Eye Color");
            if (clickButtonRobust(driver, "Add Eye Color")) {
                Thread.sleep(4000);
                String eyeOption = "mat-option-" + (600 + random.nextInt(12));
                selectNewestDropdownDirect(driver, eyeOption);
            }

            System.out.println("14. Adding Hair Color");
            if (clickButtonRobust(driver, "Add Hair Color")) {
                Thread.sleep(4000);
                String hairOption = "mat-option-" + (612 + random.nextInt(15));
                selectNewestDropdownDirect(driver, hairOption);
            }

            // === NAME SECTION - FIXED WITH DIRECT INPUT TARGETING ===
            System.out.println("\n15. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(3000);
                // Fill name fields directly by finding inputs in name section
                fillNameFieldsDirect(driver, data.getLastName(), data.getFirstName());
            }

            // === DATE OF BIRTH - FIXED ===
            System.out.println("\n16. Adding DOB (Page 2)");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(3000);
                String dobInputId = findNewestDateInput(driver);
                fillDateInputFixed(driver, dobInputId, data.getDob());
            }

            // === CITIZENSHIP - ALREADY WORKING ===
            System.out.println("\n17. Adding Citizenship");
            if (clickButtonRobust(driver, "Add Citizenship")) {
                Thread.sleep(3000);
                selectDropdownSimple(driver, findNewestSelectId(driver), "mat-option-1260"); // USA
            }

            // === PASSPORT - COMPLETELY FIXED ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(4000);

                // Get all passport-related elements
                List<String> passportSelects = findAllSelectIds(driver);
                List<String> passportInputs = findAllTextInputs(driver);
                List<String> passportDateInputs = findAllDateInputs(driver);

                // Passport Type (first new dropdown)
                System.out.println("  - Selecting passport type (P - Regular)");
                if (passportSelects.size() > 0) {
                    selectDropdownSimple(driver, passportSelects.get(passportSelects.size() - 2), "mat-option-1518");
                }

                // Passport Number (first new text input)
                System.out.println("  - Filling passport number");
                if (passportInputs.size() > 0) {
                    fillInputFixed(driver, passportInputs.get(passportInputs.size() - 1), data.getPassportNumber());
                }

                // Passport Country (second new dropdown)
                System.out.println("  - Selecting passport country (USA)");
                if (passportSelects.size() > 1) {
                    selectDropdownSimple(driver, passportSelects.get(passportSelects.size() - 1), "mat-option-1520");
                }

                // Passport Issue Date (first new date input)
                System.out.println("  - Filling passport issue date");
                if (passportDateInputs.size() > 1) {
                    fillDateInputFixed(driver, passportDateInputs.get(passportDateInputs.size() - 2), data.getPassportIssueDate());
                }

                // Passport Expiry Date (second new date input)
                System.out.println("  - Filling passport expiry date");
                if (passportDateInputs.size() > 0) {
                    fillDateInputFixed(driver, passportDateInputs.get(passportDateInputs.size() - 1), data.getPassportExpiryDate());
                }
            }

            // === A NUMBER - ALREADY WORKING ===
            System.out.println("\n19. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(3000);
                String aInputId = findNewestTextInput(driver);
                fillInputFixed(driver, aInputId, data.getaNumber());
            }

            // === DRIVER'S LICENSE - FIXED BUTTON TEXT ===
            System.out.println("\n20. Adding Driver's License");
            if (clickButtonFlexible(driver, "Add Driver", "License")) {
                Thread.sleep(4000);

                // License Number
                System.out.println("  - Filling license number");
                String licenseInputId = findNewestTextInput(driver);
                fillInputFixed(driver, licenseInputId, data.getDriverLicense());

                // License State
                System.out.println("  - Selecting state");
                String stateOption = "mat-option-" + (1774 + random.nextInt(62));
                selectDropdownSimple(driver, findNewestSelectId(driver), stateOption);
            }

            // === SSN - ALREADY WORKING ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(3000);
                fillSSNInputFixed(driver, data.getSsn());
            }

            // === MISC NUMBER - FIXED BUTTON FINDING ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonFlexible(driver, "Add Misc", "Number")) {
                Thread.sleep(4000);

                // Misc Type dropdown
                System.out.println("  - Selecting misc type");
                selectDropdownSimple(driver, findNewestSelectId(driver), "mat-option-" + (1885 + random.nextInt(5)));

                // Misc Number
                System.out.println("  - Filling misc number");
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputFixed(driver, findNewestTextInput(driver), miscNumber);
            }

            // === PHONE NUMBER - FIXED WITH DIRECT TARGETING ===
            System.out.println("\n23. Adding Phone Number");
            if (clickButtonRobust(driver, "Add Phone Number")) {
                Thread.sleep(4000);

                // Direct phone field targeting
                fillPhoneFieldsDirect(driver);
            }

            // === ALTERNATIVE COMMUNICATIONS - FIXED WITH DIRECT TARGETING ===
            System.out.println("\n24. Adding Alternative Communication");
            if (clickButtonFlexible(driver, "Add Alter", "Communication")) {
                Thread.sleep(4000);

                // Direct alternative communication targeting
                fillAlterCommFieldsDirect(driver);
            }

            // === ADDRESS - FIXED WITH DIRECT FIELD TARGETING ===
            System.out.println("\n25. Adding Address");
            if (clickButtonRobust(driver, "Add Address")) {
                Thread.sleep(4000);

                // Direct address field targeting to prevent field confusion
                fillAddressFieldsDirect(driver);
            }

            // === FINANCIAL ACCOUNT - COMPLETELY FIXED ORDER ===
            System.out.println("\n26. Adding Financial Account");
            if (clickButtonRobust(driver, "Add Financial Account")) {
                Thread.sleep(4000);

                List<String> finInputs = findAllTextInputs(driver);
                List<String> finDateInputs = findAllDateInputs(driver);

                // Institution (first new input)
                System.out.println("  - Filling institution");
                if (finInputs.size() >= 6) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 6), "Test Bank");
                }

                // Branch (second new input)
                System.out.println("  - Filling branch");
                if (finInputs.size() >= 5) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 5), "Main Branch");
                }

                // Officer Name (third new input)
                System.out.println("  - Filling officer name");
                if (finInputs.size() >= 4) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 4), "John Doe");
                }

                // Account Number (fourth new input)
                System.out.println("  - Filling account number");
                if (finInputs.size() >= 3) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 3), "ACC" + (100000 + random.nextInt(900000)));
                }

                // Account Type (fifth new input)
                System.out.println("  - Filling account type");
                if (finInputs.size() >= 2) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 2), "Checking");
                }

                // Financial ID (sixth new input)
                System.out.println("  - Filling financial ID");
                if (finInputs.size() >= 1) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 1), "FIN" + (1000 + random.nextInt(9000)));
                }

                // Date (newest date input)
                System.out.println("  - Filling date");
                if (finDateInputs.size() >= 1) {
                    fillDateInputFixed(driver, finDateInputs.get(finDateInputs.size() - 1), generatePastDate(30, 365));
                }
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
     * FIXED: Direct selection for newest dropdown with proper waiting
     */
    private static boolean selectNewestDropdownDirect(WebDriver driver, String optionId) {
        try {
            System.out.println("🎯 Direct selecting newest dropdown → " + optionId);

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
                            "      } else if (++attempts > 30) {" +
                            "        clearInterval(checkOption);" +
                            "        resolve(false);" +
                            "      }" +
                            "    }, 200);" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(3000);

            if (result != null && result) {
                System.out.println("✅ Direct selected newest dropdown → " + optionId);
                return true;
            }

            System.out.println("❌ Failed direct selection for newest dropdown");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error in direct newest dropdown selection: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Direct name fields filling with better targeting
     */
    private static boolean fillNameFieldsDirect(WebDriver driver, String lastName, String firstName) {
        try {
            System.out.println("Filling name fields directly - Last: " + lastName + ", First: " + firstName);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  // Wait for name section to be fully loaded" +
                            "  setTimeout(() => {" +
                            "    // Find all visible text inputs that are not readonly or disabled" +
                            "    var allInputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"])'));" +
                            "    var visibleInputs = allInputs.filter(input => {" +
                            "      var rect = input.getBoundingClientRect();" +
                            "      return rect.width > 0 && rect.height > 0 && input.offsetParent !== null;" +
                            "    });" +
                            "    " +
                            "    // Get the two newest inputs (should be last name and first name)" +
                            "    if (visibleInputs.length >= 2) {" +
                            "      var lastNameInput = visibleInputs[visibleInputs.length - 2];" +
                            "      var firstNameInput = visibleInputs[visibleInputs.length - 1];" +
                            "      " +
                            "      // Fill last name" +
                            "      lastNameInput.focus();" +
                            "      lastNameInput.value = '" + lastName + "';" +
                            "      lastNameInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      lastNameInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      lastNameInput.blur();" +
                            "      " +
                            "      // Small delay between fields" +
                            "      setTimeout(() => {" +
                            "        // Fill first name" +
                            "        firstNameInput.focus();" +
                            "        firstNameInput.value = '" + firstName + "';" +
                            "        firstNameInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "        firstNameInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "        firstNameInput.blur();" +
                            "        resolve(true);" +
                            "      }, 500);" +
                            "    } else {" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);

            if (result != null && result) {
                System.out.println("✅ Filled name fields directly");
                return true;
            } else {
                System.out.println("❌ Failed to fill name fields directly");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling name fields: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Direct phone fields filling with proper sequencing
     */
    private static boolean fillPhoneFieldsDirect(WebDriver driver) {
        try {
            System.out.println("Filling phone fields directly");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select phone type (first dropdown)
            System.out.println("  - Selecting phone type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length < 2) { resolve(false); return; }" +
                            "  " +
                            "  var phoneTypeSelect = selects[selects.length - 2];" +
                            "  phoneTypeSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = phoneTypeSelect.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { phoneTypeSelect.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      // Look for any visible phone type option" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "      " +
                            "      if (visibleOptions.length > 0) {" +
                            "        clearInterval(checkOption);" +
                            "        // Select first available option" +
                            "        visibleOptions[0].click();" +
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

            Thread.sleep(3000);
            if (!(typeResult != null && typeResult)) {
                System.out.println("❌ Failed to select phone type");
                return false;
            }

            // Step 2: Select phone country (second dropdown)
            System.out.println("  - Selecting phone country");
            Boolean countryResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length < 1) { resolve(false); return; }" +
                            "  " +
                            "  var phoneCountrySelect = selects[selects.length - 1];" +
                            "  phoneCountrySelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = phoneCountrySelect.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { phoneCountrySelect.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      // Look for any visible country option" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "      " +
                            "      if (visibleOptions.length > 0) {" +
                            "        clearInterval(checkOption);" +
                            "        // Select first available option" +
                            "        visibleOptions[0].click();" +
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

            Thread.sleep(3000);
            if (!(countryResult != null && countryResult)) {
                System.out.println("❌ Failed to select phone country");
                return false;
            }

            // Step 3: Fill phone number (newest text input)
            System.out.println("  - Filling phone number");
            String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
            Boolean numberResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  setTimeout(() => {" +
                            "    // Get all visible text inputs" +
                            "    var allInputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])'));" +
                            "    var visibleInputs = allInputs.filter(input => {" +
                            "      var rect = input.getBoundingClientRect();" +
                            "      return rect.width > 0 && rect.height > 0 && input.offsetParent !== null;" +
                            "    });" +
                            "    " +
                            "    if (visibleInputs.length > 0) {" +
                            "      var phoneInput = visibleInputs[visibleInputs.length - 1];" +
                            "      phoneInput.focus();" +
                            "      phoneInput.value = '" + phoneNumber + "';" +
                            "      phoneInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      phoneInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      phoneInput.blur();" +
                            "      resolve(true);" +
                            "    } else {" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);

            if (numberResult != null && numberResult) {
                System.out.println("✅ Phone fields filled directly");
                return true;
            } else {
                System.out.println("❌ Failed to fill phone number");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error filling phone fields: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Direct alternative communication fields filling
     */
    private static boolean fillAlterCommFieldsDirect(WebDriver driver) {
        try {
            System.out.println("Filling alternative communication fields directly");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select communication type
            System.out.println("  - Selecting communication type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length === 0) { resolve(false); return; }" +
                            "  " +
                            "  var commTypeSelect = selects[selects.length - 1];" +
                            "  commTypeSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = commTypeSelect.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { commTypeSelect.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      // Look for any visible communication type option" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "      " +
                            "      if (visibleOptions.length > 0) {" +
                            "        clearInterval(checkOption);" +
                            "        // Select first available option" +
                            "        visibleOptions[0].click();" +
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

            Thread.sleep(3000);
            if (!(typeResult != null && typeResult)) {
                System.out.println("❌ Failed to select communication type");
                return false;
            }

            // Step 2: Fill communication value
            System.out.println("  - Filling communication value");
            String email = "test" + System.currentTimeMillis() + "@example.com";
            Boolean valueResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  setTimeout(() => {" +
                            "    // Get all visible text inputs" +
                            "    var allInputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])'));" +
                            "    var visibleInputs = allInputs.filter(input => {" +
                            "      var rect = input.getBoundingClientRect();" +
                            "      return rect.width > 0 && rect.height > 0 && input.offsetParent !== null;" +
                            "    });" +
                            "    " +
                            "    if (visibleInputs.length > 0) {" +
                            "      var commInput = visibleInputs[visibleInputs.length - 1];" +
                            "      commInput.focus();" +
                            "      commInput.value = '" + email + "';" +
                            "      commInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      commInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      commInput.blur();" +
                            "      resolve(true);" +
                            "    } else {" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);

            if (valueResult != null && valueResult) {
                System.out.println("✅ Alternative communication fields filled directly");
                return true;
            } else {
                System.out.println("❌ Failed to fill communication value");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error filling alternative communication fields: " + e.getMessage());
            return false;
        }
    }

    /**
     * COMPLETELY FIXED: Direct address fields filling with proper field ordering
     */
    private static boolean fillAddressFieldsDirect(WebDriver driver) {
        try {
            System.out.println("Filling address fields directly with proper ordering");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select address type first
            System.out.println("  - Step 1: Selecting address type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length === 0) { resolve(false); return; }" +
                            "  " +
                            "  var addressTypeSelect = selects[selects.length - 1];" +
                            "  addressTypeSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = addressTypeSelect.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { addressTypeSelect.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "      " +
                            "      if (visibleOptions.length > 0) {" +
                            "        clearInterval(checkOption);" +
                            "        visibleOptions[0].click();" +
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

            Thread.sleep(3000);
            if (!(typeResult != null && typeResult)) {
                System.out.println("❌ Failed to select address type");
                return false;
            }

            // Step 2: Fill street address (first text input after type selection)
            System.out.println("  - Step 2: Filling street address");
            Boolean streetResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  setTimeout(() => {" +
                            "    var allInputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])'));" +
                            "    var visibleInputs = allInputs.filter(input => {" +
                            "      var rect = input.getBoundingClientRect();" +
                            "      return rect.width > 0 && rect.height > 0 && input.offsetParent !== null;" +
                            "    });" +
                            "    " +
                            "    if (visibleInputs.length > 0) {" +
                            "      var streetInput = visibleInputs[visibleInputs.length - 1];" +
                            "      streetInput.focus();" +
                            "      streetInput.value = '123 Test Street';" +
                            "      streetInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      streetInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      streetInput.blur();" +
                            "      resolve(true);" +
                            "    } else {" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);
            if (!(streetResult != null && streetResult)) {
                System.out.println("❌ Failed to fill street");
                return false;
            }

            // Step 3: Fill city (second text input)
            System.out.println("  - Step 3: Filling city");
            Boolean cityResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  setTimeout(() => {" +
                            "    var allInputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])'));" +
                            "    var visibleInputs = allInputs.filter(input => {" +
                            "      var rect = input.getBoundingClientRect();" +
                            "      return rect.width > 0 && rect.height > 0 && input.offsetParent !== null;" +
                            "    });" +
                            "    " +
                            "    if (visibleInputs.length > 1) {" +
                            "      var cityInput = visibleInputs[visibleInputs.length - 1];" +
                            "      cityInput.focus();" +
                            "      cityInput.value = 'Washington';" +
                            "      cityInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      cityInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      cityInput.blur();" +
                            "      resolve(true);" +
                            "    } else {" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);
            if (!(cityResult != null && cityResult)) {
                System.out.println("❌ Failed to fill city");
                return false;
            }

            // Step 4: Select state (second dropdown after address type)
            System.out.println("  - Step 4: Selecting state");
            Boolean stateResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length < 1) { resolve(false); return; }" +
                            "  " +
                            "  var stateSelect = selects[selects.length - 1];" +
                            "  stateSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    var trigger = stateSelect.querySelector('.mat-select-trigger');" +
                            "    if (trigger) { trigger.click(); } else { stateSelect.click(); }" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "      " +
                            "      if (visibleOptions.length > 0) {" +
                            "        clearInterval(checkOption);" +
                            "        visibleOptions[0].click();" +
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

            Thread.sleep(4000); // Wait longer for country dropdown to appear
            if (!(stateResult != null && stateResult)) {
                System.out.println("❌ Failed to select state");
                return false;
            }

            // Step 5: Select country (appears after state selection)
            System.out.println("  - Step 5: Selecting country");
            Boolean countryResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  setTimeout(() => {" +
                            "    var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "    if (selects.length < 1) { resolve(false); return; }" +
                            "    " +
                            "    var countrySelect = selects[selects.length - 1];" +
                            "    countrySelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "    " +
                            "    setTimeout(() => {" +
                            "      var trigger = countrySelect.querySelector('.mat-select-trigger');" +
                            "      if (trigger) { trigger.click(); } else { countrySelect.click(); }" +
                            "      " +
                            "      var attempts = 0;" +
                            "      var checkOption = setInterval(() => {" +
                            "        var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "        var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "        " +
                            "        if (visibleOptions.length > 0) {" +
                            "          clearInterval(checkOption);" +
                            "          visibleOptions[0].click();" +
                            "          setTimeout(() => {" +
                            "            document.body.click();" +
                            "            resolve(true);" +
                            "          }, 500);" +
                            "        } else if (++attempts > 25) {" +
                            "          clearInterval(checkOption);" +
                            "          resolve(false);" +
                            "        }" +
                            "      }, 200);" +
                            "    }, 1000);" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(3000);
            if (!(countryResult != null && countryResult)) {
                System.out.println("❌ Failed to select country");
                return false;
            }

            // Step 6: Fill postal code (final text input)
            System.out.println("  - Step 6: Filling postal code");
            Boolean postalResult = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  setTimeout(() => {" +
                            "    var allInputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])'));" +
                            "    var visibleInputs = allInputs.filter(input => {" +
                            "      var rect = input.getBoundingClientRect();" +
                            "      return rect.width > 0 && rect.height > 0 && input.offsetParent !== null;" +
                            "    });" +
                            "    " +
                            "    if (visibleInputs.length > 0) {" +
                            "      var postalInput = visibleInputs[visibleInputs.length - 1];" +
                            "      postalInput.focus();" +
                            "      postalInput.value = '20001';" +
                            "      postalInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      postalInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      postalInput.blur();" +
                            "      resolve(true);" +
                            "    } else {" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1000);" +
                            "});"
            );

            Thread.sleep(2000);

            if (postalResult != null && postalResult) {
                System.out.println("✅ Address fields filled directly with proper ordering");
                return true;
            } else {
                System.out.println("❌ Failed to fill postal code");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error filling address fields: " + e.getMessage());
            return false;
        }
    }

    // ==================== OTHER HELPER METHODS (UNCHANGED) ====================

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

    /**
     * Simple dropdown selection method - more reliable than Promise-based approach
     */
    private static boolean selectDropdownSimple(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Simple selection " + selectId + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Close any open dropdowns first
            forceCloseDropdown(driver);
            Thread.sleep(300);

            Boolean result = (Boolean) js.executeScript(
                    "var select = document.getElementById('" + selectId + "');" +
                            "if (!select) return false;" +
                            "select.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "var trigger = select.querySelector('.mat-select-trigger');" +
                            "if (trigger) { trigger.click(); } else { select.click(); }" +
                            "return true;"
            );

            if (result != null && result) {
                Thread.sleep(1500); // Wait for options to appear

                Boolean optionResult = (Boolean) js.executeScript(
                        "var option = document.getElementById('" + optionId + "');" +
                                "if (option && option.offsetParent !== null) {" +
                                "  option.click();" +
                                "  setTimeout(() => document.body.click(), 300);" +
                                "  return true;" +
                                "}" +
                                "return false;"
                );

                if (optionResult != null && optionResult) {
                    System.out.println("✅ Simple selected " + selectId + " → " + optionId);
                    Thread.sleep(500);
                    return true;
                }
            }

            System.out.println("❌ Simple selection failed " + selectId);
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error in simple selection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill weight field specifically
     */
    private static boolean fillWeightField(WebDriver driver, String weight) {
        try {
            System.out.println("Filling weight field with: " + weight);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var weightInput = document.querySelector('input[mask=\"0*\"][maxlength=\"4\"]');" +
                            "if (weightInput) {" +
                            "  weightInput.focus();" +
                            "  weightInput.value = '" + weight + "';" +
                            "  weightInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  weightInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  weightInput.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Filled weight field");
                return true;
            } else {
                System.out.println("❌ Failed to fill weight field");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling weight field: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find newest select element ID
     */
    private static String findNewestSelectId(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String selectId = (String) js.executeScript(
                    "var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "if (selects.length > 0) {" +
                            "  var newest = selects[selects.length - 1];" +
                            "  if (!newest.id) {" +
                            "    newest.id = 'auto-select-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);" +
                            "  }" +
                            "  return newest.id;" +
                            "}" +
                            "return null;"
            );
            return selectId;
        } catch (Exception e) {
            System.err.println("Error finding newest select: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find newest date input
     */
    private static String findNewestDateInput(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String inputId = (String) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input[mask=\"00/00/0000\"]'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "if (visibleInputs.length > 0) {" +
                            "  var newest = visibleInputs[visibleInputs.length - 1];" +
                            "  if (!newest.id) {" +
                            "    newest.id = 'auto-date-newest-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);" +
                            "  }" +
                            "  return newest.id;" +
                            "}" +
                            "return null;"
            );
            return inputId;
        } catch (Exception e) {
            System.err.println("Error finding newest date input: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find all select element IDs
     */
    @SuppressWarnings("unchecked")
    private static List<String> findAllSelectIds(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            List<String> selectIds = (List<String>) js.executeScript(
                    "var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "var ids = [];" +
                            "selects.forEach((select, index) => {" +
                            "  if (!select.id) {" +
                            "    select.id = 'auto-select-all-' + Date.now() + '-' + index;" +
                            "  }" +
                            "  ids.push(select.id);" +
                            "});" +
                            "return ids;"
            );
            return selectIds != null ? selectIds : new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error finding all selects: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Find all text input IDs
     */
    @SuppressWarnings("unchecked")
    private static List<String> findAllTextInputs(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            List<String> inputIds = (List<String>) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([mask])'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "var ids = [];" +
                            "visibleInputs.forEach((input, index) => {" +
                            "  if (!input.id) {" +
                            "    input.id = 'auto-text-all-' + Date.now() + '-' + index;" +
                            "  }" +
                            "  ids.push(input.id);" +
                            "});" +
                            "return ids;"
            );
            return inputIds != null ? inputIds : new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error finding all text inputs: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Find all date input IDs
     */
    @SuppressWarnings("unchecked")
    private static List<String> findAllDateInputs(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            List<String> inputIds = (List<String>) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input[mask=\"00/00/0000\"]'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "var ids = [];" +
                            "visibleInputs.forEach((input, index) => {" +
                            "  if (!input.id) {" +
                            "    input.id = 'auto-date-all-' + Date.now() + '-' + index;" +
                            "  }" +
                            "  ids.push(input.id);" +
                            "});" +
                            "return ids;"
            );
            return inputIds != null ? inputIds : new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error finding all date inputs: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Find newest text input
     */
    private static String findNewestTextInput(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String inputId = (String) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([mask])'));" +
                            "var visibleInputs = inputs.filter(i => i.offsetWidth > 0 && i.offsetHeight > 0);" +
                            "if (visibleInputs.length > 0) {" +
                            "  var newest = visibleInputs[visibleInputs.length - 1];" +
                            "  if (!newest.id) {" +
                            "    newest.id = 'auto-text-newest-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);" +
                            "  }" +
                            "  return newest.id;" +
                            "}" +
                            "return null;"
            );
            return inputId;
        } catch (Exception e) {
            System.err.println("Error finding newest text input: " + e.getMessage());
            return null;
        }
    }

    /**
     * Flexible button clicking for partial text matches
     */
    private static boolean clickButtonFlexible(WebDriver driver, String... textParts) {
        try {
            System.out.println("Attempting flexible click for parts: " + String.join(", ", textParts));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Remove overlays first
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

            // Build flexible xpath for multiple text parts
            StringBuilder xpathBuilder = new StringBuilder();
            for (int i = 0; i < textParts.length; i++) {
                if (i > 0) xpathBuilder.append(" and ");
                xpathBuilder.append("contains(normalize-space(.), '").append(textParts[i]).append("')");
            }

            String xpath = "//button[" + xpathBuilder.toString() + "] | //a[" + xpathBuilder.toString() + "]";

            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

            js.executeScript("arguments[0].click();", targetButton);
            Thread.sleep(1000);
            System.out.println("✅ Clicked flexible button: " + String.join(" ", textParts));
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error clicking flexible button '" + String.join(" ", textParts) + "': " + e.getMessage());
            return false;
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