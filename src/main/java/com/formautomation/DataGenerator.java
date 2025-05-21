package com.formautomation;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for generating random person data
 */
public class DataGenerator {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    /**
     * Generate random person data
     * @return PersonData object with randomly generated data
     */
    public static PersonData generatePersonData() {
        PersonData data = new PersonData();

        // Generate basic personal information
        data.setFirstName(faker.name().firstName());
        data.setLastName(faker.name().lastName());

        // Generate date of birth (18-80 years ago)
        Date dobDate = faker.date().birthday(18, 80);
        String dob = formatDate(dobDate, "MM/dd/yyyy");
        data.setDob(dob);

        // Generate passport number (letter followed by 8 digits)
        String passportNumber = "P" + (10000000 + random.nextInt(90000000));
        data.setPassportNumber(passportNumber);

        // Generate passport issue date (1-5 years ago)
        Calendar issueCal = Calendar.getInstance();
        issueCal.add(Calendar.YEAR, -random.nextInt(5) - 1);
        String issueDate = formatDate(issueCal.getTime(), "MM/dd/yyyy");
        data.setPassportIssueDate(issueDate);

        // Generate passport expiry date (3-8 years from issue date)
        Calendar expiryCal = (Calendar) issueCal.clone();
        expiryCal.add(Calendar.YEAR, random.nextInt(6) + 3);
        String expiryDate = formatDate(expiryCal.getTime(), "MM/dd/yyyy");
        data.setPassportExpiryDate(expiryDate);

        // Generate driver's license number (letter followed by 7-12 digits)
        int dlLength = random.nextInt(6) + 7; // 7-12 digits
        StringBuilder dlBuilder = new StringBuilder("DL");
        for (int i = 0; i < dlLength; i++) {
            dlBuilder.append(random.nextInt(10));
        }
        data.setDriverLicense(dlBuilder.toString());

        // Generate A-Number (9 digits)
        StringBuilder aNumberBuilder = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            aNumberBuilder.append(random.nextInt(10));
        }
        data.setaNumber(aNumberBuilder.toString());

        // Generate SSN (3 digits, 2 digits, 4 digits with hyphens)
        String ssn = String.format("%03d-%02d-%04d",
                100 + random.nextInt(900),
                10 + random.nextInt(90),
                1000 + random.nextInt(9000));
        data.setSsn(ssn);

        // Generate height (in feet and inches format: x' y")
        int feet = 5 + random.nextInt(2); // 5 or 6 feet
        int inches = random.nextInt(12); // 0-11 inches
        data.setHeight(feet + "' " + inches + "\"");

        // Generate weight (in pounds, 100-250)
        int weight = 100 + random.nextInt(151);
        data.setWeight(String.valueOf(weight));

        System.out.println("Generated random person data: " + data);
        return data;
    }

    /**
     * Format a date into a string with the specified pattern
     * @param date The date to format
     * @param pattern The date pattern
     * @return Formatted date string
     */
    private static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}