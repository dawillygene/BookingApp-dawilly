package com.aggy.booking.Service;

import com.aggy.booking.Model.ServiceCategory;
import com.aggy.booking.Repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceCategoryService {
    
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    
    /**
     * Get all categories
     */
    public List<ServiceCategory> getAllCategories() {
        return serviceCategoryRepository.findAll();
    }
    
    /**
     * Get all active categories
     */
    public List<ServiceCategory> getActiveCategories() {
        return serviceCategoryRepository.findByIsActiveTrueOrderByName();
    }
    
    /**
     * Get categories with pagination
     */
    public Page<ServiceCategory> getCategoriesWithPagination(Pageable pageable) {
        return serviceCategoryRepository.findAll(pageable);
    }
    
    /**
     * Get category by ID
     */
    public Optional<ServiceCategory> getCategoryById(Long id) {
        return serviceCategoryRepository.findById(id);
    }
    
    /**
     * Create a new category
     */
    public ServiceCategory createCategory(ServiceCategory category) {
        category.setCreatedAt(LocalDateTime.now());
        if (category.getIsActive() == null) {
            category.setIsActive(true);
        }
        return serviceCategoryRepository.save(category);
    }
    
    /**
     * Update a category
     */
    public ServiceCategory updateCategory(ServiceCategory category) {
        category.setUpdatedAt(LocalDateTime.now());
        return serviceCategoryRepository.save(category);
    }
    
    /**
     * Delete a category
     */
    public void deleteCategory(Long id) {
        serviceCategoryRepository.deleteById(id);
    }
    
    /**
     * Toggle category status
     */
    public ServiceCategory toggleCategoryStatus(Long id) {
        Optional<ServiceCategory> categoryOpt = serviceCategoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            ServiceCategory category = categoryOpt.get();
            category.setIsActive(!category.getIsActive());
            category.setUpdatedAt(LocalDateTime.now());
            return serviceCategoryRepository.save(category);
        }
        throw new RuntimeException("Category not found");
    }
    
    /**
     * Get total categories count
     */
    public int getTotalCategoriesCount() {
        return (int) serviceCategoryRepository.count();
    }
    
    /**
     * Get active categories count
     */
    public int getActiveCategoriesCount() {
        return (int) serviceCategoryRepository.countByIsActiveTrue();
    }
    
    /**
     * Check if category name exists
     */
    public boolean existsByName(String name) {
        return serviceCategoryRepository.existsByName(name);
    }
    
    /**
     * Find category by name
     */
    public Optional<ServiceCategory> findByName(String name) {
        return serviceCategoryRepository.findByName(name);
    }
}
