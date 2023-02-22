package org.codesapiens.data.dto;

import java.util.Objects;

import org.codesapiens.data.entity.TagEntity;

public class TagDto {

    private String title;

    private String url;

    private Character symbol;

    public static TagDto fromEntity(TagEntity entity) {
        final var dto = new TagDto();
        dto.setTitle(entity.getTitle());
        dto.setUrl(entity.getUrl());
        dto.setSymbol(entity.getSymbol());
        return dto;
    }

    public TagEntity toEntity() {
        final var entity = new TagEntity();
        entity.setTitle(this.getTitle());
        entity.setUrl(this.getUrl());
        entity.setSymbol(this.getSymbol());
        return entity;
    }

    public TagDto(String title, String url) {
        this.title = title;
        this.url = url;
        this.symbol = '#';
    }

    public TagDto() {

    }

    public TagDto(String title) {
        this.title = title;
        this.symbol = '#';
    }

    public TagDto(String title, Character symbol) {
        this.title = title;
        this.symbol = symbol;
    }

    public TagDto(String title, String url, Character symbol) {
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
        if (this == o)
            return true;
        if (!(o instanceof TagDto))
            return false;
        if (!super.equals(o))
            return false;
        TagDto that = (TagDto) o;
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
