package com.aggy.booking.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "time_slots")
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @OneToOne(mappedBy = "timeSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Appointment appointment;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public TimeSlot() {
        this.createdAt = LocalDateTime.now();
        this.isAvailable = true;
    }
    
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public TimeSlot(ServiceProvider provider, LocalDateTime startTime, LocalDateTime endTime) {
        this(startTime, endTime);
        this.provider = provider;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ServiceProvider getProvider() {
        return provider;
    }
    
    public void setProvider(ServiceProvider provider) {
        this.provider = provider;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public Appointment getAppointment() {
        return appointment;
    }
    
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public String getFormattedStartTime() {
        return startTime.format(DateTimeFormatter.ofPattern("h:mm a"));
    }
    
    public String getFormattedEndTime() {
        return endTime.format(DateTimeFormatter.ofPattern("h:mm a"));
    }
    
    public String getFormattedTimeRange() {
        return getFormattedStartTime() + " - " + getFormattedEndTime();
    }
    
    public String getFormattedDate() {
        return startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    
    public boolean isBooked() {
        return appointment != null && !appointment.isCancelled();
    }
    
    public boolean isPast() {
        return endTime.isBefore(LocalDateTime.now());
    }
    
    public boolean isToday() {
        return startTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    public void book() {
        this.isAvailable = false;
    }
    
    public void release() {
        this.isAvailable = true;
        this.appointment = null;
    }
}
