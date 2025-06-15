package com.aggy.booking.Service;

import com.aggy.booking.Model.*;
import com.aggy.booking.Repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TimeSlotService {
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    // Get available time slots for a specific date and provider
    public List<TimeSlot> getAvailableTimeSlots(ServiceProvider provider, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return timeSlotRepository.findAvailableSlotsByProviderAndDateRange(provider, startOfDay, endOfDay);
    }
    
    // Get all available time slots for the next N days
    public List<TimeSlot> getAvailableTimeSlotsForNextDays(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(days);
        return timeSlotRepository.findAvailableSlotsByDateRange(now, endDate);
    }
    
    // Get time slot by ID
    public Optional<TimeSlot> getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id);
    }
    
    // Create a new time slot
    public TimeSlot createTimeSlot(ServiceProvider provider, LocalDateTime startTime, 
                                 LocalDateTime endTime) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setProvider(provider);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);
        timeSlot.setIsAvailable(true);
        return timeSlotRepository.save(timeSlot);
    }
    
    // Book a time slot
    public TimeSlot bookTimeSlot(Long timeSlotId) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
        if (optionalTimeSlot.isPresent()) {
            TimeSlot timeSlot = optionalTimeSlot.get();
            if (!timeSlot.getIsAvailable()) {
                throw new RuntimeException("Time slot is not available");
            }
            timeSlot.book();
            return timeSlotRepository.save(timeSlot);
        }
        throw new RuntimeException("Time slot not found");
    }
    
    // Release a time slot
    public TimeSlot releaseTimeSlot(Long timeSlotId) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
        if (optionalTimeSlot.isPresent()) {
            TimeSlot timeSlot = optionalTimeSlot.get();
            timeSlot.release();
            return timeSlotRepository.save(timeSlot);
        }
        throw new RuntimeException("Time slot not found");
    }
    
    // Get time slots for a provider within a date range
    public List<TimeSlot> getProviderTimeSlots(ServiceProvider provider, 
                                             LocalDateTime startDate, LocalDateTime endDate) {
        return timeSlotRepository.findByProviderAndStartTimeBetween(provider, startDate, endDate);
    }
    
    // Generate time slots for a provider for a specific day
    public List<TimeSlot> generateDailyTimeSlots(ServiceProvider provider, LocalDate date, 
                                               int intervalMinutes, int startHour, int endHour) {
        List<TimeSlot> slots = new java.util.ArrayList<>();
        
        LocalDateTime current = date.atTime(startHour, 0);
        LocalDateTime dayEnd = date.atTime(endHour, 0);
        
        while (current.isBefore(dayEnd)) {
            LocalDateTime slotEnd = current.plusMinutes(intervalMinutes);
            if (slotEnd.isAfter(dayEnd)) break;
            
            TimeSlot slot = createTimeSlot(provider, current, slotEnd);
            slots.add(slot);
            current = slotEnd;
        }
        
        return slots;
    }
    
    // Check if a time slot conflicts with existing bookings
    public boolean hasConflict(ServiceProvider provider, LocalDateTime startTime, LocalDateTime endTime) {
        List<TimeSlot> conflictingSlots = timeSlotRepository.findConflictingSlots(
            provider, startTime, endTime);
        return !conflictingSlots.isEmpty();
    }
    
    // Get upcoming booked time slots for a provider
    public List<TimeSlot> getUpcomingBookedSlots(ServiceProvider provider) {
        return timeSlotRepository.findUpcomingBookedSlots(provider, LocalDateTime.now());
    }
    
    // Delete a time slot
    public void deleteTimeSlot(Long timeSlotId) {
        Optional<TimeSlot> optionalTimeSlot = timeSlotRepository.findById(timeSlotId);
        if (optionalTimeSlot.isPresent()) {
            TimeSlot timeSlot = optionalTimeSlot.get();
            if (timeSlot.getAppointment() != null) {
                throw new RuntimeException("Cannot delete time slot with existing appointment");
            }
            timeSlotRepository.delete(timeSlot);
        } else {
            throw new RuntimeException("Time slot not found");
        }
    }
}
