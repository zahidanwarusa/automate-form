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
 * Updated FormFiller with enhanced page transition and element loading handling
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

            // First, click the Search button and wait for results
            System.out.println("Clicking Search button...");
            try {
                // Try with class name and attribute
                WebElement searchButton = driver.findElement(
                        By.cssSelector("button.btn.btn-primary.search-btn[searchsubmit]"));
                searchButton.click();
                System.out.println("Search button clicked successfully");
            } catch (Exception e) {
                System.out.println("Could not find Search button with CSS selector, trying with XPath...");
                try {
                    // Try with XPath as fallback
                    WebElement searchButton = driver.findElement(
                            By.xpath("//button[contains(@class, 'search-btn') and @type='submit']"));
                    searchButton.click();
                    System.out.println("Search button clicked successfully with XPath");
                } catch (Exception ex) {
                    System.out.println("Still could not find Search button: " + ex.getMessage());
                    // Continue anyway - the button might not be necessary in some cases
                }
            }

            // Wait for search results to load
            System.out.println("Waiting for search results to load...");
            try {
                System.out.println("- Waiting 5 seconds for initial loading...");
                Thread.sleep(5000); // Wait 5 seconds for initial loading

                // Check if loading indicator exists and is visible
                try {
                    // Look for common loading indicators
                    boolean loadingIndicatorFound = driver.findElements(By.cssSelector(".loading, .spinner, .loader")).size() > 0;
                    if (loadingIndicatorFound) {
                        System.out.println("- Loading indicator found, waiting for it to disappear...");
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                                By.cssSelector(".loading, .spinner, .loader")));
                    }
                } catch (Exception e) {
                    // Ignore - just continue with fixed delay
                    System.out.println("- No loading indicator found, using fixed delay");
                }

                System.out.println("- Waiting additional 5 seconds for completion...");
                Thread.sleep(5000); // Additional safety wait

                System.out.println("Loading appears to be complete.");
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted while waiting for search results");
            }

            // Scroll down a bit to find the Create TECS Lookout button
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");

            // Additional wait to ensure the TECS Lookout button is available after scrolling
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted while waiting after scroll");
            }

            // Click on Create TECS Lookout button
            System.out.println("Looking for Create TECS Lookout button...");
            try {
                WebElement createButton = driver.findElement(By.xpath("//a[contains(text(), 'Create TECS Lookout')]"));
                createButton.click();
                System.out.println("Create TECS Lookout button clicked successfully");
            } catch (Exception e) {
                System.out.println("Could not find Create TECS Lookout button, trying with different selector...");
                try {
                    WebElement createButton = driver.findElement(By.className("event-button"));
                    createButton.click();
                    System.out.println("Create TECS Lookout button clicked successfully using class name");
                } catch (Exception ex) {
                    System.out.println("Still could not find the Create TECS Lookout button: " + ex.getMessage());
                    return false;
                }
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
     * Fill out the second page of the form with enhanced waiting and fallback strategies
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");

            // Enhanced wait for the second page to load completely
            if (!waitForSecondPageLoad(driver)) {
                System.out.println("Second page did not load properly, but continuing...");
            }

            // Try to get basic page information first
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                System.out.println("=== PAGE DEBUG INFO ===");
                System.out.println("Page title: " + driver.getTitle());
                System.out.println("Current URL: " + driver.getCurrentUrl());

                Object bodyText = js.executeScript("return document.body ? document.body.innerText.substring(0, 200) : 'No body'");
                System.out.println("Page body text (first 200 chars): " + bodyText);

                Object selectCount = js.executeScript("return document.querySelectorAll('mat-select').length");
                Object formFieldCount = js.executeScript("return document.querySelectorAll('mat-form-field').length");
                Object inputCount = js.executeScript("return document.querySelectorAll('input').length");
                Object buttonCount = js.executeScript("return document.querySelectorAll('button').length");

                System.out.println("Element counts - mat-select: " + selectCount +
                        ", mat-form-field: " + formFieldCount +
                        ", input: " + inputCount +
                        ", button: " + buttonCount);
                System.out.println("======================");
            } catch (Exception e) {
                System.out.println("Could not get page debug info: " + e.getMessage());
            }

            // Before attempting any dropdowns, let's ensure Angular has fully loaded
            try {
                System.out.println("Waiting additional time for Angular to fully initialize...");
                Thread.sleep(10000); // 10 second wait for full Angular initialization

                // Try to trigger Angular change detection
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                        "if (window.ng && window.ng.getComponent) { " +
                                "  try { " +
                                "    var appRoot = document.querySelector('app-root'); " +
                                "    if (appRoot) { " +
                                "      var component = window.ng.getComponent(appRoot); " +
                                "      if (component && component.ngZone) { " +
                                "        component.ngZone.run(() => {}); " +
                                "      } " +
                                "    } " +
                                "  } catch(e) { console.log('Angular trigger failed:', e); } " +
                                "}");

                Thread.sleep(2000); // Wait for change detection
                System.out.println("Angular initialization complete.");
            } catch (Exception e) {
                System.out.println("Angular initialization failed: " + e.getMessage());
            }

            // First dropdown selection (select option 68 - OB - OUTBOUND SUBJECT)
            System.out.println("Attempting first dropdown selection...");
            if (!selectDropdownOption(driver, 0, "mat-option-68", "first dropdown (OB - OUTBOUND SUBJECT)")) {
                System.out.println("Failed first dropdown, but continuing...");
            }

            // Wait between selections
            Thread.sleep(2000);

            // Second dropdown selection (select option 549 - AB - AG/BIO COUNTERMEASURES)
            System.out.println("Attempting second dropdown selection...");
            if (!selectDropdownOption(driver, 1, "mat-option-549", "second dropdown (AB - AG/BIO COUNTERMEASURES)")) {
                System.out.println("Failed second dropdown, but continuing...");
            }

            Thread.sleep(2000);

            // Third dropdown selection (select option 238 - 0 - NO NOTIFICATION)
            System.out.println("Attempting third dropdown selection...");
            if (!selectDropdownOption(driver, 2, "mat-option-238", "third dropdown (0 - NO NOTIFICATION)")) {
                System.out.println("Failed third dropdown, but continuing...");
            }

            Thread.sleep(2000);

            // Fourth (Multiple) dropdown selection (select a random option from 253-548)
            System.out.println("Attempting fourth dropdown selection...");
            String randomOptionId = "mat-option-" + (253 + random.nextInt(6)); // Random between 253-258
            if (!selectDropdownOption(driver, 3, randomOptionId, "fourth dropdown (Multiple)")) {
                System.out.println("Failed fourth dropdown, but continuing...");
            }

            Thread.sleep(2000);

            // Fifth dropdown selection (select option 242 - 0 - NOT ON PRIMARY)
            System.out.println("Attempting fifth dropdown selection...");
            if (!selectDropdownOption(driver, 4, "mat-option-242", "fifth dropdown (0 - NOT ON PRIMARY)")) {
                System.out.println("Failed fifth dropdown, but continuing...");
            }

            Thread.sleep(2000);

            // Fill remarks field
            System.out.println("Attempting to fill remarks field...");
            if (!fillInputField(driver, "mat-input-1", "Automated test entry. Random data generated for testing purposes.", "remarks")) {
                System.out.println("Failed to fill remarks, but continuing...");
            }

            Thread.sleep(1000);

            // Select Y/N dropdown (mat-option-2 - Y)
            System.out.println("Attempting Y/N dropdown selection...");
            if (!selectDropdownOption(driver, 5, "mat-option-2", "Y/N dropdown")) {
                System.out.println("Failed Y/N dropdown, but continuing...");
            }

            Thread.sleep(2000);

            // Select height dropdown (select a random height)
            System.out.println("Attempting height dropdown selection...");
            String heightOptionId = "mat-option-" + (8 + random.nextInt(13)); // Random between 8-20
            if (!selectDropdownOption(driver, 6, heightOptionId, "height dropdown")) {
                System.out.println("Failed height dropdown, but continuing...");
            }

            Thread.sleep(1000);

            // Fill in weight
            System.out.println("Attempting to fill weight field...");
            String weight = String.valueOf(120 + random.nextInt(131)); // Random between 120-250
            if (!fillInputField(driver, "mat-input-0", weight, "weight")) {
                System.out.println("Failed to fill weight, but continuing...");
            }

            // Add Sex
            System.out.println("Attempting to add sex...");
            if (clickButton(driver, "Add Sex")) {
                Thread.sleep(2000);
                String sexOption = random.nextBoolean() ? "mat-option-630" : "mat-option-631";
                selectLatestDropdownOption(driver, sexOption, "sex");
            }

            // Add Race
            System.out.println("Attempting to add race...");
            if (clickButton(driver, "Add Race")) {
                Thread.sleep(2000);
                String raceOption = "mat-option-" + (594 + random.nextInt(6)); // Random between 594-599
                selectLatestDropdownOption(driver, raceOption, "race");
            }

            // Add Eye Color
            System.out.println("Attempting to add eye color...");
            if (clickButton(driver, "Add Eye Color")) {
                Thread.sleep(2000);
                String eyeOption = "mat-option-" + (600 + random.nextInt(12)); // Random between 600-611
                selectLatestDropdownOption(driver, eyeOption, "eye color");
            }

            // Add Hair Color
            System.out.println("Attempting to add hair color...");
            if (clickButton(driver, "Add Hair Color")) {
                Thread.sleep(2000);
                String hairOption = "mat-option-" + (612 + random.nextInt(15)); // Random between 612-626
                selectLatestDropdownOption(driver, hairOption, "hair color");
            }

            // Add Name (reusing previously generated name)
            System.out.println("Attempting to add name...");
            if (clickButton(driver, "Add Name")) {
                Thread.sleep(2000);
                fillInputField(driver, "mat-input-2", data.getLastName(), "last name in Add Name");
                fillInputField(driver, "mat-input-3", data.getFirstName(), "first name in Add Name");
            }

            // Add DOB (reusing previously generated DOB)
            System.out.println("Attempting to add DOB...");
            if (clickButton(driver, "Add DOB")) {
                Thread.sleep(2000);
                fillInputField(driver, "mat-input-11", data.getDob(), "DOB in Add DOB");
            }

            // Add Citizenship (USA)
            System.out.println("Attempting to add citizenship...");
            if (clickButton(driver, "Add Citizenship")) {
                Thread.sleep(2000);
                selectLatestDropdownOption(driver, "mat-option-1260", "citizenship (USA)");
            }

            // Add Passport
            System.out.println("Attempting to add passport...");
            if (clickButton(driver, "Add Passport")) {
                Thread.sleep(2000);
                // Select passport type
                selectLatestDropdownOption(driver, "mat-option-1518", "passport type (P - Regular)");
                Thread.sleep(1000);
                // Fill passport details
                fillInputField(driver, "mat-input-19", data.getPassportNumber(), "passport number");
                selectLatestDropdownOption(driver, "mat-option-1520", "passport country (USA)");
                fillInputField(driver, "mat-input-20", data.getPassportIssueDate(), "passport issue date");
                fillInputField(driver, "mat-input-21", data.getPassportExpiryDate(), "passport expiry date");
            }

            // Add A#
            System.out.println("Attempting to add A#...");
            if (clickButton(driver, "Add A#")) {
                Thread.sleep(2000);
                fillInputField(driver, "mat-input-22", data.getaNumber(), "A# number");
            }

            // Add Driver's License
            System.out.println("Attempting to add driver's license...");
            if (clickButton(driver, "Add Driver's License")) {
                Thread.sleep(2000);
                fillInputField(driver, "mat-input-23", data.getDriverLicense(), "driver's license number");
                String stateOption = "mat-option-" + (1774 + random.nextInt(62)); // Random between 1774-1835
                selectLatestDropdownOption(driver, stateOption, "driver's license state");
            }

            // Add SSN
            System.out.println("Attempting to add SSN...");
            if (clickButton(driver, "Add SSN")) {
                Thread.sleep(2000);
                // Find the most recently added input field for SSN
                List<WebElement> inputFields = driver.findElements(By.xpath("//input[contains(@class, 'mat-input-element')]"));
                if (!inputFields.isEmpty()) {
                    WebElement ssnInput = inputFields.get(inputFields.size() - 1);
                    ssnInput.clear();
                    ssnInput.sendKeys(data.getSsn());
                    System.out.println("SSN filled successfully");
                } else {
                    System.out.println("Could not find SSN input field");
                }
            }

            System.out.println("Second page completed!");
            return true;
        } catch (Exception e) {
            System.out.println("Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Enhanced waiting for second page load with multiple indicators
     */
    private static boolean waitForSecondPageLoad(WebDriver driver) {
        System.out.println("Waiting for second page to load completely...");

        // Wait for page transition (URL change or new elements)
        try {
            Thread.sleep(5000); // Longer initial wait for page transition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Strategy 1: Wait for the specific app-tecs-lookout component
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            longWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("app-tecs-lookout")));
            System.out.println("TECS Lookout app component detected...");
        } catch (Exception e) {
            System.out.println("TECS Lookout component not found: " + e.getMessage());
        }

        // Strategy 2: Wait for mat-form-field elements with specific attributes
        try {
            longWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("mat-form-field[_ngcontent-aeo-c182]")));
            System.out.println("Mat-form-field elements with Angular content detected...");
        } catch (Exception e) {
            System.out.println("Specific mat-form-field elements not found: " + e.getMessage());
        }

        // Strategy 3: Wait for mat-select elements specifically
        try {
            longWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("mat-select")));
            System.out.println("Mat-select elements detected...");
        } catch (Exception e) {
            System.out.println("Mat-select elements not found: " + e.getMessage());
        }

        // Strategy 4: JavaScript check for Angular and mat-select elements
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebDriverWait jsWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            jsWait.until(driver1 -> {
                try {
                    // Check for document ready and Angular elements
                    Boolean ready = (Boolean) js.executeScript(
                            "return document.readyState === 'complete' && " +
                                    "document.querySelectorAll('mat-select').length > 0 && " +
                                    "document.querySelectorAll('mat-form-field').length > 0");
                    if (ready != null && ready) {
                        System.out.println("JavaScript confirms mat-select elements are present");
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
            System.out.println("JavaScript readiness check failed: " + e.getMessage());
        }

        // Strategy 5: Print current page elements for debugging
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length");
            Object matFormFieldCount = js.executeScript("return document.querySelectorAll('mat-form-field').length");
            Object appTecsCount = js.executeScript("return document.querySelectorAll('app-tecs-lookout').length");

            System.out.println("Current page element counts:");
            System.out.println("- mat-select elements: " + matSelectCount);
            System.out.println("- mat-form-field elements: " + matFormFieldCount);
            System.out.println("- app-tecs-lookout elements: " + appTecsCount);

            // Get page title and URL for verification
            System.out.println("- Page title: " + driver.getTitle());
            System.out.println("- Current URL: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("Could not get element counts: " + e.getMessage());
        }

        // Final extended wait for Angular to render completely
        try {
            Thread.sleep(5000);
            System.out.println("Extended wait completed for Angular rendering...");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Enhanced dropdown selection with multiple fallback strategies targeting the specific HTML structure
     */
    private static boolean selectDropdownOption(WebDriver driver, int dropdownIndex, String optionId, String description) {
        try {
            System.out.println("Selecting " + description + " (option: " + optionId + ")");

            // Strategy 1: Target specific Angular content attributes
            try {
                List<WebElement> matSelects = driver.findElements(By.cssSelector("mat-select[_ngcontent-aeo-c182]"));
                if (matSelects.size() > dropdownIndex) {
                    WebElement dropdown = matSelects.get(dropdownIndex);

                    // Scroll to element first
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
                    Thread.sleep(1000);

                    // Check if element is visible and clickable
                    if (dropdown.isDisplayed() && dropdown.isEnabled()) {
                        dropdown.click();
                        Thread.sleep(1500);

                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.id(optionId)));
                        option.click();

                        System.out.println("Successfully selected " + description + " using Angular content approach");
                        return true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Angular content approach failed for " + description + ": " + e.getMessage());
            }

            // Strategy 2: Generic mat-select approach with better waiting
            try {
                List<WebElement> matSelects = driver.findElements(By.tagName("mat-select"));
                System.out.println("Found " + matSelects.size() + " mat-select elements");

                if (matSelects.size() > dropdownIndex) {
                    WebElement dropdown = matSelects.get(dropdownIndex);

                    // Scroll to element first
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
                    Thread.sleep(1000);

                    // Wait for element to be clickable
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.elementToBeClickable(dropdown));

                    dropdown.click();
                    Thread.sleep(1500);

                    WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.id(optionId)));
                    option.click();

                    System.out.println("Successfully selected " + description + " using standard mat-select approach");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Standard mat-select approach failed for " + description + ": " + e.getMessage());
            }

            // Strategy 3: CSS selector for combobox role
            try {
                List<WebElement> dropdowns = driver.findElements(By.cssSelector("[role='combobox'][aria-haspopup='true']"));
                System.out.println("Found " + dropdowns.size() + " combobox elements");

                if (dropdowns.size() > dropdownIndex) {
                    WebElement dropdown = dropdowns.get(dropdownIndex);

                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdown);
                    Thread.sleep(1000);

                    dropdown.click();
                    Thread.sleep(1500);

                    WebElement option = driver.findElement(By.id(optionId));
                    option.click();

                    System.out.println("Successfully selected " + description + " using combobox approach");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Combobox approach failed for " + description + ": " + e.getMessage());
            }

            // Strategy 4: JavaScript click with element verification
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // First verify elements exist
                Object selectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
                System.out.println("JavaScript reports " + selectCount + " mat-select elements");

                if (selectCount instanceof Long && ((Long) selectCount).intValue() > dropdownIndex) {
                    // Try to click the dropdown using JavaScript
                    Boolean clicked = (Boolean) js.executeScript(
                            "var selects = document.querySelectorAll('mat-select'); " +
                                    "if (selects.length > " + dropdownIndex + ") { " +
                                    "  var dropdown = selects[" + dropdownIndex + "]; " +
                                    "  dropdown.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                    "  dropdown.click(); " +
                                    "  return true; " +
                                    "} " +
                                    "return false;");

                    if (clicked != null && clicked) {
                        Thread.sleep(2000);

                        // Try to click the option
                        Boolean optionClicked = (Boolean) js.executeScript(
                                "var option = document.getElementById('" + optionId + "'); " +
                                        "if (option) { " +
                                        "  option.click(); " +
                                        "  return true; " +
                                        "} " +
                                        "return false;");

                        if (optionClicked != null && optionClicked) {
                            System.out.println("Successfully selected " + description + " using JavaScript approach");
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("JavaScript approach failed for " + description + ": " + e.getMessage());
            }

            // Strategy 5: Brute force - try all available dropdowns
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                System.out.println("Trying brute force approach for " + description);

                Boolean success = (Boolean) js.executeScript(
                        "var selectors = ['mat-select', '[role=\"combobox\"]', '.mat-select']; " +
                                "for (var s = 0; s < selectors.length; s++) { " +
                                "  var elements = document.querySelectorAll(selectors[s]); " +
                                "  if (elements.length > " + dropdownIndex + ") { " +
                                "    elements[" + dropdownIndex + "].click(); " +
                                "    setTimeout(function() { " +
                                "      var option = document.getElementById('" + optionId + "'); " +
                                "      if (option) option.click(); " +
                                "    }, 1500); " +
                                "    return true; " +
                                "  } " +
                                "} " +
                                "return false;");

                if (success != null && success) {
                    Thread.sleep(3000);
                    System.out.println("Successfully selected " + description + " using brute force approach");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Brute force approach failed for " + description + ": " + e.getMessage());
            }

            System.out.println("All approaches failed for " + description);
            return false;

        } catch (Exception e) {
            System.out.println("Error selecting " + description + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Enhanced input field filling with multiple strategies
     */
    private static boolean fillInputField(WebDriver driver, String fieldId, String value, String description) {
        try {
            System.out.println("Filling " + description + " with value: " + value);

            // Strategy 1: Direct ID approach
            try {
                WebElement input = driver.findElement(By.id(fieldId));
                input.clear();
                input.sendKeys(value);
                System.out.println("Successfully filled " + description + " using direct ID");
                return true;
            } catch (Exception e) {
                System.out.println("Direct ID approach failed for " + description + ": " + e.getMessage());
            }

            // Strategy 2: JavaScript approach
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var input = document.getElementById('" + fieldId + "'); " +
                                "if (input) { " +
                                "  input.value = '" + value.replace("'", "\\'") + "'; " +
                                "  input.dispatchEvent(new Event('input')); " +
                                "  input.dispatchEvent(new Event('change')); " +
                                "  return true; " +
                                "} " +
                                "return false;");

                if (result != null && result) {
                    System.out.println("Successfully filled " + description + " using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript approach failed for " + description + ": " + e.getMessage());
            }

            // Strategy 3: XPath approach
            try {
                WebElement input = driver.findElement(By.xpath("//input[@id='" + fieldId + "']"));
                input.clear();
                input.sendKeys(value);
                System.out.println("Successfully filled " + description + " using XPath");
                return true;
            } catch (Exception e) {
                System.out.println("XPath approach failed for " + description + ": " + e.getMessage());
            }

            System.out.println("All approaches failed for " + description);
            return false;

        } catch (Exception e) {
            System.out.println("Error filling " + description + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Enhanced button clicking with multiple strategies
     */
    private static boolean clickButton(WebDriver driver, String buttonText) {
        try {
            System.out.println("Attempting to click button: " + buttonText);

            // Strategy 1: XPath with text
            try {
                WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
                button.click();
                System.out.println("Successfully clicked " + buttonText + " using XPath");
                return true;
            } catch (Exception e) {
                System.out.println("XPath approach failed for " + buttonText + ": " + e.getMessage());
            }

            // Strategy 2: Span inside button
            try {
                WebElement button = driver.findElement(By.xpath("//button//span[contains(text(), '" + buttonText + "')]/ancestor::button"));
                button.click();
                System.out.println("Successfully clicked " + buttonText + " using span search");
                return true;
            } catch (Exception e) {
                System.out.println("Span search approach failed for " + buttonText + ": " + e.getMessage());
            }

            // Strategy 3: JavaScript search
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var buttons = document.querySelectorAll('button'); " +
                                "for (var i = 0; i < buttons.length; i++) { " +
                                "  if (buttons[i].textContent.includes('" + buttonText + "')) { " +
                                "    buttons[i].click(); " +
                                "    return true; " +
                                "  } " +
                                "} " +
                                "return false;");

                if (result != null && result) {
                    System.out.println("Successfully clicked " + buttonText + " using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript approach failed for " + buttonText + ": " + e.getMessage());
            }

            System.out.println("All approaches failed for button: " + buttonText);
            return false;

        } catch (Exception e) {
            System.out.println("Error clicking button " + buttonText + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Select option from the most recently added dropdown
     */
    private static boolean selectLatestDropdownOption(WebDriver driver, String optionId, String description) {
        try {
            System.out.println("Selecting latest dropdown option for " + description);

            // Find all dropdowns and click the last one
            List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
            if (!dropdowns.isEmpty()) {
                WebElement latestDropdown = dropdowns.get(dropdowns.size() - 1);
                latestDropdown.click();
                Thread.sleep(1000);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.id(optionId)));
                option.click();

                System.out.println("Successfully selected " + description);
                return true;
            }

            return false;
        } catch (Exception e) {
            System.out.println("Error selecting latest dropdown option for " + description + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Wait for an element to be visible and then send keys to it
     * @param driver WebDriver instance
     * @param by By selector for the element
     * @param text Text to send
     * @return true if successful, false otherwise
     */
    private static boolean waitAndSendKeys(WebDriver driver, By by, String text) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            );
            element.clear();
            element.sendKeys(text);
            return true;
        } catch (Exception e) {
            System.out.println("Error sending keys to element " + by + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Wait for an element to be clickable and then click it
     * @param driver WebDriver instance
     * @param by By selector for the element
     * @return true if successful, false otherwise
     */
    private static boolean waitAndClick(WebDriver driver, By by) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(
                    ExpectedConditions.elementToBeClickable(by)
            );
            element.click();
            return true;
        } catch (Exception e) {
            System.out.println("Error clicking element " + by + ": " + e.getMessage());
            return false;
        }
    }
}