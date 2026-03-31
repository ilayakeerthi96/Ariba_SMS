
package com.itti.leadcapturing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class BuyerCreateDTO {
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String companyType;
    
    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;
    
    private String contactPersonDesignation;
    
    @Email(message = "Valid email is required")
    private String contactPersonEmail;
    
    private String contactPersonPhone;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String gstNumber;
    private String panNumber;
    private String cinNumber;
    private String website;

    // Constructors
    public BuyerCreateDTO() {}

    public BuyerCreateDTO(String companyName, String contactPersonName, String contactPersonEmail,
                          String contactPersonPhone, String addressLine1, String city, String state, String postalCode) {
        this.companyName = companyName;
        this.contactPersonName = contactPersonName;
        this.contactPersonEmail = contactPersonEmail;
        this.contactPersonPhone = contactPersonPhone;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getContactPersonDesignation() { return contactPersonDesignation; }
    public void setContactPersonDesignation(String contactPersonDesignation) { this.contactPersonDesignation = contactPersonDesignation; }

    public String getContactPersonEmail() { return contactPersonEmail; }
    public void setContactPersonEmail(String contactPersonEmail) { this.contactPersonEmail = contactPersonEmail; }

    public String getContactPersonPhone() { return contactPersonPhone; }
    public void setContactPersonPhone(String contactPersonPhone) { this.contactPersonPhone = contactPersonPhone; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getCinNumber() { return cinNumber; }
    public void setCinNumber(String cinNumber) { this.cinNumber = cinNumber; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}