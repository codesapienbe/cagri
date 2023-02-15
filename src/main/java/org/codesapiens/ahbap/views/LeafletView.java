package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.codesapiens.ahbap.data.entity.TagEntity;
import org.codesapiens.ahbap.data.service.*;
import org.vaadin.elmot.flow.sensors.GeoLocation;
import software.xdev.vaadin.maps.leaflet.flow.LMap;
import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LComponent;
import software.xdev.vaadin.maps.leaflet.flow.data.LMarker;
import software.xdev.vaadin.maps.leaflet.flow.data.LTileLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Route("leaflet-maps-view")
public class LeafletView extends VerticalLayout {
    private boolean viewLunch = false;

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
    private final Button btnLunch = new Button("Ben neredeyim?");
    private LMap map;

    private LMarker markerZob;

    private final GeoLocation geoLocation;

    private TextField phoneField;
    private TextField firstNameField;
    private TextField lastNameField;
    private Button callHelpOnTwitterButton;
    private Button callHelpOnFacebookButton;
    private Button callHelpOnWhatsAppButton;
    private Button callHelpOnSmsButton;

    private final List<CheckboxGroup<ItemEntity>> itemsCheck = new ArrayList<>();

    private final String sessionId = VaadinSession.getCurrent().getSession().getId();

    private final NotificationService notification;

    private final Accordion accordion = new Accordion();

    public LeafletView(PersonService personService,
                       ItemService itemService, TagService tagService, RequirementService requirementService,
                       GeoLocation geoLocation, NotificationService notification) {
        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;
        this.geoLocation = geoLocation;
        this.notification = notification;
        add(this.geoLocation);


        this.setSizeFull();
        this.accordion.setWidthFull();


        this.btnLunch.addClickListener(this::btnLunchClick);
        final HorizontalLayout hlButtonContainer = new HorizontalLayout();
        hlButtonContainer.setWidthFull();
        hlButtonContainer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        hlButtonContainer.setPadding(false);
        hlButtonContainer.setSpacing(false);
        hlButtonContainer.add(
                this.btnLunch,
                new Button("Çağrı Yap", ev ->
                {
                    final Icon icoClose = VaadinIcon.CLOSE.create();

                    final Dialog dialog = new Dialog(icoClose);
                    dialog.setWidth("50vw");
                    dialog.setHeight("50vh");
                    dialog.open();



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

                    icoClose.addClickListener(iev -> dialog.close());
                }));

        this.add(this.map, hlButtonContainer);
        this.setSizeFull();
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


    private void btnLunchClick(final ClickEvent<Button> event) {
        this.viewLunch = !this.viewLunch;

        final List<LComponent> normalComponents = Collections.singletonList(this.markerZob);

        this.map.setViewPoint(new LCenter(
                geoLocation.getValue().getLatitude(),
                geoLocation.getValue().getLongitude(),
                this.viewLunch ? 16 : 17)
        );
        this.map.removeLComponents(normalComponents);
        this.map.addLComponents(normalComponents);

        this.btnLunch.setText(this.viewLunch ? "Beni Bul" : "Beni bul (yakınlaştır)");
    }

    private void initMapComponents() {
        this.markerZob = new LMarker(
                geoLocation.getValue().getLatitude(),
                geoLocation.getValue().getLongitude(),
                "Benim konumum"
        );
        this.markerZob.setPopup("Tren istasyonu");

        this.map = new LMap(
                geoLocation.getValue().getLatitude(),
                geoLocation.getValue().getLongitude(),
                17);
        this.map.setTileLayer(LTileLayer.DEFAULT_OPENSTREETMAP_TILE);

        this.map.setSizeFull();
        // add some logic here for called Markers (token)
        this.map.addMarkerClickListener(ev -> System.out.println(ev.getTag()));

        this.map.addLComponents(this.markerZob);
    }
}
