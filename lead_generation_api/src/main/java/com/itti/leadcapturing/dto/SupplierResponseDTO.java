package com.itti.leadcapturing.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SupplierResponseDTO {
    
    private Long id;
    private String companyName;
    private String companyType;
    private String industrySector;
    private String contactPersonName;
    private String contactPersonDesignation;
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
    private String tanNumber;
    private String website;
    private List<SupplierLocationResponseDTO> locations;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    // Constructors
    public SupplierResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public String getIndustrySector() { return industrySector; }
    public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }

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

    public String getTanNumber() { return tanNumber; }
    public void setTanNumber(String tanNumber) { this.tanNumber = tanNumber; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public List<SupplierLocationResponseDTO> getLocations() { return locations; }
    public void setLocations(List<SupplierLocationResponseDTO> locations) { this.locations = locations; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}