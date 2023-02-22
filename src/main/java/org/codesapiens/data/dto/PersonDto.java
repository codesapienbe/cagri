package org.codesapiens.data.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codesapiens.data.entity.PersonEntity;

import java.time.LocalDateTime;
import java.util.Objects;

public class PersonDto {

    private String firstName;

    private String lastName;

    private String phone;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    private String registeredAt;

    private String sessionId;

    public static PersonDto fromEntity(PersonEntity entity) {
        final var dto = new PersonDto();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhone(entity.getPhone());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setImageUrl(entity.getImageUrl());
        dto.setRegisteredAt(entity.getRegisteredAt());
        dto.setSessionId(entity.getSessionId());
        return dto;
    }

    public PersonEntity toEntity() {
        final var entity = new PersonEntity();
        entity.setFirstName(this.getFirstName());
        entity.setLastName(this.getLastName());
        entity.setPhone(this.getPhone());
        entity.setLatitude(this.getLatitude());
        entity.setLongitude(this.getLongitude());
        entity.setImageUrl(this.getImageUrl());
        entity.setRegisteredAt(this.getRegisteredAt());
        entity.setSessionId(this.getSessionId());
        return entity;
    }

    public PersonDto() {
    }

    public PersonDto(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public PersonDto(String firstName, String lastName, String phone, String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public PersonDto(String firstName, String lastName, String phone,
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
        if (this == o)
            return true;
        if (!(o instanceof PersonDto))
            return false;
        if (!super.equals(o))
            return false;
        PersonDto that = (PersonDto) o;
        return getPhone().equals(that.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPhone());
    }
}
