package org.codesapiens.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.codesapiens.data.dto.ItemDto;
import org.codesapiens.data.dto.PersonDto;
import org.codesapiens.data.dto.RequirementDto;

import java.util.Objects;

@Entity
@Table(name = "requirements")
public class RequirementEntity extends AbstractEntity {

    @Column(nullable = false)
    private String sessionId;

    @ManyToOne(optional = false)
    private ItemEntity item;

    private Double quantity;

    @ManyToOne(optional = false)
    private PersonEntity person;

    @Min(1)
    @Max(10)
    private Integer priority;

    private String description;

    public static RequirementEntity fromDto(RequirementDto dto) {
        final var entity = new RequirementEntity();
        entity.setItem(ItemEntity.fromDto(dto.getItem()));
        entity.setPerson(PersonEntity.fromDto(dto.getPerson()));
        entity.setQuantity(dto.getQuantity());
        entity.setPriority(dto.getPriority());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public RequirementDto toDto() {
        final var dto = new RequirementDto();
        dto.setItem(ItemDto.fromEntity(this.getItem()));
        dto.setPerson(PersonDto.fromEntity(this.getPerson()));
        dto.setQuantity(this.getQuantity());
        dto.setPriority(this.getPriority());
        dto.setDescription(this.getDescription());
        return dto;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
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
        if (!(o instanceof RequirementEntity))
            return false;
        if (!super.equals(o))
            return false;
        RequirementEntity that = (RequirementEntity) o;
        return getItem().equals(that.getItem()) && getPerson().equals(that.getPerson());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getItem(), getPerson());
    }
}
