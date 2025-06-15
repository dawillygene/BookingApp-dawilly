package com.aggy.booking.Service;

import com.aggy.booking.Model.ServiceProvider;
import com.aggy.booking.Repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}
