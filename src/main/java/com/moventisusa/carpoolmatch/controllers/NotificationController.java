package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Damian Davila
 */
@Controller
public class NotificationController extends AbstractBaseController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/contact/{userId}")
    public String contactMatch(@PathVariable int userId, Model model, Principal principal) {

        User toUser;
        try {
            toUser = userRepository.findById(userId).get();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            model.addAttribute(MESSAGE_KEY, "danger|Matched 'to' user not found. Please log out and in again.");
            return "redirect:/match";
        }

        User fromUser = getLoggedInUser(principal);
        if (fromUser == null) {
            model.addAttribute(MESSAGE_KEY, "danger|User not found. Please log out and in again.");
            return "redirect:/match";
        }

        String contactMessage = "Hi Username,\n" +
                "Looks like we're a match!";
        model.addAttribute("fromUser", fromUser);
        model.addAttribute("toUser", toUser);
        model.addAttribute("contactMessage", contactMessage);
        model.addAttribute("title", "Contact Car Pooler");

        return "contact";
    }

    @PostMapping(value = "/contact")
    public String sendContact(@ModelAttribute @Valid User toUser,
                              @ModelAttribute @Valid User fromUser,
                                    @RequestParam String contactMessage,
                                    Errors errors, Model model, RedirectAttributes redirModel) {

        model.addAttribute("title", "Contact Car Pooler");
        if (errors.hasErrors())
            return "contact";

        // testing
        System.out.println(contactMessage);
        // format and send email

        redirModel.addFlashAttribute(MESSAGE_KEY, "success|Sent your message. Good luck!");
        return "redirect:/match";
    }



}
