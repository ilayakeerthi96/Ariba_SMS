package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "supplier_locations")
public class SupplierLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location name is required")
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_type")
    private String locationType; // e.g., Head Office, Branch, Warehouse

    // Contact Person Details for Location
    @NotBlank(message = "Location contact person name is required")
    @Column(name = "location_contact_name")
    private String locationContactName;

    @Email
    @Column(name = "location_contact_email")
    private String locationContactEmail;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    @Column(name = "location_contact_phone")
    private String locationContactPhone;

    // Address Details
    @NotBlank(message = "Address line 1 is required")
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @NotBlank(message = "State is required")
    @Column(name = "state")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Column(name = "country")
    private String country;

    // Additional Details
    @Column(name = "landline_number")
    private String landlineNumber;

    @Column(name = "fax_number")
    private String faxNumber;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonBackReference("supplier-locations")
    private Supplier supplier;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("supplier-location-departments")
    private List<SupplierDepartment> departments;

    // Soft delete fields
    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public SupplierLocation() {}

    public SupplierLocation(String locationName, String locationContactName, String locationContactEmail,
                           String locationContactPhone, String addressLine1, String city, String state, String postalCode) {
        this.locationName = locationName;
        this.locationContactName = locationContactName;
        this.locationContactEmail = locationContactEmail;
        this.locationContactPhone = locationContactPhone;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.isDeleted = false;
    }

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

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public List<SupplierDepartment> getDepartments() { return departments; }
    public void setDepartments(List<SupplierDepartment> departments) { this.departments = departments; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}