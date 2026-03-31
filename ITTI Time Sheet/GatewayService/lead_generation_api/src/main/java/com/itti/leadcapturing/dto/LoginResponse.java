
package com.itti.leadcapturing.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private boolean success;
    private String message;

    // ✅ NEW: Department Details
    private DepartmentDetails department;

    // ✅ NEW: Location Details
    private LocationDetails location;

    // ✅ NEW: Buyer Details
    private BuyerDetails buyer;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String email, String fullName, String role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.success = true;
        this.message = "Login successful";
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public DepartmentDetails getDepartment() { return department; }
    public void setDepartment(DepartmentDetails department) { this.department = department; }

    public LocationDetails getLocation() { return location; }
    public void setLocation(LocationDetails location) { this.location = location; }

    public BuyerDetails getBuyer() { return buyer; }
    public void setBuyer(BuyerDetails buyer) { this.buyer = buyer; }

    // ============================================
    // INNER CLASS: Department Details
    // ============================================
    public static class DepartmentDetails {
        private Long id;
        private String name;
        private String code;
        private String description;

        public DepartmentDetails() {}

        public DepartmentDetails(Long id, String name, String code, String description) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.description = description;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // ============================================
    // INNER CLASS: Location Details
    // ============================================
    public static class LocationDetails {
        private Long id;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String addressLine1;
        private String addressLine2;

        public LocationDetails() {}

        public LocationDetails(Long id, String city, String state, String postalCode, 
                             String country, String addressLine1, String addressLine2) {
            this.id = id;
            this.city = city;
            this.state = state;
            this.postalCode = postalCode;
            this.country = country;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    }

    // ============================================
    // INNER CLASS: Buyer Details
    // ============================================
    public static class BuyerDetails {
        private Long id;
        private String name;
        private String code;
        private String email;
        private String phone;
        private String website;
        private String industryType;
        private String companyType;

        public BuyerDetails() {}

        public BuyerDetails(Long id, String name, String code, String email, String phone,
                          String website, String industryType, String companyType) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.email = email;
            this.phone = phone;
            this.website = website;
            this.industryType = industryType;
            this.companyType = companyType;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }

        public String getIndustryType() { return industryType; }
        public void setIndustryType(String industryType) { this.industryType = industryType; }

        public String getCompanyType() { return companyType; }
        public void setCompanyType(String companyType) { this.companyType = companyType; }
    }
}