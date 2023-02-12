package org.codesapiens.ahbap.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Entity
@Table(name = "items")
public class ItemEntity extends AbstractEntity {

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String title;
    private String description;

    public ItemEntity() {
    }

    public ItemEntity(String title) {
        this.title = title;
        this.description = title;
        this.category = "Tanımsız";
    }

    public ItemEntity(String title, String category) {
        this.title = title;
        this.category = category;
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
        if (!(o instanceof ItemEntity)) return false;
        if (!super.equals(o)) return false;
        ItemEntity that = (ItemEntity) o;
        return getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle());
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":'" + title + '\'' +
                ", \"description\":'" + description + '\'' +
                ", \"category\":'" + category + '\'' +
                "}";
    }
}
