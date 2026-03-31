package com.itti.leadcapturing.dto;

/**
 * DTO for Location information in responses
 */
public class LocationDto {
    
    private Long id;
    private String locationName;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String currencyCode;
    private String currencySymbol;

    // Constructors
    public LocationDto() {}

    public LocationDto(Long id, String locationName) {
        this.id = id;
        this.locationName = locationName;
    }

    public LocationDto(Long id, String locationName, String city, String state, String postalCode, String country) {
        this.id = id;
        this.locationName = locationName;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    // Getters and Setters
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

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    @Override
    public String toString() {
        return "LocationDto{" +
                "id=" + id +
                ", locationName='" + locationName + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}