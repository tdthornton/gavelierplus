package com.gavelier.gavelierplus;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.logging.Logger;

@Controller
public class Pages {


    private final static Logger LOGGER = Logger.getLogger(Pages.class.getName());

    @GetMapping("/")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Model model) {

        model.addAttribute("name", name);

        return "index";
    }

    @GetMapping("/restricted")
    public String restricted(Principal principal, Model model) {

        LOGGER.info("Access to restricted page");

        model.addAttribute("name", principal.getName());

        return "restricted";

    }

}