package com.eventsbooking.eventsbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventsbooking.eventsbooking.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	long countByServiceServiceIdAndBookingDate(Long serviceId, String bookingDate);

	long countByServiceServiceIdAndBookingDateAndBookingStatusNot(Long serviceId, String bookingDate, String status);

	// bookings where user is userId
	@Query("select b from Booking b where b.user.userId = :userId")
	List<Booking> findByUserUserId(Long userId);

	Booking findByBookingId(Long bookingId);

	// bookings where provider is providerId
	@Query("SELECT b FROM Booking b WHERE b.provider.userId = :providerId ORDER BY b.bookingDate DESC")
	List<Booking> findByProviderUserId(@Param("providerId") Long providerId);

	// distinct categories for the provider's bookings (for filter)
	@Query("SELECT DISTINCT s.serviceCategory FROM Services s WHERE s.provider.userId = :providerId")
	List<String> findDistinctCategories(@Param("providerId") Long providerId);
}
