package com.itti.leadcapturing.dto;

public class SupplierLoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private boolean success;
    private String message;

    // Supplier-specific details
    private SupplierDepartmentDetails department;
    private SupplierLocationDetails location;
    private SupplierDetails supplier;

    // Constructors
    public SupplierLoginResponse() {}

    public SupplierLoginResponse(String token, Long userId, String email, String fullName, String role) {
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

    public SupplierDepartmentDetails getDepartment() { return department; }
    public void setDepartment(SupplierDepartmentDetails department) { this.department = department; }

    public SupplierLocationDetails getLocation() { return location; }
    public void setLocation(SupplierLocationDetails location) { this.location = location; }

    public SupplierDetails getSupplier() { return supplier; }
    public void setSupplier(SupplierDetails supplier) { this.supplier = supplier; }

    // ============================================
    // INNER CLASS: Supplier Department Details
    // ============================================
    public static class SupplierDepartmentDetails {
        private Long id;
        private String name;
        private String categoryOfProducts;
        private String description;

        public SupplierDepartmentDetails() {}

        public SupplierDepartmentDetails(Long id, String name, String categoryOfProducts, String description) {
            this.id = id;
            this.name = name;
            this.categoryOfProducts = categoryOfProducts;
            this.description = description;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategoryOfProducts() { return categoryOfProducts; }
        public void setCategoryOfProducts(String categoryOfProducts) { this.categoryOfProducts = categoryOfProducts; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // ============================================
    // INNER CLASS: Supplier Location Details
    // ============================================
    public static class SupplierLocationDetails {
        private Long id;
        private String locationName;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String addressLine1;
        private String addressLine2;

        public SupplierLocationDetails() {}

        public SupplierLocationDetails(Long id, String locationName, String city, String state, 
                                      String postalCode, String country, String addressLine1, 
                                      String addressLine2) {
            this.id = id;
            this.locationName = locationName;
            this.city = city;
            this.state = state;
            this.postalCode = postalCode;
            this.country = country;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getLocationName() { return locationName; }
        public void setLocationName(String locationName) { this.locationName = locationName; }

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
    // INNER CLASS: Supplier Details
    // ============================================
    public static class SupplierDetails {
        private Long id;
        private String name;
        private String code;
        private String email;
        private String phone;
        private String website;
        private String industrySector;
        private String companyType;

        public SupplierDetails() {}

        public SupplierDetails(Long id, String name, String code, String email, String phone,
                              String website, String industrySector, String companyType) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.email = email;
            this.phone = phone;
            this.website = website;
            this.industrySector = industrySector;
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

        public String getIndustrySector() { return industrySector; }
        public void setIndustrySector(String industrySector) { this.industrySector = industrySector; }

        public String getCompanyType() { return companyType; }
        public void setCompanyType(String companyType) { this.companyType = companyType; }
    }
}