package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.MatchCriteria;
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
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

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
            model.addAttribute(MESSAGE_KEY, "error|User not found. Please log out and in again.");
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
        /*  Thymeleaf/JPA wouldn't automatically link the daysAvailable map from html to database.
            So, after much research, am now doing this work around.
        */
        /*  Use DaysAvailableForm to link user's daysAvailable map to all weekdays in html */
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
        model.addAttribute("daysAvailableList", daysAvailableList);

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
            model.addAttribute(MESSAGE_KEY, "error|User not found. Please log out and in again.");
            return "match";
        }

        List<User> allUsers = userRepository.findAll();
        model.addAttribute("matchedUsers", matchService.getMatchedUsers(user, allUsers));
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

        User user = userRepository.findById(userId).get();
        if (user == null) {
            model.addAttribute(MESSAGE_KEY, "error|User not found. Please log out and in again.");
            return "match";
        }

        /*  Thymeleaf/JPA wouldn't automatically link the daysAvailable map from html to database.
            So, after much research, am now doing this work around.
        */
        /*  Use DaysAvailableForm to link user's daysAvailable map to all weekdays in html */
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
        model.addAttribute("daysAvailableList", daysAvailableList);

        model.addAttribute(user);
        model.addAttribute("title", "View A Match");


        return "view";
    }


}