package com.moventisusa.carpoolmatch.models;

import com.google.maps.GeoApiContext;
import com.moventisusa.carpoolmatch.config.ApplicationConfig;

public class MapConnector {

    private static MapConnector mapConnector = new MapConnector();

    private ApplicationConfig applicationConfig = new ApplicationConfig();

    private GeoApiContext mapContext = new GeoApiContext.Builder()
            .apiKey(applicationConfig.getMapApiKey())
            .build();

    private MapConnector() {}

    public static MapConnector getInstance(){
        return mapConnector;
    }

    public GeoApiContext getContext() {
        return mapContext;
    }

}
