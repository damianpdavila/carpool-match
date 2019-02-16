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

        Map<RideType, Map<RideType, Integer>> rideTypeMatrix = new HashMap<>();
        Map<RideType, Integer> rideTypeColumn = new HashMap<>();

        rideTypeColumn.put(RideType.DROPOFF, 3);
        rideTypeColumn.put(RideType.PICKUP, 5);
        rideTypeColumn.put(RideType.BOTH, 5);
        rideTypeColumn.put(RideType.NONE, 99);
        rideTypeMatrix.put(RideType.DROPOFF, new HashMap<RideType, Integer>(rideTypeColumn));
        rideTypeColumn.put(RideType.DROPOFF, 5);
        rideTypeColumn.put(RideType.PICKUP, 3);
        rideTypeColumn.put(RideType.BOTH, 5);
        rideTypeColumn.put(RideType.NONE, 99);
        rideTypeMatrix.put(RideType.PICKUP, new HashMap<RideType, Integer>(rideTypeColumn));
        rideTypeColumn.put(RideType.DROPOFF, 5);
        rideTypeColumn.put(RideType.PICKUP, 5);
        rideTypeColumn.put(RideType.BOTH, 5);
        rideTypeColumn.put(RideType.NONE, 99);
        rideTypeMatrix.put(RideType.BOTH, new HashMap<RideType, Integer>(rideTypeColumn));
        rideTypeColumn.put(RideType.DROPOFF, 88);
        rideTypeColumn.put(RideType.PICKUP, 88);
        rideTypeColumn.put(RideType.BOTH, 88);
        rideTypeColumn.put(RideType.NONE, 0);
        rideTypeMatrix.put(RideType.NONE, new HashMap<RideType, Integer>(rideTypeColumn));

        boolean dayMisMatch = false;

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
                matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None)", 5);
            } else {
                rideTypeValue = rideTypeMatrix.get(user.getMatchCriteria().getRideType()).get(pmUser.getMatchCriteria().getRideType());

                if (rideTypeValue == 99){
                    /* could be a match, potential match user is not driving, need to compare compensation */
                    if (user.getMatchCriteria().getPayFromOtherRider() == null || pmUser.getMatchCriteria().getPayToOtherDriver() == null ||
                            user.getMatchCriteria().getPayFromOtherRider() <= pmUser.getMatchCriteria().getPayToOtherDriver()){
                        matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None) + Compensation: I get paid", 3);
                    }
                } else if (rideTypeValue == 88){
                    /* could be a match, current subject user is not driving, need to compare compensation */
                    if (user.getMatchCriteria().getPayToOtherDriver() == null || pmUser.getMatchCriteria().getPayFromOtherRider() == null ||
                            user.getMatchCriteria().getPayToOtherDriver() >= pmUser.getMatchCriteria().getPayFromOtherRider()){
                        matchedUser.addMatch("Ride type (Drop Off/Pick Up/Both/None) + Compensation: I can pay", 3);
                    }
                } else if (rideTypeValue != 0){
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
                matchedUser.addMatch("Days available", 5);
            }

         // private Double matchDistance;

         // private LocalTime dropoffTime;
            if (user.getMatchCriteria().getDropoffTime() == null || pmUser.getMatchCriteria().getDropoffTime() == null ||
                    ! user.getMatchCriteria().getDropoffTime().isAfter(pmUser.getMatchCriteria().getDropoffTime()) ){
                matchedUser.addMatch("Time I leave for drop off", 5);
            }
         // private LocalTime pickupTime;
            if (user.getMatchCriteria().getPickupTime() == null || pmUser.getMatchCriteria().getPickupTime() == null ||
                    ! user.getMatchCriteria().getPickupTime().isAfter(pmUser.getMatchCriteria().getPickupTime()) ){
                matchedUser.addMatch("Time I can pick up", 5);
            }
         // private Integer seatsAvailable;
            if (user.getMatchCriteria().getSeatsAvailable() == null || pmUser.getMatchCriteria().getSeatsNeeded() == null ||
                    user.getMatchCriteria().getSeatsAvailable() >= pmUser.getMatchCriteria().getSeatsNeeded()){
                matchedUser.addMatch("Seats I have", 5);
            }
         // private Integer seatsNeeded;
            if (user.getMatchCriteria().getSeatsNeeded() == null || pmUser.getMatchCriteria().getSeatsAvailable() == null ||
                    user.getMatchCriteria().getSeatsNeeded() <= pmUser.getMatchCriteria().getSeatsAvailable()){
                matchedUser.addMatch("Seats I need", 5);
            }
         // private boolean noSmoking;
            /* If not no smoking, then assume smoker OR no preference */
            if ( ! user.getMatchCriteria().isNoSmoking() || ! pmUser.getMatchCriteria().isNoSmoking() ||
                    user.getMatchCriteria().isNoSmoking() == pmUser.getMatchCriteria().isNoSmoking()){
                matchedUser.addMatch("Smoking preference", 5);
            }

            if (matchedUser.getMatchTotal() > 0){
                matchedUsers.add(matchedUser);
            }

        }

        matchedUsers.sort(new MatchValueComparator());
        return matchedUsers;
    }
}
