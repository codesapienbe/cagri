package org.codesapiens.data.service;

import org.codesapiens.data.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<TagEntity, UUID> {

    Optional<TagEntity> findByTitle(String title);

    List<TagEntity> findAllBySymbol(Character symbol);
}