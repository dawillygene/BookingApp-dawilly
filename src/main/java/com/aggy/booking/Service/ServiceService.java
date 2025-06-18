package com.aggy.booking.Service;

import com.aggy.booking.Model.Service;
import com.aggy.booking.Model.ServiceProvider;
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
}
