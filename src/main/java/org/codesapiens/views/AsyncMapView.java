package org.codesapiens.views;

import javax.annotation.PostConstruct;

import org.codesapiens.data.service.ItemService;
import org.codesapiens.data.service.MessageService;
import org.codesapiens.data.service.PersonService;
import org.codesapiens.data.service.RequirementService;
import org.codesapiens.data.service.ShareService;
import org.codesapiens.data.service.TagService;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import software.xdev.vaadin.maps.leaflet.flow.LMap;
import software.xdev.vaadin.maps.leaflet.flow.data.LCenter;
import software.xdev.vaadin.maps.leaflet.flow.data.LTileLayer;

@PageTitle("YARDIM ÇAĞIR!")
@Route("")
// @Push
public class AsyncMapView extends VerticalLayout {

    private static final String BASE_URL = "https://cagriapp.com/";

    private final PersonService personService;
    private final ItemService itemService;
    private final TagService tagService;
    private final RequirementService requirementService;
    private final ShareService shareService;
    private final MessageService messageService;

    private final GeoLocation geoLocation = new GeoLocation();
    public final LMap map = new LMap();

    private AsyncMapData thread;

    @PostConstruct
    public void init() {
        setMargin(false);
        setSpacing(false);
        setWidth("100%");
        setHeight("100%");
        setSizeFull();

        // initialize geoLocation
        geoLocation.setWatch(true);
        geoLocation.setHighAccuracy(true);
        geoLocation.setTimeout(100000);
        geoLocation.setMaxAge(200000);
        add(geoLocation);
    }

    public AsyncMapView(final PersonService personService,
            final ItemService itemService,
            final TagService tagService,
            final RequirementService requirementService,
            final ShareService shareService,
            final MessageService messageService) {

        this.personService = personService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.requirementService = requirementService;
        this.shareService = shareService;
        this.messageService = messageService;

        // Kahramanmaraş geolocation
        map.setZoom(6);
        var lCenter = geoLocation.getValue() != null
                ? new LCenter(geoLocation.getValue().getLatitude(), geoLocation.getValue().getLongitude())
                : new LCenter(37.585, 36.937);
        map.setCenter(lCenter);
        map.setViewPoint(lCenter);
        map.setSizeFull();
        map.setTileLayer(new LTileLayer(BASE_URL, BASE_URL, 0));

        add(map);

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {

        // Start the data feed thread
        thread = new AsyncMapData(attachEvent.getUI(), this, requirementService);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        thread.interrupt();
        thread = null;
    }

}
