package com.aggy.booking.Repository;

import com.aggy.booking.Model.ServiceProvider;
import com.aggy.booking.Model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    
    // Find available time slots
    List<TimeSlot> findByIsAvailableTrueAndStartTimeAfterOrderByStartTimeAsc(LocalDateTime now);
    
    // Find available time slots for a specific provider
    List<TimeSlot> findByProviderAndIsAvailableTrueAndStartTimeAfterOrderByStartTimeAsc(ServiceProvider provider, LocalDateTime now);
    
    // Find time slots for a specific date range
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.startTime BETWEEN :startDate AND :endDate ORDER BY ts.startTime ASC")
    List<TimeSlot> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find available time slots for a specific date range
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.isAvailable = true AND ts.startTime BETWEEN :startDate AND :endDate ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find available time slots for a specific provider and date range
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.provider = :provider AND ts.isAvailable = true AND ts.startTime BETWEEN :startDate AND :endDate ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableByProviderAndDateRange(@Param("provider") ServiceProvider provider, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find time slots for a specific day
    @Query("SELECT ts FROM TimeSlot ts WHERE DATE(ts.startTime) = DATE(:date) ORDER BY ts.startTime ASC")
    List<TimeSlot> findByDate(@Param("date") LocalDateTime date);
    
    // Find available time slots for a specific day
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.isAvailable = true AND DATE(ts.startTime) = DATE(:date) ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableByDate(@Param("date") LocalDateTime date);
    
    // Find overlapping time slots (for validation)
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.provider = :provider AND ((ts.startTime <= :endTime AND ts.endTime >= :startTime))")
    List<TimeSlot> findOverlappingSlots(@Param("provider") ServiceProvider provider, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Find time slots by provider
    List<TimeSlot> findByProviderOrderByStartTimeAsc(ServiceProvider provider);
    
    // Find time slots by provider ordered by start time
    List<TimeSlot> findByProviderOrderByStartTime(ServiceProvider provider);

    // Count available time slots for today
    @Query("SELECT COUNT(ts) FROM TimeSlot ts WHERE ts.isAvailable = true AND DATE(ts.startTime) = DATE(:today)")
    Long countAvailableForToday(@Param("today") LocalDateTime today);
    
    // Additional methods for TimeSlotService
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.provider = :provider AND ts.isAvailable = true AND ts.startTime BETWEEN :startDate AND :endDate ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableSlotsByProviderAndDateRange(@Param("provider") ServiceProvider provider, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.isAvailable = true AND ts.startTime BETWEEN :startDate AND :endDate ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableSlotsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.provider = :provider AND ts.startTime BETWEEN :startDate AND :endDate ORDER BY ts.startTime ASC")
    List<TimeSlot> findByProviderAndStartTimeBetween(@Param("provider") ServiceProvider provider, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.provider = :provider AND ts.startTime < :endTime AND ts.endTime > :startTime")
    List<TimeSlot> findConflictingSlots(@Param("provider") ServiceProvider provider, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.provider = :provider AND ts.isAvailable = false AND ts.startTime > :now ORDER BY ts.startTime ASC")
    List<TimeSlot> findUpcomingBookedSlots(@Param("provider") ServiceProvider provider, @Param("now") LocalDateTime now);
}
