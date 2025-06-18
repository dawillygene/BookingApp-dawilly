package com.aggy.booking.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/","/booking/**", "/home", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/provider/**").hasRole("PROVIDER")
                        .anyRequest().authenticated() 
                )
                .formLogin(form -> form
                        .loginPage("/login")        
                        .permitAll()            
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error") 
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")      
                        .logoutSuccessUrl("/login?logout")
                )
                .csrf(csrf -> csrf.disable()) 
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.deny())
                ); 
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}