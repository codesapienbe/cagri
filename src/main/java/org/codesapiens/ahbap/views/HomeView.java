package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@PageTitle("Harita | Yardım Çağır ")
@Route("")
public class HomeView extends VerticalLayout {

    /**
     * BEANS
     */

    private final PersonService personService;
    private final ItemService itemService;

    private final TagService tagService;
    private final RequirementService requirementService;

    private final LMap map;

    private LMarker markerMyCoordinates;

    private final GeoLocation geoLocation;

    private final TextField phoneField = new TextField("Telefon");
    private final TextField firstNameField = new TextField("Ad");
    private final TextField lastNameField = new TextField("Soyad");

    private final List<MultiSelectListBox<ItemEntity>> itemsCheck = new ArrayList<>();

    private final String sessionId = VaadinSession.getCurrent().getSession().getId();

    private final NotificationService notification;

    /**
     * Constructor
     */
    public HomeView(PersonService personService, ItemService itemService, TagService tagService, RequirementService requirementService,
                    GeoLocation geoLocation, NotificationService notification) {

        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;
        this.geoLocation = geoLocation;
        this.notification = notification;

        initMainPageStyle();

        add(this.geoLocation);

        double defaultLatitude = 37.165867063341274;
        double defaultLongitude = 37.042596136246736;
        this.map = new LMap(defaultLatitude, defaultLongitude, 10);
        this.map.setTileLayer(LTileLayer.DEFAULT_OPENSTREETMAP_TILE);
        this.map.setSizeFull();
        // add some logic here for called Markers (token)
        this.map.addMarkerClickListener(onMarkerClick -> System.out.println(onMarkerClick.getTag()));

        this.setSizeFull();

        final var accordion = new Accordion();
        accordion.setWidthFull();

        final var footerButtonsLayout = new HorizontalLayout();
        initFooterLayoutStyle(footerButtonsLayout);

        final var btnFindMe = new Button("Ben neredeyim?");
        initFooterButtonStyle(btnFindMe, "left", "#4caf50");

        btnFindMe.addClickListener(this::onFindMeClick);

        final var btnCallHelp = new Button("Çağrı Yap");
        initFooterButtonStyle(btnCallHelp, "right", "#f44336");

        btnCallHelp.addClickListener(this::onCallHelpClick);
        footerButtonsLayout.add(btnFindMe, btnCallHelp);

        this.add(this.map, footerButtonsLayout);
        this.setSizeFull();
    }

    private static void initFooterButtonStyle(Button btnFindMe, String position, String background) {
        btnFindMe.getStyle()
                .set("background-color", background)
                .set("color", "#fff")
                .set("border", "none")
                .set("border-radius", "5px")
                .set(position, "0")
                .set("bottom", "0")
                .set("z-index", "1000");
    }

    private static void initFooterLayoutStyle(HorizontalLayout footerButtonsLayout) {
        footerButtonsLayout.getStyle()
                .set("position", "absolute")
                .set("bottom", "0")
                .set("width", "100%")
                .set("padding", "10px")
                .set("margin", "0")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("background-color", "transparent")
                .set("z-index", "1000");
    }

    private void initMainPageStyle() {
        this.getStyle()
                .set("background-color", "#f5f5f5")
                .set("padding", "0")
                .set("margin", "0")
                .set("spacing", "0")
                .set("border", "0")
                .set("font-family", "Roboto, sans-serif");
    }

    private void onCallHelpClick(final ClickEvent<Button> event) {
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

        formLayout.add(firstNameField, lastNameField);
        formLayout.add(phoneField, 2);


        final var itemsGroupedByCategory = this.itemService.list(PageRequest.of(0, 100)).stream()
                .collect(Collectors.groupingBy(ItemEntity::getCategory));

        itemsGroupedByCategory.forEach((category, items) -> {
            final var lBox = new MultiSelectListBox<>();
            lBox.setItems(items);

            formLayout.add(lBox, 2);
        });

        dialog.add(formLayout);

        final var buttonsLayout = new HorizontalLayout();
        buttonsLayout.getStyle()
                .set("bottom", "0")
                .set("width", "50%")
                .set("max-width", "75%")
                .set("padding", "10px")
                .set("margin", "0")
                .set("background-color", "transparent")
                .set("z-index", "1000");

        final var callHelpOnTwitterIcon = new Image("https://www.svgrepo.com/show/489937/twitter.svg", "WhatsApp");
        callHelpOnTwitterIcon.setWidth("50px");
        callHelpOnTwitterIcon.setHeight("50px");
        final var callHelpOnTwitterButton = new Button(callHelpOnTwitterIcon, callHelpOnTwitterEvent());
        styleDialogButton(callHelpOnTwitterButton);
        buttonsLayout.add(
                callHelpOnTwitterButton
        );

        final var callHelpOnFacebookIcon = new Image("https://www.svgrepo.com/show/452197/facebook.svg", "WhatsApp");
        callHelpOnFacebookIcon.setWidth("50px");
        callHelpOnFacebookIcon.setHeight("50px");
        final var callHelpOnFacebookButton = new Button(callHelpOnFacebookIcon, callHelpOnFacebookEvent());
        styleDialogButton(callHelpOnFacebookButton);
        // TODO: fix it later it does not work
        callHelpOnFacebookButton.setEnabled(false);
        buttonsLayout.add(
                callHelpOnFacebookButton
        );

        final var callHelpOnWhatsAppIcon = new Image("https://www.svgrepo.com/show/452133/whatsapp.svg", "WhatsApp");
        callHelpOnWhatsAppIcon.setWidth("50px");
        callHelpOnWhatsAppIcon.setHeight("50px");
        final var callHelpOnWhatsAppButton = new Button(callHelpOnWhatsAppIcon, callHelpOnWhatsAppEvent());
        styleDialogButton(callHelpOnWhatsAppButton);
        buttonsLayout.add(
                callHelpOnWhatsAppButton
        );

        final var callHelpOnSmsIcon = new Image("https://www.svgrepo.com/show/375147/sms.svg", "SMS");
        callHelpOnSmsIcon.setWidth("50px");
        callHelpOnSmsIcon.setHeight("50px");
        final var callHelpOnSmsButton = new Button(callHelpOnSmsIcon, callHelpOnSmsEvent());
        styleDialogButton(callHelpOnSmsButton);
        buttonsLayout.add(
                callHelpOnSmsButton
        );

        dialog.add(buttonsLayout);

        icoClose.addClickListener(iev -> dialog.close());
    }

    private static void styleDialogButton(Button callHelpOnTwitterButton) {
        callHelpOnTwitterButton.getStyle()
                .set("margin-top", "1em")
                .set("color", "white")
                .set("border", "none");
    }

    private ComponentEventListener<ClickEvent<Button>> callHelpOnSmsEvent() {
        return onClick -> {
            Optional<PersonEntity> foundPerson = personService.getByPhone(phoneField.getValue().trim());

            if (foundPerson.isPresent()) {
                notification.sendNotification("Daha önce çağrınız mevcut.");
            } else {
                PersonEntity personToSave = mapToPerson();
                PersonEntity savedPerson = personService.update(personToSave);

                for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
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

                for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
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

                for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
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

                for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
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

    private static void styleButtonLayout(HorizontalLayout buttonsLayout) {
        buttonsLayout.setPadding(false);
        buttonsLayout.setSpacing(false);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsLayout.setWidthFull();
    }

    private void onFindMeClick(final ClickEvent<Button> event) {

        var latitude = geoLocation.getValue() == null ? 0.0 : geoLocation.getValue().getLatitude();
        var longitude = geoLocation.getValue() == null ? 0.0 : geoLocation.getValue().getLongitude();
        this.map.setViewPoint(new LCenter(latitude, longitude, 8));

        if (this.markerMyCoordinates == null) {
            this.markerMyCoordinates = new LMarker(latitude, longitude, "Benim konumum");
        } else {
            this.markerMyCoordinates.setLat(latitude);
            this.markerMyCoordinates.setLon(longitude);
        }

        this.map.addMarkerClickListener(onFindMeClick -> {
            final var dialog = new Dialog();

            // Add a close button to the dialog
            final var closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), closeProfileView -> dialog.close());

            // Add the user profile layout to the dialog
            final var profileLayout = new UserProfileLayout(
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

        this.map.addLComponents(this.markerMyCoordinates);

    }

}
