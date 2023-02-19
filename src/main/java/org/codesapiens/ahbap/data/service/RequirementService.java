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

    private final RequirementRepository requirementRepository;
    private final PersonRepository personRepository;

    @Autowired
    public RequirementService(RequirementRepository requirementRepository, PersonRepository personRepository) {
        this.requirementRepository = requirementRepository;
        this.personRepository = personRepository;
    }

    public Optional<RequirementEntity> get(UUID id) {
        return requirementRepository.findById(id);
    }

    public List<RequirementEntity> getByPersonId(UUID personId) {
        return requirementRepository.findAllByPerson_Id(personId);
    }

    public List<RequirementEntity> get(String sessionId) {
        return requirementRepository.findAllBySessionId(sessionId);
    }

    public RequirementEntity update(RequirementEntity entity) {
        return requirementRepository.save(entity);
    }

    public boolean updateBySessionIdOrPersonId(UUID personId, String sessionId, List<ItemEntity> items) {

        var requirements = requirementRepository.findAllBySessionIdOrPerson_Id(sessionId, personId);

        if (!requirements.isEmpty()) {
            requirementRepository.deleteAll(requirements);
        }

        var foundPerson = personRepository.findById(personId);

        if (foundPerson.isEmpty()) {
            return false;
        } else {
            var person = foundPerson.get();

            for (ItemEntity item : items) {
                var requirement = new RequirementEntity();
                requirement.setPerson(person);
                requirement.setSessionId(sessionId);
                requirement.setItem(item);
                requirement.setPriority(1);
                requirement.setQuantity(1.00);
                requirement.setDescription("");
                requirementRepository.save(requirement);
            }

            return true;
        }


    }

    public void delete(UUID id) {
        requirementRepository.deleteById(id);
    }

    public Page<RequirementEntity> list(Pageable pageable) {
        return requirementRepository.findAll(pageable);
    }

    public int count() {
        return (int) requirementRepository.count();
    }

    public long countByItem(String title, String category) {
        return requirementRepository.countByItem_TitleAndItem_Category(
                title,
                category
        );
    }

    public long countByItem(UUID itemId, String category) {
        return requirementRepository.countByItem_IdAndItem_Category(
                itemId,
                category
        );
    }

}
