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
 * Utility class to handle form filling operations
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

            // Scroll down a bit to find the Create TECS Lookout button
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");
            Thread.sleep(1000);

            // Click on Create TECS Lookout button
            try {
                WebElement createButton = driver.findElement(By.xpath("//a[contains(text(), 'Create TECS Lookout')]"));
                createButton.click();
            } catch (Exception e) {
                System.out.println("Could not find Create TECS Lookout button, trying with different selector...");
                try {
                    WebElement createButton = driver.findElement(By.className("event-button"));
                    createButton.click();
                } catch (Exception ex) {
                    System.out.println("Still could not find the button. Stopping.");
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
     * Fill out the second page of the form
     * @param driver WebDriver instance
     * @param data PersonData with the information to fill
     * @return true if successful, false otherwise
     */
    public static boolean fillSecondPage(WebDriver driver, PersonData data) {
        try {
            System.out.println("Filling out second page...");

            // Wait for the second page to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("mat-select")));

            // First dropdown selection (select option 68 - OB - OUTBOUND SUBJECT)
            try {
                // Find the first mat-select element
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(0).click();
                Thread.sleep(1000);

                // Select option from dropdown
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("mat-option-68"))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error on first dropdown: " + e.getMessage());
            }

            // Second dropdown selection (select option 549 - AB - AG/BIO COUNTERMEASURES)
            try {
                // Find the second mat-select element
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(1).click();
                Thread.sleep(1000);

                // Select option from dropdown
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("mat-option-549"))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error on second dropdown: " + e.getMessage());
            }

            // Third dropdown selection (select option 238 - 0 - NO NOTIFICATION)
            try {
                // Find the third mat-select element
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(2).click();
                Thread.sleep(1000);

                // Select option from dropdown
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("mat-option-238"))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error on third dropdown: " + e.getMessage());
            }

            // Fourth (Multiple) dropdown selection (select a random option from 253-548)
            try {
                // Find the fourth mat-select element
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(3).click();
                Thread.sleep(1000);

                // Select random option from dropdown
                String optionId = "mat-option-" + (253 + random.nextInt(6)); // Random between 253-258
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(optionId))
                );
                option.click();

                // Close the dropdown by clicking elsewhere
                driver.findElement(By.tagName("body")).click();
            } catch (Exception e) {
                System.out.println("Error on fourth dropdown: " + e.getMessage());
            }

            // Fifth dropdown selection (select option 242 - 0 - NOT ON PRIMARY)
            try {
                // Find the fifth mat-select element
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(4).click();
                Thread.sleep(1000);

                // Select option from dropdown
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("mat-option-242"))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error on fifth dropdown: " + e.getMessage());
            }

            // Fill remarks field
            try {
                WebElement remarks = driver.findElement(By.id("mat-input-1"));
                remarks.clear();
                remarks.sendKeys("Automated test entry. Random data generated for testing purposes.");
            } catch (Exception e) {
                System.out.println("Error filling remarks: " + e.getMessage());
            }

            // Select Y/N dropdown (mat-option-2 - Y)
            try {
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(5).click();
                Thread.sleep(1000);

                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("mat-option-2"))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error on Y/N dropdown: " + e.getMessage());
            }

            // Select height dropdown (select a random height)
            try {
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(6).click();
                Thread.sleep(1000);

                // Select random height option
                String optionId = "mat-option-" + (8 + random.nextInt(13)); // Random between 8-20
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(optionId))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error on height dropdown: " + e.getMessage());
            }

            // Fill in weight
            try {
                WebElement weightInput = driver.findElement(By.id("mat-input-0"));
                weightInput.clear();
                weightInput.sendKeys(String.valueOf(120 + random.nextInt(131))); // Random between 120-250
            } catch (Exception e) {
                System.out.println("Error filling weight: " + e.getMessage());
            }

            // Add Sex
            try {
                // Click Add Sex button
                WebElement addSexButton = driver.findElement(By.xpath("//button[contains(., 'Add Sex')]"));
                addSexButton.click();
                Thread.sleep(1000);

                // Select from dropdown (F - FEMALE or M - MALE randomly)
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click(); // Get the most recently added dropdown
                Thread.sleep(1000);

                // Random selection between male/female
                String sexOption = random.nextBoolean() ? "mat-option-630" : "mat-option-631";
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(sexOption))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error adding sex: " + e.getMessage());
            }

            // Add Race
            try {
                // Click Add Race button
                WebElement addRaceButton = driver.findElement(By.xpath("//button[contains(., 'Add Race')]"));
                addRaceButton.click();
                Thread.sleep(1000);

                // Select from dropdown (random race)
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click();
                Thread.sleep(1000);

                String raceOption = "mat-option-" + (594 + random.nextInt(6)); // Random between 594-599
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(raceOption))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error adding race: " + e.getMessage());
            }

            // Add Eye Color
            try {
                // Click Add Eye Color button
                WebElement addEyeButton = driver.findElement(By.xpath("//button[contains(., 'Add Eye Color')]"));
                addEyeButton.click();
                Thread.sleep(1000);

                // Select from dropdown (random eye color)
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click();
                Thread.sleep(1000);

                String eyeOption = "mat-option-" + (600 + random.nextInt(12)); // Random between 600-611
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(eyeOption))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error adding eye color: " + e.getMessage());
            }

            // Add Hair Color
            try {
                // Click Add Hair Color button
                WebElement addHairButton = driver.findElement(By.xpath("//button[contains(., 'Add Hair Color')]"));
                addHairButton.click();
                Thread.sleep(1000);

                // Select from dropdown (random hair color)
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click();
                Thread.sleep(1000);

                String hairOption = "mat-option-" + (612 + random.nextInt(15)); // Random between 612-626
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(hairOption))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error adding hair color: " + e.getMessage());
            }

            // Add Name (reusing previously generated name)
            try {
                // Click Add Name button
                WebElement addNameButton = driver.findElement(By.xpath("//button[contains(., 'Add Name')]"));
                addNameButton.click();
                Thread.sleep(1000);

                // Fill in last name (reuse from earlier)
                WebElement lastNameInput = driver.findElement(By.id("mat-input-2"));
                lastNameInput.clear();
                lastNameInput.sendKeys(data.getLastName());

                // Fill in first name (reuse from earlier)
                WebElement firstNameInput = driver.findElement(By.id("mat-input-3"));
                firstNameInput.clear();
                firstNameInput.sendKeys(data.getFirstName());
            } catch (Exception e) {
                System.out.println("Error adding name: " + e.getMessage());
            }

            // Add DOB (reusing previously generated DOB)
            try {
                // Click Add DOB button
                WebElement addDobButton = driver.findElement(By.xpath("//button[contains(., 'Add DOB')]"));
                addDobButton.click();
                Thread.sleep(1000);

                // Fill in DOB
                WebElement dobInput = driver.findElement(By.id("mat-input-11"));
                dobInput.clear();
                dobInput.sendKeys(data.getDob());
            } catch (Exception e) {
                System.out.println("Error adding DOB: " + e.getMessage());
            }

            // Add Citizenship (USA)
            try {
                // Click Add Citizenship button
                WebElement addCitizenshipButton = driver.findElement(By.xpath("//button[contains(., 'Add Citizenship')]"));
                addCitizenshipButton.click();
                Thread.sleep(1000);

                // Select USA from dropdown
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click();
                Thread.sleep(1000);

                String usaOption = "mat-option-1260"; // USA - UNITED STATES OF AMERICA
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(usaOption))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error adding citizenship: " + e.getMessage());
            }

            // Add Passport
            try {
                // Click Add Passport button
                WebElement addPassportButton = driver.findElement(By.xpath("//button[contains(., 'Add Passport')]"));
                addPassportButton.click();
                Thread.sleep(1000);

                // Select passport type
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click();
                Thread.sleep(1000);

                // Select Regular Passport
                String passportTypeOption = "mat-option-1518"; // P - Regular
                WebElement typeOption = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(passportTypeOption))
                );
                typeOption.click();

                // Fill passport number
                WebElement passportNumberInput = driver.findElement(By.id("mat-input-19"));
                passportNumberInput.clear();
                passportNumberInput.sendKeys(data.getPassportNumber());

                // Select passport country (USA)
                List<WebElement> updatedDropdowns = driver.findElements(By.tagName("mat-select"));
                updatedDropdowns.get(updatedDropdowns.size() - 1).click();
                Thread.sleep(1000);

                String usaOption = "mat-option-1520"; // USA - UNITED STATES OF AMERICA
                WebElement countryOption = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(usaOption))
                );
                countryOption.click();

                // Fill passport issue date
                WebElement passportIssueInput = driver.findElement(By.id("mat-input-20"));
                passportIssueInput.clear();
                passportIssueInput.sendKeys(data.getPassportIssueDate());

                // Fill passport expiry date
                WebElement passportExpiryInput = driver.findElement(By.id("mat-input-21"));
                passportExpiryInput.clear();
                passportExpiryInput.sendKeys(data.getPassportExpiryDate());
            } catch (Exception e) {
                System.out.println("Error adding passport: " + e.getMessage());
            }

            // Add A#
            try {
                // Click Add A# button
                WebElement addAButton = driver.findElement(By.xpath("//button[contains(., 'Add A#')]"));
                addAButton.click();
                Thread.sleep(1000);

                // Fill A# number
                WebElement aNumberInput = driver.findElement(By.id("mat-input-22"));
                aNumberInput.clear();
                aNumberInput.sendKeys(data.getaNumber());
            } catch (Exception e) {
                System.out.println("Error adding A#: " + e.getMessage());
            }

            // Add Driver's License
            try {
                // Click Add Driver's License button
                WebElement addLicenseButton = driver.findElement(By.xpath("//button[contains(., \"Add Driver's License\")]"));
                addLicenseButton.click();
                Thread.sleep(1000);

                // Fill driver's license number
                WebElement licenseInput = driver.findElement(By.id("mat-input-23"));
                licenseInput.clear();
                licenseInput.sendKeys(data.getDriverLicense());

                // Select state
                List<WebElement> dropdowns = driver.findElements(By.tagName("mat-select"));
                dropdowns.get(dropdowns.size() - 1).click();
                Thread.sleep(1000);

                // Select a random US state
                String stateOption = "mat-option-" + (1774 + random.nextInt(62)); // Random between 1774-1835
                WebElement option = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id(stateOption))
                );
                option.click();
            } catch (Exception e) {
                System.out.println("Error adding driver's license: " + e.getMessage());
            }

            // Add SSN
            try {
                // Click Add SSN button
                WebElement addSsnButton = driver.findElement(By.xpath("//button[contains(., 'Add SSN')]"));
                addSsnButton.click();
                Thread.sleep(1000);

                // Wait for SSN input to appear and fill it
                wait.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@class, 'mat-input-element') and contains(@id, 'mat-input')]"))
                );

                // Find the most recently added input field (likely the SSN input)
                List<WebElement> inputFields = driver.findElements(By.xpath("//input[contains(@class, 'mat-input-element') and contains(@id, 'mat-input')]"));
                WebElement ssnInput = inputFields.get(inputFields.size() - 1);
                ssnInput.clear();
                ssnInput.sendKeys(data.getSsn());
            } catch (Exception e) {
                System.out.println("Error adding SSN: " + e.getMessage());
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