package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.User;
import com.moventisusa.carpoolmatch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * Created by Damian Davila
 */
public abstract class AbstractBaseController {

    @Autowired
    UserService userService;

    protected static final String MESSAGE_KEY = "message";

    @ModelAttribute("user")
    public User getLoggedInUser(Principal principal) {
        if (principal != null)
            return userService.findByEmail(principal.getName());
        return null;
    }
}
