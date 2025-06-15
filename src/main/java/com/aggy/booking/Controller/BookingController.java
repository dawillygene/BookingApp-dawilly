package com.aggy.booking.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @GetMapping
    public String bookingPage(Model model) {
        model.addAttribute("pageTitle", "Book Appointment");
        // Add the missing bookingForm object
        model.addAttribute("bookingForm", new BookingForm());
        // Add sample data for demo purposes
        model.addAttribute("services", getSampleServices());
        model.addAttribute("providers", getSampleProviders());
        model.addAttribute("availableSlots", getSampleTimeSlots());
        return "pages/booking";
    }

    @PostMapping("/confirm")
    public String confirmBooking(@ModelAttribute BookingForm bookingForm, Model model) {
        // Process booking confirmation
        model.addAttribute("message", "Booking confirmed successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/filter")
    public String filterSlots(@RequestParam(required = false) String date,
                             @RequestParam(required = false) String service,
                             @RequestParam(required = false) String provider,
                             Model model) {
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedService", service);
        model.addAttribute("selectedProvider", provider);
        // Add the missing bookingForm object for the filter view as well
        model.addAttribute("bookingForm", new BookingForm());
        model.addAttribute("services", getSampleServices());
        model.addAttribute("providers", getSampleProviders());
        // Filter and return updated slots
        model.addAttribute("availableSlots", getFilteredTimeSlots(date, service, provider));
        return "pages/booking";
    }

    // Sample data methods for demo
    private Object getSampleServices() {
        return java.util.Arrays.asList(
            new Service(1L, "Hair Cut", 45, 45.00),
            new Service(2L, "Hair Styling", 60, 65.00),
            new Service(3L, "Consultation", 30, 30.00),
            new Service(4L, "Color Treatment", 120, 120.00)
        );
    }

    private Object getSampleProviders() {
        return java.util.Arrays.asList(
            new Provider(1L, "John Smith", "Hair Stylist"),
            new Provider(2L, "Dr. Johnson", "Consultant"),
            new Provider(3L, "Maria Garcia", "Color Specialist"),
            new Provider(4L, "David Lee", "Senior Stylist")
        );
    }

    private Object getSampleTimeSlots() {
        return java.util.Arrays.asList(
            new TimeSlot(1L, "9:00 AM", true, "Hair Cut", "John Smith"),
            new TimeSlot(2L, "10:00 AM", false, "Consultation", "Dr. Johnson"),
            new TimeSlot(3L, "11:00 AM", true, "Hair Styling", "Maria Garcia"),
            new TimeSlot(4L, "2:00 PM", true, "Color Treatment", "David Lee")
        );
    }

    private Object getFilteredTimeSlots(String date, String service, String provider) {
        // Return filtered time slots based on criteria
        return getSampleTimeSlots();
    }

    // Inner classes for demo data
    static class Service {
        public Long id;
        public String name;
        public int duration;
        public double price;

        public Service(Long id, String name, int duration, double price) {
            this.id = id;
            this.name = name;
            this.duration = duration;
            this.price = price;
        }
    }

    static class Provider {
        public Long id;
        public String name;
        public String specialty;

        public Provider(Long id, String name, String specialty) {
            this.id = id;
            this.name = name;
            this.specialty = specialty;
        }
    }

    static class TimeSlot {
        public Long id;
        public String time;
        public boolean available;
        public String service;
        public String provider;

        public TimeSlot(Long id, String time, boolean available, String service, String provider) {
            this.id = id;
            this.time = time;
            this.available = available;
            this.service = service;
            this.provider = provider;
        }
    }

    static class BookingForm {
        public Long slotId;
        public String fullName;
        public String email;
        public String phoneNumber;
        public String notes;

        // Default constructor
        public BookingForm() {}

        // Getters and setters for Spring form binding
        public Long getSlotId() { return slotId; }
        public void setSlotId(Long slotId) { this.slotId = slotId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}