package com.aggy.booking.Config;

import com.aggy.booking.Model.*;
import com.aggy.booking.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create sample provider account if it doesn't exist
        if (!userService.existsByEmail("provider@provider.com")) {
            createSampleProvider();
        } else {
            System.out.println("Provider account already exists - skipping creation");
        }

        // Create sample services if they don't exist
        if (serviceService.getAllServices().isEmpty()) {
            createSampleServices();
        } else {
            System.out.println("Services already exist - skipping creation");
        }
    }

    private void createSampleProvider() {
        try {
            // Check if user already exists by username as well
            if (userService.existsByUsername("provider")) {
                System.out.println("Provider user already exists with username 'provider'");
                return;
            }

            // Create user account for provider
            User providerUser = new User();
            providerUser.setUsername("provider");
            providerUser.setEmail("provider@provider.com");
            providerUser.setPassword(passwordEncoder.encode("provider"));
            providerUser.setFirstName("Dr. John");
            providerUser.setLastName("Smith");
            providerUser.setRole(User.Role.PROVIDER);
            providerUser.setEnabled(true);
            providerUser.setCreatedAt(LocalDateTime.now());

            User savedUser = userService.updateUser(providerUser);

            // Create service provider profile
            ServiceProvider provider = new ServiceProvider();
            provider.setFirstName("Dr. John");
            provider.setLastName("Smith");
            provider.setEmail("provider@provider.com");
            provider.setPhone("+1 (555) 123-4567");
            provider.setSpecialization("General Medicine");
            provider.setBio("Experienced physician with over 10 years of practice in general medicine. " +
                          "Dedicated to providing quality healthcare and building lasting relationships with patients.");
            provider.setLocation("Downtown Medical Center");
            provider.setAddress("123 Healthcare Ave, Medical District, City 12345");
            provider.setIsActive(true);
            provider.setUser(savedUser);
            provider.setCreatedAt(LocalDateTime.now());

            serviceProviderService.saveServiceProvider(provider);

            System.out.println("=== Sample provider account created successfully ===");
            System.out.println("Username: provider");
            System.out.println("Password: provider");
            System.out.println("Email: provider@provider.com");
            System.out.println("Login URL: http://localhost:8080/login");
            System.out.println("Provider Dashboard: http://localhost:8080/provider/dashboard");
            System.out.println("================================================");
        } catch (Exception e) {
            System.err.println("Error creating sample provider: " + e.getMessage());
            System.err.println("This might be due to a database schema issue with the role column.");
            System.err.println("Please run the database_fix.sql script to update the role column:");
            System.err.println("mysql -u venlit -p BookingAppAggy < database_fix.sql");
            e.printStackTrace();
        }
    }

    private void createSampleServices() {
        try {
            // General Consultation
            Service consultation = serviceService.createService(
                "General Consultation",
                "Comprehensive medical consultation for general health concerns",
                "Medical",
                75.00,
                30
            );

            // Physical Examination
            Service physicalExam = serviceService.createService(
                "Physical Examination", 
                "Complete physical examination and health assessment",
                "Medical",
                100.00,
                45
            );

            // Follow-up Visit
            Service followUp = serviceService.createService(
                "Follow-up Visit",
                "Follow-up consultation for ongoing treatment", 
                "Medical",
                50.00,
                20
            );

            System.out.println("Sample services created successfully");
        } catch (Exception e) {
            System.err.println("Error creating sample services: " + e.getMessage());
        }
    }
}
