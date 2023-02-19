package org.codesapiens.ahbap.data.service;

import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequirementService {

    private final RequirementRepository repository;

    @Autowired
    public RequirementService(RequirementRepository repository) {
        this.repository = repository;
    }

    public Optional<RequirementEntity> get(UUID id) {
        return repository.findById(id);
    }

    public List<RequirementEntity> getByPersonId(UUID personId) {
        return repository.findAllByPerson_Id(personId);
    }

    public List<RequirementEntity> get(String sessionId) {
        return repository.findAllBySessionId(sessionId);
    }

    public RequirementEntity update(RequirementEntity entity) {
        return repository.save(entity);
    }

    public void updateBySessionIdOrPersonId(UUID personId, String sessionId, List<ItemEntity> items) {

        var requirements = repository.findAllBySessionIdOrPerson_Id(sessionId, personId);
        repository.deleteAll(requirements);

        var foundPerson = requirements.get(0).getPerson();

        for (ItemEntity item : items) {
            var requirement = new RequirementEntity();
            requirement.setPerson(foundPerson);
            requirement.setSessionId(sessionId);
            requirement.setItem(item);
            requirement.setPriority(1);
            requirement.setQuantity(1.00);
            requirement.setDescription("");
            repository.save(requirement);
        }

    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<RequirementEntity> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public long countByItem(String title, String category) {
        return repository.countByItem_TitleAndItem_Category(
                title,
                category
        );
    }

    public long countByItem(UUID itemId, String category) {
        return repository.countByItem_IdAndItem_Category(
                itemId,
                category
        );
    }

}
