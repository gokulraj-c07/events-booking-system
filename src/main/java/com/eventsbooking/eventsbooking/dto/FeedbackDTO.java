package com.eventsbooking.eventsbooking.dto;

public class FeedbackDTO {

	private Long bookingId;
    private boolean hasFeedback;

    public FeedbackDTO() {}
    public FeedbackDTO(Long bookingId, boolean hasFeedback) {
        this.bookingId = bookingId;
        this.hasFeedback = hasFeedback;
    }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public boolean isHasFeedback() { return hasFeedback; }
    public void setHasFeedback(boolean hasFeedback) { this.hasFeedback = hasFeedback; }
}
