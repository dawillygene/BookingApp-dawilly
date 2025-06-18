package com.aggy.booking.Repository;

import com.aggy.booking.Model.Service;
import com.aggy.booking.Model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    // Find active services
    List<Service> findByIsActiveTrueOrderByName();
    
    // Find services by category
    List<Service> findByCategoryAndIsActiveTrueOrderByName(String category);
    
    // Find services by name (case insensitive)
    @Query("SELECT s FROM Service s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.isActive = true")
    List<Service> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find services by price range
    @Query("SELECT s FROM Service s WHERE s.price BETWEEN :minPrice AND :maxPrice AND s.isActive = true ORDER BY s.price ASC")
    List<Service> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Find services by duration range
    @Query("SELECT s FROM Service s WHERE s.durationMinutes BETWEEN :minDuration AND :maxDuration AND s.isActive = true ORDER BY s.durationMinutes ASC")
    List<Service> findByDurationRange(@Param("minDuration") Integer minDuration, @Param("maxDuration") Integer maxDuration);
    
    // Get all distinct categories
    @Query("SELECT DISTINCT s.category FROM Service s WHERE s.category IS NOT NULL AND s.isActive = true ORDER BY s.category")
    List<String> findDistinctCategories();
    
    // Provider-specific queries
    
    // Find active services by provider
    List<Service> findByProviderAndIsActiveTrueOrderByName(ServiceProvider provider);
    
    // Find all services by provider (including inactive)
    List<Service> findByProviderOrderByName(ServiceProvider provider);
    
    // Count services by provider
    Long countByProvider(ServiceProvider provider);
    
    // Count active services by provider
    Long countByProviderAndIsActiveTrue(ServiceProvider provider);
    
    // Find services by provider and category
    List<Service> findByProviderAndCategoryAndIsActiveTrueOrderByName(ServiceProvider provider, String category);
}
