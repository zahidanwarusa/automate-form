package com.formautomation;

import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Helper class for interacting with Angular applications
 */
public class AngularHelper {
    private final WebDriver driver;
    private final NgWebDriver ngDriver;
    private final JavascriptExecutor jsExecutor;
    private final WebDriverWait wait;

    /**
     * Constructor
     * @param driver Selenium WebDriver instance
     */
    public AngularHelper(WebDriver driver) {
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;
        this.ngDriver = new NgWebDriver(jsExecutor);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    /**
     * Wait for Angular to finish rendering
     */
    public void waitForAngular() {
        try {
            System.out.println("Waiting for Angular to finish rendering...");
            ngDriver.waitForAngularRequestsToFinish();
            System.out.println("Angular rendering complete.");
        } catch (Exception e) {
            System.out.println("Error waiting for Angular: " + e.getMessage());
        }
    }

    /**
     * Click on a mat-select dropdown element
     * @param label The label of the dropdown (visible text)
     * @return true if successful, false otherwise
     */
    public boolean clickMatSelect(String label) {
        try {
            System.out.println("Trying to find and click mat-select with label: " + label);

            // Wait for Angular to finish rendering
            waitForAngular();

            // Method 1: Try to find by label text
            try {
                WebElement matSelectLabel = driver.findElement(By.xpath(
                        "//mat-label[contains(text(), '" + label + "')]/ancestor::mat-form-field//mat-select"));
                matSelectLabel.click();
                System.out.println("Found and clicked mat-select by label text");
                Thread.sleep(1000); // Wait for dropdown to open
                return true;
            } catch (Exception e) {
                System.out.println("Could not find mat-select by label text: " + e.getMessage());
            }

            // Method 2: Try to find any mat-select element
            try {
                List<WebElement> matSelects = driver.findElements(By.tagName("mat-select"));
                if (!matSelects.isEmpty()) {
                    System.out.println("Found " + matSelects.size() + " mat-select elements, clicking the first one");
                    matSelects.get(0).click();
                    Thread.sleep(1000); // Wait for dropdown to open
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not find any mat-select elements: " + e.getMessage());
            }

            // Method 3: Try using JavaScript to click
            try {
                String jsScript = "return document.querySelector('mat-select');";
                WebElement matSelect = (WebElement) jsExecutor.executeScript(jsScript);
                if (matSelect != null) {
                    jsExecutor.executeScript("arguments[0].click();", matSelect);
                    System.out.println("Clicked mat-select using JavaScript");
                    Thread.sleep(1000); // Wait for dropdown to open
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not click mat-select using JavaScript: " + e.getMessage());
            }

            System.out.println("Failed to find and click mat-select");
            return false;
        } catch (Exception e) {
            System.out.println("Error in clickMatSelect: " + e.getMessage());
            return false;
        }
    }

    /**
     * Select an option from an already opened mat-select dropdown
     * @param optionText The text of the option to select
     * @return true if successful, false otherwise
     */
    public boolean selectMatOption(String optionText) {
        try {
            System.out.println("Trying to select mat-option with text: " + optionText);

            // Method 1: Try to find by exact option text
            try {
                WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//mat-option//span[contains(text(), '" + optionText + "')]")));
                option.click();
                System.out.println("Selected mat-option by text");
                return true;
            } catch (Exception e) {
                System.out.println("Could not find mat-option by text: " + e.getMessage());
            }

            // Method 2: Try to find by option ID if provided
            if (optionText.startsWith("mat-option-")) {
                try {
                    WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                            By.id(optionText)));
                    option.click();
                    System.out.println("Selected mat-option by ID");
                    return true;
                } catch (Exception e) {
                    System.out.println("Could not find mat-option by ID: " + e.getMessage());
                }
            }

            // Method 3: Try to find any mat-option element
            try {
                List<WebElement> options = driver.findElements(By.tagName("mat-option"));
                if (!options.isEmpty()) {
                    System.out.println("Found " + options.size() + " mat-option elements, clicking the first one");
                    options.get(0).click();
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not find any mat-option elements: " + e.getMessage());
            }

            System.out.println("Failed to find and select mat-option");
            return false;
        } catch (Exception e) {
            System.out.println("Error in selectMatOption: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handle an entire mat-select dropdown operation (click + select)
     * @param label The label of the dropdown
     * @param optionText The text of the option to select
     * @return true if successful, false otherwise
     */
    public boolean handleMatSelectDropdown(String label, String optionText) {
        try {
            if (clickMatSelect(label)) {
                Thread.sleep(1000); // Wait for dropdown to fully open
                return selectMatOption(optionText);
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error in handleMatSelectDropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Try different approaches to find a button and click it
     * @param buttonText The text on the button
     * @return true if successful, false otherwise
     */
    public boolean clickButton(String buttonText) {
        try {
            System.out.println("Trying to find and click button with text: " + buttonText);

            // Wait for Angular to finish rendering
            waitForAngular();

            // Method 1: Try xpath with button text
            try {
                WebElement button = driver.findElement(By.xpath(
                        "//button[contains(text(), '" + buttonText + "')]"));
                button.click();
                System.out.println("Found and clicked button by text");
                return true;
            } catch (Exception e) {
                System.out.println("Could not find button by text: " + e.getMessage());
            }

            // Method 2: Try looking for span inside button
            try {
                WebElement button = driver.findElement(By.xpath(
                        "//button//span[contains(text(), '" + buttonText + "')]/.."));
                button.click();
                System.out.println("Found and clicked button by span text");
                return true;
            } catch (Exception e) {
                System.out.println("Could not find button by span text: " + e.getMessage());
            }

            // Method 3: Try using JavaScript to click
            try {
                String jsScript = "var btns = Array.from(document.querySelectorAll('button')); " +
                        "var targetBtn = btns.find(b => b.textContent.includes('" + buttonText + "')); " +
                        "return targetBtn;";
                WebElement button = (WebElement) jsExecutor.executeScript(jsScript);
                if (button != null) {
                    jsExecutor.executeScript("arguments[0].click();", button);
                    System.out.println("Clicked button using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not click button using JavaScript: " + e.getMessage());
            }

            System.out.println("Failed to find and click button");
            return false;
        } catch (Exception e) {
            System.out.println("Error in clickButton: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill a mat-input field
     * @param label The label of the input field
     * @param value The value to enter
     * @return true if successful, false otherwise
     */
    public boolean fillMatInput(String label, String value) {
        try {
            System.out.println("Trying to fill mat-input with label: " + label);

            // Wait for Angular to finish rendering
            waitForAngular();

            // Method 1: Try to find by label text
            try {
                WebElement input = driver.findElement(By.xpath(
                        "//mat-label[contains(text(), '" + label + "')]/ancestor::mat-form-field//input"));
                input.clear();
                input.sendKeys(value);
                System.out.println("Found and filled input by label text");
                return true;
            } catch (Exception e) {
                System.out.println("Could not find input by label text: " + e.getMessage());
            }

            // Method 2: Try using JS to find by label and fill
            try {
                String jsScript = "var labels = Array.from(document.querySelectorAll('mat-label')); " +
                        "var targetLabel = labels.find(l => l.textContent.includes('" + label + "')); " +
                        "if (targetLabel) { " +
                        "   var input = targetLabel.closest('mat-form-field').querySelector('input'); " +
                        "   if (input) { input.value = ''; return input; } " +
                        "} " +
                        "return null;";
                WebElement input = (WebElement) jsExecutor.executeScript(jsScript);
                if (input != null) {
                    input.sendKeys(value);
                    System.out.println("Found and filled input using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Could not find and fill input using JavaScript: " + e.getMessage());
            }

            System.out.println("Failed to find and fill input");
            return false;
        } catch (Exception e) {
            System.out.println("Error in fillMatInput: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get NgWebDriver instance
     * @return NgWebDriver instance
     */
    public NgWebDriver getNgDriver() {
        return ngDriver;
    }
}