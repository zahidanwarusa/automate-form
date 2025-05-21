package com.formautomation;

/**
 * Class to hold all the personal data for form filling
 */
public class PersonData {
    private String firstName;
    private String lastName;
    private String dob;
    private String passportNumber;
    private String passportIssueDate;
    private String passportExpiryDate;
    private String driverLicense;
    private String ssn;
    private String aNumber;

    // Default constructor
    public PersonData() {
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportIssueDate() {
        return passportIssueDate;
    }

    public void setPassportIssueDate(String passportIssueDate) {
        this.passportIssueDate = passportIssueDate;
    }

    public String getPassportExpiryDate() {
        return passportExpiryDate;
    }

    public void setPassportExpiryDate(String passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getaNumber() {
        return aNumber;
    }

    public void setaNumber(String aNumber) {
        this.aNumber = aNumber;
    }

    @Override
    public String toString() {
        return "PersonData{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportIssueDate='" + passportIssueDate + '\'' +
                ", passportExpiryDate='" + passportExpiryDate + '\'' +
                ", driverLicense='" + driverLicense + '\'' +
                ", ssn='" + ssn + '\'' +
                ", aNumber='" + aNumber + '\'' +
                '}';
    }
}