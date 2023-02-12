package org.codesapiens.ahbap.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.elmot.flow.sensors.GeoLocation;

@Configuration
public class GeolocationConfig {

    @Bean
    public GeoLocation getGeolocation() {
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setWatch(true);
        geoLocation.setHighAccuracy(true);
        geoLocation.setTimeout(100000);
        geoLocation.setMaxAge(200000);
        return geoLocation;
    }
}
