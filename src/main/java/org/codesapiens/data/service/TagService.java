package org.codesapiens.data.service;

import org.codesapiens.data.entity.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TagService {

    private final TagRepository repository;

    @Autowired
    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public Optional<TagEntity> get(UUID id) {
        return repository.findById(id);
    }

    public TagEntity update(TagEntity entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Cacheable("tags") public Page<TagEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<TagEntity> listBySymbol(Character symbol) {
        return repository.findAllBySymbol(symbol);
    }

    public int count() {
        return (int) repository.count();
    }

}
