package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.codesapiens.ahbap.data.service.ItemService;
import org.codesapiens.ahbap.data.service.PersonService;
import org.codesapiens.ahbap.data.service.RequirementService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@PageTitle("Yardım Profili!!!")
@Route(
        value = "/emergency/profile/:personId",
        layout = AppLayoutBottomNavbar.class
)
@RouteAlias(
        value = "/acil/profil/:personId",
        layout = AppLayoutBottomNavbar.class
)
public class EmergencyProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final PersonService personService;
    private final ItemService itemService;
    private final RequirementService requirementService;

    private final GeoLocation geoLocation;

    private final TextField phoneField;

    private final TextField firstNameField;

    private final TextField lastNameField;

    private final Button submitButton;

    private final List<CheckboxGroup<ItemEntity>> itemsCheck = new ArrayList<>();
    private final String sessionId = VaadinSession.getCurrent().getSession().getId();
    private PersonEntity currentPerson;
    public EmergencyProfileView(PersonService personService,
                                ItemService itemService, RequirementService requirementService,
                                GeoLocation geoLocation) {
        this.personService = personService;
        this.itemService = itemService;
        this.requirementService = requirementService;
        this.geoLocation = geoLocation;

        this.setSizeFull();

        add(
                this.geoLocation
        );

        phoneField = new TextField("Telefon");
        phoneField.setWidth(86, Unit.PERCENTAGE);
        phoneField.setReadOnly(true);

        firstNameField = new TextField("Ad");
        firstNameField.setWidth(86, Unit.PERCENTAGE);
        firstNameField.setReadOnly(true);

        lastNameField = new TextField("Soyad");
        lastNameField.setWidth(86, Unit.PERCENTAGE);
        lastNameField.setReadOnly(true);

        submitButton = new Button("Yardım Edildi", callHelpEvent());
        styleButton(submitButton);

        add(
                firstNameField,
                lastNameField,
                phoneField,
                submitButton
        );

        centralizeLayout(this);

        phoneField.focus();

    }


    private static void styleButton(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_ERROR);
        saveButton.setHeight(150, Unit.PIXELS);
        saveButton.setWidth(150, Unit.PIXELS);
        saveButton.getStyle()
                .set("font-weight", "bold")
                .set("margin-top", "0.5em")
                .set("font-size", "1.5em")
                .set("background-color", "var(--lumo-primary-color)")
                .set("color", "white")
                .set("border-radius", "50%");
        saveButton.setDisableOnClick(true);
    }

    private ComponentEventListener<ClickEvent<Button>> callHelpEvent() {

        Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue());

        return onSubmit -> {

            Notification.show("Yardım Edildi", 3000, Notification.Position.MIDDLE);

        };
    }

    public void centralizeLayout(VerticalLayout component) {
        component.setAlignItems(Alignment.CENTER);
        component.setJustifyContentMode(JustifyContentMode.CENTER);
        component.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

        add(new H2("Yardım Profili"));

        UUID personId = UUID.fromString(beforeEnterEvent.getRouteParameters().get("personId").get());

        personService.get(personId).ifPresent(per -> {
            this.currentPerson = per;
            this.firstNameField.setValue(this.currentPerson.getFirstName());
            this.lastNameField.setValue(this.currentPerson.getLastName());
            this.phoneField.setValue(this.currentPerson.getPhone());

            requirementService
                    .getByPersonId(this.currentPerson.getId())
                    .stream()
                    .map(RequirementEntity::getItem)
                    .collect(Collectors.groupingBy(ItemEntity::getCategory))
                    .forEach((cat, ig) -> {
                        CheckboxGroup<ItemEntity> itemEntityCheckboxGroup = new CheckboxGroup<>();
                        itemEntityCheckboxGroup.setItems(ig);
                        itemEntityCheckboxGroup.setLabel(cat);
                        itemsCheck.add(itemEntityCheckboxGroup);
                    });


            itemsCheck.forEach(icg -> {
                icg.setMaxWidth(86, Unit.PERCENTAGE);
                icg.setItemLabelGenerator(
                        it -> it.getTitle() + " (" + (
                                requirementService.countByItem(
                                        it.getId(), it.getCategory()
                                )
                        ) + ")"
                );
                add(
                        icg
                );
            });

        });
    }
}
