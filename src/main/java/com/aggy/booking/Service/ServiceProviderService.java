package com.aggy.booking.Service;

import com.aggy.booking.Model.ServiceProvider;
import com.aggy.booking.Repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ServiceProviderService {
    
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    
    // Get all service providers
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.findAll();
    }
    
    // Get all active service providers
    public List<ServiceProvider> getActiveServiceProviders() {
        return serviceProviderRepository.findByIsActiveTrueOrderByFirstNameAscLastNameAsc();
    }
    
    // Get service provider by ID
    public Optional<ServiceProvider> getServiceProviderById(Long id) {
        return serviceProviderRepository.findById(id);
    }
    
    // Get service providers by specialization
    public List<ServiceProvider> getServiceProvidersBySpecialization(String specialization) {
        return serviceProviderRepository.findBySpecializationAndIsActiveTrueOrderByFirstNameAscLastNameAsc(specialization);
    }
    
    // Search service providers by name
    public List<ServiceProvider> searchServiceProvidersByName(String name) {
        return serviceProviderRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Create a new service provider
    public ServiceProvider createServiceProvider(String firstName, String lastName, String email, 
                                               String phone, String specialization, String bio) {
        ServiceProvider provider = new ServiceProvider();
        provider.setFirstName(firstName);
        provider.setLastName(lastName);
        provider.setEmail(email);
        provider.setPhone(phone);
        provider.setSpecialization(specialization);
        provider.setBio(bio);
        provider.setIsActive(true);
        return serviceProviderRepository.save(provider);
    }
    
    // Update an existing service provider
    public ServiceProvider updateServiceProvider(Long id, String firstName, String lastName, String email, 
                                               String phone, String specialization, String bio, Boolean isActive) {
        Optional<ServiceProvider> optionalProvider = serviceProviderRepository.findById(id);
        if (optionalProvider.isPresent()) {
            ServiceProvider provider = optionalProvider.get();
            provider.setFirstName(firstName);
            provider.setLastName(lastName);
            provider.setEmail(email);
            provider.setPhone(phone);
            provider.setSpecialization(specialization);
            provider.setBio(bio);
            provider.setIsActive(isActive);
            return serviceProviderRepository.save(provider);
        }
        throw new RuntimeException("Service provider not found");
    }
    
    // Activate a service provider
    public ServiceProvider activateServiceProvider(Long id) {
        Optional<ServiceProvider> optionalProvider = serviceProviderRepository.findById(id);
        if (optionalProvider.isPresent()) {
            ServiceProvider provider = optionalProvider.get();
            provider.setIsActive(true);
            return serviceProviderRepository.save(provider);
        }
        throw new RuntimeException("Service provider not found");
    }
    
    // Deactivate a service provider
    public ServiceProvider deactivateServiceProvider(Long id) {
        Optional<ServiceProvider> optionalProvider = serviceProviderRepository.findById(id);
        if (optionalProvider.isPresent()) {
            ServiceProvider provider = optionalProvider.get();
            provider.setIsActive(false);
            return serviceProviderRepository.save(provider);
        }
        throw new RuntimeException("Service provider not found");
    }
    
    // Delete a service provider
    public void deleteServiceProvider(Long id) {
        if (serviceProviderRepository.existsById(id)) {
            serviceProviderRepository.deleteById(id);
        } else {
            throw new RuntimeException("Service provider not found");
        }
    }
    
    // Get service providers count
    public Long getServiceProvidersCount() {
        return serviceProviderRepository.count();
    }
    
    // Get active service providers count
    public Long getActiveServiceProvidersCount() {
        return serviceProviderRepository.countByIsActiveTrue();
    }
    
    // Get service providers by location
    public List<ServiceProvider> getServiceProvidersByLocation(String location) {
        return serviceProviderRepository.findByLocationAndIsActiveTrueOrderByFirstNameAscLastNameAsc(location);
    }
    
    // Get all distinct specializations
    public List<String> getAllSpecializations() {
        return serviceProviderRepository.findDistinctSpecializations();
    }
    
    // Get all distinct locations
    public List<String> getAllLocations() {
        return serviceProviderRepository.findDistinctLocations();
    }
    
    // Find service provider by email
    public ServiceProvider findByEmail(String email) {
        return serviceProviderRepository.findByEmail(email);
    }
    
    // Save service provider
    public ServiceProvider saveServiceProvider(ServiceProvider provider) {
        provider.setUpdatedAt(LocalDateTime.now());
        return serviceProviderRepository.save(provider);
    }
    
    // ================= ADMIN MANAGEMENT METHODS =================
    
    // Get providers with pagination
    public Page<ServiceProvider> getAllProviders(Pageable pageable) {
        return serviceProviderRepository.findAll(pageable);
    }
    
    public Page<ServiceProvider> getPendingProviders(Pageable pageable) {
        // For now, we'll assume providers with isActive = false are pending
        // In a real implementation, you might have a separate status field
        return serviceProviderRepository.findByIsActiveFalse(pageable);
    }
    
    public Page<ServiceProvider> getActiveProviders(Pageable pageable) {
        return serviceProviderRepository.findByIsActiveTrue(pageable);
    }
    
    public Page<ServiceProvider> getInactiveProviders(Pageable pageable) {
        return serviceProviderRepository.findByIsActiveFalse(pageable);
    }
    
    // Count methods for admin dashboard
    public Long getPendingProvidersCount() {
        return serviceProviderRepository.countByIsActiveFalse();
    }
    
    public Long getActiveProvidersCount() {
        return serviceProviderRepository.countByIsActiveTrue();
    }
    
    public Long getInactiveProvidersCount() {
        return serviceProviderRepository.countByIsActiveFalse();
    }
    
    // Provider approval workflow
    public void approveProvider(Long providerId) {
        Optional<ServiceProvider> providerOpt = serviceProviderRepository.findById(providerId);
        if (providerOpt.isPresent()) {
            ServiceProvider provider = providerOpt.get();
            provider.setIsActive(true);
            provider.setUpdatedAt(LocalDateTime.now());
            serviceProviderRepository.save(provider);
        } else {
            throw new RuntimeException("Provider not found");
        }
    }
    
    public void rejectProvider(Long providerId) {
        Optional<ServiceProvider> providerOpt = serviceProviderRepository.findById(providerId);
        if (providerOpt.isPresent()) {
            ServiceProvider provider = providerOpt.get();
            provider.setIsActive(false);
            provider.setUpdatedAt(LocalDateTime.now());
            serviceProviderRepository.save(provider);
        } else {
            throw new RuntimeException("Provider not found");
        }
    }
    
    public void toggleProviderStatus(Long providerId) {
        Optional<ServiceProvider> providerOpt = serviceProviderRepository.findById(providerId);
        if (providerOpt.isPresent()) {
            ServiceProvider provider = providerOpt.get();
            provider.setIsActive(!provider.getIsActive());
            provider.setUpdatedAt(LocalDateTime.now());
            serviceProviderRepository.save(provider);
        } else {
            throw new RuntimeException("Provider not found");
        }
    }
    
    // Analytics methods
    public Map<String, Object> getProviderPerformance() {
        Map<String, Object> performance = new HashMap<>();
        List<ServiceProvider> providers = serviceProviderRepository.findAll();
        
        // Calculate basic performance metrics
        performance.put("totalProviders", providers.size());
        performance.put("activeProviders", providers.stream().filter(ServiceProvider::getIsActive).count());
        performance.put("averageRating", 4.5); // Placeholder - implement actual rating system
        performance.put("topPerformers", providers.stream().limit(5).toList());
        
        return performance;
    }
    
    public Map<String, Object> getProviderSummary(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        // Get providers created in date range
        List<ServiceProvider> newProviders = serviceProviderRepository.findByCreatedAtBetween(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        summary.put("newProviders", newProviders.size());
        summary.put("totalActive", getActiveProvidersCount());
        summary.put("totalPending", getPendingProvidersCount());
        summary.put("providerDetails", newProviders);
        
        return summary;
    }
}
