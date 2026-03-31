// // File: src/main/java/com/itti/leadcapturing/dto/BuyerCreateByAdminDTO.java

// package com.itti.leadcapturing.dto;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Email;

// /**
//  * DTO for Organization Admin to create buyers
//  * Includes organizationCompanyName for hierarchy linking
//  */
// public class BuyerCreateByAdminDTO {
    
//     @NotBlank(message = "Company name is required")
//     private String companyName;
    
//     private String companyType;
    
//     @NotBlank(message = "Contact person name is required")
//     private String contactPersonName;
    
//     private String contactPersonDesignation;
    
//     @Email(message = "Valid email is required")
//     private String contactPersonEmail;
    
//     private String contactPersonPhone;
    
//     private String addressLine1;
//     private String addressLine2;
//     private String city;
//     private String state;
//     private String postalCode;
//     private String country;
//     private String gstNumber;
//     private String panNumber;
//     private String cinNumber;
//     private String website;

//     // ✅ NEW: Organization Admin's company name (for hierarchy linking)
//     @NotBlank(message = "Organization company name is required")
//     private String organizationCompanyName;

//     // Constructors
//     public BuyerCreateByAdminDTO() {}

//     // Getters and Setters
//     public String getCompanyName() { return companyName; }
//     public void setCompanyName(String companyName) { this.companyName = companyName; }

//     public String getCompanyType() { return companyType; }
//     public void setCompanyType(String companyType) { this.companyType = companyType; }

//     public String getContactPersonName() { return contactPersonName; }
//     public void setContactPersonName(String contactPersonName) { 
//         this.contactPersonName = contactPersonName; 
//     }

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

//     // ✅ NEW
//     public String getOrganizationCompanyName() { return organizationCompanyName; }
//     public void setOrganizationCompanyName(String organizationCompanyName) { 
//         this.organizationCompanyName = organizationCompanyName; 
//     }
// }


// File: src/main/java/com/itti/leadcapturing/dto/BuyerCreateByAdminDTO.java

package com.itti.leadcapturing.dto;

import com.itti.leadcapturing.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO for Organization Admin to create buyers
 * Includes organizationCompanyName for hierarchy linking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyerCreateByAdminDTO {
    
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

    // ✅ CRITICAL: Organization Admin's company name (for hierarchy linking)
    @NotBlank(message = "Organization company name is required")
    private String organizationCompanyName;

    // ✅ IMPORTANT: Complete nested structure from frontend
    private List<Location> locations;
}