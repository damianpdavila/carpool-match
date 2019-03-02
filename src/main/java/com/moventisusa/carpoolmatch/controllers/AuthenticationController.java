package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.UserForm;
import com.moventisusa.carpoolmatch.services.EmailExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by Damian Davila
 */
@Controller
public class AuthenticationController extends AbstractBaseController {

    @GetMapping(value = "/register")
    public String registerForm(Model model, Principal principal) {

        /* Safety check. Ensure a user is not already logged in before registering. Ensures no leaking data */
        User user = getLoggedInUser(principal);
        if (user != null) {
            model.addAttribute(new UserForm());
            model.addAttribute(MESSAGE_KEY, "danger|Sorry you must log out first.");
            model.addAttribute("title", "Register");
            return "register";
        }

        model.addAttribute(new UserForm());
        model.addAttribute(MESSAGE_KEY, "info|Let's get started with your login credentials first.");
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping(value = "/register")
    public String register(@ModelAttribute @Valid UserForm userForm, Errors errors, RedirectAttributes model, Principal principal) {

        /* Safety check. Ensure a user is not already logged in before registering. Ensures no leaking data */
        User user = getLoggedInUser(principal);
        if (user != null) {
            model.addFlashAttribute(MESSAGE_KEY, "danger|Sorry you must log out first.");
            return "redirect:/register";
        }

        model.addAttribute("title", "Register");
        if (errors.hasErrors())
            return "register";

        try {
            userService.save(userForm);
        } catch (EmailExistsException e) {
            errors.rejectValue("email", "email.alreadyexists", e.getMessage());
            return "register";
        }
        model.addFlashAttribute(MESSAGE_KEY, "success|Credentials all set. Now let's get your contact and other info set up.");
        return "redirect:/profile";
    }

    @GetMapping(value = "/login")
    public String login(Model model, Principal user, String error, String logout) {

        if (user != null) {
            // TODO invalidate the session, effectively log out.
            ;
        }
        if (error != null)
            model.addAttribute(MESSAGE_KEY, "danger|Your username and password are invalid");

        if (logout != null)
            model.addAttribute(MESSAGE_KEY, "info|You have logged out");

        model.addAttribute("title", "Log In");

        return "login";
    }

    @GetMapping(value = "/force-logout")
    public String forceLogout(RedirectAttributes model, Principal user) {

        model.addFlashAttribute(MESSAGE_KEY, "warning|Updated your email; you must log in with your new email address");

        return "force-logout";
    }

}
