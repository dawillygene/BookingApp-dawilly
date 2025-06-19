package com.aggy.booking.Repository;

import com.aggy.booking.Model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    
    /**
     * Find active categories ordered by name
     */
    List<ServiceCategory> findByIsActiveTrueOrderByName();
    
    /**
     * Count active categories
     */
    long countByIsActiveTrue();
    
    /**
     * Check if category name exists
     */
    boolean existsByName(String name);
    
    /**
     * Find category by name
     */
    Optional<ServiceCategory> findByName(String name);
}
