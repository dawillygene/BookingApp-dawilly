package com.aggy.booking.Repository;

import com.aggy.booking.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by username or email
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Admin management methods
    
    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(User.Role role, Pageable pageable);
    
    /**
     * Find users by role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Count users by role
     */
    long countByRole(User.Role role);
    
    /**
     * Count enabled users
     */
    long countByEnabledTrue();
    
    /**
     * Count users created after a specific date
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Count users created between dates
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find users created between dates
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find users with pagination and optional search
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role)")
    Page<User> findUsersWithSearch(@Param("search") String search, 
                                   @Param("role") User.Role role, 
                                   Pageable pageable);
}
