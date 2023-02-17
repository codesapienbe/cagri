package org.codesapiens.ahbap.data.service;

import org.codesapiens.ahbap.data.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findAllByChannel(String channel);
}