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

            // Fill basic fields
            System.out.println("Filling last name: " + data.getLastName());
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            System.out.println("Filling first name: " + data.getFirstName());
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            System.out.println("Filling DOB: " + data.getDob());
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
            fillInput(driver, "mat-input-4", primaryStartDate);

            System.out.println("5b. Filling Primary End Date");
            String primaryEndDate = generateFutureDate(30, 365); // 30 days to 1 year from now
            fillInput(driver, "mat-input-5", primaryEndDate);

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
            String raceOption = "mat-option-" + (594 + random.nextInt(6));
            addFieldWithEnhancedDropdown(driver, "Add Race", "mat-select-18", raceOption);

            System.out.println("13. Adding Eye Color");
            String eyeOption = "mat-option-" + (600 + random.nextInt(12));
            addFieldWithEnhancedDropdown(driver, "Add Eye Color", "mat-select-20", eyeOption);

            System.out.println("14. Adding Hair Color");
            String hairOption = "mat-option-" + (612 + random.nextInt(15));
            addFieldWithEnhancedDropdown(driver, "Add Hair Color", "mat-select-22", hairOption);

            // === NAME SECTION ===
            System.out.println("\n15. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(2000);
                fillInput(driver, "mat-input-2", data.getLastName());
                fillInput(driver, "mat-input-3", data.getFirstName());
                // Middle name is optional - mat-input-7
            }

            // === DATE OF BIRTH ===
            System.out.println("\n16. Adding DOB");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(2000);
                fillDateInput(driver, "mat-input-11", data.getDob());
            }

            // === CITIZENSHIP ===
            System.out.println("\n17. Adding Citizenship");
            addFieldWithEnhancedDropdown(driver, "Add Citizenship", "mat-select-34", "mat-option-1260"); // USA

            // === PASSPORT ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(3000);

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
                Thread.sleep(2000);
                fillInput(driver, "mat-input-22", data.getaNumber());
            }

            // === DRIVER'S LICENSE ===
            System.out.println("\n20. Adding Driver's License");
            if (clickButtonRobust(driver, "Add Driver")) { // Shorter text to avoid apostrophe issues
                Thread.sleep(3000);

                // License Number
                System.out.println("  - Filling license number");
                fillInput(driver, "mat-input-23", data.getDriverLicense());

                // License State
                System.out.println("  - Selecting state");
                String stateOption = "mat-option-" + (1774 + random.nextInt(62)); // Random US state
                selectDropdownByPosition(driver, 0, stateOption);
            }

            // === SSN ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(2000);
                fillSSNInput(driver, data.getSsn());
            }

            // === ADDITIONAL FIELDS (from page2.txt analysis) ===
            System.out.println("\n=== ADDING ADDITIONAL FIELDS ===");

            // === MISC NUMBER ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonRobust(driver, "Add Misc")) {
                Thread.sleep(3000);

                // Misc Type dropdown
                selectDropdownByPosition(driver, 0, "mat-option-" + (1885 + random.nextInt(5)));

                // Misc Number
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputByPosition(driver, 0, miscNumber);
            }

            // === PHONE NUMBER ===
            System.out.println("\n23. Adding Phone Number");
            if (clickButtonRobust(driver, "Add Phone")) {
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
            if (clickButtonRobust(driver, "Add Alter")) {
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
            if (clickButtonRobust(driver, "Add Financial")) {
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
     * Enhanced dropdown selection with guaranteed closure
     */
    private static boolean selectDropdownEnhanced(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Enhanced selection: " + selectId + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // First ensure any open dropdowns are closed
            ensureAllDropdownsClosed(driver);
            Thread.sleep(500);

            // Execute the dropdown selection with guaranteed closure
            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  console.log('Starting dropdown selection: " + selectId + "');" +
                            "  " +
                            "  // Find the select element" +
                            "  var select = document.getElementById('" + selectId + "');" +
                            "  if (!select) {" +
                            "    console.error('Select not found: " + selectId + "');" +
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Scroll into view and click" +
                            "  select.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  " +
                            "  // Click the select to open dropdown" +
                            "  setTimeout(() => {" +
                            "    select.click();" +
                            "    console.log('Clicked select, waiting for panel...');" +
                            "    " +
                            "    // Wait for panel to appear and select option" +
                            "    setTimeout(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option) {" +
                            "        console.log('Found option, clicking: " + optionId + "');" +
                            "        option.click();" +
                            "        " +
                            "        // CRITICAL: Force close the dropdown after selection" +
                            "        setTimeout(() => {" +
                            "          // Method 1: Click body" +
                            "          document.body.click();" +
                            "          " +
                            "          // Method 2: Press Escape" +
                            "          var escEvent = new KeyboardEvent('keydown', {" +
                            "            key: 'Escape'," +
                            "            keyCode: 27," +
                            "            bubbles: true" +
                            "          });" +
                            "          document.dispatchEvent(escEvent);" +
                            "          " +
                            "          // Method 3: Remove overlay backdrop" +
                            "          var backdrop = document.querySelector('.cdk-overlay-backdrop');" +
                            "          if (backdrop) backdrop.click();" +
                            "          " +
                            "          // Method 4: Hide all panels" +
                            "          var panels = document.querySelectorAll('.mat-select-panel');" +
                            "          panels.forEach(p => p.style.display = 'none');" +
                            "          " +
                            "          console.log('Dropdown closed');" +
                            "          resolve(true);" +
                            "        }, 500);" +
                            "      } else {" +
                            "        console.error('Option not found: " + optionId + "');" +
                            "        // Still close dropdown even if option not found" +
                            "        document.body.click();" +
                            "        resolve(false);" +
                            "      }" +
                            "    }, 1500);" +
                            "  }, 500);" +
                            "});"
            );

            // Wait extra time to ensure closure
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
            return false;
        }
    }

    /**
     * Add field with enhanced dropdown handling - specifically for dynamically added fields
     */
    private static boolean addFieldWithEnhancedDropdown(WebDriver driver, String buttonText,
                                                        String expectedSelectId, String optionId) {
        try {
            System.out.println("🎯 Adding " + buttonText + " with enhanced dropdown");

            // Click the add button
            if (!clickButtonRobust(driver, buttonText)) {
                return false;
            }

            Thread.sleep(3000); // Wait for new field to appear

            // Use the expected select ID if available, otherwise find newest
            if (expectedSelectId != null && !expectedSelectId.isEmpty()) {
                return selectDropdownEnhanced(driver, expectedSelectId, optionId);
            } else {
                // Fallback to position-based selection
                return selectDropdownByPosition(driver, 0, optionId);
            }

        } catch (Exception e) {
            System.out.println("❌ Error adding field with dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Select dropdown by position (for dynamically added fields)
     */
    private static boolean selectDropdownByPosition(WebDriver driver, int position, String optionId) {
        try {
            System.out.println("🎯 Selecting dropdown at position " + position + " → " + optionId);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Ensure dropdowns are closed first
            ensureAllDropdownsClosed(driver);
            Thread.sleep(500);

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var selects = document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])');" +
                            "  console.log('Found ' + selects.length + ' active dropdowns');" +
                            "  " +
                            "  // Get dropdowns from bottom (newest first)" +
                            "  var targetSelect = selects[selects.length - 1 - " + position + "];" +
                            "  if (!targetSelect) {" +
                            "    console.error('No dropdown at position " + position + "');" +
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Click to open" +
                            "  targetSelect.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  targetSelect.click();" +
                            "  " +
                            "  // Wait and select option" +
                            "  setTimeout(() => {" +
                            "    var option = document.getElementById('" + optionId + "');" +
                            "    if (option) {" +
                            "      option.click();" +
                            "      " +
                            "      // Force close" +
                            "      setTimeout(() => {" +
                            "        document.body.click();" +
                            "        var escEvent = new KeyboardEvent('keydown', {key: 'Escape', keyCode: 27, bubbles: true});" +
                            "        document.dispatchEvent(escEvent);" +
                            "        resolve(true);" +
                            "      }, 500);" +
                            "    } else {" +
                            "      console.error('Option not found: " + optionId + "');" +
                            "      document.body.click();" +
                            "      resolve(false);" +
                            "    }" +
                            "  }, 1500);" +
                            "});"
            );

            Thread.sleep(2500);
            return result != null && result;

        } catch (Exception e) {
            System.out.println("❌ Error selecting by position: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ensure all dropdowns are closed before proceeding
     */
    private static void ensureAllDropdownsClosed(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "// Close all open dropdowns" +
                            "document.body.click();" +
                            "document.activeElement.blur();" +
                            "" +
                            "// Remove overlay backdrops" +
                            "var backdrops = document.querySelectorAll('.cdk-overlay-backdrop');" +
                            "backdrops.forEach(b => b.click());" +
                            "" +
                            "// Hide all panels" +
                            "var panels = document.querySelectorAll('.mat-select-panel');" +
                            "panels.forEach(p => p.style.display = 'none');" +
                            "" +
                            "// Remove overlay containers" +
                            "var overlays = document.querySelectorAll('.cdk-overlay-container');" +
                            "overlays.forEach(o => {" +
                            "  if (o.innerHTML.includes('mat-select-panel')) {" +
                            "    o.innerHTML = '';" +
                            "  }" +
                            "});"
            );
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("Error ensuring dropdowns closed: " + e.getMessage());
        }
    }

    /**
     * Select height dropdown with proper format matching
     */
    private static boolean selectHeightDropdown(WebDriver driver, String selectId, String heightValue) {
        try {
            // Height options start from mat-option-8 (3' 0") and go up
            // We need to find the right option based on the height value
            int startOption = 8;
            int feet = Integer.parseInt(heightValue.split("'")[0].trim());
            int inches = Integer.parseInt(heightValue.split("'")[1].replace("\"", "").trim());

            // Calculate option offset (each foot has 12 options)
            int optionOffset = (feet - 3) * 12 + inches;
            String optionId = "mat-option-" + (startOption + optionOffset);

            return selectDropdownEnhanced(driver, selectId, optionId);
        } catch (Exception e) {
            // Fallback to random height
            int randomOption = 8 + random.nextInt(60);
            return selectDropdownEnhanced(driver, selectId, "mat-option-" + randomOption);
        }
    }

    /**
     * Fill SSN input (special handling for SSN mask)
     */
    private static boolean fillSSNInput(WebDriver driver, String ssn) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
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
            }
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error filling SSN: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill date input with calendar widget
     */
    private static boolean fillDateInput(WebDriver driver, String inputId, String date) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "var input = document.getElementById('" + inputId + "');" +
                            "if (input) {" +
                            "  input.value = '" + date + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "}"
            );
            return true;
        } catch (Exception e) {
            return fillInput(driver, inputId, date);
        }
    }

    /**
     * Fill input by position (for dynamically added fields)
     */
    private static boolean fillInputByPosition(WebDriver driver, int position, String value) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled])');" +
                            "var visibleInputs = Array.from(inputs).filter(i => i.offsetHeight > 0);" +
                            "var targetInput = visibleInputs[visibleInputs.length - 1 - " + position + "];" +
                            "if (targetInput) {" +
                            "  targetInput.value = '" + value + "';" +
                            "  targetInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  targetInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );
            return result != null && result;
        } catch (Exception e) {
            System.out.println("Error filling input by position: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill date input by position
     */
    private static boolean fillDateInputByPosition(WebDriver driver, int position, String date) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var inputs = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                            "var targetInput = inputs[inputs.length - 1 - " + position + "];" +
                            "if (targetInput) {" +
                            "  targetInput.value = '" + date + "';" +
                            "  targetInput.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  targetInput.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );
            return result != null && result;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Select dropdown if present (for conditional dropdowns)
     */
    private static boolean selectDropdownIfPresent(WebDriver driver, String selectId, String optionId) {
        try {
            WebElement element = driver.findElement(By.id(selectId));
            if (element.isDisplayed()) {
                return selectDropdownEnhanced(driver, selectId, optionId);
            }
        } catch (Exception e) {
            // Element not found or not visible
        }
        return true; // Not an error if dropdown doesn't exist
    }

    /**
     * Fill textarea
     */
    private static boolean fillTextarea(WebDriver driver, String textareaId, String value) {
        try {
            WebElement textarea = driver.findElement(By.id(textareaId));
            textarea.clear();
            textarea.sendKeys(value);
            System.out.println("✅ Filled textarea: " + textareaId);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to fill textarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check and click submit button if enabled
     */
    private static boolean checkAndClickSubmit(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var submitBtn = document.querySelector('button.submit-button');" +
                            "if (submitBtn && !submitBtn.disabled) {" +
                            "  submitBtn.click();" +
                            "  return true;" +
                            "}" +
                            "return false;"
            );

            if (result != null && result) {
                System.out.println("✅ Clicked SUBMIT button");
                return true;
            } else {
                System.out.println("ℹ️ SUBMIT button is disabled or not found");
                return false;
            }
        } catch (Exception e) {
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
     * ROBUST button clicking - handles interception
     */
    private static boolean clickButtonRobust(WebDriver driver, String buttonText) {
        try {
            // Clear any overlays first
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "// Remove any overlays that might be blocking clicks " +
                            "var overlays = document.querySelectorAll('.cdk-overlay-backdrop, .mat-dialog-container'); " +
                            "for (var i = 0; i < overlays.length; i++) { " +
                            "  overlays[i].remove(); " +
                            "}"
            );

            // Use JavaScript to find and click button
            Boolean result = (Boolean) js.executeScript(
                    "var buttons = document.querySelectorAll('button'); " +
                            "for (var i = 0; i < buttons.length; i++) { " +
                            "  var text = buttons[i].textContent.trim(); " +
                            "  if (text.includes('" + buttonText.replace("'", "\\'") + "')) { " +
                            "    buttons[i].scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                            "    setTimeout(function() { " +
                            "      buttons[i].click(); " +
                            "    }, 500); " +
                            "    return true; " +
                            "  } " +
                            "} " +
                            "return false;"
            );

            if (result != null && result) {
                Thread.sleep(1000);
                System.out.println("✅ Clicked button: " + buttonText);
                return true;
            }

            System.out.println("❌ Button not found: " + buttonText);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error clicking button: " + e.getMessage());
            return false;
        }
    }

    /**
     * Simple button clicking (KEEP WORKING VERSION)
     */
    private static boolean clickButtonSimple(WebDriver driver, String buttonText) {
        try {
            try {
                WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
                button.click();
                return true;
            } catch (Exception e) {
                WebElement button = driver.findElement(By.xpath("//a[contains(text(), '" + buttonText + "')]"));
                button.click();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fill input field (KEEP WORKING VERSION)
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
            return false;
        }
    }

    /**
     * Wait and send keys (KEEP WORKING VERSION)
     */
    private static boolean waitAndSendKeys(WebDriver driver, By by, String text) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            element.clear();
            element.sendKeys(text);
            return true;
        } catch (Exception e) {
            System.out.println("Error sending keys to element " + by + ": " + e.getMessage());
            return false;
        }
    }
}