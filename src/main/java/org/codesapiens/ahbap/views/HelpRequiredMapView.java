package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.codesapiens.ahbap.data.entity.RequirementEntity;
import org.codesapiens.ahbap.data.service.RequirementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import java.util.HashMap;
import java.util.UUID;

@PageTitle("YARDIM GEREKLİ OLANLAR!!!")
@Route(
        value = "emergency/map",
        layout = AppLayoutBottomNavbar.class
)
@RouteAlias(
        value = "acil/harita",
        layout = AppLayoutBottomNavbar.class
)
public class HelpRequiredMapView extends VerticalLayout {

    private final Map map = new Map();
    private final TextArea mapClickInfo = new TextArea("Seçili koordinat detayları");

    private final RequirementService requirementService;

    private final GeoLocation geoLocation;

    private final Button findMeButton = new Button("Beni Bul!");

    private static final String BASE_URL = "http://localhost:8080";

    public HelpRequiredMapView(RequirementService requirementService, GeoLocation geoLocation) {
        this.requirementService = requirementService;
        this.geoLocation = geoLocation;

        addClassName("map-view");

        Page<RequirementEntity> reqs = this.requirementService.list(Pageable.ofSize(1000));

        add(this.geoLocation);

        styleButton(findMeButton);
        this.findMeButton.addClickListener(onCLick -> {
            Double geoLat = this.geoLocation.getValue().getLatitude();
            Double geoLon = this.geoLocation.getValue().getLongitude();
            Coordinate center = new Coordinate(geoLat, geoLon);
            map.setCenter(center);
        });
        map.setZoom(15);
        add(map);

        // Setup text areas for logging event data
        mapClickInfo.setReadOnly(true);
        mapClickInfo.setWidthFull();
        mapClickInfo.addThemeName("monospace");
        add(mapClickInfo);

        // Add markers for cities
        HashMap<Feature, FoundPerson> personLookup = new HashMap<>();

        reqs.getContent().forEach(req -> {
            Coordinate coordinates = new Coordinate(
                    req.getPerson().getLongitude(),
                    req.getPerson().getLatitude()
            );

            MarkerFeature cityMarker = new MarkerFeature(coordinates);
            map.getFeatureLayer().addFeature(cityMarker);
            // Store relation between cities and markers in a hash map
            personLookup.put(cityMarker, new FoundPerson(req.getPerson().getId(), coordinates));
        });

        // Add a click listener to the map
        map.addClickEventListener(onSelectedLoc -> {
            Coordinate coordinates = onSelectedLoc.getCoordinate();
            String info = String.format("Coordinates = { x: %s, y: %s }", coordinates.getX(), coordinates.getY());
            mapClickInfo.setValue(info);
        });

        // Add click listener for markers
        map.addFeatureClickListener(onFeature -> {
            MarkerFeature feature = (MarkerFeature) onFeature.getFeature();
            Coordinate coordinates = feature.getCoordinates();
            // Get person entity for event marker,
            // see remaining example on how the markers are set up
            FoundPerson foundPerson = personLookup.get(feature);
            String info = "";
            info += String.format("City        = %s%n", foundPerson.getPersonId());
            info += String.format("Coordinates = { x: %s, y: %s }", coordinates.getX(), coordinates.getY());

            redirectToProfile(foundPerson.getPersonId());

        });

        setSpacing(false);
        setPadding(false);
    }


    private static class FoundPerson {
        private final UUID personId;
        private final Coordinate coordinates;

        public UUID getPersonId() {
            return personId;
        }

        public Coordinate getCoordinates() {
            return coordinates;
        }

        public FoundPerson(UUID personId, Coordinate coordinates) {
            this.personId = personId;
            this.coordinates = coordinates;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FoundPerson other = (FoundPerson) obj;
            if (coordinates == null) {
                return other.coordinates == null;
            } else {
                return coordinates.equals(other.coordinates);
            }
        }

        @Override
        public String toString() {
            return String.valueOf(this.getCoordinates());
        }

    }

    private static void styleButton(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        saveButton.setHeight(150, Unit.PIXELS);
        saveButton.setWidth(150, Unit.PIXELS);
        saveButton.getStyle()
                .set("font-weight", "bold")
                .set("margin-top", "0.5em")
                .set("font-size", "1.5em")
                .set("background-color", "var(--lumo-primary-color)")
                .set("color", "white")
                .set("border-radius", "50%");
        saveButton.setDisableOnClick(true);
    }

    private void redirectToProfile(UUID personId) {
        getUI().ifPresent(ui -> {
            String fullUrl = BASE_URL + "/emergency/profile/" + personId;
            ui.getPage().executeJs("window.open($0, '_blank')", fullUrl);
        });
    }

    private void redirectToUrl(Double[] from, Double[] to, String channel) {

        var redirectTo = new Object() {
            String currentUrl = "https://www.google.com/maps/dir" + "/"
                    + from[0] + "," + from[1] + "/"
                    + to[0] + "," + to[1] + "/@" + to[0] + "," + to[1];
        };

        if (channel.equalsIgnoreCase("twitter")) {
            redirectTo.currentUrl = "https://twitter.com/share?url=[localhost:8080/person/1]&text=[ACİL YARDIM ÇAĞRISI !!!]&via=afad&hashtags=deprem,yardim,acil,türkiye,afad";
        } else if (channel.equalsIgnoreCase("facebook")) {
            redirectTo.currentUrl = "https://www.facebook.com/sharer/sharer.php?u=[localhost:8080/person/1]";
        } else if (channel.equalsIgnoreCase("whatsapp")) {
            redirectTo.currentUrl = "https://api.whatsapp.com/send?text=[ACİL YARDIM!!!] [http://localhost:8080/person/1]";
        } else if (channel.equalsIgnoreCase("telegram")) {
            redirectTo.currentUrl = "https://telegram.me/share/url?url=[localhost:8080/person/1]";
        } else if (channel.equalsIgnoreCase("email")) {
            redirectTo.currentUrl = "mailto:?subject=[ACİL YARDIM!!!]&body=[http://localhost:8080/person/1]";
        } else if (channel.equalsIgnoreCase("sms")) {
            redirectTo.currentUrl = "sms:4411?body=[ACİL YARDIM!!!] [http://localhost:8080/person/1]";
        }

        getUI().ifPresent(ui -> ui.getPage().executeJs("window.open($0, '_blank')", redirectTo.currentUrl));

    }


}
