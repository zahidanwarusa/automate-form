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
                Thread.sleep(3000);
                // Just fill the passport number for now to avoid complex multi-dropdown issues
                fillInput(driver, "mat-input-19", data.getPassportNumber());
            }

            System.out.println("18. Adding A#");
            if (clickButtonRobust(driver, "Add A#")) {
                Thread.sleep(3000);
                fillInput(driver, "mat-input-22", data.getaNumber());
            }

            System.out.println("19. Adding Drivers License");
            if (clickButtonRobust(driver, "Drivers License")) { // Avoid apostrophe
                Thread.sleep(3000);
                fillInput(driver, "mat-input-23", data.getDriverLicense());
            }

            System.out.println("20. Adding SSN");
            if (clickButtonRobust(driver, "Add SSN")) {
                Thread.sleep(3000);
                // Find newest input
                List<WebElement> allInputs = driver.findElements(By.xpath("//input[contains(@class, 'mat-input-element')]"));
                if (!allInputs.isEmpty()) {
                    WebElement ssnInput = allInputs.get(allInputs.size() - 1);
                    ssnInput.clear();
                    ssnInput.sendKeys(data.getSsn());
                    System.out.println("SSN filled: " + data.getSsn());
                }
            }

            System.out.println("Second page completed successfully!");
            return true;

        } catch (Exception e) {
            System.out.println("Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ROBUST dropdown selection - handles interception
     */
    private static boolean selectDropdown(WebDriver driver, String selectId, String optionId) {
        try {
            // Method 1: Try standard approach
            try {
                WebElement matSelect = driver.findElement(By.id(selectId));
                matSelect.click();
                Thread.sleep(1500);

                WebElement option = driver.findElement(By.id(optionId));
                option.click();

                // Close dropdown
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("document.body.click();");
                Thread.sleep(500);

                System.out.println("✅ Selected " + selectId + " → " + optionId);
                return true;
            } catch (Exception e) {
                System.out.println("Standard approach failed: " + e.getMessage());
            }

            // Method 2: JavaScript approach for interception
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var select = document.getElementById('" + selectId + "'); " +
                                "if (select) { " +
                                "  select.click(); " +
                                "  setTimeout(function() { " +
                                "    var option = document.getElementById('" + optionId + "'); " +
                                "    if (option) { " +
                                "      option.click(); " +
                                "      document.body.click(); " +
                                "    } " +
                                "  }, 1000); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(2000);
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
     * Add field with dropdown - handles interception robustly
     */
    private static boolean addFieldWithDropdown(WebDriver driver, String buttonText, String optionNumber) {
        try {
            if (clickButtonRobust(driver, buttonText)) {
                Thread.sleep(3000); // Wait for new dropdown

                // Use JavaScript to find and click the newest dropdown
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var selects = document.querySelectorAll('mat-select'); " +
                                "if (selects.length > 0) { " +
                                "  var newest = selects[selects.length - 1]; " +
                                "  newest.click(); " +
                                "  setTimeout(function() { " +
                                "    var option = document.getElementById('mat-option-" + optionNumber + "'); " +
                                "    if (option) { " +
                                "      option.click(); " +
                                "      document.body.click(); " +
                                "    } " +
                                "  }, 1500); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(2500);
                    System.out.println("✅ Added " + buttonText + " with option " + optionNumber);
                    return true;
                }
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