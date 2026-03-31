
package com.itti.leadcapturing.dto;

public class BuyerDto {
    private Long id;
    private String companyName;
    private String contactPersonEmail;

    // ✅ Constructor with 3 fields (matching your actual Buyer model)
    public BuyerDto(Long id, String companyName, String contactPersonEmail) {
        this.id = id;
        this.companyName = companyName;
        this.contactPersonEmail = contactPersonEmail;
    }

    public BuyerDto() {}

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getCompanyName() { 
        return companyName; 
    }
    public void setCompanyName(String companyName) { 
        this.companyName = companyName; 
    }

    public String getContactPersonEmail() { 
        return contactPersonEmail; 
    }
    public void setContactPersonEmail(String contactPersonEmail) { 
        this.contactPersonEmail = contactPersonEmail; 
    }

    @Override
    public String toString() {
        return "BuyerDto{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", contactPersonEmail='" + contactPersonEmail + '\'' +
                '}';
    }
}