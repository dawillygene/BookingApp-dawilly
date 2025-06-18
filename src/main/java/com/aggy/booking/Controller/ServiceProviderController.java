package com.aggy.booking.Controller;

import com.aggy.booking.Model.*;
import com.aggy.booking.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/provider")
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private UserService userService;

    private ServiceProvider getCurrentServiceProvider() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userService.findByUsernameOrEmail(username, username);
            if (user != null && user.getRole() == User.Role.PROVIDER) {
                return serviceProviderService.findByEmail(user.getEmail());
            }
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        // Dashboard statistics
        List<Appointment> todayAppointments = appointmentService.getTodayAppointmentsByProvider(provider);
        List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointmentsByProvider(provider);
        List<Appointment> allAppointments = appointmentService.getAppointmentsByProvider(provider);

        model.addAttribute("provider", provider);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("totalAppointments", allAppointments.size());
        model.addAttribute("todayCount", todayAppointments.size());
        model.addAttribute("upcomingCount", upcomingAppointments.size());
        model.addAttribute("pageTitle", "Provider Dashboard");

        return "serviceprovider/dashboard";
    }

    @GetMapping("/services")
    public String services(Model model) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        List<Service> allServices = serviceService.getActiveServices();
        model.addAttribute("provider", provider);
        model.addAttribute("services", allServices);
        model.addAttribute("pageTitle", "Manage Services");

        return "serviceprovider/services";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        List<Appointment> allAppointments = appointmentService.getAppointmentsByProvider(provider);
        List<Appointment> todayAppointments = appointmentService.getTodayAppointmentsByProvider(provider);
        List<Service> services = serviceService.getActiveServices();
        
        model.addAttribute("provider", provider);
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("services", services);
        model.addAttribute("pageTitle", "My Appointments");

        return "serviceprovider/appointments";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        List<TimeSlot> timeSlots = timeSlotService.getTimeSlotsByProvider(provider);
        List<Service> services = serviceService.getActiveServices();
        
        model.addAttribute("provider", provider);
        model.addAttribute("timeSlots", timeSlots);
        model.addAttribute("services", services);
        model.addAttribute("pageTitle", "My Schedule");

        return "serviceprovider/schedule";
    }

    @PostMapping("/schedule/add")
    public String addTimeSlot(@RequestParam Long serviceId,
                              @RequestParam String startTime,
                              @RequestParam String endTime,
                              RedirectAttributes redirectAttributes) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        try {
            Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
            if (serviceOpt.isPresent()) {
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setProvider(provider);
                timeSlot.setStartTime(LocalDateTime.parse(startTime));
                timeSlot.setEndTime(LocalDateTime.parse(endTime));
                timeSlot.setIsAvailable(true);

                timeSlotService.save(timeSlot);
                redirectAttributes.addFlashAttribute("message", "Time slot added successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Service not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding time slot: " + e.getMessage());
        }

        return "redirect:/provider/schedule";
    }

    @PostMapping("/schedule/edit/{id}")
    public String editTimeSlot(@PathVariable Long id,
                               @RequestParam Long serviceId,
                               @RequestParam String startTime,
                               @RequestParam String endTime,
                               RedirectAttributes redirectAttributes) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        try {
            Optional<TimeSlot> timeSlotOpt = timeSlotService.findById(id);
            Optional<Service> serviceOpt = serviceService.getServiceById(serviceId);
            
            if (timeSlotOpt.isPresent() && serviceOpt.isPresent()) {
                TimeSlot timeSlot = timeSlotOpt.get();
                
                // Verify the time slot belongs to this provider
                if (!timeSlot.getProvider().getId().equals(provider.getId())) {
                    redirectAttributes.addFlashAttribute("error", "Not authorized to edit this time slot");
                    return "redirect:/provider/schedule";
                }

                timeSlot.setStartTime(LocalDateTime.parse(startTime));
                timeSlot.setEndTime(LocalDateTime.parse(endTime));

                timeSlotService.save(timeSlot);
                redirectAttributes.addFlashAttribute("message", "Time slot updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Time slot or service not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating time slot: " + e.getMessage());
        }

        return "redirect:/provider/schedule";
    }

    @PostMapping("/schedule/delete/{id}")
    public String deleteTimeSlot(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        try {
            Optional<TimeSlot> timeSlotOpt = timeSlotService.findById(id);
            if (timeSlotOpt.isPresent()) {
                TimeSlot timeSlot = timeSlotOpt.get();
                
                // Verify the time slot belongs to this provider
                if (!timeSlot.getProvider().getId().equals(provider.getId())) {
                    redirectAttributes.addFlashAttribute("error", "Not authorized to delete this time slot");
                    return "redirect:/provider/schedule";
                }

                timeSlotService.deleteTimeSlot(id);
                redirectAttributes.addFlashAttribute("message", "Time slot deleted successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Time slot not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting time slot: " + e.getMessage());
        }

        return "redirect:/provider/schedule";
    }

    @PostMapping("/appointment/{id}/status")
    @ResponseBody
    public String updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "error:Not authorized";
        }

        try {
            Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                
                // Verify the appointment belongs to this provider
                if (!appointment.getProvider().getId().equals(provider.getId())) {
                    return "error:Not authorized to update this appointment";
                }

                appointment.setStatus(AppointmentStatus.valueOf(status.toUpperCase()));
                appointmentService.updateAppointment(appointment);
                
                return "success:Appointment status updated successfully";
            }
            return "error:Appointment not found";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        // Calculate statistics
        List<Appointment> allAppointments = appointmentService.getAppointmentsByProvider(provider);
        List<Service> allServices = serviceService.getActiveServices(); // You may want to filter by provider

        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalServices", allServices.size());
        stats.put("totalAppointments", allAppointments.size());
        stats.put("completedAppointments", (int) allAppointments.stream()
                .filter(Appointment::isCompleted).count());
        stats.put("upcomingAppointments", (int) allAppointments.stream()
                .filter(Appointment::isUpcoming).count());

        model.addAttribute("provider", provider);
        model.addAttribute("stats", stats);
        model.addAttribute("pageTitle", "My Profile");

        return "serviceprovider/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute ServiceProvider provider,
                              RedirectAttributes redirectAttributes) {
        ServiceProvider currentProvider = getCurrentServiceProvider();
        if (currentProvider == null) {
            return "redirect:/login";
        }

        try {
            // Update only the fields that can be changed
            currentProvider.setFirstName(provider.getFirstName());
            currentProvider.setLastName(provider.getLastName());
            currentProvider.setEmail(provider.getEmail());
            currentProvider.setPhone(provider.getPhone());
            currentProvider.setSpecialization(provider.getSpecialization());
            currentProvider.setBio(provider.getBio());
            currentProvider.setAddress(provider.getAddress());

            serviceProviderService.saveServiceProvider(currentProvider);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/provider/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        ServiceProvider provider = getCurrentServiceProvider();
        if (provider == null) {
            return "redirect:/login";
        }

        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                return "redirect:/provider/profile";
            }

            boolean success = userService.changePassword(provider.getUser().getUsername(), 
                                                       currentPassword, newPassword);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Password changed successfully");
            } else {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
        }

        return "redirect:/provider/profile";
    }
}
