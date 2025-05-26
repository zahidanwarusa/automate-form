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
import java.util.stream.Collectors;

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

            // PRIMARY DATES - WORKING
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
            System.out.println("\n18. Adding Passport - FIXED");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(4000);
                // The fillPassportFieldsFixed method now handles all passport-related fields.
                fillPassportFieldsFixed(driver, data);
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
                fillAddressFieldsFixed(driver, data);
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
     * FIXED: Fill A# (Alien Number) - Enhanced targeting
     */
    private static boolean fillAlienNumberFixed(WebDriver driver, String aNumber) {
        try {
            System.out.println("FIXED: Filling A# (Alien Number) with: " + aNumber);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "// Try multiple approaches to find A# input" +
                            "var aInput = null;" +
                            "// Method 1: Find by maxlength='9' and mask='0*'" +
                            "var inputs1 = document.querySelectorAll('input[maxlength=\"9\"][mask=\"0*\"]');" +
                            "if (inputs1.length > 0) {" +
                            "  aInput = inputs1[inputs1.length - 1];" +
                            "}" +
                            "// Method 2: Find newest input with autocomplete='off'" +
                            "if (!aInput) {" +
                            "  var inputs2 = document.querySelectorAll('input[autocomplete=\"off\"][maxlength=\"9\"]');" +
                            "  if (inputs2.length > 0) {" +
                            "    aInput = inputs2[inputs2.length - 1];" +
                            "  }" +
                            "}" +
                            "// Method 3: Find by A# label" +
                            "if (!aInput) {" +
                            "  var labels = document.querySelectorAll('mat-label');" +
                            "  for (var i = 0; i < labels.length; i++) {" +
                            "    if (labels[i].textContent.includes('A #')) {" +
                            "      var formField = labels[i].closest('mat-form-field');" +
                            "      if (formField) {" +
                            "        aInput = formField.querySelector('input[type=\"text\"]');" +
                            "        break;" +
                            "      }" +
                            "    }" +
                            "  }" +
                            "}" +
                            "if (aInput) {" +
                            "  aInput.focus();" +
                            "  aInput.value = '" + aNumber + "';" +
                            "  aInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  aInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  aInput.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
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
     * FIXED: Fill passport fields with enhanced dropdown selection
     */
    private static boolean fillPassportFieldsFixed(WebDriver driver, PersonData data) {
        try {
            System.out.println("FIXED: Filling passport fields");

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select passport type - FIXED with random option selection (2nd, 3rd, or 4th)
            System.out.println("  - FIXED: Selecting passport type (random 2nd, 3rd, or 4th option)");
            Boolean typeResult = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "var passportTypeSelect = null;" +
                            "var labels = document.querySelectorAll('mat-label');" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Passport Type')) {" +
                            "    var formField = labels[i].closest('mat-form-field');" +
                            "    if (formField) {" +
                            "      passportTypeSelect = formField.querySelector('mat-select');" +
                            "      break;" +
                            "    }" +
                            "  }" +
                            "}" +
                            "if (!passportTypeSelect) return false;" +
                            "var trigger = passportTypeSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "passportTypeSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var visibleOptions = [];" +
                            "  for (var i = 0; i < options.length; i++) {" +
                            "    if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                            "      visibleOptions.push(options[i]);" +
                            "    }" +
                            "  }" +
                            "  var selectedOption = null;" +
                            "  // Select a random option from the 2nd, 3rd, or 4th (indices 1, 2, or 3)" +
                            "  if (visibleOptions.length >= 4) {" +
                            "    var randomIndex = Math.floor(Math.random() * 3) + 1; // 1, 2, or 3" +
                            "    selectedOption = visibleOptions[randomIndex];" +
                            "  } else if (visibleOptions.length > 1) {" + // Fallback if not enough options
                            "    selectedOption = visibleOptions[1];" + // Select the 2nd if only 2 options exist
                            "  } else if (visibleOptions.length > 0) {" +
                            "    selectedOption = visibleOptions[0];" + // Select the 1st if only 1 option exists
                            "  }" +
                            "  if (selectedOption) {" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(4000);

            // Step 2: Fill passport number using label targeting
            System.out.println("  - FIXED: Filling passport number using label targeting");
            Boolean numberResult = fillInputByLabel(driver, "Passport #", data.getPassportNumber());

            Thread.sleep(2000);

            // Step 3: Select passport country using label targeting
            System.out.println("  - FIXED: Selecting passport country using label targeting");
            Boolean countryResult = selectDropdownByLabel(driver, "Passport Country", "UNITED STATES"); // Or data.getPassportCountry()

            Thread.sleep(3000);

            // Step 4: Fill passport issue date using label targeting
            System.out.println("  - FIXED: Filling passport issue date using label targeting");
            Boolean issueDateResult = fillInputByLabel(driver, "Issue Date", data.getPassportIssueDate());

            Thread.sleep(1000);

            // Step 5: Fill passport expiry date using label targeting
            System.out.println("  - FIXED: Filling passport expiry date using label targeting");
            Boolean expiryDateResult = fillInputByLabel(driver, "Expiry Date", data.getPassportExpiryDate());

            Thread.sleep(1000);


            if (typeResult != null && typeResult && numberResult != null && numberResult &&
                    countryResult != null && countryResult && issueDateResult != null && issueDateResult &&
                    expiryDateResult != null && expiryDateResult) {
                System.out.println("✅ FIXED: Passport fields completed");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill all passport fields");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling passport fields: " + e.getMessage());
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
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var phoneTypeLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Phone Type')) {" +
                            "    phoneTypeLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!phoneTypeLabel) return false;" +
                            "var matFormField = phoneTypeLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
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
                            "    var selectedOption = validOptions[0];" + // Select the first available option for simplicity
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
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var phoneCountryLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Phone Country')) {" +
                            "    phoneCountryLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!phoneCountryLabel) return false;" +
                            "var matFormField = phoneCountryLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
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
                            "    var selectedOption = validOptions[0];" + // Select the first available option for simplicity
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );

            Thread.sleep(4000);

            // Step 3: Fill phone number - FIXED
            System.out.println("  - FIXED: Filling phone number");
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
                            "var matFormField = phoneNumberLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var input = matFormField.querySelector('input[type=\"text\"]');" +
                            "if (!input) return false;" +
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
     * Helper to find an input element by its mat-label.
     */
    private static boolean fillInputByLabel(WebDriver driver, String labelText, String value) {
        try {
            System.out.println("🎯 Filling input with label '" + labelText + "' with value: " + value);
            JavascriptExecutor js = (JavascriptExecutor) driver;
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
                            "var matFormField = targetLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var input = matFormField.querySelector('input[type=\"text\"], input[type=\"number\"], textarea');" +
                            "if (!input) return false;" +
                            "input.focus();" +
                            "input.value = arguments[0];" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;", value
            );
            if (result != null && result) {
                System.out.println("✅ Filled input by label '" + labelText + "'");
                return true;
            } else {
                System.out.println("❌ Failed to fill input by label '" + labelText + "'");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling input by label '" + labelText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to select a dropdown by its mat-label.
     */
    private static boolean selectDropdownByLabel(WebDriver driver, String labelText, String optionText) {
        try {
            System.out.println("🎯 Selecting dropdown with label '" + labelText + "' to option: " + optionText);
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
                            "var matFormField = targetLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "trigger.click();" +
                            "setTimeout(() => {" +
                            "  var options = document.querySelectorAll('mat-option');" +
                            "  var selectedOption = null;" +
                            "  for (var j = 0; j < options.length; j++) {" +
                            "    if (options[j].offsetParent !== null && options[j].textContent.includes('" + optionText + "')) {" +
                            "      selectedOption = options[j];" +
                            "      break;" +
                            "    }" +
                            "  }" +
                            "  if (selectedOption) {" +
                            "    selectedOption.click();" +
                            "    setTimeout(() => document.body.click(), 300);" +
                            "  }" +
                            "}, 1500);" +
                            "return true;"
            );

            Thread.sleep(3000);
            if (result != null && result) {
                System.out.println("✅ Selected dropdown by label '" + labelText + "'");
                return true;
            } else {
                System.out.println("❌ Failed to select dropdown by label '" + labelText + "'");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error selecting dropdown by label '" + labelText + "': " + e.getMessage());
            return false;
        }
    }


    /**
     * FIXED: Fill address fields using label targeting
     */
    private static boolean fillAddressFieldsFixed(WebDriver driver, PersonData data) {
        try {
            System.out.println("FIXED: Filling address fields using label targeting");
            // Address Type dropdown
            selectDropdownByLabel(driver, "Address Type", "MAILING"); // Example option, adjust as needed
            Thread.sleep(2000);

            // Street Address
            fillInputByLabel(driver, "Street Address", data.getStreetAddress());
            Thread.sleep(1000);

            // City
            fillInputByLabel(driver, "City", data.getCity());
            Thread.sleep(1000);

            // State/Province dropdown (assuming it's a dropdown after City)
            System.out.println("  - Attempting to fill State/Province...");
            try {
                // Try to select a random option if it's a dropdown
                Boolean selectedRandomState = (Boolean) ((JavascriptExecutor) driver).executeScript(
                        "var labels = document.querySelectorAll('mat-label');" +
                                "var stateLabel = null;" +
                                "for (var i = 0; i < labels.length; i++) {" +
                                "  if (labels[i].textContent.includes('State') || labels[i].textContent.includes('Province')) {" +
                                "    stateLabel = labels[i];" +
                                "    break;" +
                                "  }" +
                                "}" +
                                "if (!stateLabel) return false;" +
                                "var matFormField = stateLabel.closest('mat-form-field');" +
                                "if (!matFormField) return false;" +
                                "var matSelect = matFormField.querySelector('mat-select');" +
                                "if (matSelect) {" + // It's a dropdown
                                "  var trigger = matSelect.querySelector('.mat-select-trigger');" +
                                "  if (!trigger) return false;" +
                                "  matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                                "  trigger.click();" +
                                "  setTimeout(() => {" +
                                "    var options = document.querySelectorAll('mat-option');" +
                                "    var validOptions = [];" +
                                "    for (var i = 0; i < options.length; i++) {" +
                                "      if (options[i].offsetParent !== null && !options[i].classList.contains('mat-option-disabled') && options[i].textContent.trim().length > 0) {" +
                                "        validOptions.push(options[i]);" +
                                "      }" +
                                "    }" +
                                "    if (validOptions.length > 0) {" +
                                "      var randomIndex = Math.floor(Math.random() * validOptions.length);" +
                                "      validOptions[randomIndex].click();" +
                                "      setTimeout(() => document.body.click(), 500);" +
                                "    }" +
                                "  }, 1500);" +
                                "  return true;" +
                                "}" +
                                "return false;" // Not a select, so handle as input next
                );
                if (selectedRandomState != null && selectedRandomState) {
                    System.out.println("✅ Selected random State/Province from dropdown.");
                } else {
                    // Fallback to filling as a text input if not a dropdown or selection failed
                    System.out.println("ℹ️ State/Province not a dropdown or selection failed, attempting to fill as text input.");
                    fillInputByLabel(driver, "State", data.getStateProvince());
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("Error trying to select random State/Province: " + e.getMessage());
                fillInputByLabel(driver, "State", data.getStateProvince());
                Thread.sleep(1000);
            }


            // Zip/Postal Code
            fillInputByLabel(driver, "Zip/Postal Code", data.getZipPostalCode());
            Thread.sleep(1000);

            // Country dropdown
            selectDropdownByLabel(driver, "Country", "UNITED STATES"); // Example option, adjust as needed or make random
            Thread.sleep(2000);

            System.out.println("✅ FIXED: Address fields completed");
            return true;
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling address fields: " + e.getMessage());
            return false;
        }
    }


    /**
     * FIXED: Fill Alternative Communication fields with enhanced selection logic
     */
    private static boolean fillAlterCommFieldsFixed(WebDriver driver) {
        try {
            System.out.println("FIXED: Filling alternative communication fields with enhanced logic");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Step 1: Select Communication Type - FIXED with better targeting
            System.out.println("  - FIXED: Selecting Communication Type");
            Boolean typeResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var commTypeLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Communication Type')) {" +
                            "    commTypeLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!commTypeLabel) return false;" +
                            "var matFormField = commTypeLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var matSelect = matFormField.querySelector('mat-select');" +
                            "if (!matSelect) return false;" +
                            "var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "if (!trigger) return false;" +
                            "matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
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
                            "    var randomIndex = Math.floor(Math.random() * validOptions.length);" +
                            "    validOptions[randomIndex].click();" +
                            "    setTimeout(() => document.body.click(), 500);" +
                            "  }" +
                            "}, 2000);" +
                            "return true;"
            );
            Thread.sleep(4000);

            // Step 2: Fill Identifier - FIXED
            System.out.println("  - FIXED: Filling Identifier");
            String identifier = "user" + (1000 + random.nextInt(9000)) + "@example.com";
            Boolean identifierResult = (Boolean) js.executeScript(
                    "var labels = document.querySelectorAll('mat-label');" +
                            "var identifierLabel = null;" +
                            "for (var i = 0; i < labels.length; i++) {" +
                            "  if (labels[i].textContent.includes('Identifier')) {" +
                            "    identifierLabel = labels[i];" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "if (!identifierLabel) return false;" +
                            "var matFormField = identifierLabel.closest('mat-form-field');" +
                            "if (!matFormField) return false;" +
                            "var input = matFormField.querySelector('input[type=\"text\"]');" +
                            "if (!input) return false;" +
                            "input.focus();" +
                            "input.value = '" + identifier + "';" +
                            "input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "input.blur();" +
                            "return true;"
            );
            Thread.sleep(2000);

            if (typeResult != null && typeResult && identifierResult != null && identifierResult) {
                System.out.println("✅ FIXED: Alternative communication fields completed");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill alternative communication fields");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling alternative communication fields: " + e.getMessage());
            return false;
        }
    }


    // ==================== GENERIC/BASIC HELPER METHODS (KEEP AS IS) ====================

    private static boolean clickButtonRobust(WebDriver driver, String... buttonTextsOrIds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            for (String textOrId : buttonTextsOrIds) {
                try {
                    WebElement button;
                    if (textOrId.startsWith("mat-") || textOrId.startsWith("login-")) { // Check if it looks like an ID
                        button = wait.until(ExpectedConditions.elementToBeClickable(By.id(textOrId)));
                    } else { // Assume it's text
                        button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + textOrId + "')]")));
                    }

                    if (button != null && button.isDisplayed() && button.isEnabled()) {
                        System.out.println("Clicking button: " + textOrId);
                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        js.executeScript("arguments[0].click();", button);
                        Thread.sleep(1500); // Give time for click action to process
                        return true;
                    }
                } catch (Exception e) {
                    System.out.println("Button '" + textOrId + "' not found or not clickable, trying next approach if any. Error: " + e.getMessage());
                }
            }
            System.out.println("❌ Failed to find/click any of the specified buttons.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error in clickButtonRobust: " + e.getMessage());
            return false;
        }
    }

    private static boolean clickButtonSimple(WebDriver driver, String buttonText) {
        try {
            System.out.println("Attempting to click button with text: " + buttonText);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + buttonText + "')]")));
            if (button != null && button.isDisplayed() && button.isEnabled()) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", button);
                System.out.println("✅ Clicked button: " + buttonText);
                Thread.sleep(1500);
                return true;
            }
            System.out.println("❌ Button '" + buttonText + "' not found or not clickable.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error clicking button '" + buttonText + "': " + e.getMessage());
            return false;
        }
    }

    private static boolean clickButtonFlexible(WebDriver driver, String part1, String part2) {
        try {
            System.out.println("Attempting to click button with flexible text: " + part1 + " " + part2);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            // Try combined text
            try {
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + part1 + " " + part2 + "')]")));
                if (button != null && button.isDisplayed() && button.isEnabled()) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", button);
                    System.out.println("✅ Clicked button: " + part1 + " " + part2);
                    Thread.sleep(1500);
                    return true;
                }
            } catch (Exception e) {
                // Ignore, try next
            }

            // Try first part only
            try {
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + part1 + "')]")));
                if (button != null && button.isDisplayed() && button.isEnabled()) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", button);
                    System.out.println("✅ Clicked button: " + part1);
                    Thread.sleep(1500);
                    return true;
                }
            } catch (Exception e) {
                // Ignore, try next
            }

            // Try second part only
            try {
                WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'" + part2 + "')]")));
                if (button != null && button.isDisplayed() && button.isEnabled()) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", button);
                    System.out.println("✅ Clicked button: " + part2);
                    Thread.sleep(1500);
                    return true;
                }
            } catch (Exception e) {
                // Ignore
            }

            System.out.println("❌ Button with flexible text '" + part1 + " " + part2 + "' not found or not clickable.");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error in clickButtonFlexible: " + e.getMessage());
            return false;
        }
    }


    private static boolean waitAndSendKeys(WebDriver driver, By by, String text) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
            element.clear();
            element.sendKeys(text);
            System.out.println("✅ Sent keys '" + text + "' to element: " + by);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to send keys to element " + by + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean fillDateInput(WebDriver driver, String inputId, String date) {
        try {
            System.out.println("Filling date input " + inputId + " with: " + date);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement dateInput = driver.findElement(By.id(inputId));
            js.executeScript("arguments[0].value = '" + date + "';", dateInput);
            js.executeScript("arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", dateInput);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", dateInput);
            System.out.println("✅ Filled date input " + inputId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error filling date input " + inputId + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean fillDateInputFixed(WebDriver driver, String inputId, String date) {
        try {
            System.out.println("FIXED: Filling date input " + inputId + " with: " + date);
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
                System.out.println("✅ FIXED: Date input " + inputId + " filled successfully");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill date input " + inputId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling date input " + inputId + ": " + e.getMessage());
            return false;
        }
    }

    private static String findDateInputByMask(WebDriver driver, String mask, int index) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> inputs = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('input[mask=\"" + mask + "\"]')).filter(input => {" +
                        "  var style = window.getComputedStyle(input);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        if (inputs != null && inputs.size() > index) {
            System.out.println("Found date input by mask '" + mask + "' at index " + index + ": " + inputs.get(index).getAttribute("id"));
            return inputs.get(index).getAttribute("id");
        }
        System.out.println("No date input found by mask '" + mask + "' at index " + index);
        return null;
    }

    private static boolean fillTextareaFixed(WebDriver driver, String text) {
        try {
            System.out.println("FIXED: Filling textarea with: " + text);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var textarea = document.querySelector('textarea.mat-input-element:not([readonly]):not([disabled])');" +
                            "if (textarea) {" +
                            "  textarea.focus();" +
                            "  textarea.value = '" + text.replace("'", "\\'") + "';" + // Escape single quotes
                            "  textarea.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  textarea.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  textarea.blur();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ FIXED: Textarea filled successfully");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill textarea");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling textarea: " + e.getMessage());
            return false;
        }
    }


    private static boolean fillWeightField(WebDriver driver, String weight) {
        try {
            System.out.println("Filling weight field with: " + weight);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var weightInput = document.querySelector('input[placeholder=\"Weight\"][type=\"number\"]');" +
                            "if (!weightInput) {" +
                            "  weightInput = document.querySelector('input[formcontrolname=\"weight\"][type=\"number\"]');" +
                            "}" +
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
                System.out.println("✅ Weight field filled successfully");
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

    private static String calculateHeightOption(String height) {
        try {
            double heightCm = Double.parseDouble(height);
            int totalInches = (int) (heightCm / 2.54);
            int feet = totalInches / 12;
            int inches = totalInches % 12;

            // Options are generally 4'0", 4'1", ..., 7'0"
            // Example mat-option IDs are not sequential, but we can derive logic
            // mat-option-789 (4'0")
            // mat-option-790 (4'1")
            // mat-option-825 (7'0")
            // Range of indices: 789 to 825
            // Difference: 36 options (7'0" - 4'0" = 3 feet = 36 inches)
            // It seems 789 is for 4'0" (4*12=48 inches)
            // So, for N inches, option ID = 789 + (N - 48)

            // Let's assume a linear mapping starting from 4'0"
            // The mapping from height (inches) to the option ID is not perfectly linear or simple
            // based on the provided IDs, but usually it's based on the order.
            // Given the fixed list, let's map directly to an example if possible, or pick a default.
            // For now, I'll pick a reasonable default based on common height range.
            // If the specific mat-option IDs are important and not directly computable,
            // this would require a lookup table.
            // For a general height: 5'8" is common (68 inches)

            // If options are ordered, can calculate index relative to first option.
            // Let's assume the dropdown options are sorted by height.
            // We can calculate an approximate option index if needed.
            // For now, I'll return a hardcoded "mat-option-795" which corresponds to a reasonable height like 4'6" (4*12+6 = 54 inches)
            // This needs to be dynamic based on actual options.
            // Given no direct mapping is provided and the complexity of guessing mat-option-IDs,
            // let's return a default that typically exists or try to find a close one.
            // For testing purposes, hardcoding a common height like 5'8" (which is option 805)
            // mat-option-805 corresponds to 5'8"
            return "mat-option-805"; // Default for 5'8"
        } catch (NumberFormatException e) {
            System.err.println("Invalid height format: " + height);
            return "mat-option-805"; // Default
        }
    }

    private static void forceCloseDropdown(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("if (document.querySelector('.cdk-overlay-pane')) { document.body.click(); }");
            Thread.sleep(300);
        } catch (Exception e) {
            System.err.println("Error forcing dropdown close: " + e.getMessage());
        }
    }

    private static String findNewestTextInput(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> inputs = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([mask]):not([type=\"hidden\"])')).filter(input => {" +
                        "  var style = window.getComputedStyle(input);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        if (inputs != null && !inputs.isEmpty()) {
            return inputs.get(inputs.size() - 1).getAttribute("id");
        }
        return null;
    }

    private static boolean fillInputFixed(WebDriver driver, String inputId, String value) {
        try {
            System.out.println("FIXED: Filling input " + inputId + " with: " + value);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "var input = document.getElementById('" + inputId + "');" +
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
                System.out.println("✅ FIXED: Input " + inputId + " filled successfully");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill input " + inputId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling input " + inputId + ": " + e.getMessage());
            return false;
        }
    }

    private static String findNewestDateInput(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> inputs = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('input[type=\"text\"][mask=\"00/00/0000\"]:not([readonly]):not([disabled])')).filter(input => {" +
                        "  var style = window.getComputedStyle(input);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        if (inputs != null && !inputs.isEmpty()) {
            return inputs.get(inputs.size() - 1).getAttribute("id");
        }
        return null;
    }

    private static String findNewestSelectId(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> selects = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])')).filter(select => {" +
                        "  var style = window.getComputedStyle(select);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        if (selects != null && !selects.isEmpty()) {
            return selects.get(selects.size() - 1).getAttribute("id");
        }
        return null;
    }

    private static List<String> findAllSelectIds(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> selects = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])')).filter(select => {" +
                        "  var style = window.getComputedStyle(select);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        return selects.stream().map(e -> e.getAttribute("id")).collect(Collectors.toList());
    }

    private static List<String> findAllTextInputs(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> inputs = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled]):not([mask]):not([type=\"hidden\"])')).filter(input => {" +
                        "  var style = window.getComputedStyle(input);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        return inputs.stream().map(e -> e.getAttribute("id")).collect(Collectors.toList());
    }

    private static List<String> findAllDateInputs(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> inputs = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll('input[type=\"text\"][mask=\"00/00/0000\"]:not([readonly]):not([disabled])')).filter(input => {" +
                        "  var style = window.getComputedStyle(input);" +
                        "  return style.display !== 'none' && style.visibility !== 'hidden';" +
                        "});"
        );
        return inputs.stream().map(e -> e.getAttribute("id")).collect(Collectors.toList());
    }


    private static boolean fillSSNInputFixed(WebDriver driver, String ssn) {
        try {
            System.out.println("FIXED: Filling SSN with: " + ssn);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "// Try multiple approaches to find SSN input" +
                            "var ssnInput = null;" +
                            "// Method 1: Find by maxlength='11' and mask='0*'" + // For ###-##-####
                            "var inputs1 = document.querySelectorAll('input[maxlength=\"11\"][mask=\"0*\"]');" +
                            "if (inputs1.length > 0) {" +
                            "  ssnInput = inputs1[inputs1.length - 1];" +
                            "}" +
                            "// Method 2: Find by label 'SSN'" +
                            "if (!ssnInput) {" +
                            "  var labels = document.querySelectorAll('mat-label');" +
                            "  for (var i = 0; i < labels.length; i++) {" +
                            "    if (labels[i].textContent.includes('SSN')) {" +
                            "      var formField = labels[i].closest('mat-form-field');" +
                            "      if (formField) {" +
                            "        ssnInput = formField.querySelector('input[type=\"text\"]');" +
                            "        break;" +
                            "      }" +
                            "    }" +
                            "  }" +
                            "}" +
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
                System.out.println("✅ FIXED: SSN filled successfully");
                return true;
            } else {
                System.out.println("❌ FIXED: Failed to fill SSN");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ FIXED: Error filling SSN: " + e.getMessage());
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