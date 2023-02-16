package org.codesapiens.ahbap.data.service;

import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    Optional<PersonEntity> findByPhone(String phone);

    Optional<PersonEntity> findByFirstNameLikeOrLastNameLike(String firstName, String lastName);

    Optional<PersonEntity> findBySessionId(String sessionId);
}