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

        /* create default email content */
        /*
        Hi Username,
        Looks like we're a match!
        I'd like to discuss starting a car pool with you if you're
        available.
                Here's my contact info:
        Name: Name
        Email: Email Address
        Phone: Phone
        I look forward to meeting you!
                IMPORTANT:
        Do not reply to this email sent by Car Pool Match. Name will
        not receive it. Click on Name's email address above or just
        copy and paste it into a new email message.
        */
        String separator = System.getProperty("line.separator");
        String contactMessage = String.join(
                separator,
                String.format("Hi %s,", toUser.getUsername()),
                "Looks like we're a match!",
                "I'd like to discuss starting a car pool with you if you're available",
                "Here's my contact info:",
                String.format("Name: %s", fromUser.getFullName()),
                String.format("Email: %s", fromUser.getEmail()),
                "Phone: ",
                "I look forward to meeting you!",
                "IMPORTANT:",
                String.format("Do not reply to this email sent by Carpool Match. %1$s will not receive it.  Click on %1$s 's email address above or just copy and paste it into a new message.", fromUser.getFullName())
        );

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

        // format and send email service

        redirModel.addFlashAttribute(MESSAGE_KEY, "success|Sent your message. Good luck!");
        return "redirect:/match";
    }



}
