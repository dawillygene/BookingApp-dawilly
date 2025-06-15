package com.aggy.booking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("adminStats", new AdminStats());
        model.addAttribute("totalUsers", 150);
        model.addAttribute("totalAppointments", 1250);
        model.addAttribute("monthlyRevenue", 15500.00);
        model.addAttribute("activeProviders", 8);
        return "pages/admin";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "redirect:/admin";
    }


    static class AdminStats {
        public int todayAppointments = 12;
        public int totalRevenue = 2450;
        public int newCustomers = 8;
        public int pendingBookings = 5;
        public double occupancyRate = 85.5;
        public int activeProviders = 8;
        public double satisfaction = 4.8;

        public AdminStats() {
        }

        public int getTodayAppointments() { return todayAppointments; }
        public int getTotalRevenue() { return totalRevenue; }
        public int getNewCustomers() { return newCustomers; }
        public int getPendingBookings() { return pendingBookings; }
        public double getOccupancyRate() { return occupancyRate; }
        public int getActiveProviders() { return activeProviders; }
        public double getSatisfaction() { return satisfaction; }
    }
}
