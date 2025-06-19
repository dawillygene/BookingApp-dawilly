package com.aggy.booking.Repository;

import com.aggy.booking.Model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    
    // Find active providers
    List<ServiceProvider> findByIsActiveTrueOrderByFirstNameAscLastNameAsc();
    
    // Find providers by specialization
    List<ServiceProvider> findBySpecializationAndIsActiveTrueOrderByFirstNameAscLastNameAsc(String specialization);
    
    // Find providers by name (case insensitive)
    @Query("SELECT sp FROM ServiceProvider sp WHERE (LOWER(sp.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(sp.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND sp.isActive = true")
    List<ServiceProvider> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find providers by location
    List<ServiceProvider> findByLocationAndIsActiveTrueOrderByFirstNameAscLastNameAsc(String location);
    
    // Get all distinct specializations
    @Query("SELECT DISTINCT sp.specialization FROM ServiceProvider sp WHERE sp.specialization IS NOT NULL AND sp.isActive = true ORDER BY sp.specialization")
    List<String> findDistinctSpecializations();
    
    // Get all distinct locations
    @Query("SELECT DISTINCT sp.location FROM ServiceProvider sp WHERE sp.location IS NOT NULL AND sp.isActive = true ORDER BY sp.location")
    List<String> findDistinctLocations();
    
    // Count active providers
    Long countByIsActiveTrue();
    
    // Count inactive providers
    Long countByIsActiveFalse();

    // Find provider by email
    ServiceProvider findByEmail(String email);
    
    // ================= ADMIN PAGINATION METHODS =================
    
    // Find by status with pagination
    Page<ServiceProvider> findByIsActiveTrue(Pageable pageable);
    Page<ServiceProvider> findByIsActiveFalse(Pageable pageable);
    
    // Find by date range
    List<ServiceProvider> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Advanced search with pagination
    @Query("SELECT sp FROM ServiceProvider sp WHERE " +
           "(:name IS NULL OR LOWER(sp.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(sp.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:specialization IS NULL OR sp.specialization = :specialization) AND " +
           "(:location IS NULL OR sp.location = :location) AND " +
           "(:isActive IS NULL OR sp.isActive = :isActive)")
    Page<ServiceProvider> searchProviders(@Param("name") String name, 
                                        @Param("specialization") String specialization,
                                        @Param("location") String location, 
                                        @Param("isActive") Boolean isActive, 
                                        Pageable pageable);
}
