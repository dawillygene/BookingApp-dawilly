package com.aggy.booking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/register")
    public String register(){
        return "Auth/register";
    }
    @GetMapping("/login")
    public String login(){
        return "Auth/login";
    }

    @GetMapping("/home")
    public String home() {
        return "Dashboard/home";
    }

}
