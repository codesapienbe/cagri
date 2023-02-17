package org.codesapiens.ahbap.data.service;

import org.codesapiens.ahbap.data.entity.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository repository;

    @Autowired
    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public Optional<MessageEntity> get(UUID id) {
        return repository.findById(id);
    }

    public MessageEntity update(MessageEntity entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<MessageEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<MessageEntity> listByChannel(String channel) {
        return repository.findAllByChannel(channel);
    }

    public int count() {
        return (int) repository.count();
    }

}
