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
     * Fill second page - ROBUST approach for interception issues
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for page load
            Thread.sleep(10000);

            // === MAIN DROPDOWNS (WORKING) ===
            System.out.println("1. First dropdown (OB - OUTBOUND SUBJECT)");
            selectDropdown(driver, "mat-select-4", "mat-option-68");

            System.out.println("2. Second dropdown (AB - AG/BIO COUNTERMEASURES)");
            selectDropdown(driver, "mat-select-10", "mat-option-549");

            System.out.println("3. Third dropdown (0 - NO NOTIFICATION)");
            selectDropdown(driver, "mat-select-6", "mat-option-238");

            System.out.println("4. Fourth dropdown (Multiple)");
            int randomOption = 253 + random.nextInt(6);
            selectDropdown(driver, "mat-select-12", "mat-option-" + randomOption);

            System.out.println("5. Fifth dropdown (0 - NOT ON PRIMARY)");
            selectDropdown(driver, "mat-select-8", "mat-option-242");

            // === FORM FIELDS (WORKING) ===
            System.out.println("6. Filling remarks");
            fillInput(driver, "mat-input-1", "Automated test entry - " + System.currentTimeMillis());

            System.out.println("7. Y/N dropdown");
            selectDropdown(driver, "mat-select-0", "mat-option-2");

            System.out.println("8. Height dropdown");
            int randomHeight = 8 + random.nextInt(20);
            selectDropdown(driver, "mat-select-2", "mat-option-" + randomHeight);

            System.out.println("9. Weight field");
            String weight = String.valueOf(120 + random.nextInt(131));
            fillInput(driver, "mat-input-0", weight);

            // === ADD SECTIONS (FIX INTERCEPTION) ===
            System.out.println("10. Adding Sex");
            addFieldWithDropdown(driver, "Add Sex", random.nextBoolean() ? "630" : "631");

            System.out.println("11. Adding Race");
            int raceOption = 594 + random.nextInt(6);
            addFieldWithDropdown(driver, "Add Race", String.valueOf(raceOption));

            System.out.println("12. Adding Eye Color");
            int eyeOption = 600 + random.nextInt(12);
            addFieldWithDropdown(driver, "Add Eye Color", String.valueOf(eyeOption));

            System.out.println("13. Adding Hair Color");
            int hairOption = 612 + random.nextInt(15);
            addFieldWithDropdown(driver, "Add Hair Color", String.valueOf(hairOption));

            System.out.println("14. Adding Name");
            if (clickButtonRobust(driver, "Add Name")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-2", data.getLastName());
                fillInput(driver, "mat-input-3", data.getFirstName());
            }

            System.out.println("15. Adding DOB");
            if (clickButtonRobust(driver, "Add DOB")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-11", data.getDob());
            }

            System.out.println("16. Adding Citizenship");
            addFieldWithDropdown(driver, "Add Citizenship", "1260");

            System.out.println("17. Adding Passport");
            if (clickButtonRobust(driver, "Add Passport")) {
                Thread.sleep(4000);

                // Passport has multiple fields - handle them step by step
                System.out.println("   17a. Passport Type");
                selectNewestDropdown(driver, "1518"); // P - Regular
                Thread.sleep(2000);

                System.out.println("   17b. Passport Number");
                fillInput(driver, "mat-input-19", data.getPassportNumber());
                Thread.sleep(1000);

                System.out.println("   17c. Passport Country");
                selectNewestDropdown(driver, "1520"); // USA
                Thread.sleep(2000);

                System.out.println("   17d. Passport Issue Date");
                fillInput(driver, "mat-input-20", data.getPassportIssueDate());
                Thread.sleep(1000);

                System.out.println("   17e. Passport Expiry Date");
                fillInput(driver, "mat-input-21", data.getPassportExpiryDate());
            }

            System.out.println("18. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-22", data.getaNumber());
            }

            System.out.println("19. Adding Driver License");
            if (clickButtonRobust(driver, "Driver")) { // Simplified to avoid apostrophe issues
                Thread.sleep(4000);

                System.out.println("   19a. License Number");
                fillInput(driver, "mat-input-23", data.getDriverLicense());
                Thread.sleep(2000);

                System.out.println("   19b. License State");
                int stateOption = 1774 + random.nextInt(62); // Random US state
                selectNewestDropdown(driver, String.valueOf(stateOption));
            }

            System.out.println("20. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(3000);
                // Find newest input for SSN
                fillNewestInput(driver, data.getSsn());
            }

            // Additional fields that might be missing
            System.out.println("21. Checking for additional fields...");
            Thread.sleep(2000);

            // Check if there are any other "Add" buttons we missed
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "console.log('=== REMAINING ADD BUTTONS ==='); " +
                            "var buttons = document.querySelectorAll('button'); " +
                            "for (var i = 0; i < buttons.length; i++) { " +
                            "  var text = buttons[i].textContent.trim(); " +
                            "  if (text.includes('Add') && !text.includes('Sex') && !text.includes('Race') && " +
                            "      !text.includes('Eye') && !text.includes('Hair') && !text.includes('Name') && " +
                            "      !text.includes('DOB') && !text.includes('Citizenship') && !text.includes('Passport') && " +
                            "      !text.includes('A#') && !text.includes('Driver') && !text.includes('SSN')) { " +
                            "    console.log('Unused Add button: ' + text); " +
                            "  } " +
                            "}"
            );

            System.out.println("Second page completed successfully!");
            return true;

        } catch (Exception e) {
            System.out.println("Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ROBUST dropdown selection - handles interception and ensures closing
     */
    private static boolean selectDropdown(WebDriver driver, String selectId, String optionId) {
        try {
            System.out.println("🎯 Selecting " + selectId + " → " + optionId);

            // Method 1: Try standard approach with forced closing
            try {
                WebElement matSelect = driver.findElement(By.id(selectId));
                matSelect.click();
                Thread.sleep(1500);

                WebElement option = driver.findElement(By.id(optionId));
                option.click();
                Thread.sleep(500);

                // Force close dropdown - multiple methods
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("document.body.click();");
                Thread.sleep(300);
                js.executeScript("document.querySelector('body').click();");
                Thread.sleep(300);

                // Press ESC key as backup
                js.executeScript(
                        "var event = new KeyboardEvent('keydown', {key: 'Escape', keyCode: 27}); " +
                                "document.dispatchEvent(event);"
                );
                Thread.sleep(500);

                System.out.println("✅ Selected and closed " + selectId + " → " + optionId);
                return true;

            } catch (Exception e) {
                System.out.println("Standard approach failed: " + e.getMessage());
            }

            // Method 2: Full JavaScript approach with guaranteed closing
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var select = document.getElementById('" + selectId + "'); " +
                                "if (select) { " +
                                "  console.log('Clicking select: " + selectId + "'); " +
                                "  select.click(); " +
                                "  " +
                                "  setTimeout(function() { " +
                                "    var option = document.getElementById('" + optionId + "'); " +
                                "    if (option) { " +
                                "      console.log('Clicking option: " + optionId + "'); " +
                                "      option.click(); " +
                                "      " +
                                "      // Multiple close attempts " +
                                "      setTimeout(function() { " +
                                "        document.body.click(); " +
                                "        document.querySelector('body').click(); " +
                                "        // Close any open overlays " +
                                "        var overlays = document.querySelectorAll('.cdk-overlay-pane'); " +
                                "        for (var i = 0; i < overlays.length; i++) { " +
                                "          overlays[i].style.display = 'none'; " +
                                "        } " +
                                "        console.log('Dropdown closed for " + selectId + "'); " +
                                "      }, 500); " +
                                "    } else { " +
                                "      console.log('Option " + optionId + " not found'); " +
                                "    } " +
                                "  }, 1500); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(3000); // Wait for all operations to complete
                    System.out.println("✅ Selected and closed " + selectId + " → " + optionId + " (JS)");
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
     * Add field with dropdown - FIXED version with proper option clicking
     */
    private static boolean addFieldWithDropdown(WebDriver driver, String buttonText, String optionNumber) {
        try {
            System.out.println("🎯 Adding " + buttonText + " with option " + optionNumber);

            if (clickButtonRobust(driver, buttonText)) {
                Thread.sleep(4000); // Longer wait for new dropdown to appear

                // More robust JavaScript approach
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "console.log('Looking for newest dropdown after adding " + buttonText + "'); " +
                                "var selects = document.querySelectorAll('mat-select'); " +
                                "console.log('Found ' + selects.length + ' total mat-select elements'); " +
                                "if (selects.length > 0) { " +
                                "  var newest = selects[selects.length - 1]; " +
                                "  console.log('Clicking newest dropdown'); " +
                                "  newest.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "  " +
                                "  setTimeout(function() { " +
                                "    newest.click(); " +
                                "    console.log('Dropdown clicked, waiting for options'); " +
                                "    " +
                                "    setTimeout(function() { " +
                                "      var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                                "      if (option) { " +
                                "        console.log('Found option mat-option-" + optionNumber + ", clicking it'); " +
                                "        option.click(); " +
                                "        " +
                                "        setTimeout(function() { " +
                                "          console.log('Closing dropdown'); " +
                                "          document.body.click(); " +
                                "          // Force close overlays " +
                                "          var overlays = document.querySelectorAll('.cdk-overlay-pane'); " +
                                "          for (var i = 0; i < overlays.length; i++) { " +
                                "            overlays[i].style.display = 'none'; " +
                                "          } " +
                                "          console.log('Completed " + buttonText + " selection'); " +
                                "        }, 500); " +
                                "      } else { " +
                                "        console.log('Option mat-option-" + optionNumber + " not found'); " +
                                "        // List available options for debugging " +
                                "        var options = document.querySelectorAll('mat-option'); " +
                                "        console.log('Available options:'); " +
                                "        for (var i = 0; i < Math.min(options.length, 10); i++) { " +
                                "          console.log('  ' + options[i].id + ': ' + options[i].textContent); " +
                                "        } " +
                                "      } " +
                                "    }, 2000); " +
                                "  }, 1000); " +
                                "  return true; " +
                                "} " +
                                "console.log('No dropdowns found'); " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(4000); // Wait for all operations to complete
                    System.out.println("✅ Added " + buttonText + " with option " + optionNumber);
                    return true;
                } else {
                    System.out.println("❌ JavaScript returned false for " + buttonText);
                }
            } else {
                System.out.println("❌ Failed to click button: " + buttonText);
            }

            System.out.println("❌ Failed to add " + buttonText);
            return false;

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
     * Select option in the newest dropdown (for multi-field sections like Passport)
     */
    private static boolean selectNewestDropdown(WebDriver driver, String optionNumber) {
        try {
            System.out.println("🎯 Selecting newest dropdown option: " + optionNumber);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean result = (Boolean) js.executeScript(
                    "var selects = document.querySelectorAll('mat-select'); " +
                            "if (selects.length > 0) { " +
                            "  var newest = selects[selects.length - 1]; " +
                            "  console.log('Clicking newest dropdown for option " + optionNumber + "'); " +
                            "  newest.click(); " +
                            "  " +
                            "  setTimeout(function() { " +
                            "    var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                            "    if (option) { " +
                            "      console.log('Clicking option mat-option-" + optionNumber + "'); " +
                            "      option.click(); " +
                            "      " +
                            "      setTimeout(function() { " +
                            "        document.body.click(); " +
                            "        var overlays = document.querySelectorAll('.cdk-overlay-pane'); " +
                            "        for (var i = 0; i < overlays.length; i++) { " +
                            "          overlays[i].style.display = 'none'; " +
                            "        } " +
                            "        console.log('Closed dropdown after selecting " + optionNumber + "'); " +
                            "      }, 500); " +
                            "    } else { " +
                            "      console.log('Option " + optionNumber + " not found'); " +
                            "    } " +
                            "  }, 1500); " +
                            "  return true; " +
                            "} " +
                            "console.log('No dropdowns found'); " +
                            "return false;"
            );

            if (result != null && result) {
                Thread.sleep(3000);
                System.out.println("✅ Selected newest dropdown option: " + optionNumber);
                return true;
            }

            System.out.println("❌ Failed to select newest dropdown option: " + optionNumber);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error selecting newest dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill the newest input field (for dynamic fields like SSN)
     */
    private static boolean fillNewestInput(WebDriver driver, String value) {
        try {
            System.out.println("🎯 Filling newest input with: " + value);

            List<WebElement> allInputs = driver.findElements(By.xpath("//input[contains(@class, 'mat-input-element')]"));
            if (!allInputs.isEmpty()) {
                WebElement newestInput = allInputs.get(allInputs.size() - 1);

                // Highlight the input
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border = '3px solid red';", newestInput);
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", newestInput);
                Thread.sleep(1000);

                newestInput.clear();
                newestInput.sendKeys(value);

                // Remove highlight
                js.executeScript("arguments[0].style.border = '';", newestInput);

                System.out.println("✅ Filled newest input: " + value);
                return true;
            } else {
                System.out.println("❌ No input fields found");
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Error filling newest input: " + e.getMessage());
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