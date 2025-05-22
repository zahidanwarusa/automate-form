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
 * Clean FormFiller - Keep what works, fix interception issues
 */
public class FormFiller {
    private static final Random random = new Random();

    /**
     * Fill first page (KEEP EXACT WORKING CODE)
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out first page...");

            // Fill basic fields (WORKING)
            System.out.println("Filling last name: " + data.getLastName());
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            System.out.println("Filling first name: " + data.getFirstName());
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            System.out.println("Filling DOB: " + data.getDob());
            waitAndSendKeys(driver, By.id("dob"), data.getDob());

            // Click search (WORKING)
            System.out.println("Clicking SEARCH button...");
            if (!clickButtonSimple(driver, "Search")) {
                System.out.println("Search button not found, continuing...");
            }

            // Wait for search results (WORKING)
            System.out.println("Waiting for search results to load...");
            Thread.sleep(8000);

            // Handle tab switching (WORKING)
            String originalWindow = driver.getWindowHandle();
            System.out.println("Original window handle: " + originalWindow);

            System.out.println("Looking for Create TECS Lookout button...");
            if (!clickButtonSimple(driver, "Create TECS Lookout")) {
                System.out.println("Could not find Create TECS Lookout button");
                return false;
            }

            // Switch tabs if needed (WORKING)
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
     * Fill second page - COMPREHENSIVE coverage with all fields
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("\n=== FILLING SECOND PAGE - COMPREHENSIVE ===");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for page load
            Thread.sleep(10000);

            // Debug page state
            debugPageState(driver);

            // === MAIN DROPDOWNS (1-8) ===
            System.out.println("\n--- MAIN DROPDOWNS ---");

            System.out.println("1. First dropdown (OB - OUTBOUND SUBJECT)");
            selectDropdown(driver, "mat-select-4", "mat-option-68");
            Thread.sleep(1000);

            System.out.println("2. Second dropdown (AB - AG/BIO COUNTERMEASURES)");
            selectDropdown(driver, "mat-select-10", "mat-option-549");
            Thread.sleep(1000);

            System.out.println("3. Third dropdown (0 - NO NOTIFICATION)");
            selectDropdown(driver, "mat-select-6", "mat-option-238");
            Thread.sleep(1000);

            System.out.println("4. Fourth dropdown (Multiple) - SPECIAL HANDLING");
            int randomOption = 253 + random.nextInt(6);
            selectDropdown(driver, "mat-select-12", "mat-option-" + randomOption);
            Thread.sleep(2000); // Extra wait for multiple select

            System.out.println("5. Fifth dropdown (0 - NOT ON PRIMARY)");
            selectDropdown(driver, "mat-select-8", "mat-option-242");
            Thread.sleep(1000);

            System.out.println("6. Y/N dropdown");
            selectDropdown(driver, "mat-select-0", "mat-option-2");
            Thread.sleep(1000);

            System.out.println("7. Height dropdown");
            int randomHeight = 8 + random.nextInt(20);
            selectDropdown(driver, "mat-select-2", "mat-option-" + randomHeight);
            Thread.sleep(1000);

            // === FORM FIELDS ===
            System.out.println("\n--- FORM FIELDS ---");

            System.out.println("8. Filling remarks");
            fillInput(driver, "mat-input-1", "Automated test entry - " + System.currentTimeMillis());

            System.out.println("9. Weight field");
            String weight = String.valueOf(120 + random.nextInt(131));
            fillInput(driver, "mat-input-0", weight);

            // === DYNAMIC ADD SECTIONS ===
            System.out.println("\n--- DYNAMIC ADD SECTIONS ---");

            System.out.println("10. Adding Sex");
            String sexOption = random.nextBoolean() ? "630" : "631"; // F or M
            addFieldWithDropdown(driver, "Add Sex", sexOption);
            Thread.sleep(2000);

            System.out.println("11. Adding Race");
            int raceOption = 594 + random.nextInt(6); // 594-599
            addFieldWithDropdown(driver, "Add Race", String.valueOf(raceOption));
            Thread.sleep(2000);

            System.out.println("12. Adding Eye Color");
            int eyeOption = 600 + random.nextInt(12); // 600-611
            addFieldWithDropdown(driver, "Add Eye Color", String.valueOf(eyeOption));
            Thread.sleep(2000);

            System.out.println("13. Adding Hair Color");
            int hairOption = 612 + random.nextInt(15); // 612-626
            addFieldWithDropdown(driver, "Add Hair Color", String.valueOf(hairOption));
            Thread.sleep(2000);

            // === PERSONAL INFO SECTIONS ===
            System.out.println("\n--- PERSONAL INFO SECTIONS ---");

            System.out.println("14. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-2", data.getLastName());
                fillInput(driver, "mat-input-3", data.getFirstName());
            }
            Thread.sleep(1000);

            System.out.println("15. Adding DOB");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-11", data.getDob());
            }
            Thread.sleep(1000);

            System.out.println("16. Adding Citizenship");
            addFieldWithDropdown(driver, "Add Citizenship", "1260"); // USA
            Thread.sleep(2000);

            // === DOCUMENT SECTIONS ===
            System.out.println("\n--- DOCUMENT SECTIONS ---");

            System.out.println("17. Adding Passport (Comprehensive)");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(3000);

                // Passport type dropdown (first new dropdown)
                System.out.println("17a. Selecting passport type");
                addLatestDropdownOption(driver, "1518"); // P - Regular
                Thread.sleep(1000);

                // Passport number
                System.out.println("17b. Filling passport number");
                fillInput(driver, "mat-input-19", data.getPassportNumber());
                Thread.sleep(1000);

                // Passport country dropdown (second new dropdown)
                System.out.println("17c. Selecting passport country");
                addLatestDropdownOption(driver, "1520"); // USA
                Thread.sleep(1000);

                // Passport dates
                System.out.println("17d. Filling passport dates");
                fillInput(driver, "mat-input-20", data.getPassportIssueDate());
                fillInput(driver, "mat-input-21", data.getPassportExpiryDate());
            }
            Thread.sleep(2000);

            System.out.println("18. Adding A Number");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-22", data.getaNumber());
            }
            Thread.sleep(1000);

            System.out.println("19. Adding Drivers License");
            if (clickButtonRobust(driver, "Drivers License")) { // Avoid apostrophe
                Thread.sleep(3000);

                // License number
                System.out.println("19a. Filling license number");
                fillInput(driver, "mat-input-23", data.getDriverLicense());
                Thread.sleep(1000);

                // State selection
                System.out.println("19b. Selecting state");
                int stateOption = 1774 + random.nextInt(62); // Random US state
                addLatestDropdownOption(driver, String.valueOf(stateOption));
            }
            Thread.sleep(2000);

            System.out.println("20. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(3000);
                // Find newest input field
                List<WebElement> allInputs = driver.findElements(By.xpath("//input[contains(@class, 'mat-input-element')]"));
                if (!allInputs.isEmpty()) {
                    WebElement ssnInput = allInputs.get(allInputs.size() - 1);
                    ssnInput.clear();
                    ssnInput.sendKeys(data.getSsn());
                    System.out.println("✅ SSN filled: " + data.getSsn());
                } else {
                    System.out.println("❌ SSN input field not found");
                }
            }

            System.out.println("\n✅ SECOND PAGE COMPLETED SUCCESSFULLY!");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Debug page state - shows what elements are available
     */
    private static void debugPageState(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            System.out.println("\n=== PAGE STATE DEBUG ===");

            Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
            Object inputCount = js.executeScript("return document.querySelectorAll('input').length;");
            Object buttonCount = js.executeScript("return document.querySelectorAll('button').length;");

            System.out.println("📊 Mat-select elements: " + matSelectCount);
            System.out.println("📊 Input elements: " + inputCount);
            System.out.println("📊 Button elements: " + buttonCount);

            // List first few dropdowns and their IDs
            js.executeScript(
                    "console.log('=== AVAILABLE DROPDOWNS ==='); " +
                            "var selects = document.querySelectorAll('mat-select'); " +
                            "for (var i = 0; i < Math.min(selects.length, 8); i++) { " +
                            "  console.log('Dropdown ' + i + ': ID=' + selects[i].id + ', classes=' + selects[i].className.substring(0,60)); " +
                            "}"
            );

            System.out.println("Check browser console for detailed element list");
            System.out.println("========================\n");

        } catch (Exception e) {
            System.out.println("Error debugging page state: " + e.getMessage());
        }
    }

    /**
     * Helper method for selecting option in the most recently added dropdown
     */
    private static boolean addLatestDropdownOption(WebDriver driver, String optionNumber) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select'); " +
                            "if (selects.length > 0) { " +
                            "  var newest = selects[selects.length - 1]; " +
                            "  newest.style.border = '3px solid orange'; " +
                            "  newest.click(); " +
                            "  setTimeout(function() { " +
                            "    var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                            "    if (option) { " +
                            "      option.click(); " +
                            "      document.body.click(); " +
                            "      newest.style.border = ''; " +
                            "    } " +
                            "  }, 1500); " +
                            "  return true; " +
                            "} " +
                            "return false;"
            );

            if (result != null && result) {
                Thread.sleep(2500);
                System.out.println("✅ Selected latest dropdown option: " + optionNumber);
                return true;
            }

            return false;
        } catch (Exception e) {
            System.out.println("❌ Error selecting latest dropdown option: " + e.getMessage());
            return false;
        }
    }

    /**
     * ROBUST dropdown selection - handles interception
     */
    private static boolean selectDropdown(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Selecting " + selectId + " → " + optionId);

            // Special handling for mat-select-12 (Multiple dropdown)
            boolean isMultipleSelect = selectId.equals("mat-select-12");

            // Method 1: Try standard approach
            try {
                WebElement matSelect = driver.findElement(By.id(selectId));

                // Highlight for debugging
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border = '3px solid red';", matSelect);

                matSelect.click();
                Thread.sleep(1500);

                WebElement option = driver.findElement(By.id(optionId));
                option.click();
                Thread.sleep(500);

                // Special close logic for multiple select
                if (isMultipleSelect) {
                    // For multiple select, click outside to close
                    js.executeScript("document.querySelector('body').click();");
                    System.out.println("🔄 Closed multiple select dropdown");
                } else {
                    // For regular select, clicking option should close it automatically
                    // But add safety click if needed
                    js.executeScript("document.body.click();");
                }

                // Remove highlight
                js.executeScript("arguments[0].style.border = '';", matSelect);
                Thread.sleep(500);

                System.out.println("✅ Selected " + selectId + " → " + optionId);
                return true;

            } catch (Exception e) {
                System.out.println("Standard approach failed: " + e.getMessage());
            }

            // Method 2: JavaScript approach for interception
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;

                String closeScript = isMultipleSelect ?
                        "setTimeout(function() { document.body.click(); }, 1000);" :
                        "setTimeout(function() { document.body.click(); }, 500);";

                Boolean result = (Boolean) js.executeScript(
                        "var select = document.getElementById('" + selectId + "'); " +
                                "if (select) { " +
                                "  select.style.border = '3px solid red'; " +
                                "  select.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "  select.click(); " +
                                "  setTimeout(function() { " +
                                "    var option = document.getElementById('" + optionId + "'); " +
                                "    if (option) { " +
                                "      option.click(); " +
                                "      console.log('Clicked option: " + optionId + "'); " +
                                "      " + closeScript + " " +
                                "      setTimeout(function() { select.style.border = ''; }, 2000); " +
                                "    } else { " +
                                "      console.log('Option not found: " + optionId + "'); " +
                                "    } " +
                                "  }, 1500); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(3000); // Wait for all animations
                    System.out.println("✅ Selected " + selectId + " → " + optionId + " (JS)");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript approach failed: " + e.getMessage());
            }

            System.out.println("❌ Failed to select " + selectId);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error selecting dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add field with dropdown - ENHANCED with better debugging and timing
     */
    private static boolean addFieldWithDropdown(WebDriver driver, String buttonText, String optionNumber) {
        try {
            System.out.println("🎯 Adding " + buttonText + " with option " + optionNumber);

            if (clickButtonRobust(driver, buttonText)) {
                Thread.sleep(3000); // Wait for new dropdown to appear

                // Debug: Check how many dropdowns exist
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Object dropdownCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
                System.out.println("📊 Found " + dropdownCount + " total dropdowns after adding " + buttonText);

                // Enhanced JavaScript approach with better debugging
                Boolean result = (Boolean) js.executeScript(
                        "console.log('=== DROPDOWN SELECTION DEBUG ==='); " +
                                "var selects = document.querySelectorAll('mat-select'); " +
                                "console.log('Total selects found: ' + selects.length); " +

                                "if (selects.length > 0) { " +
                                "  var newest = selects[selects.length - 1]; " +
                                "  console.log('Newest select ID: ' + newest.id); " +
                                "  console.log('Newest select classes: ' + newest.className); " +

                                "  // Highlight the dropdown " +
                                "  newest.style.border = '3px solid blue'; " +
                                "  newest.scrollIntoView({behavior: 'smooth', block: 'center'}); " +

                                "  // Click the dropdown " +
                                "  newest.click(); " +
                                "  console.log('Clicked newest dropdown'); " +

                                "  setTimeout(function() { " +
                                "    var optionId = 'mat-option-" + optionNumber + "'; " +
                                "    var option = document.getElementById(optionId); " +
                                "    console.log('Looking for option: ' + optionId); " +
                                "    console.log('Option found: ' + (option ? 'YES' : 'NO')); " +

                                "    if (option) { " +
                                "      option.style.border = '3px solid green'; " +
                                "      option.click(); " +
                                "      console.log('✅ Clicked option: ' + optionId); " +

                                "      // Close dropdown " +
                                "      setTimeout(function() { " +
                                "        document.body.click(); " +
                                "        newest.style.border = ''; " +
                                "        if (option) option.style.border = ''; " +
                                "        console.log('Closed dropdown for " + buttonText + "'); " +
                                "      }, 500); " +
                                "    } else { " +
                                "      console.log('❌ Option " + optionNumber + " not found!'); " +
                                "      // List available options " +
                                "      var allOptions = document.querySelectorAll('mat-option'); " +
                                "      console.log('Available options:'); " +
                                "      for (var i = 0; i < Math.min(allOptions.length, 10); i++) { " +
                                "        console.log('  ' + allOptions[i].id + ': ' + allOptions[i].textContent.trim().substring(0, 30)); " +
                                "      } " +
                                "      newest.style.border = ''; " +
                                "    } " +
                                "  }, 2000); " +

                                "  return true; " +
                                "} else { " +
                                "  console.log('❌ No dropdowns found!'); " +
                                "  return false; " +
                                "}"
                );

                if (result != null && result) {
                    Thread.sleep(4000); // Wait for all animations and debugging
                    System.out.println("✅ Added " + buttonText + " with option " + optionNumber);
                    return true;
                } else {
                    System.out.println("❌ JavaScript execution failed for " + buttonText);
                    return false;
                }
            } else {
                System.out.println("❌ Failed to click button: " + buttonText);
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Error adding " + buttonText + ": " + e.getMessage());
            return false;
        }
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