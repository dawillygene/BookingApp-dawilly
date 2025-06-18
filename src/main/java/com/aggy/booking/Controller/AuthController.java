package com.aggy.booking.Controller;

import com.aggy.booking.Model.User;
import com.aggy.booking.Model.ServiceProvider;
import com.aggy.booking.Service.UserService;
import com.aggy.booking.Service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "Auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        try {
    
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username already exists!");
                return "Auth/register";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already exists!");
                return "Auth/register";
            }
        
            user.setRole(User.Role.USER);
            user.setEnabled(true);
            userService.createUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            return "Auth/register";
        }
    }

    @GetMapping("/register/provider")
    public String registerProvider(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("provider", new ServiceProvider());
        return "Auth/provider-register";
    }

    @PostMapping("/register/provider")
    public String registerProvider(@ModelAttribute("user") User user, 
                                 @ModelAttribute("provider") ServiceProvider provider,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate user data
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username already exists!");
                return "Auth/provider-register";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already exists!");
                return "Auth/provider-register";
            }
            
            // Create user account
            user.setRole(User.Role.PROVIDER);
            user.setEnabled(true);
            User savedUser = userService.createUser(user);
            
            // Create provider profile
            provider.setEmail(user.getEmail());
            provider.setFirstName(user.getFirstName());
            provider.setLastName(user.getLastName());
            provider.setUser(savedUser);
            provider.setIsActive(true);
            serviceProviderService.saveServiceProvider(provider);
            
            redirectAttributes.addFlashAttribute("success", "Provider registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
            model.addAttribute("user", user);
            model.addAttribute("provider", provider);
            return "Auth/provider-register";
        }
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
