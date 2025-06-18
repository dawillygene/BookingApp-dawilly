package com.aggy.booking.Config;

import com.aggy.booking.Model.*;
import com.aggy.booking.Repository.*;
import com.aggy.booking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no data exists
        if (serviceRepository.count() == 0) {
            initializeServiceProviders(); // Initialize providers first
            initializeServices(); // Then initialize services with provider relationships
            initializeTimeSlots();
            System.out.println("Sample data initialized successfully!");
        }
    }

    private void initializeServices() {
        // Get the providers we just created
        ServiceProvider sarah = serviceProviderRepository.findByEmail("sarah.johnson@example.com");
        ServiceProvider michael = serviceProviderRepository.findByEmail("michael.chen@example.com");
        ServiceProvider emma = serviceProviderRepository.findByEmail("emma.davis@example.com");
        ServiceProvider david = serviceProviderRepository.findByEmail("david.wilson@example.com");

        // Sarah's Hair Care Services
        Service haircut = new Service();
        haircut.setName("Hair Cut & Styling");
        haircut.setDescription("Professional hair cutting and styling service");
        haircut.setCategory("Hair Care");
        haircut.setPrice(45.0);
        haircut.setDurationMinutes(60);
        haircut.setProvider(sarah);
        haircut.setIsActive(true);
        haircut.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(haircut);

        // Michael's Wellness Services
        Service massage = new Service();
        massage.setName("Relaxing Massage");
        massage.setDescription("Full body relaxing massage therapy");
        massage.setCategory("Wellness");
        massage.setPrice(75.0);
        massage.setDurationMinutes(90);
        massage.setProvider(michael);
        massage.setIsActive(true);
        massage.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(massage);

        // Emma's Beauty Services
        Service manicure = new Service();
        manicure.setName("Manicure & Nail Art");
        manicure.setDescription("Professional manicure with nail art options");
        manicure.setCategory("Beauty");
        manicure.setPrice(35.0);
        manicure.setDurationMinutes(45);
        manicure.setProvider(emma);
        manicure.setIsActive(true);
        manicure.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(manicure);

        // David's Healthcare Services
        Service consultation = new Service();
        consultation.setName("Health Consultation");
        consultation.setDescription("General health and wellness consultation");
        consultation.setCategory("Healthcare");
        consultation.setPrice(120.0);
        consultation.setDurationMinutes(30);
        consultation.setProvider(david);
        consultation.setIsActive(true);
        consultation.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(consultation);
    }

    private void initializeServiceProviders() {
        // Check if we need to create provider user accounts
        if (!userService.existsByEmail("sarah.johnson@example.com")) {
            // Create Sarah's user account
            User sarahUser = new User();
            sarahUser.setUsername("sarah");
            sarahUser.setEmail("sarah.johnson@example.com");
            sarahUser.setPassword("sarah123");
            sarahUser.setFirstName("Sarah");
            sarahUser.setLastName("Johnson");
            sarahUser.setRole(User.Role.PROVIDER);
            sarahUser.setEnabled(true);
            User savedSarahUser = userService.createUser(sarahUser);

            ServiceProvider sarah = new ServiceProvider();
            sarah.setFirstName("Sarah");
            sarah.setLastName("Johnson");
            sarah.setEmail("sarah.johnson@example.com");
            sarah.setPhone("+1-555-0101");
            sarah.setSpecialization("Hair Styling & Color");
            sarah.setLocation("Downtown Studio");
            sarah.setBio("Professional hair stylist with 8+ years of experience in modern cutting and coloring techniques.");
            sarah.setIsActive(true);
            sarah.setCreatedAt(LocalDateTime.now());
            sarah.setUser(savedSarahUser);
            serviceProviderRepository.save(sarah);
            System.out.println("Created provider: sarah/sarah123");
        } else {
            System.out.println("Provider account already exists - skipping creation");
        }

        if (!userService.existsByEmail("michael.chen@example.com")) {
            // Create Michael's user account
            User michaelUser = new User();
            michaelUser.setUsername("michael");
            michaelUser.setEmail("michael.chen@example.com");
            michaelUser.setPassword("michael123");
            michaelUser.setFirstName("Michael");
            michaelUser.setLastName("Chen");
            michaelUser.setRole(User.Role.PROVIDER);
            michaelUser.setEnabled(true);
            User savedMichaelUser = userService.createUser(michaelUser);

            ServiceProvider michael = new ServiceProvider();
            michael.setFirstName("Michael");
            michael.setLastName("Chen");
            michael.setEmail("michael.chen@example.com");
            michael.setPhone("+1-555-0102");
            michael.setSpecialization("Massage Therapy");
            michael.setLocation("Wellness Center");
            michael.setBio("Licensed massage therapist specializing in Swedish and deep tissue massage techniques.");
            michael.setIsActive(true);
            michael.setCreatedAt(LocalDateTime.now());
            michael.setUser(savedMichaelUser);
            serviceProviderRepository.save(michael);
            System.out.println("Created provider: michael/michael123");
        }

        if (!userService.existsByEmail("emma.davis@example.com")) {
            // Create Emma's user account
            User emmaUser = new User();
            emmaUser.setUsername("emma");
            emmaUser.setEmail("emma.davis@example.com");
            emmaUser.setPassword("emma123");
            emmaUser.setFirstName("Emma");
            emmaUser.setLastName("Davis");
            emmaUser.setRole(User.Role.PROVIDER);
            emmaUser.setEnabled(true);
            User savedEmmaUser = userService.createUser(emmaUser);

            ServiceProvider emma = new ServiceProvider();
            emma.setFirstName("Emma");
            emma.setLastName("Davis");
            emma.setEmail("emma.davis@example.com");
            emma.setPhone("+1-555-0103");
            emma.setSpecialization("Beauty & Nail Care");
            emma.setLocation("Beauty Salon");
            emma.setBio("Certified nail technician and beauty specialist with expertise in nail art and skincare.");
            emma.setIsActive(true);
            emma.setCreatedAt(LocalDateTime.now());
            emma.setUser(savedEmmaUser);
            serviceProviderRepository.save(emma);
            System.out.println("Created provider: emma/emma123");
        }

        if (!userService.existsByEmail("david.wilson@example.com")) {
            // Create David's user account
            User davidUser = new User();
            davidUser.setUsername("david");
            davidUser.setEmail("david.wilson@example.com");
            davidUser.setPassword("david123");
            davidUser.setFirstName("Dr. David");
            davidUser.setLastName("Wilson");
            davidUser.setRole(User.Role.PROVIDER);
            davidUser.setEnabled(true);
            User savedDavidUser = userService.createUser(davidUser);

            ServiceProvider david = new ServiceProvider();
            david.setFirstName("Dr. David");
            david.setLastName("Wilson");
            david.setEmail("david.wilson@example.com");
            david.setPhone("+1-555-0104");
            david.setSpecialization("General Practice");
            david.setLocation("Medical Center");
            david.setBio("Board-certified physician with 15+ years of experience in family medicine and preventive care.");
            david.setIsActive(true);
            david.setCreatedAt(LocalDateTime.now());
            david.setUser(savedDavidUser);
            serviceProviderRepository.save(david);
            System.out.println("Created provider: david/david123");
        }
    }

    private void initializeTimeSlots() {
        List<ServiceProvider> providers = serviceProviderRepository.findAll();
        
        // Create time slots for the next 7 days
        for (int day = 0; day < 7; day++) {
            LocalDate date = LocalDate.now().plusDays(day);
            
            for (ServiceProvider provider : providers) {
                // Create time slots from 9 AM to 5 PM (every 30 minutes)
                for (int hour = 9; hour < 17; hour++) {
                    for (int minute = 0; minute < 60; minute += 30) {
                        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
                        
                        TimeSlot timeSlot = new TimeSlot();
                        timeSlot.setProvider(provider);
                        timeSlot.setStartTime(startTime);
                        timeSlot.setEndTime(startTime.plusMinutes(30));
                        timeSlot.setIsAvailable(true);
                        timeSlot.setCreatedAt(LocalDateTime.now());
                        timeSlotRepository.save(timeSlot);
                    }
                }
            }
        }
    }
}
