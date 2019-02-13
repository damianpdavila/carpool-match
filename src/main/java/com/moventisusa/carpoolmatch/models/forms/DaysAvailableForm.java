package com.moventisusa.carpoolmatch.models.forms;

import java.time.DayOfWeek;

/**
 * Created by Damian Davila
 */

public class DaysAvailableForm {

    private String day;

    private boolean available;

    public DaysAvailableForm() {}

    public DaysAvailableForm(DayOfWeek day, Boolean available) {
        this.day = day.toString();
        this.available = available;
    }

    public DaysAvailableForm(String day, Boolean available) {
        this.day = day;
        this.available = available;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}
