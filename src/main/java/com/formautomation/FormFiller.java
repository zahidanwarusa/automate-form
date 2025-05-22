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
 * FormFiller - Keep what works, fix only mat-select issue
 */
public class FormFiller {
    private static final Random random = new Random();

    /**
     * Fill out the first page of the form (KEEPING ORIGINAL WORKING CODE)
     */
    public static boolean fillFirstPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out first page...");

            // Fill in last name (ORIGINAL WORKING CODE)
            System.out.println("Filling last name: " + data.getLastName());
            waitAndSendKeys(driver, By.id("lastName"), data.getLastName());

            // Fill in first name (ORIGINAL WORKING CODE)
            System.out.println("Filling first name: " + data.getFirstName());
            waitAndSendKeys(driver, By.id("firstName"), data.getFirstName());

            // Fill in DOB (ORIGINAL WORKING CODE)
            System.out.println("Filling DOB: " + data.getDob());
            waitAndSendKeys(driver, By.id("dob"), data.getDob());

            // Click the SEARCH button (ORIGINAL WORKING CODE)
            System.out.println("Clicking SEARCH button...");
            boolean searchClicked = false;

            try {
                WebElement searchButton = driver.findElement(By.xpath("//button[contains(text(), 'Search')]"));
                searchButton.click();
                searchClicked = true;
                System.out.println("Search button clicked using text search");
            } catch (Exception e) {
                System.out.println("Search by text failed: " + e.getMessage());
            }

            if (!searchClicked) {
                try {
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

            // Wait for search results (ORIGINAL WORKING CODE)
            System.out.println("Waiting for search results to load...");
            Thread.sleep(8000);

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".loading, .spinner, .loader, .mat-spinner")));
                System.out.println("Loading completed");
            } catch (Exception e) {
                System.out.println("No loading indicator found or timeout waiting for loading to complete");
            }

            Thread.sleep(3000);

            // Scroll and find Create TECS Lookout button (ORIGINAL WORKING CODE)
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");
            Thread.sleep(2000);

            // Store window handle (ORIGINAL WORKING CODE)
            String originalWindow = driver.getWindowHandle();
            System.out.println("Original window handle: " + originalWindow);
            System.out.println("Current windows before click: " + driver.getWindowHandles().size());

            // Click Create TECS Lookout button (ORIGINAL WORKING CODE)
            System.out.println("Looking for Create TECS Lookout button...");
            boolean createButtonClicked = false;

            try {
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

            // Handle tab switching (ORIGINAL WORKING CODE)
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
     * Fill second page - ONLY fix mat-select, keep everything else working
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Wait for second page (ORIGINAL WORKING TIME)
            Thread.sleep(10000);
            System.out.println("Second page loaded, starting form filling...");

            // Debug what's on the page
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
            System.out.println("Found " + matSelectCount + " mat-select elements on page");

            // First dropdown - NEW APPROACH FOR MAT-SELECT ONLY
            System.out.println("1. Selecting first dropdown (OB - OUTBOUND SUBJECT)...");
            if (clickMatSelectByTrigger(driver, "mat-select-4")) {
                Thread.sleep(2000);
                if (clickOptionById(driver, "mat-option-68")) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Second dropdown
            System.out.println("2. Selecting second dropdown (AB - AG/BIO COUNTERMEASURES)...");
            if (clickMatSelectByTrigger(driver, "mat-select-10")) {
                Thread.sleep(2000);
                if (clickOptionById(driver, "mat-option-549")) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Third dropdown
            System.out.println("3. Selecting third dropdown (0 - NO NOTIFICATION)...");
            if (clickMatSelectByTrigger(driver, "mat-select-6")) {
                Thread.sleep(2000);
                if (clickOptionById(driver, "mat-option-238")) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Fourth dropdown (Multiple)
            System.out.println("4. Selecting fourth dropdown (Multiple)...");
            if (clickMatSelectByTrigger(driver, "mat-select-12")) {
                Thread.sleep(2000);
                int randomOption = 253 + random.nextInt(6); // Random between 253-258
                if (clickOptionById(driver, "mat-option-" + randomOption)) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Fifth dropdown
            System.out.println("5. Selecting fifth dropdown (0 - NOT ON PRIMARY)...");
            if (clickMatSelectByTrigger(driver, "mat-select-8")) {
                Thread.sleep(2000);
                if (clickOptionById(driver, "mat-option-242")) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Fill remarks field (KEEP ORIGINAL WORKING APPROACH)
            System.out.println("6. Filling remarks field...");
            fillInputByIdOriginal(driver, "mat-input-1", "Automated test entry - " + System.currentTimeMillis());
            Thread.sleep(2000);

            // Y/N dropdown
            System.out.println("7. Selecting Y/N dropdown...");
            if (clickMatSelectByTrigger(driver, "mat-select-0")) {
                Thread.sleep(2000);
                if (clickOptionById(driver, "mat-option-2")) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Height dropdown
            System.out.println("8. Selecting height dropdown...");
            if (clickMatSelectByTrigger(driver, "mat-select-2")) {
                Thread.sleep(2000);
                int randomHeight = 8 + random.nextInt(20);
                if (clickOptionById(driver, "mat-option-" + randomHeight)) {
                    closeDropdown(driver); // Close dropdown after selection
                }
            }
            Thread.sleep(2000);

            // Fill weight field (KEEP ORIGINAL WORKING APPROACH)
            System.out.println("9. Filling weight field...");
            String weight = String.valueOf(120 + random.nextInt(131));
            fillInputByIdOriginal(driver, "mat-input-0", weight);
            Thread.sleep(2000);

            // Add Sex
            System.out.println("10. Adding sex...");
            if (clickAddButton(driver, "Add Sex")) {
                Thread.sleep(3000); // Wait longer for new dropdown to appear
                String sexOption = random.nextBoolean() ? "630" : "631"; // F or M
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-" + sexOption)) {
                        closeDropdown(driver);
                    }
                }
            }
            Thread.sleep(2000);

            // Add Race
            System.out.println("11. Adding race...");
            if (clickAddButton(driver, "Add Race")) {
                Thread.sleep(3000); // Wait longer for new dropdown to appear
                int raceOption = 594 + random.nextInt(6); // Random between 594-599
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-" + raceOption)) {
                        closeDropdown(driver);
                    }
                }
            }
            Thread.sleep(2000);

            // Add Eye Color
            System.out.println("12. Adding eye color...");
            if (clickAddButton(driver, "Add Eye Color")) {
                Thread.sleep(3000); // Wait longer for new dropdown to appear
                int eyeOption = 600 + random.nextInt(12); // Random between 600-611
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-" + eyeOption)) {
                        closeDropdown(driver);
                    }
                }
            }
            Thread.sleep(2000);

            // Add Hair Color
            System.out.println("13. Adding hair color...");
            if (clickAddButton(driver, "Add Hair Color")) {
                Thread.sleep(3000); // Wait longer for new dropdown to appear
                int hairOption = 612 + random.nextInt(15); // Random between 612-626
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-" + hairOption)) {
                        closeDropdown(driver);
                    }
                }
            }
            Thread.sleep(2000);

            // Add Name (KEEP ORIGINAL WORKING APPROACH)
            System.out.println("14. Adding name...");
            if (clickAddButton(driver, "Add Name")) {
                Thread.sleep(3000); // Wait for input fields to appear
                fillInputByIdOriginal(driver, "mat-input-2", data.getLastName());
                Thread.sleep(1000);
                fillInputByIdOriginal(driver, "mat-input-3", data.getFirstName());
            }
            Thread.sleep(2000);

            // Add DOB (KEEP ORIGINAL WORKING APPROACH)
            System.out.println("15. Adding DOB...");
            if (clickAddButton(driver, "Add DOB")) {
                Thread.sleep(3000); // Wait for input field to appear
                fillInputByIdOriginal(driver, "mat-input-11", data.getDob());
            }
            Thread.sleep(2000);

            // Add Citizenship
            System.out.println("16. Adding citizenship...");
            if (clickAddButton(driver, "Add Citizenship")) {
                Thread.sleep(3000); // Wait for dropdown to appear
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-1260")) { // USA
                        closeDropdown(driver);
                    }
                }
            }
            Thread.sleep(2000);

            // Add Passport
            System.out.println("17. Adding passport...");
            if (clickAddButton(driver, "Add Passport")) {
                Thread.sleep(3000); // Wait for fields to appear

                // Select passport type (P - Regular)
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-1518")) {
                        closeDropdown(driver);
                    }
                }
                Thread.sleep(1000);

                // Fill passport number
                fillInputByIdOriginal(driver, "mat-input-19", data.getPassportNumber());
                Thread.sleep(1000);

                // Select passport country (USA) - need to find the second dropdown
                List<WebElement> allSelects = driver.findElements(By.tagName("mat-select"));
                if (allSelects.size() >= 2) {
                    WebElement countrySelect = allSelects.get(allSelects.size() - 1); // Get last dropdown
                    highlightAndClick(driver, countrySelect);
                    Thread.sleep(2000);
                    if (clickOptionById(driver, "mat-option-1520")) {
                        closeDropdown(driver);
                    }
                }
                Thread.sleep(1000);

                // Fill passport issue date
                fillInputByIdOriginal(driver, "mat-input-20", data.getPassportIssueDate());
                Thread.sleep(1000);

                // Fill passport expiry date
                fillInputByIdOriginal(driver, "mat-input-21", data.getPassportExpiryDate());
            }
            Thread.sleep(2000);

            // Add A#
            System.out.println("18. Adding A#...");
            if (clickAddButton(driver, "Add A#")) {
                Thread.sleep(3000); // Wait for input field to appear
                fillInputByIdOriginal(driver, "mat-input-22", data.getaNumber());
            }
            Thread.sleep(2000);

            // Add Driver's License
            System.out.println("19. Adding driver's license...");
            if (clickAddButton(driver, "Add Driver's License")) {
                Thread.sleep(3000); // Wait for fields to appear

                fillInputByIdOriginal(driver, "mat-input-23", data.getDriverLicense());
                Thread.sleep(1000);

                // Select random US state - find the newest dropdown
                if (clickLatestDropdown(driver)) {
                    Thread.sleep(2000);
                    int stateOption = 1774 + random.nextInt(62); // Random US state
                    if (clickOptionById(driver, "mat-option-" + stateOption)) {
                        closeDropdown(driver);
                    }
                }
            }
            Thread.sleep(2000);

            // Add SSN
            System.out.println("20. Adding SSN...");
            if (clickAddButton(driver, "Add SSN")) {
                Thread.sleep(3000); // Wait for input field to appear
                // Find the newest input field for SSN
                List<WebElement> allInputs = driver.findElements(By.xpath("//input[@class and contains(@class, 'mat-input-element')]"));
                if (!allInputs.isEmpty()) {
                    WebElement ssnInput = allInputs.get(allInputs.size() - 1);
                    ssnInput.clear();
                    ssnInput.sendKeys(data.getSsn());
                    System.out.println("SSN filled successfully: " + data.getSsn());
                } else {
                    System.out.println("Could not find SSN input field");
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
     * NEW METHOD: Click mat-select by finding and clicking the trigger
     * This is the ONLY new addition - specifically for mat-select issue
     */
    private static boolean clickMatSelectByTrigger(WebDriver driver, String selectId) {
        try {
            System.out.println("Trying to click mat-select: " + selectId);

            // Method 1: Find mat-select and click its trigger
            try {
                WebElement matSelect = driver.findElement(By.id(selectId));

                // Highlight for debugging
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border = '3px solid red';", matSelect);

                // Scroll to element
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", matSelect);
                Thread.sleep(1000);

                // Try to find and click the trigger
                try {
                    WebElement trigger = matSelect.findElement(By.className("mat-select-trigger"));
                    trigger.click();
                    System.out.println("Successfully clicked mat-select-trigger for " + selectId);

                    // Remove highlight
                    js.executeScript("arguments[0].style.border = '';", matSelect);
                    return true;
                } catch (Exception e) {
                    // If trigger not found, click the mat-select itself
                    matSelect.click();
                    System.out.println("Successfully clicked mat-select directly for " + selectId);

                    // Remove highlight
                    js.executeScript("arguments[0].style.border = '';", matSelect);
                    return true;
                }

            } catch (Exception e) {
                System.out.println("Direct approach failed for " + selectId + ": " + e.getMessage());
            }

            // Method 2: JavaScript approach
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                Boolean result = (Boolean) js.executeScript(
                        "var matSelect = document.getElementById('" + selectId + "'); " +
                                "if (matSelect) { " +
                                "  matSelect.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "  var trigger = matSelect.querySelector('.mat-select-trigger'); " +
                                "  if (trigger) { " +
                                "    trigger.click(); " +
                                "  } else { " +
                                "    matSelect.click(); " +
                                "  } " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    System.out.println("Successfully clicked " + selectId + " using JavaScript");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript approach failed for " + selectId + ": " + e.getMessage());
            }

            System.out.println("Failed to click mat-select: " + selectId);
            return false;

        } catch (Exception e) {
            System.out.println("Error clicking mat-select " + selectId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click option by ID (simple approach)
     */
    private static boolean clickOptionById(WebDriver driver, String optionId) {
        try {
            Thread.sleep(1000); // Wait for dropdown to open
            WebElement option = driver.findElement(By.id(optionId));
            option.click();
            System.out.println("Successfully clicked option: " + optionId);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click option " + optionId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click the newest mat-option with specific ID suffix (for dynamically added dropdowns)
     */
    private static boolean clickNewestMatOption(WebDriver driver, String optionNumber) {
        try {
            String optionId = "mat-option-" + optionNumber;
            System.out.println("Clicking newest mat-option: " + optionId);

            // Wait a bit for dropdown to open
            Thread.sleep(1000);

            WebElement option = driver.findElement(By.id(optionId));
            option.click();
            System.out.println("Successfully clicked option: " + optionId);
            return true;

        } catch (Exception e) {
            System.out.println("Failed to click option " + optionNumber + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Close any open dropdown by clicking outside or pressing ESC
     */
    private static void closeDropdown(WebDriver driver) {
        try {
            // Click on body to close any open dropdowns
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.body.click();");
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Could not close dropdown: " + e.getMessage());
        }
    }

    /**
     * Improved button clicking specifically for "Add X" buttons
     */
    private static boolean clickAddButton(WebDriver driver, String buttonText) {
        try {
            System.out.println("🎯 Looking for Add button: " + buttonText);

            // First, list all buttons to see what's available
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "console.log('=== AVAILABLE BUTTONS ==='); " +
                            "var buttons = document.querySelectorAll('button'); " +
                            "for (var i = 0; i < Math.min(buttons.length, 10); i++) { " +
                            "  console.log('Button ' + i + ': ' + buttons[i].textContent.trim()); " +
                            "}"
            );

            // Method 1: Direct text search
            try {
                WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
                // Highlight the button
                js.executeScript("arguments[0].style.border = '3px solid red';", button);
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", button);
                Thread.sleep(1000);

                button.click();

                // Remove highlight
                js.executeScript("arguments[0].style.border = '';", button);
                System.out.println("✅ Successfully clicked Add button: " + buttonText);
                return true;

            } catch (Exception e) {
                System.out.println("Direct text search failed: " + e.getMessage());
            }

            // Method 2: Look for span inside button
            try {
                WebElement button = driver.findElement(By.xpath("//button//span[contains(text(), '" + buttonText + "')]/ancestor::button"));
                // Highlight the button
                js.executeScript("arguments[0].style.border = '3px solid red';", button);
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", button);
                Thread.sleep(1000);

                button.click();

                // Remove highlight
                js.executeScript("arguments[0].style.border = '';", button);
                System.out.println("✅ Successfully clicked Add button by span: " + buttonText);
                return true;

            } catch (Exception e) {
                System.out.println("Span search failed: " + e.getMessage());
            }

            // Method 3: JavaScript search through all buttons
            try {
                Boolean result = (Boolean) js.executeScript(
                        "var buttons = document.querySelectorAll('button'); " +
                                "for (var i = 0; i < buttons.length; i++) { " +
                                "  var buttonText = buttons[i].textContent.trim(); " +
                                "  if (buttonText.includes('" + buttonText + "')) { " +
                                "    console.log('Found button: ' + buttonText); " +
                                "    buttons[i].style.border = '3px solid red'; " +
                                "    buttons[i].scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "    setTimeout(function() { " +
                                "      buttons[i].click(); " +
                                "      buttons[i].style.border = ''; " +
                                "    }, 1000); " +
                                "    return true; " +
                                "  } " +
                                "} " +
                                "return false;"
                );

                if (result != null && result) {
                    Thread.sleep(1500); // Wait for the JavaScript timeout
                    System.out.println("✅ Successfully clicked Add button using JavaScript: " + buttonText);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JavaScript search failed: " + e.getMessage());
            }

            System.out.println("❌ Could not find Add button: " + buttonText);
            return false;

        } catch (Exception e) {
            System.out.println("❌ Error clicking Add button " + buttonText + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Click the most recently added/last mat-select dropdown
     */
    private static boolean clickLatestDropdown(WebDriver driver) {
        try {
            System.out.println("🎯 Looking for latest dropdown...");

            List<WebElement> allSelects = driver.findElements(By.tagName("mat-select"));
            System.out.println("Found " + allSelects.size() + " total mat-select elements");

            if (!allSelects.isEmpty()) {
                WebElement latestSelect = allSelects.get(allSelects.size() - 1);
                return highlightAndClick(driver, latestSelect);
            } else {
                System.out.println("No mat-select elements found");
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Error clicking latest dropdown: " + e.getMessage());
            return false;
        }
    }

    /**
     * Highlight element and click it
     */
    private static boolean highlightAndClick(WebDriver driver, WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Highlight
            js.executeScript("arguments[0].style.border = '3px solid red';", element);
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            Thread.sleep(1000);

            // Try to click trigger first, then element itself
            try {
                WebElement trigger = element.findElement(By.className("mat-select-trigger"));
                trigger.click();
                System.out.println("✅ Clicked mat-select-trigger");
            } catch (Exception e) {
                element.click();
                System.out.println("✅ Clicked mat-select directly");
            }

            // Remove highlight
            js.executeScript("arguments[0].style.border = '';", element);
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error highlighting and clicking element: " + e.getMessage());
            return false;
        }
    }

    /**
     * ORIGINAL WORKING METHOD: Fill input by ID
     */
    private static boolean fillInputByIdOriginal(WebDriver driver, String inputId, String value) {
        try {
            WebElement input = driver.findElement(By.id(inputId));
            input.clear();
            input.sendKeys(value);
            System.out.println("Successfully filled " + inputId + " with: " + value);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to fill " + inputId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * ORIGINAL WORKING METHOD: Click button by text
     */
    private static boolean clickButtonOriginal(WebDriver driver, String buttonText) {
        try {
            try {
                WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
                button.click();
                System.out.println("Successfully clicked button: " + buttonText);
                return true;
            } catch (Exception e) {
                WebElement button = driver.findElement(By.xpath("//button//span[contains(text(), '" + buttonText + "')]/ancestor::button"));
                button.click();
                System.out.println("Successfully clicked button by span: " + buttonText);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Failed to click button " + buttonText + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * ORIGINAL WORKING METHOD: Wait and send keys
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