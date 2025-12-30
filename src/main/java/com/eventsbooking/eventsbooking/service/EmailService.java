package com.eventsbooking.eventsbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp, String name) {
        String subject = "Your OTP for EventHub Login";
        String message = "Dear " + name + ",\n\nYour One-Time Password (OTP) is: " + otp +
                "\n\nThis OTP will expire in 10 minutes.\n\nRegards,\nEventHub Team";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Smart EventHub!";
        String message = "Hi " + name + ",\n\nYour account has been created successfully." +
                "\nYou can now log in using your email and OTP.\n\nRegards,\nEventHub Team";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    // 1. User complete the payment - send emails to user and provider
    public void sendPaymentSuccessEmails(com.eventsbooking.eventsbooking.model.Booking booking,
            com.eventsbooking.eventsbooking.model.Payment payment) {
        // To User
        String userSubject = "Payment Successful - Booking Confirmed!";
        String userMsg = "Dear " + booking.getUser().getName() + ",\n\n" +
                "Your payment of ₹" + payment.getAmountPaid() + " for '" + booking.getService().getServiceName()
                + "' was successful.\n" +
                "Transaction ID: " + payment.getTransactionId() + "\n" +
                "Booking Date: " + booking.getBookingDate() + "\n\n" +
                "Thank you for choosing EventHub!";

        sendSimpleEmail(booking.getUser().getEmailId(), userSubject, userMsg);

        // To Provider
        String providerSubject = "New Confirmed Booking for " + booking.getService().getServiceName();
        String providerMsg = "Dear " + booking.getProvider().getName() + ",\n\n" +
                "You have a new confirmed booking!\n" +
                "Customer: " + booking.getUser().getName() + "\n" +
                "Date: " + booking.getBookingDate() + " (" + booking.getTimeSlot() + ")\n" +
                "Payment Status: PAID\n\n" +
                "Please prepare for the event.";

        sendSimpleEmail(booking.getProvider().getEmailId(), providerSubject, providerMsg);
    }

    // 2. User tried to book but did not complete payment - notify provider
    public void sendBookingInitiatedNotification(com.eventsbooking.eventsbooking.model.Booking booking) {
        String subject = "Booking Interest: " + booking.getService().getServiceName();
        String message = "Dear " + booking.getProvider().getName() + ",\n\n" +
                "A user (" + booking.getUser().getName() + ") has initiated a booking for your service.\n" +
                "Scheduled Date: " + booking.getBookingDate() + "\n" +
                "Note: Payment is currently pending. We will notify you once it's completed.";

        sendSimpleEmail(booking.getProvider().getEmailId(), subject, message);
    }

    // 3. Send invoice email to user after payment
    public void sendInvoiceEmail(com.eventsbooking.eventsbooking.model.Bill bill) {
        String subject = "Invoice for your Booking - " + bill.getInvoiceNumber();
        String message = "Dear " + bill.getBooking().getUser().getName() + ",\n\n" +
                "Please find your invoice details below:\n" +
                "Invoice No: " + bill.getInvoiceNumber() + "\n" +
                "Service: " + bill.getBooking().getService().getServiceName() + "\n" +
                "Amount Paid: ₹" + bill.getAmount() + "\n" +
                "Billing Date: " + bill.getDate() + "\n" +
                "Status: " + bill.getStatus() + "\n\n" +
                "Regards,\nEventHub Accounts Team";

        sendSimpleEmail(bill.getBooking().getUser().getEmailId(), subject, message);
    }

    // 4. Feedback notification email to user after event date
    public void sendFeedbackReminder(com.eventsbooking.eventsbooking.model.Booking booking) {
        String subject = "How was your event?";
        String message = "Hi " + booking.getUser().getName() + ",\n\n" +
                "We hope you enjoyed your event for '" + booking.getService().getServiceName() + "'.\n" +
                "Could you please take a moment to share your feedback?\n\n" +
                "It helps us improve and helps providers serve you better!";

        sendSimpleEmail(booking.getUser().getEmailId(), subject, message);
    }

    // 5. User gave feedback - notify provider
    public void sendFeedbackNotificationToProvider(com.eventsbooking.eventsbooking.model.Feedback feedback) {
        String subject = "New Feedback Received!";
        String message = "Dear " + feedback.getBooking().getProvider().getName() + ",\n\n" +
                "You have received new feedback for your service '"
                + feedback.getBooking().getService().getServiceName() + "'.\n" +
                "Rating: " + feedback.getRating() + "/5\n" +
                "Comment: " + feedback.getComment() + "\n" +
                "From: " + feedback.getUser().getName();

        sendSimpleEmail(feedback.getBooking().getProvider().getEmailId(), subject, message);
    }

    // Helper for internal use
    private void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }
}
