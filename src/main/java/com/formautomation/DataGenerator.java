package com.formautomation;

import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to generate random but valid personal data
 */
public class DataGenerator {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Generate random personal data
     * @return PersonData object with all needed information
     */
    public static PersonData generatePersonData() {
        PersonData data = new PersonData();

        // Generate first and last name
        data.setFirstName(faker.name().firstName());
        data.setLastName(faker.name().lastName());

        // Generate DOB between 18-70 years old
        Date dobDate = faker.date().birthday(18, 70);
        data.setDob(dateFormat.format(dobDate));

        // Generate passport number (9 alphanumeric characters)
        data.setPassportNumber(generateRandomString(9, true));

        // Generate passport issue date (1-5 years ago)
        int issueYearsAgo = random.nextInt(5) + 1;
        LocalDate issueDate = LocalDate.now().minusYears(issueYearsAgo);
        data.setPassportIssueDate(formatLocalDate(issueDate));

        // Generate passport expiry date (1-9 years in future)
        int expiryYears = random.nextInt(9) + 1;
        LocalDate expiryDate = LocalDate.now().plusYears(expiryYears);
        data.setPassportExpiryDate(formatLocalDate(expiryDate));

        // Generate driver's license (8-12 digit number)
        data.setDriverLicense(generateRandomString(8 + random.nextInt(5), false));

        // Generate SSN (format: XXX-XX-XXXX)
        String ssn = String.format("%03d-%02d-%04d",
                100 + random.nextInt(900),
                10 + random.nextInt(90),
                1000 + random.nextInt(9000));
        data.setSsn(ssn);

        // Generate A-Number (9 digits)
        data.setaNumber(generateRandomDigits(9));

        // Print the generated data for reference
        System.out.println("Generated random data:");
        System.out.println(data);

        return data;
    }

    /**
     * Generate a random string of specified length
     * @param length Length of the string
     * @param includeAlpha Whether to include alphabetic characters
     * @return Random string
     */
    private static String generateRandomString(int length, boolean includeAlpha) {
        String chars = includeAlpha ?
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789" :
                "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Generate a random string of digits
     * @param length Length of the string
     * @return Random string of digits
     */
    private static String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Format a LocalDate object to MM/dd/yyyy format
     * @param date LocalDate to format
     * @return Formatted date string
     */
    private static String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return date.format(formatter);
    }
}