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

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("pageTitle", "Admin Panel");
        model.addAttribute("breadcrumb", "Admin");
        // Add the missing adminStats object with proper structure
        model.addAttribute("adminStats", new AdminStats());
        // Add sample admin data
        model.addAttribute("totalUsers", 150);
        model.addAttribute("totalAppointments", 1250);
        model.addAttribute("monthlyRevenue", 15500.00);
        model.addAttribute("activeProviders", 8);
        return "pages/admin";
    }

    // AdminStats class to hold dashboard statistics
    static class AdminStats {
        public int todayAppointments = 12;
        public int totalRevenue = 2450;
        public int newCustomers = 8;
        public int pendingBookings = 5;
        public double occupancyRate = 85.5;

        // Constructor with default demo values
        public AdminStats() {
            // Initialize with sample data
        }

        // Getters (Spring MVC will use these for template binding)
        public int getTodayAppointments() { return todayAppointments; }
        public int getTotalRevenue() { return totalRevenue; }
        public int getNewCustomers() { return newCustomers; }
        public int getPendingBookings() { return pendingBookings; }
        public double getOccupancyRate() { return occupancyRate; }
    }
}