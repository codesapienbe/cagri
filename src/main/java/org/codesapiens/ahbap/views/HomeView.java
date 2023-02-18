package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.codesapiens.ahbap.data.service.StyleUtils.*;


@PageTitle("Harita | Yardım Çağır ")
@Route("")
public class HomeView extends VerticalLayout {

    private final PersonService personService;
    private final ItemService itemService;
    private final TagService tagService;
    private final RequirementService requirementService;
    private final ShareService shareService;
    private final NotificationService notification;

    /**
     * ItemCheckBoxes are used to create a list of requirements
     */

    private final MultiSelectComboBox<ItemEntity> shelter = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> medicine = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> nutrition = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> clothes = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> baby = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> elderly = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> disabled = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> pet = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> hygiene = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<ItemEntity> other = new MultiSelectComboBox<>();

    private final HorizontalLayout footerButtonsLayout = new HorizontalLayout();

    private final HorizontalLayout headerLayout = new HorizontalLayout();


    private PersonEntity currentPerson;

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

        pageBackground(getElement());

        geoLocation.setWatch(true);
        geoLocation.setHighAccuracy(true);
        geoLocation.setTimeout(100000);
        geoLocation.setMaxAge(200000);
        add(geoLocation);

        footerLayout(footerButtonsLayout.getElement());

        final var btnFindMe = new Button("Ben neredeyim?");
        footerButton(btnFindMe, "left", "#4caf50");
        btnFindMe.addClickListener(onClick -> onFindMeClick());

        final var phoneField = new TextField();
        phoneField.addValueChangeListener(e -> {
            this.currentPerson.setPhone(e.getValue());
        });
        styleTextField(phoneField);

        final var btnRequirements = new Button("İhtiyaçlar");
        // background Turquoise
        footerButton(btnRequirements, "right", "#00bcd4");

        btnRequirements.addClickListener(onClick -> onCallHelpClick(this.geoLocation));
        footerButtonsLayout.add(btnFindMe, phoneField, btnRequirements);

        headerLayout(headerLayout.getElement());

        final var callHelpOnTwitterIcon = new Image("https://www.svgrepo.com/show/489937/twitter.svg", "WhatsApp");
        styleIcon(callHelpOnTwitterIcon);
        final var callHelpOnTwitterButton = new Button(callHelpOnTwitterIcon, e -> getUI().ifPresent(ui -> ui.getPage().executeJs(
                "window.open($0, '_blank')",
                "https://twitter.com/intent/tweet?text=ÇAĞRI: " + getRequirementsFromItemBoxes() + " için yardım çağırıyorum. Yardım edebilir misiniz? https://ahbap.org"
        )));
        styleDialogButton(callHelpOnTwitterButton.getElement());

        final var callHelpOnFacebookIcon = new Image("https://www.svgrepo.com/show/452197/facebook.svg", "WhatsApp");
        styleIcon(callHelpOnFacebookIcon);
        final var callHelpOnFacebookButton = new Button(callHelpOnFacebookIcon,
                onClick -> callHelpOnFacebookEvent(
                        this.getChildren()
                                .filter(component -> component instanceof MultiSelectListBox)
                                .map(component -> (MultiSelectListBox<ItemEntity>) component)
                                .collect(Collectors.toList())
                )
        );
        styleDialogButton(callHelpOnFacebookButton.getElement());
        // TODO: fix it later it does not work
        callHelpOnFacebookButton.setEnabled(false);

        final var callHelpOnWhatsAppIcon = new Image("https://www.svgrepo.com/show/452133/whatsapp.svg", "WhatsApp");
        styleIcon(callHelpOnWhatsAppIcon);
        final var callHelpOnWhatsAppButton = new Button(callHelpOnWhatsAppIcon, onClick -> callHelpOnWhatsAppEvent(
                this.getChildren()
                        .filter(component -> component instanceof MultiSelectListBox)
                        .map(component -> (MultiSelectListBox<ItemEntity>) component)
                        .collect(Collectors.toList())
        ));
        styleDialogButton((callHelpOnWhatsAppButton.getElement()));

        final var callHelpOnSmsIcon = new Image("https://www.svgrepo.com/show/375147/sms.svg", "SMS");
        styleIcon(callHelpOnSmsIcon);
        final var callHelpOnSmsButton = new Button(callHelpOnSmsIcon, onClick -> callHelpOnSmsEvent(
                this.getChildren()
                        .filter(component -> component instanceof MultiSelectListBox)
                        .map(component -> (MultiSelectListBox<ItemEntity>) component)
                        .collect(Collectors.toList())
        ));
        styleDialogButton(callHelpOnSmsButton.getElement());

        headerLayout.add(
                callHelpOnTwitterButton,
                callHelpOnFacebookButton,
                callHelpOnWhatsAppButton,
                callHelpOnSmsButton
        );

        add(
                headerLayout,
                footerButtonsLayout
        );

        setSizeFull();
    }

    private String getRequirementsFromItemBoxes() {
        final var shelterText = this.shelter.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var nutritionText = this.nutrition.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var clothesText = this.clothes.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var babyText = this.baby.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var disabledText = this.disabled.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var elderlyText = this.elderly.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var petText = this.pet.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var hygieneText = this.hygiene.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var medicineText = this.medicine.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        final var otherText = this.other.getValue().stream().map(ItemEntity::getTitle).collect(Collectors.joining(" #"));
        return shelterText + nutritionText + " " + clothesText + " " + babyText + " " + disabledText + " " + elderlyText + " " + petText + " " + hygieneText + " " + medicineText + " " + otherText;
    }

    private void onCallHelpClick(GeoLocation geoLocation) {
        final var icoClose = VaadinIcon.CLOSE.create();

        final var dialog = new Dialog(icoClose);
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setDraggable(true);

        dialog.open();

        final var itemsGroupedByCategory = this.itemService.list(PageRequest.of(0, 100)).stream()
                .collect(Collectors.groupingBy(ItemEntity::getCategory));

        this.shelter.setValue(itemsGroupedByCategory.getOrDefault("Barınma", Collections.emptyList()));
        this.nutrition.setValue(itemsGroupedByCategory.getOrDefault("Gıda", Collections.emptyList()));
        this.clothes.setValue(itemsGroupedByCategory.getOrDefault("Kıyafet", Collections.emptyList()));
        this.baby.setValue(itemsGroupedByCategory.getOrDefault("Bebek", Collections.emptyList()));
        this.disabled.setValue(itemsGroupedByCategory.getOrDefault("Engelli", Collections.emptyList()));
        this.elderly.setValue(itemsGroupedByCategory.getOrDefault("Yaşlı", Collections.emptyList()));
        this.pet.setValue(itemsGroupedByCategory.getOrDefault("Evcil Hayvan", Collections.emptyList()));
        this.hygiene.setValue(itemsGroupedByCategory.getOrDefault("Hijyen", Collections.emptyList()));
        this.medicine.setValue(itemsGroupedByCategory.getOrDefault("Medikal", Collections.emptyList()));
        this.other.setValue(itemsGroupedByCategory.getOrDefault("Diğer", Collections.emptyList()));

        dialog.add(
                this.shelter,
                this.nutrition,
                this.clothes,
                this.baby,
                this.disabled,
                this.elderly,
                this.pet,
                this.hygiene,
                this.medicine,
                this.other
        );

        add(
                dialog
        );

        icoClose.addClickListener(iev -> dialog.close());
    }

    private void callHelpOnSmsEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck
    ) {

        for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
            for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                RequirementEntity requirementToSave = mapToRequirement(currentPerson, item, VaadinSession.getCurrent().getSession().getId());
                requirementService.update(requirementToSave);
            }
        }

        notification.sendNotification("SMS çağrınız gönderildi.");

        shareOnSMS(
                itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
        );

    }

    private void shareOnSMS(List<ItemEntity> requiredItems) {
        final var sms = shareService.getShareUrl(currentPerson, requiredItems);
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", sms));
    }

    private void callHelpOnWhatsAppEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck
    ) {
        Optional<PersonEntity> foundPerson = personService.getByPhone(this.currentPerson.getPhone().trim());

        if (foundPerson.isPresent()) {
            notification.sendNotification("Daha önce çağrınız mevcut.");
        } else {

            for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
                for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                    RequirementEntity requirementToSave = mapToRequirement(
                            currentPerson,
                            item,
                            VaadinSession.getCurrent().getSession().getId()
                    );
                    requirementService.update(requirementToSave);
                }
            }

            notification.sendNotification("Whatsapp çağrınız alındı.");

            shareOnWhatsApp(
                    itemsCheck.stream().flatMap(cg -> cg.getSelectedItems().stream()).collect(Collectors.toList())
            );
        }

    }

    private void callHelpOnFacebookEvent(
            List<MultiSelectListBox<ItemEntity>> itemsCheck
    ) {

        for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
            for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                RequirementEntity requirementToSave = mapToRequirement(
                        currentPerson,
                        item,
                        VaadinSession.getCurrent().getSession().getId()
                );
                requirementService.update(requirementToSave);
            }
        }

        notification.sendNotification("Facebook çağrınız paylaşıldı.");

        shareOnFacebook(
                itemsCheck
                        .stream()
                        .flatMap(cg -> cg.getSelectedItems().stream())
                        .collect(Collectors.toList())
        );
    }

    private void shareOnFacebook(List<ItemEntity> requiredItems) {
        // set content of the post for Facebook
        Double[] from = {
                currentPerson.getLatitude(),
                currentPerson.getLongitude()
        };

        Double[] to = {
                currentPerson.getLatitude(),
                currentPerson.getLongitude()
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
            List<MultiSelectListBox<ItemEntity>> itemsCheck
    ) {
        Optional<PersonEntity> foundPerson = personService.getByPhone(this.currentPerson.getPhone().trim());

        for (MultiSelectListBox<ItemEntity> itemEntityCheckboxGroup : itemsCheck) {
            for (ItemEntity item : itemEntityCheckboxGroup.getSelectedItems()) {
                RequirementEntity requirementToSave = mapToRequirement(this.currentPerson, item, VaadinSession.getCurrent().getSession().getId());
                requirementService.update(requirementToSave);
            }
        }

        notification.sendNotification("Çağrınız alındı. Lütfen çağrı kanalı seçiniz.");

        shareOnTwitter(
                itemsCheck
                        .stream()
                        .flatMap(cg -> cg.getSelectedItems().stream())
                        .collect(Collectors.toList())
        );

    }

    private void shareOnTwitter(List<ItemEntity> requiredItems) {
        List<TagEntity> annotations = tagService.listBySymbol('@');
        List<TagEntity> hashtags = tagService.listBySymbol('#');
        final var url = this.shareService.shareOnTwitter(currentPerson, requiredItems, annotations, hashtags);
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", url));
    }

    private void shareOnWhatsApp(List<ItemEntity> requiredItems) {
        final var url = this.shareService.shareOnWhatsApp(currentPerson, requiredItems);
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", url));
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

    private void onFindMeClick() {

        int clickCount = findMeClickCount.incrementAndGet();

        final var map = new LMap(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude(), 5);
        map.setTileLayer(LTileLayer.DEFAULT_OPENSTREETMAP_TILE);
        map.setSizeFull();

        if (clickCount == 1) {

            // TODO: add some logic here for called Markers (token)
            map.addMarkerClickListener(onMarkerClick -> System.out.println(onMarkerClick.getTag()));
            map.setCenter(new LCenter(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude()));
            map.setViewPoint(new LCenter(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude(), 8));

            final var tagText = "Sorgu: " + this.geoLocation.getValue().getLatitude() + ", " + this.geoLocation.getValue().getLongitude() + " konumundaki kullanıcı bilgileri sorgulandı";
            final var markerMyCoordinates = new LMarker(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude(), tagText);

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

        } else {
            map.setCenter(new LCenter(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude()));
            map.setViewPoint(new LCenter(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude(), 8));
        }

    }

}
