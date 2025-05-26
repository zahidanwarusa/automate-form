package com.formautomation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;

public class FormAutomation {
    public static void main(String[] args) {
        System.out.println("Starting form automation...");

        // Print diagnostic information before starting
        BrowserDiagnostics.printDiagnosticInfo();

        // Generate random person data
        PersonData personData = DataGenerator.generatePersonData();

        // Save data to Excel for future reference (initial save without TECS ID)
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
            String targetUrl = "https://sasq-sat.cbp.dhs.gov/person?query=person"; // Your target URL
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
                boolean secondPageSuccess = FormFiller.fillSecondPage(driver, personData);

                if (secondPageSuccess) {
                    System.out.println("✅ Second page completed successfully!");

                    // Wait a moment for any post-submission processing
                    Thread.sleep(3000);

                    // Capture TECS ID from the page
                    String tecsId = captureTecsId(driver);

                    // Update PersonData and Excel with TECS ID
                    updatePersonDataWithTecsId(personData, tecsId);

                    System.out.println("✅ Form automation completed successfully with TECS ID capture!");
                } else {
                    System.out.println("❌ Failed to complete the second page.");
                }
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

    /**
     * Capture TECS ID from the page after form submission
     * @param driver WebDriver instance
     * @return TECS ID string or null if not found
     */
    private static String captureTecsId(WebDriver driver) {
        try {
            System.out.println("🔍 Attempting to capture TECS ID from page...");

            // Wait a bit for the page to load completely after submission
            Thread.sleep(5000);

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Try multiple strategies to find TECS ID
            String tecsId = null;

            // Strategy 1: Look for "TECS ID:" followed by the actual ID (MOST SPECIFIC)
            tecsId = (String) js.executeScript(
                    "var elements = document.querySelectorAll('*');" +
                            "for (var i = 0; i < elements.length; i++) {" +
                            "  var text = elements[i].textContent || elements[i].innerText || '';" +
                            "  var match = text.match(/TECS\\s+ID\\s*:\\s*([A-Z0-9]{10,20})/i);" +
                            "  if (match && match[1]) {" +
                            "    console.log('Found TECS ID via Strategy 1 (TECS ID:):', match[1]);" +
                            "    return match[1];" +
                            "  }" +
                            "}" +
                            "return null;"
            );

            if (tecsId != null && !tecsId.trim().isEmpty()) {
                System.out.println("✅ TECS ID captured via Strategy 1 (TECS ID:): " + tecsId);
                return tecsId.trim();
            }

            // Strategy 2: Look in success message elements specifically
            tecsId = (String) js.executeScript(
                    "var successElements = document.querySelectorAll('.tecs-submitted-message, .tecs-flex-item, .success, .confirmation');" +
                            "for (var i = 0; i < successElements.length; i++) {" +
                            "  var text = successElements[i].textContent || successElements[i].innerText || '';" +
                            "  var match = text.match(/TECS\\s+ID\\s*:\\s*([A-Z0-9]{10,20})/i);" +
                            "  if (match && match[1]) {" +
                            "    console.log('Found TECS ID via Strategy 2 (success elements):', match[1]);" +
                            "    return match[1];" +
                            "  }" +
                            "}" +
                            "return null;"
            );

            if (tecsId != null && !tecsId.trim().isEmpty()) {
                System.out.println("✅ TECS ID captured via Strategy 2 (success elements): " + tecsId);
                return tecsId.trim();
            }

            // Strategy 3: Look for the specific pattern P3G followed by numbers and letters
            tecsId = (String) js.executeScript(
                    "var bodyText = document.body.textContent || document.body.innerText || '';" +
                            "var match = bodyText.match(/\\b(P[0-9A-Z]{2}[0-9]{8}[A-Z0-9]{2})\\b/g);" +
                            "if (match && match.length > 0) {" +
                            "  console.log('Found TECS ID via Strategy 3 (P3G pattern):', match[0]);" +
                            "  return match[0];" +
                            "}" +
                            "return null;"
            );

            if (tecsId != null && !tecsId.trim().isEmpty()) {
                System.out.println("✅ TECS ID captured via Strategy 3 (P3G pattern): " + tecsId);
                return tecsId.trim();
            }

            // Strategy 4: Look for any text that contains "created" and extract nearby IDs
            tecsId = (String) js.executeScript(
                    "var elements = document.querySelectorAll('*');" +
                            "for (var i = 0; i < elements.length; i++) {" +
                            "  var text = elements[i].textContent || elements[i].innerText || '';" +
                            "  if (text.toLowerCase().includes('created') || text.toLowerCase().includes('successfully')) {" +
                            "    var match = text.match(/([A-Z][0-9A-Z]{10,20})/g);" +
                            "    if (match) {" +
                            "      for (var j = 0; j < match.length; j++) {" +
                            "        if (match[j].length >= 10 && match[j].match(/^[A-Z][0-9A-Z]+$/)) {" +
                            "          console.log('Found TECS ID via Strategy 4 (created message):', match[j]);" +
                            "          return match[j];" +
                            "        }" +
                            "      }" +
                            "    }" +
                            "  }" +
                            "}" +
                            "return null;"
            );

            if (tecsId != null && !tecsId.trim().isEmpty()) {
                System.out.println("✅ TECS ID captured via Strategy 4 (created message): " + tecsId);
                return tecsId.trim();
            }

            // Strategy 5: Look for any alphanumeric string that starts with a letter and is 10+ chars
            tecsId = (String) js.executeScript(
                    "var bodyText = document.body.textContent || document.body.innerText || '';" +
                            "var matches = bodyText.match(/\\b[A-Z][A-Z0-9]{9,}\\b/g);" +
                            "if (matches && matches.length > 0) {" +
                            "  // Filter out common words that might match this pattern" +
                            "  var filtered = matches.filter(function(m) {" +
                            "    return !m.match(/^(SUBMITTED|CREATED|SUCCESSFULLY|UNCLASSIFIED)$/i);" +
                            "  });" +
                            "  if (filtered.length > 0) {" +
                            "    console.log('Found potential TECS ID via Strategy 5:', filtered[0]);" +
                            "    return filtered[0];" +
                            "  }" +
                            "}" +
                            "return null;"
            );

            if (tecsId != null && !tecsId.trim().isEmpty()) {
                System.out.println("✅ TECS ID captured via Strategy 5 (alphanumeric): " + tecsId);
                return tecsId.trim();
            }

            System.out.println("❌ Could not find TECS ID on the page");

            // Debug: Print page content to console for manual inspection
            String pageContent = (String) js.executeScript("return document.body.textContent || document.body.innerText || '';");
            System.out.println("📄 Page content for manual inspection:");
            System.out.println("FULL PAGE TEXT:");
            System.out.println(pageContent);

            // Also try to find any text containing "TECS" for debugging
            String tecsRelatedText = (String) js.executeScript(
                    "var elements = document.querySelectorAll('*');" +
                            "var tecsTexts = [];" +
                            "for (var i = 0; i < elements.length; i++) {" +
                            "  var text = elements[i].textContent || elements[i].innerText || '';" +
                            "  if (text.toLowerCase().includes('tecs')) {" +
                            "    tecsTexts.push(text.trim());" +
                            "  }" +
                            "}" +
                            "return tecsTexts.join(' | ');"
            );

            System.out.println("🔍 All TECS-related text found:");
            System.out.println(tecsRelatedText);

            return null;

        } catch (Exception e) {
            System.err.println("❌ Error capturing TECS ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update PersonData with TECS ID and save to Excel
     * @param personData The PersonData object to update
     * @param tecsId The captured TECS ID
     */
    private static void updatePersonDataWithTecsId(PersonData personData, String tecsId) {
        try {
            if (tecsId != null && !tecsId.trim().isEmpty()) {
                personData.setTecsId(tecsId.trim());
                System.out.println("✅ PersonData updated with TECS ID: " + tecsId);

                // Update Excel file with complete data including TECS ID
                ExcelManager.updateExcelWithTecsId(personData);

                System.out.println("✅ Final PersonData: " + personData.toString());
            } else {
                personData.setTecsId("NOT_CAPTURED");
                System.out.println("⚠️ TECS ID not captured, setting to 'NOT_CAPTURED'");

                // Still update Excel with available data
                ExcelManager.updateExcelWithTecsId(personData);
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating PersonData with TECS ID: " + e.getMessage());
            e.printStackTrace();
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