package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.models.forms.EmailComposeForm;
import com.moventisusa.carpoolmatch.repositories.UserRepository;
import com.moventisusa.carpoolmatch.services.EmailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.NoSuchElementException;

/**
 * Created by Damian Davila
 */
@Controller
public class NotificationController extends AbstractBaseController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private EmailSendService emailSendService;

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

        model.addAttribute("emailForm", new EmailComposeForm(fromUser, toUser, contactMessage));
        model.addAttribute("title", "Contact Car Pooler");

        return "contact";
    }

    @PostMapping(value = "/contact/{toUserId}")
    public String sendContact(@PathVariable int toUserId,
                                    @ModelAttribute EmailComposeForm emailForm,
                                    Errors errors, Model model, RedirectAttributes redirModel,  Principal principal) {

        model.addAttribute("title", "Contact Car Pooler");
        if (errors.hasErrors())
            return "contact";

        User toUser;
        try {
            toUser = userRepository.findById(toUserId).get();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            model.addAttribute(MESSAGE_KEY, "danger|Matched 'to' user not found. Please log out and in again.");
            return "redirect:/match";
        }

        String toUserEmailFormatted = String.join("", toUser.getFullName(), " <", toUser.getEmail(), ">" );
        try {
            emailSendService.prepareAndSend(toUserEmailFormatted, EmailSendService.CPMATCH_EMAIL_FROM, EmailSendService.CPMATCH_EMAIL_SUBJECT, emailForm.getMessage());
            redirModel.addFlashAttribute(MESSAGE_KEY, "success|Sent your message. Good luck!");
        } catch (MailException me) {
            redirModel.addFlashAttribute(MESSAGE_KEY, String.join("","danger|Error sending your message to user ", toUser.getUsername(), ". Please try again."));
        }

        return "redirect:/match";
    }

}
