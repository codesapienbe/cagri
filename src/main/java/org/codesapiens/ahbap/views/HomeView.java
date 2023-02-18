package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.entity.PersonEntity;
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

        // Loads the items from the database
        seedItems();

        btnRequirements.addClickListener(onClick -> onRequirementsClickEvent());
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
                e -> {
                    getUI().ifPresent(ui -> ui.getPage().executeJs(
                            "window.open($0, '_blank')",
                            "https://www.facebook.com/sharer/sharer.php?u=https://cagriapp.com&quote=ÇAĞRI: " + getRequirementsFromItemBoxes() + " için yardım çağırıyorum. Yardım edebilir misiniz?"
                    ));
                }
        );
        styleDialogButton(callHelpOnFacebookButton.getElement());
        callHelpOnFacebookButton.setEnabled(false);

        final var callHelpOnWhatsAppIcon = new Image("https://www.svgrepo.com/show/452133/whatsapp.svg", "WhatsApp");
        styleIcon(callHelpOnWhatsAppIcon);
        final var callHelpOnWhatsAppButton = new Button(callHelpOnWhatsAppIcon,
                e -> getUI().ifPresent(ui -> ui.getPage().executeJs(
                        "window.open($0, '_blank')",
                        "https://wa.me/?text=ÇAĞRI: " + getRequirementsFromItemBoxes() + " için yardım çağırıyorum. Yardım edebilir misiniz? https://cagriapp.com"
                ))
        );
        styleDialogButton((callHelpOnWhatsAppButton.getElement()));

        final var callHelpOnSmsIcon = new Image("https://www.svgrepo.com/show/375147/sms.svg", "SMS");
        styleIcon(callHelpOnSmsIcon);
        final var callHelpOnSmsButton = new Button(callHelpOnSmsIcon,
                e -> getUI().ifPresent(ui -> ui.getPage().executeJs(
                        "window.open($0, '_blank')",
                        "sms:?body=ÇAĞRI: " + getRequirementsFromItemBoxes() + " için yardım çağırıyorum. Yardım edebilir misiniz? https://cagriapp.com"
                ))
        );
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

    private void seedItems() {

        final var itemsGroupedByCategory = this.itemService.list(PageRequest.of(0, 100)).stream()
                .collect(Collectors.groupingBy(ItemEntity::getCategory));

        List<ItemEntity> shelterGroup = itemsGroupedByCategory.getOrDefault("Barınma", Collections.emptyList());
        this.shelter.setItems(shelterGroup);
        StyleUtils.styleMultiSelectComboBox(this.shelter, "Barınma");

        List<ItemEntity> nutritionGroup = itemsGroupedByCategory.getOrDefault("Gıda", Collections.emptyList());
        this.nutrition.setItems(nutritionGroup);
        StyleUtils.styleMultiSelectComboBox(this.nutrition, "Gıda");

        List<ItemEntity> clothGroup = itemsGroupedByCategory.getOrDefault("Kıyafet", Collections.emptyList());
        this.clothes.setItems(clothGroup);
        StyleUtils.styleMultiSelectComboBox(this.clothes, "Kıyafet");

        List<ItemEntity> babyGroup = itemsGroupedByCategory.getOrDefault("Bebek", Collections.emptyList());
        this.baby.setItems(babyGroup);
        StyleUtils.styleMultiSelectComboBox(this.baby, "Bebek");

        List<ItemEntity> disabledGroup = itemsGroupedByCategory.getOrDefault("Engelli", Collections.emptyList());
        this.disabled.setItems(disabledGroup);
        StyleUtils.styleMultiSelectComboBox(this.disabled, "Engelli");

        List<ItemEntity> elderlyGroup = itemsGroupedByCategory.getOrDefault("Yaşlı", Collections.emptyList());
        this.elderly.setItems(elderlyGroup);
        StyleUtils.styleMultiSelectComboBox(this.elderly, "Yaşlı");

        List<ItemEntity> petGroup = itemsGroupedByCategory.getOrDefault("Evcil Hayvan", Collections.emptyList());
        this.pet.setItems(petGroup);
        StyleUtils.styleMultiSelectComboBox(this.pet, "Evcil Hayvan");

        List<ItemEntity> hygieneGroup = itemsGroupedByCategory.getOrDefault("Hijyen", Collections.emptyList());
        this.hygiene.setItems(hygieneGroup);
        StyleUtils.styleMultiSelectComboBox(this.hygiene, "Hijyen");

        List<ItemEntity> medicalGroup = itemsGroupedByCategory.getOrDefault("Medikal", Collections.emptyList());
        this.medicine.setItems(medicalGroup);
        StyleUtils.styleMultiSelectComboBox(this.medicine, "Medikal");

        List<ItemEntity> otherGroup = itemsGroupedByCategory.getOrDefault("Diğer", Collections.emptyList());
        this.other.setItems(otherGroup);
        StyleUtils.styleMultiSelectComboBox(this.other, "Diğer");
    }

    private void styleItemBox() {
        this.shelter.setLabel("Barınma");
        this.shelter.setPlaceholder("Barınma");
        this.shelter.setItemLabelGenerator(ItemEntity::getTitle);
        this.shelter.setClearButtonVisible(true);
        this.shelter.setAllowCustomValue(true);
        this.shelter.setRequired(false);
        this.shelter.setRequiredIndicatorVisible(false);
        this.shelter.setMinWidth("100%");
        this.shelter.setWidth("100%");
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

    private void onRequirementsClickEvent() {

        final var icoClose = VaadinIcon.CLOSE.create();
        icoClose.setColor("red");
        icoClose.setSize("20px");
        icoClose.getStyle()
                .set("cursor", "pointer")
                .set("float", "right")
                .set("margin-top", "10px")
                .set("margin-right", "10px");

        final var dialog = new Dialog(icoClose);
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setDraggable(true);

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

        dialog.open();

        add(
                dialog
        );

        icoClose.addClickListener(iev -> dialog.close());
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
