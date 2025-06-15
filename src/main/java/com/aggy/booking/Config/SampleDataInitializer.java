package com.aggy.booking.Config;

import com.aggy.booking.Model.*;
import com.aggy.booking.Repository.*;
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

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no data exists
        if (serviceRepository.count() == 0) {
            initializeServices();
            initializeServiceProviders();
            initializeTimeSlots();
            System.out.println("Sample data initialized successfully!");
        }
    }

    private void initializeServices() {
        Service haircut = new Service();
        haircut.setName("Hair Cut & Styling");
        haircut.setDescription("Professional hair cutting and styling service");
        haircut.setCategory("Hair Care");
        haircut.setPrice(45.0);
        haircut.setDurationMinutes(60);
        haircut.setIsActive(true);
        haircut.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(haircut);

        Service massage = new Service();
        massage.setName("Relaxing Massage");
        massage.setDescription("Full body relaxing massage therapy");
        massage.setCategory("Wellness");
        massage.setPrice(75.0);
        massage.setDurationMinutes(90);
        massage.setIsActive(true);
        massage.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(massage);

        Service manicure = new Service();
        manicure.setName("Manicure & Nail Art");
        manicure.setDescription("Professional manicure with nail art options");
        manicure.setCategory("Beauty");
        manicure.setPrice(35.0);
        manicure.setDurationMinutes(45);
        manicure.setIsActive(true);
        manicure.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(manicure);

        Service consultation = new Service();
        consultation.setName("Health Consultation");
        consultation.setDescription("General health and wellness consultation");
        consultation.setCategory("Healthcare");
        consultation.setPrice(120.0);
        consultation.setDurationMinutes(30);
        consultation.setIsActive(true);
        consultation.setCreatedAt(LocalDateTime.now());
        serviceRepository.save(consultation);
    }

    private void initializeServiceProviders() {
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
        serviceProviderRepository.save(sarah);

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
        serviceProviderRepository.save(michael);

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
        serviceProviderRepository.save(emma);

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
        serviceProviderRepository.save(david);
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
