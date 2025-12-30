package com.eventsbooking.eventsbooking.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "booking")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;

	// Provider (the user who provides the service)
	@ManyToOne
	@JoinColumn(name = "provider_id", nullable = false)
	private User provider;

	// User who makes the booking
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// Service being booked
	@ManyToOne
	@JoinColumn(name = "service_id", nullable = false)
	private Services service;

	private String bookingDate;
	private String timeSlot;
	private String bookingStatus;
	private String totalAmount;
	@Column(nullable = false)
	private LocalDateTime createdAt;

	public Booking(Long bookingId, User provider, User user, Services service, String bookingDate, String timeSlot,
			String bookingStatus, String totalAmount, LocalDateTime createdAt) {
		super();
		this.bookingId = bookingId;
		this.provider = provider;
		this.user = user;
		this.service = service;
		this.bookingDate = bookingDate;
		this.timeSlot = timeSlot;
		this.bookingStatus = bookingStatus;
		this.totalAmount = totalAmount;
		this.createdAt = createdAt;
	}

	public Booking() {
		super();

	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public User getProvider() {
		return provider;
	}

	public void setProvider(User provider) {
		this.provider = provider;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Services getService() {
		return service;
	}

	public void setService(Services service) {
		this.service = service;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
