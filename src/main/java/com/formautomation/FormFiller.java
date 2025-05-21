package com.formautomation;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class FormFiller {

    /**
     * Fill out the first page of the form
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out first page...");

            // Wait for the page to load and elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Click the Search button to initiate the workflow
            System.out.println("Clicking Search button...");
            WebElement searchButton = findClickableElement(driver, By.xpath("//button[contains(text(), 'Search')]"));
            if (searchButton != null) {
                searchButton.click();
                System.out.println("Search button clicked successfully");
            } else {
                System.out.println("Search button not found");
                return false;
            }

            // Wait for search results to load
            System.out.println("Waiting for search results to load...");

            // Wait for initial loading
            System.out.println("- Waiting 5 seconds for initial loading...");
            Thread.sleep(5000);

            // Check for loading indicator and wait for it to disappear if present
            List<WebElement> loadingIndicators = driver.findElements(By.cssSelector(".loading-indicator, .spinner, .mat-progress-spinner"));
            if (!loadingIndicators.isEmpty()) {
                System.out.println("- Loading indicator found, waiting for it to disappear...");
                wait.until(ExpectedConditions.invisibilityOfAllElements(loadingIndicators));
                System.out.println("- Waiting additional 5 seconds for completion...");
                Thread.sleep(5000);
            }

            System.out.println("Loading appears to be complete.");

            // Look for the "Create TECS Lookout" button
            System.out.println("Looking for Create TECS Lookout button...");
            WebElement createButton = findClickableElement(driver, By.xpath("//a[contains(text(), 'Create TECS Lookout')]"));

            if (createButton == null) {
                // Try alternative selectors
                createButton = findClickableElement(driver, By.cssSelector(".event-button"));

                if (createButton == null) {
                    // Try JavaScript to find and click the button by text content
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    Boolean result = (Boolean) js.executeScript(
                            "var links = document.querySelectorAll('a');" +
                                    "for (var i = 0; i < links.length; i++) {" +
                                    "  if (links[i].textContent.includes('Create TECS Lookout')) {" +
                                    "    links[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    if (result) {
                        System.out.println("Create TECS Lookout button clicked using JavaScript");
                    } else {
                        System.out.println("Create TECS Lookout button not found");
                        return false;
                    }
                } else {
                    createButton.click();
                    System.out.println("Create TECS Lookout button clicked using CSS selector");
                }
            } else {
                createButton.click();
                System.out.println("Create TECS Lookout button clicked successfully");
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
     * Fill out the second page of the form
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");

            // First, check if we're in a new window or tab
            Set<String> windowHandles = driver.getWindowHandles();
            if (windowHandles.size() > 1) {
                System.out.println("Detected multiple windows/tabs. Switching to the new one...");
                String originalWindow = driver.getWindowHandle();

                // Switch to the new window/tab (the one that's not the current one)
                for (String windowHandle : windowHandles) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        System.out.println("Switched to new window/tab: " + driver.getTitle());
                        break;
                    }
                }
            }

            // Wait longer for Angular application to load fully
            System.out.println("Waiting for Angular application to stabilize...");
            Thread.sleep(10000); // Wait 10 seconds for the page to fully load

            // Check for iframes
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            if (!iframes.isEmpty()) {
                System.out.println("Found " + iframes.size() + " iframes, switching to first one");
                driver.switchTo().frame(0);
            }

            // Initialize JavaScript executor for direct DOM manipulation
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Print page details for debugging
            System.out.println("Page Title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());

            // Check for Angular using JavaScript
            Boolean hasAngular = false;
            try {
                hasAngular = (Boolean) js.executeScript(
                        "return (window.getAllAngularRootElements !== undefined) && (window.getAllAngularRootElements().length > 0);");
                System.out.println("Angular detected on page: " + hasAngular);
            } catch (Exception e) {
                System.out.println("Error checking for Angular: " + e.getMessage());
            }

            // Dump page structure for debugging
            System.out.println("Analyzing page structure...");
            analyzePageStructure(js);

            // APPROACH: Direct JavaScript interaction with mat-select elements

            // First dropdown - Subject Type (OB - OUTBOUND SUBJECT)
            selectOptionByJavaScript(js, 0, "mat-option-68", "First dropdown (Subject Type)");

            // Second dropdown - Category (AB - AG/BIO COUNTERMEASURES)
            selectOptionByJavaScript(js, 1, "mat-option-549", "Second dropdown (Category)");

            // Third dropdown - Notification (0 - NO NOTIFICATION)
            selectOptionByJavaScript(js, 2, "mat-option-238", "Third dropdown (Notification)");

            // Fourth dropdown - Multiple selection (PVRVK - PROVISIONAL REVOCATION)
            try {
                Boolean multiSelectResult = (Boolean) js.executeScript(
                        "var matSelects = document.querySelectorAll('[role=\"combobox\"][aria-multiselectable=\"true\"]');" +
                                "if (matSelects.length > 0) {" +
                                "  matSelects[0].click();" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Fourth dropdown (Multiple Select) clicked: " + multiSelectResult);

                if (multiSelectResult) {
                    Thread.sleep(1000);

                    Boolean optionResult = (Boolean) js.executeScript(
                            "var option = document.getElementById('mat-option-253');" +
                                    "if (option) {" +
                                    "  option.click();" +
                                    "  return true;" +
                                    "}" +
                                    "return false;");

                    System.out.println("Multiple Select option selected: " + optionResult);

                    // Click elsewhere to close dropdown
                    js.executeScript("document.body.click();");
                }
            } catch (Exception e) {
                System.out.println("Error with fourth dropdown: " + e.getMessage());
            }

            // Fifth dropdown - Primary (0 - NOT ON PRIMARY)
            selectOptionByJavaScript(js, 4, "mat-option-242", "Fifth dropdown (Primary)");

            // Fill remarks textarea
            fillTextAreaByJavaScript(js, "mat-input-1", "Automated test entry for " + data.getFirstName() + " " + data.getLastName() + ". Generated for testing purposes.");

            // ADD USER ATTRIBUTES

            // Add Sex
            addAttributeAndSelect(js, "Add Sex", "mat-option-630", "Sex (Female)");

            // Add Race
            addAttributeAndSelect(js, "Add Race", "mat-option-594", "Race (Asian)");

            // Add Eye Color
            addAttributeAndSelect(js, "Add Eye Color", "mat-option-600", "Eye Color (Blue/Green)");

            // Add Hair Color
            addAttributeAndSelect(js, "Add Hair Color", "mat-option-612", "Hair Color (Bald)");

            // Add Name with data from first page
            try {
                Boolean addNameResult = (Boolean) js.executeScript(
                        "var buttons = Array.from(document.querySelectorAll('button'));" +
                                "var addNameButton = buttons.find(button => button.textContent.includes('Add Name'));" +
                                "if (addNameButton) {" +
                                "  addNameButton.click();" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Add Name button clicked: " + addNameResult);

                if (addNameResult) {
                    Thread.sleep(1000);

                    // Fill last name and first name inputs
                    Boolean lastNameResult = (Boolean) js.executeScript(
                            "var inputs = document.querySelectorAll('input[maxlength=\"50\"]');" +
                                    "if (inputs.length > 0) {" +
                                    "  inputs[0].value = '" + data.getLastName() + "';" +
                                    "  var event = new Event('input', { bubbles: true });" +
                                    "  inputs[0].dispatchEvent(event);" +
                                    "  return true;" +
                                    "}" +
                                    "return false;");

                    Boolean firstNameResult = (Boolean) js.executeScript(
                            "var inputs = document.querySelectorAll('input[maxlength=\"50\"]');" +
                                    "if (inputs.length > 1) {" +
                                    "  inputs[1].value = '" + data.getFirstName() + "';" +
                                    "  var event = new Event('input', { bubbles: true });" +
                                    "  inputs[1].dispatchEvent(event);" +
                                    "  return true;" +
                                    "}" +
                                    "return false;");

                    System.out.println("Name fields filled - Last name: " + lastNameResult + ", First name: " + firstNameResult);
                }
            } catch (Exception e) {
                System.out.println("Error adding name: " + e.getMessage());
            }

            // Add DOB with data from first page
            try {
                Boolean addDobResult = (Boolean) js.executeScript(
                        "var buttons = Array.from(document.querySelectorAll('button'));" +
                                "var addDobButton = buttons.find(button => button.textContent.includes('Add DOB'));" +
                                "if (addDobButton) {" +
                                "  addDobButton.click();" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Add DOB button clicked: " + addDobResult);

                if (addDobResult) {
                    Thread.sleep(1000);

                    // Find and fill DOB input
                    Boolean dobResult = (Boolean) js.executeScript(
                            "var inputs = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                                    "if (inputs.length > 0) {" +
                                    "  inputs[0].value = '" + data.getDob() + "';" +
                                    "  var event = new Event('input', { bubbles: true });" +
                                    "  inputs[0].dispatchEvent(event);" +
                                    "  return true;" +
                                    "}" +
                                    "return false;");

                    System.out.println("DOB field filled: " + dobResult);
                }
            } catch (Exception e) {
                System.out.println("Error adding DOB: " + e.getMessage());
            }

            // Add Citizenship (USA)
            try {
                Boolean addCitizenshipResult = (Boolean) js.executeScript(
                        "var buttons = Array.from(document.querySelectorAll('button'));" +
                                "var addCitizenButton = buttons.find(button => button.textContent.includes('Add Citizenship'));" +
                                "if (addCitizenButton) {" +
                                "  addCitizenButton.click();" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Add Citizenship button clicked: " + addCitizenshipResult);

                if (addCitizenshipResult) {
                    Thread.sleep(1000);

                    // Click on the most recently added dropdown (should be citizenship)
                    Boolean citizenshipDropdownResult = (Boolean) js.executeScript(
                            "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                    "if (comboboxes.length > 0) {" +
                                    "  comboboxes[comboboxes.length - 1].click();" +
                                    "  return true;" +
                                    "}" +
                                    "return false;");

                    if (citizenshipDropdownResult) {
                        Thread.sleep(1000);

                        // Select USA option
                        Boolean citizenshipOptionResult = (Boolean) js.executeScript(
                                "var option = document.getElementById('mat-option-1260');" +
                                        "if (option) {" +
                                        "  option.click();" +
                                        "  return true;" +
                                        "}" +
                                        "return false;");

                        System.out.println("Citizenship USA option selected: " + citizenshipOptionResult);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error adding citizenship: " + e.getMessage());
            }

            // Add Passport
            addPassport(js, data);

            // Add A#
            addANumber(js, data);

            // Add Driver's License
            addDriverLicense(js, data);

            // Add SSN
            addSSN(js, data);

            // Attempt to submit the form
            try {
                Boolean submitResult = (Boolean) js.executeScript(
                        "var buttons = Array.from(document.querySelectorAll('button'));" +
                                "var submitButton = buttons.find(button => button.textContent.includes('Submit') || " +
                                "                              button.textContent.includes('Save') || " +
                                "                              button.textContent.includes('Create'));" +
                                "if (submitButton) {" +
                                "  submitButton.click();" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Submit button clicked: " + submitResult);
            } catch (Exception e) {
                System.out.println("Error submitting form: " + e.getMessage());
            }

            System.out.println("Second page completed to the extent possible!");
            return true;
        } catch (Exception e) {
            System.out.println("Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to find clickable elements with wait
     */
    private static WebElement findClickableElement(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            System.out.println("Element not found or not clickable: " + locator);
            return null;
        }
    }

    /**
     * Helper method to analyze page structure for debugging
     */
    private static void analyzePageStructure(JavascriptExecutor js) {
        try {
            // Check for various Angular Material components
            js.executeScript(
                    "console.log('Analyzing page structure...');" +
                            "console.log('mat-select elements: ' + document.querySelectorAll('mat-select').length);" +
                            "console.log('mat-form-field elements: ' + document.querySelectorAll('mat-form-field').length);" +
                            "console.log('mat-option elements: ' + document.querySelectorAll('mat-option').length);" +
                            "console.log('Elements with role=\"combobox\": ' + document.querySelectorAll('[role=\"combobox\"]').length);" +
                            "console.log('Elements with role=\"listbox\": ' + document.querySelectorAll('[role=\"listbox\"]').length);" +
                            "console.log('Input elements: ' + document.querySelectorAll('input').length);" +
                            "console.log('Button elements: ' + document.querySelectorAll('button').length);"
            );

            // Try to print some of the button text to help with identification
            js.executeScript(
                    "var buttons = document.querySelectorAll('button');" +
                            "console.log('First 5 button texts:');" +
                            "for (var i = 0; i < Math.min(5, buttons.length); i++) {" +
                            "  console.log(i + ': ' + buttons[i].textContent.trim());" +
                            "}"
            );

            // Check for comboboxes (which are used for mat-select)
            Long comboboxCount = (Long) js.executeScript("return document.querySelectorAll('[role=\"combobox\"]').length;");
            System.out.println("Found " + comboboxCount + " combobox elements on the page");

            if (comboboxCount > 0) {
                // Try to output the first few combobox attributes to help debug
                js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "console.log('First 3 combobox details:');" +
                                "for (var i = 0; i < Math.min(3, comboboxes.length); i++) {" +
                                "  console.log(i + ': aria-expanded=' + comboboxes[i].getAttribute('aria-expanded') + " +
                                "      ', aria-required=' + comboboxes[i].getAttribute('aria-required') + " +
                                "      ', id=' + comboboxes[i].getAttribute('id'));" +
                                "}"
                );
            }
        } catch (Exception e) {
            System.out.println("Error analyzing page structure: " + e.getMessage());
        }
    }

    /**
     * Helper method to select an option in a dropdown using JavaScript
     */
    private static void selectOptionByJavaScript(JavascriptExecutor js, int dropdownIndex, String optionId, String description) {
        try {
            // First, click the dropdown to open it
            Boolean dropdownResult = (Boolean) js.executeScript(
                    "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                            "if (comboboxes.length > " + dropdownIndex + ") {" +
                            "  comboboxes[" + dropdownIndex + "].click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println(description + " clicked: " + dropdownResult);

            if (dropdownResult) {
                Thread.sleep(1000); // Wait for dropdown to open

                // Then, click the option
                Boolean optionResult = (Boolean) js.executeScript(
                        "var option = document.getElementById('" + optionId + "');" +
                                "if (option) {" +
                                "  option.click();" +
                                "  return true;" +
                                "}" +
                                "var options = document.querySelectorAll('mat-option');" +
                                "if (options.length > 0) {" +
                                "  options[0].click();" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println(description + " option selected: " + optionResult);
            }
        } catch (Exception e) {
            System.out.println("Error selecting option for " + description + ": " + e.getMessage());
        }
    }

    /**
     * Helper method to fill a textarea using JavaScript
     */
    private static void fillTextAreaByJavaScript(JavascriptExecutor js, String inputId, String value) {
        try {
            Boolean result = (Boolean) js.executeScript(
                    "var textarea = document.getElementById('" + inputId + "');" +
                            "if (textarea) {" +
                            "  textarea.value = '" + value.replace("'", "\\'") + "';" +
                            "  var event = new Event('input', { bubbles: true });" +
                            "  textarea.dispatchEvent(event);" +
                            "  return true;" +
                            "}" +

                            "var textareas = document.querySelectorAll('textarea');" +
                            "if (textareas.length > 0) {" +
                            "  textareas[0].value = '" + value.replace("'", "\\'") + "';" +
                            "  var event = new Event('input', { bubbles: true });" +
                            "  textareas[0].dispatchEvent(event);" +
                            "  return true;" +
                            "}" +

                            "return false;");

            System.out.println("Remarks field filled: " + result);
        } catch (Exception e) {
            System.out.println("Error filling textarea: " + e.getMessage());
        }
    }

    /**
     * Helper method to add an attribute (like Sex, Race, Eye Color) and select an option
     */
    private static void addAttributeAndSelect(JavascriptExecutor js, String buttonLabel, String optionId, String description) {
        try {
            Boolean addButtonResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addButton = buttons.find(button => button.textContent.includes('" + buttonLabel + "'));" +
                            "if (addButton) {" +
                            "  addButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println(buttonLabel + " button clicked: " + addButtonResult);

            if (addButtonResult) {
                Thread.sleep(1000);

                // Find and click the newly added dropdown
                Boolean dropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "if (comboboxes.length > 0) {" +
                                "  // Click the most recently added dropdown (likely the last one)" +
                                "  for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "    if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "      comboboxes[i].click();" +
                                "      return true;" +
                                "    }" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println(description + " dropdown clicked: " + dropdownResult);

                if (dropdownResult) {
                    Thread.sleep(1000);

                    // Select the specified option
                    Boolean optionResult = (Boolean) js.executeScript(
                            "var option = document.getElementById('" + optionId + "');" +
                                    "if (option) {" +
                                    "  option.click();" +
                                    "  return true;" +
                                    "}" +

                                    "// Fallback: try to select any option" +
                                    "var options = document.querySelectorAll('mat-option');" +
                                    "if (options.length > 0) {" +
                                    "  options[0].click();" +
                                    "  return true;" +
                                    "}" +

                                    "return false;");

                    System.out.println(description + " option selected: " + optionResult);
                }
            }
        } catch (Exception e) {
            System.out.println("Error adding " + description + ": " + e.getMessage());
        }
    }

    /**
     * Helper method to add and fill passport information
     */
    private static void addPassport(JavascriptExecutor js, PersonData data) {
        try {
            Boolean addPassportResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addPassportButton = buttons.find(button => button.textContent.includes('Add Passport'));" +
                            "if (addPassportButton) {" +
                            "  addPassportButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Add Passport button clicked: " + addPassportResult);

            if (addPassportResult) {
                Thread.sleep(1000);

                // Click on passport type dropdown
                Boolean passportTypeDropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "  if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "    comboboxes[i].click();" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Passport type dropdown clicked: " + passportTypeDropdownResult);

                if (passportTypeDropdownResult) {
                    Thread.sleep(1000);

                    // Select Regular passport (or any available option)
                    Boolean passportTypeOptionResult = (Boolean) js.executeScript(
                            "var option = document.getElementById('mat-option-1518');" +
                                    "if (option) {" +
                                    "  option.click();" +
                                    "  return true;" +
                                    "}" +

                                    "// Fallback: try to select any option" +
                                    "var options = document.querySelectorAll('mat-option');" +
                                    "if (options.length > 0) {" +
                                    "  options[0].click();" +
                                    "  return true;" +
                                    "}" +

                                    "return false;");

                    System.out.println("Passport type option selected: " + passportTypeOptionResult);
                }

                // Generate passport number if not present
                String passportNumber = data.getPassportNumber();
                if (passportNumber == null || passportNumber.isEmpty()) {
                    passportNumber = "P" + (new Random().nextInt(900000) + 100000);
                }

                // Fill passport number
                Boolean passportNumberResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[maxlength=\"20\"]');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var input = inputs[i];" +
                                "  if (input.id && (input.id.includes('passport') || input.id === 'mat-input-19')) {" +
                                "    input.value = '" + passportNumber + "';" +
                                "    var event = new Event('input', { bubbles: true });" +
                                "    input.dispatchEvent(event);" +
                                "    return true;" +
                                "  }" +
                                "}" +

                                "// Try to find by nearby label text" +
                                "var labels = document.querySelectorAll('label, mat-label');" +
                                "for (var i = 0; i < labels.length; i++) {" +
                                "  if (labels[i].textContent.includes('Passport #')) {" +
                                "    var inputId = labels[i].getAttribute('for');" +
                                "    if (inputId) {" +
                                "      var input = document.getElementById(inputId);" +
                                "      if (input) {" +
                                "        input.value = '" + passportNumber + "';" +
                                "        var event = new Event('input', { bubbles: true });" +
                                "        input.dispatchEvent(event);" +
                                "        return true;" +
                                "      }" +
                                "    }" +
                                "  }" +
                                "}" +

                                "// Last attempt - try recently added inputs" +
                                "var recentInputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "if (recentInputs.length > 0) {" +
                                "  var input = recentInputs[recentInputs.length - 1];" +
                                "  input.value = '" + passportNumber + "';" +
                                "  var event = new Event('input', { bubbles: true });" +
                                "  input.dispatchEvent(event);" +
                                "  return true;" +
                                "}" +

                                "return false;");

                System.out.println("Passport number filled: " + passportNumberResult);

                // Click on passport country dropdown
                Boolean passportCountryDropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "  if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "    comboboxes[i].click();" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Passport country dropdown clicked: " + passportCountryDropdownResult);

                if (passportCountryDropdownResult) {
                    Thread.sleep(1000);

                    // Select USA option (or any available option)
                    Boolean passportCountryOptionResult = (Boolean) js.executeScript(
                            "var option = document.getElementById('mat-option-1520');" +
                                    "if (option) {" +
                                    "  option.click();" +
                                    "  return true;" +
                                    "}" +

                                    "// Fallback: try to select USA or first option" +
                                    "var options = document.querySelectorAll('mat-option');" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.includes('USA') || " +
                                    "      options[i].textContent.includes('UNITED STATES')) {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +

                                    "if (options.length > 0) {" +
                                    "  options[0].click();" +
                                    "  return true;" +
                                    "}" +

                                    "return false;");

                    System.out.println("Passport country option selected: " + passportCountryOptionResult);
                }

                // Fill passport dates
                fillDateField(js, "passport issue", data.getPassportIssueDate());
                fillDateField(js, "passport expiration", data.getPassportExpiryDate());
            }
        } catch (Exception e) {
            System.out.println("Error adding passport: " + e.getMessage());
        }
    }

    /**
     * Helper method to fill a date field
     */
    private static void fillDateField(JavascriptExecutor js, String fieldDescription, String dateValue) {
        if (dateValue == null || dateValue.isEmpty()) {
            // Generate a random date in MM/DD/YYYY format if not provided
            Random random = new Random();
            int month = random.nextInt(12) + 1;
            int day = random.nextInt(28) + 1; // Avoid invalid dates
            int year = 2020 + random.nextInt(5); // 2020-2024
            dateValue = String.format("%02d/%02d/%04d", month, day, year);
        }

        try {
            Boolean dateResult = (Boolean) js.executeScript(
                    "var dateInputs = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                            "for (var i = 0; i < dateInputs.length; i++) {" +
                            "  if (dateInputs[i].value === '') {" +
                            "    dateInputs[i].value = '" + dateValue + "';" +
                            "    var event = new Event('input', { bubbles: true });" +
                            "    dateInputs[i].dispatchEvent(event);" +
                            "    return true;" +
                            "  }" +
                            "}" +
                            "return false;");

            System.out.println(fieldDescription + " date filled: " + dateResult);
        } catch (Exception e) {
            System.out.println("Error filling " + fieldDescription + " date: " + e.getMessage());
        }
    }

    /**
     * Helper method to add and fill A# information
     */
    private static void addANumber(JavascriptExecutor js, PersonData data) {
        try {
            Boolean addANumberResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addANumberButton = buttons.find(button => button.textContent.includes('Add A#'));" +
                            "if (addANumberButton) {" +
                            "  addANumberButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Add A# button clicked: " + addANumberResult);

            if (addANumberResult) {
                Thread.sleep(1000);

                // Generate A# if not present (9-digit number)
                String aNumber = data.getaNumber();
                if (aNumber == null || aNumber.isEmpty()) {
                    aNumber = String.valueOf(100000000 + new Random().nextInt(900000000));
                }

                // Find and fill A# input
                Boolean aNumberResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[mask=\"0*\"]');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  if (inputs[i].id && inputs[i].id === 'mat-input-22') {" +
                                "    inputs[i].value = '" + aNumber + "';" +
                                "    var event = new Event('input', { bubbles: true });" +
                                "    inputs[i].dispatchEvent(event);" +
                                "    return true;" +
                                "  }" +
                                "}" +

                                "// Try to find by label" +
                                "var labels = document.querySelectorAll('label, mat-label');" +
                                "for (var i = 0; i < labels.length; i++) {" +
                                "  if (labels[i].textContent.includes('A #')) {" +
                                "    var inputId = labels[i].getAttribute('for');" +
                                "    if (inputId) {" +
                                "      var input = document.getElementById(inputId);" +
                                "      if (input) {" +
                                "        input.value = '" + aNumber + "';" +
                                "        var event = new Event('input', { bubbles: true });" +
                                "        input.dispatchEvent(event);" +
                                "        return true;" +
                                "      }" +
                                "    }" +
                                "  }" +
                                "}" +

                                "// Last attempt - try recently added inputs" +
                                "var recentInputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "if (recentInputs.length > 0) {" +
                                "  var input = recentInputs[recentInputs.length - 1];" +
                                "  input.value = '" + aNumber + "';" +
                                "  var event = new Event('input', { bubbles: true });" +
                                "  input.dispatchEvent(event);" +
                                "  return true;" +
                                "}" +

                                "return false;");

                System.out.println("A# field filled: " + aNumberResult);
            }
        } catch (Exception e) {
            System.out.println("Error adding A#: " + e.getMessage());
        }
    }

    /**
     * Helper method to add and fill driver's license information
     */
    private static void addDriverLicense(JavascriptExecutor js, PersonData data) {
        try {
            Boolean addDriverLicenseResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addDriverLicenseButton = buttons.find(button => button.textContent.includes(\"Add Driver's License\"));" +
                            "if (addDriverLicenseButton) {" +
                            "  addDriverLicenseButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Add Driver's License button clicked: " + addDriverLicenseResult);

            if (addDriverLicenseResult) {
                Thread.sleep(1000);

                // Generate driver's license number if not present
                String driverLicense = data.getDriverLicense();
                if (driverLicense == null || driverLicense.isEmpty()) {
                    driverLicense = "DL" + (100000 + new Random().nextInt(900000));
                }

                // Fill driver's license number
                Boolean driverLicenseResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[maxlength=\"20\"]');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var input = inputs[i];" +
                                "  if (input.id && input.id === 'mat-input-23') {" +
                                "    input.value = '" + driverLicense + "';" +
                                "    var event = new Event('input', { bubbles: true });" +
                                "    input.dispatchEvent(event);" +
                                "    return true;" +
                                "  }" +
                                "}" +

                                "// Try to find by label" +
                                "var labels = document.querySelectorAll('label, mat-label');" +
                                "for (var i = 0; i < labels.length; i++) {" +
                                "  if (labels[i].textContent.includes(\"Driver's License #\")) {" +
                                "    var inputId = labels[i].getAttribute('for');" +
                                "    if (inputId) {" +
                                "      var input = document.getElementById(inputId);" +
                                "      if (input) {" +
                                "        input.value = '" + driverLicense + "';" +
                                "        var event = new Event('input', { bubbles: true });" +
                                "        input.dispatchEvent(event);" +
                                "        return true;" +
                                "      }" +
                                "    }" +
                                "  }" +
                                "}" +

                                "// Last attempt - try recently added inputs" +
                                "var recentInputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "if (recentInputs.length > 0) {" +
                                "  var input = recentInputs[recentInputs.length - 1];" +
                                "  input.value = '" + driverLicense + "';" +
                                "  var event = new Event('input', { bubbles: true });" +
                                "  input.dispatchEvent(event);" +
                                "  return true;" +
                                "}" +

                                "return false;");

                System.out.println("Driver's license number filled: " + driverLicenseResult);

                // Click on state dropdown
                Boolean stateDropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "  if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "    comboboxes[i].click();" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("State dropdown clicked: " + stateDropdownResult);

                if (stateDropdownResult) {
                    Thread.sleep(1000);

                    // Try to select a US state or any available option
                    Boolean stateOptionResult = (Boolean) js.executeScript(
                            "var option = document.getElementById('mat-option-1774');" +
                                    "if (option) {" +
                                    "  option.click();" +
                                    "  return true;" +
                                    "}" +

                                    "// Fallback: try to select any option" +
                                    "var options = document.querySelectorAll('mat-option');" +
                                    "if (options.length > 0) {" +
                                    "  options[0].click();" +
                                    "  return true;" +
                                    "}" +

                                    "return false;");

                    System.out.println("State option selected: " + stateOptionResult);
                }
            }
        } catch (Exception e) {
            System.out.println("Error adding driver's license: " + e.getMessage());
        }
    }

    /**
     * Helper method to add and fill SSN information
     */
    private static void addSSN(JavascriptExecutor js, PersonData data) {
        try {
            Boolean addSSNResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addSSNButton = buttons.find(button => button.textContent.includes('Add SSN'));" +
                            "if (addSSNButton) {" +
                            "  addSSNButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Add SSN button clicked: " + addSSNResult);

            if (addSSNResult) {
                Thread.sleep(1000);

                // Generate SSN if not present (###-##-####)
                String ssn = data.getSsn();
                if (ssn == null || ssn.isEmpty()) {
                    Random random = new Random();
                    int part1 = 100 + random.nextInt(900);
                    int part2 = 10 + random.nextInt(90);
                    int part3 = 1000 + random.nextInt(9000);
                    ssn = part1 + "-" + part2 + "-" + part3;
                }

                // Find and fill SSN input using multiple strategies
                Boolean ssnResult = (Boolean) js.executeScript(
                        "// Try to find by specific attributes or patterns for SSN fields" +
                                "var inputs = document.querySelectorAll('input');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var input = inputs[i];" +
                                "  if (input.id && input.id.toLowerCase().includes('ssn')) {" +
                                "    input.value = '" + ssn + "';" +
                                "    var event = new Event('input', { bubbles: true });" +
                                "    input.dispatchEvent(event);" +
                                "    return true;" +
                                "  }" +
                                "}" +

                                "// Try to find by label text" +
                                "var labels = document.querySelectorAll('label, mat-label');" +
                                "for (var i = 0; i < labels.length; i++) {" +
                                "  if (labels[i].textContent.includes('SSN')) {" +
                                "    var inputId = labels[i].getAttribute('for');" +
                                "    if (inputId) {" +
                                "      var input = document.getElementById(inputId);" +
                                "      if (input) {" +
                                "        input.value = '" + ssn + "';" +
                                "        var event = new Event('input', { bubbles: true });" +
                                "        input.dispatchEvent(event);" +
                                "        return true;" +
                                "      }" +
                                "    }" +
                                "  }" +
                                "}" +

                                "// Last attempt - try recently added inputs" +
                                "var recentInputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "if (recentInputs.length > 0) {" +
                                "  var input = recentInputs[recentInputs.length - 1];" +
                                "  input.value = '" + ssn + "';" +
                                "  var event = new Event('input', { bubbles: true });" +
                                "  input.dispatchEvent(event);" +
                                "  return true;" +
                                "}" +

                                "return false;");

                System.out.println("SSN field filled: " + ssnResult);
            }
        } catch (Exception e) {
            System.out.println("Error adding SSN: " + e.getMessage());
        }
    }
}