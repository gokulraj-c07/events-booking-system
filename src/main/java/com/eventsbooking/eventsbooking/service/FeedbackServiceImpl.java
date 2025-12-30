package com.eventsbooking.eventsbooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.model.Feedback;
import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.repository.BookingRepository;
import com.eventsbooking.eventsbooking.repository.FeedbackRepository;
import com.eventsbooking.eventsbooking.repository.UserRepository;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean hasFeedback(Long bookingId) {
        return feedbackRepository.existsByBookingBookingId(bookingId);
    }

    @Override
    public Feedback saveFeedback(Long bookingId, Long userId, int rating, String comment) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if (feedbackRepository.existsByBookingBookingId(bookingId)) {
            Feedback existing = feedbackRepository.findByBookingBookingId(booking.getBookingId());
            existing.setRating(rating);
            existing.setComment(comment);
            return feedbackRepository.save(existing);
        }

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Feedback fb = new Feedback(booking, user, rating, comment == null ? "" : comment.trim(), LocalDateTime.now());
        return feedbackRepository.save(fb);
    }

    @Override
    public Feedback getFeedbackByBookingId(Long bookingId) {
        return feedbackRepository.findByBookingBookingId(bookingId);
    }

    @Override
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    @Override
    @SuppressWarnings("null")
    public Feedback getById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }
}
