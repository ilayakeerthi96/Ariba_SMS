// package com.itti.leadcapturing.dto;

// import com.itti.leadcapturing.model.Location;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Email;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// import java.util.List;

// /**
//  * DTO for Organization Admin to create buyers
//  * Includes organizationCompanyName for hierarchy linking and logo support
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class BuyerCreateByAdminDTO {
    
//     @NotBlank(message = "Company name is required")
//     private String companyName;
    
//     private String companyType;
    
//     // ✅ NEW: Logo fields
//     private String logoFilename;
//     private String logoUrl;
//     private String logoBase64; // For base64 encoded image from frontend
//     private String logoContentType;
    
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

//     @NotBlank(message = "Organization company name is required")
//     private String organizationCompanyName;

//     private List<Location> locations;
// }

package com.itti.leadcapturing.dto;

import com.itti.leadcapturing.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * MODIFIED: Removed logoFilename, logoUrl, logoBase64, logoContentType fields.
 * The logo and company name are now inherited from the OrganizationAdmin.
 * organizationCompanyName is still used internally to validate the admin linkage,
 * but the buyer's displayed company name comes from the admin's companyName.
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

    @NotBlank(message = "Organization company name is required")
    private String organizationCompanyName;

    private List<Location> locations;
}