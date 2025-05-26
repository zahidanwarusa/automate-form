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
 * ENHANCED FormFiller with label-based targeting for better reliability
 */
public class FormFiller {
    private static final Random random = new Random();
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofSeconds(10);

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
     * ENHANCED second page with label-based targeting
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page with label-based targeting...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for page load
            Thread.sleep(10000);

            // === MAIN DROPDOWNS - ENHANCED WITH LABEL TARGETING ===
            System.out.println("\n=== FILLING MAIN DROPDOWNS ===");

            System.out.println("1. Record Status dropdown");
            selectDropdownByLabel(driver, "Record Status", "OB - OUTBOUND SUBJECT");

            System.out.println("2. Query Notification dropdown");
            selectDropdownByLabel(driver, "Query Notification", "0 - NO NOTIFICATION");

            System.out.println("3. Primary Action dropdown");
            selectDropdownByLabel(driver, "Primary Action", "4 - REFER TO PASSPORT CONTROL");

            // PRIMARY DATES - Check if they appear
            System.out.println("3a. Checking for Primary Start Date");
            if (fillDateInputByLabel(driver, "Primary Start Date", generatePastDate(30, 365))) {
                System.out.println("3b. Filling Primary End Date");
                fillDateInputByLabel(driver, "Primary End Date", generateFutureDate(30, 365));
            }

            System.out.println("4. Category dropdown");
            selectDropdownByLabel(driver, "Category", "AB - AG/BIO COUNTERMEASURES");

            System.out.println("5. Exclusions dropdown");
            selectDropdownByLabel(driver, "Exclusions", "ANCX - NIV EXEMPTION FOR CERTAIN ANC MEMBERS");

            // Wait for exclusion site to appear
            Thread.sleep(3000);
            System.out.println("6. Exclusion Site dropdown");
            selectDropdownByLabel(driver, "Exclusion Site", "PRS - PARIS");

            // === FORM FIELDS ===
            System.out.println("\n=== FILLING TEXT FIELDS ===");

            System.out.println("7. Filling remarks");
            fillTextareaByLabel(driver, "Remarks", "Automated test entry - Subject under review - Generated at " + System.currentTimeMillis());

            // === PHYSICAL DESCRIPTIONS ===
            System.out.println("\n=== FILLING PHYSICAL DESCRIPTIONS ===");

            System.out.println("8. Hispanic dropdown");
            selectDropdownByLabel(driver, "Hispanic", "Y - YES");

            System.out.println("9. Height dropdown");
            selectDropdownByLabel(driver, "Height", data.getHeight());

            System.out.println("10. Weight field");
            fillInputByLabel(driver, "Weight", data.getWeight());

            // === ADD SECTIONS WITH ENHANCED TARGETING ===
            System.out.println("\n=== ADDING DYNAMIC SECTIONS ===");

            System.out.println("11. Adding Sex");
            if (clickButtonByText(driver, "Add Sex")) {
                Thread.sleep(4000);
                String sexValue = random.nextBoolean() ? "F - FEMALE" : "M - MALE";
                selectNewestDropdownByFirstOption(driver, sexValue);
            }

            System.out.println("12. Adding Race");
            if (clickButtonByText(driver, "Add Race")) {
                Thread.sleep(4000);
                selectNewestDropdownByFirstOption(driver, "A - ASIAN");
            }

            System.out.println("13. Adding Eye Color");
            if (clickButtonByText(driver, "Add Eye Color")) {
                Thread.sleep(4000);
                selectNewestDropdownByFirstOption(driver, "BG - BLUE/GREEN");
            }

            System.out.println("14. Adding Hair Color");
            if (clickButtonByText(driver, "Add Hair Color")) {
                Thread.sleep(4000);
                selectNewestDropdownByFirstOption(driver, "BA - BALD");
            }

            // === NAME SECTION ===
            System.out.println("\n15. Adding Name");
            if (clickButtonByText(driver, "Add Name")) {
                Thread.sleep(3000);
                fillInputByLabel(driver, "Last Name", data.getLastName());
                fillInputByLabel(driver, "First Name", data.getFirstName());
            }

            // === DATE OF BIRTH ===
            System.out.println("\n16. Adding DOB (Page 2)");
            if (clickButtonByText(driver, "Add DOB")) {
                Thread.sleep(3000);
                fillDateInputByLabel(driver, "Date of Birth", data.getDob());
            }

            // === CITIZENSHIP ===
            System.out.println("\n17. Adding Citizenship");
            if (clickButtonByText(driver, "Add Citizenship")) {
                Thread.sleep(3000);
                selectDropdownByLabel(driver, "Citizenship", "USA - UNITED STATES OF AMERICA");
            }

            // === PASSPORT ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonByText(driver, "Add Passport")) {
                Thread.sleep(4000);

                System.out.println("  - Selecting passport type");
                selectDropdownByLabel(driver, "Passport Type", "P - Regular");

                System.out.println("  - Filling passport number");
                fillInputByLabel(driver, "Passport #", data.getPassportNumber());

                System.out.println("  - Selecting passport country");
                selectDropdownByLabel(driver, "Passport Country", "USA - UNITED STATES OF AMERICA");

                System.out.println("  - Filling passport issue date");
                fillDateInputByLabel(driver, "Passport Issue Date", data.getPassportIssueDate());

                System.out.println("  - Filling passport expiry date");
                fillDateInputByLabel(driver, "Passport Expiration Date", data.getPassportExpiryDate());
            }

            // === A NUMBER ===
            System.out.println("\n19. Adding A#");
            if (clickButtonByText(driver, "Add A#")) {
                Thread.sleep(3000);
                fillInputByLabel(driver, "A #", data.getaNumber());
            }

            // === DRIVER'S LICENSE ===
            System.out.println("\n20. Adding Driver's License");
            if (clickButtonByText(driver, "Add Driver's License")) {
                Thread.sleep(4000);

                System.out.println("  - Filling license number");
                fillInputByLabel(driver, "Driver's License #", data.getDriverLicense());

                System.out.println("  - Selecting state");
                selectDropdownByLabel(driver, "Driver's License State", "UN - UNKNOWN");
            }

            // === SSN ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonByText(driver, "Add SSN")) {
                Thread.sleep(3000);
                fillInputByLabel(driver, "SSN", data.getSsn());
            }

            // === MISC NUMBER ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonByText(driver, "Add Misc. Number")) {
                Thread.sleep(4000);

                System.out.println("  - Selecting misc type");
                selectNewestDropdownByFirstOption(driver, "ANY_OPTION");

                System.out.println("  - Filling misc number");
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputByLabel(driver, "Misc. #", miscNumber);
            }

            // === PHONE NUMBER ===
            System.out.println("\n23. Adding Phone Number");
            if (clickButtonByText(driver, "Add Phone Number")) {
                Thread.sleep(4000);

                System.out.println("  - Selecting phone type");
                selectDropdownByLabel(driver, "Phone Type", "FIRST_AVAILABLE");

                System.out.println("  - Selecting phone country");
                selectDropdownByLabel(driver, "Phone Country", "FIRST_AVAILABLE");

                System.out.println("  - Filling phone number");
                String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
                fillInputByLabel(driver, "Phone #", phoneNumber);
            }

            // === ALTERNATIVE COMMUNICATIONS ===
            System.out.println("\n24. Adding Alternative Communication");
            if (clickButtonByText(driver, "Add Alter. Communication")) {
                Thread.sleep(4000);

                System.out.println("  - Selecting communication type");
                selectDropdownByLabel(driver, "Type", "FIRST_AVAILABLE");

                System.out.println("  - Filling communication value");
                String email = "test" + System.currentTimeMillis() + "@example.com";
                fillInputByLabel(driver, "Alter. Communications", email);
            }

            // === ADDRESS ===
            System.out.println("\n25. Adding Address");
            if (clickButtonByText(driver, "Add Address")) {
                Thread.sleep(4000);

                System.out.println("  - Selecting address type");
                selectDropdownByLabel(driver, "Type", "FIRST_AVAILABLE");

                System.out.println("  - Filling street");
                fillInputByLabel(driver, "Street", "123 Test Street");

                System.out.println("  - Filling city");
                fillInputByLabel(driver, "City", "Washington");

                System.out.println("  - Selecting state");
                selectDropdownByLabel(driver, "State", "FIRST_AVAILABLE");

                System.out.println("  - Selecting country");
                selectDropdownByLabel(driver, "Country", "USA - UNITED STATES OF AMERICA");

                System.out.println("  - Filling postal code");
                fillInputByLabel(driver, "Postal Code", "20001");
            }

            // === FINANCIAL ACCOUNT ===
            System.out.println("\n26. Adding Financial Account");
            if (clickButtonByText(driver, "Add Financial Account")) {
                Thread.sleep(4000);

                System.out.println("  - Filling institution");
                fillInputByLabel(driver, "Institution", "Test Bank");

                System.out.println("  - Filling branch");
                fillInputByLabel(driver, "Branch", "Main Branch");

                System.out.println("  - Filling officer name");
                fillInputByLabel(driver, "Officer Name", "John Doe");

                System.out.println("  - Filling account number");
                fillInputByLabel(driver, "Acct #", "ACC" + (100000 + random.nextInt(900000)));

                System.out.println("  - Filling account type");
                fillInputByLabel(driver, "Acct Type", "Checking");

                System.out.println("  - Filling financial ID");
                fillInputByLabel(driver, "Financial Id", "FIN" + (1000 + random.nextInt(9000)));

                System.out.println("  - Filling date");
                fillDateInputByLabel(driver, "Date", generatePastDate(30, 365));
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

    // ==================== ENHANCED LABEL-BASED TARGETING METHODS ====================

    /**
     * Select dropdown by label text
     */
    private static boolean selectDropdownByLabel(WebDriver driver, String labelText, String optionText) {
        try {
            System.out.println("🎯 Selecting dropdown by label '" + labelText + "' → '" + optionText + "'");

            JavascriptExecutor js = (JavascriptExecutor) driver;
            forceCloseDropdown(driver);
            Thread.sleep(500);

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  // Find mat-label with the specified text" +
                            "  var labels = Array.from(document.querySelectorAll('mat-label'));" +
                            "  var targetLabel = labels.find(label => " +
                            "    label.textContent.trim().includes('" + labelText + "') || " +
                            "    label.textContent.trim().toLowerCase().includes('" + labelText.toLowerCase() + "')" +
                            "  );" +
                            "  " +
                            "  if (!targetLabel) { resolve(false); return; }" +
                            "  " +
                            "  // Navigate to the mat-select element" +
                            "  var matFormField = targetLabel.closest('mat-form-field');" +
                            "  if (!matFormField) { resolve(false); return; }" +
                            "  " +
                            "  var matSelect = matFormField.querySelector('mat-select');" +
                            "  if (!matSelect) { resolve(false); return; }" +
                            "  " +
                            "  var trigger = matSelect.querySelector('.mat-select-trigger');" +
                            "  if (!trigger) { resolve(false); return; }" +
                            "  " +
                            "  // Scroll into view and click" +
                            "  matSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    trigger.click();" +
                            "    " +
                            "    // Wait for options to appear and select" +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null);" +
                            "      " +
                            "      var targetOption = null;" +
                            "      if ('" + optionText + "' === 'FIRST_AVAILABLE') {" +
                            "        targetOption = visibleOptions.find(opt => !opt.classList.contains('mat-option-disabled'));" +
                            "      } else {" +
                            "        targetOption = visibleOptions.find(opt => " +
                            "          opt.textContent.trim().includes('" + optionText + "') || " +
                            "          opt.querySelector('.mat-option-text')?.textContent.trim().includes('" + optionText + "')" +
                            "        );" +
                            "      }" +
                            "      " +
                            "      if (targetOption) {" +
                            "        clearInterval(checkOption);" +
                            "        targetOption.click();" +
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

            Thread.sleep(2000);

            if (result != null && result) {
                System.out.println("✅ Selected dropdown by label '" + labelText + "' → '" + optionText + "'");
                return true;
            }

            System.out.println("❌ Failed to select dropdown by label '" + labelText + "'");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting dropdown by label: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill input field by label text
     */
    private static boolean fillInputByLabel(WebDriver driver, String labelText, String value) {
        try {
            System.out.println("📝 Filling input by label '" + labelText + "' with: " + value);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  // Find mat-label with the specified text" +
                            "  var labels = Array.from(document.querySelectorAll('mat-label'));" +
                            "  var targetLabel = labels.find(label => " +
                            "    label.textContent.trim().includes('" + labelText + "') || " +
                            "    label.textContent.trim().toLowerCase().includes('" + labelText.toLowerCase() + "')" +
                            "  );" +
                            "  " +
                            "  if (!targetLabel) {" +
                            "    // Fallback: try to find by placeholder or aria-label" +
                            "    var inputs = Array.from(document.querySelectorAll('input'));" +
                            "    var input = inputs.find(inp => " +
                            "      inp.placeholder?.includes('" + labelText + "') || " +
                            "      inp.getAttribute('aria-label')?.includes('" + labelText + "')" +
                            "    );" +
                            "    if (input) {" +
                            "      input.focus();" +
                            "      input.value = '" + value + "';" +
                            "      input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      input.blur();" +
                            "      resolve(true);" +
                            "      return;" +
                            "    }" +
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Navigate to the input element" +
                            "  var matFormField = targetLabel.closest('mat-form-field');" +
                            "  if (!matFormField) { resolve(false); return; }" +
                            "  " +
                            "  var input = matFormField.querySelector('input:not([type=\"hidden\"]):not([readonly]):not([disabled])');" +
                            "  if (!input) { resolve(false); return; }" +
                            "  " +
                            "  // Fill the input" +
                            "  input.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    input.focus();" +
                            "    input.value = '" + value + "';" +
                            "    input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "    input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "    input.blur();" +
                            "    resolve(true);" +
                            "  }, 500);" +
                            "});"
            );

            Thread.sleep(1000);

            if (result != null && result) {
                System.out.println("✅ Filled input by label '" + labelText + "'");
                return true;
            }

            System.out.println("❌ Failed to fill input by label '" + labelText + "'");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error filling input by label: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill date input by label text
     */
    private static boolean fillDateInputByLabel(WebDriver driver, String labelText, String dateValue) {
        try {
            System.out.println("📅 Filling date input by label '" + labelText + "' with: " + dateValue);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  // Find mat-label with the specified text" +
                            "  var labels = Array.from(document.querySelectorAll('mat-label'));" +
                            "  var targetLabel = labels.find(label => " +
                            "    label.textContent.trim().includes('" + labelText + "') || " +
                            "    label.textContent.trim().toLowerCase().includes('" + labelText.toLowerCase() + "')" +
                            "  );" +
                            "  " +
                            "  if (!targetLabel) { resolve(false); return; }" +
                            "  " +
                            "  // Navigate to the date input element" +
                            "  var matFormField = targetLabel.closest('mat-form-field');" +
                            "  if (!matFormField) { resolve(false); return; }" +
                            "  " +
                            "  var dateInput = matFormField.querySelector('input[mask=\"00/00/0000\"]');" +
                            "  if (!dateInput) { resolve(false); return; }" +
                            "  " +
                            "  // Fill the date input" +
                            "  dateInput.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    dateInput.focus();" +
                            "    dateInput.value = '" + dateValue + "';" +
                            "    dateInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "    dateInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "    dateInput.blur();" +
                            "    resolve(true);" +
                            "  }, 500);" +
                            "});"
            );

            Thread.sleep(1000);

            if (result != null && result) {
                System.out.println("✅ Filled date input by label '" + labelText + "'");
                return true;
            }

            System.out.println("❌ Failed to fill date input by label '" + labelText + "'");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error filling date input by label: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill textarea by label text
     */
    private static boolean fillTextareaByLabel(WebDriver driver, String labelText, String value) {
        try {
            System.out.println("📝 Filling textarea by label '" + labelText + "' with: " + value);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  // Find mat-label with the specified text" +
                            "  var labels = Array.from(document.querySelectorAll('mat-label'));" +
                            "  var targetLabel = labels.find(label => " +
                            "    label.textContent.trim().includes('" + labelText + "') || " +
                            "    label.textContent.trim().toLowerCase().includes('" + labelText.toLowerCase() + "')" +
                            "  );" +
                            "  " +
                            "  if (!targetLabel) {" +
                            "    // Fallback: find textarea by maxlength attribute" +
                            "    var textarea = document.querySelector('textarea[maxlength=\"3000\"]');" +
                            "    if (textarea) {" +
                            "      textarea.focus();" +
                            "      textarea.value = '" + value + "';" +
                            "      textarea.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "      textarea.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "      textarea.blur();" +
                            "      resolve(true);" +
                            "      return;" +
                            "    }" +
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Navigate to the textarea element" +
                            "  var matFormField = targetLabel.closest('mat-form-field');" +
                            "  if (!matFormField) { resolve(false); return; }" +
                            "  " +
                            "  var textarea = matFormField.querySelector('textarea');" +
                            "  if (!textarea) { resolve(false); return; }" +
                            "  " +
                            "  // Fill the textarea" +
                            "  textarea.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    textarea.focus();" +
                            "    textarea.value = '" + value + "';" +
                            "    textarea.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "    textarea.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "    textarea.blur();" +
                            "    resolve(true);" +
                            "  }, 500);" +
                            "});"
            );

            Thread.sleep(1000);

            if (result != null && result) {
                System.out.println("✅ Filled textarea by label '" + labelText + "'");
                return true;
            }

            System.out.println("❌ Failed to fill textarea by label '" + labelText + "'");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error filling textarea by label: " + e.getMessage());
            return false;
        }
    }

    /**
     * Select newest dropdown by first available option
     */
    private static boolean selectNewestDropdownByFirstOption(WebDriver driver, String preferredOption) {
        try {
            System.out.println("🎯 Selecting newest dropdown with preferred option: " + preferredOption);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            forceCloseDropdown(driver);
            Thread.sleep(500);

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));" +
                            "  if (selects.length === 0) { resolve(false); return; }" +
                            "  " +
                            "  var newest = selects[selects.length - 1];" +
                            "  var trigger = newest.querySelector('.mat-select-trigger');" +
                            "  if (!trigger) { resolve(false); return; }" +
                            "  " +
                            "  newest.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  setTimeout(() => {" +
                            "    trigger.click();" +
                            "    " +
                            "    var attempts = 0;" +
                            "    var checkOption = setInterval(() => {" +
                            "      var options = Array.from(document.querySelectorAll('mat-option'));" +
                            "      var visibleOptions = options.filter(opt => opt.offsetParent !== null && !opt.classList.contains('mat-option-disabled'));" +
                            "      " +
                            "      if (visibleOptions.length > 0) {" +
                            "        clearInterval(checkOption);" +
                            "        " +
                            "        // Try to find preferred option first" +
                            "        var targetOption = visibleOptions.find(opt => " +
                            "          opt.textContent.trim().includes('" + preferredOption + "')" +
                            "        );" +
                            "        " +
                            "        // If not found, use first available" +
                            "        if (!targetOption) {" +
                            "          targetOption = visibleOptions[0];" +
                            "        }" +
                            "        " +
                            "        targetOption.click();" +
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

            Thread.sleep(2000);

            if (result != null && result) {
                System.out.println("✅ Selected newest dropdown");
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
     * Click button by text content
     */
    private static boolean clickButtonByText(WebDriver driver, String buttonText) {
        try {
            System.out.println("🔘 Clicking button with text: " + buttonText);

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

            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            // Try multiple button selection strategies
            String[] xpaths = {
                    "//button[contains(normalize-space(.), '" + buttonText + "')]",
                    "//a[contains(normalize-space(.), '" + buttonText + "')]",
                    "//mat-button[contains(normalize-space(.), '" + buttonText + "')]",
                    "//cbp-button//button[contains(normalize-space(.), '" + buttonText + "')]",
                    "//*[@role='button'][contains(normalize-space(.), '" + buttonText + "')]"
            };

            WebElement targetButton = null;
            for (String xpath : xpaths) {
                try {
                    targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                    break;
                } catch (Exception ignored) {
                    // Try next xpath
                }
            }

            if (targetButton == null) {
                System.out.println("❌ Button not found: " + buttonText);
                return false;
            }

            js.executeScript("arguments[0].click();", targetButton);
            Thread.sleep(1000);
            System.out.println("✅ Clicked button: " + buttonText);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error clicking button '" + buttonText + "': " + e.getMessage());
            return false;
        }
    }

    // ==================== UTILITY METHODS ====================

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

    // ==================== EXISTING METHODS FOR COMPATIBILITY ====================

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