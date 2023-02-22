package org.codesapiens.data.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.codesapiens.data.entity.RequirementEntity;

import java.util.Objects;

public class RequirementDto {

    private String sessionId;

    private ItemDto item;

    private Double quantity;

    private PersonDto person;

    private Integer priority;

    private String description;

    public static RequirementDto fromEntity(RequirementEntity entity) {
        final var dto = new RequirementDto();
        dto.setItem(ItemDto.fromEntity(entity.getItem()));
        dto.setPerson(PersonDto.fromEntity(entity.getPerson()));
        dto.setQuantity(entity.getQuantity());
        dto.setPriority(entity.getPriority());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    public RequirementEntity toEntity() {
        final var entity = new RequirementEntity();
        entity.setItem(this.getItem().toEntity());
        entity.setPerson(this.getPerson().toEntity());
        entity.setQuantity(this.getQuantity());
        entity.setPriority(this.getPriority());
        entity.setDescription(this.getDescription());
        return entity;
    }

    public ItemDto getItem() {
        return item;
    }

    public void setItem(ItemDto item) {
        this.item = item;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RequirementDto))
            return false;
        if (!super.equals(o))
            return false;
        RequirementDto that = (RequirementDto) o;
        return getItem().equals(that.getItem()) && getPerson().equals(that.getPerson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getItem(), getPerson());
    }
}
