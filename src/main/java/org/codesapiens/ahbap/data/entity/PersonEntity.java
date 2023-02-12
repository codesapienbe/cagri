package org.codesapiens.ahbap.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "people")
public class PersonEntity extends AbstractEntity {

    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String phone;

    private Double latitude;

    private Double longitude;

    public PersonEntity() {
    }

    public PersonEntity(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonEntity)) return false;
        if (!super.equals(o)) return false;
        PersonEntity that = (PersonEntity) o;
        return getPhone().equals(that.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPhone());
    }
}
