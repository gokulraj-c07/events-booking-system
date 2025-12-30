package com.eventsbooking.eventsbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eventsbooking.eventsbooking.model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>{

	List<Bill> findByBookingUserUserId(Long userId);
	
	Bill findByBookingBookingId(Long bookingId);
	
	// fetch all bills for provider
    @Query("SELECT b FROM Bill b WHERE b.booking.provider.userId = :providerId")
    List<Bill> findBillsByProvider(Long providerId);
}
