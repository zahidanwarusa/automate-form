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
 * FormFiller - RESTORED to working version, ONLY fixing mat-select clicking
 */
public class FormFiller {
    private static final Random random = new Random();

    /**
     * Fill out the first page of the form (RESTORED WORKING VERSION)
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
     * Fill out the second page - ONLY FIXING MAT-SELECT CLICKING, keeping everything else
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");

            // Wait for second page to load
            Thread.sleep(10000);
            System.out.println("Second page loaded, starting form filling...");

            // Debug: Check current page state
            JavascriptExecutor js = (JavascriptExecutor) driver;
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());
            Object matSelectCount = js.executeScript("return document.querySelectorAll('mat-select').length;");
            System.out.println("Mat-select elements found: " + matSelectCount);

            // First dropdown - ONLY CHANGE: Use improved mat-select clicking
            System.out.println("1. Selecting first dropdown (OB - OUTBOUND SUBJECT)...");
            if (clickMatSelectImproved(driver, "mat-select-4")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-68']");
            }

            Thread.sleep(2000);

            // Second dropdown
            System.out.println("2. Selecting second dropdown (AB - AG/BIO COUNTERMEASURES)...");
            if (clickMatSelectImproved(driver, "mat-select-10")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-549']");
            }

            Thread.sleep(2000);

            // Third dropdown
            System.out.println("3. Selecting third dropdown (0 - NO NOTIFICATION)...");
            if (clickMatSelectImproved(driver, "mat-select-6")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-238']");
            }

            Thread.sleep(2000);

            // Fourth dropdown (Multiple)
            System.out.println("4. Selecting fourth dropdown (Multiple)...");
            if (clickMatSelectImproved(driver, "mat-select-12")) {
                Thread.sleep(2000);
                int randomOption = 253 + random.nextInt(6);
                clickElementByXPath(driver, "//*[@id='mat-option-" + randomOption + "']");
            }

            Thread.sleep(2000);

            // Fifth dropdown
            System.out.println("5. Selecting fifth dropdown (0 - NOT ON PRIMARY)...");
            if (clickMatSelectImproved(driver, "mat-select-8")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-242']");
            }

            Thread.sleep(2000);

            // Fill remarks field - USING ORIGINAL WORKING METHOD
            System.out.println("6. Filling remarks field...");
            fillElementByXPath(driver, "//*[@id='mat-input-1']", "Automated test entry - " + System.currentTimeMillis());

            Thread.sleep(2000);

            // Y/N dropdown
            System.out.println("7. Selecting Y/N dropdown...");
            if (clickMatSelectImproved(driver, "mat-select-0")) {
                Thread.sleep(2000);
                clickElementByXPath(driver, "//*[@id='mat-option-2']");
            }

            Thread.sleep(2000);

            // Height dropdown
            System.out.println("8. Selecting height dropdown...");
            if (clickMatSelectImproved(driver, "mat-select-2")) {
                Thread.sleep(2000);
                int randomHeight = 8 + random.nextInt(20);
                clickElementByXPath(driver, "//*[@id='mat-option-" + randomHeight + "']");
            }

            Thread.sleep(2000);

            // Fill weight field - USING ORIGINAL WORKING METHOD
            System.out.println("9. Filling weight field...");
            String weight = String.valueOf(120 + random.nextInt(131));
            fillElementByXPath(driver, "//*[@id='mat-input-0']", weight);

            Thread.sleep(2000);

            // Add Name - USING ORIGINAL WORKING METHOD
            System.out.println("14. Adding name...");
            if (clickButtonByText(driver, "Add Name")) {
                Thread.sleep(2000);
                fillElementByXPath(driver, "//*[@id='mat-input-2']", data.getLastName());
                fillElementByXPath(driver, "//*[@id='mat-input-3']", data.getFirstName());
            }

            Thread.sleep(2000);

            // Add DOB - USING ORIGINAL WORKING METHOD
            System.out.println("15. Adding DOB...");
            if (clickButtonByText(driver, "Add DOB")) {
                Thread.sleep(2000);
                fillElementByXPath(driver, "//*[@id='mat-input-11']", data.getDob());
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
     * ONLY NEW METHOD - Improved mat-select clicking based on your console testing
     */
    private static boolean clickMatSelectImproved(WebDriver driver, String selectId) {
        try {
            System.out.println("Clicking mat-select: " + selectId);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // First verify element exists (like your console test)
            Object elementExists = js.executeScript("return document.getElementById('" + selectId + "') !== null;");
            if (!(Boolean) elementExists) {
                System.out.println("Element " + selectId + " does not exist");
                return false;
            }

            // Method 1: Click the mat-select-trigger (based on your HTML analysis)
            try {
                Boolean triggerClicked = (Boolean) js.executeScript(
                        "var matSelect = document.getElementById('" + selectId + "'); " +
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

                if (triggerClicked != null && triggerClicked) {
                    System.out.println("Successfully clicked mat-select-trigger for " + selectId);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Trigger click failed: " + e.getMessage());
            }

            // Method 2: Direct mat-select click (fallback)
            try {
                Boolean directClicked = (Boolean) js.executeScript(
                        "var matSelect = document.getElementById('" + selectId + "'); " +
                                "if (matSelect) { " +
                                "  matSelect.scrollIntoView({behavior: 'smooth', block: 'center'}); " +
                                "  matSelect.click(); " +
                                "  return true; " +
                                "} " +
                                "return false;"
                );

                if (directClicked != null && directClicked) {
                    System.out.println("Successfully clicked mat-select directly for " + selectId);
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Direct click failed: " + e.getMessage());
            }

            System.out.println("Failed to click mat-select: " + selectId);
            return false;

        } catch (Exception e) {
            System.out.println("Error clicking mat-select " + selectId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * ORIGINAL WORKING METHODS - DO NOT MODIFY
     */
    private static boolean clickElementByXPath(WebDriver driver, String xpath) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            element.click();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to click element with XPath " + xpath + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean fillElementByXPath(WebDriver driver, String xpath, String value) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            element.clear();
            element.sendKeys(value);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to fill element with XPath " + xpath + ": " + e.getMessage());
            return false;
        }
    }

    private static boolean clickButtonByText(WebDriver driver, String buttonText) {
        try {
            WebElement button = driver.findElement(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
            button.click();
            return true;
        } catch (Exception e) {
            try {
                WebElement button = driver.findElement(By.xpath("//button//span[contains(text(), '" + buttonText + "')]/ancestor::button"));
                button.click();
                return true;
            } catch (Exception e2) {
                System.out.println("Failed to click button " + buttonText + ": " + e2.getMessage());
                return false;
            }
        }
    }

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