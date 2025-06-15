package com.aggy.booking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Add any model attributes needed for the home page
        model.addAttribute("pageTitle", "Home");
        return "pages/home";
    }
}