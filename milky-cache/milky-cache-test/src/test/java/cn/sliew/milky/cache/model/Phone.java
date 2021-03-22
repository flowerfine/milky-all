package cn.sliew.milky.cache.model;

import java.io.Serializable;
import java.util.Objects;

public class Phone implements Serializable {

    private static final long serialVersionUID = 4022175897469522722L;

    private String country;

    private String area;

    private String number;

    private String extension;

    public Phone(String country, String area, String number, String extension) {
        this.country = country;
        this.area = area;
        this.number = number;
        this.extension = extension;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(country, phone.country) &&
                Objects.equals(area, phone.area) &&
                Objects.equals(number, phone.number) &&
                Objects.equals(extension, phone.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, area, number, extension);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "country='" + country + '\'' +
                ", area='" + area + '\'' +
                ", number='" + number + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}
