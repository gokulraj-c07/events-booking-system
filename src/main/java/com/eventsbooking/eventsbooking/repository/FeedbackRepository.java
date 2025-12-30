package com.eventsbooking.eventsbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventsbooking.eventsbooking.model.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long>{

	Feedback findByBookingBookingId(Long bookingId);  // find feedback for booking
	
	boolean existsByBookingBookingId(Long bookingId);
}
