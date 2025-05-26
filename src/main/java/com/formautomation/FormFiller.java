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
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * FIXED FormFiller - Back to working version with only specific fixes
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
     * WORKING second page - back to mostly working version with specific fixes
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for page load
            Thread.sleep(10000);

            // === MAIN DROPDOWNS - WORKING ===
            System.out.println("\n=== FILLING MAIN DROPDOWNS ===");

            System.out.println("1. Record Status dropdown (OB - OUTBOUND SUBJECT)");
            selectDropdownFixed(driver, "mat-select-4", "mat-option-68");

            System.out.println("2. Query Notification dropdown (0 - NO NOTIFICATION)");
            selectDropdownFixed(driver, "mat-select-6", "mat-option-238");

            System.out.println("3. Primary Action dropdown (4 - REFER TO PASSPORT CONTROL)");
            selectDropdownFixed(driver, "mat-select-8", "mat-option-245");

//            // PRIMARY DATES - WORKING
//            System.out.println("3a. Filling Primary Start Date");
//            String primaryStartDate = generatePastDate(30, 365);
//            fillDateInputFixed(driver, findDateInputByMask(driver, "00/00/0000", 0), primaryStartDate);

            System.out.println("3b. Filling Primary End Date");
            String primaryEndDate = generateFutureDate(30, 365);
            fillDateInputFixed(driver, findDateInputByMask(driver, "00/00/0000", 1), primaryEndDate);

            System.out.println("4. Category dropdown (AB - AG/BIO COUNTERMEASURES)");
            selectDropdownFixed(driver, "mat-select-10", "mat-option-549");

            System.out.println("5. Exclusions dropdown (ANCX - NIV EXEMPTION)");
            selectDropdownFixed(driver, "mat-select-12", "mat-option-253");

            // EXCLUSION SITE - WORKING
            Thread.sleep(3000);
            System.out.println("6. Exclusion Site dropdown (PRS - PARIS)");
            selectDropdownSimple(driver, "mat-select-25", "mat-option-627");

            // === FORM FIELDS - WORKING ===
            System.out.println("\n=== FILLING TEXT FIELDS ===");

            System.out.println("7. Filling remarks");
            fillTextareaFixed(driver, "Automated test entry - Subject under review - Generated at " + System.currentTimeMillis());

            // === PHYSICAL DESCRIPTIONS - WORKING ===
            System.out.println("\n=== FILLING PHYSICAL DESCRIPTIONS ===");

            System.out.println("8. Hispanic dropdown (Y - YES)");
            selectDropdownFixed(driver, "mat-select-0", "mat-option-2");

            System.out.println("9. Height dropdown");
            String heightOption = calculateHeightOption(data.getHeight());
            System.out.println("   Calculated height option: " + heightOption + " for height: " + data.getHeight());
            selectDropdownFixed(driver, "mat-select-2", heightOption);

            System.out.println("10. Weight field");
            fillWeightField(driver, data.getWeight());

            // === ADD SECTIONS - FIXED APPROACHES ===
            System.out.println("\n=== ADDING DYNAMIC SECTIONS ===");

            System.out.println("11. Adding Sex - FIXED");
            if (clickButtonRobust(driver, "Add Sex")) {
                Thread.sleep(4000);
                // FIXED: Use label-based approach for sex dropdown
                selectDropdownByLabelFixed(driver, "Sex:", "M - MALE");
            }

            System.out.println("12. Adding Race - FIXED");
            if (clickButtonRobust(driver, "Add Race")) {
                Thread.sleep(4000);
                // FIXED: Use label-based approach for race dropdown
                selectDropdownByLabelFixed(driver, "Race:", "A - ASIAN");
            }

            System.out.println("13. Adding Eye Color - FIXED");
            if (clickButtonRobust(driver, "Add Eye Color")) {
                Thread.sleep(4000);
                // FIXED: Use label-based approach for eye color dropdown
                selectDropdownByLabelFixed(driver, "Eye Color:", "BG - BLUE/GREEN");
            }

            System.out.println("14. Adding Hair Color - FIXED");
            if (clickButtonRobust(driver, "Add Hair Color")) {
                Thread.sleep(4000);
                // FIXED: Use label-based approach for hair color dropdown
                selectDropdownByLabelFixed(driver, "Hair Color:", "BA - BALD");
            }

            // === NAME SECTION - FIXED ===
            System.out.println("\n15. Adding Name - FIXED");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(3000);
                // FIXED: Fill name fields using label targeting
                fillNameFieldsFixed(driver, data.getLastName(), data.getFirstName());
            }

            // === DATE OF BIRTH - WORKING ===
            System.out.println("\n16. Adding DOB (Page 2)");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(3000);
                String dobInputId = findNewestDateInput(driver);
                fillDateInputFixed(driver, dobInputId, data.getDob());
            }

            // === CITIZENSHIP - WORKING ===
            System.out.println("\n17. Adding Citizenship");
            if (clickButtonRobust(driver, "Add Citizenship")) {
                Thread.sleep(3000);
                selectDropdownSimple(driver, findNewestSelectId(driver), "mat-option-1260"); // USA
            }

            // === PASSPORT - FIXED ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(4000);

                // This logic for finding elements was working, so we keep it.
                List<String> passportSelects = findAllSelectIds(driver);
                List<String> passportInputs = findAllTextInputs(driver);
                List<String> passportDateInputs = findAllDateInputs(driver);

                // FIXED: Select a random passport type instead of a hardcoded one.
                System.out.println("  - Selecting random passport type");
                if (passportSelects.size() > 0) {
                    // We target the second to last select element, as in your original code.
                    selectRandomOptionForDropdown(driver, passportSelects.get(passportSelects.size() - 2));
                }

                // UNCHANGED: Your working logic for filling the passport number.
                System.out.println("  - Filling passport number");
                if (passportInputs.size() > 0) {
                    fillInputFixed(driver, passportInputs.get(passportInputs.size() - 1), data.getPassportNumber());
                }

                // FIXED: Select a random passport country instead of a hardcoded one.
                System.out.println("  - Selecting random passport country");
                if (passportSelects.size() > 1) {
                    // We target the last select element, as in your original code.
                    selectRandomOptionForDropdown(driver, passportSelects.get(passportSelects.size() - 1));
                }

                // UNCHANGED: Your working logic for the dates.
                System.out.println("  - Filling passport issue date");
                if (passportDateInputs.size() > 1) {
                    fillDateInputFixed(driver, passportDateInputs.get(passportDateInputs.size() - 2), data.getPassportIssueDate());
                }

                System.out.println("  - Filling passport expiry date");
                if (passportDateInputs.size() > 0) {
                    fillDateInputFixed(driver, passportDateInputs.get(passportDateInputs.size() - 1), data.getPassportExpiryDate());
                }
            }


            // === A NUMBER - WORKING ===
            System.out.println("\n19. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(3000);
                // Directly call the enhanced fillAlienNumberFixed method
                if (fillAlienNumberFixed(driver, data.getaNumber())) {
                    System.out.println("A# successfully added.");
                } else {
                    System.out.println("Failed to add A#.");
                }
            }

            // === DRIVER'S LICENSE - WORKING ===
            System.out.println("\n20. Adding Driver's License");
            if (clickButtonFlexible(driver, "Add Driver", "License")) {
                Thread.sleep(4000);

                System.out.println("  - Filling license number");
                String licenseInputId = findNewestTextInput(driver);
                fillInputFixed(driver, licenseInputId, data.getDriverLicense());

                System.out.println("  - Selecting state");
                String stateOption = "mat-option-" + (1774 + random.nextInt(62));
                selectDropdownSimple(driver, findNewestSelectId(driver), stateOption);
            }

            // === SSN - WORKING ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(3000);
                fillSSNInputFixed(driver, data.getSsn());
            }

            // === MISC NUMBER - WORKING ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonFlexible(driver, "Add Misc", "Number")) {
                Thread.sleep(4000);

                System.out.println("  - Selecting misc type");
                selectDropdownSimple(driver, findNewestSelectId(driver), "mat-option-" + (1885 + random.nextInt(5)));

                System.out.println("  - Filling misc number");
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputFixed(driver, findNewestTextInput(driver), miscNumber);
            }

            // === PHONE NUMBER - FIXED ===
            System.out.println("\n23. Adding Phone Number - FIXED");
            if (clickButtonRobust(driver, "Add Phone Number")) {
                Thread.sleep(4000);
                fillPhoneFieldsFixed(driver);
            }

            // === ALTERNATIVE COMMUNICATIONS - FIXED ===
            System.out.println("\n24. Adding Alternative Communication - FIXED");
            if (clickButtonFlexible(driver, "Add Alter", "Communication")) {
                Thread.sleep(4000);
                fillAlterCommFieldsFixed(driver);
            }

            // === ADDRESS - FIXED ===
            System.out.println("\n25. Adding Address - FIXED");
            if (clickButtonRobust(driver, "Add Address")) {
                Thread.sleep(4000);
                fillAddressFieldsFixed(driver);
            }

            // === FINANCIAL ACCOUNT - WORKING ===
            System.out.println("\n26. Adding Financial Account");
            if (clickButtonRobust(driver, "Add Financial Account")) {
                Thread.sleep(4000);

                List<String> finInputs = findAllTextInputs(driver);
                List<String> finDateInputs = findAllDateInputs(driver);

                System.out.println("  - Filling institution");
                if (finInputs.size() >= 6) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 6), "Test Bank");
                }

                System.out.println("  - Filling branch");
                if (finInputs.size() >= 5) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 5), "Main Branch");
                }

                System.out.println("  - Filling officer name");
                if (finInputs.size() >= 4) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 4), "John Doe");
                }

                System.out.println("  - Filling account number");
                if (finInputs.size() >= 3) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 3), "ACC" + (100000 + random.nextInt(900000)));
                }

                System.out.println("  - Filling account type");
                if (finInputs.size() >= 2) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 2), "Checking");
                }

                System.out.println("  - Filling financial ID");
                if (finInputs.size() >= 1) {
                    fillInputFixed(driver, finInputs.get(finInputs.size() - 1), "FIN" + (1000 + random.nextInt(9000)));
                }

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
     * WORKING dropdown selection method
     */
    private static boolean selectDropdownFixed(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Selecting " + selectId + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

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
     * FIXED: Simple label-based dropdown selection for the problematic dropdowns
     */
    private static boolean selectDropdownByLabelFixed(WebDriver driver, String labelText, String optionText) {
        try {
            System.out.println("🎯 Selecting by label '" + labelText + "' → '" + optionText + "'");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            forceCloseDropdown(driver);
            Thread.sleep(500);

            Boolean result = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var targetLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('" + labelText + "')) {" +
                            "    targetLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!targetLabel) return false;" +
                            "" +
                            "var matFormField = targetLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "" +
                            "matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  for (var j = 0; j < options.length; j++) {" +
                            "    if (options[j].offsetParent !== null && options[j].textContent.includes('" + optionText + "')) {" +
                            "      options[j].click();" +
                            "      setTimeout(() => document.body.click(), 300);" +
                            "      return;" +
                            "    }" +
                            "  }" +
                            "}, 1500);" +
                            "return true;"
            );

            Thread.sleep(3000);

            if (result != null && result) {
                System.out.println("✅ Selected by label '" + labelText + "' → '" + optionText + "'");
                return true;
            }

            System.out.println("❌ Failed to select by label '" + labelText + "'");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting by label: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Fill name fields using label targeting
     */
    private static boolean fillNameFieldsFixed(WebDriver driver, String lastName, String firstName) {
        try {
            System.out.println("Filling name fields - Last: " + lastName + ", First: " + firstName);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Fill last name
            Boolean lastNameResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var lastNameLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Last Name')) {" +
                            "    lastNameLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!lastNameLabel) return false;" +
                            "" +
                            "var matFormField = lastNameLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var input = matFormField.querySelector('input[type=\"text\"]');" +
                            "if (!input) return false;" +
                            "" +
                            "input.focus();" +
                            "input.value = '" + lastName + "';" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;"
            );

            Thread.sleep(1000);

            // Fill first name
            Boolean firstNameResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var firstNameLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('First Name')) {" +
                            "    firstNameLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!firstNameLabel) return false;" +
                            "" +
                            "var matFormField = firstNameLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var input = matFormField.querySelector('input[type=\"text\"]');" +
                            "if (!input) return false;" +
                            "" +
                            "input.focus();" +
                            "input.value = '" + firstName + "';" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;"
            );

            Thread.sleep(1000);

            if (lastNameResult != null && lastNameResult && firstNameResult != null && firstNameResult) {
                System.out.println("✅ Filled name fields");
                return true;
            } else {
                System.out.println("❌ Failed to fill name fields");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling name fields: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Fill phone fields using label targeting

    private static boolean fillPhoneFieldsFixed(WebDriver driver) {
        try {
            System.out.println("Filling phone fields");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Fill phone type first
            System.out.println("  - Selecting phone type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var phoneTypeLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Phone Type')) {" +
                            "    phoneTypeLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!phoneTypeLabel) return false;" +
                            "" +
                            "var matFormField = phoneTypeLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  for (var j = 0; j < options.length; j++) {" +
                            "    if (options[j].offsetParent !== null && !options[j].classList.contains('mat-option-disabled')) {" +
                            "      options[j].click();" +
                            "      setTimeout(() => document.body.click(), 300);" +
                            "      return;" +
                            "    }" +
                            "  }" +
                            "}, 1500);" +
                            "return true;"
            );

            Thread.sleep(3000);

            // Fill phone country
            System.out.println("  - Selecting phone country");
            Boolean countryResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var phoneCountryLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Phone Country')) {" +
                            "    phoneCountryLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!phoneCountryLabel) return false;" +
                            "" +
                            "var matFormField = phoneCountryLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  for (var j = 0; j < options.length; j++) {" +
                            "    if (options[j].offsetParent !== null && !options[j].classList.contains('mat-option-disabled')) {" +
                            "      options[j].click();" +
                            "      setTimeout(() => document.body.click(), 300);" +
                            "      return;" +
                            "    }" +
                            "  }" +
                            "}, 1500);" +
                            "return true;"
            );

            Thread.sleep(3000);

            // Fill phone number
            System.out.println("  - Filling phone number");
            String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
            Boolean numberResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var phoneNumberLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Phone #')) {" +
                            "    phoneNumberLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!phoneNumberLabel) return false;" +
                            "" +
                            "var matFormField = phoneNumberLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var input = matFormField.querySelector('input[type=\"text\"]');" +
                            "if (!input) return false;" +
                            "" +
                            "input.focus();" +
                            "input.value = '" + phoneNumber + "';" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;"
            );

            Thread.sleep(2000);

            if (numberResult != null && numberResult) {
                System.out.println("✅ Phone fields filled");
                return true;
            } else {
                System.out.println("❌ Failed to fill phone fields");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error filling phone fields: " + e.getMessage());
            return false;
        }
    }
     */

    /**
     * FIXED: Fill A# (Alien Number) - Refined targeting by label and attributes
     */
    private static boolean fillAlienNumberFixed(WebDriver driver, String aNumber) {
        try {
            System.out.println("FIXED: Filling A# (Alien Number) with: " + aNumber);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var aInput = null;" +
                            "var labels = document.querySelectorAll('mat-label');" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  var labelText = labels[i].textContent.trim();" +
                            "  if (labelText.includes('A #')) {" +
                            "    var formField = labels[i].closest('mat-form-field');" +
                            "    if (formField) {" +
                            "      // Find input within this specific mat-form-field that matches attributes" +
                            "      aInput = formField.querySelector('input[type=\"text\"][maxlength=\"9\"]');" +
                            "      if (aInput) {" +
                            "        break; // Found the specific input" +
                            "      }" +
                            "    }" +
                            "  }" +
                            "}" +
                            "" +
                            "if (!aInput) {" +
                            "  console.error('A# input not found using label and attribute criteria.');" +
                            "  return false;" +
                            "}" +
                            "" +
                            "input = aInput;" + // Assign to 'input' variable for consistency
                            "input.focus();" +
                            "input.value = '" + aNumber + "';" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;"
            );

            if (result != null && result) {
                System.out.println("✅ FIXED: A# filled successfully");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill A#");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling A#: " + e.getMessage());
            return false;
        }
    }
    /**
     * FIXED: Fill passport fields using label targeting and random dropdown selection.
     * This method identifies fields by their <mat-label> and selects a random,
     * valid option for dropdowns like 'Type' and 'Issuing Country' instead of relying on fixed IDs.
     */
    private static boolean fillPassportFieldsFixed(WebDriver driver, PersonData data) {
        try {
            System.out.println("FIXED: Filling passport fields using label ID and random selection.");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Helper function to fill a text/date field by its label
            BiFunction<String, String, Boolean> fillInputByLabel = (label, value) -> {
                System.out.println("  - Filling '" + label + "' with value...");
                try {
                    return (Boolean) js.executeScript(
                            "var labels = Array.from(document.querySelectorAll('mat-label'));" +
                                    "var targetLabels = labels.filter(l => l.textContent.trim().includes(arguments[0]) && l.offsetParent !== null);" +
                                    "if (targetLabels.length === 0) return false;" +
                                    "var targetLabel = targetLabels[targetLabels.length - 1];" +
                                    "var formField = targetLabel.closest('mat-form-field');" +
                                    "if (!formField) return false;" +
                                    "var input = formField.querySelector('input');" +
                                    "if (!input) return false;" +
                                    "input.focus(); input.value = arguments[1];" +
                                    "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                                    "input.blur(); return true;", label, value
                    );
                } catch (Exception e) { return false; }
            };

            // Helper function to select a random option from a dropdown by its label
            Function<String, Boolean> selectRandomOptionByLabel = (label) -> {
                System.out.println("  - Selecting random option for '" + label + "'");
                try {
                    forceCloseDropdown(driver);
                    Thread.sleep(500);
                    return (Boolean) js.executeScript(
                            "return new Promise((resolve) => {" +
                                    "  var labels = Array.from(document.querySelectorAll('mat-label'));" +
                                    "  var targetLabels = labels.filter(l => l.textContent.trim().includes(arguments[0]) && l.offsetParent !== null);" +
                                    "  if (targetLabels.length === 0) { resolve(false); return; }" +
                                    "  var targetLabel = targetLabels[targetLabels.length - 1];" +
                                    "  var formField = targetLabel.closest('mat-form-field');" +
                                    "  if (!formField) { resolve(false); return; }" +
                                    "  var select = formField.querySelector('mat-select');" +
                                    "  if (!select) { resolve(false); return; }" +
                                    "  select.click();" +
                                    "  setTimeout(() => {" +
                                    "    var options = Array.from(document.querySelectorAll('mat-option:not(.mat-option-disabled)'));" +
                                    "    var visibleOptions = options.filter(o => o.offsetParent !== null && o.textContent.trim() !== '');" +
                                    "    if (visibleOptions.length > 1) {" +
                                    "      var randomIndex = 1 + Math.floor(Math.random() * (visibleOptions.length - 1));" +
                                    "      visibleOptions[randomIndex].click();" +
                                    "    } else if (visibleOptions.length === 1) { visibleOptions[0].click(); }" +
                                    "    else { resolve(false); return; }" +
                                    "    setTimeout(() => { document.body.click(); resolve(true); }, 500);" +
                                    "  }, 2500);" +
                                    "});", label
                    );
                } catch (Exception e) { return false; }
            };

            // Execute the steps
            if (!selectRandomOptionByLabel.apply("Type")) return false;
            Thread.sleep(2000);

            if (!fillInputByLabel.apply("Passport Number", data.getPassportNumber())) return false;
            Thread.sleep(1000);

            if (!selectRandomOptionByLabel.apply("Issuing Country")) return false;
            Thread.sleep(3000);

            if (!fillInputByLabel.apply("Issue Date", data.getPassportIssueDate())) return false;
            Thread.sleep(1000);

            if (!fillInputByLabel.apply("Expiry Date", data.getPassportExpiryDate())) return false;
            Thread.sleep(1000);

            System.out.println("✅ Passport fields filled successfully using labels and random selection.");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error in new passport filling method: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * FIXED: Fill phone fields with enhanced selection logic
     */
    private static boolean fillPhoneFieldsFixed(WebDriver driver) {
        try {
            System.out.println("FIXED: Filling phone fields with enhanced logic");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select phone type - FIXED with better targeting
            System.out.println("  - FIXED: Selecting phone type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "if (selects.length < 2) return false;" +
                            "var phoneTypeSelect = selects[selects.length - 2];" +
                            "var trigger = phoneTypeSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "phoneTypeSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var validOptions = [];" +
                            "  for (var i = 0; i < options.length; i++) {" +
                            "    if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                            "      validOptions.push(options[i]);" +
                            "    }" +
                            "  }" +
                            "  if (validOptions.length > 0) {" +
                            "    var selectedOption = validOptions[0];" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(4000);

            // Step 2: Select phone country - FIXED with better targeting
            System.out.println("  - FIXED: Selecting phone country");
            Boolean countryResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "if (selects.length === 0) return false;" +
                            "var phoneCountrySelect = selects[selects.length - 1];" +
                            "var trigger = phoneCountrySelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "phoneCountrySelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var validOptions = [];" +
                            "  for (var i = 0; i < options.length; i++) {" +
                            "    if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                            "      validOptions.push(options[i]);" +
                            "    }" +
                            "  }" +
                            "  if (validOptions.length > 0) {" +
                            "    var selectedOption = validOptions[0];" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(4000);

            // Step 3: Fill phone number - FIXED
            System.out.println("  - Filling phone number");
            String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
            Boolean numberResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var phoneNumberLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Phone #')) {" +
                            "    phoneNumberLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!phoneNumberLabel) return false;" +
                            "" +
                            "var matFormField = phoneNumberLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "" +
                            "var input = matFormField.querySelector('input[type=\"text\"]');" +
                            "if (!input) return false;" +
                            "" +
                            "input.focus();" +
                            "input.value = '" + phoneNumber + "';" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;"
            );

            Thread.sleep(2000);

            if (numberResult != null && numberResult) {
                System.out.println("✅ FIXED: Phone fields completed");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill phone number");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling phone fields: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Fill address fields with sequential approach

    private static boolean fillAddressFieldsFixed(WebDriver driver) {
        try {
            System.out.println("FIXED: Filling address fields with proper sequencing");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select address type - FIXED
            System.out.println("  - FIXED Step 1: Selecting address type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "if (selects.length === 0) return false;" +
                            "var addressTypeSelect = selects[selects.length - 1];" +
                            "var trigger = addressTypeSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "addressTypeSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var validOptions = [];" +
                            "  for (var i = 0; i < options.length; i++) {" +
                            "    if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                            "      validOptions.push(options[i]);" +
                            "    }" +
                            "  }" +
                            "  if (validOptions.length > 0) {" +
                            "    var selectedOption = validOptions[0];" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(4000);

            // Step 2: Fill street address - FIXED
            System.out.println("  - FIXED Step 2: Filling street address");
            Boolean streetResult = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])');" +
                            "var visibleInputs = [];" +
                            "for (var i = 0; i < inputs.length; i++) {" +
                            "  var rect = inputs[i].getBoundingClientRect();" +
                            "  if (rect.width > 0 && rect.height > 0 && inputs[i].offsetParent !== null && inputs[i].value === '') {" +
                            "    visibleInputs.push(inputs[i]);" +
                            "  }" +
                            "}" +
                            "if (visibleInputs.length === 0) return false;" +
                            "var streetInput = visibleInputs[visibleInputs.length - 1];" +
                            "streetInput.focus();" +
                            "streetInput.value = '123 Test Street';" +
                            "streetInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "streetInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "streetInput.blur();" +
                            "return true;"
            );

            Thread.sleep(3000);

            // Step 3: Fill city - FIXED
            System.out.println("  - FIXED Step 3: Filling city");
            Boolean cityResult = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])');" +
                            "var visibleInputs = [];" +
                            "for (var i = 0; i < inputs.length; i++) {" +
                            "  var rect = inputs[i].getBoundingClientRect();" +
                            "  if (rect.width > 0 && rect.height > 0 && inputs[i].offsetParent !== null && inputs[i].value === '') {" +
                            "    visibleInputs.push(inputs[i]);" +
                            "  }" +
                            "}" +
                            "if (visibleInputs.length === 0) return false;" +
                            "var cityInput = visibleInputs[visibleInputs.length - 1];" +
                            "cityInput.focus();" +
                            "cityInput.value = 'Washington';" +
                            "cityInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "cityInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "cityInput.blur();" +
                            "return true;"
            );

            Thread.sleep(3000);

            // Step 4: Select state - FIXED
            System.out.println("  - FIXED Step 4: Selecting state");
            Boolean stateResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "if (selects.length === 0) return false;" +
                            "var stateSelect = selects[selects.length - 1];" +
                            "var trigger = stateSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "stateSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var validOptions = [];" +
                            "  for (var i = 0; i < options.length; i++) {" +
                            "    if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                            "      validOptions.push(options[i]);" +
                            "    }" +
                            "  }" +
                            "  if (validOptions.length > 0) {" +
                            "    var selectedOption = validOptions[0];" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(5000); // Wait longer for country dropdown to appear

            // Step 5: Select country - FIXED
            System.out.println("  - FIXED Step 5: Selecting country");
            Boolean countryResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "if (selects.length === 0) return false;" +
                            "var countrySelect = selects[selects.length - 1];" +
                            "var trigger = countrySelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "countrySelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var validOptions = [];" +
                            "  for (var i = 0; i < options.length; i++) {" +
                            "    if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                            "      validOptions.push(options[i]);" +
                            "    }" +
                            "  }" +
                            "  // Try to find USA or select first available" +
                            "  var selectedOption = null;" +
                            "  for (var j = 0; j < validOptions.length; j++) {" +
                            "    var optText = validOptions[j].textContent.trim();" +
                            "    if (optText.includes('USA') || optText.includes('UNITED STATES')) {" +
                            "      selectedOption = validOptions[j];" +
                            "      break;" +
                            "    }" +
                            "  }" +
                            "  if (!selectedOption && validOptions.length > 0) {" +
                            "    selectedOption = validOptions[0];" +
                            "  }" +
                            "  if (selectedOption) {" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(4000);

            // Step 6: Fill postal code - FIXED
            System.out.println("  - FIXED Step 6: Filling postal code");
            Boolean postalResult = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])');" +
                            "var visibleInputs = [];" +
                            "for (var i = 0; i < inputs.length; i++) {" +
                            "  var rect = inputs[i].getBoundingClientRect();" +
                            "  if (rect.width > 0 && rect.height > 0 && inputs[i].offsetParent !== null && inputs[i].value === '') {" +
                            "    visibleInputs.push(inputs[i]);" +
                            "  }" +
                            "}" +
                            "if (visibleInputs.length === 0) return false;" +
                            "var postalInput = visibleInputs[visibleInputs.length - 1];" +
                            "postalInput.focus();" +
                            "postalInput.value = '20001';" +
                            "postalInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "postalInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "postalInput.blur();" +
                            "return true;"
            );

            Thread.sleep(2000);

            System.out.println("✅ FIXED: Address fields completed successfully");
            return true;

        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling address fields: " + e.getMessage());
            return false;
        }
    }

     */
    /**
     * FIXED: Fill alternative communication fields
     */
    private static boolean fillAlterCommFieldsFixed(WebDriver driver) {
        try {
            System.out.println("Filling alternative communication fields");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Select communication type
            System.out.println("  - Selecting communication type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "if (selects.length === 0) return false;" +
                            "var newest = selects[selects.length - 1];" +
                            "var trigger = newest.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  for (var j = 0; j < options.length; j++) {" +
                            "    if (options[j].offsetParent !== null && !options[j].classList.contains('mat-option-disabled')) {" +
                            "      options[j].click();" +
                            "      setTimeout(() => document.body.click(), 300);" +
                            "      return;" +
                            "    }" +
                            "  }" +
                            "}, 1500);" +
                            "return true;"
            );

            Thread.sleep(3000);

            // Fill communication value
            System.out.println("  - Filling communication value");
            String email = "test" + System.currentTimeMillis() + "@example.com";
            Boolean valueResult = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([type=\"hidden\"]):not([mask])');" +
                            "var visibleInputs = [];" +
                            "for (var i = 0; i < inputs.length; i++) {" +
                            "  var rect = inputs[i].getBoundingClientRect();" +
                            "  if (rect.width > 0 && rect.height > 0 && inputs[i].offsetParent !== null) {" +
                            "    visibleInputs.push(inputs[i]);" +
                            "  }" +
                            "}" +
                            "if (visibleInputs.length === 0) return false;" +
                            "var newest = visibleInputs[visibleInputs.length - 1];" +
                            "newest.focus();" +
                            "newest.value = '" + email + "';" +
                            "newest.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "newest.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "newest.blur();" +
                            "return true;"
            );

            Thread.sleep(2000);

            if (valueResult != null && valueResult) {
                System.out.println("✅ Alternative communication fields filled");
                return true;
            } else {
                System.out.println("❌ Failed to fill communication fields");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error filling alternative communication fields: " + e.getMessage());
            return false;
        }
    }

    /**
     * FIXED: Fill address fields using a robust, label-based identification approach.
     * This method locates input fields and dropdowns by their visible <mat-label> text,
     * ensuring data is entered into the correct field regardless of its ID or position on the page.
     */
    private static boolean fillAddressFieldsFixed(WebDriver driver) {
        try {
            System.out.println("FIXED: Filling address fields using label identification.");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Helper function to fill a text field based on its associated label text.
            BiFunction<String, String, Boolean> fillInputByLabel = (label, value) -> {
                System.out.println("  - Locating field by label '" + label + "' and filling with '" + value + "'");
                try {
                    return (Boolean) js.executeScript(
                            "var labels = Array.from(document.querySelectorAll('mat-label'));" +
                                    "var targetLabels = labels.filter(l => l.textContent.trim().includes('" + label + "') && l.offsetParent !== null);" +
                                    "if (targetLabels.length === 0) { console.error('Label not found for: " + label + "'); return false; }" +
                                    "var targetLabel = targetLabels[targetLabels.length - 1];" +
                                    "var formField = targetLabel.closest('mat-form-field');" +
                                    "if (!formField) return false;" +
                                    "var input = formField.querySelector('input');" +
                                    "if (!input) return false;" +
                                    "input.focus(); input.value = arguments[1];" +
                                    "input.dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "input.dispatchEvent(new Event('change', { bubbles: true }));" +
                                    "input.blur(); return true;", label, value
                    );
                } catch (Exception e) { return false; }
            };

            // Helper function to select a random option from a dropdown based on its label.
            Function<String, Boolean> selectRandomOptionByLabel = (label) -> {
                System.out.println("  - Locating dropdown by label '" + label + "' and selecting a random option.");
                try {
                    forceCloseDropdown(driver);
                    Thread.sleep(500);
                    return (Boolean) js.executeScript(
                            "return new Promise((resolve) => {" +
                                    "  var labels = Array.from(document.querySelectorAll('mat-label'));" +
                                    "  var targetLabels = labels.filter(l => l.textContent.trim().includes(arguments[0]) && l.offsetParent !== null);" +
                                    "  if (targetLabels.length === 0) { resolve(false); return; }" +
                                    "  var targetLabel = targetLabels[targetLabels.length - 1];" +
                                    "  var formField = targetLabel.closest('mat-form-field');" +
                                    "  if (!formField) { resolve(false); return; }" +
                                    "  var select = formField.querySelector('mat-select');" +
                                    "  if (!select) { resolve(false); return; }" +
                                    "  select.click();" +
                                    "  setTimeout(() => {" +
                                    "    var options = Array.from(document.querySelectorAll('mat-option:not(.mat-option-disabled)'));" +
                                    "    var visibleOptions = options.filter(o => o.offsetParent !== null && o.textContent.trim() !== '');" +
                                    "    if (visibleOptions.length > 1) {" +
                                    "      var randomIndex = 1 + Math.floor(Math.random() * (visibleOptions.length - 1));" + // Avoid first (often blank) option
                                    "      visibleOptions[randomIndex].click();" +
                                    "    } else if (visibleOptions.length === 1) { visibleOptions[0].click(); }" +
                                    "    else { resolve(false); return; }" +
                                    "    setTimeout(() => { document.body.click(); resolve(true); }, 500);" +
                                    "  }, 2000);" +
                                    "});", label
                    );
                } catch (Exception e) { return false; }
            };

            // Sequentially fill the address form
            if (!selectRandomOptionByLabel.apply("Type:")) return false;
            Thread.sleep(2000);

            if (!fillInputByLabel.apply("Street", "123 Automation Lane")) return false;
            Thread.sleep(1000);

            if (!fillInputByLabel.apply("City", "Selenium City")) return false;
            Thread.sleep(1000);

            if (!selectRandomOptionByLabel.apply("State")) return false;
            Thread.sleep(3000);

            if (!selectRandomOptionByLabel.apply("Country")) return false;
            Thread.sleep(2000);

            if (!fillInputByLabel.apply("Postal", "90210")) return false;
            Thread.sleep(1000);

            System.out.println("✅ Address fields filled successfully using labels.");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error in robust address filling method: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== ALL OTHER WORKING HELPER METHODS (UNCHANGED) ====================

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

    private static boolean fillSSNInputFixed(WebDriver driver, String ssn) {
        try {
            System.out.println("Filling SSN input with: " + ssn);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var inputs = Array.from(document.querySelectorAll('input[mask=\"000-00-0000\"]'));" +
                            "var ssnInput = inputs[inputs.length - 1];" +
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

    private static boolean selectDropdownSimple(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Simple selection " + selectId + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

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
                Thread.sleep(1500);

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
     * Selects a random, visible, and enabled option from a dropdown panel
     * after it has been opened.
     * @param driver The WebDriver instance.
     * @param selectId The ID of the mat-select element to click and open.
     * @return true if an option was successfully selected, false otherwise.
     */
    private static boolean selectRandomOptionForDropdown(WebDriver driver, String selectId) {
        try {
            System.out.println("🎯 Selecting random option for dropdown ID: " + selectId);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            forceCloseDropdown(driver); // Ensure no other dropdowns are open
            Thread.sleep(500);

            // Step 1: Click the dropdown to open its options panel.
            Boolean clicked = (Boolean) js.executeScript(
                    "var select = document.getElementById(arguments[0]);" +
                            "if (!select) { console.error('Dropdown not found:', arguments[0]); return false; }" +
                            "var trigger = select.querySelector('.mat-select-trigger');" +
                            "if (trigger) { trigger.click(); } else { select.click(); }" +
                            "return true;", selectId
            );

            if (clicked == null || !clicked) {
                System.out.println("❌ Failed to click dropdown to open panel: " + selectId);
                return false;
            }

            // Wait for the options panel to animate and render.
            Thread.sleep(2000);

            // Step 2: Find all visible options and click a random one.
            Boolean selected = (Boolean) js.executeScript(
                    "var options = Array.from(document.querySelectorAll('mat-option:not(.mat-option-disabled)'));" +
                            "var visibleOptions = options.filter(o => o.offsetParent !== null && o.textContent.trim() !== '');" +
                            "if (visibleOptions.length > 0) {" +
                            "  var randomIndex = Math.floor(Math.random() * visibleOptions.length);" +
                            "  console.log('Randomly selecting option: ' + visibleOptions[randomIndex].textContent);" +
                            "  visibleOptions[randomIndex].click();" +
                            "  setTimeout(() => document.body.click(), 500);" + // Click away to close the panel
                            "  return true;" +
                            "} else {" +
                            "  console.error('No visible options found for dropdown:', arguments[0]);" +
                            "  return false;" +
                            "}", selectId
            );

            if (selected != null && selected) {
                System.out.println("✅ Randomly selected an option for: " + selectId);
                Thread.sleep(1000);
                return true;
            }

            System.out.println("❌ Could not find and select a random option for dropdown: " + selectId);
            return false;

        } catch (Exception e) {
            System.err.println("❌ EXCEPTION during random dropdown selection: " + e.getMessage());
            return false;
        }
    }

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

    private static boolean clickButtonFlexible(WebDriver driver, String... textParts) {
        try {
            System.out.println("Attempting flexible click for parts: " + String.join(", ", textParts));
            JavascriptExecutor js = (JavascriptExecutor) driver;

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

    private static String calculateHeightOption(String heightValue) {
        try {
            int startOptionId = 8;
            int feet = Integer.parseInt(heightValue.split("'")[0].trim());
            int inches = Integer.parseInt(heightValue.split("'")[1].replace("\"", "").trim());

            int optionOffset = (feet - 3) * 12 + inches;
            return "mat-option-" + (startOptionId + optionOffset);
        } catch (Exception e) {
            System.err.println("Error calculating height option: " + e.getMessage());
            return "mat-option-" + (8 + random.nextInt(60));
        }
    }

    private static String generatePastDate(int minDaysAgo, int maxDaysAgo) {
        java.time.LocalDate date = java.time.LocalDate.now()
                .minusDays(minDaysAgo + random.nextInt(maxDaysAgo - minDaysAgo));
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    private static String generateFutureDate(int minDaysAhead, int maxDaysAhead) {
        java.time.LocalDate date = java.time.LocalDate.now()
                .plusDays(minDaysAhead + random.nextInt(maxDaysAhead - minDaysAhead));
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

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

    private static boolean clickButtonRobust(WebDriver driver, String identifier, String textFallback) {
        try {
            System.out.println("Attempting robust click for: " + identifier);
            JavascriptExecutor js = (JavascriptExecutor) driver;

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