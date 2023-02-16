package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.codesapiens.ahbap.data.entity.TagEntity;
import org.codesapiens.ahbap.data.service.*;
import org.springframework.data.domain.PageRequest;
import org.vaadin.elmot.flow.sensors.GeoLocation;
import software.xdev.vaadin.maps.leaflet.flow.LMap;
import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LMarker;
import software.xdev.vaadin.maps.leaflet.flow.data.LTileLayer;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@PageTitle("Harita | Yardım Çağır ")
@Route("")
public class HomeView extends VerticalLayout {

    private final PersonService personService;
    private final ItemService itemService;
    private final TagService tagService;
    private final RequirementService requirementService;
    private final ShareService shareService;
    private final NotificationService notification;

    private final GeoLocation geoLocation = new GeoLocation();

    // Create a click counter for findMeButton
    private final AtomicInteger findMeClickCount = new AtomicInteger(0);

    public HomeView(PersonService personService, ItemService itemService,
                    TagService tagService, RequirementService requirementService,
                    ShareService shareService, NotificationService notification) {

        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;
        this.shareService = shareService;
        this.notification = notification;

        StyleUtils.pageBackground(this.getElement());

        geoLocation.setWatch(true);
        geoLocation.setHighAccuracy(true);
        geoLocation.setTimeout(100000);
        geoLocation.setMaxAge(200000);
        add(geoLocation);

        this.setSizeFull();

        final var accordion = new Accordion();
        accordion.setWidthFull();

        final var footerButtonsLayout = new HorizontalLayout();
        StyleUtils.footerLayout(footerButtonsLayout.getElement());

        final var btnFindMe = new Button("Ben neredeyim?");
        StyleUtils.footerButton(btnFindMe, "left", "#4caf50");
        btnFindMe.addClickListener(onClick -> onFindMeClick(geoLocation));

        final var btnCallHelp = new Button("Çağrı Yap");
        StyleUtils.footerButton(btnCallHelp, "right", "#f44336");

        btnCallHelp.addClickListener(onClick -> onCallHelpClick(geoLocation));
        footerButtonsLayout.add(btnFindMe, btnCallHelp);

        this.add(footerButtonsLayout);
        this.setSizeFull();
    }

    private void onCallHelpClick(GeoLocation geoLocation) {
        final var icoClose = VaadinIcon.CLOSE.create();

        final var dialog = new Dialog(icoClose);
        dialog.setMaxHeight(90, Unit.PERCENTAGE);
        dialog.setMaxWidth(80, Unit.PERCENTAGE);
        dialog.setCloseOnEsc(false);
        dialog.setDraggable(true);

        dialog.open();

        final var formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("20em", 2));

        final var phoneField = new TextField("Telefon");
        final var firstNameField = new TextField("Ad");
        final var lastNameField = new TextField("Soyad");
        final var descriptionField = new TextArea("Açıklama");

        formLayout.add(firstNameField, lastNameField);
        formLayout.add(descriptionField, 2);
        formLayout.add(phoneField, 2);

        final var itemsGroupedByCategory = this.itemService.list(PageRequest.of(0, 100)).stream()
                .collect(Collectors.groupingBy(ItemEntity::getCategory));

        itemsGroupedByCategory.forEach((category, items) -> {
            final var lBox = new MultiSelectListBox<ItemEntity>();
            lBox.setItems(items);
            lBox.setItemLabelGenerator(ItemEntity::getTitle);

            formLayout.add(lBox, 2);
        });

        dialog.add(formLayout);

        final var buttonsLayout = new HorizontalLayout();
        StyleUtils.shareButtons(buttonsLayout.getElement());

        final var callHelpOnTwitterIcon = new Image("https://www.svgrepo.com/show/489937/twitter.svg", "WhatsApp");
        callHelpOnTwitterIcon.setWidth("50px");
        callHelpOnTwitterIcon.setHeight("50px");
        final var callHelpOnTwitterButton = new Button(callHelpOnTwitterIcon,
                onClick -> callHelpOnTwitterEvent(
                        formLayout.getChildren()
                                .filter(component -> component instanceof MultiSelectListBox)
                                .map(component -> (MultiSelectListBox<ItemEntity>) component)
                                .collect(Collectors.toList()),
                        phoneField,
                        firstNameField,
                        lastNameField,
                        geoLocation
                )
        );
        StyleUtils.dialogButton(callHelpOnTwitterButton.getElement());
        buttonsLayout.add(
                callHelpOnTwitterButton
        );

        final var callHelpOnFacebookIcon = new Image("https://www.svgrepo.com/show/452197/facebook.svg", "WhatsApp");
        callHelpOnFacebookIcon.setWidth("50px");
        callHelpOnFacebookIcon.setHeight("50px");
        final var callHelpOnFacebookButton = new Button(callHelpOnFacebookIcon,
                onClick -> callHelpOnFacebookEvent(
                        formLayout.getChildren()
                                .filter(component -> component instanceof MultiSelectListBox)
                                .map(component -> (MultiSelectListBox<ItemEntity>) component)
                                .collect(Collectors.toList()),
                        phoneField,
                        geoLocation
                )
        );
        StyleUtils.dialogButton(callHelpOnFacebookButton.getElement());
        // TODO: fix it later it does not work
        callHelpOnFacebookButton.setEnabled(false);
        buttonsLayout.add(
                callHelpOnFacebookButton
        );

        final var callHelpOnWhatsAppIcon = new Image("https://www.svgrepo.com/show/452133/whatsapp.svg", "WhatsApp");
        callHelpOnWhatsAppIcon.setWidth("50px");
        callHelpOnWhatsAppIcon.setHeight("50px");
        final var callHelpOnWhatsAppButton = new Button(callHelpOnWhatsAppIcon, onClick -> callHelpOnWhatsAppEvent(
                formLayout.getChildren()
                        .filter(component -> component instanceof MultiSelectListBox)
                        .map(component -> (MultiSelectListBox<ItemEntity>) component)
                        .collect(Collectors.toList()),
                phoneField,
                geoLocation
        ));
        StyleUtils.dialogButton((callHelpOnWhatsAppButton.getElement()));
        buttonsLayout.add(
                callHelpOnWhatsAppButton
        );

        final var callHelpOnSmsIcon = new Image("https://www.svgrepo.com/show/375147/sms.svg", "SMS");
        callHelpOnSmsIcon.setWidth("50px");
        callHelpOnSmsIcon.setHeight("50px");
        final var callHelpOnSmsButton = new Button(callHelpOnSmsIcon, onClick -> callHelpOnSmsEvent(
                formLayout.getChildren()
                        .filter(component -> component instanceof MultiSelectListBox)
                        .map(component -> (MultiSelectListBox<ItemEntity>) component)
                        .collect(Collectors.toList()),
                phoneField,
                geoLocation
        ));
        StyleUtils.dialogButton(callHelpOnSmsButton.getElement());
        buttonsLayout.add(
                callHelpOnSmsButton
        );

        dialog.add(buttonsLayout);

        icoClose.addClickListener(iev -> dialog.close());
    }


    private void callHelpOnSmsEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck,
            TextField phoneField, GeoLocation geoLocation
    ) {
        Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

        if (foundPerson.isPresent()) {
            notification.sendNotification("Daha önce çağrınız mevcut.");
        } else {
            PersonEntity personToSave = findOrCreatePerson(phoneField, geoLocation);
            PersonEntity savedPerson = personService.update(personToSave);

            for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                    RequirementEntity requirementToSave = mapToRequirement(savedPerson, item, VaadinSession.getCurrent().getSession().getId());
                    requirementService.update(requirementToSave);
                }
            }

            notification.sendNotification("SMS çağrınız gönderildi.");

            shareOnSMS(
                    personToSave,
                    itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
            );
        }

    }

    private PersonEntity findOrCreatePerson(@NotNull TextField phoneField, @NotNull GeoLocation geoLocation) {
        return mapToPerson(
                "",
                "",
                phoneField.getValue(),
                geoLocation.getValue().getLatitude(),
                geoLocation.getValue().getLongitude(),
                VaadinSession.getCurrent().getSession().getId()
        );
    }

    private void shareOnSMS(PersonEntity savedPerson, List<ItemEntity> requiredItems) {
        final var sms = shareService.getShareUrl(savedPerson, requiredItems);
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", sms));
    }

    private void callHelpOnWhatsAppEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck,
            TextField phoneField,
            GeoLocation geoLocation
    ) {
        Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

        if (foundPerson.isPresent()) {
            notification.sendNotification("Daha önce çağrınız mevcut.");
        } else {
            PersonEntity personToSave = findOrCreatePerson(phoneField, geoLocation);
            PersonEntity savedPerson = personService.update(personToSave);

            for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                    RequirementEntity requirementToSave = mapToRequirement(
                            savedPerson,
                            item,
                            VaadinSession.getCurrent().getSession().getId()
                    );
                    requirementService.update(requirementToSave);
                }
            }

            notification.sendNotification("Whatsapp çağrınız alındı.");

            shareOnWhatsApp(
                    personToSave,
                    itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
            );
        }

    }

    private void callHelpOnFacebookEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck,
            TextField phoneField,
            GeoLocation geoLocation
    ) {

        Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

        if (foundPerson.isPresent()) {
            notification.sendNotification("Daha önce çağrınız mevcut.");
        } else {
            PersonEntity personToSave = findOrCreatePerson(phoneField, geoLocation);
            PersonEntity savedPerson = personService.update(personToSave);

            for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                    RequirementEntity requirementToSave = mapToRequirement(
                            savedPerson,
                            item,
                            VaadinSession.getCurrent().getSession().getId()
                    );
                    requirementService.update(requirementToSave);
                }
            }

            notification.sendNotification("Facebook çağrınız paylaşıldı.");

            shareOnFacebook(
                    personToSave,
                    itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
            );
        }
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

    private void callHelpOnTwitterEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck,
            TextField phoneField,
            TextField firstNameField,
            TextField lastNameField,
            GeoLocation geoLocation
    ) {


        Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

        if (foundPerson.isPresent()) {
            notification.sendNotification("Daha önce çağrınız mevcut.");
        } else {
            PersonEntity personToSave = mapToPerson(
                    firstNameField.getValue(),
                    lastNameField.getValue(),
                    phoneField.getValue(),
                    geoLocation.getValue().getLatitude(),
                    geoLocation.getValue().getLongitude(),
                    VaadinSession.getCurrent().getSession().getId()
            );
            PersonEntity savedPerson = personService.update(personToSave);

            for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                    RequirementEntity requirementToSave = mapToRequirement(savedPerson, item, VaadinSession.getCurrent().getSession().getId());
                    requirementService.update(requirementToSave);
                }
            }

            notification.sendNotification("Çağrınız alındı. Lütfen çağrı kanalı seçiniz.");

            shareOnTwitter(
                    personToSave,
                    itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
            );
        }

    }

    private void shareOnTwitter(PersonEntity savedPerson, List<ItemEntity> requiredItems) {
        List<TagEntity> annotations = tagService.listBySymbol('@');
        List<TagEntity> hashtags = tagService.listBySymbol('#');
        final var url = this.shareService.shareOnTwitter(savedPerson, requiredItems, annotations, hashtags);
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", url));
    }

    private void shareOnWhatsApp(PersonEntity savedPerson, List<ItemEntity> requiredItems) {
        final var url = this.shareService.shareOnWhatsApp(savedPerson, requiredItems);
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", url));
    }

    private PersonEntity mapToPerson(String firstName, String lastName, String phone,
                                     Double latitude, Double longitude, String sessionId) {
        PersonEntity personEntity = new PersonEntity();
        // Capitalize first letters of the name and surname
        personEntity.setFirstName(firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
        personEntity.setLastName(lastName.substring(0, 1).toUpperCase() + lastName.substring(1));
        personEntity.setPhone(phone.trim());
        personEntity.setLatitude(geoLocation.getValue().getLatitude());
        personEntity.setLongitude(geoLocation.getValue().getLongitude());
        personEntity.setSessionId(sessionId);
        return personEntity;
    }

    private RequirementEntity mapToRequirement(PersonEntity foundPerson, ItemEntity item, String sessionId) {
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

    private void onFindMeClick(GeoLocation geoLocation) {

        int clickCount = findMeClickCount.incrementAndGet();

        if(clickCount > 1){
            return;
        }

        final var map = new LMap(geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(), 8);
        map.setTileLayer(LTileLayer.DEFAULT_OPENSTREETMAP_TILE);
        map.setSizeFull();
        // TODO: add some logic here for called Markers (token)
        map.addMarkerClickListener(onMarkerClick -> System.out.println(onMarkerClick.getTag()));
        map.setCenter(new LCenter(geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude()));
        map.setViewPoint(new LCenter(geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(), 8));

        final var tagText = "Sorgu: " + geoLocation.getValue().getLatitude() + ", " + geoLocation.getValue().getLongitude() + " konumundaki kullanıcı bilgileri sorgulandı";
        final var markerMyCoordinates = new LMarker(geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude(), tagText);

        map.addMarkerClickListener(event -> {
            final var dialog = new Dialog();

            // Add a close button to the dialog
            final var closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), closeProfileView -> dialog.close());

            // Add the user profile layout to the dialog
            final var profileLayout = new ProfileLayout(
                    "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png",
                    "John Doe",
                    "+90 555 555 55 55",
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now())
            );

            // Add the button to the dialog
            dialog.add(closeButton);
            dialog.add(profileLayout);

            dialog.open();

            add(dialog);

        });

        map.addLComponents(markerMyCoordinates);

        add(map);

    }

}
