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
 * Simplified FormFiller with element highlighting and better debugging
 */
public class FormFiller {
    private static final Random random = new Random();

    /**
     * Fill out the first page of the form
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("=== FILLING FIRST PAGE ===");

            // Fill in last name
            System.out.println("1. Filling last name: " + data.getLastName());
            if (!fillInput(driver, "lastName", data.getLastName())) {
                System.out.println("❌ Failed to fill last name");
                return false;
            }

            // Fill in first name
            System.out.println("2. Filling first name: " + data.getFirstName());
            if (!fillInput(driver, "firstName", data.getFirstName())) {
                System.out.println("❌ Failed to fill first name");
                return false;
            }

            // Fill in DOB
            System.out.println("3. Filling DOB: " + data.getDob());
            if (!fillInput(driver, "dob", data.getDob())) {
                System.out.println("❌ Failed to fill DOB");
                return false;
            }

            // Click the SEARCH button
            System.out.println("4. Clicking SEARCH button...");
            if (!clickButton(driver, "Search")) {
                System.out.println("❌ Failed to click search button, but continuing...");
            }

            // Wait for search results
            System.out.println("5. Waiting for search results...");
            Thread.sleep(8000);

            // Store window handle before clicking Create TECS Lookout
            String originalWindow = driver.getWindowHandle();
            System.out.println("Original window: " + originalWindow);

            // Click Create TECS Lookout button
            System.out.println("6. Clicking Create TECS Lookout button...");
            if (!clickButton(driver, "Create TECS Lookout")) {
                System.out.println("❌ Failed to click Create TECS Lookout button");
                return false;
            }

            // Wait and check for new tab
            Thread.sleep(5000);
            if (driver.getWindowHandles().size() > 1) {
                System.out.println("✅ New tab detected! Switching...");
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        System.out.println("Switched to new tab: " + windowHandle);
                        break;
                    }
                }
                Thread.sleep(3000);
                System.out.println("New tab URL: " + driver.getCurrentUrl());
            } else {
                System.out.println("✅ Same tab navigation");
                System.out.println("Current URL: " + driver.getCurrentUrl());
            }

            System.out.println("✅ FIRST PAGE COMPLETED!");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error filling first page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill out the second page with highlighting and step-by-step approach
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("\n=== FILLING SECOND PAGE ===");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page Title: " + driver.getTitle());

            // Wait for page to load
            System.out.println("Waiting for second page to load...");
            Thread.sleep(10000);

            // Check what elements are available
            checkPageElements(driver);

            // Step 1: First dropdown (OB - OUTBOUND SUBJECT)
            System.out.println("\n--- STEP 1: First Dropdown ---");
            if (clickMatSelect(driver, "mat-select-4", "First dropdown")) {
                Thread.sleep(2000);
                clickMatOption(driver, "mat-option-68", "OB - OUTBOUND SUBJECT");
            }
            Thread.sleep(2000);

            // Step 2: Second dropdown (AB - AG/BIO COUNTERMEASURES)
            System.out.println("\n--- STEP 2: Second Dropdown ---");
            if (clickMatSelect(driver, "mat-select-10", "Second dropdown")) {
                Thread.sleep(2000);
                clickMatOption(driver, "mat-option-549", "AB - AG/BIO COUNTERMEASURES");
            }
            Thread.sleep(2000);

            // Step 3: Third dropdown (0 - NO NOTIFICATION)
            System.out.println("\n--- STEP 3: Third Dropdown ---");
            if (clickMatSelect(driver, "mat-select-6", "Third dropdown")) {
                Thread.sleep(2000);
                clickMatOption(driver, "mat-option-238", "0 - NO NOTIFICATION");
            }
            Thread.sleep(2000);

            // Step 4: Fill remarks field
            System.out.println("\n--- STEP 4: Remarks Field ---");
            fillInputById(driver, "mat-input-1", "Automated test entry - " + System.currentTimeMillis());
            Thread.sleep(2000);

            // Step 5: Y/N dropdown
            System.out.println("\n--- STEP 5: Y/N Dropdown ---");
            if (clickMatSelect(driver, "mat-select-0", "Y/N dropdown")) {
                Thread.sleep(2000);
                clickMatOption(driver, "mat-option-2", "Y");
            }
            Thread.sleep(2000);

            // Step 6: Height dropdown
            System.out.println("\n--- STEP 6: Height Dropdown ---");
            if (clickMatSelect(driver, "mat-select-2", "Height dropdown")) {
                Thread.sleep(2000);
                int randomHeight = 8 + random.nextInt(12);
                clickMatOption(driver, "mat-option-" + randomHeight, "Height option");
            }
            Thread.sleep(2000);

            // Step 7: Weight field
            System.out.println("\n--- STEP 7: Weight Field ---");
            String weight = String.valueOf(120 + random.nextInt(80));
            fillInputById(driver, "mat-input-0", weight);
            Thread.sleep(2000);

            // Step 8: Add Name
            System.out.println("\n--- STEP 8: Add Name ---");
            if (clickButton(driver, "Add Name")) {
                Thread.sleep(3000);
                fillInputById(driver, "mat-input-2", data.getLastName());
                Thread.sleep(1000);
                fillInputById(driver, "mat-input-3", data.getFirstName());
            }
            Thread.sleep(2000);

            // Step 9: Add DOB
            System.out.println("\n--- STEP 9: Add DOB ---");
            if (clickButton(driver, "Add DOB")) {
                Thread.sleep(3000);
                fillInputById(driver, "mat-input-11", data.getDob());
            }

            System.out.println("\n✅ SECOND PAGE COMPLETED!");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Highlight element on page (visual feedback)
     */
    private static void highlightElement(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Add red border to highlight the element
            js.executeScript(
                    "arguments[0].style.border = '3px solid red';" +
                            "arguments[0].style.backgroundColor = 'yellow';" +
                            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    element
            );
            Thread.sleep(1000); // Keep highlight for 1 second
        } catch (Exception e) {
            System.out.println("Could not highlight element: " + e.getMessage());
        }
    }

    /**
     * Remove highlight from element
     */
    private static void removeHighlight(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "arguments[0].style.border = '';" +
                            "arguments[0].style.backgroundColor = '';",
                    element
            );
        } catch (Exception e) {
            // Ignore errors when removing highlight
        }
    }

    /**
     * Fill input field by ID with highlighting
     */
    private static boolean fillInput(WebDriver driver, String fieldId, String value) {
        try {
            System.out.println("🎯 Looking for input field: " + fieldId);

            WebElement element = driver.findElement(By.id(fieldId));
            highlightElement(driver, element);

            System.out.println("✅ Found and highlighted element: " + fieldId);
            element.clear();
            element.sendKeys(value);

            removeHighlight(driver, element);
            System.out.println("✅ Successfully filled: " + fieldId + " = " + value);
            return true;

        } catch (Exception e) {
            System.out.println("❌ Failed to fill input " + fieldId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill input field by element ID with highlighting
     */
    private static boolean fillInputById(WebDriver driver, String elementId, String value) {
        try {
            System.out.println("🎯 Looking for element by ID: " + elementId);

            // Try direct ID approach first
            try {
                WebElement element = driver.findElement(By.id(elementId));
                highlightElement(driver, element);

                System.out.println("✅ Found element: " + elementId);
                element.clear();
                element.sendKeys(value);

                removeHighlight(driver, element);
                System.out.println("✅ Successfully filled: " + elementId + " = " + value);
                return true;

            } catch (Exception e) {
                System.out.println("Direct ID approach failed: " + e.getMessage());
            }

            // Try XPath approach
            try {
                String xpath = "//*[@id='" + elementId + "']";
                WebElement element = driver.findElement(By.xpath(xpath));
                highlightElement(driver, element);

                System.out.println("✅ Found element with XPath: " + elementId);
                element.clear();
                element.sendKeys(value);

                removeHighlight(driver, element);
                System.out.println("✅ Successfully filled with XPath: " + elementId + " = " + value);
                return true;

            } catch (Exception e2) {
                System.out.println("XPath approach also failed: " + e2.getMessage());
            }

            System.out.println("❌ Could not find element: " + elementId);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error filling element " + elementId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click mat-select dropdown with highlighting
     */
    private static boolean clickMatSelect(WebDriver driver, String selectId, String description) {
        try {
            System.out.println("🎯 Looking for mat-select: " + selectId + " (" + description + ")");

            // Method 1: Direct ID
            try {
                WebElement matSelect = driver.findElement(By.id(selectId));
                highlightElement(driver, matSelect);

                System.out.println("✅ Found mat-select: " + selectId);

                // Try clicking the trigger inside
                try {
                    WebElement trigger = matSelect.findElement(By.className("mat-select-trigger"));
                    trigger.click();
                    System.out.println("✅ Clicked mat-select-trigger");
                    removeHighlight(driver, matSelect);
                    return true;
                } catch (Exception e) {
                    // Try clicking the mat-select itself
                    matSelect.click();
                    System.out.println("✅ Clicked mat-select directly");
                    removeHighlight(driver, matSelect);
                    return true;
                }

            } catch (Exception e) {
                System.out.println("Direct approach failed: " + e.getMessage());
            }

            // Method 2: JavaScript click
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var element = document.getElementById('" + selectId + "'); " +
                                "if (element) { " +
                                "  element.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "  var trigger = element.querySelector('.mat-select-trigger'); " +
                                "  if (trigger) { " +
                                "    trigger.click(); " +
                                "  } else { " +
                                "    element.click(); " +
                                "  } " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    System.out.println("✅ Clicked mat-select using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript approach failed: " + e.getMessage());
            }

            System.out.println("❌ Failed to click mat-select: " + selectId);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error clicking mat-select " + selectId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click mat-option with highlighting
     */
    private static boolean clickMatOption(WebDriver driver, String optionId, String description) {
        try {
            System.out.println("🎯 Looking for mat-option: " + optionId + " (" + description + ")");

            // Wait a bit for dropdown to open
            Thread.sleep(1000);

            try {
                WebElement option = driver.findElement(By.id(optionId));
                highlightElement(driver, option);

                System.out.println("✅ Found mat-option: " + optionId);
                option.click();

                removeHighlight(driver, option);
                System.out.println("✅ Successfully clicked: " + optionId);
                return true;

            } catch (Exception e) {
                System.out.println("❌ Failed to click option " + optionId + ": " + e.getMessage());
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Error clicking option " + optionId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click button by text with highlighting
     */
    private static boolean clickButton(WebDriver driver, String buttonText) {
        try {
            System.out.println("🎯 Looking for button: " + buttonText);

            // Method 1: XPath by text
            try {
                WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
                highlightElement(driver, button);

                System.out.println("✅ Found button: " + buttonText);
                button.click();

                removeHighlight(driver, button);
                System.out.println("✅ Successfully clicked button: " + buttonText);
                return true;

            } catch (Exception e) {
                System.out.println("Text-based button search failed: " + e.getMessage());
            }

            // Method 2: Look for span inside button
            try {
                WebElement button = driver.findElement(By.xpath("//button//span[contains(text(), '" + buttonText + "')]/ancestor::button"));
                highlightElement(driver, button);

                System.out.println("✅ Found button by span: " + buttonText);
                button.click();

                removeHighlight(driver, button);
                System.out.println("✅ Successfully clicked button by span: " + buttonText);
                return true;

            } catch (Exception e) {
                System.out.println("Span-based button search failed: " + e.getMessage());
            }

            // Method 3: JavaScript search
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var buttons = document.querySelectorAll('button'); " +
                                "for (var i = 0; i < buttons.length; i++) { " +
                                "  if (buttons[i].textContent.includes('" + buttonText + "')) { " +
                                "    buttons[i].scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "    buttons[i].click(); " +
                                "    return true; " +
                                "  } " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    System.out.println("✅ Clicked button using JavaScript: " + buttonText);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript button search failed: " + e.getMessage());
            }

            System.out.println("❌ Could not find button: " + buttonText);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error clicking button " + buttonText + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Check what elements are available on the page
     */
    private static void checkPageElements(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            System.out.println("\n=== PAGE ELEMENT CHECK ===");

            Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
            System.out.println("Mat-select elements: " + matSelectCount);

            Object inputCount = js.executeScript("return document.querySelectorAll('input').length;");
            System.out.println("Input elements: " + inputCount);

            Object buttonCount = js.executeScript("return document.querySelectorAll('button').length;");
            System.out.println("Button elements: " + buttonCount);

            // List first 5 mat-select IDs
            js.executeScript(
                    "console.log('=== MAT-SELECT ELEMENTS ==='); " +
                            "var selects = document.querySelectorAll('mat-select'); " +
                            "for (var i = 0; i < Math.min(selects.length, 5); i++) { " +
                            "  console.log('mat-select ' + i + ': ID=' + selects[i].id + ', classes=' + selects[i].className.substring(0,50)); " +
                            "}"
            );

            System.out.println("Check browser console for detailed element list");
            System.out.println("========================\n");

        } catch (Exception e) {
            System.out.println("Error checking page elements: " + e.getMessage());
        }
    }

    /**
     * Wait for an element to be visible and then send keys to it
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