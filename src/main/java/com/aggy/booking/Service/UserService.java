package com.aggy.booking.Service;

import com.aggy.booking.Model.User;
import com.aggy.booking.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
