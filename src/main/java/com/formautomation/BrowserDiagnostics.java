package com.formautomation;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.util.Map;

/**
 * Utility class for diagnosing browser and WebDriver issues
 */
public class BrowserDiagnostics {

    /**
     * Print diagnostic information about the current system and WebDriver setup
     */
    public static void printDiagnosticInfo() {
        System.out.println("\n======= ENVIRONMENT DIAGNOSTICS =======");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("User Directory: " + System.getProperty("user.dir"));

        // Check ChromeDriver existence
        File chromeDriver = new File("chromedriver.exe");
        System.out.println("ChromeDriver exists: " + chromeDriver.exists());
        if (chromeDriver.exists()) {
            System.out.println("ChromeDriver path: " + chromeDriver.getAbsolutePath());
            System.out.println("ChromeDriver can execute: " + chromeDriver.canExecute());
        }

        // Check if Chrome is running
        boolean isChromeRunning = false;
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
            String taskList = scanner.hasNext() ? scanner.next() : "";
            isChromeRunning = taskList.contains("chrome.exe");
            System.out.println("Chrome already running: " + isChromeRunning);
        } catch (Exception e) {
            System.out.println("Could not check if Chrome is running: " + e.getMessage());
        }

        System.out.println("====================================\n");
    }

    /**
     * Print information about the active WebDriver session
     * @param driver The WebDriver instance
     */
    public static void printWebDriverInfo(WebDriver driver) {
        if (driver == null) {
            System.out.println("WebDriver is null!");
            return;
        }

        System.out.println("\n======= WEBDRIVER INFO =======");
        try {
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            System.out.println("Browser Name: " + caps.getBrowserName());
            System.out.println("Browser Version: " + caps.getBrowserVersion());

            // Get ChromeDriver info
            Object chromeDriverInfo = caps.getCapability("chrome");
            if (chromeDriverInfo instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> chromeInfo = (Map<String, Object>) chromeDriverInfo;
                System.out.println("ChromeDriver Version: " + chromeInfo.get("chromedriverVersion"));
            }

            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page Title: " + driver.getTitle());
        } catch (Exception e) {
            System.out.println("Error getting WebDriver info: " + e.getMessage());
        }
        System.out.println("===========================\n");
    }
}