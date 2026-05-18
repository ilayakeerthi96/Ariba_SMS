
// package com.itti.leadcapturing.dto;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;

// /**
//  * DTO for supplier self-registration (public endpoint).
//  * Carries company info, primary contact, address, and the
//  * first user's login credentials.
//  *
//  * Document uploads (GST / PAN / TAN certificates) are handled
//  * separately via POST /api/supplier/documents/upload (multipart).
//  */
// public class SupplierRegistrationRequest {

//     // ── Company Info ──────────────────────────────────────────
//     @NotBlank(message = "Company name is required")
//     private String companyName;

//     private String companyType;
//     private String industrySector;
//     private String website;

//     // ── Registration numbers ──────────────────────────────────
//     private String gstNumber;
//     private String panNumber;
//     private String tanNumber;

//     // ── Primary contact ───────────────────────────────────────
//     @NotBlank(message = "Contact person name is required")
//     private String contactPersonName;

//     private String contactPersonDesignation;

//     @Email(message = "Valid contact email required")
//     private String contactPersonEmail;

//     @Pattern(regexp = "^[0-9]{10}$", message = "Contact phone must be 10 digits")
//     private String contactPersonPhone;

//     // ── Address ───────────────────────────────────────────────
//     @NotBlank(message = "Address line 1 is required")
//     private String addressLine1;

//     private String addressLine2;
//     private String city;
//     private String state;
//     private String postalCode;
//     private String country;

//     // ── Organization this supplier wants to register with ─────
//     @NotBlank(message = "Organization company name is required")
//     private String organizationCompanyName;

//     // ── Logo (optional, sent as base64 data URL) ──────────────
//     private String logoBase64;
//     private String logoFilename;
//     private String logoContentType;

//     // ── Login account credentials ─────────────────────────────
//     @NotBlank(message = "First name is required")
//     private String firstName;

//     @NotBlank(message = "Last name is required")
//     private String lastName;

//     private String designation;

//     @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
//     private String phone;

//     @NotBlank(message = "Email is required")
//     @Email(message = "Valid email required")
//     private String email;

//     @NotBlank(message = "Password is required")
//     private String password;

//     // ── Getters & Setters ─────────────────────────────────────

//     public String getCompanyName() { return companyName; }
//     public void setCompanyName(String companyName) { this.companyName = companyName; }

//     public String getCompanyType() { return companyType; }
//     public void setCompanyType(String companyType) { this.companyType = companyType; }

//     public String getIndustrySector() { return industrySector; }
//     public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }

//     public String getWebsite() { return website; }
//     public void setWebsite(String website) { this.website = website; }

//     public String getGstNumber() { return gstNumber; }
//     public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

//     public String getPanNumber() { return panNumber; }
//     public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

//     public String getTanNumber() { return tanNumber; }
//     public void setTanNumber(String tanNumber) { this.tanNumber = tanNumber; }

//     public String getContactPersonName() { return contactPersonName; }
//     public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

//     public String getContactPersonDesignation() { return contactPersonDesignation; }
//     public void setContactPersonDesignation(String contactPersonDesignation) {
//         this.contactPersonDesignation = contactPersonDesignation;
//     }

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

//     public String getOrganizationCompanyName() { return organizationCompanyName; }
//     public void setOrganizationCompanyName(String organizationCompanyName) {
//         this.organizationCompanyName = organizationCompanyName;
//     }

//     public String getLogoBase64() { return logoBase64; }
//     public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }

//     public String getLogoFilename() { return logoFilename; }
//     public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }

//     public String getLogoContentType() { return logoContentType; }
//     public void setLogoContentType(String logoContentType) { this.logoContentType = logoContentType; }

//     public String getFirstName() { return firstName; }
//     public void setFirstName(String firstName) { this.firstName = firstName; }

//     public String getLastName() { return lastName; }
//     public void setLastName(String lastName) { this.lastName = lastName; }

//     public String getDesignation() { return designation; }
//     public void setDesignation(String designation) { this.designation = designation; }

//     public String getPhone() { return phone; }
//     public void setPhone(String phone) { this.phone = phone; }

//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }

//     public String getPassword() { return password; }
//     public void setPassword(String password) { this.password = password; }
// }


package com.itti.leadcapturing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class SupplierRegistrationRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String companyType;
    private String industrySector;
    private String website;
    private String gstNumber;
    private String panNumber;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    private String designation;

    @NotBlank(message = "Address is required")
    private String addressLine1;

    private String addressLine2;
    private String city;
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
    private String postalCode;

    private String country;

    @NotBlank(message = "Organization company name is required")
    private String organizationCompanyName;

    // ✅ ADD THIS — logo support for self-registration
    private String logoBase64;
    private String logoFilename;
    private String logoContentType;

    // Getters and Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }

    public String getIndustrySector() { return industrySector; }
    public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

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

    public String getOrganizationCompanyName() { return organizationCompanyName; }
    public void setOrganizationCompanyName(String organizationCompanyName) {
        this.organizationCompanyName = organizationCompanyName;
    }

    // ✅ NEW logo getters/setters
    public String getLogoBase64() { return logoBase64; }
    public void setLogoBase64(String logoBase64) { this.logoBase64 = logoBase64; }

    public String getLogoFilename() { return logoFilename; }
    public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }

    public String getLogoContentType() { return logoContentType; }
    public void setLogoContentType(String logoContentType) { this.logoContentType = logoContentType; }
}