package org.codesapiens.data.service;

import org.codesapiens.data.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {

    Optional<ItemEntity> findByTitle(String title);
}