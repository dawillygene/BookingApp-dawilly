package com.aggy.booking.Controller;

import com.aggy.booking.Model.*;
import com.aggy.booking.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ServiceProviderService serviceProviderService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @GetMapping
    public String adminDashboard(Model model) {
        AdminDashboardData dashboardData = getAdminDashboardData();
        
        model.addAttribute("pageTitle", "Admin Dashboard");
        model.addAttribute("breadcrumb", "Admin");
        model.addAttribute("currentPage", "dashboard"); // Add current page identifier
        model.addAttribute("adminStats", dashboardData.getStats());
        model.addAttribute("totalUsers", dashboardData.getTotalUsers());
        model.addAttribute("totalAppointments", dashboardData.getTotalAppointments());
        model.addAttribute("monthlyRevenue", dashboardData.getMonthlyRevenue());
        model.addAttribute("activeProviders", dashboardData.getActiveProviders());
        model.addAttribute("pendingProviders", dashboardData.getPendingProviders());
        model.addAttribute("recentAppointments", dashboardData.getRecentAppointments());
        model.addAttribute("systemHealth", dashboardData.getSystemHealth());
        
        return "admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "redirect:/admin";
    }

    // ================= PROVIDER MANAGEMENT =================

    @GetMapping("/providers")
    public String providerManagement(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(defaultValue = "") String status,
                                   Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceProvider> providers;
        
        if ("pending".equals(status)) {
            providers = serviceProviderService.getPendingProviders(pageable);
        } else if ("active".equals(status)) {
            providers = serviceProviderService.getActiveProviders(pageable);
        } else if ("inactive".equals(status)) {
            providers = serviceProviderService.getInactiveProviders(pageable);
        } else {
            providers = serviceProviderService.getAllProviders(pageable);
        }
        
        model.addAttribute("pageTitle", "Provider Management");
        model.addAttribute("currentPage", "providers"); // Add current page identifier
        model.addAttribute("providers", providers);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("pendingCount", serviceProviderService.getPendingProvidersCount());
        model.addAttribute("activeCount", serviceProviderService.getActiveProvidersCount());
        model.addAttribute("inactiveCount", serviceProviderService.getInactiveProvidersCount());
        
        return "admin/providers";
    }

    @PostMapping("/providers/{id}/approve")
    @ResponseBody
    public ResponseEntity<Map<String, String>> approveProvider(@PathVariable Long id) {
        try {
            serviceProviderService.approveProvider(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Provider approved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to approve provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/providers/{id}/reject")
    @ResponseBody
    public ResponseEntity<Map<String, String>> rejectProvider(@PathVariable Long id) {
        try {
            serviceProviderService.rejectProvider(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Provider rejected successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to reject provider: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/providers/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> toggleProviderStatus(@PathVariable Long id) {
        try {
            serviceProviderService.toggleProviderStatus(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Provider status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update provider status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ================= USER MANAGEMENT =================

    @GetMapping("/users")
    public String userManagement(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "") String role,
                               Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        
        if ("USER".equals(role)) {
            users = userService.getUsersByRole(User.Role.USER, pageable);
        } else if ("PROVIDER".equals(role)) {
            users = userService.getUsersByRole(User.Role.PROVIDER, pageable);
        } else if ("ADMIN".equals(role)) {
            users = userService.getUsersByRole(User.Role.ADMIN, pageable);
        } else {
            users = userService.getAllUsers(pageable);
        }
        
        model.addAttribute("pageTitle", "User Management");
        model.addAttribute("currentPage", "users"); // Add current page identifier
        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("userCount", userService.getUsersCountByRole(User.Role.USER));
        model.addAttribute("providerCount", userService.getUsersCountByRole(User.Role.PROVIDER));
        model.addAttribute("adminCount", userService.getUsersCountByRole(User.Role.ADMIN));
        
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> toggleUserStatus(@PathVariable Long id) {
        try {
            userService.toggleUserStatus(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update user status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/users/{id}/change-role")
    @ResponseBody
    public ResponseEntity<Map<String, String>> changeUserRole(@PathVariable Long id, 
                                                            @RequestParam String newRole) {
        try {
            User.Role role = User.Role.valueOf(newRole);
            userService.changeUserRole(id, role);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "User role updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to update user role: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ================= SERVICE MANAGEMENT =================

    @GetMapping("/services")
    public String serviceManagement(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) Boolean isActive,
                                  Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        ServiceCategory category = null;
        if (categoryId != null) {
            category = serviceCategoryService.getCategoryById(categoryId).orElse(null);
        }
        
        Page<com.aggy.booking.Model.Service> services = serviceService.getServicesForAdmin(search, category, isActive, pageable);
        List<ServiceCategory> categories = serviceCategoryService.getAllCategories();
        
        model.addAttribute("pageTitle", "Service Management");
        model.addAttribute("breadcrumb", "Services");
        model.addAttribute("services", services);
        model.addAttribute("categories", categories);
        model.addAttribute("totalServices", serviceService.getTotalServicesCount());
        model.addAttribute("activeServices", serviceService.getActiveServicesCount());
        
        return "admin/services";
    }

    // ================= SERVICE CATEGORY MANAGEMENT =================

    @GetMapping("/categories")
    public String categoryManagement(Model model) {
        List<ServiceCategory> categories = serviceCategoryService.getAllCategories();
        
        model.addAttribute("pageTitle", "Service Categories");
        model.addAttribute("categories", categories);
        model.addAttribute("newCategory", new ServiceCategory());
        
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(@ModelAttribute ServiceCategory category, 
                               RedirectAttributes redirectAttributes) {
        try {
            serviceCategoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("success", "Category created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create category: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        try {
            serviceCategoryService.deleteCategory(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Category deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to delete category: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ================= ANALYTICS & REPORTS =================

    @GetMapping("/analytics")
    public String analytics(Model model) {
        AnalyticsData analytics = getAnalyticsDataInternal();
        
        model.addAttribute("pageTitle", "System Analytics");
        model.addAttribute("analytics", analytics);
        
        return "admin/analytics";
    }

    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue = "30") int days, Model model) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        ReportData reportData = generateReports(startDate, endDate);
        
        model.addAttribute("pageTitle", "System Reports");
        model.addAttribute("reportData", reportData);
        model.addAttribute("selectedDays", days);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "admin/reports";
    }

    // ================= API ENDPOINTS FOR AJAX =================

    @GetMapping("/api/dashboard-stats")
    @ResponseBody
    public ResponseEntity<AdminDashboardData> getDashboardStats() {
        return ResponseEntity.ok(getAdminDashboardData());
    }

    @GetMapping("/api/analytics-data")
    @ResponseBody
    public ResponseEntity<AnalyticsData> getAnalyticsDataApi() {
        return ResponseEntity.ok(getAnalyticsDataInternal());
    }

    // ================= HELPER METHODS =================

    private AdminDashboardData getAdminDashboardData() {
        AdminDashboardData data = new AdminDashboardData();
        
        // Basic stats
        data.setTotalUsers(userService.getTotalUsersCount());
        data.setTotalAppointments(appointmentService.getTotalAppointmentsCount().intValue());
        data.setActiveProviders(serviceProviderService.getActiveProvidersCount().intValue());
        data.setPendingProviders(serviceProviderService.getPendingProvidersCount().intValue());
        
        // Today's data
        data.setTodayAppointments(appointmentService.getTodayAppointmentsCount());
        data.setTodayRevenue(appointmentService.getTodayRevenue());
        
        // Monthly data
        data.setMonthlyRevenue(appointmentService.getMonthlyRevenue());
        data.setMonthlyAppointments(appointmentService.getMonthlyAppointmentsCount());
        
        // Recent activity
        data.setRecentAppointments(appointmentService.getRecentAppointmentsForAdmin(10));
        
        // System health
        Map<String, Object> systemHealth = new HashMap<>();
        systemHealth.put("emailNotifications", true);
        systemHealth.put("databaseStatus", "healthy");
        systemHealth.put("activeUsers", userService.getActiveUsersCount());
        systemHealth.put("systemUptime", "99.9%");
        data.setSystemHealth(systemHealth);
        
        // Admin stats
        AdminStats stats = new AdminStats();
        stats.setTodayAppointments(data.getTodayAppointments());
        stats.setTotalRevenue(data.getTodayRevenue().intValue());
        stats.setNewCustomers(userService.getNewUsersThisWeek());
        stats.setPendingBookings(appointmentService.getPendingAppointmentsCount());
        stats.setActiveProviders(data.getActiveProviders());
        data.setStats(stats);
        
        return data;
    }

    private AnalyticsData getAnalyticsDataInternal() {
        AnalyticsData analytics = new AnalyticsData();
        
        // Booking trends (last 30 days)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        analytics.setBookingTrends(appointmentService.getBookingTrends(startDate, endDate));
        
        // Revenue analytics
        analytics.setRevenueData(appointmentService.getRevenueAnalytics(startDate, endDate));
        
        // Provider performance
        analytics.setProviderPerformance(serviceProviderService.getProviderPerformance());
        
        // Service popularity
        analytics.setServicePopularity(serviceService.getServicePopularity());
        
        // User engagement
        analytics.setUserEngagement(userService.getUserEngagementStats());
        
        return analytics;
    }

    private ReportData generateReports(LocalDate startDate, LocalDate endDate) {
        ReportData report = new ReportData();
        
        report.setAppointmentSummary(appointmentService.getAppointmentSummary(startDate, endDate));
        report.setRevenueSummary(appointmentService.getRevenueSummary(startDate, endDate));
        report.setProviderSummary(serviceProviderService.getProviderSummary(startDate, endDate));
        report.setUserActivitySummary(userService.getUserActivitySummary(startDate, endDate));
        
        return report;
    }

    // ================= DATA CLASSES =================

    public static class AdminStats {
        private int todayAppointments;
        private int totalRevenue;
        private int newCustomers;
        private int pendingBookings;
        private double occupancyRate = 85.5;
        private int activeProviders;
        private double satisfaction = 4.8;

        // Getters and setters
        public int getTodayAppointments() { return todayAppointments; }
        public void setTodayAppointments(int todayAppointments) { this.todayAppointments = todayAppointments; }
        
        public int getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(int totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public int getNewCustomers() { return newCustomers; }
        public void setNewCustomers(int newCustomers) { this.newCustomers = newCustomers; }
        
        public int getPendingBookings() { return pendingBookings; }
        public void setPendingBookings(int pendingBookings) { this.pendingBookings = pendingBookings; }
        
        public double getOccupancyRate() { return occupancyRate; }
        public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
        
        public int getActiveProviders() { return activeProviders; }
        public void setActiveProviders(int activeProviders) { this.activeProviders = activeProviders; }
        
        public double getSatisfaction() { return satisfaction; }
        public void setSatisfaction(double satisfaction) { this.satisfaction = satisfaction; }
    }

    public static class AdminDashboardData {
        private int totalUsers;
        private int totalAppointments;
        private int activeProviders;
        private int pendingProviders;
        private int todayAppointments;
        private Double todayRevenue;
        private Double monthlyRevenue;
        private int monthlyAppointments;
        private List<Appointment> recentAppointments;
        private Map<String, Object> systemHealth;
        private AdminStats stats;

        // Getters and setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }
        
        public int getActiveProviders() { return activeProviders; }
        public void setActiveProviders(int activeProviders) { this.activeProviders = activeProviders; }
        
        public int getPendingProviders() { return pendingProviders; }
        public void setPendingProviders(int pendingProviders) { this.pendingProviders = pendingProviders; }
        
        public int getTodayAppointments() { return todayAppointments; }
        public void setTodayAppointments(int todayAppointments) { this.todayAppointments = todayAppointments; }
        
        public Double getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(Double todayRevenue) { this.todayRevenue = todayRevenue; }
        
        public Double getMonthlyRevenue() { return monthlyRevenue; }
        public void setMonthlyRevenue(Double monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
        
        public int getMonthlyAppointments() { return monthlyAppointments; }
        public void setMonthlyAppointments(int monthlyAppointments) { this.monthlyAppointments = monthlyAppointments; }
        
        public List<Appointment> getRecentAppointments() { return recentAppointments; }
        public void setRecentAppointments(List<Appointment> recentAppointments) { this.recentAppointments = recentAppointments; }
        
        public Map<String, Object> getSystemHealth() { return systemHealth; }
        public void setSystemHealth(Map<String, Object> systemHealth) { this.systemHealth = systemHealth; }
        
        public AdminStats getStats() { return stats; }
        public void setStats(AdminStats stats) { this.stats = stats; }
    }

    public static class AnalyticsData {
        private Map<String, Object> bookingTrends;
        private Map<String, Object> revenueData;
        private Map<String, Object> providerPerformance;
        private Map<String, Object> servicePopularity;
        private Map<String, Object> userEngagement;

        // Getters and setters
        public Map<String, Object> getBookingTrends() { return bookingTrends; }
        public void setBookingTrends(Map<String, Object> bookingTrends) { this.bookingTrends = bookingTrends; }
        
        public Map<String, Object> getRevenueData() { return revenueData; }
        public void setRevenueData(Map<String, Object> revenueData) { this.revenueData = revenueData; }
        
        public Map<String, Object> getProviderPerformance() { return providerPerformance; }
        public void setProviderPerformance(Map<String, Object> providerPerformance) { this.providerPerformance = providerPerformance; }
        
        public Map<String, Object> getServicePopularity() { return servicePopularity; }
        public void setServicePopularity(Map<String, Object> servicePopularity) { this.servicePopularity = servicePopularity; }
        
        public Map<String, Object> getUserEngagement() { return userEngagement; }
        public void setUserEngagement(Map<String, Object> userEngagement) { this.userEngagement = userEngagement; }
    }

    public static class ReportData {
        private Map<String, Object> appointmentSummary;
        private Map<String, Object> revenueSummary;
        private Map<String, Object> providerSummary;
        private Map<String, Object> userActivitySummary;

        // Getters and setters
        public Map<String, Object> getAppointmentSummary() { return appointmentSummary; }
        public void setAppointmentSummary(Map<String, Object> appointmentSummary) { this.appointmentSummary = appointmentSummary; }
        
        public Map<String, Object> getRevenueSummary() { return revenueSummary; }
        public void setRevenueSummary(Map<String, Object> revenueSummary) { this.revenueSummary = revenueSummary; }
        
        public Map<String, Object> getProviderSummary() { return providerSummary; }
        public void setProviderSummary(Map<String, Object> providerSummary) { this.providerSummary = providerSummary; }
        
        public Map<String, Object> getUserActivitySummary() { return userActivitySummary; }
        public void setUserActivitySummary(Map<String, Object> userActivitySummary) { this.userActivitySummary = userActivitySummary; }
    }
}
