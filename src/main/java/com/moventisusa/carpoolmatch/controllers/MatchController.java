package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.MatchCriteria;
import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.DaysAvailableForm;
import com.moventisusa.carpoolmatch.repositories.MatchCriteriaRepository;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

@Controller
public class MatchController extends AbstractBaseController {

    @Autowired
    MatchCriteriaRepository matchCriteriaRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/preferences")
    public String showPreferences(Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        if (user == null){
            model.addAttribute(MESSAGE_KEY, "error|User not found. Please log out and in again.");
            return "preferences";
        }
        if (user.getMatchCriteria() == null){
            MatchCriteria newCriteria = new MatchCriteria();

            Map<DayOfWeek,Boolean> testAvailable = new HashMap<DayOfWeek, Boolean>();
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
        for (DayOfWeek day : DayOfWeek.values() ){
            /*  if this day in user's daysAvailable map, set available, otherwise set unavailable */
            DaysAvailableForm daForm = new DaysAvailableForm();
            daForm.setDay(day.getDisplayName(TextStyle.FULL, Locale.US));
            /* */
            available = user.getMatchCriteria().getDaysAvailable().getOrDefault(day, false);
            daForm.setAvailable(available);
            daysAvailableList.add(daForm);
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
        Map<DayOfWeek,Boolean> newAvailable = new HashMap<DayOfWeek, Boolean>();
        for (String day : daysAvailableUpdate){
            newAvailable.put(DayOfWeek.valueOf(day.toUpperCase().trim()), true);
        }
        user.getMatchCriteria().setDaysAvailable(newAvailable);

        userRepository.save(user);

        redirModel.addFlashAttribute(MESSAGE_KEY, "success|Updated your match criteria");
        return "redirect:/";
    }

}