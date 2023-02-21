package org.codesapiens.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid;
import org.codesapiens.data.entity.RequirementEntity;
import org.codesapiens.data.service.StyleUtils;

import java.util.List;

@Tag("vaadin-requirements-group")
public class RequirementsGrid extends Grid<RequirementEntity> {

    public RequirementsGrid(List<RequirementEntity> requirements) {

        removeAllColumns();

        addColumn(r -> r.getItem().getTitle()).setHeader("Başlık");
        addColumn(r -> r.getQuantity()).setHeader("Miktar");
        addColumn(r -> r.getPriority()).setHeader("Öncelik");
        setItems(requirements);

        StyleUtils.removeAllMarginAndPadding(this.getElement());

    }
}
