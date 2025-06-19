package com.aggy.booking.Service;

import com.aggy.booking.Model.User;
import com.aggy.booking.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

  
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

   
    public User createUser(String username, String email, String password, 
                          String firstName, String lastName, User.Role role) {
        User user = new User(username, email, password, firstName, lastName);
        user.setRole(role);
        return createUser(user);
    }

   
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

   
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

  
    public User updateUser(User user) {
        return userRepository.save(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

  
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

  
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                updateUser(user);
                return true;
            }
        }
        return false;
    }

    public void setUserEnabled(Long userId, boolean enabled) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(enabled);
            updateUser(user);
        }
    }

    public User findByUsernameOrEmail(String username, String email) {
        Optional<User> user = userRepository.findByUsernameOrEmail(username, email);
        return user.orElse(null);
    }
    
    // ================= ADMIN MANAGEMENT METHODS =================
    
    // Get users with pagination
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    // Count methods for admin dashboard
    public int getTotalUsersCount() {
        return (int) userRepository.count();
    }
    
    public int getUsersCountByRole(User.Role role) {
        return (int) userRepository.countByRole(role);
    }
    
    public int getActiveUsersCount() {
        return (int) userRepository.countByEnabledTrue();
    }
    
    public int getNewUsersThisWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        return (int) userRepository.countByCreatedAtAfter(weekAgo);
    }
    
    // User management methods
    public void toggleUserStatus(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(!user.isEnabled());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    public void changeUserRole(Long userId, User.Role newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    // Analytics methods
    public Map<String, Object> getUserEngagementStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusMonths(1));
        
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("newUsersThisMonth", newUsersThisMonth);
        stats.put("userRetentionRate", totalUsers > 0 ? (activeUsers * 100.0 / totalUsers) : 0);
        
        // Get user registration trends (last 30 days)
        List<Map<String, Object>> registrationTrends = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDateTime startOfDay = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            int count = (int) userRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startOfDay.toLocalDate().toString());
            dayData.put("registrations", count);
            registrationTrends.add(dayData);
        }
        stats.put("registrationTrends", registrationTrends);
        
        return stats;
    }
    
    public Map<String, Object> getUserActivitySummary(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        List<User> newUsers = userRepository.findByCreatedAtBetween(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        summary.put("newUsers", newUsers.size());
        summary.put("totalActive", getActiveUsersCount());
        summary.put("totalUsers", getTotalUsersCount());
        summary.put("userDetails", newUsers);
        
        // Role breakdown
        Map<String, Integer> roleBreakdown = new HashMap<>();
        for (User.Role role : User.Role.values()) {
            roleBreakdown.put(role.name(), getUsersCountByRole(role));
        }
        summary.put("roleBreakdown", roleBreakdown);
        
        return summary;
    }
}
