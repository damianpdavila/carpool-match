package com.moventisusa.carpoolmatch.services;

import com.moventisusa.carpoolmatch.comparators.MatchValueComparator;
import com.moventisusa.carpoolmatch.models.MatchedUser;
import com.moventisusa.carpoolmatch.models.RideType;
import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;

@Service
public class MatchService {

    @Autowired
    private UserRepository userRepository;

    public List<MatchedUser> getMatchedUsers(User user, List<User> potentialMatches){

        List<MatchedUser> matchedUsers = new ArrayList<>();

        /** Create 4x4 matrix to match the RideType.
         *  Outer map is rideType of current subject user.
         *  Nested map has rideType for potential user match we are processing.
         *  The result is the Integer "match value": 0:no match; 1-5:match; 88:maybe, user ride only; 99:maybe, pmUser ride only;
         */
        int rideTypeValue = 0;
        Integer NO_MATCH = 0;
        Integer LOW_MATCH = 1;
        Integer MID_MATCH = 3;
        Integer HIGH_MATCH = 5;
        Integer MAYBE_MATCH_USER_RIDE_ONLY = 88;
        Integer MAYBE_MATCH_POTENTIAL_USER_RIDE_ONLY = 99;

        Map<RideType, Map<RideType, Integer>> rideTypeMatrix = new HashMap<>();
        Map<RideType, Integer> rideTypeColumn = new HashMap<>();

        rideTypeColumn.put(RideType.DROPOFF, MID_MATCH);
        rideTypeColumn.put(RideType.PICKUP, HIGH_MATCH);
        rideTypeColumn.put(RideType.BOTH, HIGH_MATCH);
        rideTypeColumn.put(RideType.NONE, MAYBE_MATCH_POTENTIAL_USER_RIDE_ONLY);
        rideTypeMatrix.put(RideType.DROPOFF, new HashMap<RideType, Integer>(rideTypeColumn));
        rideTypeColumn.put(RideType.DROPOFF, HIGH_MATCH);
        rideTypeColumn.put(RideType.PICKUP, MID_MATCH);
        rideTypeColumn.put(RideType.BOTH, HIGH_MATCH);
        rideTypeColumn.put(RideType.NONE, MAYBE_MATCH_POTENTIAL_USER_RIDE_ONLY);
        rideTypeMatrix.put(RideType.PICKUP, new HashMap<RideType, Integer>(rideTypeColumn));
        rideTypeColumn.put(RideType.DROPOFF, HIGH_MATCH);
        rideTypeColumn.put(RideType.PICKUP, HIGH_MATCH);
        rideTypeColumn.put(RideType.BOTH, HIGH_MATCH);
        rideTypeColumn.put(RideType.NONE, MAYBE_MATCH_POTENTIAL_USER_RIDE_ONLY);
        rideTypeMatrix.put(RideType.BOTH, new HashMap<RideType, Integer>(rideTypeColumn));
        rideTypeColumn.put(RideType.DROPOFF, MAYBE_MATCH_USER_RIDE_ONLY);
        rideTypeColumn.put(RideType.PICKUP, MAYBE_MATCH_USER_RIDE_ONLY);
        rideTypeColumn.put(RideType.BOTH, MAYBE_MATCH_USER_RIDE_ONLY);
        rideTypeColumn.put(RideType.NONE, NO_MATCH);
        rideTypeMatrix.put(RideType.NONE, new HashMap<RideType, Integer>(rideTypeColumn));

        boolean dayMisMatch = false;
        double matchDistance = 0;

        for (User pmUser : potentialMatches) {

            if (pmUser.equals(user)) continue;

            MatchedUser matchedUser = new MatchedUser(pmUser);

            /** Note that nulls are equivalent to "no preference"
             *  so always match for matching purposes.
             */

         // private RideType rideType;
         // private Double payToOtherDriver;
         // private Double payFromOtherRider;
            if (user.getMatchCriteria().getRideType() == null || pmUser.getMatchCriteria().getRideType() == null){
                matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None)", HIGH_MATCH);
            } else {
                rideTypeValue = rideTypeMatrix.get(user.getMatchCriteria().getRideType()).get(pmUser.getMatchCriteria().getRideType());

                if (rideTypeValue == MAYBE_MATCH_POTENTIAL_USER_RIDE_ONLY){
                    /* could be a match, potential match user is not driving, need to compare compensation */
                    if (user.getMatchCriteria().getPayFromOtherRider() == null || pmUser.getMatchCriteria().getPayToOtherDriver() == null ||
                            user.getMatchCriteria().getPayFromOtherRider() <= pmUser.getMatchCriteria().getPayToOtherDriver()){
                        matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None) + Compensation: I get paid", MID_MATCH);
                    }
                } else if (rideTypeValue == MAYBE_MATCH_USER_RIDE_ONLY){
                    /* could be a match, current subject user is not driving, need to compare compensation */
                    if (user.getMatchCriteria().getPayToOtherDriver() == null || pmUser.getMatchCriteria().getPayFromOtherRider() == null ||
                            user.getMatchCriteria().getPayToOtherDriver() >= pmUser.getMatchCriteria().getPayFromOtherRider()){
                        matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None) + Compensation: I can pay", MID_MATCH);
                    }
                } else if (rideTypeValue != NO_MATCH){
                    /* value 0 = "no match" */
                    matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None)", rideTypeValue);
                }

            }
         // private Map<DayOfWeek, Boolean> daysAvailable = new HashMap<DayOfWeek, Boolean>();
            /* This criterium does not have a "no preference" option */
            for (DayOfWeek day : DayOfWeek.values()){
                if (user.getMatchCriteria().getDaysAvailable().get(day) != pmUser.getMatchCriteria().getDaysAvailable().get(day)) {
                    dayMisMatch = true;
                    break;
                }
            }
            if ( ! dayMisMatch){
                matchedUser.addMatch("Days available", HIGH_MATCH);
            }

         // private Double matchDistance;
            matchDistance = distance(user.getLatitude(), user.getLongitude(), pmUser.getLatitude(), pmUser.getLongitude(), "M");

            if (user.getMatchCriteria().getMatchDistance() == null ||
                    matchDistance <= user.getMatchCriteria().getMatchDistance()){
                matchedUser.addMatch("Distance to car pool match", HIGH_MATCH);
            }

         // private LocalTime dropoffTime;
            if (user.getMatchCriteria().getDropoffTime() == null || pmUser.getMatchCriteria().getDropoffTime() == null ||
                    ! user.getMatchCriteria().getDropoffTime().isAfter(pmUser.getMatchCriteria().getDropoffTime()) ){
                matchedUser.addMatch("Time I leave for drop off", HIGH_MATCH);
            }
         // private LocalTime pickupTime;
            if (user.getMatchCriteria().getPickupTime() == null || pmUser.getMatchCriteria().getPickupTime() == null ||
                    ! user.getMatchCriteria().getPickupTime().isAfter(pmUser.getMatchCriteria().getPickupTime()) ){
                matchedUser.addMatch("Time I can pick up", HIGH_MATCH);
            }
         // private Integer seatsAvailable;
            if (user.getMatchCriteria().getSeatsAvailable() == null || pmUser.getMatchCriteria().getSeatsNeeded() == null ||
                    user.getMatchCriteria().getSeatsAvailable() >= pmUser.getMatchCriteria().getSeatsNeeded()){
                matchedUser.addMatch("Seats I have", HIGH_MATCH);
            }
         // private Integer seatsNeeded;
            if (user.getMatchCriteria().getSeatsNeeded() == null || pmUser.getMatchCriteria().getSeatsAvailable() == null ||
                    user.getMatchCriteria().getSeatsNeeded() <= pmUser.getMatchCriteria().getSeatsAvailable()){
                matchedUser.addMatch("Seats I need", HIGH_MATCH);
            }
         // private boolean noSmoking;
            /* If not no smoking, then assume smoker OR no preference */
            if ( ! user.getMatchCriteria().isNoSmoking() || ! pmUser.getMatchCriteria().isNoSmoking() ||
                    user.getMatchCriteria().isNoSmoking() == pmUser.getMatchCriteria().isNoSmoking()){
                matchedUser.addMatch("Smoking preference", HIGH_MATCH);
            }

            if (matchedUser.getMatchTotal() > 0){
                matchedUsers.add(matchedUser);
            }

        }

        matchedUsers.sort(new MatchValueComparator());
        return matchedUsers;
    }

    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::                                                                         :*/
    /*::  This routine calculates the distance between two points (given the     :*/
    /*::  latitude/longitude of those points). It is being used to calculate     :*/
    /*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
    /*::                                                                         :*/
    /*::  Definitions:                                                           :*/
    /*::    South latitudes are negative, east longitudes are positive           :*/
    /*::                                                                         :*/
    /*::  Passed to function:                                                    :*/
    /*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
    /*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
    /*::    unit = the unit you desire for results                               :*/
    /*::           where: 'M' is statute miles (default)                         :*/
    /*::                  'K' is kilometers                                      :*/
    /*::                  'N' is nautical miles                                  :*/
    /*::  Worldwide cities and other features databases with latitude longitude  :*/
    /*::  are available at https://www.geodatasource.com                         :*/
    /*::                                                                         :*/
    /*::  For enquiries, please contact sales@geodatasource.com                  :*/
    /*::                                                                         :*/
    /*::  Official Web site: https://www.geodatasource.com                       :*/
    /*::                                                                         :*/
    /*::           GeoDataSource.com (C) All Rights Reserved 2018                :*/
    /*::                                                                         :*/
    /*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
}
