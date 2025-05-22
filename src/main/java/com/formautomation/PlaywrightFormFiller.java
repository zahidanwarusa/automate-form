// Playwright Java Example - Much Better for Angular
package com.formautomation;

import com.microsoft.playwright.*;

public class PlaywrightFormFiller {

    public static void main(String[] args) {
        // Launch browser
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(1000)); // Slow down for visibility

            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            // Navigate to your form
            page.navigate("https://sasq-sat.cbp.dhs.gov/person?query=person");

            // Playwright automatically waits for elements - no manual waits needed!

            // Fill first page
            page.fill("#lastName", "Smith");
            page.fill("#firstName", "John");
            page.fill("#dob", "01/15/1990");

            // Click search button - Playwright finds it automatically
            page.click("text=Search");

            // Wait for results and click TECS Lookout
            page.click("text=Create TECS Lookout");

            // Second page - Angular Material handling
            // Playwright handles Angular Material much better

            // Select from dropdowns by visible text (much more reliable)
            page.selectOption("mat-select >> nth=0", "OB - OUTBOUND SUBJECT");
            page.selectOption("mat-select >> nth=1", "AB - AG/BIO COUNTERMEASURES");

            // Fill textarea by label
            page.fill("textarea", "Automated test entry");

            // Click buttons by text
            page.click("text=Add Sex");
            page.selectOption("mat-select >> nth=-1", "F - FEMALE"); // Last dropdown

            page.click("text=Add Race");
            page.selectOption("mat-select >> nth=-1", "A - ASIAN");

            // Much simpler and more reliable than Selenium!

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
