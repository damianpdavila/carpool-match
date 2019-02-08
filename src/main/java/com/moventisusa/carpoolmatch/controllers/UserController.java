package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.controllers.AbstractBaseController;
import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.UserForm;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import com.moventisusa.carpoolmatch.services.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.security.Principal;

/**
 * Created by Damian Davila
 */
@Controller
public class UserController extends AbstractBaseController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/profile")
    public String showProfile(Model model, Principal principal) {
        User user = getLoggedInUser(principal);
        if (user == null){
            model.addAttribute(MESSAGE_KEY, "error|User not found. Please log out and in again.");
            return "profile";
        }
        model.addAttribute(user);
        model.addAttribute("title", "About You");
        return "profile";
    }

    @PostMapping(value = "/profile")
    public String updateProfile(@ModelAttribute @Valid User user, Errors errors, RedirectAttributes model) {

        model.addAttribute("title", "About You");
        if (errors.hasErrors())
            return "profile";

        try {
            userService.update(user);
        } catch (EmailExistsException e) {
            errors.rejectValue("email", "email.alreadyexists", e.getMessage());
            return "profile";
        }
        model.addFlashAttribute(MESSAGE_KEY, "success|Updated your profile");
        return "redirect:/preferences";
    }

}
