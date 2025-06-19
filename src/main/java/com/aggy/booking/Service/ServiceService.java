package com.aggy.booking.Service;

import com.aggy.booking.Model.Service;
import com.aggy.booking.Model.ServiceCategory;
import com.aggy.booking.Model.ServiceProvider;
import com.aggy.booking.Repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
public class ServiceService {
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    // Get all services
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    // Get all active services
    public List<Service> getActiveServices() {
        return serviceRepository.findByIsActiveTrueOrderByName();
    }
    
    // Get service by ID
    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }
    
    // Get services by category
    public List<Service> getServicesByCategory(String category) {
        return serviceRepository.findByCategoryAndIsActiveTrueOrderByName(category);
    }
    
    // Get services by name (search)
    public List<Service> searchServicesByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Create a new service
    public Service createService(String name, String description, String category, 
                               Double price, Integer durationMinutes) {
        Service service = new Service();
        service.setName(name);
        service.setDescription(description);
        service.setCategory(category);
        service.setPrice(price);
        service.setDurationMinutes(durationMinutes);
        service.setIsActive(true);
        return serviceRepository.save(service);
    }
    
    // Update an existing service
    public Service updateService(Long id, String name, String description, String category, 
                               Double price, Integer durationMinutes, Boolean isActive) {
        Optional<Service> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Service service = optionalService.get();
            service.setName(name);
            service.setDescription(description);
            service.setCategory(category);
            service.setPrice(price);
            service.setDurationMinutes(durationMinutes);
            service.setIsActive(isActive);
            return serviceRepository.save(service);
        }
        throw new RuntimeException("Service not found");
    }
    
    // Activate a service
    public Service activateService(Long id) {
        Optional<Service> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Service service = optionalService.get();
            service.setIsActive(true);
            return serviceRepository.save(service);
        }
        throw new RuntimeException("Service not found");
    }
    
    // Deactivate a service
    public Service deactivateService(Long id) {
        Optional<Service> optionalService = serviceRepository.findById(id);
        if (optionalService.isPresent()) {
            Service service = optionalService.get();
            service.setIsActive(false);
            return serviceRepository.save(service);
        }
        throw new RuntimeException("Service not found");
    }
    
    // Delete a service
    public void deleteService(Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
        } else {
            throw new RuntimeException("Service not found");
        }
    }
    
    // Get services count
    public Long getServicesCount() {
        return serviceRepository.count();
    }
    
    // Get services by price range
    public List<Service> getServicesByPriceRange(Double minPrice, Double maxPrice) {
        return serviceRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    // Get services by duration range
    public List<Service> getServicesByDurationRange(Integer minDuration, Integer maxDuration) {
        return serviceRepository.findByDurationRange(minDuration, maxDuration);
    }
    
    // Get all distinct categories
    public List<String> getAllCategories() {
        return serviceRepository.findDistinctCategories();
    }
    
    // Provider-specific methods
    
    // Get services by provider
    public List<Service> getServicesByProvider(ServiceProvider provider) {
        return serviceRepository.findByProviderAndIsActiveTrueOrderByName(provider);
    }
    
    // Get all services by provider (including inactive)
    public List<Service> getAllServicesByProvider(ServiceProvider provider) {
        return serviceRepository.findByProviderOrderByName(provider);
    }
    
    // Create a service for a specific provider
    public Service createServiceForProvider(String name, String description, String category, 
                                          Double price, Integer durationMinutes, ServiceProvider provider) {
        Service service = new Service();
        service.setName(name);
        service.setDescription(description);
        service.setCategory(category);
        service.setPrice(price);
        service.setDurationMinutes(durationMinutes);
        service.setProvider(provider);
        service.setIsActive(true);
        return serviceRepository.save(service);
    }
    
    // Update service (only if owned by provider)
    public Service updateServiceForProvider(Long serviceId, String name, String description, 
                                          String category, Double price, Integer durationMinutes, 
                                          ServiceProvider provider) {
        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isPresent()) {
            Service service = serviceOpt.get();
            // Security check: ensure the service belongs to the provider
            if (service.getProvider() != null && service.getProvider().getId().equals(provider.getId())) {
                service.setName(name);
                service.setDescription(description);
                service.setCategory(category);
                service.setPrice(price);
                service.setDurationMinutes(durationMinutes);
                return serviceRepository.save(service);
            } else {
                throw new RuntimeException("Unauthorized: Service does not belong to this provider");
            }
        } else {
            throw new RuntimeException("Service not found");
        }
    }
    
    // Deactivate service (only if owned by provider)
    public Service deactivateServiceForProvider(Long serviceId, ServiceProvider provider) {
        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isPresent()) {
            Service service = serviceOpt.get();
            if (service.getProvider() != null && service.getProvider().getId().equals(provider.getId())) {
                service.setIsActive(false);
                return serviceRepository.save(service);
            } else {
                throw new RuntimeException("Unauthorized: Service does not belong to this provider");
            }
        } else {
            throw new RuntimeException("Service not found");
        }
    }
    
    // Get provider services count
    public Long getServicesCountByProvider(ServiceProvider provider) {
        return serviceRepository.countByProvider(provider);
    }
    
    // Get active provider services count
    public Long getActiveServicesCountByProvider(ServiceProvider provider) {
        return serviceRepository.countByProviderAndIsActiveTrue(provider);
    }
    
    // Admin management methods
    
    /**
     * Get services with pagination and search for admin dashboard
     */
    public Page<Service> getServicesForAdmin(String search, ServiceCategory category, Boolean isActive, Pageable pageable) {
        return serviceRepository.findServicesWithSearch(search, category, isActive, pageable);
    }
    
    /**
     * Get all services by category
     */
    public List<Service> getServicesByServiceCategory(ServiceCategory category) {
        return serviceRepository.findByServiceCategoryAndIsActiveTrueOrderByName(category);
    }
    
    /**
     * Get services by category with pagination
     */
    public Page<Service> getServicesByCategoryPaginated(ServiceCategory category, Pageable pageable) {
        return serviceRepository.findByServiceCategory(category, pageable);
    }
    
    // Analytics and statistics methods
    
    /**
     * Get total services count
     */
    public int getTotalServicesCount() {
        return (int) serviceRepository.count();
    }
    
    /**
     * Get active services count
     */
    public int getActiveServicesCount() {
        return (int) serviceRepository.countByIsActiveTrue();
    }
    
    /**
     * Get services count by category
     */
    public int getServicesCountByCategory(ServiceCategory category) {
        return (int) serviceRepository.countByServiceCategory(category);
    }
    
    /**
     * Get new services count this week
     */
    public int getNewServicesThisWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        return (int) serviceRepository.countByCreatedAtAfter(weekAgo);
    }
    
    /**
     * Get service statistics by category
     */
    public Map<String, Object> getServiceStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalServices", getTotalServicesCount());
        stats.put("activeServices", getActiveServicesCount());
        stats.put("inactiveServices", getTotalServicesCount() - getActiveServicesCount());
        stats.put("newServicesThisWeek", getNewServicesThisWeek());
        
        // Get statistics by category
        List<Object[]> categoryStats = serviceRepository.getServiceStatsByCategory();
        List<Map<String, Object>> categoryStatsList = new ArrayList<>();
        for (Object[] stat : categoryStats) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("category", stat[0]);
            categoryData.put("count", stat[1]);
            categoryData.put("averagePrice", stat[2]);
            categoryData.put("minPrice", stat[3]);
            categoryData.put("maxPrice", stat[4]);
            categoryStatsList.add(categoryData);
        }
        stats.put("categoryStats", categoryStatsList);
        
        return stats;
    }
    
    /**
     * Get service registration trends (last 30 days)
     */
    public List<Map<String, Object>> getServiceRegistrationTrends() {
        List<Map<String, Object>> trends = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDateTime startOfDay = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            int count = (int) serviceRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", startOfDay.toLocalDate().toString());
            dayData.put("services", count);
            trends.add(dayData);
        }
        return trends;
    }
    
    /**
     * Get average price by category
     */
    public Double getAveragePriceByCategory(ServiceCategory category) {
        return serviceRepository.getAveragePriceByCategory(category);
    }
    
    /**
     * Get service popularity analytics (placeholder)
     */
    public Map<String, Object> getServicePopularity() {
        // Placeholder implementation - would need appointment data to calculate popularity
        Map<String, Object> popularity = new HashMap<>();
        popularity.put("message", "Service popularity data would go here");
        return popularity;
    }
}
