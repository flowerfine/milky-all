package cn.sliew.milky.cache.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PersonInfo implements Serializable {

    private static final long serialVersionUID = 2554186796646272501L;

    List<Phone> phones;

    Phone fax;

    FullAddress fullAddress;

    String mobileNo;

    String name;

    boolean male;

    boolean female;

    String department;

    String jobTitle;

    String homepageUrl;

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public Phone getFax() {
        return fax;
    }

    public void setFax(Phone fax) {
        this.fax = fax;
    }

    public FullAddress getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(FullAddress fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonInfo that = (PersonInfo) o;
        return male == that.male &&
                female == that.female &&
                Objects.equals(phones, that.phones) &&
                Objects.equals(fax, that.fax) &&
                Objects.equals(fullAddress, that.fullAddress) &&
                Objects.equals(mobileNo, that.mobileNo) &&
                Objects.equals(name, that.name) &&
                Objects.equals(department, that.department) &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(homepageUrl, that.homepageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phones, fax, fullAddress, mobileNo, name, male, female, department, jobTitle, homepageUrl);
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "phones=" + phones +
                ", fax=" + fax +
                ", fullAddress=" + fullAddress +
                ", mobileNo='" + mobileNo + '\'' +
                ", name='" + name + '\'' +
                ", male=" + male +
                ", female=" + female +
                ", department='" + department + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", homepageUrl='" + homepageUrl + '\'' +
                '}';
    }
}
