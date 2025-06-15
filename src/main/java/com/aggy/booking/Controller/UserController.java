package com.aggy.booking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("userProfile", getSampleUserProfile());
        model.addAttribute("userStats", getSampleUserStats());
        return "pages/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UserProfile userProfile, Model model) {
        // Process profile update
        model.addAttribute("message", "Profile updated successfully!");
        return "redirect:/user/profile";
    }

    @GetMapping("/appointments")
    public String myAppointments(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("appointments", getSampleAppointments(status));
        model.addAttribute("selectedStatus", status);
        return "pages/my-appointments";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("stats", getSampleDashboardStats());
        model.addAttribute("recentAppointments", getSampleRecentAppointments());
        return "pages/dashboard";
    }

    // Sample data methods
    private UserProfile getSampleUserProfile() {
        UserProfile profile = new UserProfile();
        profile.firstName = "John";
        profile.lastName = "Doe";
        profile.email = "john.doe@example.com";
        profile.phoneNumber = "(555) 123-4567";
        profile.address = "123 Main St, Anytown, USA";
        profile.dateOfBirth = "1990-01-15";
        return profile;
    }

    private UserStats getSampleUserStats() {
        UserStats stats = new UserStats();
        stats.totalAppointments = 12;
        stats.upcomingAppointments = 2;
        stats.completedAppointments = 8;
        stats.cancelledAppointments = 2;
        return stats;
    }

    private Object getSampleAppointments(String status) {
        return java.util.Arrays.asList(
            new Appointment(1L, "Hair Cut", "John Smith", "2024-12-15", "2:00 PM", 60, "CONFIRMED", 45.00, "123 Salon Street"),
            new Appointment(2L, "Consultation", "Dr. Johnson", "2024-12-18", "10:00 AM", 30, "COMPLETED", 30.00, "456 Medical Center"),
            new Appointment(3L, "Color Treatment", "Maria Garcia", "2024-12-20", "1:00 PM", 120, "CONFIRMED", 120.00, "789 Beauty Plaza")
        );
    }

    private DashboardStats getSampleDashboardStats() {
        DashboardStats stats = new DashboardStats();
        stats.totalAppointments = 24;
        stats.upcomingAppointments = 3;
        stats.completedAppointments = 18;
        stats.cancelledAppointments = 3;
        return stats;
    }

    private Object getSampleRecentAppointments() {
        return java.util.Arrays.asList(
            new Appointment(1L, "Hair Cut", "John Smith", "2024-12-15", "2:00 PM", 60, "CONFIRMED", 45.00, "123 Salon Street"),
            new Appointment(2L, "Consultation", "Dr. Johnson", "2024-12-18", "10:00 AM", 30, "COMPLETED", 30.00, "456 Medical Center")
        );
    }

    // Data classes
    static class UserProfile {
        public String firstName;
        public String lastName;
        public String email;
        public String phoneNumber;
        public String address;
        public String dateOfBirth;
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
        
        public String getInitials() {
            return (firstName != null ? firstName.substring(0, 1) : "") + 
                   (lastName != null ? lastName.substring(0, 1) : "");
        }
    }

    static class UserStats {
        public int totalAppointments;
        public int upcomingAppointments;
        public int completedAppointments;
        public int cancelledAppointments;
    }

    static class Appointment {
        public Long id;
        public String service;
        public String provider;
        public String date;
        public String time;
        public int duration;
        public String status;
        public double price;
        public String location;
        public String notes;

        public Appointment(Long id, String service, String provider, String date, String time, 
                          int duration, String status, double price, String location) {
            this.id = id;
            this.service = service;
            this.provider = provider;
            this.date = date;
            this.time = time;
            this.duration = duration;
            this.status = status;
            this.price = price;
            this.location = location;
        }
    }

    static class DashboardStats {
        public int totalAppointments;
        public int upcomingAppointments;
        public int completedAppointments;
        public int cancelledAppointments;
    }
}