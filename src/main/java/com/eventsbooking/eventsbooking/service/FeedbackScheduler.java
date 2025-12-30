package com.eventsbooking.eventsbooking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.repository.BookingRepository;

@Service
public class FeedbackScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Run every day at 10:00 AM to check for events that were completed yesterday.
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendFeedbackReminders() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.toString();

        // Getting all bookings
        List<Booking> allBookings = bookingRepository.findAll();

        for (Booking booking : allBookings) {
            // If the booking date was yesterday and it was confirmed
            if (yesterdayStr.equals(booking.getBookingDate())
                    && "CONFIRM".equalsIgnoreCase(booking.getBookingStatus())) {

                // Check if user already gave feedback
                if (!feedbackService.hasFeedback(booking.getBookingId())) {
                    emailService.sendFeedbackReminder(booking);
                }
            }
        }
    }
}
