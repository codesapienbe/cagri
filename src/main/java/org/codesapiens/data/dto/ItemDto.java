package org.codesapiens.data.dto;

import org.codesapiens.data.entity.ItemEntity;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

public class ItemDto {

    private String title;

    private String description;

    public static ItemDto fromEntity(ItemEntity entity) {
        final var dto = new ItemDto();
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        return dto;
    }

    public ItemEntity toEntity() {
        final var entity = new ItemEntity();
        entity.setTitle(this.getTitle());
        entity.setDescription(this.getDescription());
        entity.setCategory(this.getCategory());
        return entity;
    }

    public ItemDto() {
        this.setCategory("Tanımsız");
    }

    public ItemDto(String title) {
        this();
        this.setTitle(title);
        this.setDescription(title);
    }

    public ItemDto(String title, String category) {
        this(title);
        this.setCategory(category);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotEmpty
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        if (!super.equals(o)) return false;
        ItemDto that = (ItemDto) o;
        return getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle());
    }

    @Override
    public String toString() {
        return this.getTitle();
    }
}
