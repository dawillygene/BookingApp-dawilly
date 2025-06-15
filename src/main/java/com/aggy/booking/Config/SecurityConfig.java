package com.aggy.booking.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/","/booking/**", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login")        // Specify your custom login page URL
                        .permitAll()                // Allow everyone to access the login page
                        .defaultSuccessUrl("/home", true) // Redirect to /home after successful login
                        .failureUrl("/login?error") // Redirect to /login?error on login failure
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")       // Spring Security's default logout URL
                        .logoutSuccessUrl("/login?logout") // Redirect to login page with logout message
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF for H2 console and development
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.deny())
                ); // Allow H2 console frames
        return http.build();
    }

    // --- User Management (For testing, use real database in production) ---

    @Bean
    public UserDetailsService userDetailsService() {
        // Define some in-memory users for testing
        // ALWAYS encode passwords for real applications!
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password")) // "password" is encoded
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123")) // "admin123" is encoded
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Recommended password encoder
    }
}