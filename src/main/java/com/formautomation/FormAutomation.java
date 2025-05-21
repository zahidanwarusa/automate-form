package com.formautomation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class FormAutomation {
    public static void main(String[] args) {
        System.out.println("Starting form automation...");

        // Generate random person data
        PersonData personData = DataGenerator.generatePersonData();

        // Save data to Excel for future reference
        ExcelManager.saveDataToExcel(personData);

        // Setup the WebDriver
        WebDriver driver = setupDriver();

        if (driver == null) {
            System.out.println("Failed to initialize WebDriver. Exiting...");
            return;
        }

        try {
            // Navigate to the website
            System.out.println("Navigating to the website...");
            driver.get("https://xyz.com"); // Make sure to include the protocol (http:// or https://)

            // Wait for the page to load
            Thread.sleep(3000);

            // Fill out the first page
            boolean firstPageSuccess = FormFiller.fillFirstPage(driver, personData);

            if (firstPageSuccess) {
                // Wait for second page to load
                Thread.sleep(5000);

                // Fill out the second page
                FormFiller.fillSecondPage(driver, personData);

                // Wait a bit before finishing
                Thread.sleep(5000);

                System.out.println("Form automation completed successfully!");
            } else {
                System.out.println("Failed to complete the first page. Stopping.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during automation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Keep the browser open for review (you can close it by uncommenting the line below)
            // driver.quit();
            System.out.println("Script execution completed.");
        }
    }

    private static WebDriver setupDriver() {
        try {
            // Set the path to the ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); // Update with your path

            // Configure ChromeOptions
            ChromeOptions options = new ChromeOptions();

            // Use an existing Chrome profile (comment out these lines if not using a profile)
            String userDataDir = "C:\\Users\\YourUsername\\AppData\\Local\\Google\\Chrome\\User Data"; // Update with your path
            String profileDir = "Profile 1"; // Update with your profile directory name
            options.addArguments("--user-data-dir=" + userDataDir);
            options.addArguments("--profile-directory=" + profileDir);

            // Add standard options
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

            // Disable notifications, password saving prompts, and other popups
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            // Uncomment the line below if you want to run headless (no browser window)
            // options.addArguments("--headless=new");

            // Initialize and return the WebDriver
            return new ChromeDriver(options);
        } catch (Exception e) {
            System.out.println("Error setting up WebDriver: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}