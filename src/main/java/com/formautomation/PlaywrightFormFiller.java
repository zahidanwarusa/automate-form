package com.formautomation;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;

public class RobustPlaywrightFormFiller {

    public static void main(String[] args) {
        // Generate random data
        PersonData personData = DataGenerator.generatePersonData();
        ExcelManager.saveDataToExcel(personData);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(500) // Slow down for visibility
                    .setArgs(java.util.Arrays.asList("--start-maximized")));

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            System.out.println("Navigating to the website...");
            page.navigate("https://sasq-sat.cbp.dhs.gov/person?query=person");

            // Wait for page to load completely
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Fill first page
            if (fillFirstPage(page, personData)) {
                System.out.println("First page completed, proceeding to second page...");

                // Wait for navigation to second page
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Fill second page
                fillSecondPage(page, personData);
            }

            // Keep browser open for review
            System.out.println("Form completed! Keeping browser open for 10 seconds...");
            page.waitForTimeout(10000);

        } catch (Exception e) {
            System.err.println("Error in form automation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean fillFirstPage(Page page, PersonData data) {
        try {
            System.out.println("=== FILLING FIRST PAGE ===");

            // Debug: Print page info
            printPageInfo(page, "First Page");

            // Wait for Angular to be ready
            waitForAngularReady(page);

            // Fill the form fields
            System.out.println("Filling lastName: " + data.getLastName());
            page.fill("#lastName", data.getLastName());

            System.out.println("Filling firstName: " + data.getFirstName());
            page.fill("#firstName", data.getFirstName());

            System.out.println("Filling DOB: " + data.getDob());
            page.fill("#dob", data.getDob());

            // Take screenshot for debugging
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("first-page-filled.png")));

            // Find and click search button with multiple strategies
            System.out.println("Looking for search button...");
            if (clickSearchButton(page)) {
                System.out.println("Search button clicked successfully!");

                // Wait for search results
                waitForSearchResults(page);

                // Find and click TECS Lookout button
                System.out.println("Looking for TECS Lookout button...");
                if (clickTecsLookoutButton(page)) {
                    System.out.println("TECS Lookout button clicked successfully!");
                    return true;
                } else {
                    System.err.println("Failed to click TECS Lookout button");
                    return false;
                }
            } else {
                System.err.println("Failed to click search button");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error in fillFirstPage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean clickSearchButton(Page page) {
        // Strategy 1: Try by button text
        try {
            System.out.println("Strategy 1: Looking for button with text 'Search'");
            Locator searchButton = page.locator("button:has-text('Search')");
            if (searchButton.count() > 0) {
                searchButton.first().click();
                System.out.println("Found and clicked search button by text");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Strategy 1 failed: " + e.getMessage());
        }

        // Strategy 2: Try by CSS class
        try {
            System.out.println("Strategy 2: Looking for button with search classes");
            String[] searchSelectors = {
                    "button.btn.btn-primary.search-btn",
                    "button.search-btn",
                    "button[searchsubmit]",
                    "button.btn-primary",
                    "input[type='submit']"
            };

            for (String selector : searchSelectors) {
                Locator button = page.locator(selector);
                if (button.count() > 0) {
                    button.first().click();
                    System.out.println("Found and clicked search button with selector: " + selector);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Strategy 2 failed: " + e.getMessage());
        }

        // Strategy 3: Try by button type
        try {
            System.out.println("Strategy 3: Looking for submit buttons");
            Locator submitButtons = page.locator("button[type='submit'], input[type='submit']");
            if (submitButtons.count() > 0) {
                submitButtons.first().click();
                System.out.println("Found and clicked submit button");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Strategy 3 failed: " + e.getMessage());
        }

        // Strategy 4: Look for any button and check text content
        try {
            System.out.println("Strategy 4: Checking all buttons for search-related text");
            Locator allButtons = page.locator("button");
            int buttonCount = allButtons.count();
            System.out.println("Found " + buttonCount + " buttons on page");

            for (int i = 0; i < buttonCount; i++) {
                try {
                    Locator button = allButtons.nth(i);
                    String buttonText = button.textContent().toLowerCase();
                    System.out.println("Button " + i + " text: '" + buttonText + "'");

                    if (buttonText.contains("search") || buttonText.contains("submit") || buttonText.contains("find")) {
                        button.click();
                        System.out.println("Clicked button with text: " + buttonText);
                        return true;
                    }
                } catch (Exception e) {
                    // Continue to next button
                }
            }
        } catch (Exception e) {
            System.out.println("Strategy 4 failed: " + e.getMessage());
        }

        // Strategy 5: Try JavaScript execution
        try {
            System.out.println("Strategy 5: Using JavaScript to find and click search button");
            Object result = page.evaluate(
                    "() => {" +
                            "  const buttons = Array.from(document.querySelectorAll('button, input[type=\"submit\"]'));" +
                            "  console.log('Found buttons:', buttons.length);" +
                            "  for (let btn of buttons) {" +
                            "    const text = (btn.textContent || btn.value || '').toLowerCase();" +
                            "    const className = (btn.className || '').toLowerCase();" +
                            "    console.log('Button text:', text, 'class:', className);" +
                            "    if (text.includes('search') || className.includes('search') || " +
                            "        text.includes('submit') || btn.type === 'submit') {" +
                            "      btn.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "      setTimeout(() => btn.click(), 500);" +
                            "      return 'clicked: ' + text;" +
                            "    }" +
                            "  }" +
                            "  return 'no search button found';" +
                            "}"
            );

            System.out.println("JavaScript result: " + result);
            if (result.toString().startsWith("clicked:")) {
                page.waitForTimeout(1000);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Strategy 5 failed: " + e.getMessage());
        }

        // Strategy 6: Take screenshot and list all elements for debugging
        try {
            System.out.println("Strategy 6: Debug - taking screenshot and listing elements");
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("search-button-debug.png")));

            // List all interactive elements
            Object elements = page.evaluate(
                    "() => {" +
                            "  const interactive = Array.from(document.querySelectorAll('button, input, a, [onclick]'));" +
                            "  return interactive.map(el => ({" +
                            "    tag: el.tagName," +
                            "    type: el.type," +
                            "    text: (el.textContent || el.value || '').trim().substring(0, 50)," +
                            "    class: el.className," +
                            "    id: el.id" +
                            "  }));" +
                            "}"
            );

            System.out.println("All interactive elements found:");
            System.out.println(elements);

        } catch (Exception e) {
            System.out.println("Debug strategy failed: " + e.getMessage());
        }

        System.err.println("All search button strategies failed!");
        return false;
    }

    private static boolean clickTecsLookoutButton(Page page) {
        try {
            // Scroll down to find the button
            page.evaluate("window.scrollBy(0, 300)");
            page.waitForTimeout(2000);

            // Strategy 1: Find by text content
            System.out.println("Looking for TECS Lookout button by text...");
            String[] tecsSelectors = {
                    "text=Create TECS Lookout",
                    "a:has-text('Create TECS Lookout')",
                    "a:has-text('TECS Lookout')",
                    ".event-button:has-text('Create TECS Lookout')",
                    ".event-button a",
                    "span.event-button a"
            };

            for (String selector : tecsSelectors) {
                try {
                    Locator button = page.locator(selector);
                    if (button.count() > 0) {
                        button.first().click();
                        System.out.println("Clicked TECS Lookout button with selector: " + selector);
                        return true;
                    }
                } catch (Exception e) {
                    // Try next selector
                }
            }

            // Strategy 2: JavaScript search
            Object result = page.evaluate(
                    "() => {" +
                            "  const links = Array.from(document.querySelectorAll('a, button, span'));" +
                            "  for (let link of links) {" +
                            "    const text = (link.textContent || '').toLowerCase();" +
                            "    if (text.includes('tecs') && text.includes('lookout')) {" +
                            "      link.scrollIntoView({behavior: 'smooth', block: 'center'});" +
                            "      setTimeout(() => link.click(), 500);" +
                            "      return 'clicked tecs lookout';" +
                            "    }" +
                            "  }" +
                            "  return 'tecs lookout not found';" +
                            "}"
            );

            if (result.toString().contains("clicked")) {
                page.waitForTimeout(1000);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Error clicking TECS Lookout button: " + e.getMessage());
            return false;
        }
    }

    public static boolean fillSecondPage(Page page, PersonData data) {
        try {
            System.out.println("=== FILLING SECOND PAGE ===");

            // Wait for Angular Material page to load
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.waitForTimeout(5000);

            // Debug: Print page info
            printPageInfo(page, "Second Page");

            // Wait for Angular Material components
            page.waitForSelector("mat-form-field, mat-select", new Page.WaitForSelectorOptions().setTimeout(30000));

            // Take screenshot
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("second-page-loaded.png")));

            // Handle dropdowns by position and text
            handleDropdowns(page);

            // Fill remarks
            fillRemarks(page);

            // Handle Add buttons
            handleAddButtons(page, data);

            System.out.println("Second page completed successfully!");
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("second-page-completed.png")));

            return true;

        } catch (Exception e) {
            System.err.println("Error in fillSecondPage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void handleDropdowns(Page page) {
        System.out.println("Handling dropdowns...");

        String[][] dropdownOptions = {
                {"0", "OB - OUTBOUND SUBJECT"},
                {"1", "AB - AG/BIO COUNTERMEASURES"},
                {"2", "0 - NO NOTIFICATION"},
                {"3", "PVRVK - PROVISIONAL REVOCATION"},
                {"4", "0 - NOT ON PRIMARY"}
        };

        for (String[] option : dropdownOptions) {
            try {
                int index = Integer.parseInt(option[0]);
                String optionText = option[1];

                System.out.println("Selecting dropdown " + index + " with option: " + optionText);

                // Click dropdown
                page.locator("mat-select").nth(index).click();
                page.waitForTimeout(1000);

                // Select option by text
                page.locator("mat-option:has-text('" + optionText + "')").first().click();
                page.waitForTimeout(1000);

                System.out.println("Successfully selected: " + optionText);

            } catch (Exception e) {
                System.out.println("Failed to select dropdown " + option[0] + ": " + e.getMessage());
            }
        }
    }

    private static void fillRemarks(Page page) {
        try {
            System.out.println("Filling remarks...");
            page.fill("textarea", "Automated test entry - Playwright");
            System.out.println("Remarks filled successfully");
        } catch (Exception e) {
            System.out.println("Failed to fill remarks: " + e.getMessage());
        }
    }

    private static void handleAddButtons(Page page, PersonData data) {
        System.out.println("Handling Add buttons...");

        // Add Sex
        try {
            page.locator("text=Add Sex").click();
            page.waitForTimeout(1000);
            page.locator("mat-select").last().click();
            page.locator("mat-option:has-text('F - FEMALE')").first().click();
            System.out.println("Added Sex successfully");
        } catch (Exception e) {
            System.out.println("Failed to add sex: " + e.getMessage());
        }

        // Add Race
        try {
            page.locator("text=Add Race").click();
            page.waitForTimeout(1000);
            page.locator("mat-select").last().click();
            page.locator("mat-option:has-text('A - ASIAN')").first().click();
            System.out.println("Added Race successfully");
        } catch (Exception e) {
            System.out.println("Failed to add race: " + e.getMessage());
        }

        // Add Name
        try {
            page.locator("text=Add Name").click();
            page.waitForTimeout(1000);

            // Fill name fields - find by label text
            page.locator("input").filter(new Locator.FilterOptions().setHasText("Last Name")).fill(data.getLastName());
            page.locator("input").filter(new Locator.FilterOptions().setHasText("First Name")).fill(data.getFirstName());
            System.out.println("Added Name successfully");
        } catch (Exception e) {
            System.out.println("Failed to add name: " + e.getMessage());
        }

        // Continue with other Add buttons...
    }

    private static void waitForAngularReady(Page page) {
        try {
            System.out.println("Waiting for Angular to be ready...");

            // Wait for document ready state
            page.waitForFunction("() => document.readyState === 'complete'");

            // Wait for Angular
            page.waitForFunction(
                    "() => {" +
                            "  if (typeof window.angular !== 'undefined') {" +
                            "    return window.angular.element(document.body).injector().get('$http').pendingRequests.length === 0;" +
                            "  }" +
                            "  return true;" +
                            "}"
            );

            page.waitForTimeout(2000);
            System.out.println("Angular is ready");

        } catch (Exception e) {
            System.out.println("Angular ready check failed, continuing anyway: " + e.getMessage());
        }
    }

    private static void waitForSearchResults(Page page) {
        try {
            System.out.println("Waiting for search results...");

            // Wait for loading indicators to disappear
            page.waitForFunction(
                    "() => document.querySelectorAll('.loading, .spinner, .loader').length === 0"
            );

            page.waitForTimeout(5000);
            System.out.println("Search results loaded");

        } catch (Exception e) {
            System.out.println("Search results wait completed with issues: " + e.getMessage());
        }
    }

    private static void printPageInfo(Page page, String pageName) {
        try {
            System.out.println("=== " + pageName.toUpperCase() + " DEBUG INFO ===");
            System.out.println("URL: " + page.url());
            System.out.println("Title: " + page.title());

            Object elementCounts = page.evaluate(
                    "() => ({" +
                            "  buttons: document.querySelectorAll('button').length," +
                            "  inputs: document.querySelectorAll('input').length," +
                            "  matSelects: document.querySelectorAll('mat-select').length," +
                            "  matFormFields: document.querySelectorAll('mat-form-field').length," +
                            "  links: document.querySelectorAll('a').length," +
                            "  textareas: document.querySelectorAll('textarea').length" +
                            "})"
            );

            System.out.println("Element counts: " + elementCounts);
            System.out.println("=====================================");

        } catch (Exception e) {
            System.out.println("Could not print page info: " + e.getMessage());
        }
    }
}