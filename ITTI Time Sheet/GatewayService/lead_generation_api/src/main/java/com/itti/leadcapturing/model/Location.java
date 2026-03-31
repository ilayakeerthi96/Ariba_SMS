

// ==================== LOCATION ENTITY ====================
package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location name is required")
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_type")
    private String locationType;

    @Column(name = "location_contact_name")
    private String locationContactName;

    @Email
    @Column(name = "location_contact_email")
    private String locationContactEmail;

    @Column(name = "location_contact_phone")
    private String locationContactPhone;

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

    @Column(name = "landline_number")
    private String landlineNumber;

    @Column(name = "fax_number")
    private String faxNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @JsonBackReference("buyer-locations")
    private Buyer buyer;

    // ✅ CRITICAL FIX: CascadeType.ALL ensures departments AND users are saved
    @OneToMany(
        mappedBy = "location", 
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonManagedReference("location-departments")
    private List<Department> departments = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Constructors
    public Location() {
        this.departments = new ArrayList<>();
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

    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }

    public List<Department> getDepartments() { 
        if (departments == null) {
            departments = new ArrayList<>();
        }
        return departments; 
    }
    
    public void setDepartments(List<Department> departments) { 
        this.departments = departments != null ? departments : new ArrayList<>();
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}