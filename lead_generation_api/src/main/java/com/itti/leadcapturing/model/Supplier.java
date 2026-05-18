

// package com.itti.leadcapturing.model;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.Pattern;
// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import java.time.LocalDateTime;
// import java.util.List;

// @Entity
// @Table(name = "suppliers")
// public class Supplier {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     // Company Details
//     @NotBlank(message = "Company name is required")
//     @Column(name = "company_name")
//     private String companyName;

//     @Column(name = "company_type")
//     private String companyType;

//     @Column(name = "industry_sector")
//     private String industrySector;

//     // ✅ Logo fields — stored in DB
//     @Column(name = "logo_filename")
//     private String logoFilename;

//     @Column(name = "logo_url")
//     private String logoUrl;

//     @Lob
//     @Column(name = "logo_data", columnDefinition = "LONGBLOB")
//     private byte[] logoData;

//     @Column(name = "logo_content_type")
//     private String logoContentType;

//     // ✅ logoBase64 is TRANSIENT — received from frontend, decoded into logoData before save, never stored in DB
//     @Transient
//     private String logoBase64;

//     // Contact Person Details
//     @NotBlank(message = "Contact person name is required")
//     @Column(name = "contact_person_name")
//     private String contactPersonName;

//     @Column(name = "contact_person_designation")
//     private String contactPersonDesignation;

//     @Email
//     @Column(name = "contact_person_email")
//     private String contactPersonEmail;

//     @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
//     @Column(name = "contact_person_phone")
//     private String contactPersonPhone;

//     // HQ Address Details
//     @NotBlank(message = "Address line 1 is required")
//     @Column(name = "address_line1")
//     private String addressLine1;

//     @Column(name = "address_line2")
//     private String addressLine2;

//     @Column(name = "city")
//     private String city;

//     @Column(name = "state")
//     private String state;

//     @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
//     @Column(name = "postal_code")
//     private String postalCode;

//     @Column(name = "country")
//     private String country;

//     // Company Registration Details
//     @Column(name = "gst_number")
//     private String gstNumber;

//     @Column(name = "pan_number")
//     private String panNumber;

//     @Column(name = "tan_number")
//     private String tanNumber;

//     @Column(name = "website")
//     private String website;

//       @Column(length = 20)
//     private String theme = "orange";
 
//     public String getTheme() { return theme; }
//     public void setTheme(String theme) { this.theme = theme; }

//     // Relationships
//     @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//     @JsonManagedReference("supplier-locations")
//     private List<SupplierLocation> locations;

//     // Soft delete fields
//     @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
//     private Boolean isDeleted = false;

//     @Column(name = "deleted_at")
//     private LocalDateTime deletedAt;

//     // Constructors
//     public Supplier() {}

//     public Supplier(String companyName, String contactPersonName, String contactPersonEmail,
//                    String contactPersonPhone, String addressLine1, String city, String state, String postalCode) {
//         this.companyName = companyName;
//         this.contactPersonName = contactPersonName;
//         this.contactPersonEmail = contactPersonEmail;
//         this.contactPersonPhone = contactPersonPhone;
//         this.addressLine1 = addressLine1;
//         this.city = city;
//         this.state = state;
//         this.postalCode = postalCode;
//         this.isDeleted = false;
//     }

//     // Getters and Setters
//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public String getCompanyName() { return companyName; }
//     public void setCompanyName(String companyName) { this.companyName = companyName; }

//     public String getCompanyType() { return companyType; }
//     public void setCompanyType(String companyType) { this.companyType = companyType; }

//     public String getIndustrySector() { return industrySector; }
//     public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }

//     // Logo getters/setters
//     public String getLogoFilename() { return logoFilename; }
//     public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }

//     public String getLogoUrl() { return logoUrl; }
//     public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

//     public byte[] getLogoData() { return logoData; }
//     public void setLogoData(byte[] logoData) { this.logoData = logoData; }

//     public String getLogoContentType() { return logoContentType; }
//     public void setLogoContentType(String logoContentType) { this.logoContentType = logoContentType; }

//     // ✅ logoBase64 getter/setter — transient, for receiving from frontend only
//     public String getLogoBase64() { return logoBase64; }
//     public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }

//     public String getContactPersonName() { return contactPersonName; }
//     public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

//     public String getContactPersonDesignation() { return contactPersonDesignation; }
//     public void setContactPersonDesignation(String contactPersonDesignation) { this.contactPersonDesignation = contactPersonDesignation; }

//     public String getContactPersonEmail() { return contactPersonEmail; }
//     public void setContactPersonEmail(String contactPersonEmail) { this.contactPersonEmail = contactPersonEmail; }

//     public String getContactPersonPhone() { return contactPersonPhone; }
//     public void setContactPersonPhone(String contactPersonPhone) { this.contactPersonPhone = contactPersonPhone; }

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

//     public String getTanNumber() { return tanNumber; }
//     public void setTanNumber(String tanNumber) { this.tanNumber = tanNumber; }

//     public String getWebsite() { return website; }
//     public void setWebsite(String website) { this.website = website; }

//     public List<SupplierLocation> getLocations() { return locations; }
//     public void setLocations(List<SupplierLocation> locations) { this.locations = locations; }

//     public Boolean getIsDeleted() { return isDeleted; }
//     public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

//     public LocalDateTime getDeletedAt() { return deletedAt; }
//     public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
// }

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;

/**
 * MODIFIED: Added approvalStatus, approvalComments, createdByCompanyName fields.
 * When a supplier is created it starts in PENDING_APPROVAL status.
 * It becomes APPROVED only after all hierarchy levels approve it.
 */
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
    private String companyType;

    @Column(name = "industry_sector")
    private String industrySector;

    // Logo fields
    @Column(name = "logo_filename")
    private String logoFilename;

    @Column(name = "logo_url")
    private String logoUrl;

    @Lob
    @Column(name = "logo_data", columnDefinition = "LONGBLOB")
    private byte[] logoData;

    @Column(name = "logo_content_type")
    private String logoContentType;

    // logoBase64 is TRANSIENT — received from frontend, decoded into logoData before save
    @Transient
    private String logoBase64;

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
    private String tanNumber;

    @Column(name = "website")
    private String website;

    @Column(length = 20)
    private String theme = "orange";

    // ==================== NEW: APPROVAL FIELDS ====================

    /**
     * Approval status of the supplier.
     * PENDING  → submitted for approval, waiting hierarchy review
     * APPROVED → all levels approved, supplier is active and usable in RFQs
     * REJECTED → permanently rejected by an approver
     * HOLD     → one approver put it on hold
     */
    @Column(name = "approval_status", length = 30)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /**
     * Comments/remarks from the approval process (last action's remarks).
     */
    @Column(name = "approval_comments", length = 1000)
    private String approvalComments;

    /**
     * The company (organizationCompanyName) of the admin/user who created this supplier.
     * Used to find the correct hierarchy levels for approval routing.
     */
    @Column(name = "created_by_company_name", length = 200)
    private String createdByCompanyName;

    /**
     * ID of the HierarchyUser or buyer User who initiated the supplier creation.
     * Stored for audit and resubmission tracking.
     */
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    /**
     * When final approval was granted.
     */
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

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
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public String getIndustrySector() { return industrySector; }
    public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }

    public String getLogoFilename() { return logoFilename; }
    public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public byte[] getLogoData() { return logoData; }
    public void setLogoData(byte[] logoData) { this.logoData = logoData; }

    public String getLogoContentType() { return logoContentType; }
    public void setLogoContentType(String logoContentType) { this.logoContentType = logoContentType; }

    public String getLogoBase64() { return logoBase64; }
    public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }

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

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getApprovalComments() { return approvalComments; }
    public void setApprovalComments(String approvalComments) { this.approvalComments = approvalComments; }

    public String getCreatedByCompanyName() { return createdByCompanyName; }
    public void setCreatedByCompanyName(String createdByCompanyName) { this.createdByCompanyName = createdByCompanyName; }

    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }

    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }

    public List<SupplierLocation> getLocations() { return locations; }
    public void setLocations(List<SupplierLocation> locations) { this.locations = locations; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}