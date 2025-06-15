package com.aggy.booking.Repository;

import com.aggy.booking.Model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
