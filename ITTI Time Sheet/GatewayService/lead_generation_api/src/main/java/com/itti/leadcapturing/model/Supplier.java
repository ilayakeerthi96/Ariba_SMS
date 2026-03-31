package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Company Details
    @NotBlank(message = "Company name is required")
    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_type")
    private String companyType; // e.g., Manufacturing, Trading, Services

    @Column(name = "industry_sector")
    private String industrySector; // e.g., IT, Logistics, Electrical, etc.

    // Contact Person Details
    @NotBlank(message = "Contact person name is required")
    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Column(name = "contact_person_designation")
    private String contactPersonDesignation;

    @Email
    @Column(name = "contact_person_email")
    private String contactPersonEmail;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Column(name = "contact_person_phone")
    private String contactPersonPhone;

    // HQ Address Details
    @NotBlank(message = "Address line 1 is required")
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    // Company Registration Details
    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "tan_number")
    private String tanNumber; // Tax Account Number

    @Column(name = "website")
    private String website;

    // Relationships
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("supplier-locations")
    private List<SupplierLocation> locations;

    // Soft delete fields
    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public Supplier() {}

    public Supplier(String companyName, String contactPersonName, String contactPersonEmail,
                   String contactPersonPhone, String addressLine1, String city, String state, String postalCode) {
        this.companyName = companyName;
        this.contactPersonName = contactPersonName;
        this.contactPersonEmail = contactPersonEmail;
        this.contactPersonPhone = contactPersonPhone;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.isDeleted = false;
    }

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

    public List<SupplierLocation> getLocations() { return locations; }
    public void setLocations(List<SupplierLocation> locations) { this.locations = locations; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}