package com.formautomation;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.Set;

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

            // Add a longer wait for the page to be fully loaded
            Thread.sleep(3000);

            // Initialize JavaScript executor for more reliable interaction
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Fill lastName field using JavaScript
            WebElement lastNameField = driver.findElement(By.id("lastName"));
            js.executeScript("arguments[0].value = arguments[1]", lastNameField, data.getLastName());
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }))", lastNameField);
            System.out.println("Last name filled: " + data.getLastName());

            // Fill firstName field using JavaScript
            WebElement firstNameField = driver.findElement(By.id("firstName"));
            js.executeScript("arguments[0].value = arguments[1]", firstNameField, data.getFirstName());
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }))", firstNameField);
            System.out.println("First name filled: " + data.getFirstName());

            // Fill DOB field using JavaScript
            WebElement dobField = driver.findElement(By.id("dob"));
            js.executeScript("arguments[0].value = arguments[1]", dobField, data.getDob());
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }))", dobField);
            System.out.println("DOB filled: " + data.getDob());

            // Scroll down to ensure the search button is in view
            js.executeScript("window.scrollBy(0, 300)");
            Thread.sleep(1000);

            // Find search button using different methods
            WebElement searchButton = null;

            try {
                // First try: by CSS selector
                searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
                System.out.println("Found search button by CSS selector");
            } catch (Exception e) {
                System.out.println("Could not find search button by CSS selector: " + e.getMessage());

                try {
                    // Second try: by XPath with text content
                    searchButton = driver.findElement(By.xpath("//button[contains(text(), 'Search')]"));
                    System.out.println("Found search button by XPath with text");
                } catch (Exception e2) {
                    System.out.println("Could not find search button by XPath with text: " + e2.getMessage());

                    try {
                        // Third try: any button that might be the search button
                        List<WebElement> allButtons = driver.findElements(By.tagName("button"));
                        System.out.println("Found " + allButtons.size() + " buttons on the page");

                        for (int i = 0; i < allButtons.size(); i++) {
                            WebElement button = allButtons.get(i);
                            String buttonText = button.getText().toLowerCase();
                            System.out.println("Button " + i + " text: " + buttonText);

                            if (buttonText.contains("search") ||
                                    buttonText.contains("submit") ||
                                    buttonText.contains("go") ||
                                    buttonText.isEmpty()) {
                                searchButton = button;
                                System.out.println("Found potential search button with text: " + buttonText);
                                break;
                            }
                        }
                    } catch (Exception e3) {
                        System.out.println("Error finding buttons: " + e3.getMessage());
                    }
                }
            }

            // Print details about the button we found
            if (searchButton != null) {
                System.out.println("Search button found: " + searchButton.getTagName() + " " +
                        "isDisplayed=" + searchButton.isDisplayed() + " " +
                        "isEnabled=" + searchButton.isEnabled());

                // Get button location
                Point location = searchButton.getLocation();
                System.out.println("Button location: x=" + location.getX() + ", y=" + location.getY());

                // Try to make sure the button is visible
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", searchButton);
                Thread.sleep(1000);

                System.out.println("Clicking Search button...");
                try {
                    // Try JavaScript click as it's more reliable
                    js.executeScript("arguments[0].click();", searchButton);
                    System.out.println("Search button clicked successfully using JavaScript");
                } catch (Exception jsClickException) {
                    System.out.println("JavaScript click failed: " + jsClickException.getMessage());

                    // Try regular click as last resort
                    searchButton.click();
                    System.out.println("Search button clicked successfully using WebElement click");
                }
            } else {
                // If we can't find the button, try to submit the form directly
                System.out.println("Search button not found, trying to submit the form directly");
                WebElement form = driver.findElement(By.tagName("form"));
                js.executeScript("arguments[0].submit();", form);
            }

            // Wait for search results to load
            System.out.println("Waiting for search results to load...");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Wait for initial loading to complete
            System.out.println("- Waiting 5 seconds for initial loading...");
            Thread.sleep(5000);

            // Check for loading indicator (Optional - adjust based on actual page behavior)
            try {
                WebElement loadingIndicator = driver.findElement(By.cssSelector(".loading-indicator, .spinner, .loader, [aria-busy='true']"));
                if (loadingIndicator.isDisplayed()) {
                    System.out.println("- Loading indicator found, waiting for it to disappear...");
                    wait.until(ExpectedConditions.invisibilityOf(loadingIndicator));
                }
            } catch (NoSuchElementException e) {
                // Loading indicator not found - may not be present, continue
            }

            // Wait a bit more for any asynchronous updates to complete
            System.out.println("- Waiting additional 5 seconds for completion...");
            Thread.sleep(5000);

            System.out.println("Loading appears to be complete.");

            // Look for Create TECS Lookout button
            System.out.println("Looking for Create TECS Lookout button...");

            // Try multiple strategies to find the button
            WebElement tecsButton = null;

            try {
                // Strategy 1: By link text
                tecsButton = driver.findElement(By.linkText("Create TECS Lookout"));
                System.out.println("Found TECS button by link text");
            } catch (NoSuchElementException e) {
                // Strategy 2: By contains text
                try {
                    tecsButton = driver.findElement(By.xpath("//a[contains(text(), 'Create TECS Lookout')]"));
                    System.out.println("Found TECS button by XPath text contains");
                } catch (NoSuchElementException e2) {
                    // Strategy 3: By JavaScript search
                    try {
                        tecsButton = (WebElement) js.executeScript(
                                "return document.querySelector(\"a.more.event-button\")");
                        System.out.println("Found TECS button by JavaScript");
                    } catch (Exception e3) {
                        // Strategy 4: By any link with TECS in it
                        try {
                            List<WebElement> links = driver.findElements(By.tagName("a"));
                            for (WebElement link : links) {
                                if (link.getText().contains("TECS") || link.getText().contains("Lookout")) {
                                    tecsButton = link;
                                    System.out.println("Found TECS button by scanning links: " + link.getText());
                                    break;
                                }
                            }
                        } catch (Exception e4) {
                            System.out.println("Error scanning links: " + e4.getMessage());
                        }
                    }
                }
            }

            if (tecsButton != null) {
                // Try to make sure the button is visible
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", tecsButton);
                Thread.sleep(1000);

                try {
                    // Try JavaScript click as it's more reliable
                    js.executeScript("arguments[0].click();", tecsButton);
                    System.out.println("Create TECS Lookout button clicked successfully using JavaScript");
                } catch (Exception jsClickException) {
                    System.out.println("JavaScript click failed: " + jsClickException.getMessage());

                    // Try regular click as last resort
                    tecsButton.click();
                    System.out.println("Create TECS Lookout button clicked successfully using WebElement click");
                }

                System.out.println("First page completed!");
                return true;
            } else {
                System.out.println("Failed to find Create TECS Lookout button");
                return false;
            }

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

                for (String windowHandle : windowHandles) {
                    if (!windowHandle.equals(originalWindow)) {
                        driver.switchTo().window(windowHandle);
                        System.out.println("Switched to new window/tab: " + driver.getTitle());
                        break;
                    }
                }
            }

            // Wait for Angular application to load
            System.out.println("Waiting for Angular application to stabilize...");
            Thread.sleep(10000);

            // Check for Angular application using JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Debug info
            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());

            // Try to stabilize Angular application using JavaScript
            try {
                js.executeScript(
                        "var waitForAngular = function() { " +
                                "  try { " +
                                "    if (window.getAllAngularTestabilities) { " +
                                "      var testabilities = window.getAllAngularTestabilities(); " +
                                "      var callback = function() { console.log('Angular stabilized'); }; " +
                                "      var whenStable = function(testability) { testability.whenStable(callback); }; " +
                                "      testabilities.forEach(whenStable); " +
                                "      return 'Waiting for Angular to stabilize'; " +
                                "    } " +
                                "    return 'No Angular found'; " +
                                "  } catch(e) { " +
                                "    return 'Error checking Angular: ' + e; " +
                                "  } " +
                                "}; " +
                                "return waitForAngular();");

                System.out.println("Attempted to wait for Angular to stabilize");
            } catch (Exception e) {
                System.out.println("Error waiting for Angular: " + e.getMessage());
            }

            // Check for iframes (Angular components might be inside iframes)
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            if (!iframes.isEmpty()) {
                System.out.println("Found " + iframes.size() + " iframes, switching to first one");
                driver.switchTo().frame(0);
            }

            // ADVANCED APPROACH: Check the actual DOM structure using JavaScript
            System.out.println("\n--- DOM Analysis ---");

            String bodyHTML = (String) js.executeScript("return document.body.outerHTML.substring(0, 1000);");
            System.out.println("First 1000 chars of body: " + bodyHTML);

            // Check for common Angular Material components
            Long matFormFieldCount = (Long) js.executeScript("return document.querySelectorAll('mat-form-field').length;");
            System.out.println("mat-form-field count: " + matFormFieldCount);

            Long matSelectCount = (Long) js.executeScript("return document.querySelectorAll('mat-select').length;");
            System.out.println("mat-select count: " + matSelectCount);

            Long comboboxCount = (Long) js.executeScript("return document.querySelectorAll('[role=\"combobox\"]').length;");
            System.out.println("role=combobox count: " + comboboxCount);

            // Print first few comboboxes for debugging
            js.executeScript(
                    "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                            "for(var i=0; i<Math.min(comboboxes.length, 3); i++) {" +
                            "  console.log('Combobox ' + i + ':', " +
                            "    'id=' + comboboxes[i].id, " +
                            "    'aria-labelledby=' + comboboxes[i].getAttribute('aria-labelledby'), " +
                            "    'class=' + comboboxes[i].className);" +
                            "}");

            // Use a completely JavaScript-based approach
            System.out.println("\n--- USING PURE JAVASCRIPT APPROACH ---");

            // First dropdown - "Subject Type"
            selectOptionWithJavaScript(js, 0, "OB - OUTBOUND SUBJECT");

            // Second dropdown - "Category"
            selectOptionWithJavaScript(js, 1, "AB - AG/BIO COUNTERMEASURES");

            // Third dropdown - "Notification"
            selectOptionWithJavaScript(js, 2, "0 - NO NOTIFICATION");

            // Fourth multi-select dropdown (if present)
            if (comboboxCount >= 4) {
                selectOptionWithJavaScript(js, 3, "PVRVK - PROVISIONAL REVOCATION");
            }

            // Fifth dropdown (if present)
            if (comboboxCount >= 5) {
                selectOptionWithJavaScript(js, 4, "0 - NOT ON PRIMARY");
            }

            // Fill remarks textarea
            Boolean remarksResult = (Boolean) js.executeScript(
                    "var textareas = document.querySelectorAll('textarea');" +
                            "if (textareas.length > 0) {" +
                            "  textareas[0].value = 'Test remarks for automated entry - " + data.getLastName() + ", " + data.getFirstName() + "';" +
                            "  textareas[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Filled remarks field: " + remarksResult);

            // Click "Add Sex" button and select option
            clickButtonAndSelectOption(js, "Add Sex", "F - FEMALE");

            // Click "Add Race" button and select option
            clickButtonAndSelectOption(js, "Add Race", "A - ASIAN");

            // Click "Add Eye Color" button and select option
            clickButtonAndSelectOption(js, "Add Eye Color", "BL - BLUE");

            // Click "Add Hair Color" button and select option
            clickButtonAndSelectOption(js, "Add Hair Color", "BK - BLACK");

            // Click "Add Name" button and fill first/last name
            Boolean addNameResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addNameButton = buttons.find(b => b.textContent.includes('Add Name'));" +
                            "if (addNameButton) {" +
                            "  addNameButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add Name button: " + addNameResult);

            if (addNameResult) {
                Thread.sleep(1000);

                // Find all input fields that might be name fields
                Boolean nameFieldsResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "var lastName, firstName;" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + inputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('Last Name')) {" +
                                "    lastName = inputs[i];" +
                                "  }" +
                                "  if (label && label.textContent.includes('First Name')) {" +
                                "    firstName = inputs[i];" +
                                "  }" +
                                "}" +
                                "if (lastName && firstName) {" +
                                "  lastName.value = '" + data.getLastName() + "';" +
                                "  firstName.value = '" + data.getFirstName() + "';" +
                                "  lastName.dispatchEvent(new Event('input', { bubbles: true }));" +
                                "  firstName.dispatchEvent(new Event('input', { bubbles: true }));" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Filled name fields: " + nameFieldsResult);

                // If the above approach didn't work, try using IDs directly
                if (!nameFieldsResult) {
                    js.executeScript(
                            "var lastNameInput = document.getElementById('mat-input-2');" +
                                    "var firstNameInput = document.getElementById('mat-input-3');" +
                                    "if (lastNameInput) {" +
                                    "  lastNameInput.value = '" + data.getLastName() + "';" +
                                    "  lastNameInput.dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "}" +
                                    "if (firstNameInput) {" +
                                    "  firstNameInput.value = '" + data.getFirstName() + "';" +
                                    "  firstNameInput.dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "}");

                    System.out.println("Attempted to fill name fields using direct IDs");
                }
            }

            // Click "Add DOB" button and fill DOB
            Boolean addDobResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addDobButton = buttons.find(b => b.textContent.includes('Add DOB'));" +
                            "if (addDobButton) {" +
                            "  addDobButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add DOB button: " + addDobResult);

            if (addDobResult) {
                Thread.sleep(1000);

                Boolean dobFieldResult = (Boolean) js.executeScript(
                        "var datePickers = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                                "if (datePickers.length > 0) {" +
                                "  for (var i = 0; i < datePickers.length; i++) {" +
                                "    var label = document.querySelector('label[for=\"' + datePickers[i].id + '\"]');" +
                                "    if (!label || label.textContent.includes('DOB') || !datePickers[i].value) {" +
                                "      datePickers[i].value = '" + data.getDob() + "';" +
                                "      datePickers[i].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "      return true;" +
                                "    }" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Filled DOB field: " + dobFieldResult);

                // Fallback approach if above didn't work
                if (!dobFieldResult) {
                    js.executeScript(
                            "var dobInput = document.getElementById('mat-input-11');" +
                                    "if (dobInput) {" +
                                    "  dobInput.value = '" + data.getDob() + "';" +
                                    "  dobInput.dispatchEvent(new Event('input', { bubbles: true }));" +
                                    "}");

                    System.out.println("Attempted to fill DOB field using direct ID");
                }
            }

            // Add remaining fields as needed
            addRemainingFields(js, data);

            System.out.println("Second page completed successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Error filling second page: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to select an option from a dropdown using JavaScript
     */
    private static void selectOptionWithJavaScript(JavascriptExecutor js, int dropdownIndex, String optionText) {
        try {
            // First click to open the dropdown
            Boolean clickResult = (Boolean) js.executeScript(
                    "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                            "if (comboboxes.length > " + dropdownIndex + ") {" +
                            "  comboboxes[" + dropdownIndex + "].click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked dropdown " + dropdownIndex + ": " + clickResult);

            if (clickResult) {
                Thread.sleep(1000);

                // Then select the option by text
                Boolean selectResult = (Boolean) js.executeScript(
                        "var options = document.querySelectorAll('.mat-option-text');" +
                                "for (var i = 0; i < options.length; i++) {" +
                                "  if (options[i].textContent.trim() === '" + optionText + "') {" +
                                "    options[i].click();" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Selected option '" + optionText + "': " + selectResult);

                // If selecting by text failed, try a backup approach using option IDs
                if (!selectResult) {
                    selectResult = (Boolean) js.executeScript(
                            "var panel = document.querySelector('.mat-select-panel');" +
                                    "if (panel) {" +
                                    "  var option = Array.from(panel.querySelectorAll('mat-option')).find(o => o.textContent.includes('" + optionText + "'));" +
                                    "  if (option) {" +
                                    "    option.click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    System.out.println("Selected option using backup approach: " + selectResult);
                }

                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("Error selecting option: " + e.getMessage());
        }
    }

    /**
     * Helper method to click a button and select an option from the resulting dropdown
     */
    private static void clickButtonAndSelectOption(JavascriptExecutor js, String buttonText, String optionText) {
        try {
            Boolean buttonResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var targetButton = buttons.find(b => b.textContent.includes('" + buttonText + "'));" +
                            "if (targetButton) {" +
                            "  targetButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked '" + buttonText + "' button: " + buttonResult);

            if (buttonResult) {
                Thread.sleep(1000);

                // Find the most recently added dropdown and click it
                Boolean dropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "if (comboboxes.length > 0) {" +
                                "  for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "    if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "      comboboxes[i].click();" +
                                "      return true;" +
                                "    }" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Clicked dropdown for " + buttonText + ": " + dropdownResult);

                if (dropdownResult) {
                    Thread.sleep(1000);

                    // Select the option by text
                    Boolean optionResult = (Boolean) js.executeScript(
                            "var options = document.querySelectorAll('.mat-option-text');" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.trim() === '" + optionText + "') {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    System.out.println("Selected '" + optionText + "' option: " + optionResult);

                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in clickButtonAndSelectOption: " + e.getMessage());
        }
    }

    /**
     * Add the remaining fields like citizenship, passport, etc.
     */
    private static void addRemainingFields(JavascriptExecutor js, PersonData data) {
        try {
            // Add Citizenship
            Boolean addCitizenshipResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addCitizenshipButton = buttons.find(b => b.textContent.includes('Add Citizenship'));" +
                            "if (addCitizenshipButton) {" +
                            "  addCitizenshipButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add Citizenship button: " + addCitizenshipResult);

            if (addCitizenshipResult) {
                Thread.sleep(1000);

                // Find and click the most recently added dropdown
                Boolean citizenshipDropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "if (comboboxes.length > 0) {" +
                                "  for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "    if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "      comboboxes[i].click();" +
                                "      return true;" +
                                "    }" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Clicked citizenship dropdown: " + citizenshipDropdownResult);

                if (citizenshipDropdownResult) {
                    Thread.sleep(1000);

                    // Select USA option
                    Boolean usaOptionResult = (Boolean) js.executeScript(
                            "var options = document.querySelectorAll('.mat-option-text');" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.includes('USA') || options[i].textContent.includes('UNITED STATES')) {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    System.out.println("Selected USA citizenship: " + usaOptionResult);
                }
            }

            // Generate a random passport number if not already set
            if (data.getPassportNumber() == null || data.getPassportNumber().isEmpty()) {
                data.setPassportNumber("P" + System.currentTimeMillis() % 10000000);
            }

            // Add Passport
            Boolean addPassportResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addPassportButton = buttons.find(b => b.textContent.includes('Add Passport'));" +
                            "if (addPassportButton) {" +
                            "  addPassportButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add Passport button: " + addPassportResult);

            if (addPassportResult) {
                Thread.sleep(1000);

                // Select passport type (find most recent dropdown)
                Boolean passportTypeResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "if (comboboxes.length > 0) {" +
                                "  for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "    if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "      comboboxes[i].click();" +
                                "      return true;" +
                                "    }" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Clicked passport type dropdown: " + passportTypeResult);

                if (passportTypeResult) {
                    Thread.sleep(1000);

                    // Select Regular passport type
                    Boolean regularPassportResult = (Boolean) js.executeScript(
                            "var options = document.querySelectorAll('.mat-option-text');" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.includes('Regular') || options[i].textContent.includes('R -')) {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    System.out.println("Selected Regular passport type: " + regularPassportResult);
                }

                // Fill passport number
                Boolean passportNumberResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + inputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('Passport #')) {" +
                                "    inputs[i].value = '" + data.getPassportNumber() + "';" +
                                "    inputs[i].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Filled passport number: " + passportNumberResult);

                // Select passport country dropdown
                Boolean passportCountryResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "if (comboboxes.length > 0) {" +
                                "  var passportCountryDropdown = null;" +
                                "  for (var i = 0; i < comboboxes.length; i++) {" +
                                "    if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "      var label = document.querySelector('label[id=\"' + comboboxes[i].getAttribute('aria-labelledby') + '\"]');" +
                                "      if (!label || !passportCountryDropdown) {" +
                                "        passportCountryDropdown = comboboxes[i];" +
                                "      }" +
                                "    }" +
                                "  }" +
                                "  if (passportCountryDropdown) {" +
                                "    passportCountryDropdown.click();" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Clicked passport country dropdown: " + passportCountryResult);

                if (passportCountryResult) {
                    Thread.sleep(1000);

                    // Select USA
                    Boolean usaOptionResult = (Boolean) js.executeScript(
                            "var options = document.querySelectorAll('.mat-option-text');" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.includes('USA') || options[i].textContent.includes('UNITED STATES')) {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    System.out.println("Selected USA passport country: " + usaOptionResult);
                }

                // Fill passport issue date
                Boolean passportIssueDateResult = (Boolean) js.executeScript(
                        "var dateInputs = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                                "for (var i = 0; i < dateInputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + dateInputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('Issue')) {" +
                                "    dateInputs[i].value = '01/01/2020';" +
                                "    dateInputs[i].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Filled passport issue date: " + passportIssueDateResult);

                // Fill passport expiry date
                Boolean passportExpiryDateResult = (Boolean) js.executeScript(
                        "var dateInputs = document.querySelectorAll('input[mask=\"00/00/0000\"]');" +
                                "for (var i = 0; i < dateInputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + dateInputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('Expiration')) {" +
                                "    dateInputs[i].value = '01/01/2030';" +
                                "    dateInputs[i].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Filled passport expiry date: " + passportExpiryDateResult);
            }

            // Generate A# if not already set
            if (data.getaNumber() == null || data.getaNumber().isEmpty()) {
                data.setaNumber("A" + (10000000 + (int)(Math.random() * 90000000)));
            }

            // Add A#
            Boolean addANumberResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addANumberButton = buttons.find(b => b.textContent.includes('Add A#'));" +
                            "if (addANumberButton) {" +
                            "  addANumberButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add A# button: " + addANumberResult);

            if (addANumberResult) {
                Thread.sleep(1000);

                // Fill A# field
                Boolean aNumberResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + inputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('A #')) {" +
                                "    inputs[i].value = '" + data.getaNumber() + "';" +
                                "    inputs[i].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Filled A# field: " + aNumberResult);
            }

            // Generate driver's license if not already set
            if (data.getDriverLicense() == null || data.getDriverLicense().isEmpty()) {
                data.setDriverLicense("DL" + (100000000 + (int)(Math.random() * 900000000)));
            }

            // Add Driver's License
            Boolean addDriverLicenseResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addDriverLicenseButton = buttons.find(b => b.textContent.includes(\"Driver's License\"));" +
                            "if (addDriverLicenseButton) {" +
                            "  addDriverLicenseButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add Driver's License button: " + addDriverLicenseResult);

            if (addDriverLicenseResult) {
                Thread.sleep(1000);

                // Fill driver's license number
                Boolean driverLicenseNumberResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + inputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('Driver\\'s License #')) {" +
                                "    inputs[i].value = '" + data.getDriverLicense() + "';" +
                                "    inputs[i].dispatchEvent(new Event('input', { bubbles: true }));" +
                                "    return true;" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Filled driver's license number: " + driverLicenseNumberResult);

                // Select state dropdown
                Boolean stateDropdownResult = (Boolean) js.executeScript(
                        "var comboboxes = document.querySelectorAll('[role=\"combobox\"]');" +
                                "if (comboboxes.length > 0) {" +
                                "  for (var i = comboboxes.length - 1; i >= 0; i--) {" +
                                "    if (comboboxes[i].getAttribute('aria-expanded') !== 'true') {" +
                                "      comboboxes[i].click();" +
                                "      return true;" +
                                "    }" +
                                "  }" +
                                "}" +
                                "return false;");

                System.out.println("Clicked state dropdown: " + stateDropdownResult);

                if (stateDropdownResult) {
                    Thread.sleep(1000);

                    // Select Virginia or any US state
                    Boolean stateOptionResult = (Boolean) js.executeScript(
                            "var options = document.querySelectorAll('.mat-option-text');" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.includes('VA -') || options[i].textContent.includes('Virginia')) {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "// If Virginia not found, try any US state" +
                                    "for (var i = 0; i < options.length; i++) {" +
                                    "  if (options[i].textContent.match(/[A-Z]{2} - /)) {" +
                                    "    options[i].click();" +
                                    "    return true;" +
                                    "  }" +
                                    "}" +
                                    "return false;");

                    System.out.println("Selected state for driver's license: " + stateOptionResult);
                }
            }

            // Generate SSN if not already set
            if (data.getSsn() == null || data.getSsn().isEmpty()) {
                // Generate a random SSN in format XXX-XX-XXXX
                int area = 100 + (int)(Math.random() * 899);
                int group = 10 + (int)(Math.random() * 89);
                int serial = 1000 + (int)(Math.random() * 8999);
                data.setSsn(area + "-" + group + "-" + serial);
            }

            // Add SSN
            Boolean addSsnResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var addSsnButton = buttons.find(b => b.textContent.includes('Add SSN'));" +
                            "if (addSsnButton) {" +
                            "  addSsnButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Clicked Add SSN button: " + addSsnResult);

            if (addSsnResult) {
                Thread.sleep(1000);

                // Find the SSN input field and fill it
                Boolean ssnResult = (Boolean) js.executeScript(
                        "var inputs = document.querySelectorAll('input[type=\"text\"]');" +
                                "var ssnInput = null;" +
                                "for (var i = 0; i < inputs.length; i++) {" +
                                "  var label = document.querySelector('label[for=\"' + inputs[i].id + '\"]');" +
                                "  if (label && label.textContent.includes('SSN')) {" +
                                "    ssnInput = inputs[i];" +
                                "    break;" +
                                "  }" +
                                "}" +
                                "if (!ssnInput && inputs.length > 0) {" +
                                "  // If we couldn't find by label, use the last added input field" +
                                "  ssnInput = inputs[inputs.length - 1];" +
                                "}" +
                                "if (ssnInput) {" +
                                "  ssnInput.value = '" + data.getSsn() + "';" +
                                "  ssnInput.dispatchEvent(new Event('input', { bubbles: true }));" +
                                "  return true;" +
                                "}" +
                                "return false;");

                System.out.println("Filled SSN field: " + ssnResult);
            }

            // Finally, try to submit the form if there's a submit button
            Boolean submitResult = (Boolean) js.executeScript(
                    "var buttons = Array.from(document.querySelectorAll('button'));" +
                            "var submitButton = buttons.find(b => {" +
                            "  return b.textContent.includes('Submit') || " +
                            "         b.textContent.includes('Save') || " +
                            "         b.textContent.includes('Next') || " +
                            "         b.type === 'submit';" +
                            "});" +
                            "if (submitButton) {" +
                            "  submitButton.click();" +
                            "  return true;" +
                            "}" +
                            "return false;");

            System.out.println("Attempted to click submit button: " + submitResult);

        } catch (Exception e) {
            System.out.println("Error adding remaining fields: " + e.getMessage());
        }
    }
}