package com.aggy.booking.Config;

import com.aggy.booking.Model.User;
import com.aggy.booking.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {

        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@admin.com");
            admin.setPassword("admin");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            admin.setEnabled(true);
            
            userService.createUser(admin);
            System.out.println("Created default admin user: admin/admin123");
        }


        if (!userService.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@user.com");
            user.setPassword("user");
            user.setFirstName("Regular");
            user.setLastName("User");
            user.setRole(User.Role.USER);
            user.setEnabled(true);
            
            userService.createUser(user);
            System.out.println("Created default regular user: user/password");
        }
    }
}
