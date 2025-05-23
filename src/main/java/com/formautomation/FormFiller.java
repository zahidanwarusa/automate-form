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
    private static final Duration DEFAULT_WAIT_TIME = Duration.ofSeconds(15); // Increased default wait time

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
            // Using fillDateInput for DOB here as well for consistency and robustness with date pickers.
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
            selectDropdown(driver, "mat-select-4", "mat-option-68");

            System.out.println("2. Category dropdown (AB - AG/BIO COUNTERMEASURES)");
            selectDropdown(driver, "mat-select-10", "mat-option-549");

            System.out.println("3. Query Notification dropdown (0 - NO NOTIFICATION)");
            selectDropdown(driver, "mat-select-6", "mat-option-238");

            System.out.println("4. Exclusions dropdown (Multiple - ANCX)");
            selectDropdown(driver, "mat-select-12", "mat-option-253");

            System.out.println("5. Primary Action dropdown (4 - REFER TO PASSPORT CONTROL)");
            selectDropdown(driver, "mat-select-8", "mat-option-245");

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
            selectDropdown(driver, "mat-select-0", "mat-option-2"); // Y - Yes

            System.out.println("9. Height dropdown");
            String heightOption = data.getHeight(); // e.g., "5' 10\""
            selectHeightDropdown(driver, "mat-select-2", heightOption);

            System.out.println("10. Weight field");
            fillInput(driver, "mat-input-0", data.getWeight()); // Using fillInput for weight

            // === ADD SECTIONS - Enhanced with proper waiting ===
            System.out.println("\n=== ADDING DYNAMIC SECTIONS ===");

            System.out.println("11. Adding Sex");
            addFieldWithDropdown(driver, "Add Sex", random.nextBoolean() ? "630" : "631"); // F or M

            System.out.println("12. Adding Race");
            String raceOption = String.valueOf(594 + random.nextInt(6)); // Random race option
            addFieldWithDropdown(driver, "Add Race", raceOption);

            System.out.println("13. Adding Eye Color");
            String eyeOption = String.valueOf(600 + random.nextInt(12)); // Random eye color option
            addFieldWithDropdown(driver, "Add Eye Color", eyeOption);

            System.out.println("14. Adding Hair Color");
            String hairOption = String.valueOf(612 + random.nextInt(15)); // Random hair color option
            addFieldWithDropdown(driver, "Add Hair Color", hairOption);

            // === NAME SECTION ===
            System.out.println("\n15. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(2000); // Wait for input fields to appear
                fillInput(driver, "mat-input-2", data.getLastName());
                fillInput(driver, "mat-input-3", data.getFirstName());
                // Middle name is optional - mat-input-7
            }

            // === DATE OF BIRTH (DOB) ===
            System.out.println("\n16. Adding DOB (Page 2)");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(2000); // Wait for input field to appear
                fillDateInput(driver, "mat-input-11", data.getDob());
            }

            // === CITIZENSHIP ===
            System.out.println("\n17. Adding Citizenship");
            addFieldWithDropdown(driver, "Add Citizenship", "1260"); // USA

            // === PASSPORT ===
            System.out.println("\n18. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(3000); // Wait for new passport fields to appear

                // Passport Type
                System.out.println("  - Selecting passport type");
                selectNewestDropdown(driver, "1518"); // P - Regular

                // Passport Number
                System.out.println("  - Filling passport number");
                fillInput(driver, "mat-input-19", data.getPassportNumber()); // Assuming mat-input-19 is the passport number field

                // Passport Country
                System.out.println("  - Selecting passport country");
                selectSecondNewestDropdown(driver, "1520"); // USA

                // Passport Issue Date
                System.out.println("  - Filling passport issue date");
                fillInput(driver, "mat-input-20", data.getPassportIssueDate()); // Assuming mat-input-20 is the issue date field

                // Passport Expiry Date
                System.out.println("  - Filling passport expiry date");
                fillInput(driver, "mat-input-21", data.getPassportExpiryDate()); // Assuming mat-input-21 is the expiry date field
            }

            // === A NUMBER ===
            System.out.println("\n19. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(2000); // Wait for input field to appear
                fillInput(driver, "mat-input-22", data.getaNumber()); // Assuming mat-input-22 is for A#
            }

            // === DRIVER'S LICENSE ===
            System.out.println("\n20. Adding Driver's License");
            if (clickButtonRobust(driver, "Add Driver's License")) {
                Thread.sleep(3000); // Wait for new license fields to appear

                // License Number
                System.out.println("  - Filling license number");
                fillInput(driver, "mat-input-23", data.getDriverLicense()); // Assuming mat-input-23 is for Driver's License

                // License State
                System.out.println("  - Selecting state");
                String stateOption = String.valueOf(1774 + random.nextInt(62)); // Random US state
                selectNewestDropdown(driver, stateOption);
            }

            // === SSN ===
            System.out.println("\n21. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(2000); // Wait for input field to appear
                fillSSNInput(driver, data.getSsn());
            }

            // === ADDITIONAL FIELDS (from page2.txt analysis) ===
            System.out.println("\n=== ADDING ADDITIONAL FIELDS ===");

            // === MISC NUMBER ===
            System.out.println("\n22. Adding Misc Number");
            if (clickButtonRobust(driver, "Add Misc Number")) {
                Thread.sleep(3000);

                // Misc Type dropdown
                selectNewestDropdown(driver, String.valueOf(1885 + random.nextInt(5)));

                // Misc Number
                String miscNumber = "MISC" + (100000 + random.nextInt(900000));
                fillInputByPosition(driver, 0, miscNumber);
            }

            // === PHONE NUMBER ===
            System.out.println("\n23. Adding Phone Number");
            if (clickButtonRobust(driver, "Add Phone Number")) {
                Thread.sleep(3000);

                // Phone Type
                selectNewestDropdown(driver, String.valueOf(1890 + random.nextInt(4)));

                // Phone Country
                selectSecondNewestDropdown(driver, "1895"); // USA

                // Phone Number
                String phoneNumber = "202" + (1000000 + random.nextInt(9000000));
                fillInputByPosition(driver, 0, phoneNumber);
            }

            // === ALTERNATIVE COMMUNICATIONS ===
            System.out.println("\n24. Adding Alternative Communication");
            if (clickButtonRobust(driver, "Add Alternative Communication")) {
                Thread.sleep(3000);

                // Communication Type
                selectNewestDropdown(driver, String.valueOf(1900 + random.nextInt(3)));

                // Communication Value
                String email = "test" + System.currentTimeMillis() + "@example.com";
                fillInputByPosition(driver, 0, email);
            }

            // === ADDRESS ===
            System.out.println("\n25. Adding Address");
            if (clickButtonRobust(driver, "Add Address")) {
                Thread.sleep(3000);

                // Address Type
                selectNewestDropdown(driver, String.valueOf(1910 + random.nextInt(4)));

                // Street
                fillInputByPosition(driver, 0, "123 Test Street");

                // City
                fillInputByPosition(driver, 1, "Washington");

                // State (if US address)
                selectSecondNewestDropdown(driver, "1915"); // DC

                // Country
                selectDropdownByPosition(driver, 2, "1260"); // USA
                // Note: selectDropdownByPosition is still used here, as it's a general utility.
                // The 'old' file didn't have a specific 'selectThirdNewestDropdown', so we'll
                // keep the most robust, general solution for cases like this.

                // Postal Code
                fillInputByPosition(driver, 2, "20001");
            }

            // === FINANCIAL ACCOUNT ===
            System.out.println("\n26. Adding Financial Account");
            if (clickButtonRobust(driver, "Add Financial Account")) {
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
            forceCloseDropdown(driver);
            Thread.sleep(2000);

            // Try to submit the form (if submit button is enabled)
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

    /**
     * ROBUST dropdown selection - handles interception with FORCED closure
     * This method is adapted from FormFiller old.java.
     */
    private static boolean selectDropdown(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Selecting " + selectId + " → " + optionId);

            // Method 1: Try standard approach with aggressive closing
            try {
                WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
                WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id(selectId)));
                matSelect.click();
                Thread.sleep(1500); // Give time for options to appear

                WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.id(optionId)));
                option.click();
                Thread.sleep(500);

                // AGGRESSIVE dropdown closing - try multiple methods
                forceCloseDropdown(driver);

                System.out.println("✅ Selected " + selectId + " → " + optionId);
                return true;
            } catch (Exception e) {
                System.err.println("Standard approach failed: " + e.getMessage());
            }

            // Method 2: JavaScript approach for interception with forced close
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var select = document.getElementById('" + selectId + "'); " +
                                "if (select) { " +
                                "  // Try clicking trigger first " +
                                "  var trigger = select.querySelector('.mat-select-trigger'); " +
                                "  if (trigger) { " +
                                "    trigger.click(); " +
                                "  } else { " +
                                "    select.click(); " +
                                "  } " +
                                "  setTimeout(function() { " +
                                "    var option = document.getElementById('" + optionId + "'); " +
                                "    if (option) { " +
                                "      option.click(); " +
                                "      // Force close dropdown multiple ways " +
                                "      setTimeout(function() { " +
                                "        document.body.click(); " +
                                "        document.activeElement.blur(); " +
                                "        // Click somewhere safe " +
                                "        var safe = document.querySelector('h1, h2, .page-title'); " +
                                "        if (safe) safe.click(); " +
                                "      }, 500); " +
                                "    } " +
                                "  }, 1000); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(3000); // Wait for all actions to complete
                    System.out.println("✅ Selected " + selectId + " → " + optionId + " (JS)");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("JavaScript approach failed: " + e.getMessage());
            }

            System.out.println("❌ Failed to select " + selectId);
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * FORCE close any open dropdowns - multiple aggressive methods
     * This method is copied directly from FormFiller old.java.
     */
    private static void forceCloseDropdown(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Method 1: Click body
            js.executeScript("document.body.click();");
            Thread.sleep(300);

            // Method 2: Blur active element
            js.executeScript("if (document.activeElement) document.activeElement.blur();");
            Thread.sleep(300);

            // Method 3: Press ESC key
            js.executeScript("document.dispatchEvent(new KeyboardEvent('keydown', {key: 'Escape'}));");
            Thread.sleep(300);

            // Method 4: Click on a safe element (page title)
            js.executeScript(
                    "var safe = document.querySelector('h1, h2, .page-title, .mat-toolbar'); " +
                            "if (safe) safe.click();"
            );
            Thread.sleep(300);

            // Method 5: Remove any open overlay panels
            js.executeScript(
                    "var panels = document.querySelectorAll('.mat-select-panel, .cdk-overlay-pane'); " +
                            "for (var i = 0; i < panels.length; i++) { " +
                            "  if (panels[i].style.display !== 'none') { " +
                            "    panels[i].style.display = 'none'; " +
                            "  } " +
                            "}"
            );

            System.out.println("🔒 Force closed dropdown");

        } catch (Exception e) {
            System.err.println("Error force closing dropdown: " + e.getMessage());
        }
    }

    /**
     * Add field with dropdown - ENHANCED with better option clicking
     * This method is adapted from FormFiller old.java.
     */
    private static boolean addFieldWithDropdown(WebDriver driver, String buttonText, String optionNumber) {
        try {
            System.out.println("🎯 Adding " + buttonText + " with option " + optionNumber);

            if (clickButtonRobust(driver, buttonText)) {
                Thread.sleep(3000); // Wait for new dropdown

                // Enhanced JavaScript approach for new dropdowns
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // First, get count of dropdowns to find the newest one
                // Use WebDriverWait to ensure dropdowns are present before querying
                WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
                wait.until(d -> ((List<WebElement>) js.executeScript("return Array.from(document.querySelectorAll('mat-select'));")).size() > 0);

                Long selectCount = (Long) js.executeScript("return document.querySelectorAll('mat-select').length;");
                System.out.println("Found " + selectCount + " dropdowns after adding " + buttonText);

                Boolean result = (Boolean) js.executeScript(
                        "var selects = document.querySelectorAll('mat-select'); " +
                                "console.log('Total selects found: ' + selects.length); " +

                                "if (selects.length > 0) { " +
                                "  var newest = selects[selects.length - 1]; " +
                                "  console.log('Clicking newest dropdown'); " +
                                "  newest.scrollIntoView({behavior: 'smooth', block: 'center'}); " +

                                "  // Try clicking trigger first " +
                                "  var trigger = newest.querySelector('.mat-select-trigger'); " +
                                "  if (trigger) { " +
                                "    trigger.click(); " +
                                "  } else { " +
                                "    newest.click(); " +
                                "  } " +

                                "  // Wait then select option " +
                                "  setTimeout(function() { " +
                                "    console.log('Looking for option: mat-option-" + optionNumber + "'); " +
                                "    var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                                "    if (option) { " +
                                "      option.click(); " +
                                "      // Force close after selection " +
                                "      setTimeout(function() { " +
                                "        document.body.click(); " +
                                "        document.activeElement.blur(); " +
                                "      }, 500); " + // Increased delay for closure
                                "    } else { " +
                                "      console.log('Option not found!'); " +
                                "    } " +
                                "  }, 2000); " + // Increased delay for option to appear
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(4000); // Wait for all actions to complete

                    // Force close any remaining dropdowns
                    forceCloseDropdown(driver);

                    System.out.println("✅ Added " + buttonText + " with option " + optionNumber);
                    return true;
                } else {
                    System.err.println("❌ JavaScript execution failed for " + buttonText);
                }
            }

            System.out.println("❌ Failed to add " + buttonText);
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error adding " + buttonText + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Select option from the newest (most recently added) dropdown
     * This method is copied directly from FormFiller old.java.
     */
    private static boolean selectNewestDropdown(WebDriver driver, String optionNumber) {
        try {
            System.out.println("🎯 Selecting from newest dropdown: option " + optionNumber);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            // Wait until at least one mat-select is present
            wait.until(d -> ((List<WebElement>) js.executeScript("return Array.from(document.querySelectorAll('mat-select'));")).size() > 0);

            Boolean result = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select'); " +
                            "console.log('Found ' + selects.length + ' dropdowns'); " +

                            "if (selects.length > 0) { " +
                            "  var newest = selects[selects.length - 1]; " +
                            "  console.log('Clicking newest dropdown'); " +
                            "  newest.scrollIntoView({behavior: 'smooth', block: 'center'}); " +

                            "  // Click dropdown " +
                            "  var trigger = newest.querySelector('.mat-select-trigger'); " +
                            "  if (trigger) { " +
                            "    trigger.click(); " +
                            "  } else { " +
                            "    newest.click(); " +
                            "  } " +

                            "  // Wait and select option " +
                            "  setTimeout(function() { " +
                            "    var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                            "    if (option) { " +
                            "      option.click(); " +
                            "      // Force close " +
                            "      setTimeout(function() { " +
                            "        document.body.click(); " +
                            "        document.activeElement.blur(); " +
                            "      }, 300); " +
                            "    } " +
                            "  }, 1500); " +
                            "  return true; " +
                            "} " +
                            "return false;"
            );

            if (result != null && result) {
                Thread.sleep(2500);
                System.out.println("✅ Selected option " + optionNumber + " from newest dropdown");
                return true;
            }

            System.out.println("❌ Failed to select from newest dropdown");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting newest dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Select option from the second newest dropdown (for passport country)
     * This method is copied directly from FormFiller old.java.
     */
    private static boolean selectSecondNewestDropdown(WebDriver driver, String optionNumber) {
        try {
            System.out.println("🎯 Selecting from second newest dropdown: option " + optionNumber);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            // Wait until at least two mat-select elements are present
            wait.until(d -> ((List<WebElement>) js.executeScript("return Array.from(document.querySelectorAll('mat-select'));")).size() >= 2);

            Boolean result = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select'); " +
                            "console.log('Found ' + selects.length + ' dropdowns for second newest'); " +

                            "if (selects.length >= 2) { " +
                            "  var secondNewest = selects[selects.length - 2]; " +
                            "  console.log('Clicking second newest dropdown'); " +
                            "  secondNewest.scrollIntoView({behavior: 'smooth', block: 'center'}); " +

                            "  // Click dropdown " +
                            "  var trigger = secondNewest.querySelector('.mat-select-trigger'); " +
                            "  if (trigger) { " +
                            "    trigger.click(); " +
                            "  } else { " +
                            "    secondNewest.click(); " +
                            "  } " +

                            "  // Wait and select option " +
                            "  setTimeout(function() { " +
                            "    var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                            "    if (option) { " +
                            "      option.click(); " +
                            "      // Force close " +
                            "      setTimeout(function() { " +
                            "        document.body.click(); " +
                            "        document.activeElement.blur(); " +
                            "      }, 300); " +
                            "    } " +
                            "  }, 1500); " +
                            "  return true; " +
                            "} " +
                            "return false;"
            );

            if (result != null && result) {
                Thread.sleep(2500);
                System.out.println("✅ Selected option " + optionNumber + " from second newest dropdown");
                return true;
            }

            System.out.println("❌ Failed to select from second newest dropdown");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error selecting second newest dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Select dropdown by position (for dynamically added fields).
     * This method is useful when the ID of a dynamically added dropdown is not static,
     * and you need to target it based on its order of appearance (e.g., the newest one).
     * This method is kept as a general utility, especially for cases where more than
     * the newest or second newest dropdown needs to be targeted.
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
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            // Ensure dropdowns are closed first to avoid interference
            forceCloseDropdown(driver);
            Thread.sleep(500);

            // Find the target mat-select element by position using JavaScript
            WebElement targetSelect = wait.until(d -> {
                List<WebElement> selects = (List<WebElement>) js.executeScript(
                        "return Array.from(document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])'));"
                );
                int targetIndex = selects.size() - 1 - position;
                if (targetIndex >= 0 && targetIndex < selects.size()) {
                    return selects.get(targetIndex);
                }
                return null;
            });

            if (targetSelect == null) {
                System.out.println("❌ No dropdown found at calculated position " + position);
                return false;
            }

            Boolean result = (Boolean) js.executeScript(
                    "return new Promise((resolve) => {" +
                            "  var targetSelect = arguments[0];" + // Pass WebElement directly
                            "  console.log('Found ' + document.querySelectorAll('mat-select:not([aria-disabled=\"true\"])').length + ' active dropdowns on the page.');" +
                            "  " +
                            "  // Find the mat-select-trigger within the target mat-select and click it" +
                            "  var trigger = targetSelect.querySelector('.mat-select-trigger');" +
                            "  if (!trigger) {" +
                            "    console.error('Mat-select-trigger not found for dropdown at position " + position + "');" +
                            "    resolve(false);" +
                            "    return;" +
                            "  }" +
                            "  " +
                            "  // Scroll the target dropdown into view and click it to open the panel" +
                            "  trigger.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "  setTimeout(() => {" +
                            "    trigger.click();" + // Click the trigger
                            "    console.log('Clicked dropdown at position " + position + ", waiting for options...');" +
                            "    " +
                            "    // Wait for options to appear and then click the desired option" +
                            "    var checkOptionInterval = setInterval(() => {" +
                            "      var option = document.getElementById('" + optionId + "');" +
                            "      if (option) {" +
                            "        clearInterval(checkOptionInterval);" +
                            "        console.log('Found option, attempting to click: " + optionId + "');" +
                            "        option.click();" +
                            "        " +
                            "        // CRITICAL: Force close the dropdown after selection using multiple methods" +
                            "        setTimeout(() => {" +
                            "          document.body.click();" +
                            "          var escEvent = new KeyboardEvent('keydown', {" +
                            "            key: 'Escape'," +
                            "            keyCode: 27," +
                            "            bubbles: true" +
                            "          });" +
                            "          document.dispatchEvent(escEvent);" +
                            "          var backdrop = document.querySelector('.cdk-overlay-backdrop');" +
                            "          if (backdrop) backdrop.click();" +
                            "          var panels = document.querySelectorAll('.mat-select-panel');" +
                            "          panels.forEach(p => p.style.display = 'none');" +
                            "          console.log('Dropdown at position " + position + " closed.');" +
                            "          resolve(true);" +
                            "        }, 500);" +
                            "      } else {" +
                            "        console.log('Option ' + '" + optionId + "' + ' not yet found. Retrying...');" +
                            "      }" +
                            "    }, 200); // Check every 200ms" +
                            "    " +
                            "    // Timeout for option finding to prevent infinite loop" +
                            "    setTimeout(() => {" +
                            "      if (checkOptionInterval) {" +
                            "        clearInterval(checkOptionInterval);" +
                            "        console.error('Timeout: Option ' + '" + optionId + "' + ' not found within allowed time for dropdown at position " + position + ".');" +
                            "        document.body.click();" + // Still try to close if option not found
                            "        resolve(false);" +
                            "      }" +
                            "    }, 5000); // 5 seconds timeout for option to appear" +
                            "  }, 500);" + // Wait for click to register
                            "});", targetSelect // Pass the WebElement as an argument
            );

            // Wait extra time in Java for all JS actions to complete
            Thread.sleep(2500);
            return result != null && result;

        } catch (Exception e) {
            System.err.println("❌ Error selecting by position " + position + ": " + e.getMessage());
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
                return selectDropdown(driver, selectId, optionId); // Use the 'old' selectDropdown
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Conditional dropdown '" + selectId + "' not found or not visible. Skipping selection.");
            // Element not found or not visible within the wait time, which is expected for conditional fields
        }
        return true; // Not considered an error if the dropdown doesn't exist
    }

    /**
     * Fill textarea field.
     * This method also adds a WebDriverWait for the textarea to be visible.
     *
     * @param driver The WebDriver instance.
     * @param textareaId The ID of the textarea element.
     * @param value The string value to enter.
     * @return true if the textarea was filled, false otherwise.
     */
    private static boolean fillTextarea(WebDriver driver, String textareaId, String value) {
        try {
            System.out.println("Filling textarea: " + textareaId + " with: " + value);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement textarea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(textareaId)));
            textarea.clear();
            textarea.sendKeys(value);
            System.out.println("✅ Filled textarea: " + textareaId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to fill textarea '" + textareaId + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check and click submit button if enabled.
     * This method uses JavaScript to find a submit button and click it only if it's not disabled.
     * This method also adds a WebDriverWait for the submit button to be clickable.
     *
     * @param driver The WebDriver instance.
     * @return true if the submit button was clicked, false otherwise.
     */
    private static boolean checkAndClickSubmit(WebDriver driver) {
        try {
            System.out.println("Checking for SUBMIT button...");
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.submit-button, button[type=\"submit\"], button[aria-label*=\"Submit\"], button[title*=\"Submit\"]")));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var submitBtn = arguments[0];" + // Pass WebElement as argument
                            "if (submitBtn && !submitBtn.disabled) {" +
                            "  submitBtn.click();" +
                            "  return true;" +
                            "}" +
                            "return false;", submitBtn // Pass the WebElement
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
            System.err.println("❌ Error checking/clicking submit button: " + e.getMessage());
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
     * This method also adds a WebDriverWait for the button to be clickable.
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

            WebElement targetButton = null;
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            try {
                // Try to find by ID first
                targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(identifier)));
            } catch (Exception e) {
                // If not found by ID, try to find by text content
                String xpath = "//button[contains(normalize-space(.), '" + identifier + "')] | //a[contains(normalize-space(.), '" + identifier + "')] | //cbp-button//button[contains(normalize-space(.), '" + identifier + "')]";
                if (textFallback != null) {
                    xpath += " | //button[contains(normalize-space(.), '" + textFallback + "')] | //a[contains(normalize-space(.), '" + textFallback + "')] | //cbp-button//button[contains(normalize-space(.), '" + textFallback + "')]";
                }
                targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            }

            // Click the element using JavaScript for robustness
            js.executeScript("arguments[0].click();", targetButton);

            Thread.sleep(1000); // Wait for the click action to register and page to react
            System.out.println("✅ Clicked button: " + identifier + (textFallback != null ? " (via text: " + textFallback + ")" : ""));
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error clicking button '" + identifier + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Simple button clicking (KEEP WORKING VERSION).
     * This method uses Selenium's findElement by XPath.
     * This method also adds a WebDriverWait for the button to be clickable.
     *
     * @param driver The WebDriver instance.
     * @param buttonText The text content of the button.
     * @return true if the button was clicked, false otherwise.
     */
    private static boolean clickButtonSimple(WebDriver driver, String buttonText) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement button = null;
            try {
                button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), '" + buttonText + "')]")));
            } catch (Exception e) {
                // Fallback to 'a' tag if button not found
                button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), '" + buttonText + "')]")));
            }
            button.click();
            System.out.println("✅ Clicked simple button: " + buttonText);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to click simple button '" + buttonText + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill input field (KEEP WORKING VERSION).
     * This method clears an input field and then sends keys to it.
     * This method also adds a WebDriverWait for the input field to be visible.
     *
     * @param driver The WebDriver instance.
     * @param inputId The ID of the input element.
     * @param value The string value to enter.
     * @return true if the input was filled, false otherwise.
     */
    private static boolean fillInput(WebDriver driver, String inputId, String value) {
        try {
            System.out.println("Filling input: " + inputId + " with: " + value);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(inputId)));
            input.clear();
            input.sendKeys(value);
            System.out.println("✅ Filled " + inputId + ": " + value);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to fill " + inputId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Wait for element visibility and send keys (KEEP WORKING VERSION).
     * This method waits for an element to be visible before clearing and sending keys.
     * This method also adds a WebDriverWait for the element to be visible.
     *
     * @param driver The WebDriver instance.
     * @param by The By locator strategy for the element.
     * @param text The text to send to the element.
     * @return true if keys were sent, false otherwise.
     */
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
            e.printStackTrace();
            return false;
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
            return selectDropdown(driver, selectId, optionId);
        } catch (Exception e) {
            System.err.println("❌ Error calculating or selecting height. Falling back to random height. " + e.getMessage());
            e.printStackTrace();
            // Fallback to a random height if parsing fails or option is not found
            int randomOption = 8 + random.nextInt(60); // Assuming 60 options cover a reasonable range
            return selectDropdown(driver, selectId, "mat-option-" + randomOption);
        }
    }

    /**
     * Fill SSN input field, handling potential input masks or special behaviors.
     * This uses JavaScript to directly set the value and dispatch input/change events.
     * This method also adds a WebDriverWait for the input field to be visible.
     *
     * @param driver The WebDriver instance.
     * @param ssn The SSN string to enter.
     * @return true if SSN was filled, false otherwise.
     */
    private static boolean fillSSNInput(WebDriver driver, String ssn) {
        try {
            System.out.println("Filling SSN input with: " + ssn);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement ssnInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[mask=\"000-00-0000\"]"))); // Use CSS selector for mask

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var input = arguments[0];" +
                            "if (input) {" +
                            "  input.value = '" + ssn + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;", ssnInput
            );

            if (result != null && result) {
                System.out.println("✅ SSN filled: " + ssn);
                return true;
            } else {
                System.out.println("❌ SSN input element not found or script failed.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling SSN: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill date input field, specifically designed for date picker inputs.
     * It uses JavaScript to directly set the value and trigger events, which is often
     * more reliable for date inputs that might have complex UI components.
     * This method also adds a WebDriverWait for the input field to be visible.
     *
     * @param driver The WebDriver instance.
     * @param inputId The ID of the date input field.
     * @param date The date string in "MM/dd/yyyy" format.
     * @return true if the date was filled, false otherwise.
     */
    private static boolean fillDateInput(WebDriver driver, String inputId, String date) {
        try {
            System.out.println("Filling date input (ID: " + inputId + ") with: " + date);
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);
            WebElement inputElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(inputId)));

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var input = arguments[0];" + // Use arguments[0] to pass the WebElement directly
                            "if (input) {" +
                            "  input.value = '" + date + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;", inputElement // Pass the WebElement as an argument
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
            System.err.println("❌ Error filling date input " + inputId + ": " + e.getMessage());
            e.printStackTrace();
            // Fallback to standard fillInput
            return fillInput(driver, inputId, date);
        }
    }

    /**
     * Fill input by position (for dynamically added fields).
     * This finds the target input based on its position from the end of all visible,
     * non-readonly/non-disabled input elements.
     * This method also adds a WebDriverWait for the input field to be visible.
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
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            WebElement targetInput = wait.until(d -> {
                List<WebElement> inputs = (List<WebElement>) js.executeScript(
                        "return Array.from(document.querySelectorAll('input.mat-input-element:not([readonly]):not([disabled])')).filter(i => i.offsetWidth > 0 || i.offsetHeight > 0);"
                );
                int targetIndex = inputs.size() - 1 - position;
                if (targetIndex >= 0 && targetIndex < inputs.size()) {
                    return inputs.get(targetIndex);
                }
                return null;
            });

            if (targetInput == null) {
                System.out.println("❌ Failed to find input at position " + position);
                return false;
            }

            Boolean result = (Boolean) js.executeScript(
                    "var input = arguments[0];" + // Use arguments[0] to pass the WebElement directly
                            "if (input) {" +
                            "  input.value = '" + value + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;", targetInput // Pass the WebElement as an argument
            );
            if (result != null && result) {
                System.out.println("✅ Filled input at position " + position + ": " + value);
                return true;
            } else {
                System.out.println("❌ Failed to fill input at position " + position);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling input by position " + position + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill date input by position (for dynamically added date fields).
     * Similar to fillInputByPosition but specifically targets date inputs with a mask.
     * This method also adds a WebDriverWait for the input field to be visible.
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
            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT_TIME);

            WebElement targetInput = wait.until(d -> {
                List<WebElement> inputs = (List<WebElement>) js.executeScript(
                        "return Array.from(document.querySelectorAll('input[mask=\"00/00/0000\"]')).filter(i => i.offsetWidth > 0 || i.offsetHeight > 0);"
                );
                int targetIndex = inputs.size() - 1 - position;
                if (targetIndex >= 0 && targetIndex < inputs.size()) {
                    return inputs.get(targetIndex);
                }
                return null;
            });

            if (targetInput == null) {
                System.out.println("❌ Failed to find date input at position " + position);
                return false;
            }

            Boolean result = (Boolean) js.executeScript(
                    "var input = arguments[0];" + // Use arguments[0] to pass the WebElement directly
                            "if (input) {" +
                            "  input.value = '" + date + "';" +
                            "  input.dispatchEvent(new Event('input', {bubbles: true}));" +
                            "  input.dispatchEvent(new Event('change', {bubbles: true}));" +
                            "  return true;" +
                            "}" +
                            "return false;", targetInput // Pass the WebElement as an argument
            );
            if (result != null && result) {
                System.out.println("✅ Filled date input at position " + position + ": " + date);
                return true;
            } else {
                System.out.println("❌ Failed to fill date input at position " + position);
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error filling date input by position " + position + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
