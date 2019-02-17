package com.moventisusa.carpoolmatch.models;

import com.google.maps.GeoApiContext;

public class MapConnector {

    private static MapConnector mapConnector = new MapConnector();

    private GeoApiContext mapContext = new GeoApiContext.Builder()
            .apiKey("AIzaSyBlEMTjswGM0-lDgv3GsDUI3xv18aBOlns")
            .build();

    private MapConnector() {}

    public static MapConnector getInstance(){
        return mapConnector;
    }

    public GeoApiContext getContext() {
        return mapContext;
    }

}
