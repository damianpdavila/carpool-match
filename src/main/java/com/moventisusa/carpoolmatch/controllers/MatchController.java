package com.moventisusa.carpoolmatch.controllers;

import com.google.maps.ImageResult;
import com.google.maps.StaticMapsApi;
import com.google.maps.StaticMapsRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.Size;
import com.moventisusa.carpoolmatch.models.MapConnector;
import com.moventisusa.carpoolmatch.models.MatchCriteria;
import com.moventisusa.carpoolmatch.models.MatchedUser;
import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.DaysAvailableForm;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import com.moventisusa.carpoolmatch.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

@Controller
public class MatchController extends AbstractBaseController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MatchService matchService;

    @GetMapping(value = "/preferences")
    public String showPreferences(Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        if (user == null) {
            model.addAttribute(MESSAGE_KEY, "danger|User not found. Please log out and in again.");
            return "preferences";
        }
        if (user.getMatchCriteria() == null) {
            MatchCriteria newCriteria = new MatchCriteria();
            newCriteria.setNoSmoking(true);

            Map<DayOfWeek, Boolean> testAvailable = new HashMap<DayOfWeek, Boolean>();
            testAvailable.put(DayOfWeek.MONDAY, true);
            testAvailable.put(DayOfWeek.TUESDAY, true);
            testAvailable.put(DayOfWeek.WEDNESDAY, true);
            testAvailable.put(DayOfWeek.THURSDAY, true);
            testAvailable.put(DayOfWeek.FRIDAY, true);
            newCriteria.setDaysAvailable(testAvailable);

            user.setMatchCriteria(newCriteria);
        }
        model.addAttribute("daysAvailableList", createDaysAvailableList(user));
        model.addAttribute(user);
        model.addAttribute("title", "Your Match Criteria");
        return "preferences";
    }

    @PostMapping(value = "/preferences")
    public String updatePreferences(@ModelAttribute @Valid User user,
                                    @RequestParam String[] daysAvailableUpdate,
                                    Errors errors, Model model, RedirectAttributes redirModel) {

        model.addAttribute("title", "Your Match Criteria");
        if (errors.hasErrors())
            return "preferences";

        /* Re-initialize the user's daysAvailable hashmap to reflect their choices; map only contains available days. */
        Map<DayOfWeek, Boolean> newAvailable = new HashMap<>();
        for (String day : daysAvailableUpdate) {
            newAvailable.put(DayOfWeek.valueOf(day.toUpperCase().trim()), true);
        }
        user.getMatchCriteria().setDaysAvailable(newAvailable);

        userRepository.save(user);

        redirModel.addFlashAttribute(MESSAGE_KEY, "success|Updated your match criteria");
        return "redirect:/match";
    }

    @GetMapping(value = "/match")
    public String getMatches(Model model, Principal principal) {

        User user = getLoggedInUser(principal);
        if (user == null) {
            model.addAttribute(MESSAGE_KEY, "danger|User not found. Please log out and in again.");
            return "match";
        }

        List<User> allUsers = userRepository.findAll();
        model.addAttribute("matchedUsers", matchService.getMatchedUsers(user, allUsers));

        String mapImageString = createMatchMapImageString(user, matchService.getMatchedUsers(user, allUsers));
        model.addAttribute("mapImage", mapImageString);
        model.addAttribute("title", "Your Matches");

        return "match";
    }

    @PostMapping(value = "/match")
    public String showMatches(@ModelAttribute @Valid User user,
                              // @RequestParam String[] dummy,
                              Errors errors, Model model, RedirectAttributes redirModel) {

        model.addAttribute("title", "Your Match Criteria");
        if (errors.hasErrors())
            return "preferences";

        //redirModel.addFlashAttribute(MESSAGE_KEY, "success|Updated your match criteria");
        return "match";
    }

    @GetMapping(value = "/view/{userId}")
    public String viewMatch(@PathVariable int userId, Model model, Principal principal) {

        User user;
        try {
            user = userRepository.findById(userId).get();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            model.addAttribute(MESSAGE_KEY, "danger|User not found. Please log out and in again.");
            return "match";
        }

        model.addAttribute("daysAvailableList", createDaysAvailableList(user));

        model.addAttribute(user);
        model.addAttribute("title", "View A Match");


        return "view";
    }
    /*  Thymeleaf/JPA wouldn't automatically link the daysAvailable map from html to database.
        So, after much research, am now doing this work around.
        Use DaysAvailableForm to link user's daysAvailable map to all weekdays in html.
    */

    /** Build list of DaysAvailableForm's, forcing one per each day of week.
     *
     * @param user  user requesting to be matched
     * @return      DaysAvailableForm object populated with user's data
     */
    private List<DaysAvailableForm> createDaysAvailableList(User user) {

        List<DaysAvailableForm> daysAvailableList = new ArrayList<>();
        /*  Build list of DaysAvailableForm's, forcing one per each day of week.
            Want all days of week so that displays on page that way.
        */
        boolean available;
        for (DayOfWeek day : DayOfWeek.values()) {

            DaysAvailableForm daForm = new DaysAvailableForm();
            daForm.setDay(day.getDisplayName(TextStyle.FULL, Locale.US));
            available = user.getMatchCriteria().getDaysAvailable().getOrDefault(day, false);
            daForm.setAvailable(available);
            daysAvailableList.add(daForm);
            /* just need M-F for this implementation */
            if (day == DayOfWeek.FRIDAY) break;
        }

        return daysAvailableList;

    }

    /** Generate Google Static Map using matched user data.
     *
     * @param user          user requesting the matches
     * @param matchedUsers  list of other users who matched
     * @return              The string value to insert into the <IMG> src attribute.
     */
    private String createMatchMapImageString(User user, List<MatchedUser> matchedUsers){

        final int WIDTH = 640;
        final int HEIGHT = 480;

        StaticMapsRequest req = StaticMapsApi.newRequest(MapConnector.getInstance().getContext(), new Size(WIDTH, HEIGHT));

        StaticMapsRequest.Markers markers = new StaticMapsRequest.Markers();
        markers.color("black");
        markers.addLocation(new LatLng(user.getLatitude(), user.getLongitude()));
        req.markers(markers);

        for (int i = 0; i < matchedUsers.size(); i++) {
            markers = new StaticMapsRequest.Markers();
            if (matchedUsers.get(i).getMatchDetail().containsKey(MatchService.CRITERIA_DISTANCETOMATCH)) {
                markers.color("green");
            } else {
                markers.color("red");
            }
            /* Number the first 5 matches; also, the static map can only handle single-digit labels (actually 0-9, and A-Z, but only 1 character) */
            if (i<6) {markers.label(String.valueOf(i+1));}
            markers.addLocation(new LatLng(matchedUsers.get(i).getUser().getLatitude(), matchedUsers.get(i).getUser().getLongitude()));
            req.markers(markers);
        }
        LatLng center = new LatLng(user.getLatitude(), user.getLongitude());
        req.center(center);

        StaticMapsRequest.Path path = new StaticMapsRequest.Path();
        path.color("green");

        List<LatLng> pathPoints = getCirclePoints(center, user.getMatchCriteria().getMatchDistance(), "mile");
        for (LatLng point : pathPoints){
            path.addPoint(point);
        }
        req.path(path);

        ImageResult mapRaw;
        try {
            mapRaw = req.await();
        } catch (ApiException | InterruptedException | IOException e) {
            return "Error creating map image: " + e.getLocalizedMessage();
        }

        return "data:" + mapRaw.contentType + ";base64," + Base64.getEncoder().encodeToString(mapRaw.imageData);

    }

    /** Generate data (geographic points) which will later be drawn as a path in the static map
     *
     * @param center    the center of the circle
     * @param radiusD  (in meters)
     * @return          list of points that make up the circle, expressed in latitude, longitude pairs
     */
    private List<LatLng> getCirclePoints(LatLng center, Double radiusD, String unit) {
        List<LatLng> circlePoints = new ArrayList<>();
        final double MILES_TO_METERS = 1609.344;
        double radius;

        if (radiusD == null) {
            radius = 0;
        } else {
            radius = radiusD;
        }
        if (unit.equals("mile")) {
            radius = radius * MILES_TO_METERS;
        } else if (unit.equals("km")) {
            radius = radius * 1000;
        }
        // convert center coordinates to radians
        double lat_rad = Math.toRadians(center.lat);
        double lon_rad = Math.toRadians(center.lng);
        double dist = radius / 6378137;

        // calculate circle path point for each 5 degrees
        for (int deg = 0; deg <= 360; deg += 5) {
            double rad = Math.toRadians(deg);

            // calculate coordinates of next circle path point
            double new_lat = Math.asin(Math.sin(lat_rad) * Math.cos(dist) + Math.cos(lat_rad) * Math.sin(dist) * Math.cos(rad));
            double new_lon = lon_rad + Math.atan2(Math.sin(rad) * Math.sin(dist) * Math.cos(lat_rad), Math.cos(dist)
                    - Math.sin(lat_rad) * Math.sin(new_lat));

            // convert new lat and lon to degrees
            double new_lat_deg = Math.toDegrees(new_lat);
            double new_lon_deg = Math.toDegrees(new_lon);

            circlePoints.add(new LatLng(new_lat_deg, new_lon_deg));
        }

        return circlePoints;
    }
}