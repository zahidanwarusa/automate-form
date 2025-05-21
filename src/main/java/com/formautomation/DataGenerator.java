package com.formautomation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class to generate random person data
 */
public class DataGenerator {
    private static final String[] FIRST_NAMES = {
            "James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles",
            "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson"
    };

    private static final Random random = new Random();

    /**
     * Generate random person data
     * @return PersonData with random values
     */
    public static PersonData generatePersonData() {
        PersonData data = new PersonData();

        // Generate random first and last name
        data.setFirstName(getRandomFirstName());
        data.setLastName(getRandomLastName());

        // Generate random DOB (18-80 years old)
        data.setDob(getRandomDOB(18, 80));

        // Generate a random passport number
        data.setPassportNumber("P" + randomNumberString(8));

        // Generate passport dates
        LocalDate today = LocalDate.now();
        data.setPassportIssueDate(formatDate(today.minusYears(random.nextInt(5) + 1)));
        data.setPassportExpiryDate(formatDate(today.plusYears(random.nextInt(5) + 5)));

        // Generate an A number (Alien Registration Number)
        data.setaNumber(randomNumberString(9));

        // Generate driver's license
        data.setDriverLicense("DL" + randomNumberString(8));

        // Generate SSN (Social Security Number)
        String ssn = randomNumberString(3) + "-" + randomNumberString(2) + "-" + randomNumberString(4);
        data.setSsn(ssn);

        // Generate height (5'0" - 6'6")
        int feet = 5 + random.nextInt(2);
        int inches = random.nextInt(12);
        data.setHeight(feet + "'" + inches + "\"");

        // Generate weight (120-250 lbs)
        data.setWeight(String.valueOf(120 + random.nextInt(131)));

        System.out.println("Generated random person data: " + data);
        return data;
    }

    /**
     * Get a random first name from the predefined list
     * @return A random first name
     */
    private static String getRandomFirstName() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    /**
     * Get a random last name from the predefined list
     * @return A random last name
     */
    private static String getRandomLastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    /**
     * Generate a random date of birth
     * @param minAge Minimum age in years
     * @param maxAge Maximum age in years
     * @return A date string in MM/DD/YYYY format
     */
    private static String getRandomDOB(int minAge, int maxAge) {
        LocalDate now = LocalDate.now();
        int ageRange = maxAge - minAge + 1;
        int randomAge = minAge + random.nextInt(ageRange);

        LocalDate dob = now.minusYears(randomAge)
                .minusDays(random.nextInt(365));

        return formatDate(dob);
    }

    /**
     * Format a LocalDate as MM/DD/YYYY
     * @param date The date to format
     * @return Formatted date string
     */
    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return date.format(formatter);
    }

    /**
     * Generate a random number string of a specific length
     * @param length The length of the string to generate
     * @return A string of random digits
     */
    private static String randomNumberString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0-9
        }
        return sb.toString();
    }
}