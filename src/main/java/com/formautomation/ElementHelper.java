package com.formautomation;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;

/**
 * Helper class for finding and interacting with elements using label-based targeting
 * This approach is more reliable than using changing IDs
 */
public class ElementHelper {

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(10);
    private static final Duration SHORT_WAIT = Duration.ofSeconds(3);

    // ================ DROPDOWN METHODS ================

    /**
     * Find dropdown by label text and click its trigger
     * @param driver WebDriver instance
     * @param labelText The text of the mat-label (e.g., "Sex:", "Record Status:")
     * @return true if successfully clicked
     */
    public static boolean clickDropdownByLabel(WebDriver driver, String labelText) {
        try {
            System.out.println("🎯 Looking for dropdown with label: " + labelText);

            // XPath to find mat-select trigger by label text
            String xpath = "//mat-label[contains(normalize-space(text()), '" + labelText + "')]" +
                    "/ancestor::mat-form-field" +
                    "//mat-select" +
                    "//div[contains(@class, 'mat-select-trigger')]";

            WebElement trigger = findElementSafely(driver, By.xpath(xpath), DEFAULT_WAIT);
            if (trigger != null) {
                return clickElementSafely(driver, trigger, "dropdown trigger for " + labelText);
            }

            System.out.println("❌ Could not find dropdown trigger for: " + labelText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error clicking dropdown by label '" + labelText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Select option from dropdown by option text
     * @param driver WebDriver instance
     * @param optionText The text of the option to select
     * @return true if successfully selected
     */
    public static boolean selectDropdownOption(WebDriver driver, String optionText) {
        try {
            System.out.println("🎯 Selecting dropdown option: " + optionText);

            // Wait for dropdown panel to appear
            Thread.sleep(1000);

            // Find the option by text
            String xpath = "//mat-option//span[@class='mat-option-text'][contains(normalize-space(text()), '" + optionText + "')]" +
                    " | //mat-option[contains(normalize-space(text()), '" + optionText + "')]";

            WebElement option = findElementSafely(driver, By.xpath(xpath), SHORT_WAIT);
            if (option != null) {
                boolean success = clickElementSafely(driver, option, "option " + optionText);
                Thread.sleep(500); // Allow dropdown to close
                return success;
            }

            System.out.println("❌ Could not find option: " + optionText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error selecting option '" + optionText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Complete dropdown selection by label and option text
     * @param driver WebDriver instance
     * @param labelText Label of the dropdown
     * @param optionText Text of the option to select
     * @return true if successful
     */
    public static boolean selectDropdownByLabel(WebDriver driver, String labelText, String optionText) {
        if (clickDropdownByLabel(driver, labelText)) {
            return selectDropdownOption(driver, optionText);
        }
        return false;
    }

    // ================ INPUT FIELD METHODS ================

    /**
     * Fill input field by label text
     * @param driver WebDriver instance
     * @param labelText The text of the mat-label
     * @param value The value to enter
     * @return true if successful
     */
    public static boolean fillInputByLabel(WebDriver driver, String labelText, String value) {
        try {
            System.out.println("🎯 Filling input field '" + labelText + "' with: " + value);

            // XPath to find input by label text
            String xpath = "//mat-label[contains(normalize-space(text()), '" + labelText + "')]" +
                    "/ancestor::mat-form-field" +
                    "//input[@class and contains(@class, 'mat-input-element')]";

            WebElement input = findElementSafely(driver, By.xpath(xpath), DEFAULT_WAIT);
            if (input != null) {
                return fillInputSafely(driver, input, value, labelText);
            }

            System.out.println("❌ Could not find input field for: " + labelText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error filling input by label '" + labelText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill textarea by looking for textarea element with specific attributes
     * @param driver WebDriver instance
     * @param value The value to enter
     * @return true if successful
     */
    public static boolean fillRemarksTextarea(WebDriver driver, String value) {
        try {
            System.out.println("🎯 Filling remarks textarea with: " + value);

            // Find textarea with maxlength 3000 (remarks field)
            String xpath = "//textarea[@maxlength='3000' and contains(@class, 'mat-input-element')]";

            WebElement textarea = findElementSafely(driver, By.xpath(xpath), DEFAULT_WAIT);
            if (textarea != null) {
                return fillInputSafely(driver, textarea, value, "remarks textarea");
            }

            System.out.println("❌ Could not find remarks textarea");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error filling remarks textarea: " + e.getMessage());
            return false;
        }
    }

    // ================ DATE INPUT METHODS ================

    /**
     * Fill date input by label text
     * @param driver WebDriver instance
     * @param labelText The text of the mat-label
     * @param dateValue The date value in MM/dd/yyyy format
     * @return true if successful
     */
    public static boolean fillDateByLabel(WebDriver driver, String labelText, String dateValue) {
        try {
            System.out.println("🎯 Filling date field '" + labelText + "' with: " + dateValue);

            // XPath to find date input by label text
            String xpath = "//mat-label[contains(normalize-space(text()), '" + labelText + "')]" +
                    "/ancestor::mat-form-field" +
                    "//input[@mask='00/00/0000' or contains(@placeholder, 'MM/DD/YYYY')]";

            WebElement dateInput = findElementSafely(driver, By.xpath(xpath), DEFAULT_WAIT);
            if (dateInput != null) {
                return fillInputSafely(driver, dateInput, dateValue, labelText);
            }

            System.out.println("❌ Could not find date input for: " + labelText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error filling date by label '" + labelText + "': " + e.getMessage());
            return false;
        }
    }

    // ================ BUTTON METHODS ================

    /**
     * Click button by text content
     * @param driver WebDriver instance
     * @param buttonText The text on the button
     * @return true if successful
     */
    public static boolean clickButtonByText(WebDriver driver, String buttonText) {
        try {
            System.out.println("🎯 Clicking button: " + buttonText);

            // Multiple XPath options for different button structures
            String[] xpaths = {
                    "//button[contains(normalize-space(text()), '" + buttonText + "')]",
                    "//button//span[contains(normalize-space(text()), '" + buttonText + "')]/..",
                    "//cbp-button//button[contains(normalize-space(text()), '" + buttonText + "')]",
                    "//a[contains(normalize-space(text()), '" + buttonText + "')]"
            };

            for (String xpath : xpaths) {
                WebElement button = findElementSafely(driver, By.xpath(xpath), SHORT_WAIT);
                if (button != null) {
                    return clickElementSafely(driver, button, "button " + buttonText);
                }
            }

            System.out.println("❌ Could not find button: " + buttonText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error clicking button '" + buttonText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Click button containing partial text (for complex button texts)
     * @param driver WebDriver instance
     * @param partialText Part of the button text
     * @return true if successful
     */
    public static boolean clickButtonContaining(WebDriver driver, String partialText) {
        try {
            System.out.println("🎯 Clicking button containing: " + partialText);

            String xpath = "//button[contains(normalize-space(.), '" + partialText + "')]" +
                    " | //a[contains(normalize-space(.), '" + partialText + "')]";

            WebElement button = findElementSafely(driver, By.xpath(xpath), DEFAULT_WAIT);
            if (button != null) {
                return clickElementSafely(driver, button, "button containing " + partialText);
            }

            System.out.println("❌ Could not find button containing: " + partialText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error clicking button containing '" + partialText + "': " + e.getMessage());
            return false;
        }
    }

    // ================ UTILITY METHODS ================

    /**
     * Find element safely with timeout
     * @param driver WebDriver instance
     * @param by Locator strategy
     * @param timeout Duration to wait
     * @return WebElement or null if not found
     */
    private static WebElement findElementSafely(WebDriver driver, By by, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (Exception e) {
            // Element not found - return null instead of throwing
            return null;
        }
    }

    /**
     * Click element using multiple strategies (standard click, then JS if needed)
     * @param driver WebDriver instance
     * @param element WebElement to click
     * @param description Description for logging
     * @return true if successful
     */
    private static boolean clickElementSafely(WebDriver driver, WebElement element, String description) {
        try {
            // Strategy 1: Standard Selenium click
            try {
                WebDriverWait wait = new WebDriverWait(driver, SHORT_WAIT);
                wait.until(ExpectedConditions.elementToBeClickable(element));
                element.click();
                System.out.println("✅ Standard click successful: " + description);
                return true;
            } catch (Exception e1) {
                System.out.println("⚠️ Standard click failed, trying Actions click: " + description);

                // Strategy 2: Actions click
                try {
                    Actions actions = new Actions(driver);
                    actions.moveToElement(element).click().perform();
                    System.out.println("✅ Actions click successful: " + description);
                    return true;
                } catch (Exception e2) {
                    System.out.println("⚠️ Actions click failed, trying JavaScript click: " + description);

                    // Strategy 3: JavaScript click
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", element);
                    System.out.println("✅ JavaScript click successful: " + description);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ All click strategies failed for: " + description + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill input element using multiple strategies
     * @param driver WebDriver instance
     * @param element WebElement to fill
     * @param value Value to enter
     * @param description Description for logging
     * @return true if successful
     */
    private static boolean fillInputSafely(WebDriver driver, WebElement element, String value, String description) {
        try {
            // Strategy 1: Standard Selenium sendKeys
            try {
                element.clear();
                element.sendKeys(value);
                System.out.println("✅ Standard input successful: " + description);
                return true;
            } catch (Exception e1) {
                System.out.println("⚠️ Standard input failed, trying JavaScript: " + description);

                // Strategy 2: JavaScript value setting
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                        "arguments[0].focus();" +
                                "arguments[0].value = arguments[1];" +
                                "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));" +
                                "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));" +
                                "arguments[0].blur();",
                        element, value
                );
                System.out.println("✅ JavaScript input successful: " + description);
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ All input strategies failed for: " + description + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Wait for and click the newest button with specific text
     * @param driver WebDriver instance
     * @param buttonText Text on the button
     * @return true if successful
     */
    public static boolean clickNewestButton(WebDriver driver, String buttonText) {
        try {
            System.out.println("🎯 Looking for newest button: " + buttonText);

            String xpath = "//button[contains(normalize-space(.), '" + buttonText + "')]";

            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
            List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));

            if (!buttons.isEmpty()) {
                // Get the last (newest) button
                WebElement newestButton = buttons.get(buttons.size() - 1);
                return clickElementSafely(driver, newestButton, "newest button " + buttonText);
            }

            System.out.println("❌ No buttons found with text: " + buttonText);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error clicking newest button '" + buttonText + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill the newest input field of a specific type
     * @param driver WebDriver instance
     * @param inputType Type of input (e.g., "text", or mask like "00/00/0000")
     * @param value Value to enter
     * @return true if successful
     */
    public static boolean fillNewestInput(WebDriver driver, String inputType, String value) {
        try {
            System.out.println("🎯 Filling newest input of type '" + inputType + "' with: " + value);

            String xpath;
            if (inputType.contains("/")) {
                // Date input with mask
                xpath = "//input[@mask='" + inputType + "']";
            } else {
                // Regular input
                xpath = "//input[@type='" + inputType + "' or contains(@class, 'mat-input-element')]";
            }

            WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
            List<WebElement> inputs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath)));

            if (!inputs.isEmpty()) {
                // Get the last (newest) input
                WebElement newestInput = inputs.get(inputs.size() - 1);
                return fillInputSafely(driver, newestInput, value, "newest input " + inputType);
            }

            System.out.println("❌ No inputs found of type: " + inputType);
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error filling newest input '" + inputType + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Close any open dropdowns or overlays
     * @param driver WebDriver instance
     */
    public static void closeDropdowns(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "document.body.click();" +
                            "if (document.activeElement) document.activeElement.blur();" +
                            "document.dispatchEvent(new KeyboardEvent('keydown', {key: 'Escape'}));"
            );
            Thread.sleep(300);
        } catch (Exception e) {
            // Ignore errors in cleanup
        }
    }
}