package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.codesapiens.ahbap.data.entity.TagEntity;
import org.codesapiens.ahbap.data.service.*;
import org.vaadin.elmot.flow.sensors.GeoLocation;
import org.vaadin.elmot.flow.sensors.Position;
import software.xdev.vaadin.maps.leaflet.flow.LMap;
import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LMarker;
import software.xdev.vaadin.maps.leaflet.flow.data.LTileLayer;

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

    /**
     * UI-Components
     */
    private final Button btnFindMe = new Button("Ben neredeyim?");
    private final Button btnCallHelp = new Button("Çağrı Yap");
    private LMap map;

    private LMarker markerMyCoordinates;

    private final GeoLocation geoLocation;

    private final TextField phoneField = new TextField("Telefon");
    private final TextField firstNameField = new TextField("Ad");
    private final TextField lastNameField = new TextField("Soyad");

    private final List<CheckboxGroup<ItemEntity>> itemsCheck = new ArrayList<>();

    private final String sessionId = VaadinSession.getCurrent().getSession().getId();

    private final NotificationService notification;

    private final Accordion accordion = new Accordion();

    /**
     * Default settings for the state of the map
     */

    private Double defaultLatitude = 36.937149;
    private Double defaultLongitude = 37.585831;

    /**
     * Constructor
     */
    public HomeView(PersonService personService,
                    ItemService itemService, TagService tagService, RequirementService requirementService,
                    GeoLocation geoLocation, NotificationService notification) {

        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;
        this.geoLocation = geoLocation;
        this.notification = notification;

        add(this.geoLocation);

        Position pos = geoLocation.getValue();
        if (pos == null) {
            pos = new Position();
            pos.setLatitude(defaultLatitude);
            pos.setLongitude(defaultLongitude);
        } else {
            pos = new Position();
            pos.setLatitude(geoLocation.getValue().getLatitude());
            pos.setLongitude(geoLocation.getValue().getLongitude());
        }

        Double latitude = pos.getLatitude();
        Double longitude = pos.getLongitude();

        this.markerMyCoordinates = new LMarker(
                latitude != null ? latitude : defaultLatitude,
                longitude != null ? longitude : defaultLongitude,
                "Benim konumum"
        );

        this.markerMyCoordinates.setPopup("<strong>En yüksek şiddetteki depremin olduğu konum.</strong>");

        this.map = new LMap(
                latitude != null ? latitude : defaultLatitude,
                longitude != null ? longitude : defaultLongitude,
                6
        );
        this.map.setTileLayer(LTileLayer.DEFAULT_OPENSTREETMAP_TILE);

        this.map.setSizeFull();
        // add some logic here for called Markers (token)
        this.map.addMarkerClickListener(onMarkerClick -> System.out.println(onMarkerClick.getTag()));

        this.map.addLComponents(this.markerMyCoordinates);

        this.setSizeFull();
        this.accordion.setWidthFull();

        this.btnFindMe.addClickListener(this::onFindMeClickEvent);

        final var footerButtonsLayout = new HorizontalLayout();
        footerButtonsLayout.setWidthFull();
        footerButtonsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footerButtonsLayout.setPadding(false);
        footerButtonsLayout.setSpacing(false);

        this.btnCallHelp.addClickListener(this::onCallHelpClick);
        footerButtonsLayout.add(this.btnFindMe, this.btnCallHelp);

        this.add(this.map, footerButtonsLayout);
        this.setSizeFull();
    }

    private void onCallHelpClick(final ClickEvent<Button> event) {
        final var icoClose = VaadinIcon.CLOSE.create();

        final var dialog = new Dialog(icoClose);
        dialog.setWidth(95, Unit.PERCENTAGE);
        dialog.setHeight(95, Unit.PERCENTAGE);
        dialog.open();

        final var formLayout = createFormLayout();

        formLayout.add(firstNameField, lastNameField);
        formLayout.add(phoneField, 2);

        dialog.add(formLayout);

        final var buttonsLayout = new HorizontalLayout();
        styleButtonLayout(buttonsLayout);

        final var callHelpOnTwitterButton = new Button("Twitter", callHelpOnTwitterEvent());
        String colorCodeForTwitter = "#1DA1F2";
        styleDialogButton(callHelpOnTwitterButton, colorCodeForTwitter);
        buttonsLayout.add(
                callHelpOnTwitterButton
        );

        final var callHelpOnFacebookButton = new Button("Facebook", callHelpOnFacebookEvent());
        // TODO: fix it later it does not work
        String colorCodeForFacebook = "#3b5998";
        styleDialogButton(callHelpOnFacebookButton, colorCodeForFacebook);
        buttonsLayout.add(
                callHelpOnFacebookButton
        );

        final var callHelpOnWhatsAppButton = new Button("WhatsApp", callHelpOnWhatsAppEvent());
        String colorCodeForWhatsApp = "#25D366";
        styleDialogButton(callHelpOnWhatsAppButton, colorCodeForWhatsApp);
        buttonsLayout.add(
                callHelpOnWhatsAppButton
        );

        final var callHelpOnSmsButton = new Button("SMS", callHelpOnSmsEvent());
        // Soft orange for SMS button
        String colorCodeForSms = "#FFA500";
        styleDialogButton(callHelpOnSmsButton, colorCodeForSms);
        buttonsLayout.add(
                callHelpOnSmsButton
        );

        dialog.add(buttonsLayout);

        icoClose.addClickListener(iev -> dialog.close());
    }

    private static void styleDialogButton(Button callHelpOnTwitterButton, String colorCode) {
        callHelpOnTwitterButton.getStyle()
                .set("background-color", colorCode)
                .set("margin-top", "5px")
                .set("color", "white")
                .set("border-radius", "5px")
                .set("border", "none");
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

    private static void styleButtonLayout(HorizontalLayout buttonsLayout) {
        buttonsLayout.setPadding(false);
        buttonsLayout.setSpacing(false);
        buttonsLayout.setAlignItems(Alignment.CENTER);
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonsLayout.setWidthFull();
    }

    private void onFindMeClickEvent(final ClickEvent<Button> event) {
        Double latitude = geoLocation.getValue().getLatitude();
        Double longitude = geoLocation.getValue().getLongitude();
        this.map.setViewPoint(new LCenter(latitude, longitude, 16));

        this.markerMyCoordinates = new LMarker(latitude, longitude, "Benim konumum");
        this.map.addMarkerClickListener(onFindMeClick -> {
            Dialog dialog = new Dialog();

            // Add a profile layout to the dialog which has a close button, a title and a description
            VerticalLayout profileLayout = new VerticalLayout();

            // Add a close button to the dialog
            Button closeButton = new Button("Kapat", closeProfileView -> dialog.close());
            profileLayout.add(closeButton);

            // Add a title to the dialog
            profileLayout.add(new Text("Konumunuz: " + latitude + ":" + longitude));

            // Create an avatar image view with a default image
            Image avatar = new Image("https://i.imgur.com/9YcVw9p.jpg", "Avatar");
            avatar.setWidth("100px");
            avatar.setHeight("100px");
            profileLayout.add(avatar);

            // Add a description to the dialog
            profileLayout.add(new Text("Bu konumunuzun doğruluğunu doğrulamak için lütfen bir fotoğraf yükleyiniz."));

            if(avatar.getSrc().equals("https://i.imgur.com/9YcVw9p.jpg")){
                avatar.setVisible(false);
            }

            // Add a file upload to the dialog
            Upload upload = new Upload();
            upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
            upload.setMaxFileSize(1000000);
            upload.setDropLabel(new Label("Fotoğrafınızı buraya sürükleyin"));
            Button uploadButton = new Button("Fotoğraf yükle");

            upload.addSucceededListener(onUploadSucceeded -> {
                avatar.setSrc(onUploadSucceeded.getFileName());
                avatar.setVisible(true);
            });

            upload.setUploadButton(uploadButton);

            profileLayout.add(upload);

            dialog.add(profileLayout);

            dialog.open();

            add(dialog);

        });

        this.map.addLComponents(markerMyCoordinates);
    }

}
