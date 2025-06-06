package com.formautomation;

/**
 * Class representing a person's data for form filling
 */
public class PersonData {
    private String tecsId; // TECS ID - First column
    private String firstName;
    private String lastName;
    private String dob;
    private String passportNumber;
    private String passportIssueDate;
    private String passportExpiryDate;
    private String driverLicense;
    private String aNumber;
    private String ssn;
    private String height;
    private String weight;

    // Constructor
    public PersonData() {
        // Default constructor
    }

    // TECS ID getter and setter
    public String getTecsId() {
        return tecsId;
    }

    public void setTecsId(String tecsId) {
        this.tecsId = tecsId;
    }

    // Existing getters and setters
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

    public String getaNumber() {
        return aNumber;
    }

    public void setaNumber(String aNumber) {
        this.aNumber = aNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "PersonData{" +
                "tecsId='" + tecsId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dob='" + dob + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", passportIssueDate='" + passportIssueDate + '\'' +
                ", passportExpiryDate='" + passportExpiryDate + '\'' +
                ", driverLicense='" + driverLicense + '\'' +
                ", aNumber='" + aNumber + '\'' +
                ", ssn='" + ssn + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}