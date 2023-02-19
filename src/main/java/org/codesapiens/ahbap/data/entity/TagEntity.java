package org.codesapiens.ahbap.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "tags")
public class TagEntity extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @Column(unique = true)
    private String url;

    private Character symbol;

    public TagEntity() {

    }

    public TagEntity(String title) {
        this.title = title;
        this.symbol = '#';
    }

    public TagEntity(String title, Character symbol) {
        this.title = title;
        this.symbol = symbol;
    }

    public TagEntity(String title, String url, Character symbol) {
        this.title = title;
        this.url = url;
        this.symbol = symbol;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Character getSymbol() {
        return symbol;
    }

    public void setSymbol(Character symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagEntity)) return false;
        if (!super.equals(o)) return false;
        TagEntity that = (TagEntity) o;
        return getTitle().equals(that.getTitle()) && Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle(), getUrl());
    }

    @Override
    public String toString() {
        return this.getSymbol() + this.getTitle();
    }
}
