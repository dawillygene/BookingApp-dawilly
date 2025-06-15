package com.aggy.booking.Controller;

import com.aggy.booking.Model.User;
import com.aggy.booking.Model.Appointment;
import com.aggy.booking.Model.AppointmentStatus;
import com.aggy.booking.Service.AppointmentService;
import com.aggy.booking.Service.UserService;
import com.aggy.booking.Service.ServiceService;
import com.aggy.booking.Service.ServiceProviderService;
import com.aggy.booking.Service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private TimeSlotService timeSlotService;

    // Helper method to get current user
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Optional<User> user = userService.findByUsername(auth.getName());
            return user.orElse(null);
        }
        return null;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("userProfile", currentUser);
        model.addAttribute("userStats", getUserStatsForUser(currentUser));
        return "pages/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String phoneNumber,
                              @RequestParam String address,
                              @RequestParam String dateOfBirth,
                              Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setPhoneNumber(phoneNumber);
            currentUser.setAddress(address);
            currentUser.setDateOfBirth(dateOfBirth);
            userService.updateUser(currentUser);
            
            model.addAttribute("message", "Profile updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/user/profile";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        AppointmentService.AppointmentStats stats = appointmentService.getUserAppointmentStats(currentUser);
        List<Appointment> recentAppointments = appointmentService.getRecentAppointments(currentUser, 5);
        
        model.addAttribute("pageTitle", "User Dashboard");
        model.addAttribute("userName", currentUser.getFullName());
        model.addAttribute("upcomingCount", stats.getUpcomingCount());
        model.addAttribute("totalCount", stats.getTotalCount());
        model.addAttribute("completedCount", stats.getCompletedCount());
        model.addAttribute("pendingCount", stats.getPendingCount());
        model.addAttribute("stats", stats);
        model.addAttribute("recentAppointments", recentAppointments);
        return "user/dashboard";
    }

    @GetMapping("/booking")
    public String booking(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("pageTitle", "Book Appointment");
        model.addAttribute("services", serviceService.getActiveServices());
        model.addAttribute("providers", serviceProviderService.getActiveServiceProviders());
        return "user/booking";
    }

    @GetMapping("/appointments")
    public String appointments(@RequestParam(required = false) String status, Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        List<Appointment> appointments;
        if (status != null && !status.isEmpty()) {
            try {
                AppointmentStatus statusEnum = AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointmentService.getUserAppointmentsByStatus(currentUser, statusEnum);
            } catch (IllegalArgumentException e) {
                appointments = appointmentService.getUserAppointments(currentUser);
            }
        } else {
            appointments = appointmentService.getUserAppointments(currentUser);
        }
        
        model.addAttribute("pageTitle", "My Appointments");
        model.addAttribute("appointments", appointments);
        model.addAttribute("selectedStatus", status);
        return "user/appointments";
    }

    // API Endpoints for AJAX calls
    @PostMapping("/appointments/{id}/cancel")
    @ResponseBody
    public String cancelAppointment(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "error:Not authenticated";
        }
        
        try {
            Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                if (!appointment.getUser().getId().equals(currentUser.getId())) {
                    return "error:Unauthorized";
                }
                appointmentService.cancelAppointment(id);
                return "success:Appointment cancelled successfully";
            }
            return "error:Appointment not found";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/appointments/{id}/reschedule")
    @ResponseBody
    public String rescheduleAppointment(@PathVariable Long id, @RequestParam Long newTimeSlotId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "error:Not authenticated";
        }
        
        try {
            Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                if (!appointment.getUser().getId().equals(currentUser.getId())) {
                    return "error:Unauthorized";
                }
                
                Optional<com.aggy.booking.Model.TimeSlot> timeSlotOpt = timeSlotService.getTimeSlotById(newTimeSlotId);
                if (timeSlotOpt.isPresent()) {
                    appointmentService.rescheduleAppointment(id, timeSlotOpt.get());
                    return "success:Appointment rescheduled successfully";
                }
                return "error:Time slot not found";
            }
            return "error:Appointment not found";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    // Helper method to get user stats
    private UserStats getUserStatsForUser(User user) {
        AppointmentService.AppointmentStats stats = appointmentService.getUserAppointmentStats(user);
        UserStats userStats = new UserStats();
        userStats.totalAppointments = stats.getTotalCount().intValue();
        userStats.upcomingAppointments = stats.getUpcomingCount().intValue();
        userStats.completedAppointments = stats.getCompletedCount().intValue();
        userStats.cancelledAppointments = stats.getCancelledCount().intValue();
        return userStats;
    }

    // Data classes
    static class UserStats {
        public int totalAppointments;
        public int upcomingAppointments;
        public int completedAppointments;
        public int cancelledAppointments;
    }
}