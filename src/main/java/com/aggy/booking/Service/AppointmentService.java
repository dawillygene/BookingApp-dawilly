package com.aggy.booking.Service;

import com.aggy.booking.Model.*;
import com.aggy.booking.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Create a new appointment
    public Appointment createAppointment(User user, com.aggy.booking.Model.Service service, 
                                       ServiceProvider provider, LocalDateTime appointmentDateTime, 
                                       String notes) {
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setService(service);
        appointment.setProvider(provider);
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setNotes(notes);
        appointment.setPrice(service.getPrice());
        appointment.setDurationMinutes(service.getDurationMinutes());
        appointment.setStatus(AppointmentStatus.PENDING);
        
        return appointmentRepository.save(appointment);
    }
    
    // Book an appointment with time slot
    public Appointment bookAppointment(User user, com.aggy.booking.Model.Service service, 
                                     TimeSlot timeSlot, String notes) {
        if (!timeSlot.getIsAvailable()) {
            throw new RuntimeException("Time slot is not available");
        }
        
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setService(service);
        appointment.setProvider(timeSlot.getProvider());
        appointment.setTimeSlot(timeSlot);
        appointment.setAppointmentDateTime(timeSlot.getStartTime());
        appointment.setNotes(notes);
        appointment.setPrice(service.getPrice());
        appointment.setDurationMinutes(service.getDurationMinutes());
        appointment.setStatus(AppointmentStatus.PENDING);
        
        // Mark time slot as booked
        timeSlot.book();
        timeSlot.setAppointment(appointment);
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send email notifications
        try {
            emailService.sendBookingConfirmation(savedAppointment);
            emailService.sendProviderBookingNotification(savedAppointment);
        } catch (Exception e) {
            // Log email errors but don't fail the booking
            System.err.println("Failed to send booking notification emails: " + e.getMessage());
        }
        
        return savedAppointment;
    }
    
    // Get all appointments for a user
    public List<Appointment> getUserAppointments(User user) {
        return appointmentRepository.findByUserOrderByAppointmentDateTimeDesc(user);
    }
    
    // Get appointments by status for a user
    public List<Appointment> getUserAppointmentsByStatus(User user, AppointmentStatus status) {
        return appointmentRepository.findByUserAndStatusOrderByAppointmentDateTimeDesc(user, status);
    }
    
    // Get upcoming appointments for a user
    public List<Appointment> getUpcomingAppointments(User user) {
        List<AppointmentStatus> upcomingStatuses = Arrays.asList(
            AppointmentStatus.PENDING, 
            AppointmentStatus.CONFIRMED
        );
        return appointmentRepository.findUpcomingAppointments(user, LocalDateTime.now(), upcomingStatuses);
    }
    
    // Get recent appointments for dashboard (limited to 5)
    public List<Appointment> getRecentAppointments(User user, int limit) {
        List<Appointment> appointments = appointmentRepository.findRecentAppointments(user);
        return appointments.size() > limit ? 
            appointments.subList(0, limit) : appointments;
    }
    
    // Get appointment statistics for a user
    public AppointmentStats getUserAppointmentStats(User user) {
        AppointmentStats stats = new AppointmentStats();
        
        stats.setTotalCount(appointmentRepository.countByUserAndStatus(user, null));
        stats.setUpcomingCount(appointmentRepository.countUpcomingAppointments(
            user, LocalDateTime.now(), 
            Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)));
        stats.setCompletedCount(appointmentRepository.countByUserAndStatus(user, AppointmentStatus.COMPLETED));
        stats.setPendingCount(appointmentRepository.countByUserAndStatus(user, AppointmentStatus.PENDING));
        stats.setCancelledCount(appointmentRepository.countByUserAndStatus(user, AppointmentStatus.CANCELLED));
        
        return stats;
    }
    
    // Confirm an appointment
    public Appointment confirmAppointment(Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            return appointmentRepository.save(appointment);
        }
        throw new RuntimeException("Appointment not found");
    }
    
    // Cancel an appointment
    public Appointment cancelAppointment(Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            
            // Release the time slot if it exists
            if (appointment.getTimeSlot() != null) {
                TimeSlot timeSlot = appointment.getTimeSlot();
                timeSlot.release();
            }
            
            Appointment savedAppointment = appointmentRepository.save(appointment);
            
            // Send cancellation notification emails
            try {
                emailService.sendCancellationNotification(savedAppointment, "Cancelled by customer");
            } catch (Exception e) {
                // Log email errors but don't fail the cancellation
                System.err.println("Failed to send cancellation notification emails: " + e.getMessage());
            }
            
            return savedAppointment;
        }
        throw new RuntimeException("Appointment not found");
    }
    
    // Complete an appointment
    public Appointment completeAppointment(Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setStatus(AppointmentStatus.COMPLETED);
            return appointmentRepository.save(appointment);
        }
        throw new RuntimeException("Appointment not found");
    }
    
    // Reschedule an appointment
    public Appointment rescheduleAppointment(Long appointmentId, TimeSlot newTimeSlot) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            
            // Release old time slot
            if (appointment.getTimeSlot() != null) {
                appointment.getTimeSlot().release();
            }
            
            // Book new time slot
            if (!newTimeSlot.getIsAvailable()) {
                throw new RuntimeException("New time slot is not available");
            }
            
            newTimeSlot.book();
            newTimeSlot.setAppointment(appointment);
            
            appointment.setTimeSlot(newTimeSlot);
            appointment.setAppointmentDateTime(newTimeSlot.getStartTime());
            appointment.setProvider(newTimeSlot.getProvider());
            appointment.setStatus(AppointmentStatus.PENDING); // Reset to pending for re-confirmation
            
            return appointmentRepository.save(appointment);
        }
        throw new RuntimeException("Appointment not found");
    }
    
    // Get appointment by ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    // Admin methods
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllOrderByDateDesc();
    }
    
    public List<Appointment> getTodaysAppointments() {
        return appointmentRepository.findTodaysAppointments(LocalDateTime.now());
    }
    
    public Long getAppointmentCountByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }
    
    // Get appointments by provider
    public List<Appointment> getAppointmentsByProvider(ServiceProvider provider) {
        return appointmentRepository.findByProviderOrderByAppointmentDateTimeDesc(provider);
    }
    
    // Get today's appointments for a provider
    public List<Appointment> getTodayAppointmentsByProvider(ServiceProvider provider) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return appointmentRepository.findByProviderAndAppointmentDateTimeBetweenOrderByAppointmentDateTime(
            provider, startOfDay, endOfDay);
    }
    
    public List<Appointment> getUpcomingAppointmentsByProvider(ServiceProvider provider) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        return appointmentRepository.findByProviderAndAppointmentDateTimeBetweenAndStatusInOrderByAppointmentDateTime(
            provider, now, nextWeek, Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING));
    }

    // Update an appointment
    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    // Inner class for appointment statistics
    public static class AppointmentStats {
        private Long totalCount = 0L;
        private Long upcomingCount = 0L;
        private Long completedCount = 0L;
        private Long pendingCount = 0L;
        private Long cancelledCount = 0L;
        
        // Getters and setters
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        
        public Long getUpcomingCount() { return upcomingCount; }
        public void setUpcomingCount(Long upcomingCount) { this.upcomingCount = upcomingCount; }
        
        public Long getCompletedCount() { return completedCount; }
        public void setCompletedCount(Long completedCount) { this.completedCount = completedCount; }
        
        public Long getPendingCount() { return pendingCount; }
        public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
        
        public Long getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(Long cancelledCount) { this.cancelledCount = cancelledCount; }
    }
    
    // ================= ADMIN METHODS =================
    
    /**
     * Get total appointments count for admin dashboard
     */
    public Long getTotalAppointmentsCount() {
        return appointmentRepository.count();
    }
    
    /**
     * Get today's appointments count
     */
    public int getTodayAppointmentsCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay).size();
    }
    
    /**
     * Get today's revenue
     */
    public Double getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay)
                .stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.CONFIRMED || apt.getStatus() == AppointmentStatus.COMPLETED)
                .mapToDouble(Appointment::getPrice)
                .sum();
    }
    
    /**
     * Get monthly revenue
     */
    public Double getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return appointmentRepository.findByAppointmentDateTimeBetween(startOfMonth, endOfMonth)
                .stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.CONFIRMED || apt.getStatus() == AppointmentStatus.COMPLETED)
                .mapToDouble(Appointment::getPrice)
                .sum();
    }
    
    /**
     * Get monthly appointments count
     */
    public int getMonthlyAppointmentsCount() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return appointmentRepository.findByAppointmentDateTimeBetween(startOfMonth, endOfMonth).size();
    }
    
    /**
     * Get recent appointments for admin dashboard
     */
    public List<Appointment> getRecentAppointmentsForAdmin(int limit) {
        return appointmentRepository.findTopNByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }
    
    /**
     * Get pending appointments count
     */
    public int getPendingAppointmentsCount() {
        return appointmentRepository.findByStatus(AppointmentStatus.PENDING).size();
    }
    
    /**
     * Get booking trends for analytics
     */
    public Map<String, Object> getBookingTrends(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Placeholder implementation - would need more complex queries
        Map<String, Object> trends = new HashMap<>();
        trends.put("message", "Booking trends data would go here");
        return trends;
    }
    
    /**
     * Get revenue analytics
     */
    public Map<String, Object> getRevenueAnalytics(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Placeholder implementation - would need more complex queries
        Map<String, Object> revenue = new HashMap<>();
        revenue.put("message", "Revenue analytics data would go here");
        return revenue;
    }
    
    /**
     * Get appointment summary
     */
    public Map<String, Object> getAppointmentSummary(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Placeholder implementation
        Map<String, Object> summary = new HashMap<>();
        summary.put("message", "Appointment summary data would go here");
        return summary;
    }
    
    /**
     * Get revenue summary
     */
    public Map<String, Object> getRevenueSummary(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Placeholder implementation
        Map<String, Object> summary = new HashMap<>();
        summary.put("message", "Revenue summary data would go here");
        return summary;
    }
}
