package org.codesapiens.ahbap.data.config;

import org.codesapiens.ahbap.data.entity.MessageEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class IndexEngineConfig implements CommandLineRunner {


    // HIBERNATE_INDEXING_CONFIG
    @PersistenceContext
    EntityManager entityManager;

    // HIBERNATE_INDEXING_CONFIG
    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        final var searchSession = Search.session(entityManager);
        final var messageIndexer = searchSession.massIndexer(MessageEntity.class);
        messageIndexer.startAndWait();
        final var peopleIndexer = searchSession.massIndexer(PersonEntity.class);
        peopleIndexer.startAndWait();
    }

}
