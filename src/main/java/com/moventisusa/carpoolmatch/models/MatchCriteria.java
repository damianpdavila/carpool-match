package com.moventisusa.carpoolmatch.models;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Damian Davila
 */
@Entity
public class MatchCriteria extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    private RideType rideType;

    private double matchDistance;

    private LocalTime dropoffTime;

    private LocalTime pickupTime;

    private int seatsAvailable;

    private int seatsNeeded;

    private boolean smoking;

    private double payToOtherDriver;

    private double payFromOtherRider;

    @ElementCollection
    @MapKeyColumn
    @MapKeyEnumerated(EnumType.STRING)
    private Map<DayOfWeek, Boolean> daysAvailable = new HashMap<DayOfWeek, Boolean>();

    @OneToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL,
            mappedBy = "matchCriteria")
    private User user;


    public MatchCriteria() {}

    public MatchCriteria(RideType rideType, double matchDistance, LocalTime dropoffTime, LocalTime pickupTime, int seatsAvailable, int seatsNeeded, boolean smoking, double payToOtherDriver, double payFromOtherRider, Map<DayOfWeek, Boolean> daysAvailable, User user) {
        this.rideType = rideType;
        this.matchDistance = matchDistance;
        this.dropoffTime = dropoffTime;
        this.pickupTime = pickupTime;
        this.seatsAvailable = seatsAvailable;
        this.seatsNeeded = seatsNeeded;
        this.smoking = smoking;
        this.payToOtherDriver = payToOtherDriver;
        this.payFromOtherRider = payFromOtherRider;
        this.daysAvailable = daysAvailable;
        this.user = user;
    }

    public double getMatchDistance() {
        return matchDistance;
    }

    public void setMatchDistance(double matchDistance) {
        this.matchDistance = matchDistance;
    }

    public LocalTime getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(LocalTime dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public int getSeatsNeeded() {
        return seatsNeeded;
    }

    public void setSeatsNeeded(int seatsNeeded) {
        this.seatsNeeded = seatsNeeded;
    }

    public boolean isSmoking() {
        return smoking;
    }

    public void setSmoking(boolean smoking) {
        this.smoking = smoking;
    }

    public double getPayToOtherDriver() {
        return payToOtherDriver;
    }

    public void setPayToOtherDriver(double payToOtherDriver) {
        this.payToOtherDriver = payToOtherDriver;
    }

    public double getPayFromOtherRider() {
        return payFromOtherRider;
    }

    public void setPayFromOtherRider(double payFromOtherRider) {
        this.payFromOtherRider = payFromOtherRider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setRideType(RideType rideType) {
        this.rideType = rideType;
    }

    public Map<DayOfWeek, Boolean> getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(Map<DayOfWeek, Boolean> daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    /**  The inherited .equals() and .hashCode() are sufficient
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchCriteria)) return false;
        if (!super.equals(o)) return false;
        MatchCriteria that = (MatchCriteria) o;
        return this.getUid() == that.getUid();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid());
    }*/

    @Override
    public String toString() {
        return "MatchCriteria{" +
                "uid=" + getUid() +
                // ", user=" + user.getFullName() +
                ", matchDistance=" + matchDistance +
                ", dropoffTime=" + dropoffTime +
                ", pickupTime=" + pickupTime +
                ", seatsAvailable=" + seatsAvailable +
                ", seatsNeeded=" + seatsNeeded +
                ", smoking=" + smoking +
                ", payToOtherDriver=" + payToOtherDriver +
                ", payFromOtherRider=" + payFromOtherRider +
                '}';
    }

    private static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("\\S+@\\S+");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
