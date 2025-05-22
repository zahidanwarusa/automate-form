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
            System.out.println("Filling last name: " + data.getLastName());
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            // Fill in first name
            System.out.println("Filling first name: " + data.getFirstName());
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            // Fill in DOB
            System.out.println("Filling DOB: " + data.getDob());
            waitAndSendKeys(driver, By.id("dob"), data.getDob());

            // Click the SEARCH button first
            System.out.println("Clicking SEARCH button...");
            boolean searchClicked = false;

            // Try multiple approaches to find and click the search button
            try {
                // Method 1: Try by button text
                WebElement searchButton = driver.findElement(By.xpath("//button[contains(text(), 'Search')]"));
                searchButton.click();
                searchClicked = true;
                System.out.println("Search button clicked using text search");
            } catch (Exception e) {
                System.out.println("Search by text failed: " + e.getMessage());
            }

            if (!searchClicked) {
                try {
                    // Method 2: Try by class name
                    WebElement searchButton = driver.findElement(By.className("search-btn"));
                    searchButton.click();
                    searchClicked = true;
                    System.out.println("Search button clicked using class name");
                } catch (Exception e) {
                    System.out.println("Search by class failed: " + e.getMessage());
                }
            }

            if (!searchClicked) {
                try {
                    // Method 3: Try by CSS selector
                    WebElement searchButton = driver.findElement(By.cssSelector("button.btn.btn-primary"));
                    searchButton.click();
                    searchClicked = true;
                    System.out.println("Search button clicked using CSS selector");
                } catch (Exception e) {
                    System.out.println("Search by CSS failed: " + e.getMessage());
                }
            }

            if (!searchClicked) {
                try {
                    // Method 4: Try by type submit
                    WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
                    searchButton.click();
                    searchClicked = true;
                    System.out.println("Search button clicked using submit type");
                } catch (Exception e) {
                    System.out.println("Search by submit type failed: " + e.getMessage());
                }
            }

            if (!searchClicked) {
                System.out.println("Could not find or click search button. Trying to continue anyway...");
            }

            // Wait for search results to load
            System.out.println("Waiting for search results to load...");
            Thread.sleep(8000); // Wait 8 seconds for search to complete

            // Check if there's a loading indicator and wait for it to disappear
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".loading, .spinner, .loader, .mat-spinner")));
                System.out.println("Loading completed");
            } catch (Exception e) {
                System.out.println("No loading indicator found or timeout waiting for loading to complete");
            }

            // Additional wait to ensure search results are fully loaded
            Thread.sleep(3000);

            // Scroll down to find the Create TECS Lookout button
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");
            Thread.sleep(2000);

            // Store the current window handle before clicking the button
            String originalWindow = driver.getWindowHandle();
            System.out.println("Original window handle: " + originalWindow);
            System.out.println("Current windows before click: " + driver.getWindowHandles().size());

            // Now look for the Create TECS Lookout button
            System.out.println("Looking for Create TECS Lookout button...");
            boolean createButtonClicked = false;

            try {
                // Method 1: Try by link text
                WebElement createButton = driver.findElement(By.xpath("//a[contains(text(), 'Create TECS Lookout')]"));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", createButton);
                Thread.sleep(2000);
                createButton.click();
                createButtonClicked = true;
                System.out.println("Create TECS Lookout button clicked using link text");
            } catch (Exception e) {
                System.out.println("Create button by link text failed: " + e.getMessage());
            }

            if (!createButtonClicked) {
                try {
                    // Method 2: Try by class name
                    WebElement createButton = driver.findElement(By.className("event-button"));
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", createButton);
                    Thread.sleep(2000);
                    createButton.click();
                    createButtonClicked = true;
                    System.out.println("Create TECS Lookout button clicked using class name");
                } catch (Exception e) {
                    System.out.println("Create button by class failed: " + e.getMessage());
                }
            }

            if (!createButtonClicked) {
                try {
                    // Method 3: Try by span text inside link
                    WebElement createButton = driver.findElement(By.xpath("//span[contains(text(), 'Create TECS Lookout')]/parent::a"));
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", createButton);
                    Thread.sleep(2000);
                    createButton.click();
                    createButtonClicked = true;
                    System.out.println("Create TECS Lookout button clicked using span text");
                } catch (Exception e) {
                    System.out.println("Create button by span text failed: " + e.getMessage());
                }
            }

            if (!createButtonClicked) {
                System.out.println("Could not find Create TECS Lookout button");
                return false;
            }

            // Wait for new tab/window to open
            System.out.println("Waiting for new tab to open...");
            Thread.sleep(5000);

            // Check if a new window/tab was opened
            if (driver.getWindowHandles().size() > 1) {
                System.out.println("New tab detected! Switching to new tab...");

                // Switch to the new tab
                for (String windowHandle : driver.getWindowHandles()) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        System.out.println("Switched to new tab: " + windowHandle);
                        break;
                    }
                }

                // Wait for new tab to load
                Thread.sleep(3000);

                System.out.println("New tab URL: " + driver.getCurrentUrl());
                System.out.println("New tab title: " + driver.getTitle());

            } else {
                System.out.println("No new tab opened, staying in current tab");
                System.out.println("Current URL: " + driver.getCurrentUrl());
                System.out.println("Current title: " + driver.getTitle());
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
     * Fill out the second page using direct XPath selectors by ID
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");

            // Wait for second page to load and verify elements exist
            if (!waitForSecondPageElements(driver)) {
                System.out.println("Second page elements not found, but continuing...");
            }

            // Get current page info
            JavascriptExecutor js = (JavascriptExecutor) driver;
            System.out.println("=== SECOND PAGE DEBUG INFO ===");
            System.out.println("URL: " + driver.getCurrentUrl());
            System.out.println("Title: " + driver.getTitle());

            Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
            System.out.println("Mat-select elements found: " + matSelectCount);

            // List all mat-select elements with their IDs
            js.executeScript(
                    "console.log('=== ALL MAT-SELECT ELEMENTS ==='); " +
                            "var selects = document.querySelectorAll('mat-select'); " +
                            "for (var i = 0; i < selects.length; i++) { " +
                            "  console.log('Index ' + i + ': ID=' + selects[i].id + ', Classes=' + selects[i].className.substring(0,50)); " +
                            "}"
            );
            System.out.println("Check browser console for mat-select element details");
            System.out.println("==============================");

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
     * Wait for second page elements to load and verify they exist
     */
    private static boolean waitForSecondPageElements(WebDriver driver) {
        System.out.println("Waiting for second page elements to load...");

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Wait up to 30 seconds for elements to appear
        for (int i = 0; i < 30; i++) {
            try {
                Thread.sleep(1000);

                Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
                System.out.println("Attempt " + (i+1) + ": Found " + matSelectCount + " mat-select elements");

                // Check for specific elements we need
                Boolean hasRequiredElements = (Boolean) js.executeScript(
                        "return $x(\"//*[@id='mat-select-4']\").length > 0 || " +
                                "       document.querySelectorAll('mat-select').length >= 5;"
                );

                if (hasRequiredElements != null && hasRequiredElements) {
                    System.out.println("Required elements found after " + (i+1) + " seconds");
                    return true;
                }

            } catch (Exception e) {
                System.out.println("Error during wait: " + e.getMessage());
            }
        }

        System.out.println("Timeout waiting for second page elements");
        return false;
    }

    /**
     * Click element using XPath with enhanced strategies for Angular Material components
     */
    private static boolean clickElementByXPath(WebDriver driver, String xpath) {
        try {
            System.out.println("Clicking element with XPath: " + xpath);

            // First, let's check if we're on the right page
            JavascriptExecutor js = (JavascriptExecutor) driver;
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Check if the element exists at all
            Object elementCount = js.executeScript("return $x(\"" + xpath + "\").length;");
            System.out.println("Elements found by JavaScript: " + elementCount);

            if (elementCount.equals(0L)) {
                System.out.println("Element not found by JavaScript either. Checking page state...");

                // Debug what elements are actually on the page
                Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
                System.out.println("Total mat-select elements on page: " + matSelectCount);

                // List all mat-select IDs
                js.executeScript(
                        "var selects = document.querySelectorAll('mat-select'); " +
                                "for (var i = 0; i < selects.length; i++) { " +
                                "  console.log('mat-select ' + i + ': id=' + selects[i].id + ', classes=' + selects[i].className); " +
                                "}"
                );

                return false;
            }

            // For mat-select elements, use specific clicking strategies
            if (xpath.contains("mat-select")) {
                return clickMatSelectElement(driver, xpath);
            }

            // For regular elements, try standard approaches
            return clickRegularElement(driver, xpath);

        } catch (Exception e) {
            System.out.println("Error in clickElementByXPath: " + e.getMessage());
            return false;
        }
    }

    /**
     * Specialized method for clicking mat-select elements
     */
    private static boolean clickMatSelectElement(WebDriver driver, String xpath) {
        try {
            System.out.println("Using mat-select specific clicking strategies...");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Strategy 1: Click the mat-select-trigger (the actual clickable area)
            try {
                String triggerXpath = xpath + "//div[@class and contains(@class, 'mat-select-trigger')]";
                System.out.println("Trying to click mat-select-trigger: " + triggerXpath);

                WebElement trigger = driver.findElement(By.xpath(triggerXpath));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", trigger);
                Thread.sleep(1000);
                trigger.click();
                System.out.println("Successfully clicked mat-select-trigger");
                return true;

            } catch (Exception e) {
                System.out.println("Mat-select-trigger click failed: " + e.getMessage());
            }

            // Strategy 2: Click the mat-select-value div
            try {
                String valueXpath = xpath + "//div[@class and contains(@class, 'mat-select-value')]";
                System.out.println("Trying to click mat-select-value: " + valueXpath);

                WebElement valueDiv = driver.findElement(By.xpath(valueXpath));
                valueDiv.click();
                System.out.println("Successfully clicked mat-select-value");
                return true;

            } catch (Exception e) {
                System.out.println("Mat-select-value click failed: " + e.getMessage());
            }

            // Strategy 3: JavaScript click on mat-select-trigger
            try {
                Boolean jsResult = (Boolean) js.executeScript(
                        "var matSelect = $x(\"" + xpath + "\")[0]; " +
                                "if (matSelect) { " +
                                "  var trigger = matSelect.querySelector('.mat-select-trigger'); " +
                                "  if (trigger) { " +
                                "    trigger.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "    trigger.click(); " +
                                "    return true; " +
                                "  } " +
                                "} " +
                                "return false;"
                );

                if (jsResult != null && jsResult) {
                    System.out.println("Successfully clicked mat-select-trigger using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript mat-select-trigger click failed: " + e.getMessage());
            }

            // Strategy 4: Use Angular Material's programmatic approach
            try {
                Boolean angularResult = (Boolean) js.executeScript(
                        "var matSelect = $x(\"" + xpath + "\")[0]; " +
                                "if (matSelect) { " +
                                "  // Try to trigger the mat-select open programmatically " +
                                "  var event = new MouseEvent('click', { " +
                                "    bubbles: true, " +
                                "    cancelable: true, " +
                                "    view: window " +
                                "  }); " +
                                "  matSelect.dispatchEvent(event); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (angularResult != null && angularResult) {
                    System.out.println("Successfully clicked mat-select using Angular event");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Angular mat-select click failed: " + e.getMessage());
            }

            // Strategy 5: Force focus and space key (simulates keyboard interaction)
            try {
                WebElement matSelect = driver.findElement(By.xpath(xpath));
                matSelect.click(); // Try regular click first
                Thread.sleep(500);

                // If that doesn't work, try sending SPACE key (opens dropdowns)
                matSelect.sendKeys(" ");
                System.out.println("Successfully opened mat-select using space key");
                return true;

            } catch (Exception e) {
                System.out.println("Focus and space key approach failed: " + e.getMessage());
            }

            System.out.println("All mat-select click strategies failed");
            return false;

        } catch (Exception e) {
            System.out.println("Error in clickMatSelectElement: " + e.getMessage());
            return false;
        }
    }

    /**
     * Standard clicking approach for non-mat-select elements
     */
    private static boolean clickRegularElement(WebDriver driver, String xpath) {
        try {
            // Strategy 1: Standard Selenium click
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

                // Scroll to element first
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                Thread.sleep(1000);

                element.click();
                System.out.println("Successfully clicked element directly");
                return true;

            } catch (Exception e) {
                System.out.println("Direct click failed: " + e.getMessage());
            }

            // Strategy 2: JavaScript click
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean jsResult = (Boolean) js.executeScript(
                        "var element = $x(\"" + xpath + "\")[0]; " +
                                "if (element) { " +
                                "  element.click(); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (jsResult != null && jsResult) {
                    System.out.println("Successfully clicked using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript click failed: " + e.getMessage());
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error in clickRegularElement: " + e.getMessage());
            return false;
        }
    }

    /**
     * Enhanced input field filling with special handling for date fields
     */
    private static boolean fillElementByXPath(WebDriver driver, String xpath, String value, String description) {
        try {
            System.out.println("Filling " + description + " with value: " + value);

            // Check if this is a date field (has mask="00/00/0000")
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Boolean isDateField = (Boolean) js.executeScript(
                    "var element = $x(\"" + xpath + "\")[0]; " +
                            "return element && element.getAttribute('mask') === '00/00/0000';"
            );

            if (isDateField != null && isDateField) {
                return fillDateField(driver, xpath, value, description);
            } else {
                return fillRegularField(driver, xpath, value, description);
            }

        } catch (Exception e) {
            System.out.println("Error filling " + description + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Fill element using XPath (overloaded method for backward compatibility)
     */
    private static boolean fillElementByXPath(WebDriver driver, String xpath, String value) {
        return fillElementByXPath(driver, xpath, value, "field");
    }

    /**
     * Specialized method for filling date fields with mat-datepicker
     */
    private static boolean fillDateField(WebDriver driver, String xpath, String value, String description) {
        try {
            System.out.println("Filling date field " + description + " with special handling...");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Strategy 1: Direct input fill (works for most date fields)
            try {
                WebElement input = driver.findElement(By.xpath(xpath));

                // Clear the field first
                input.clear();
                Thread.sleep(500);

                // Type the date
                input.sendKeys(value);
                Thread.sleep(500);

                // Trigger change events
                js.executeScript(
                        "var element = arguments[0]; " +
                                "element.dispatchEvent(new Event('input', {bubbles: true})); " +
                                "element.dispatchEvent(new Event('change', {bubbles: true}));",
                        input
                );

                System.out.println("Successfully filled date field using direct input");
                return true;

            } catch (Exception e) {
                System.out.println("Direct date input failed: " + e.getMessage());
            }

            // Strategy 2: JavaScript value setting with Angular events
            try {
                Boolean jsResult = (Boolean) js.executeScript(
                        "var input = $x(\"" + xpath + "\")[0]; " +
                                "if (input) { " +
                                "  input.value = '" + value.replace("'", "\\'") + "'; " +
                                "  // Trigger Angular-specific events " +
                                "  input.dispatchEvent(new Event('input', {bubbles: true})); " +
                                "  input.dispatchEvent(new Event('change', {bubbles: true})); " +
                                "  input.dispatchEvent(new Event('blur', {bubbles: true})); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (jsResult != null && jsResult) {
                    System.out.println("Successfully filled date field using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript date fill failed: " + e.getMessage());
            }

            // Strategy 3: Try using the datepicker button if direct input fails
            try {
                // Find the datepicker toggle button
                String toggleXpath = xpath + "/following-sibling::*//mat-datepicker-toggle//button";
                WebElement toggleButton = driver.findElement(By.xpath(toggleXpath));

                System.out.println("Found datepicker toggle, opening calendar...");
                toggleButton.click();
                Thread.sleep(2000);

                // For now, let's close the calendar and use direct input
                // (Calendar navigation would be complex to implement)
                js.executeScript("document.activeElement.blur();");
                Thread.sleep(1000);

                // Try direct input again after opening/closing calendar
                WebElement input = driver.findElement(By.xpath(xpath));
                input.clear();
                input.sendKeys(value);

                System.out.println("Successfully filled date field after calendar interaction");
                return true;

            } catch (Exception e) {
                System.out.println("Datepicker toggle approach failed: " + e.getMessage());
            }

            System.out.println("All date field strategies failed");
            return false;

        } catch (Exception e) {
            System.out.println("Error in fillDateField: " + e.getMessage());
            return false;
        }
    }

    /**
     * Standard field filling for non-date fields
     */
    private static boolean fillRegularField(WebDriver driver, String xpath, String value, String description) {
        try {
            // Strategy 1: Direct XPath approach
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));

                element.clear();
                element.sendKeys(value);
                System.out.println("Successfully filled " + description + " using direct method");
                return true;

            } catch (Exception e) {
                System.out.println("Direct fill failed for " + description + ": " + e.getMessage());
            }

            // Strategy 2: JavaScript approach
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var element = $x(\"" + xpath + "\")[0]; " +
                                "if (element) { " +
                                "  element.value = '" + value.replace("'", "\\'") + "'; " +
                                "  element.dispatchEvent(new Event('input')); " +
                                "  element.dispatchEvent(new Event('change')); " +
                                "  return true; " +
                                "} " +
                                "return false;");

                if (result != null && result) {
                    System.out.println("Successfully filled " + description + " using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript fill failed for " + description + ": " + e.getMessage());
            }

            System.out.println("All fill strategies failed for " + description);
            return false;

        } catch (Exception e) {
            System.out.println("Error in fillRegularField: " + e.getMessage());
            return false;
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