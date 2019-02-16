package com.moventisusa.carpoolmatch.models;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    private RideType rideType;

    private Double matchDistance;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime dropoffTime;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime pickupTime;

    private Integer seatsAvailable;

    private Integer seatsNeeded;

    private boolean noSmoking;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double payToOtherDriver;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double payFromOtherRider;

    @ElementCollection
    @MapKeyColumn
    @MapKeyEnumerated(EnumType.STRING)
    private Map<DayOfWeek, Boolean> daysAvailable = new HashMap<DayOfWeek, Boolean>();

    @OneToOne(fetch = FetchType.LAZY, cascade= CascadeType.ALL,
            mappedBy = "matchCriteria")
    private User user;


    public MatchCriteria() {}

    public MatchCriteria(RideType rideType, Double matchDistance, LocalTime dropoffTime, LocalTime pickupTime, Integer seatsAvailable, Integer seatsNeeded, boolean noSmoking, Double payToOtherDriver, Double payFromOtherRider, Map<DayOfWeek, Boolean> daysAvailable, User user) {
        this.rideType = rideType;
        this.matchDistance = matchDistance;
        this.dropoffTime = dropoffTime;
        this.pickupTime = pickupTime;
        this.seatsAvailable = seatsAvailable;
        this.seatsNeeded = seatsNeeded;
        this.noSmoking = noSmoking;
        this.payToOtherDriver = payToOtherDriver;
        this.payFromOtherRider = payFromOtherRider;
        this.daysAvailable = daysAvailable;
        this.user = user;
    }

    public RideType getRideType() {
        return rideType;
    }

    public void setRideType(RideType rideType) {
        this.rideType = rideType;
    }

    public Double getMatchDistance() {
        return matchDistance;
    }

    public void setMatchDistance(Double matchDistance) {
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

    public Integer getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(Integer seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public Integer getSeatsNeeded() {
        return seatsNeeded;
    }

    public void setSeatsNeeded(Integer seatsNeeded) {
        this.seatsNeeded = seatsNeeded;
    }

    public boolean isNoSmoking() {
        return noSmoking;
    }

    public void setNoSmoking(boolean noSmoking) {
        this.noSmoking = noSmoking;
    }

    public Double getPayToOtherDriver() {
        return payToOtherDriver;
    }

    public void setPayToOtherDriver(Double payToOtherDriver) {
        this.payToOtherDriver = payToOtherDriver;
    }

    public Double getPayFromOtherRider() {
        return payFromOtherRider;
    }

    public void setPayFromOtherRider(Double payFromOtherRider) {
        this.payFromOtherRider = payFromOtherRider;
    }

    public Map<DayOfWeek, Boolean> getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(Map<DayOfWeek, Boolean> daysAvailable) {
        this.daysAvailable = daysAvailable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                ", noSmoking=" + noSmoking +
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
