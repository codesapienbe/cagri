package org.codesapiens.ahbap.data.entity;

import org.codesapiens.ahbap.data.Name;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "people")
// HIBERNATE SEARCH
@Indexed
public class PersonEntity extends AbstractEntity {

    @FullTextField
    @Name
    private String firstName;

    @FullTextField
    @Name
    private String lastName;

    @FullTextField
    @Column(nullable = false, unique = true)
    private String phone;

    private Double latitude;

    private Double longitude;

    @KeywordField
    private String imageUrl;

    private String registeredAt;

    @KeywordField
    private String sessionId;

    public PersonEntity() {
    }

    public PersonEntity(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public PersonEntity(String firstName, String lastName, String phone, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public PersonEntity(String firstName, String lastName, String phone,
                        String sessionId, String registeredAt,
                        Double latitude, Double longitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.registeredAt = registeredAt;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getRegisteredAtAsLocalDateTime() {
        return LocalDateTime.parse(registeredAt);
    }

    public void setRegisteredAtAsLocalDateTime(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt.toString();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getInitials() {
        return firstName.substring(0, 1) + lastName.substring(0, 1);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setFullName(String fullName) {
        String[] names = fullName.split(" ");
        this.firstName = names[0];
        this.lastName = names[1];
    }

    public void setInitials(String initials) {
        this.firstName = initials.substring(0, 1);
        this.lastName = initials.substring(1, 2);
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
