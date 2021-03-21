package cn.sliew.milky.cache.model;

import java.io.Serializable;
import java.util.Objects;

public class BigPerson implements Serializable {

    private static final long serialVersionUID = -5029098761465180837L;

    private String id;

    private String username;

    Status status;

    String email;

    String penName;

    PersonInfo infoProfile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public PersonInfo getInfoProfile() {
        return infoProfile;
    }

    public void setInfoProfile(PersonInfo infoProfile) {
        this.infoProfile = infoProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigPerson bigPerson = (BigPerson) o;
        return Objects.equals(id, bigPerson.id) &&
                Objects.equals(username, bigPerson.username) &&
                status == bigPerson.status &&
                Objects.equals(email, bigPerson.email) &&
                Objects.equals(penName, bigPerson.penName) &&
                Objects.equals(infoProfile, bigPerson.infoProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, status, email, penName, infoProfile);
    }

    @Override
    public String toString() {
        return "BigPerson{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", email='" + email + '\'' +
                ", penName='" + penName + '\'' +
                ", infoProfile=" + infoProfile +
                '}';
    }
}
