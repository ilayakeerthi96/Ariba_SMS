package com.itti.leadcapturing.dto;

/**
 * ✅ Supplier DTO for including in RFQ response
 */
public class SupplierDto {
    private Long id;
    private String companyName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String contactPersonName;
    private String city;
    private String state;

    public SupplierDto() {}

    public SupplierDto(Long id, String companyName, String contactPersonEmail) {
        this.id = id;
        this.companyName = companyName;
        this.contactPersonEmail = contactPersonEmail;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactPersonEmail() { return contactPersonEmail; }
    public void setContactPersonEmail(String contactPersonEmail) { this.contactPersonEmail = contactPersonEmail; }

    public String getContactPersonPhone() { return contactPersonPhone; }
    public void setContactPersonPhone(String contactPersonPhone) { this.contactPersonPhone = contactPersonPhone; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    // public void setgetState() { return state; }
    public void setState(String state) { this.state = state; }
}