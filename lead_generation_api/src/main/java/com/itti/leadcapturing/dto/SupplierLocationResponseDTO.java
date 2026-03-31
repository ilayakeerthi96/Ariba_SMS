package com.itti.leadcapturing.dto;

import java.util.List;

public class SupplierLocationResponseDTO {
    
    private Long id;
    private String locationName;
    private String locationType;
    private String locationContactName;
    private String locationContactEmail;
    private String locationContactPhone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String landlineNumber;
    private String faxNumber;
    private List<SupplierDepartmentResponseDTO> departments;

    // Constructors
    public SupplierLocationResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public String getLocationContactName() { return locationContactName; }
    public void setLocationContactName(String locationContactName) { this.locationContactName = locationContactName; }

    public String getLocationContactEmail() { return locationContactEmail; }
    public void setLocationContactEmail(String locationContactEmail) { this.locationContactEmail = locationContactEmail; }

    public String getLocationContactPhone() { return locationContactPhone; }
    public void setLocationContactPhone(String locationContactPhone) { this.locationContactPhone = locationContactPhone; }

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

    public String getLandlineNumber() { return landlineNumber; }
    public void setLandlineNumber(String landlineNumber) { this.landlineNumber = landlineNumber; }

    public String getFaxNumber() { return faxNumber; }
    public void setFaxNumber(String faxNumber) { this.faxNumber = faxNumber; }

    public List<SupplierDepartmentResponseDTO> getDepartments() { return departments; }
    public void setDepartments(List<SupplierDepartmentResponseDTO> departments) { this.departments = departments; }
}