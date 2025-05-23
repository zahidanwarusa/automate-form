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
 * Enhanced FormFiller with robust dropdown handling and complete field coverage
 */
public class FormFiller {
    private static final Random random = new Random();

    /**
     * Fill first page (KEEP EXACT WORKING CODE)
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out first page...");

            // NEW: Click the 'CBP Users Windows Login' button first
            System.out.println("Clicking 'CBP Users Windows Login' button...");
            // Using the ID for robustness, as per the provided HTML snippet
            if (!clickButtonRobust(driver, "login-kerberos-btn", "CBP Users")) {
                System.out.println("❌ Failed to click 'CBP Users Windows Login' button. Exiting first page filling.");
                return false;
            }
            Thread.sleep(3000); // Wait for the page to load after login button click

            // Fill basic fields
            System.out.println("Filling last name: " + data.getLastName());
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            System.out.println("Filling first name: " + data.getFirstName());
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            System.out.println("Filling DOB: " + data.getDob());
            // Ensure DOB input is correctly targeted. If 'dob' is the ID, this should work.
            // If it's a date picker, the fillDateInput method might be better, but for initial page,
            // direct sendKeys is often sufficient for masked inputs.
            waitAndSendKeys(driver, By.id("dob"), data.getDob());

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
            System.out.println("Error filling first page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill second page - COMPLETE implementation with robust dropdown handling
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for page load
            Thread.sleep(10000);

            // === MAIN DROPDOWNS ===
            System.out.println("\n=== FILLING MAIN DROPDOWNS ===");

            System.out.println("1. Record Status dropdown (OB - OUTBOUND SUBJECT)");
            selectDropdownEnhanced(driver, "mat-select-4", "mat-option-68");

            System.out.println("2. Category dropdown (AB - AG/BIO COUNTERMEASURES)");
            selectDropdownEnhanced(driver, "mat-select-10", "mat-option-549");

            System.out.println("3. Query Notification dropdown (0 - NO NOTIFICATION)");
            selectDropdownEnhanced(driver, "mat-select-6", "mat-option-238");

            System.out.println("4. Exclusions dropdown (Multiple - ANCX)");
            selectDropdownEnhanced(driver, "mat-select-12", "mat-option-253");

            System.out.println("5. Primary Action dropdown (4 - REFER TO PASSPORT CONTROL)");
            selectDropdownEnhanced(driver, "mat-select-8", "mat-option-245");

            // Since we selected option 245 (not 242), we need to fill primary dates
            System.out.println("5a. Filling Primary Start Date");
            String primaryStartDate = generatePastDate(30, 365); // 30 days to 1 year ago
            fillDateInput(driver, "mat-input-4", primaryStartDate); // Use fillDateInput for date fields

            System.out.println("5b. Filling Primary End Date");
            String primaryEndDate = generateFutureDate(30, 365); // 30 days to 1 year from now
            fillDateInput(driver, "mat-input-5", primaryEndDate); // Use fillDateInput for date fields

            // If there's an Exclusion Site dropdown (appears after selecting exclusion)
            System.out.println("6. Exclusion Site dropdown (if present)");
            selectDropdownIfPresent(driver, "mat-select-25", "mat-option-627"); // PRS - PARIS

            // === FORM FIELDS ===
            System.out.println("\n=== FILLING TEXT FIELDS ===");

            System.out.println("7. Filling remarks");
            fillTextarea(driver, "mat-input-1",
                    "Automated test entry - Subject under review - Generated at " + System.currentTimeMillis());

            // === PHYSICAL DESCRIPTIONS ===
            System.out.println("\n=== FILLING PHYSICAL DESCRIPTIONS ===");

            System.out.println("8. Hispanic dropdown");
            selectDropdownEnhanced(driver, "mat-select-0", "mat-option-2"); // Y - Yes

            System.out.println("9. Height dropdown");
            String heightOption = data.getHeight(); // e.g., "5' 10\""
            selectHeightDropdown(driver, "mat-select-2", heightOption);

            System.out.println("10. Weight field");
            fillInput(driver, "mat-input-0", data.getWeight());

            // === ADD SECTIONS - Enhanced with proper waiting ===
            System.out.println("\n=== ADDING DYNAMIC SECTIONS ===");

            System.out.println("11. Adding Sex");
            addFieldWithEnhancedDropdown(driver, "Add Sex", "mat-select-24",
                    random.nextBoolean() ? "mat-option-630" : "mat-option-631"); // F or M

            System.out.println("12. Adding Race");
            String raceOption = "mat-option-" + (594 + random.nextInt(6)); // Random race option
            addFieldWithEnhancedDropdown(driver, "Add Race", "mat-select-18", raceOption);

            System.out.println("13. Adding Eye Color");
            String eyeOption = "mat-option-" + (600 + random.nextInt(12)); // Random eye color option
            addFieldWithEnhancedDropdown(driver, "Add Eye Color", "mat-select-20", eyeOption);

            System.out.println("14. Adding Hair Color");
            String hairOption = "mat-option-" + (612 + random.nextInt(15)); // Random hair color option
            addFieldWithEnhancedDropdown(driver, "Add Hair Color", "mat-select-22", hairOption);

            // === NAME SECTION ===
            System.out.println("\n15. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(2000); // Wait for input fields to appear
                fillInput(driver, "mat-input-2", data.getLastName());
                fillInput(driver, "mat-input-3", data.getFirstName());
                // Middle name is optional - mat-input-7
            }

            // === DATE OF BIRTH (DOB) ===
            // Addressing the issue where DOB on page 2 was not being selected
            System.out.println("\n16. Adding DOB (Page 2)");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(2000); // Wait for input field to appear
                // Using fillDateInput for DOB as it handles potential date picker interactions
                fillDateInput(driver, "mat-input-11", data.getDob());
            }

            // === CITIZENSHIP ===
            System.out.println("\n17. Adding Citizenship");
            addFieldWithEnhancedDropdown(driver, "Add Citizenship", "mat-select-34", "mat-option-1260"); // USA

            // === PASSPORT ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(3000); // Wait for new passport fields to appear

                // Passport Type
                System.out.println("  - Selecting passport type");
                selectDropdownByPosition(driver, 0, "mat-option-1518"); // P - Regular

                // Passport Number
                System.out.println("  - Filling passport number");
                fillInputByPosition(driver, 0, data.getPassportNumber());

                // Passport Country
                System.out.println("  - Selecting passport country");
                selectDropdownByPosition(driver, 1, "mat-option-1520"); // USA

                // Passport Issue Date
                System.out.println("  - Filling passport issue date");
                fillDateInputByPosition(driver, 0, data.getPassportIssueDate());

                // Passport Expiry Date
                System.out.println("  - Filling passport expiry date");
                fillDateInputByPosition(driver, 1, data.getPassportExpiryDate());
            }

            // === A NUMBER ===
            System.out.println("\n19. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(2000); // Wait for input field to appear
                // Ensure the correct input is targeted for A#
                fillInput(driver, "mat-input-22", data.getaNumber());
            }

            // === DRIVER'S LICENSE ===
            System.out.println("\n20. Adding Driver's License");
            // Changed button text to "Add Driver's License" for more precise matching
            if (clickButtonRobust(driver, "Add Driver's License")) {
                Thread.sleep(3000); // Wait for new license fields to appear

                // License Number
                System.out.println("  - Filling license number");
                // Ensure the correct input is targeted for Driver's License
                fillInput(driver, "mat-input-23", data.getDriverLicense());

                // License State
                System.out.println("  - Selecting state");
                String stateOption = "mat-option-" + (1774 + random.nextInt(62)); // Random US state
                selectDropdownByPosition(driver, 0, stateOption);
            }

            // === SSN ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(2000); // Wait for input field to appear
                // Ensure the correct input is targeted for SSN
                fillSSNInput(driver, data.getSsn());
            }

            // === ADDITIONAL FIELDS (from page2.txt analysis) ===
            System.out.println("\n=== ADDING ADDITIONAL FIELDS ===");

            // === MISC NUMBER ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonRobust(driver, "Add Misc Number")) { // More specific button text
                Thread.sleep(3000);

                // Misc Type dropdown
                selectDropdownByPosition(driver, 0, "mat-option-" + (1885 + random.nextInt(5)));

                // Misc Number
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputByPosition(driver, 0, miscNumber);
            }

            // === PHONE NUMBER ===
            System.out.println("\n23. Adding Phone Number");
            if (clickButtonRobust(driver, "Add Phone Number")) { // More specific button text
                Thread.sleep(3000);

                // Phone Type
                selectDropdownByPosition(driver, 0, "mat-option-" + (1890 + random.nextInt(4)));

                // Phone Country
                selectDropdownByPosition(driver, 1, "mat-option-1895"); // USA

                // Phone Number
                String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
                fillInputByPosition(driver, 0, phoneNumber);
            }

            // === ALTERNATIVE COMMUNICATIONS ===
            System.out.println("\n24. Adding Alternative Communication");
            if (clickButtonRobust(driver, "Add Alternative Communication")) { // More specific button text
                Thread.sleep(3000);

                // Communication Type
                selectDropdownByPosition(driver, 0, "mat-option-" + (1900 + random.nextInt(3)));

                // Communication Value
                String email = "test" + System.currentTimeMillis() + "@example.com";
                fillInputByPosition(driver, 0, email);
            }

            // === ADDRESS ===
            System.out.println("\n25. Adding Address");
            if (clickButtonRobust(driver, "Add Address")) {
                Thread.sleep(3000);

                // Address Type
                selectDropdownByPosition(driver, 0, "mat-option-" + (1910 + random.nextInt(4)));

                // Street
                fillInputByPosition(driver, 0, "123 Test Street");

                // City
                fillInputByPosition(driver, 1, "Washington");

                // State (if US address)
                selectDropdownByPosition(driver, 1, "mat-option-1915"); // DC

                // Country
                selectDropdownByPosition(driver, 2, "mat-option-1260"); // USA

                // Postal Code
                fillInputByPosition(driver, 2, "20001");
            }

            // === FINANCIAL ACCOUNT ===
            System.out.println("\n26. Adding Financial Account");
            if (clickButtonRobust(driver, "Add Financial Account")) { // More specific button text
                Thread.sleep(3000);

                // Institution
                fillInputByPosition(driver, 0, "Test Bank");

                // Branch
                fillInputByPosition(driver, 1, "Main Branch");

                // Officer Name
                fillInputByPosition(driver, 2, "John Doe");

                // Account Number
                fillInputByPosition(driver, 3, "ACC" + (100000 + random.nextInt(900000)));

                // Account Type
                fillInputByPosition(driver, 4, "Checking");

                // Financial ID
                fillInputByPosition(driver, 5, "FIN" + (1000 + random.nextInt(9000)));

                // Date (using date picker)
                fillDateInputByPosition(driver, 0, generatePastDate(30, 365));
            }

            // Final cleanup
            System.out.println("\n27. Final cleanup - ensuring all dropdowns are closed");
            ensureAllDropdownsClosed(driver);
            Thread.sleep(2000);

            // Try to submit the form (if submit button is enabled)
            System.out.println("\n28. Checking for SUBMIT button");
            checkAndClickSubmit(driver);

            System.out.println("\n✅ Second page completed successfully!");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Enhanced dropdown selection with guaranteed closure and improved waiting.
     * This method attempts to open a mat-select dropdown by its ID, click a specific mat-option,
     * and then aggressively close any open overlays or panels to prevent interception issues.
     *
     * @param driver The WebDriver instance.
     * @param selectId The ID of the mat-select element (e.g., "mat-select-4").
     * @param optionId The ID of the mat-option element to select (e.g., "mat-option-68").
     * @return true if the option was successfully selected and dropdown closed, false otherwise.
     */
    private static boolean selectDropdownEnhanced(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Enhanced selection: " + selectId + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // First ensure any open dropdowns are closed to prevent interference
            ensureAllDropdownsClosed(driver);
            Thread.sleep(500); // Give a moment for closure to take effect

            // Execute the dropdown selection with guaranteed closure using JavaScript Promise
            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  console.log('Starting dropdown selection: " + selectId + "');" +
                            "  var select = document.getElementById('" + selectId + "');" +
                            "  if (!select) {" +
                            "    console.error('Select not found: " + selectId + "');" +
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Scroll into view and click the select element to open the dropdown panel" +
                            "  select.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    select.click();" +
                            "    console.log('Clicked select, waiting for panel to appear...');" +
                            "    " +
                            "    // Wait for the dropdown panel to appear and then attempt to click the option" +
                            "    setTimeout(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option) {" +
                            "        console.log('Found option, attempting to click: " + optionId + "');" +
                            "        option.click();" +
                            "        " +
                            "        // CRITICAL: Force close the dropdown after selection using multiple methods" +
                            "        setTimeout(() => {" +
                            "          // Method 1: Simulate a click on the document body to close the dropdown" +
                            "          document.body.click();" +
                            "          " +
                            "          // Method 2: Dispatch an Escape keydown event to close the dropdown" +
                            "          var escEvent = new KeyboardEvent('keydown', {" +
                            "            key: 'Escape'," +
                            "            keyCode: 27," +
                            "            bubbles: true" +
                            "          });" +
                            "          document.dispatchEvent(escEvent);" +
                            "          " +
                            "          // Method 3: Directly click any visible overlay backdrop" +
                            "          var backdrop = document.querySelector('.cdk-overlay-backdrop');" +
                            "          if (backdrop) backdrop.click();" +
                            "          " +
                            "          // Method 4: Hide all mat-select-panel elements (direct DOM manipulation)" +
                            "          var panels = document.querySelectorAll('.mat-select-panel');" +
                            "          panels.forEach(p => p.style.display = 'none');" +
                            "          " +
                            "          console.log('Dropdown closure attempts executed.');" +
                            "          resolve(true);" +
                            "        }, 500);" + // Small delay after option click for closure methods
                            "      } else {" +
                            "        console.error('Option not found: " + optionId + "');" +
                            "        // If option not found, still try to close any open dropdowns" +
                            "        document.body.click();" +
                            "        resolve(false);" +
                            "      }" +
                            "    }, 1500);" + // Wait for panel to fully render options
                            "  }, 500);" + // Wait for select element click to register
                            "});"
            );

            // Wait extra time in Java to ensure all JavaScript actions complete and dropdown is truly closed
            Thread.sleep(2500);

            if (result != null && result) {
                System.out.println("✅ Successfully selected and closed: " + selectId);
                return true;
            } else {
                System.out.println("❌ Failed to select: " + selectId);
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Error in enhanced selection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Add field with enhanced dropdown handling - specifically for dynamically added fields.
     * This method clicks an "Add" button, waits for a new dropdown to appear, and then
     * attempts to select an option from it using the enhanced dropdown selection logic.
     *
     * @param driver The WebDriver instance.
     * @param buttonText The text of the "Add" button to click (e.g., "Add Sex").
     * @param expectedSelectId The ID of the expected mat-select element that appears (can be null/empty if unknown).
     * @param optionId The ID of the mat-option to select (e.g., "mat-option-630").
     * @return true if the field was added and option selected, false otherwise.
     */
    private static boolean addFieldWithEnhancedDropdown(WebDriver driver, String buttonText,
                                                        String expectedSelectId, String optionId) {
        try {
            System.out.println("🎯 Adding " + buttonText + " with enhanced dropdown");

            // Click the add button using the robust click method
            if (!clickButtonRobust(driver, buttonText)) {
                System.out.println("❌ Failed to click add button for: " + buttonText);
                return false;
            }

            Thread.sleep(3000); // Wait for the new field/dropdown to appear after clicking "Add"

            // Use the expected select ID if provided, otherwise find the newest dropdown by position
            if (expectedSelectId != null && !expectedSelectId.isEmpty()) {
                return selectDropdownEnhanced(driver, expectedSelectId, optionId);
            } else {
                // Fallback to position-based selection if a specific ID isn't known for dynamic dropdowns
                System.out.println("ℹ️ Expected select ID not provided, attempting to select from newest dropdown.");
                return selectDropdownByPosition(driver, 0, optionId); // Position 0 means the newest dropdown
            }

        } catch (Exception e) {
            System.out.println("❌ Error adding field with dropdown: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Select dropdown by position (for dynamically added fields).
     * This method is useful when the ID of a dynamically added dropdown is not static,
     * and you need to target it based on its order of appearance (e.g., the newest one).
     *
     * @param driver The WebDriver instance.
     * @param position The position of the dropdown from the end of the list of all mat-selects (0 for newest, 1 for second newest, etc.).
     * @param optionId The ID of the mat-option element to select.
     * @return true if the option was selected and dropdown closed, false otherwise.
     */
    private static boolean selectDropdownByPosition(WebDriver driver, int position, String optionId) {
        try {
            System.out.println("🎯 Selecting dropdown at position " + position + " (from newest) → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Ensure dropdowns are closed first to avoid interference
            ensureAllDropdownsClosed(driver);
            Thread.sleep(500);

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  // Find all active (not disabled) mat-select elements" +
                            "  var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "  console.log('Found ' + selects.length + ' active dropdowns on the page.');" +
                            "  " +
                            "  // Calculate the index of the target dropdown from the end of the list" +
                            "  var targetIndex = selects.length - 1 - " + position + ";" +
                            "  var targetSelect = selects[targetIndex];" +
                            "  " +
                            "  if (!targetSelect) {" +
                            "    console.error('No dropdown found at calculated position " + position + " (index ' + targetIndex + ').');" + // Corrected concatenation for targetIndex
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Scroll the target dropdown into view and click it to open the panel" +
                            "  targetSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    targetSelect.click();" +
                            "    console.log('Clicked dropdown at position " + position + ", waiting for options...');" +
                            "    " +
                            "    // Wait for options to appear and then click the desired option" +
                            "    setTimeout(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option) {" +
                            "        console.log('Found option, clicking: " + optionId + "');" +
                            "        option.click();" +
                            "        " +
                            "        // Force close the dropdown after selection using multiple methods" +
                            "        setTimeout(() => {" +
                            "          document.body.click();" +
                            "          var escEvent = new KeyboardEvent('keydown', {key: 'Escape', keyCode: 27, bubbles: true});" +
                            "          document.dispatchEvent(escEvent);" +
                            "          var backdrop = document.querySelector('.cdk-overlay-backdrop');" +
                            "          if (backdrop) backdrop.click();" +
                            "          var panels = document.querySelectorAll('.mat-select-panel');" +
                            "          panels.forEach(p => p.style.display = 'none');" +
                            "          console.log('Dropdown at position " + position + " closed.');" +
                            "          resolve(true);" +
                            "        }, 500);" +
                            "      } else {" +
                            "        console.error('Option not found: " + optionId + " for dropdown at position " + position + "');" +
                            "        document.body.click();" + // Still try to close if option not found
                            "        resolve(false);" +
                            "      }" +
                            "    }, 1500);" + // Wait for panel and options to load
                            "  }, 500);" + // Wait for click to register
                            "});"
            );

            // Wait extra time in Java for all JS actions to complete
            Thread.sleep(2500);
            return result != null && result;

        } catch (Exception e) {
            System.out.println("❌ Error selecting by position " + position + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ensures all open Angular Material dropdowns and overlays are aggressively closed.
     * This is crucial to prevent invisible overlays from blocking subsequent interactions.
     * It attempts multiple methods: clicking the body, blurring active elements,
     * dispatching Escape key, clicking backdrops, and directly hiding panels/removing overlay containers.
     *
     * @param driver The WebDriver instance.
     */
    private static void ensureAllDropdownsClosed(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            System.out.println("Attempting to ensure all dropdowns and overlays are closed...");
            js.executeScript(
                    "// Method 1: Simulate a click on the document body" +
                            "document.body.click();" +
                            "// Method 2: Blur the currently active element (if any)" +
                            "if (document.activeElement && document.activeElement !== document.body) {" +
                            "  document.activeElement.blur();" +
                            "}" +
                            "// Method 3: Dispatch an Escape keydown event" +
                            "var escEvent = new KeyboardEvent('keydown', {" +
                            "  key: 'Escape'," +
                            "  keyCode: 27," +
                            "  bubbles: true" +
                            "});" +
                            "document.dispatchEvent(escEvent);" +
                            "" +
                            "// Method 4: Click on and remove any cdk-overlay-backdrop elements (the invisible overlay)" +
                            "var backdrops = document.querySelectorAll('.cdk-overlay-backdrop');" +
                            "backdrops.forEach(b => {" +
                            "  if (b.style.display !== 'none') {" + // Only click if visible
                            "    b.click();" +
                            "    b.remove(); // Remove it from DOM after clicking" +
                            "  }" +
                            "});" +
                            "" +
                            "// Method 5: Directly hide all mat-select-panel elements" +
                            "var panels = document.querySelectorAll('.mat-select-panel');" +
                            "panels.forEach(p => {" +
                            "  if (p.style.display !== 'none') {" +
                            "    p.style.display = 'none';" +
                            "  }" +
                            "});" +
                            "" +
                            "// Method 6: Clean up cdk-overlay-container if they contain dropdown panels" +
                            "var overlays = document.querySelectorAll('.cdk-overlay-container');" +
                            "overlays.forEach(o => {" +
                            "  // Check if the overlay container specifically holds a mat-select-panel" +
                            "  if (o.innerHTML.includes('mat-select-panel')) {" +
                            "    o.innerHTML = ''; // Clear its content" +
                            "  }" +
                            "});" +
                            "console.log('Aggressive dropdown closure script executed.');"
            );
            Thread.sleep(500); // Give some time for the browser to process the JS
        } catch (Exception e) {
            System.out.println("Error ensuring dropdowns closed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Select height dropdown with proper format matching.
     * This method parses the height string (e.g., "5' 10\"") and calculates
     * the corresponding mat-option ID based on a known pattern.
     *
     * @param driver The WebDriver instance.
     * @param selectId The ID of the height mat-select element.
     * @param heightValue The height string (e.g., "5' 10\"").
     * @return true if the height was selected, false otherwise.
     */
    private static boolean selectHeightDropdown(WebDriver driver, String selectId, String heightValue) {
        try {
            // Height options start from mat-option-8 (3' 0") and go up
            // We need to find the right option based on the height value
            int startOptionId = 8; // Corresponds to 3' 0"
            int feet = Integer.parseInt(heightValue.split("'")[0].trim());
            int inches = Integer.parseInt(heightValue.split("'")[1].replace("\"", "").trim());

            // Calculate option offset: (feet - starting_feet) * 12 inches + current_inches
            int optionOffset = (feet - 3) * 12 + inches;
            String optionId = "mat-option-" + (startOptionId + optionOffset);

            System.out.println("Calculated height option ID: " + optionId + " for height: " + heightValue);
            return selectDropdownEnhanced(driver, selectId, optionId);
        } catch (Exception e) {
            System.out.println("❌ Error calculating or selecting height. Falling back to random height. " + e.getMessage());
            // Fallback to a random height if parsing fails or option is not found
            int randomOption = 8 + random.nextInt(60); // Assuming 60 options cover a reasonable range
            return selectDropdownEnhanced(driver, selectId, "mat-option-" + randomOption);
        }
    }

    /**
     * Fill SSN input field, handling potential input masks or special behaviors.
     * This uses JavaScript to directly set the value and dispatch input/change events.
     *
     * @param driver The WebDriver instance.
     * @param ssn The SSN string to enter.
     * @return true if SSN was filled, false otherwise.
     */
    private static boolean fillSSNInput(WebDriver driver, String ssn) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Find inputs with the specific SSN mask and target the newest one
            Boolean result = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input[mask=\"000-00-0000\"]');" +
                            "if (inputs.length > 0) {" +
                            "  var ssnInput = inputs[inputs.length - 1];" + // Get the newest one
                            "  ssnInput.value = '" + ssn + "';" +
                            "  ssnInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  ssnInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ SSN filled: " + ssn);
                return true;
            } else {
                System.out.println("❌ SSN input element not found or script failed.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error filling SSN: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill date input field, specifically designed for date picker inputs.
     * It uses JavaScript to directly set the value and trigger events, which is often
     * more reliable for date inputs that might have complex UI components.
     *
     * @param driver The WebDriver instance.
     * @param inputId The ID of the date input field.
     * @param date The date string in "MM/dd/yyyy" format.
     * @return true if the date was filled, false otherwise.
     */
    private static boolean fillDateInput(WebDriver driver, String inputId, String date) {
        try {
            System.out.println("Filling date input (ID: " + inputId + ") with: " + date);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var input = document.getElementById('" + inputId + "');" +
                            "if (input) {" +
                            "  input.value = '" + date + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );
            if (result != null && result) {
                System.out.println("✅ Filled date input: " + inputId + ": " + date);
                return true;
            } else {
                System.out.println("❌ Failed to fill date input via JS: " + inputId);
                // Fallback to standard fillInput if JS fails, though it might not work for complex date pickers
                return fillInput(driver, inputId, date);
            }
        } catch (Exception e) {
            System.out.println("❌ Error filling date input " + inputId + ": " + e.getMessage());
            e.printStackTrace();
            // Fallback to standard fillInput
            return fillInput(driver, inputId, date);
        }
    }

    /**
     * Fill input by position (for dynamically added fields).
     * This finds the target input based on its position from the end of all visible,
     * non-readonly/non-disabled input elements.
     *
     * @param driver The WebDriver instance.
     * @param position The position from the newest input (0 for newest, 1 for second newest, etc.).
     * @param value The string value to enter.
     * @return true if the input was filled, false otherwise.
     */
    private static boolean fillInputByPosition(WebDriver driver, int position, String value) {
        try {
            System.out.println("Filling input at position " + position + " with: " + value);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled])');" +
                            "var visibleInputs = Array.from(inputs).filter(i => i.offsetWidth > 0 || i.offsetHeight > 0);" + // Check visibility
                            "var targetInput = visibleInputs[visibleInputs.length - 1 - " + position + "];" +
                            "if (targetInput) {" +
                            "  targetInput.value = '" + value + "';" +
                            "  targetInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  targetInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );
            if (result != null && result) {
                System.out.println("✅ Filled input at position " + position + ": " + value);
                return true;
            } else {
                System.out.println("❌ Failed to fill input at position " + position);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error filling input by position " + position + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill date input by position (for dynamically added date fields).
     * Similar to fillInputByPosition but specifically targets date inputs with a mask.
     *
     * @param driver The WebDriver instance.
     * @param position The position from the newest date input (0 for newest, 1 for second newest, etc.).
     * @param date The date string in "MM/dd/yyyy" format.
     * @return true if the date input was filled, false otherwise.
     */
    private static boolean fillDateInputByPosition(WebDriver driver, int position, String date) {
        try {
            System.out.println("Filling date input at position " + position + " with: " + date);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                            "var visibleInputs = Array.from(inputs).filter(i => i.offsetWidth > 0 || i.offsetHeight > 0);" + // Check visibility
                            "var targetInput = visibleInputs[visibleInputs.length - 1 - " + position + "];" +
                            "if (targetInput) {" +
                            "  targetInput.value = '" + date + "';" +
                            "  targetInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  targetInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );
            if (result != null && result) {
                System.out.println("✅ Filled date input at position " + position + ": " + date);
                return true;
            } else {
                System.out.println("❌ Failed to fill date input at position " + position);
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error filling date input by position " + position + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Select dropdown if present (for conditional dropdowns).
     * This method checks if a dropdown element is present and visible before attempting to select from it.
     *
     * @param driver The WebDriver instance.
     * @param selectId The ID of the mat-select element.
     * @param optionId The ID of the mat-option element to select.
     * @return true if the dropdown was selected (or if it wasn't present), false if an error occurred during selection.
     */
    private static boolean selectDropdownIfPresent(WebDriver driver, String selectId, String optionId) {
        try {
            // Use WebDriverWait to check for visibility, making it more robust
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // Short wait
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(selectId)));
            if (element != null && element.isDisplayed()) {
                System.out.println("ℹ️ Conditional dropdown '" + selectId + "' is present. Attempting selection.");
                return selectDropdownEnhanced(driver, selectId, optionId);
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Conditional dropdown '" + selectId + "' not found or not visible. Skipping selection.");
            // Element not found or not visible within the wait time, which is expected for conditional fields
        }
        return true; // Not considered an error if the dropdown doesn't exist
    }

    /**
     * Fill textarea field.
     *
     * @param driver The WebDriver instance.
     * @param textareaId The ID of the textarea element.
     * @param value The string value to enter.
     * @return true if the textarea was filled, false otherwise.
     */
    private static boolean fillTextarea(WebDriver driver, String textareaId, String value) {
        try {
            WebElement textarea = driver.findElement(By.id(textareaId));
            textarea.clear();
            textarea.sendKeys(value);
            System.out.println("✅ Filled textarea: " + textareaId);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to fill textarea '" + textareaId + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check and click submit button if enabled.
     * This method uses JavaScript to find a submit button and click it only if it's not disabled.
     *
     * @param driver The WebDriver instance.
     * @return true if the submit button was clicked, false otherwise.
     */
    private static boolean checkAndClickSubmit(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var submitBtn = document.querySelector('button.submit-button, button[type=\"submit\"], button[aria-label*=\"Submit\"], button[title*=\"Submit\"]');" +
                            "if (submitBtn && !submitBtn.disabled) {" +
                            "  submitBtn.click();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Clicked SUBMIT button");
                Thread.sleep(2000); // Wait for submission to process
                return true;
            } else {
                System.out.println("ℹ️ SUBMIT button is disabled or not found");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error checking/clicking submit button: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate a past date string in "MM/dd/yyyy" format.
     *
     * @param minDaysAgo Minimum number of days in the past.
     * @param maxDaysAgo Maximum number of days in the past.
     * @return A randomly generated past date string.
     */
    private static String generatePastDate(int minDaysAgo, int maxDaysAgo) {
        java.time.LocalDate date = java.time.LocalDate.now()
                .minusDays(minDaysAgo + random.nextInt(maxDaysAgo - minDaysAgo));
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    /**
     * Generate a future date string in "MM/dd/yyyy" format.
     *
     * @param minDaysAhead Minimum number of days in the future.
     * @param maxDaysAhead Maximum number of days in the future.
     * @return A randomly generated future date string.
     */
    private static String generateFutureDate(int minDaysAhead, int maxDaysAhead) {
        java.time.LocalDate date = java.time.LocalDate.now()
                .plusDays(minDaysAhead + random.nextInt(maxDaysAhead - minDaysAhead));
        return date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    /**
     * ROBUST button clicking - handles interception using JavaScript.
     * This version can search by button text or by ID.
     *
     * @param driver The WebDriver instance.
     * @param identifier The text content of the button or its ID.
     * @return true if the button was successfully clicked, false otherwise.
     */
    private static boolean clickButtonRobust(WebDriver driver, String identifier) {
        return clickButtonRobust(driver, identifier, null); // Call the overloaded method
    }

    /**
     * ROBUST button clicking - handles interception using JavaScript, with optional ID.
     * This method first attempts to remove any blocking overlays, then uses JavaScript
     * to find and click the button, either by its ID or by its text content.
     *
     * @param driver The WebDriver instance.
     * @param identifier The text content of the button or its ID.
     * @param textFallback An optional text to search for if 'identifier' is an ID and not found.
     * @return true if the button was successfully clicked, false otherwise.
     */
    private static boolean clickButtonRobust(WebDriver driver, String identifier, String textFallback) {
        try {
            System.out.println("Attempting robust click for: " + identifier + (textFallback != null ? " (fallback: " + textFallback + ")" : ""));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Aggressively remove any overlays that might be blocking clicks
            js.executeScript(
                    "var overlays = document.querySelectorAll('.cdk-overlay-backdrop, .mat-dialog-container, .cdk-overlay-pane');" +
                            "for (var i = 0; i < overlays.length; i++) {" +
                            "  if (overlays[i].style.display !== 'none') {" +
                            "    overlays[i].remove();" + // Remove from DOM
                            "  }" +
                            "}" +
                            "// Also try to blur active elements" +
                            "if (document.activeElement) document.activeElement.blur();"
            );
            Thread.sleep(500); // Give a moment for overlays to be removed

            // Use JavaScript to find and click button, prioritizing ID if available
            String script =
                    "var button = document.getElementById('" + identifier.replace("'", "\\'") + "');" +
                            "if (button && (button.offsetWidth > 0 || button.offsetHeight > 0)) {" + // Check if visible
                            "  button.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(function() { button.click(); }, 500);" +
                            "  return true;" +
                            "}";

            // If identifier is not an ID or ID button not found/visible, search by text content
            String searchText = (textFallback != null) ? textFallback : identifier;
            script +=
                    "var buttons = document.querySelectorAll('button, a[role=\"button\"], cbp-button button');" + // Include cbp-button
                            "for (var i = 0; i < buttons.length; i++) {" +
                            "  var text = buttons[i].textContent.trim();" +
                            "  if (text.includes('" + searchText.replace("'", "\\'") + "') && (buttons[i].offsetWidth > 0 || buttons[i].offsetHeight > 0)) {" + // Check visibility
                            "    buttons[i].scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "    setTimeout(function() { buttons[i].click(); }, 500);" +
                            "    return true;" +
                            "  }" +
                            "}" +
                            "return false;";

            Boolean result = (Boolean) js.executeScript(script);

            if (result != null && result) {
                Thread.sleep(1000); // Wait for the click action to register and page to react
                System.out.println("✅ Clicked button: " + identifier + (textFallback != null ? " (via text: " + textFallback + ")" : ""));
                return true;
            }

            System.out.println("❌ Button not found or not clickable: " + identifier + (textFallback != null ? " (text: " + textFallback + ")" : ""));
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error clicking button '" + identifier + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Simple button clicking (KEEP WORKING VERSION).
     * This method uses Selenium's findElement by XPath.
     *
     * @param driver The WebDriver instance.
     * @param buttonText The text content of the button.
     * @return true if the button was clicked, false otherwise.
     */
    private static boolean clickButtonSimple(WebDriver driver, String buttonText) {
        try {
            try {
                WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
                button.click();
                System.out.println("✅ Clicked simple button (button tag): " + buttonText);
                return true;
            } catch (Exception e) {
                // Fallback to 'a' tag if button not found
                WebElement button = driver.findElement(By.xpath("//a[contains(text(), '" + buttonText + "')]"));
                button.click();
                System.out.println("✅ Clicked simple button (a tag): " + buttonText);
                return true;
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to click simple button '" + buttonText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill input field (KEEP WORKING VERSION).
     * This method clears an input field and then sends keys to it.
     *
     * @param driver The WebDriver instance.
     * @param inputId The ID of the input element.
     * @param value The string value to enter.
     * @return true if the input was filled, false otherwise.
     */
    private static boolean fillInput(WebDriver driver, String inputId, String value) {
        try {
            WebElement input = driver.findElement(By.id(inputId));
            input.clear();
            input.sendKeys(value);
            System.out.println("✅ Filled " + inputId + ": " + value);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to fill " + inputId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Wait for element visibility and send keys (KEEP WORKING VERSION).
     * This method waits for an element to be visible before clearing and sending keys.
     *
     * @param driver The WebDriver instance.
     * @param by The By locator strategy for the element.
     * @param text The text to send to the element.
     * @return true if keys were sent, false otherwise.
     */
    private static boolean waitAndSendKeys(WebDriver driver, By by, String text) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element.clear();
            element.sendKeys(text);
            System.out.println("✅ Sent keys to element " + by + ": " + text);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error sending keys to element " + by + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
