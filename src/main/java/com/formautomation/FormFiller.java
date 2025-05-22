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
 * Updated FormFiller using direct XPath selectors by ID (proven to work in console)
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
            System.out.println("Filling out first page...");

            // Fill in last name
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            // Fill in first name
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            // Fill in DOB
            waitAndSendKeys(driver, By.id("dob"), data.getDob());

            // Click the Create TECS Lookout button
            System.out.println("Looking for Create TECS Lookout button...");
            try {
                WebElement createButton = driver.findElement(By.xpath("//a[contains(text(), 'Create TECS Lookout')]"));

                // Scroll to the button first
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", createButton);
                Thread.sleep(2000);

                createButton.click();
                System.out.println("Create TECS Lookout button clicked successfully");
            } catch (Exception e) {
                System.out.println("Could not find Create TECS Lookout button: " + e.getMessage());
                return false;
            }

            System.out.println("First page completed!");
            return true;
        } catch (Exception e) {
            System.out.println("Error filling first page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill out the second page using direct XPath selectors by ID
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");

            // Wait for second page to load
            Thread.sleep(10000);
            System.out.println("Second page loaded, starting form filling...");

            // First dropdown - mat-select-4 with option mat-option-68 (OB - OUTBOUND SUBJECT)
            System.out.println("1. Selecting first dropdown (OB - OUTBOUND SUBJECT)...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-4']")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-68']");
            }

            Thread.sleep(2000);

            // Second dropdown - mat-select-10 with option mat-option-549 (AB - AG/BIO COUNTERMEASURES)
            System.out.println("2. Selecting second dropdown (AB - AG/BIO COUNTERMEASURES)...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-10']")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-549']");
            }

            Thread.sleep(2000);

            // Third dropdown - mat-select-6 with option mat-option-238 (0 - NO NOTIFICATION)
            System.out.println("3. Selecting third dropdown (0 - NO NOTIFICATION)...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-6']")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-238']");
            }

            Thread.sleep(2000);

            // Fourth dropdown (Multiple) - mat-select-12 with random option from 253-548
            System.out.println("4. Selecting fourth dropdown (Multiple)...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-12']")) {
                Thread.sleep(2000);
                int randomOption = 253 + random.nextInt(6); // Random between 253-258 for testing
                clickElementByXPath(driver, "//*[@id='mat-option-" + randomOption + "']");
            }

            Thread.sleep(2000);

            // Fifth dropdown - mat-select-8 with option mat-option-242 (0 - NOT ON PRIMARY)
            System.out.println("5. Selecting fifth dropdown (0 - NOT ON PRIMARY)...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-8']")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-242']");
            }

            Thread.sleep(2000);

            // Fill remarks field - mat-input-1 (textarea)
            System.out.println("6. Filling remarks field...");
            fillElementByXPath(driver, "//*[@id='mat-input-1']", "Automated test entry - " + System.currentTimeMillis());

            Thread.sleep(2000);

            // Y/N dropdown - mat-select-0 with option mat-option-2 (Y)
            System.out.println("7. Selecting Y/N dropdown...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-0']")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-2']");
            }

            Thread.sleep(2000);

            // Height dropdown - mat-select-2 with random height option
            System.out.println("8. Selecting height dropdown...");
            if (clickElementByXPath(driver, "//*[@id='mat-select-2']")) {
                Thread.sleep(2000);
                int randomHeight = 8 + random.nextInt(20); // Random between 8-27
                clickElementByXPath(driver, "//*[@id='mat-option-" + randomHeight + "']");
            }

            Thread.sleep(2000);

            // Fill weight field - mat-input-0
            System.out.println("9. Filling weight field...");
            String weight = String.valueOf(120 + random.nextInt(131));
            fillElementByXPath(driver, "//*[@id='mat-input-0']", weight);

            Thread.sleep(2000);

            // Add Sex button and select option
            System.out.println("10. Adding sex...");
            if (clickButtonByText(driver, "Add Sex")) {
                Thread.sleep(2000);
                // Find the newly added sex dropdown and select option
                String sexOption = random.nextBoolean() ? "630" : "631"; // F or M
                clickNewestMatOption(driver, sexOption);
            }

            Thread.sleep(2000);

            // Add Race button and select option
            System.out.println("11. Adding race...");
            if (clickButtonByText(driver, "Add Race")) {
                Thread.sleep(2000);
                int raceOption = 594 + random.nextInt(6); // Random between 594-599
                clickNewestMatOption(driver, String.valueOf(raceOption));
            }

            Thread.sleep(2000);

            // Add Eye Color button and select option
            System.out.println("12. Adding eye color...");
            if (clickButtonByText(driver, "Add Eye Color")) {
                Thread.sleep(2000);
                int eyeOption = 600 + random.nextInt(12); // Random between 600-611
                clickNewestMatOption(driver, String.valueOf(eyeOption));
            }

            Thread.sleep(2000);

            // Add Hair Color button and select option
            System.out.println("13. Adding hair color...");
            if (clickButtonByText(driver, "Add Hair Color")) {
                Thread.sleep(2000);
                int hairOption = 612 + random.nextInt(15); // Random between 612-626
                clickNewestMatOption(driver, String.valueOf(hairOption));
            }

            Thread.sleep(2000);

            // Add Name button and fill fields
            System.out.println("14. Adding name...");
            if (clickButtonByText(driver, "Add Name")) {
                Thread.sleep(2000);
                // Fill last name and first name with the same data from first page
                fillElementByXPath(driver, "//*[@id='mat-input-2']", data.getLastName());
                fillElementByXPath(driver, "//*[@id='mat-input-3']", data.getFirstName());
            }

            Thread.sleep(2000);

            // Add DOB button and fill field
            System.out.println("15. Adding DOB...");
            if (clickButtonByText(driver, "Add DOB")) {
                Thread.sleep(2000);
                fillElementByXPath(driver, "//*[@id='mat-input-11']", data.getDob());
            }

            Thread.sleep(2000);

            // Add Citizenship button and select USA
            System.out.println("16. Adding citizenship...");
            if (clickButtonByText(driver, "Add Citizenship")) {
                Thread.sleep(2000);
                clickNewestMatOption(driver, "1260"); // USA
            }

            Thread.sleep(2000);

            // Add Passport button and fill details
            System.out.println("17. Adding passport...");
            if (clickButtonByText(driver, "Add Passport")) {
                Thread.sleep(2000);
                // Select passport type (P - Regular)
                clickNewestMatOption(driver, "1518");
                Thread.sleep(1000);
                // Fill passport number
                fillElementByXPath(driver, "//*[@id='mat-input-19']", data.getPassportNumber());
                // Select passport country (USA)
                clickNewestMatOption(driver, "1520");
                // Fill passport issue date
                fillElementByXPath(driver, "//*[@id='mat-input-20']", data.getPassportIssueDate());
                // Fill passport expiry date
                fillElementByXPath(driver, "//*[@id='mat-input-21']", data.getPassportExpiryDate());
            }

            Thread.sleep(2000);

            // Add A# button and fill field
            System.out.println("18. Adding A#...");
            if (clickButtonByText(driver, "Add A#")) {
                Thread.sleep(2000);
                fillElementByXPath(driver, "//*[@id='mat-input-22']", data.getaNumber());
            }

            Thread.sleep(2000);

            // Add Driver's License button and fill details
            System.out.println("19. Adding driver's license...");
            if (clickButtonByText(driver, "Add Driver's License")) {
                Thread.sleep(2000);
                fillElementByXPath(driver, "//*[@id='mat-input-23']", data.getDriverLicense());
                // Select random US state
                int stateOption = 1774 + random.nextInt(62); // Random US state
                clickNewestMatOption(driver, String.valueOf(stateOption));
            }

            Thread.sleep(2000);

            // Add SSN button and fill field
            System.out.println("20. Adding SSN...");
            if (clickButtonByText(driver, "Add SSN")) {
                Thread.sleep(2000);
                // Find the newest input field for SSN
                List<WebElement> allInputs = driver.findElements(By.xpath("//input[@class and contains(@class, 'mat-input-element')]"));
                if (!allInputs.isEmpty()) {
                    WebElement ssnInput = allInputs.get(allInputs.size() - 1);
                    ssnInput.clear();
                    ssnInput.sendKeys(data.getSsn());
                    System.out.println("SSN filled successfully: " + data.getSsn());
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
     * Click element using XPath (proven to work in console)
     */
    private static boolean clickElementByXPath(WebDriver driver, String xpath) {
        try {
            System.out.println("Clicking element with XPath: " + xpath);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

            // Scroll to element first
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            Thread.sleep(1000);

            element.click();
            System.out.println("Successfully clicked element");
            return true;

        } catch (Exception e) {
            System.out.println("Failed to click element with XPath " + xpath + ": " + e.getMessage());

            // Fallback: try JavaScript click
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                WebElement element = driver.findElement(By.xpath(xpath));
                js.executeScript("arguments[0].click();", element);
                System.out.println("Successfully clicked element using JavaScript");
                return true;
            } catch (Exception jsEx) {
                System.out.println("JavaScript click also failed: " + jsEx.getMessage());
                return false;
            }
        }
    }

    /**
     * Fill element using XPath
     */
    private static boolean fillElementByXPath(WebDriver driver, String xpath, String value) {
        try {
            System.out.println("Filling element with XPath: " + xpath + " with value: " + value);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));

            element.clear();
            element.sendKeys(value);
            System.out.println("Successfully filled element");
            return true;

        } catch (Exception e) {
            System.out.println("Failed to fill element with XPath " + xpath + ": " + e.getMessage());

            // Fallback: try JavaScript
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                        "var element = document.evaluate(\"" + xpath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; " +
                                "if (element) { " +
                                "  element.value = '" + value.replace("'", "\\'") + "'; " +
                                "  element.dispatchEvent(new Event('input')); " +
                                "  element.dispatchEvent(new Event('change')); " +
                                "}"
                );
                System.out.println("Successfully filled element using JavaScript");
                return true;
            } catch (Exception jsEx) {
                System.out.println("JavaScript fill also failed: " + jsEx.getMessage());
                return false;
            }
        }
    }

    /**
     * Click button by text content
     */
    private static boolean clickButtonByText(WebDriver driver, String buttonText) {
        try {
            System.out.println("Clicking button with text: " + buttonText);

            String xpath = "//button[contains(., '" + buttonText + "')]";
            WebElement button = driver.findElement(By.xpath(xpath));

            // Scroll to button
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", button);
            Thread.sleep(1000);

            button.click();
            System.out.println("Successfully clicked button: " + buttonText);
            return true;

        } catch (Exception e) {
            System.out.println("Failed to click button " + buttonText + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click the newest mat-option with specific ID suffix
     */
    private static boolean clickNewestMatOption(WebDriver driver, String optionNumber) {
        try {
            String optionId = "mat-option-" + optionNumber;
            System.out.println("Clicking newest mat-option: " + optionId);

            // Wait a bit for dropdown to open
            Thread.sleep(1000);

            WebElement option = driver.findElement(By.xpath("//*[@id='" + optionId + "']"));
            option.click();
            System.out.println("Successfully clicked option: " + optionId);
            return true;

        } catch (Exception e) {
            System.out.println("Failed to click option " + optionNumber + ": " + e.getMessage());
            return false;
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