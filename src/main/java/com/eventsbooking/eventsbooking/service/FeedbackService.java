package com.eventsbooking.eventsbooking.service;

import java.util.List;

import com.eventsbooking.eventsbooking.model.Feedback;

public interface FeedbackService {

    boolean hasFeedback(Long bookingId);

    Feedback saveFeedback(Long bookingId, Long userId, int rating, String comment);

    Feedback getFeedbackByBookingId(Long bookingId);

    List<Feedback> getAll();

    Feedback getById(Long id);
}
