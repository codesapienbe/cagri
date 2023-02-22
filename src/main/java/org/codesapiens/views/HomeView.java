package org.codesapiens.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import org.codesapiens.data.dto.PersonDto;
import org.codesapiens.data.dto.RequirementDto;
import org.codesapiens.data.entity.ItemEntity;
import org.codesapiens.data.entity.MessageEntity;
import org.codesapiens.data.entity.PersonEntity;
import org.codesapiens.data.entity.RequirementEntity;
import org.codesapiens.data.service.*;
import org.springframework.data.domain.PageRequest;
import org.vaadin.elmot.flow.sensors.GeoLocation;
import software.xdev.vaadin.maps.leaflet.flow.LMap;
import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LMarker;
import software.xdev.vaadin.maps.leaflet.flow.data.LTileLayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.codesapiens.data.service.StyleUtils.*;

@PageTitle("YARDIM CAGRISI!")
@Route("")
public class HomeView extends VerticalLayout {

    private static final String BASE_URL = "https://cagriapp.com/";

    private final PersonService personService;
    private final ItemService itemService;
    private final TagService tagService;
    private final RequirementService requirementService;

    private final ShareService shareService;

    private final MessageService messageService;

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

    private final LMap map = new LMap();

    private PersonEntity currentPerson;

    private final GeoLocation geoLocation = new GeoLocation();

    // Create a click counter for findMeButton
    private final AtomicInteger findMeClickCount = new AtomicInteger(0);

    public HomeView(PersonService personService, ItemService itemService,
            TagService tagService, RequirementService requirementService, ShareService shareService,
            MessageService messageService) {

        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;
        this.shareService = shareService;
        this.messageService = messageService;

        pageBackground(getElement());

        geoLocation.setWatch(true);
        geoLocation.setHighAccuracy(true);
        geoLocation.setTimeout(100000);
        geoLocation.setMaxAge(200000);
        add(geoLocation);

        map.setCenter(new LCenter(37.585, 36.937));
        map.setZoom(6);
        map.setTileLayer(LTileLayer.DEFAULT_OPENSTREETMAP_TILE);
        map.setSizeFull();
        map.addMarkerClickListener(onMarkerClick -> System.out.println(onMarkerClick.getTag()));

        add(map);

        final var footerButtonsLayout = new HorizontalLayout();
        footerLayout(footerButtonsLayout.getElement());

        final var btnFindMe = new Button("Beni bul!");
        footerButton(btnFindMe, "left", "#4caf50");
        btnFindMe.addClickListener(onClick -> getCurrentLocation());

        final var phoneField = new TextField();
        phoneField.addValueChangeListener(e -> {
            this.currentPerson.setPhone(e.getValue());
        });
        styleTextField(phoneField);

        final var btnRequirements = new Button("Ihtiyaclarim");
        // background Turquoise
        footerButton(btnRequirements, "right", "#00bcd4");

        // Loads the items from the database
        seedItems();

        btnRequirements.addClickListener(onClick -> onRequirementsClickEvent());
        footerButtonsLayout.add(btnFindMe, phoneField, btnRequirements);

        final var headerLayout = new HorizontalLayout();
        headerLayout(headerLayout.getElement());

        final var callHelpOnTwitterIcon = new Image("https://www.svgrepo.com/show/489937/twitter.svg", "WhatsApp");
        styleIcon(callHelpOnTwitterIcon);
        final var callHelpOnTwitterButton = new Button(callHelpOnTwitterIcon, e -> {
            final var hashtags = "&hashtags=" + getRequirements();
            final var query = "https://twitter.com/intent/tweet?text=Benim ihtiyaclarim: " + hashtags
                    + "&url=https://cagriapp.com";

            final var message = new MessageEntity(
                    this.currentPerson,
                    "twitter",
                    query);
            this.messageService.update(message);

            getUI().ifPresent(ui -> ui.getPage().executeJs(
                    "window.open($0, '_blank')", query));
        });
        styleDialogButton(callHelpOnTwitterButton.getElement());

        final var callHelpOnFacebookIcon = new Image("https://www.svgrepo.com/show/452197/facebook.svg", "WhatsApp");
        styleIcon(callHelpOnFacebookIcon);
        final var callHelpOnFacebookButton = new Button(callHelpOnFacebookIcon,
                e -> {
                    final var hashtags = "&hashtag" + getRequirements();
                    final var query = "https://www.facebook.com/sharer/sharer.php?u=https://cagriapp.com" + hashtags;
                    final var message = new MessageEntity(
                            this.currentPerson,
                            "facebook",
                            query);
                    this.messageService.update(message);

                    getUI().ifPresent(ui -> ui.getPage().executeJs(
                            "window.open($0, '_blank')", query));
                });
        styleDialogButton(callHelpOnFacebookButton.getElement());
        callHelpOnFacebookButton.setEnabled(false);

        final var callHelpOnWhatsAppIcon = new Image("https://www.svgrepo.com/show/452133/whatsapp.svg", "WhatsApp");
        styleIcon(callHelpOnWhatsAppIcon);
        final var callHelpOnWhatsAppButton = new Button(callHelpOnWhatsAppIcon,
                e -> {
                    final var hashtags = "Yardiminiza ihtiyacim var." + getRequirements();
                    final var query = "https://wa.me?text=CAGRI !!! " + hashtags + " https://cagriapp.com";
                    final var message = new MessageEntity(
                            this.currentPerson,
                            "whatsapp",
                            query);
                    this.messageService.update(message);

                    getUI().ifPresent(ui -> ui.getPage().executeJs(
                            "window.open($0, '_blank')", query));
                });
        styleDialogButton((callHelpOnWhatsAppButton.getElement()));

        final var callHelpOnSmsIcon = new Image("https://www.svgrepo.com/show/375147/sms.svg", "SMS");
        styleIcon(callHelpOnSmsIcon);
        final var callHelpOnSmsButton = new Button(callHelpOnSmsIcon,
                e -> {
                    final var hashtags = "&hashtags" + getRequirements();
                    final var query = "sms:?body=CAGRI !!! " + hashtags + " https://cagriapp.com";
                    final var message = new MessageEntity(
                            this.currentPerson,
                            "sms",
                            query);
                    this.messageService.update(message);

                    getUI().ifPresent(ui -> ui.getPage().executeJs(
                            "window.open($0, '_blank')", query));
                });
        styleDialogButton(callHelpOnSmsButton.getElement());

        headerLayout.add(
                callHelpOnTwitterButton,
                callHelpOnFacebookButton,
                callHelpOnWhatsAppButton,
                callHelpOnSmsButton);

        add(
                headerLayout,
                footerButtonsLayout);

        setSizeFull();

        /*
         * Create a dialog to ask for the phone number of the person
         * who is calling for help. If the person is already registered,
         * the phone number is already known. Otherwise, the person is
         * asked to enter the phone number. The phone number is used
         * to contact the person who is calling for help.
         *
         * When the person is registered, the phone number is saved
         * in the VaadinSession.
         *
         * When the person is not registered, the phone number is saved
         * in the currentPerson object.
         */

        final var initialDialog = new Dialog();
        styleInitialDialog(initialDialog);

        final var initialDialogLayout = new VerticalLayout();
        styleInitialDialogLayout(initialDialogLayout);

        initialDialog.add(
                initialDialogLayout);

        final var initialDialogHeader = new HorizontalLayout();
        styleInitialDialogHeader(initialDialogHeader);

        final var initialDialogBody = new HorizontalLayout();
        styleInitialDialogBody(initialDialogBody);

        final var initialDialogFooter = new HorizontalLayout();
        styleInitialDialogFooter(initialDialogFooter);

        initialDialogLayout.add(
                initialDialogHeader,
                initialDialogBody,
                initialDialogFooter);

        final var initialDialogHeaderLabel = new Label("Yardim Cagir");
        styleInitialDialogHeaderLabel(initialDialogHeaderLabel);
        initialDialogHeader.add(initialDialogHeaderLabel);

        final var initialDialogBodyLabel = new Label("Telefon numaranizi giriniz");
        styleInitialDialogBodyLabel(initialDialogBodyLabel);
        initialDialogBody.add(initialDialogBodyLabel);

        final var initialDialogFooterButton = new Button("Kaydet");
        styleInitialDialogFooterButton(initialDialogFooterButton);
        initialDialogFooter.add(initialDialogFooterButton);

        final var initialDialogPhoneField = new TextField();
        styleInitialDialogPhoneField(initialDialogPhoneField);
        initialDialogBody.add(initialDialogPhoneField);

        initialDialogFooterButton.addClickListener(onClickEvent -> {

            initialDialogPhoneField.setErrorMessage("");
            initialDialogPhoneField.setRequired(true);
            initialDialogPhoneField.setRequiredIndicatorVisible(true);
            if (initialDialogPhoneField.getValue().length() == 11
                    || initialDialogPhoneField.getValue().length() == 13) {

                if (initialDialogPhoneField.getValue().length() == 11) {
                    initialDialogPhoneField.setValue("+90" + initialDialogPhoneField.getValue());
                }

                final var foundPerson = personService.getByPhone(initialDialogPhoneField.getValue());

                if (foundPerson.isPresent()) {
                    this.currentPerson = foundPerson.get();
                    this.currentPerson.setLatitude(geoLocation.getValue().getLatitude());
                    this.currentPerson.setLongitude(geoLocation.getValue().getLongitude());
                    this.currentPerson.setSessionId(VaadinSession.getCurrent().getSession().getId());
                    this.currentPerson = personService.update(this.currentPerson);

                    // Add the person to the Vaadin session
                    VaadinSession.getCurrent().setAttribute("person", this.currentPerson);

                    phoneField.setValue(this.currentPerson.getPhone());

                } else {
                    this.currentPerson = new PersonEntity();
                    this.currentPerson.setPhone(initialDialogPhoneField.getValue());
                    this.currentPerson.setFirstName("Isim");
                    this.currentPerson.setLastName("Soyisim");
                    this.currentPerson.setLatitude(geoLocation.getValue().getLatitude());
                    this.currentPerson.setLongitude(geoLocation.getValue().getLongitude());
                    // set an emergency avatar image
                    this.currentPerson.setImageUrl("https://i.imgur.com/1J8wv1M.png");
                    this.currentPerson.setRegisteredAt(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                    this.currentPerson.setSessionId(VaadinSession.getCurrent().getSession().getId());
                    // save the person to the database
                    this.currentPerson = personService.update(this.currentPerson);

                    // Add the person to the Vaadin session
                    VaadinSession.getCurrent().setAttribute("person", this.currentPerson);

                    phoneField.setValue(this.currentPerson.getPhone());
                }

                initialDialog.close();
            } else {
                initialDialogPhoneField.setErrorMessage("Lutfen gecerli bir telefon numarasi giriniz");
            }

            getAllPeopleLocation();

        });

        add(initialDialog);
        initialDialog.open();

    }

    private void bindMapData() {
        final var reqs = requirementService.getRequirementsGroupedByPerson();
        for (PersonDto person : reqs.keySet()) {

            final var tagText = "Sorgu: " + person.getLatitude() + ", " + person.getLongitude()
                    + " konumundaki kullanici bilgileri sorgulandi.";
            final var markerMyCoordinates = new LMarker(person.getLatitude(), person.getLongitude(), tagText);

            map.addMarkerClickListener(event -> {

                final var dialog = new Dialog();
                final var closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), closeProfileView -> {
                    dialog.close();
                });

                final var reqsAsString = reqs.get(person).stream().map(req -> req.getItem().toString())
                        .reduce((s1, s2) -> s1 + ", " + s2).orElse("");

                // Add the user profile layout to the dialog
                final var profileLayout = new ProfileLayout(
                        "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png",
                        person.getFirstName() + " " + person.getLastName(),
                        person.getPhone(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now()),
                        reqsAsString);

                dialog.add(
                        closeButton,
                        profileLayout);

                dialog.open();
                add(dialog);
            });

            map.addLComponents(markerMyCoordinates);
        }
    }

    private void seedItems() {

        final var itemsGroupedByCategory = this.itemService.list(PageRequest.of(0, 1000)).stream()
                .collect(Collectors.groupingBy(ItemEntity::getCategory));

        List<ItemEntity> shelterGroup = itemsGroupedByCategory.getOrDefault("Barinma", Collections.emptyList());
        this.shelter.setItems(shelterGroup);
        styleMultiSelectComboBox(this.shelter, "Barinma");

        List<ItemEntity> nutritionGroup = itemsGroupedByCategory.getOrDefault("Gida", Collections.emptyList());
        this.nutrition.setItems(nutritionGroup);
        styleMultiSelectComboBox(this.nutrition, "Gida");

        List<ItemEntity> clothGroup = itemsGroupedByCategory.getOrDefault("Kiyafet", Collections.emptyList());
        this.clothes.setItems(clothGroup);
        styleMultiSelectComboBox(this.clothes, "Kiyafet");

        List<ItemEntity> babyGroup = itemsGroupedByCategory.getOrDefault("Bebek", Collections.emptyList());
        this.baby.setItems(babyGroup);
        styleMultiSelectComboBox(this.baby, "Bebek");

        List<ItemEntity> disabledGroup = itemsGroupedByCategory.getOrDefault("Engelli", Collections.emptyList());
        this.disabled.setItems(disabledGroup);
        styleMultiSelectComboBox(this.disabled, "Engelli");

        List<ItemEntity> elderlyGroup = itemsGroupedByCategory.getOrDefault("Yasli", Collections.emptyList());
        this.elderly.setItems(elderlyGroup);
        styleMultiSelectComboBox(this.elderly, "Yasli");

        List<ItemEntity> petGroup = itemsGroupedByCategory.getOrDefault("Evcil Hayvan", Collections.emptyList());
        this.pet.setItems(petGroup);
        styleMultiSelectComboBox(this.pet, "Evcil Hayvan");

        List<ItemEntity> hygieneGroup = itemsGroupedByCategory.getOrDefault("Hijyen", Collections.emptyList());
        this.hygiene.setItems(hygieneGroup);
        styleMultiSelectComboBox(this.hygiene, "Hijyen");

        List<ItemEntity> medicalGroup = itemsGroupedByCategory.getOrDefault("Medikal", Collections.emptyList());
        this.medicine.setItems(medicalGroup);
        styleMultiSelectComboBox(this.medicine, "Medikal");

        List<ItemEntity> otherGroup = itemsGroupedByCategory.getOrDefault("Diger", Collections.emptyList());
        this.other.setItems(otherGroup);
        styleMultiSelectComboBox(this.other, "Diger");
    }

    private void onRequirementsClickEvent() {

        final var icoClose = VaadinIcon.CLOSE.create();
        styleCloseIcon(icoClose);

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
                this.other);

        dialog.open();

        add(
                dialog);

        icoClose.addClickListener(onCloseClickEvent -> {
            boolean updated = requirementService.updateBySessionIdOrPersonId(
                    currentPerson.getId(),
                    VaadinSession.getCurrent().getSession().getId(),
                    getAllSelectedItems());

            if (updated) {
                Notification.show(
                        "Ihtiyaclariniz basariyla guncellendi.", 5000, Notification.Position.TOP_CENTER);
            } else {
                setRequirements();
            }

            dialog.close();
        });
    }

    private List<ItemEntity> getAllSelectedItems() {
        return Stream.of(
                this.shelter,
                this.nutrition,
                this.clothes,
                this.baby,
                this.disabled,
                this.elderly,
                this.pet,
                this.hygiene,
                this.medicine,
                this.other).map(MultiSelectComboBox::getSelectedItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private String getRequirements() {
        final var reqOneLiner = Stream.of(
                this.shelter,
                this.nutrition,
                this.clothes,
                this.disabled,
                this.pet,
                this.hygiene,
                this.other).map(MultiSelectComboBox::getSelectedItems)
                .flatMap(Collection::stream)
                .map(ItemEntity::getTitle)
                .collect(Collectors.joining(","));

        System.out.println(reqOneLiner);

        return reqOneLiner;
    }

    private void setRequirements() {
        final var selectedShelterItems = this.shelter.getSelectedItems();
        final var selectedNutritionItems = this.nutrition.getSelectedItems();
        final var selectedClothesItems = this.clothes.getSelectedItems();
        final var selectedBabyItems = this.baby.getSelectedItems();
        final var selectedDisabledItems = this.disabled.getSelectedItems();
        final var selectedElderlyItems = this.elderly.getSelectedItems();
        final var selectedPetItems = this.pet.getSelectedItems();
        final var selectedHygieneItems = this.hygiene.getSelectedItems();
        final var selectedMedicineItems = this.medicine.getSelectedItems();
        final var selectedOtherItems = this.other.getSelectedItems();

        final var selectedItems = Stream.of(
                selectedShelterItems,
                selectedNutritionItems,
                selectedClothesItems,
                selectedBabyItems,
                selectedDisabledItems,
                selectedElderlyItems,
                selectedPetItems,
                selectedHygieneItems,
                selectedMedicineItems,
                selectedOtherItems).flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (ItemEntity item : selectedItems) {
            RequirementEntity newReq = new RequirementEntity();
            newReq.setItem(item);
            newReq.setQuantity(1.00);
            newReq.setPriority(1);
            newReq.setSessionId(VaadinSession.getCurrent().getSession().getId());
            newReq.setDescription("Aciklama");

            String phoneNumber = this.currentPerson.getPhone();
            if (this.personService.getByPhone(phoneNumber).isPresent()) {
                this.currentPerson = this.personService.getByPhone(phoneNumber).get();
            } else {

                PersonEntity newPer = new PersonEntity();
                newPer.setPhone(phoneNumber);
                newPer.setSessionId(VaadinSession.getCurrent().getSession().getId());
                newPer.setRegisteredAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                newPer.setLatitude(this.geoLocation.getValue().getLatitude());
                newPer.setLongitude(this.geoLocation.getValue().getLongitude());
                newPer.setImageUrl("https://i.ibb.co/0nZ3Z3T/unknown.png");
                newPer.setFirstName("Isim");
                newPer.setLastName("Soyisim");

                this.currentPerson = this.personService.update(newPer);
            }

            newReq.setPerson(this.currentPerson);

            this.requirementService.update(newReq);
        }
    }

    private void getAllPeopleLocation() {
        bindMapData();
    }

    private void getCurrentLocation() {

        if (findMeClickCount.incrementAndGet() == 1) {
            map.setCenter(
                    new LCenter(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude()));
            map.setViewPoint(new LCenter(this.geoLocation.getValue().getLatitude(),
                    this.geoLocation.getValue().getLongitude(), 8));

            final var tagText = "Sorgu: " + this.geoLocation.getValue().getLatitude() + ", "
                    + this.geoLocation.getValue().getLongitude() + " konumundaki kullanici bilgileri sorgulandi";
            final var markerMyCoordinates = new LMarker(this.geoLocation.getValue().getLatitude(),
                    this.geoLocation.getValue().getLongitude(), tagText);

            map.addMarkerClickListener(event -> {

                final var dialog = new Dialog();
                final var closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), closeProfileView -> {
                    dialog.close();
                });

                // Add the user profile layout to the dialog
                final var profileLayout = new ProfileLayout(
                        "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png",
                        this.currentPerson.getFirstName() + " " + this.currentPerson.getLastName(),
                        this.currentPerson.getPhone(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now()),
                        getRequirements());

                dialog.add(
                        closeButton,
                        profileLayout);

                dialog.open();
                add(dialog);
            });

            map.addLComponents(markerMyCoordinates);
        } else {
            map.setCenter(
                    new LCenter(this.geoLocation.getValue().getLatitude(), this.geoLocation.getValue().getLongitude()));
            map.setViewPoint(new LCenter(this.geoLocation.getValue().getLatitude(),
                    this.geoLocation.getValue().getLongitude(), 8));
        }

    }

}
