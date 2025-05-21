package com.formautomation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class FormAutomation {

    /**
     * Set up the WebDriver with appropriate options
     * @return Configured WebDriver instance
     */
    private static WebDriver setupDriver() {
        try {
            String browserType = System.getProperty("browser", "chrome").toLowerCase();

            switch (browserType) {
                case "chrome":
                    return setupChromeDriver();
                case "firefox":
                    return setupFirefoxDriver();
                case "edge":
                    return setupEdgeDriver();
                default:
                    System.out.println("Unsupported browser type: " + browserType + ". Defaulting to Chrome.");
                    return setupChromeDriver();
            }
        } catch (Exception e) {
            System.out.println("Error setting up WebDriver: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static WebDriver setupChromeDriver() {
        System.out.println("Setting up Chrome driver...");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");

        // Disable 'Save Password' prompt
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    private static WebDriver setupFirefoxDriver() {
        System.out.println("Setting up Firefox driver...");

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        return new FirefoxDriver(options);
    }

    private static WebDriver setupEdgeDriver() {
        System.out.println("Setting up Edge driver...");

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        return new EdgeDriver(options);
    }

    public static void main(String[] args) {
        System.out.println("Starting form automation...");

        // Print diagnostic information before starting
        BrowserDiagnostics.printDiagnosticInfo();

        // Generate random person data
        PersonData personData = DataGenerator.generatePersonData();

        // Save data to Excel for future reference
        ExcelManager.saveDataToExcel(personData);

        // Print the generated data for reference
        System.out.println("Generated person data:");
        System.out.println("First Name: " + personData.getFirstName());
        System.out.println("Last Name: " + personData.getLastName());
        System.out.println("DOB: " + personData.getDob());
        System.out.println("Passport #: " + personData.getPassportNumber());
        System.out.println("Driver's License: " + personData.getDriverLicense());
        System.out.println("A#: " + personData.getaNumber());
        System.out.println("SSN: " + personData.getSsn());

        // Setup the WebDriver
        WebDriver driver = setupDriver();

        if (driver == null) {
            System.out.println("Failed to initialize WebDriver. Exiting...");
            return;
        }

        try {
            // Set longer implicit and explicit waits
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Print WebDriver info
            BrowserDiagnostics.printWebDriverInfo(driver);

            // Navigate to the website with properly formatted URL
            System.out.println("Navigating to the website...");

            // First, let's make sure the browser is working by going to Google
            System.out.println("Testing navigation with Google...");
            driver.get("https://www.google.com");
            System.out.println("Successfully loaded Google. Now navigating to target site...");

            // Wait a bit longer to make sure Google fully loads
            Thread.sleep(5000);

            // Now try to navigate to the actual site
            String targetUrl = "https://sasq-sat.cbp.dhs.gov/person?query=person";
            System.out.println("Navigating to: " + targetUrl);
            driver.get(targetUrl);
            System.out.println("Navigation initiated. Waiting for page to load...");

            // Wait for the page to load fully - increased from 15 to 20 seconds
            Thread.sleep(20000);

            // Print current URL to diagnose navigation issues
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page title: " + driver.getTitle());

            // Fill out the first page
            boolean firstPageSuccess = FormFiller.fillFirstPage(driver, personData);

            if (firstPageSuccess) {
                // Wait longer for second page to load - increased from 5 to 10 seconds
                Thread.sleep(10000);

                // Fill out the second page
                boolean secondPageSuccess = FormFiller.fillSecondPage(driver, personData);

                if (secondPageSuccess) {
                    System.out.println("Form automation completed successfully!");
                } else {
                    System.out.println("Second page automation completed with some issues. Check logs for details.");
                }

                // Wait a bit before finishing - increased from 5 to 15 seconds
                Thread.sleep(15000);
            } else {
                System.out.println("Failed to complete the first page. Stopping.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during automation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Keep the browser open for a moment before closing
            try {
                System.out.println("Automation complete. Keeping browser open for 30 seconds for review...");
                Thread.sleep(30000); // Increased from 10 to 30 seconds
            } catch (InterruptedException e) {
                // Ignore
            }

            // Close the browser
            driver.quit();
            System.out.println("Browser closed. Script execution completed.");
        }
    }
}