package com.aggy.booking.Service;

import com.aggy.booking.Model.*;
import com.aggy.booking.Repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private TimeSlotService timeSlotService;
    
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
        
        return appointmentRepository.save(appointment);
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
            
            return appointmentRepository.save(appointment);
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
    
    public Long getTotalAppointmentsCount() {
        return appointmentRepository.count();
    }
    
    public Long getAppointmentCountByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
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
}
