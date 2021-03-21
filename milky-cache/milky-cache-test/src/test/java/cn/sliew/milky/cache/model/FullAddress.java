package cn.sliew.milky.cache.model;

import java.io.Serializable;
import java.util.Objects;

public class FullAddress implements Serializable {

    private static final long serialVersionUID = -7008816247768646165L;

    private String countryId;

    private String countryName;

    private String provinceName;

    private String cityId;

    private String cityName;

    private String streetAddress;

    private String zipCode;

    public FullAddress(String countryId, String countryName, String provinceName, String cityId, String cityName, String streetAddress, String zipCode) {
        this.countryId = countryId;
        this.countryName = countryName;
        this.provinceName = provinceName;
        this.cityId = cityId;
        this.cityName = cityName;
        this.streetAddress = streetAddress;
        this.zipCode = zipCode;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullAddress that = (FullAddress) o;
        return Objects.equals(countryId, that.countryId) &&
                Objects.equals(countryName, that.countryName) &&
                Objects.equals(provinceName, that.provinceName) &&
                Objects.equals(cityId, that.cityId) &&
                Objects.equals(cityName, that.cityName) &&
                Objects.equals(streetAddress, that.streetAddress) &&
                Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, countryName, provinceName, cityId, cityName, streetAddress, zipCode);
    }

    @Override
    public String toString() {
        return "FullAddress{" +
                "countryId='" + countryId + '\'' +
                ", countryName='" + countryName + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
