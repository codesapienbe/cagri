package org.codesapiens.data.dto;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.codesapiens.data.entity.MessageEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class MessageDto {

    private PersonDto sender;

    private String channel;

    private String text;

    private LocalDate date;

    private LocalTime time;

    public static MessageDto fromEntity(MessageEntity entity) {
        final var dto = new MessageDto();
        dto.setSender(PersonDto.fromEntity(entity.getSender()));
        dto.setChannel(entity.getChannel());
        dto.setText(entity.getText());
        dto.setDate(entity.getDate());
        dto.setTime(entity.getTime());
        return dto;
    }

    public MessageEntity toEntity() {
        final var entity = new MessageEntity();
        entity.setSender(this.getSender().toEntity());
        entity.setChannel(this.getChannel());
        entity.setText(this.getText());
        entity.setDate(this.getDate());
        entity.setTime(this.getTime());
        return entity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public MessageDto() {
    }

    public MessageDto(PersonDto sender, String channel, String text) {
        this.sender = sender;
        this.channel = channel;
        this.text = text;
    }

    public PersonDto getSender() {
        return sender;
    }

    public void setSender(PersonDto sender) {
        this.sender = sender;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MessageDto))
            return false;
        MessageDto that = (MessageDto) o;
        return Objects.equals(sender, that.sender) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, channel, text);
    }

    @Override
    public String toString() {
        return this.getText();
    }

}
