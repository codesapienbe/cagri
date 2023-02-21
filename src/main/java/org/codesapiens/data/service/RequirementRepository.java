package org.codesapiens.data.service;

import org.codesapiens.data.entity.RequirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface RequirementRepository extends JpaRepository<RequirementEntity, UUID> {

    List<RequirementEntity> findAllBySessionId(@NotEmpty String sessionId);

    List<RequirementEntity> findAllByPerson_Id(@NotNull UUID person_id);

    List<RequirementEntity> findAllBySessionIdOrPerson_Id(@NotEmpty String sessionId, @NotNull UUID person_id);

    Long countByItem_TitleAndItem_Category(@NotEmpty String title, @NotEmpty String category);

    Long countByItem_IdAndItem_Category(@NotNull UUID item_id, @NotEmpty String item_category);


}