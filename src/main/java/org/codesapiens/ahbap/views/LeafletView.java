package org.codesapiens.ahbap.views;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.router.RouteAlias;
import org.vaadin.elmot.flow.sensors.GeoLocation;
import software.xdev.vaadin.maps.leaflet.flow.LMap;
import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LCircle;
import software.xdev.vaadin.maps.leaflet.flow.data.LComponent;
import software.xdev.vaadin.maps.leaflet.flow.data.LDivIcon;
import software.xdev.vaadin.maps.leaflet.flow.data.LIcon;
import software.xdev.vaadin.maps.leaflet.flow.data.LMarker;
import software.xdev.vaadin.maps.leaflet.flow.data.LPoint;
import software.xdev.vaadin.maps.leaflet.flow.data.LPolygon;
import software.xdev.vaadin.maps.leaflet.flow.data.LTileLayer;


@Route("leaflet-maps-view")
public class LeafletView extends VerticalLayout {
    private boolean viewLunch = false;

    /**
     * UI-Components
     */
    private final Button btnLunch = new Button("Ben neredeyim?");
    private LMap map;

    private LMarker markerZob;

    private final GeoLocation geoLocation;

    public LeafletView(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
        add(this.geoLocation);

        this.initMapComponents();

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

                    icoClose.addClickListener(iev -> dialog.close());
                }));

        this.add(this.map, hlButtonContainer);
        this.setSizeFull();
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
