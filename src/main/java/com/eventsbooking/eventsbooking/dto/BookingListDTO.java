package com.eventsbooking.eventsbooking.dto;

public class BookingListDTO {

	private Long bookingId;
    private String serviceName;
    private String bookingDate;
    private String bookingStatus;
    private boolean hasFeedback;

    public BookingListDTO(Long bookingId, String serviceName, String bookingDate, String bookingStatus) {
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.bookingDate = bookingDate;
        this.bookingStatus = bookingStatus;
        this.hasFeedback = false; // default
    }

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public boolean getHasFeedback() {
		return hasFeedback;
	}

	public void setHasFeedback(boolean hasFeedback) {
		this.hasFeedback = hasFeedback;
	}
}
