
// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Email;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "buyers")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
// public class Buyer {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @NotBlank(message = "Company name is required")
//     @Column(name = "company_name", nullable = false)
//     private String companyName;

//     @Column(name = "company_type")
//     private String companyType;

//     // ✅ NEW: Logo fields
//     @Column(name = "logo_filename")
//     private String logoFilename;

//     @Column(name = "logo_url")
//     private String logoUrl;

//     @Lob
//     @Column(name = "logo_data", columnDefinition = "LONGBLOB")
//     private byte[] logoData;

//     @Column(name = "logo_content_type")
//     private String logoContentType;

//     @NotBlank(message = "Contact person name is required")
//     @Column(name = "contact_person_name")
//     private String contactPersonName;

//     @Column(name = "contact_person_designation")
//     private String contactPersonDesignation;

//     @Email
//     @Column(name = "contact_person_email")
//     private String contactPersonEmail;

//     @Column(name = "contact_person_phone")
//     private String contactPersonPhone;

//     @Column(name = "address_line1")
//     private String addressLine1;

//     @Column(name = "address_line2")
//     private String addressLine2;

//     @Column(name = "city")
//     private String city;

//     @Column(name = "state")
//     private String state;

//     @Column(name = "postal_code")
//     private String postalCode;

//     @Column(name = "country")
//     private String country;

//     @Column(name = "gst_number")
//     private String gstNumber;

//     @Column(name = "pan_number")
//     private String panNumber;

//     @Column(name = "cin_number")
//     private String cinNumber;

//     @Column(name = "website")
//     private String website;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "created_by_org_admin_id")
//     private OrganizationAdmin createdByOrgAdmin;

//     @Column(name = "organization_company_name", nullable = false)
//     private String organizationCompanyName;

//     @OneToMany(
//         mappedBy = "buyer", 
//         cascade = CascadeType.ALL,
//         orphanRemoval = true,
//         fetch = FetchType.LAZY
//     )
//     @JsonManagedReference("buyer-locations")
//     private List<Location> locations = new ArrayList<>();

//     @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
//     private Boolean isDeleted = false;

//     @Column(name = "deleted_at")
//     private LocalDateTime deletedAt;

//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;

//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;

//     // Constructors
//     public Buyer() {
//         this.locations = new ArrayList<>();
//         this.createdAt = LocalDateTime.now();
//         this.updatedAt = LocalDateTime.now();
//     }

//     @PrePersist
//     protected void onCreate() {
//         if (this.createdAt == null) {
//             this.createdAt = LocalDateTime.now();
//         }
//         if (this.updatedAt == null) {
//             this.updatedAt = LocalDateTime.now();
//         }
//     }
    
//     @PreUpdate
//     protected void onUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }

//     // ===== GETTERS AND SETTERS =====

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public String getCompanyName() { return companyName; }
//     public void setCompanyName(String companyName) { this.companyName = companyName; }

//     public String getCompanyType() { return companyType; }
//     public void setCompanyType(String companyType) { this.companyType = companyType; }

//     // ✅ NEW: Logo getters/setters
//     public String getLogoFilename() { return logoFilename; }
//     public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }

//     public String getLogoUrl() { return logoUrl; }
//     public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

//     public byte[] getLogoData() { return logoData; }
//     public void setLogoData(byte[] logoData) { this.logoData = logoData; }

//     public String getLogoContentType() { return logoContentType; }
//     public void setLogoContentType(String logoContentType) { this.logoContentType = logoContentType; }

//     public String getContactPersonName() { return contactPersonName; }
//     public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

//     public String getContactPersonDesignation() { return contactPersonDesignation; }
//     public void setContactPersonDesignation(String contactPersonDesignation) { 
//         this.contactPersonDesignation = contactPersonDesignation; 
//     }

//     public String getContactPersonEmail() { return contactPersonEmail; }
//     public void setContactPersonEmail(String contactPersonEmail) { 
//         this.contactPersonEmail = contactPersonEmail; 
//     }

//     public String getContactPersonPhone() { return contactPersonPhone; }
//     public void setContactPersonPhone(String contactPersonPhone) { 
//         this.contactPersonPhone = contactPersonPhone; 
//     }

//     public String getAddressLine1() { return addressLine1; }
//     public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

//     public String getAddressLine2() { return addressLine2; }
//     public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

//     public String getCity() { return city; }
//     public void setCity(String city) { this.city = city; }

//     public String getState() { return state; }
//     public void setState(String state) { this.state = state; }

//     public String getPostalCode() { return postalCode; }
//     public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

//     public String getCountry() { return country; }
//     public void setCountry(String country) { this.country = country; }

//     public String getGstNumber() { return gstNumber; }
//     public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

//     public String getPanNumber() { return panNumber; }
//     public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

//     public String getCinNumber() { return cinNumber; }
//     public void setCinNumber(String cinNumber) { this.cinNumber = cinNumber; }

//     public String getWebsite() { return website; }
//     public void setWebsite(String website) { this.website = website; }

//     public OrganizationAdmin getCreatedByOrgAdmin() { return createdByOrgAdmin; }
//     public void setCreatedByOrgAdmin(OrganizationAdmin createdByOrgAdmin) { 
//         this.createdByOrgAdmin = createdByOrgAdmin; 
//     }

//     public String getOrganizationCompanyName() { return organizationCompanyName; }
//     public void setOrganizationCompanyName(String organizationCompanyName) { 
//         this.organizationCompanyName = organizationCompanyName; 
//     }

//     public List<Location> getLocations() { 
//         if (locations == null) {
//             locations = new ArrayList<>();
//         }
//         return locations; 
//     }
    
//     public void setLocations(List<Location> locations) { 
//         this.locations = locations != null ? locations : new ArrayList<>();
//     }

//     public Boolean getIsDeleted() { return isDeleted; }
//     public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

//     public LocalDateTime getDeletedAt() { return deletedAt; }
//     public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
// }

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MODIFIED: Removed logo fields (logoFilename, logoUrl, logoData, logoContentType).
 * Logo is now inherited from the OrganizationAdmin who created this buyer.
 * Use createdByOrgAdmin.logoUrl to get the logo for display.
 */
@Entity
@Table(name = "buyers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_type")
    private String companyType;

    @NotBlank(message = "Contact person name is required")
    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Column(name = "contact_person_designation")
    private String contactPersonDesignation;

    @Email
    @Column(name = "contact_person_email")
    private String contactPersonEmail;

    @Column(name = "contact_person_phone")
    private String contactPersonPhone;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "cin_number")
    private String cinNumber;

    @Column(name = "website")
    private String website;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_org_admin_id")
    private OrganizationAdmin createdByOrgAdmin;

    @Column(name = "organization_company_name", nullable = false)
    private String organizationCompanyName;

    @OneToMany(
        mappedBy = "buyer",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonManagedReference("buyer-locations")
    private List<Location> locations = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Buyer() {
        this.locations = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.updatedAt == null) this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getContactPersonDesignation() { return contactPersonDesignation; }
    public void setContactPersonDesignation(String contactPersonDesignation) {
        this.contactPersonDesignation = contactPersonDesignation;
    }

    public String getContactPersonEmail() { return contactPersonEmail; }
    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() { return contactPersonPhone; }
    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

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

    public OrganizationAdmin getCreatedByOrgAdmin() { return createdByOrgAdmin; }
    public void setCreatedByOrgAdmin(OrganizationAdmin createdByOrgAdmin) {
        this.createdByOrgAdmin = createdByOrgAdmin;
    }

    public String getOrganizationCompanyName() { return organizationCompanyName; }
    public void setOrganizationCompanyName(String organizationCompanyName) {
        this.organizationCompanyName = organizationCompanyName;
    }

    public List<Location> getLocations() {
        if (locations == null) locations = new ArrayList<>();
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations != null ? locations : new ArrayList<>();
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}