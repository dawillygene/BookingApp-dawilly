package com.aggy.booking.Service;

import com.aggy.booking.Model.Service;
import com.aggy.booking.Repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    
    // Get active services count
    public Long getActiveServicesCount() {
        return (long) serviceRepository.findByIsActiveTrueOrderByName().size();
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
}
