package com.aggy.booking.Controller;

import com.aggy.booking.Model.*;
import com.aggy.booking.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {

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

    @GetMapping
    public String bookingPage(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Book Appointment");
        model.addAttribute("services", serviceService.getActiveServices());
        model.addAttribute("providers", serviceProviderService.getActiveServiceProviders());
        return "user/booking";
    }

    @GetMapping("/available-slots")
    @ResponseBody
    public List<TimeSlot> getAvailableSlots(@RequestParam Long providerId,
            @RequestParam String date) {
        try {
            Optional<ServiceProvider> providerOpt = serviceProviderService.getServiceProviderById(providerId);

            if (providerOpt.isPresent()) {
                LocalDate localDate = LocalDate.parse(date);
                return timeSlotService.getAvailableTimeSlots(providerOpt.get(), localDate);
            }
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public String createBooking(@RequestParam Long serviceId,
            @RequestParam Long timeSlotId,
            @RequestParam(required = false) String notes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "error:Not authenticated";
        }

        try {
            Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
            Optional<TimeSlot> timeSlotOpt = timeSlotService.getTimeSlotById(timeSlotId);

            if (serviceOpt.isPresent() && timeSlotOpt.isPresent()) {
                Service service = serviceOpt.get();
                TimeSlot timeSlot = timeSlotOpt.get();

                if (!timeSlot.getIsAvailable()) {
                    return "error:Time slot is no longer available";
                }

                Appointment appointment = appointmentService.bookAppointment(
                        currentUser, service, timeSlot, notes != null ? notes : "");

                return "success:Appointment booked successfully with ID: " + appointment.getId();
            }

            return "error:Service or time slot not found";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @GetMapping("/providers/{serviceId}")
    @ResponseBody
    public List<ServiceProvider> getProvidersForService(@PathVariable Long serviceId) {
        try {
            Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
            if (serviceOpt.isPresent()) {
                // For now, return all active providers
                return serviceProviderService.getActiveServiceProviders();
            }
            return java.util.Collections.emptyList();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    @GetMapping("/services/categories")
    @ResponseBody
    public List<String> getServiceCategories() {
        try {
            return serviceService.getAllCategories();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    @GetMapping("/services/category/{category}")
    @ResponseBody
    public List<Service> getServicesByCategory(@PathVariable String category) {
        try {
            return serviceService.getServicesByCategory(category);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    @GetMapping("/confirmation/{appointmentId}")
    public String bookingConfirmation(@PathVariable Long appointmentId, Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();

            // Verify that the appointment belongs to the current user
            if (!appointment.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/user/dashboard";
            }

            model.addAttribute("pageTitle", "Booking Confirmation");
            model.addAttribute("appointment", appointment);
            return "user/booking-confirmation";
        }

        return "redirect:/user/dashboard";
    }

    // Utility endpoint to generate time slots for testing
    @PostMapping("/generate-slots")
    @ResponseBody
    public String generateTimeSlots(@RequestParam Long providerId,
            @RequestParam String date,
            @RequestParam(defaultValue = "60") int intervalMinutes,
            @RequestParam(defaultValue = "9") int startHour,
            @RequestParam(defaultValue = "17") int endHour) {
        try {
            Optional<ServiceProvider> providerOpt = serviceProviderService.getServiceProviderById(providerId);
            if (providerOpt.isPresent()) {
                LocalDate localDate = LocalDate.parse(date);
                List<TimeSlot> slots = timeSlotService.generateDailyTimeSlots(
                        providerOpt.get(), localDate, intervalMinutes, startHour, endHour);
                return "success:Generated " + slots.size() + " time slots";
            }
            return "error:Provider not found";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
}