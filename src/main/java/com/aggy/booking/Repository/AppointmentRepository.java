package com.aggy.booking.Repository;

import com.aggy.booking.Model.Appointment;
import com.aggy.booking.Model.AppointmentStatus;
import com.aggy.booking.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Find appointments by user
    List<Appointment> findByUserOrderByAppointmentDateTimeDesc(User user);
    
    // Find appointments by user and status
    List<Appointment> findByUserAndStatusOrderByAppointmentDateTimeDesc(User user, AppointmentStatus status);
    
    // Find upcoming appointments for a user
    @Query("SELECT a FROM Appointment a WHERE a.user = :user AND a.appointmentDateTime > :now AND a.status IN (:statuses) ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointments(@Param("user") User user, @Param("now") LocalDateTime now, @Param("statuses") List<AppointmentStatus> statuses);
    
    // Find appointments by date range
    @Query("SELECT a FROM Appointment a WHERE a.user = :user AND a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Count appointments by user and status
    Long countByUserAndStatus(User user, AppointmentStatus status);
    
    // Count upcoming appointments
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.user = :user AND a.appointmentDateTime > :now AND a.status IN (:statuses)")
    Long countUpcomingAppointments(@Param("user") User user, @Param("now") LocalDateTime now, @Param("statuses") List<AppointmentStatus> statuses);
    
    // Find recent appointments (last 5)
    @Query("SELECT a FROM Appointment a WHERE a.user = :user ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findRecentAppointments(@Param("user") User user);
    
    // Find appointments for admin dashboard
    @Query("SELECT a FROM Appointment a ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findAllOrderByDateDesc();
    
    // Find today's appointments
    @Query("SELECT a FROM Appointment a WHERE DATE(a.appointmentDateTime) = DATE(:today) ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findTodaysAppointments(@Param("today") LocalDateTime today);
    
    // Count appointments by status
    Long countByStatus(AppointmentStatus status);
    
    // Find appointments by service provider
    @Query("SELECT a FROM Appointment a WHERE a.provider.id = :providerId ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findByProviderId(@Param("providerId") Long providerId);
}
