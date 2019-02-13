package com.moventisusa.carpoolmatch.models;

public enum RideType {
    DROPOFF("Drop off only"),
    PICKUP("Pick up only"),
    BOTH("Both"),
    NONE("None (rider only)");

    private String description;

    RideType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}




