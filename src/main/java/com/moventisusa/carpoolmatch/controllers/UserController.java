package com.moventisusa.carpoolmatch.controllers;

import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.moventisusa.carpoolmatch.models.MapConnector;
import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.services.EmailExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by Damian Davila
 */
@Controller
public class UserController extends AbstractBaseController {

    @GetMapping(value = "/profile")
    public String showProfile(Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        if (user == null){
            model.addAttribute(MESSAGE_KEY, "danger|User not found. Please log out and in again.");
            return "profile";
        }
        model.addAttribute(user);
        model.addAttribute("originalEmail", user.getEmail());
        model.addAttribute("updatedEmail", user.getEmail());
        model.addAttribute("title", "About You");
        return "profile";
    }

    @PostMapping(value = "/profile")
    public String updateProfile(@ModelAttribute @Valid User user,
                                @RequestParam String originalEmail,
                                @RequestParam String updatedEmail,
                                Errors errors, Model model, RedirectAttributes redirModel) {

        model.addAttribute("title", "About You");
        if (errors.hasErrors())
            return "profile";

        try {
            GeocodingResult[] results =  GeocodingApi.geocode(MapConnector.getInstance().getContext(),
                    user.getAddress1() +", "+ user.getAddress2() +", "+ user.getCity() +", "+ user.getState() +" "+ user.getPostalCode()).await();
            user.setLatitude(results[0].geometry.location.lat);
            user.setLongitude(results[0].geometry.location.lng);
        }
        catch (Exception e) {
            model.addAttribute(MESSAGE_KEY, "danger|Could not find your address; please correct or confirm it: ".concat(e.getMessage()));
            errors.rejectValue("address1", "address1.notexists", "Could not find your address; please correct or confirm it");
            return "profile";
        }
        /* Validate the email for duplicate */
        User existingUser = userService.findByEmail(updatedEmail);

        try {
            if (existingUser == null) {
                ;
            } else if (user.getUid() == existingUser.getUid()) {
                /* just an update to existing user so okay */
                ;
            } else {
                /* different user, same email so error */
                throw new EmailExistsException("The email address "
                        + updatedEmail + " already exists in the system");
            }
            user.setEmail(updatedEmail.trim());
            userService.update(user);
        }
        catch (EmailExistsException e) {
            errors.rejectValue("email", "email.alreadyexists", e.getMessage());
            model.addAttribute("originalEmail", originalEmail);
            model.addAttribute("updatedEmail", updatedEmail);
            return "profile";
        }
        catch (IllegalArgumentException e) {
            errors.rejectValue("email", "email.notexist", e.getMessage());
            model.addAttribute("originalEmail", originalEmail);
            model.addAttribute("updatedEmail", updatedEmail);
            return "profile";
        }

        if (user.getEmail().equals(originalEmail)) {
            redirModel.addFlashAttribute(MESSAGE_KEY, "success|Updated your profile");
            return "redirect:/preferences";
        }
        /* If updated email address, then need to invalidate the current authenticated session; simplest way is by routing to login */
        redirModel.addFlashAttribute(MESSAGE_KEY, "warning|Updated your email; you must log in with new credentials");
        return "redirect:/force-logout";
    }

}
