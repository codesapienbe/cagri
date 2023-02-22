package org.codesapiens.views;

import java.util.List;
import java.util.Map;

import org.codesapiens.data.dto.PersonDto;
import org.codesapiens.data.dto.RequirementDto;
import org.codesapiens.data.service.RequirementService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;

import org.codesapiens.views.ProfileLayout;

import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LMarker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

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

public class AsyncMapData extends Thread {

    private final UI ui;
    private final AsyncMapView view;
    private final RequirementService reqSrv;

    public AsyncMapData(UI ui, AsyncMapView view, RequirementService reqSrv) {
        this.ui = ui;
        this.view = view;
        this.reqSrv = reqSrv;
    }

    @Override
    public void run() {
        try {
            
            while (true) {
                Thread.sleep(500);
                ui.access(this::bindMapData);
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void bindMapData() {
        Map<PersonDto, List<RequirementDto>> reqs = reqSrv.getRequirementsGroupedByPerson();
        view.map.removeAll();
        for (PersonDto person : reqs.keySet()) {

            final var tagText = "Sorgu: " + person.getLatitude() + ", " + person.getLongitude() + " konumundaki kullanıcı bilgileri sorgulandı";
            final var markerMyCoordinates = new LMarker(person.getLatitude(), person.getLongitude(), tagText);
         
            view.map.addMarkerClickListener(event -> {
         
                final var dialog = new Dialog();
                final var closeButton = new Button(VaadinIcon.CLOSE_SMALL.create(), closeProfileView -> {
                    dialog.close();
                });

                final var reqsAsString = reqs.get(person).stream().map(req -> req.getItem().toString()).reduce((s1, s2) -> s1 + ", " + s2).orElse("");
         
                // Add the user profile layout to the dialog
                final var profileLayout = new ProfileLayout(
                        "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png",
                        person.getFirstName() + " " + person.getLastName(),
                        person.getPhone(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now()),
                        reqsAsString
                );
         
                dialog.add(
                        closeButton,
                        profileLayout
                );
         
                dialog.open();
                view.add(dialog);
            });
         
            view.map.addLComponents(markerMyCoordinates);
        }
    }
}
