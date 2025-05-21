package com.formautomation;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Utility class to handle Angular Material specific components
 */
public class AngularMaterialHandler {

    private final WebDriver driver;
    private final JavascriptExecutor js;
    private final WebDriverWait wait;

    /**
     * Constructor
     * @param driver WebDriver instance
     */
    public AngularMaterialHandler(WebDriver driver) {
        this.driver = driver;
        this.js = (JavascriptExecutor) driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Wait for Angular to finish rendering
     */
    public void waitForAngular() {
        try {
            // Check if Angular exists on the page
            Boolean hasAngular = (Boolean) js.executeScript(
                    "return window.getAllAngularRootElements && window.getAllAngularRootElements().length > 0");

            if (hasAngular) {
                System.out.println("Angular detected, waiting for it to stabilize...");

                // Wait for Angular to be stable
                js.executeScript(
                        "var callback = arguments[arguments.length - 1];" +
                                "var el = document.querySelector('body');" +
                                "if (window.angular) {" +
                                "  window.angular.element(el).injector().get('$browser').notifyWhenNoOutstandingRequests(callback);" +
                                "} else {" +
                                "  callback();" +
                                "}");

                System.out.println("Angular has stabilized");
            } else {
                System.out.println("No Angular detected on page");
            }
        } catch (Exception e) {
            System.out.println("Error waiting for Angular: " + e.getMessage());
        }
    }

    /**
     * Select an option from a mat-select dropdown
     * @param selectIndex Index of the mat-select element (0-based)
     * @param optionId ID of the mat-option to select
     * @return true if successful
     */
    public boolean selectMatOption(int selectIndex, String optionId) {
        try {
            // Try multiple methods to find and click the mat-select
            boolean clicked = false;

            // Method 1: Standard Selenium
            try {
                List<WebElement> matSelects = driver.findElements(By.tagName("mat-select"));
                if (matSelects.size() > selectIndex) {
                    matSelects.get(selectIndex).click();
                    clicked = true;
                }
            } catch (Exception e) {
                System.out.println("Could not click mat-select using standard Selenium: " + e.getMessage());
            }

            // Method 2: Try by CSS selector
            if (!clicked) {
                try {
                    List<WebElement> matSelects = driver.findElements(
                            By.cssSelector("[role='combobox'][aria-haspopup='true']"));
                    if (matSelects.size() > selectIndex) {
                        matSelects.get(selectIndex).click();
                        clicked = true;
                    }
                } catch (Exception e) {
                    System.out.println("Could not click mat-select using CSS selector: " + e.getMessage());
                }
            }

            // Method 3: JavaScript
            if (!clicked) {
                System.out.println("Using JavaScript to click mat-select...");
                Boolean jsResult = (Boolean) js.executeScript(
                        "var selects = document.querySelectorAll('mat-select'); " +
                                "if (selects.length > " + selectIndex + ") { " +
                                "  selects[" + selectIndex + "].click(); " +
                                "  return true; " +
                                "} " +
                                "return false;");
                clicked = jsResult != null && jsResult;
            }

            if (!clicked) {
                System.out.println("Failed to click mat-select at index " + selectIndex);
                return false;
            }

            // Wait for dropdown to open
            Thread.sleep(1000);

            // Try to select the option by ID
            try {
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(optionId))
                );
                option.click();
                return true;
            } catch (Exception e) {
                System.out.println("Could not select option by ID: " + e.getMessage());

                // Try alternative method using JavaScript
                Boolean jsResult = (Boolean) js.executeScript(
                        "var option = document.getElementById('" + optionId + "'); " +
                                "if (option) { " +
                                "  option.click(); " +
                                "  return true; " +
                                "} " +
                                "return false;");

                return jsResult != null && jsResult;
            }
        } catch (Exception e) {
            System.out.println("Error in selectMatOption: " + e.getMessage());
            return false;
        }
    }

    /**
     * Click an Angular Material button by text
     * @param buttonText Text contained in the button
     * @return true if successful
     */
    public boolean clickMatButton(String buttonText) {
        try {
            // First attempt: standard XPath
            try {
                WebElement button = driver.findElement(
                        By.xpath("//button[contains(., '" + buttonText + "')]"));
                button.click();
                return true;
            } catch (Exception e) {
                System.out.println("Could not click button using XPath: " + e.getMessage());
            }

            // Second attempt: try CSS selector for Angular Material buttons
            try {
                WebElement button = driver.findElement(
                        By.cssSelector(".mat-button, .mat-raised-button, .mat-flat-button"));
                if (button.getText().contains(buttonText)) {
                    button.click();
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not click button using CSS selector: " + e.getMessage());
            }

            // Third attempt: JavaScript
            Boolean jsResult = (Boolean) js.executeScript(
                    "var buttons = document.querySelectorAll('button'); " +
                            "for (var i = 0; i < buttons.length; i++) { " +
                            "  if (buttons[i].textContent.includes('" + buttonText + "')) { " +
                            "    buttons[i].click(); " +
                            "    return true; " +
                            "  } " +
                            "} " +
                            "return false;");

            return jsResult != null && jsResult;
        } catch (Exception e) {
            System.out.println("Error in clickMatButton: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set value for Angular Material input
     * @param inputId ID of the input element
     * @param value Value to set
     * @return true if successful
     */
    public boolean setMatInputValue(String inputId, String value) {
        try {
            WebElement input = driver.findElement(By.id(inputId));
            input.clear();
            input.sendKeys(value);
            return true;
        } catch (Exception e) {
            System.out.println("Could not set input value using standard method: " + e.getMessage());

            // Try JavaScript
            Boolean jsResult = (Boolean) js.executeScript(
                    "var input = document.getElementById('" + inputId + "'); " +
                            "if (input) { " +
                            "  input.value = '" + value.replace("'", "\\'") + "'; " +
                            "  input.dispatchEvent(new Event('input')); " +
                            "  input.dispatchEvent(new Event('change')); " +
                            "  return true; " +
                            "} " +
                            "return false;");

            return jsResult != null && jsResult;
        }
    }

    /**
     * Print detailed information about Angular Material elements on the page
     */
    public void printAngularMaterialElementsInfo() {
        System.out.println("\n===== ANGULAR MATERIAL ELEMENTS INFO =====");

        // Check for Angular version
        try {
            Object angularVersion = js.executeScript(
                    "return window.getAllAngularRootElements ? " +
                            "window.getAllAngularRootElements()[0].getAttribute('ng-version') : 'Not Angular'");
            System.out.println("Angular Version: " + angularVersion);
        } catch (Exception e) {
            System.out.println("Could not detect Angular version: " + e.getMessage());
        }

        // Count different Angular Material elements
        String[] matElements = {
                "mat-select", "mat-option", "mat-form-field", "mat-input",
                "mat-button", "mat-raised-button", "mat-datepicker"
        };

        for (String element : matElements) {
            try {
                Object count = js.executeScript(
                        "return document.querySelectorAll('" + element + "').length");
                System.out.println(element + " count: " + count);
            } catch (Exception e) {
                System.out.println("Error counting " + element + ": " + e.getMessage());
            }
        }

        // Try to get mat-select elements details
        try {
            js.executeScript(
                    "var selects = document.querySelectorAll('mat-select'); " +
                            "for (var i = 0; i < selects.length && i < 5; i++) { " +
                            "  console.log('mat-select ' + i + ' classes: ' + selects[i].className); " +
                            "  console.log('mat-select ' + i + ' id: ' + selects[i].id); " +
                            "}");
        } catch (Exception e) {
            System.out.println("Error getting mat-select details: " + e.getMessage());
        }

        System.out.println("=========================================\n");
    }
}