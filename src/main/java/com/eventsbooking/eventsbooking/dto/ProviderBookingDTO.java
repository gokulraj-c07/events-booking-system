package com.eventsbooking.eventsbooking.dto;

public class ProviderBookingDTO {

	private Long bookingId;
    private String serviceName;
    private String category;
    private String customerName;
    private String date;
    private String status;
    private boolean hasFeedback;
    private String paymentStatus;
    private String createdAt;

    public ProviderBookingDTO(Long bookingId, String serviceName, String category, String customerName,
                              String date, String status, boolean hasFeedback, String paymentStatus, String createdAt) {
        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.category = category;
        this.customerName = customerName;
        this.date = date;
        this.status = status;
        this.hasFeedback = hasFeedback;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public Long getBookingId() { return bookingId; }
    public String getServiceName() { return serviceName; }
    public String getCategory() { return category; }
    public String getCustomerName() { return customerName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public boolean getHasFeedback() { return hasFeedback; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getCreatedAt() { return createdAt; }
}
