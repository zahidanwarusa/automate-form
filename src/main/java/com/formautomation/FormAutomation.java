package com.formautomation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class FormAutomation {
    public static void main(String[] args) {
        System.out.println("Starting form automation...");

        // Print diagnostic information before starting
        BrowserDiagnostics.printDiagnosticInfo();

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
            // Print WebDriver info
            BrowserDiagnostics.printWebDriverInfo(driver);

            // Navigate to the website - using a properly formatted URL with protocol
            System.out.println("Navigating to the website...");

            // First, let's make sure the browser is working by going to Google
            System.out.println("Testing navigation with Google...");
            driver.get("https://www.google.com");
            System.out.println("Successfully loaded Google. Now navigating to target site...");

            // Wait a bit longer to make sure Google fully loads
            Thread.sleep(5000);

            // Now try to navigate to the actual site (replace with your actual URL)
            String targetUrl = "https://xyz.com"; // Update this with your actual URL
            System.out.println("Navigating to: " + targetUrl);
            driver.get(targetUrl);
            System.out.println("Navigation initiated. Waiting for page to load...");

            // Wait longer for the page to load - increased from 8 to 15 seconds
            Thread.sleep(15000);

            // Print current URL to diagnose navigation issues
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

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
            // Keep the browser open for a moment before closing
            try {
                System.out.println("Automation complete. Keeping browser open for 10 seconds for review...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // Ignore
            }

            // Close the browser
            driver.quit();
            System.out.println("Browser closed. Script execution completed.");
        }
    }

    private static WebDriver setupDriver() {
        try {
            // Set the path to the ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "chromedriver.exe"); // Update with your path

            // Configure ChromeOptions
            ChromeOptions options = new ChromeOptions();

            // Basic options
            options.addArguments("--start-maximized");
            options.addArguments("--remote-allow-origins=*");

            // Disable automation flags
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            // IMPORTANT: Do not use existing profile for now as it's causing issues
            // We'll use a fresh Chrome instance instead

            // Disable notifications and popups
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            // Set additional options to avoid DevTools issues
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            System.out.println("Initializing Chrome Driver...");
            WebDriver driver = new ChromeDriver(options);
            System.out.println("Chrome Driver initialized successfully!");
            return driver;
        } catch (Exception e) {
            System.out.println("Error setting up WebDriver: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}