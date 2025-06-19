package com.aggy.booking.Service;

import com.aggy.booking.Model.Appointment;
import com.aggy.booking.Model.ServiceProvider;
import com.aggy.booking.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.email.from:booking-app@yourcompany.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");

    /**
     * Send booking confirmation email to customer
     */
    public void sendBookingConfirmation(Appointment appointment) {
        if (!emailEnabled) {
            System.out.println("Email disabled - would send booking confirmation to: " + appointment.getUser().getEmail());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("customerName", appointment.getUser().getFullName());
            context.setVariable("serviceName", appointment.getService().getName());
            context.setVariable("providerName", appointment.getService().getProvider().getDisplayName());
            context.setVariable("appointmentDate", appointment.getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER));
            context.setVariable("appointmentDuration", appointment.getService().getDurationMinutes());
            context.setVariable("appointmentPrice", appointment.getService().getPrice());
            context.setVariable("appointmentId", appointment.getId());

            String content = templateEngine.process("emails/booking-confirmation", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(appointment.getUser().getEmail());
            helper.setSubject("Booking Confirmation - " + appointment.getService().getName());
            helper.setText(content, true);

            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    /**
     * Send booking notification to service provider
     */
    public void sendProviderBookingNotification(Appointment appointment) {
        if (!emailEnabled) {
            System.out.println("Email disabled - would send provider notification to: " + appointment.getService().getProvider().getUser().getEmail());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("providerName", appointment.getService().getProvider().getDisplayName());
            context.setVariable("customerName", appointment.getUser().getFullName());
            context.setVariable("serviceName", appointment.getService().getName());
            context.setVariable("appointmentDate", appointment.getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER));
            context.setVariable("appointmentDuration", appointment.getService().getDurationMinutes());
            context.setVariable("customerEmail", appointment.getUser().getEmail());
            context.setVariable("customerPhone", appointment.getUser().getPhoneNumber());
            context.setVariable("appointmentId", appointment.getId());

            String content = templateEngine.process("emails/provider-booking-notification", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(appointment.getService().getProvider().getUser().getEmail());
            helper.setSubject("New Booking - " + appointment.getService().getName());
            helper.setText(content, true);

            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send provider booking notification: " + e.getMessage());
        }
    }

    /**
     * Send cancellation notification
     */
    public void sendCancellationNotification(Appointment appointment, String reason) {
        if (!emailEnabled) {
            System.out.println("Email disabled - would send cancellation notification to: " + appointment.getUser().getEmail());
            return;
        }

        try {
            // Send to customer
            Context customerContext = new Context();
            customerContext.setVariable("customerName", appointment.getUser().getFullName());
            customerContext.setVariable("serviceName", appointment.getService().getName());
            customerContext.setVariable("providerName", appointment.getService().getProvider().getDisplayName());
            customerContext.setVariable("appointmentDate", appointment.getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER));
            customerContext.setVariable("reason", reason != null ? reason : "No reason provided");
            customerContext.setVariable("appointmentId", appointment.getId());

            String customerContent = templateEngine.process("emails/cancellation-notification", customerContext);

            MimeMessage customerMessage = emailSender.createMimeMessage();
            MimeMessageHelper customerHelper = new MimeMessageHelper(customerMessage, true);
            customerHelper.setFrom(fromEmail);
            customerHelper.setTo(appointment.getUser().getEmail());
            customerHelper.setSubject("Appointment Cancelled - " + appointment.getService().getName());
            customerHelper.setText(customerContent, true);

            emailSender.send(customerMessage);

            // Send to provider
            Context providerContext = new Context();
            providerContext.setVariable("providerName", appointment.getService().getProvider().getDisplayName());
            providerContext.setVariable("customerName", appointment.getUser().getFullName());
            providerContext.setVariable("serviceName", appointment.getService().getName());
            providerContext.setVariable("appointmentDate", appointment.getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER));
            providerContext.setVariable("reason", reason != null ? reason : "No reason provided");
            providerContext.setVariable("appointmentId", appointment.getId());

            String providerContent = templateEngine.process("emails/provider-cancellation-notification", providerContext);

            MimeMessage providerMessage = emailSender.createMimeMessage();
            MimeMessageHelper providerHelper = new MimeMessageHelper(providerMessage, true);
            providerHelper.setFrom(fromEmail);
            providerHelper.setTo(appointment.getService().getProvider().getUser().getEmail());
            providerHelper.setSubject("Appointment Cancelled by Customer - " + appointment.getService().getName());
            providerHelper.setText(providerContent, true);

            emailSender.send(providerMessage);

        } catch (Exception e) {
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }
    }

    /**
     * Send appointment reminder (24 hours before)
     */
    public void sendAppointmentReminder(Appointment appointment) {
        if (!emailEnabled) {
            System.out.println("Email disabled - would send reminder to: " + appointment.getUser().getEmail());
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("customerName", appointment.getUser().getFullName());
            context.setVariable("serviceName", appointment.getService().getName());
            context.setVariable("providerName", appointment.getService().getProvider().getDisplayName());
            context.setVariable("appointmentDate", appointment.getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER));
            context.setVariable("appointmentDuration", appointment.getService().getDurationMinutes());
            context.setVariable("providerPhone", appointment.getService().getProvider().getPhone());
            context.setVariable("providerAddress", appointment.getService().getProvider().getAddress());
            context.setVariable("appointmentId", appointment.getId());

            String content = templateEngine.process("emails/appointment-reminder", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(appointment.getUser().getEmail());
            helper.setSubject("Appointment Reminder - Tomorrow at " + 
                appointment.getTimeSlot().getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            helper.setText(content, true);

            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send appointment reminder: " + e.getMessage());
        }
    }

    /**
     * Send simple text email (fallback method)
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            System.out.println("Email disabled - would send to: " + to + ", Subject: " + subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send simple email: " + e.getMessage());
        }
    }
}
