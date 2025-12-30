package com.eventsbooking.eventsbooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.repository.BookingRepository;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	public List<Booking> getAll() {
		return bookingRepository.findAll();
	}

	@SuppressWarnings("null")
	public Booking getById(Long id) {
		return bookingRepository.findById(id).orElse(null);
	}
}
