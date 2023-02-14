package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.codesapiens.ahbap.data.entity.TagEntity;
import org.codesapiens.ahbap.data.service.*;
import org.springframework.data.domain.Pageable;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.util.*;
import java.util.stream.Collectors;

@PageTitle("Yardım Çağır!!!")
@Route(
        value = "/map/call",
        layout = AppLayoutBottomNavbar.class
)
@RouteAlias(
        value = "harita/cagri",
        layout = AppLayoutBottomNavbar.class
)
public class MapCallView extends VerticalLayout {

    private final PersonService personService;
    private final ItemService itemService;

    private final TagService tagService;
    private final RequirementService requirementService;

    private final GeoLocation geoLocation;

    private final TextField phoneField;

    private final TextField firstNameField;

    private final TextField lastNameField;

    private final Button callHelpOnTwitterButton;

    private final Button callHelpOnFacebookButton;

    private final Button callHelpOnWhatsAppButton;

    private final Button callHelpOnSmsButton;

    private final List<CheckboxGroup<ItemEntity>> itemsCheck = new ArrayList<>();

    private final String sessionId = VaadinSession.getCurrent().getSession().getId();

    private final NotificationService notification;

    private final Accordion accordion = new Accordion();

    public MapCallView(PersonService personService,
                       ItemService itemService, TagService tagService, RequirementService requirementService,
                       GeoLocation geoLocation, NotificationService notification) {

        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;

        this.geoLocation = geoLocation;
        this.notification = notification;

        this.setSizeFull();

        this.accordion.setWidthFull();

        add(
                this.geoLocation
        );

        FormLayout personFormLayout = createFormLayout();
        AccordionPanel personFormAccordion = accordion.add("Kişisel bilgileriniz", personFormLayout);

        phoneField = new TextField("Telefon");
        phoneField.setWidth(90, Unit.PERCENTAGE);
        phoneField.setValue("+90");

        firstNameField = new TextField("Ad");
        firstNameField.setWidth(50, Unit.PERCENTAGE);

        lastNameField = new TextField("Soyad");
        lastNameField.setWidth(50, Unit.PERCENTAGE);

        personFormLayout.add(
                firstNameField,
                lastNameField
        );
        personFormLayout.add(
                phoneField, 2
        );

        personFormAccordion.addOpenedChangeListener(onOpenedChange -> {
            String fullName = firstNameField.getValue() + " " + lastNameField.getValue();
            onOpenedChange.getSource().setSummaryText(fullName);
        });

        Map<String, List<ItemEntity>> itemsDataByCategory = itemService.list(Pageable.ofSize(100)).getContent().stream()
                .collect(Collectors.groupingBy(ItemEntity::getCategory));

        for (Map.Entry<String, List<ItemEntity>> entry : itemsDataByCategory.entrySet()) {
            String category = entry.getKey();
            List<ItemEntity> items = entry.getValue();

            CheckboxGroup<ItemEntity> group = new CheckboxGroup<>();
            group.setItems(items);
            group.setLabel(category);

            itemsCheck.add(group);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        styleButtonLayout(buttonsLayout);

        callHelpOnTwitterButton = new Button("Twitter", callHelpOnTwitterEvent());
        styleTwitterButton();
        buttonsLayout.add(
                callHelpOnTwitterButton
        );

        callHelpOnFacebookButton = new Button("Facebook", callHelpOnFacebookEvent());
        styleFacebookButton();
        // TODO: activate later when the function works properly
//        buttonsLayout.add(
//                callHelpOnFacebookButton
//        );

        callHelpOnWhatsAppButton = new Button("WhatsApp", callHelpOnWhatsAppEvent());
        styleWhatsAppButton();
        buttonsLayout.add(
                callHelpOnWhatsAppButton
        );

        callHelpOnSmsButton = new Button("SMS", callHelpOnSmsEvent());
        styleSmsButton();
        buttonsLayout.add(
                callHelpOnSmsButton
        );

        for (CheckboxGroup<ItemEntity> icg : itemsCheck) {
            icg.setMaxWidth(86, Unit.PERCENTAGE);
            icg.setItemLabelGenerator(ItemEntity::getTitle);

            AccordionPanel checkGroupAccordion = accordion.add(icg.getLabel(), icg);
            checkGroupAccordion.setOpened(false);

            checkGroupAccordion.addOpenedChangeListener(onOpenedChange -> {
                int count = icg.getSelectedItems().size();
                onOpenedChange.getSource().setSummaryText(icg.getLabel() + "(" + count + ")");
            });

            icg.addSelectionListener(onSelection -> {
                Set<ItemEntity> selectedItems = onSelection.getValue();
                checkGroupAccordion.setSummaryText(icg.getLabel() + "(" + selectedItems.size() + ")");
            });
        }

        centralizeLayout(this);

        add(
                this.accordion,
                buttonsLayout
        );

    }

    private static void styleButtonLayout(HorizontalLayout buttonsLayout) {
        buttonsLayout.setPadding(false);
        buttonsLayout.setSpacing(false);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsLayout.setWidthFull();
    }

    private void styleTwitterButton() {
        callHelpOnTwitterButton.getStyle()
                .set("text-align", "center")
                .set("margin-top", "10px")
                .set("margin-left", "10px")
                .set("margin-right", "10px")
                .set("background-color", "#00acee")
                .set("border-radius", "5px")
                .set("color", "#FFFFFF");
    }

    private void styleFacebookButton() {
        callHelpOnFacebookButton.getStyle()
                .set("text-align", "center")
                .set("margin-top", "10px")
                .set("margin-left", "10px")
                .set("margin-right", "10px")
                .set("background-color", "#3b5998")
                .set("border-radius", "5px")
                .set("color", "#FFFFFF");
    }

    private void styleWhatsAppButton() {
        callHelpOnWhatsAppButton.getStyle()
                .set("text-align", "center")
                .set("margin-top", "10px")
                .set("margin-left", "10px")
                .set("margin-right", "10px")
                .set("background-color", "#25D366")
                .set("border-radius", "5px")
                .set("color", "#FFFFFF");
    }

    private void styleSmsButton() {
        callHelpOnSmsButton.getStyle()
                .set("text-align", "center")
                .set("margin-top", "10px")
                .set("margin-left", "10px")
                .set("margin-right", "10px")
                .set("background-color", "#d8d8d8")
                .set("border-radius", "5px")
                .set("color", "#FFFFFF");
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));
        return formLayout;
    }

    private ComponentEventListener<ClickEvent<Button>> callHelpOnSmsEvent() {
        return onClick -> {
            Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

            if (foundPerson.isPresent()) {
                notification.sendNotification("Daha önce çağrınız mevcut.");
            } else {
                PersonEntity personToSave = mapToPerson();
                PersonEntity savedPerson = personService.update(personToSave);

                for (CheckboxGroup<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                    for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                        RequirementEntity requirementToSave = mapToRequirement(savedPerson, item);
                        requirementService.update(requirementToSave);
                    }
                }

                notification.sendNotification("SMS çağrınız gönderildi.");

                shareOnSMS(
                        personToSave,
                        itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
                );
            }

        };
    }

    private void shareOnSMS(PersonEntity savedPerson, List<ItemEntity> requiredItems) {
        Double[] from = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        Double[] to = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        final var directionsUrl = "https://www.google.com/maps/dir" + "/" + from[0] + "," + from[1] + "/" + to[0] + ","
                + to[1] + "/@" + to[0] + "," + to[1];


        String absoluteUrl = "sms:+905312864182?&amp;body=" +
                "ACİL%20YARDIM%20ÇAĞRISI!!!" +
                "%20" +
                "İhtiyacım%20olanlar:" +
                "%20" +
                requiredItems.stream()
                        .map(it -> it.getTitle().replace(" ", "%20"))
                        .collect(Collectors.joining("+")) +
                "%20" +
                "Konumum:" +
                "%20" +
                directionsUrl;

        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", absoluteUrl));
    }

    private ComponentEventListener<ClickEvent<Button>> callHelpOnWhatsAppEvent() {
        return onClick -> {
            Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

            if (foundPerson.isPresent()) {
                notification.sendNotification("Daha önce çağrınız mevcut.");
            } else {
                PersonEntity personToSave = mapToPerson();
                PersonEntity savedPerson = personService.update(personToSave);

                for (CheckboxGroup<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                    for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                        RequirementEntity requirementToSave = mapToRequirement(savedPerson, item);
                        requirementService.update(requirementToSave);
                    }
                }

                notification.sendNotification("Whatsapp çağrınız alındı.");

                shareOnWhatsApp(
                        personToSave,
                        itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
                );
            }

        };
    }

    private ComponentEventListener<ClickEvent<Button>> callHelpOnFacebookEvent() {
        return onClick -> {

            Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

            if (foundPerson.isPresent()) {
                notification.sendNotification("Daha önce çağrınız mevcut.");
            } else {
                PersonEntity personToSave = mapToPerson();
                PersonEntity savedPerson = personService.update(personToSave);

                for (CheckboxGroup<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                    for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                        RequirementEntity requirementToSave = mapToRequirement(savedPerson, item);
                        requirementService.update(requirementToSave);
                    }
                }

                notification.sendNotification("Facebook çağrınız paylaşıldı.");

                shareOnFacebook(
                        personToSave,
                        itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
                );
            }
        };
    }

    private void shareOnFacebook(PersonEntity savedPerson, List<ItemEntity> requiredItems) {
        // set content of the post for Facebook
        Double[] from = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        Double[] to = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        final var directionsUrl = "https://www.google.com/maps/dir" + "/" + from[0] + "," + from[1] + "/" + to[0] + ","
                + to[1] + "/@" + to[0] + "," + to[1];

        String absoluteUrl = "https://www.facebook.com/sharer.php?body=" +
                "ACİL%20YARDIM%20ÇAĞRISI!!!" +
                "%20" +
                "İhtiyacım%20olanlar:" +
                "%20" +
                requiredItems.stream()
                        .map(it -> it.getTitle().replace(" ", "%20"))
                        .collect(Collectors.joining("+")) +
                "%20" +
                "Konumum:" +
                "%20" +
                directionsUrl;

        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", absoluteUrl));
    }

    private ComponentEventListener<ClickEvent<Button>> callHelpOnTwitterEvent() {

        return onSubmit -> {

            Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

            if (foundPerson.isPresent()) {
                notification.sendNotification("Daha önce çağrınız mevcut.");
            } else {
                PersonEntity personToSave = mapToPerson();
                PersonEntity savedPerson = personService.update(personToSave);

                for (CheckboxGroup<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                    for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                        RequirementEntity requirementToSave = mapToRequirement(savedPerson, item);
                        requirementService.update(requirementToSave);
                    }
                }

                notification.sendNotification("Çağrınız alındı. Lütfen çağrı kanalı seçiniz.");

                shareOnTwitter(
                        personToSave,
                        itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
                );
            }

        };
    }

    private void shareOnTwitter(PersonEntity savedPerson, List<ItemEntity> requiredItems) {
        StringBuilder sb = new StringBuilder("https://twitter.com/share")
                .append("?")
                .append("text=ACİL YARDIM ÇAĞRISI!!! ")
                .append(("\n"))
                .append("İhtiyacım olanlar: ")
                .append(("\n"))
                .append(
                        requiredItems.stream()
                                .map(ItemEntity::getTitle)
                                .collect(Collectors.joining("+"))
                )
                .append("&")
                .append("url=")
                .append("https://maps.google.com/maps?z=12")
                .append("&")
                .append("t=m")
                .append("&")
                .append("q=loc:")
                .append(savedPerson.getLatitude())
                .append("+")
                .append(savedPerson.getLongitude());

        List<TagEntity> annotations = tagService.listBySymbol('@');
        sb.append("&via=");

        for (int i = 0; i < annotations.size(); i++) {
            TagEntity ann = annotations.get(i);
            sb.append(ann.getTitle());
            if (i < annotations.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("&hashtags=");
        List<TagEntity> hashtags = tagService.listBySymbol('#');
        for (int i = 0; i < hashtags.size(); i++) {
            TagEntity htag = hashtags.get(i);
            sb.append(htag.getTitle());
            if (i != hashtags.size() - 1) {
                sb.append(",");
            }
        }

        String url = sb.toString();
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", url));
    }

    private String convertTextToUri(String text) {
        return text.replace(" ", "%20");
    }

    private void shareOnWhatsApp(PersonEntity savedPerson, List<ItemEntity> requiredItems) {

        Double[] from = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        Double[] to = {
                savedPerson.getLatitude(),
                savedPerson.getLongitude()
        };

        final var directionsUrl = "https://www.google.com/maps/dir" + "/" + from[0] + "," + from[1] + "/" + to[0] + ","
                + to[1] + "/@" + to[0] + "," + to[1];


        String absoluteUrl = "https://wa.me?text=" +
                "ACİL%20YARDIM%20ÇAĞRISI!!!" +
                "%20" +
                "İhtiyacım%20olanlar:" +
                "%20" +
                requiredItems.stream()
                        .map(it -> it.getTitle().replace(" ", "%20"))
                        .collect(Collectors.joining("+")) +
                "%20" +
                "Konumum:" +
                "%20" +
                directionsUrl;

        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", absoluteUrl));
    }

    private PersonEntity mapToPerson() {
        PersonEntity personEntity = new PersonEntity();
        personEntity.setFirstName(firstNameField.getValue());
        personEntity.setLastName(lastNameField.getValue());
        personEntity.setPhone(phoneField.getValue());
        personEntity.setLatitude(geoLocation.getValue().getLatitude());
        personEntity.setLongitude(geoLocation.getValue().getLongitude());
        return personEntity;
    }

    private RequirementEntity mapToRequirement(PersonEntity foundPerson, ItemEntity item) {
        RequirementEntity requirement = new RequirementEntity();
        requirement.setPerson(foundPerson);
        requirement.setItem(item);
        if (item.getCategory().contains("Medikal")) {
            requirement.setPriority(10);
        } else {
            requirement.setPriority(1);
        }
        requirement.setQuantity(1.00);
        requirement.setSessionId(sessionId);

        return requirement;
    }

    public void centralizeLayout(VerticalLayout component) {
        component.setAlignItems(Alignment.CENTER);
        component.setJustifyContentMode(JustifyContentMode.CENTER);
        component.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    }

}
