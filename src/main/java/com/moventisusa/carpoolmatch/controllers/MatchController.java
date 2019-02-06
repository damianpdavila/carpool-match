package com.moventisusa.carpoolmatch.controllers;

import com.moventisusa.carpoolmatch.models.forms.UserForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchController extends AbstractBaseController {

    @GetMapping(value = "/")
    public String match(Model model) {
        model.addAttribute("title", "Home");
        return "index";
    }
}