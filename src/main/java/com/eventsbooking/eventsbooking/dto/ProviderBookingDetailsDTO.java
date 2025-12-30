package com.eventsbooking.eventsbooking.dto;

public class ProviderBookingDetailsDTO {

    private Long bookingId;
    private String serviceName;
    private String category;
    private String location;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String bookingDate;
    private String price;
    private String status;
    private String paymentStatus;
    private Integer rating;
    private String feedbackComment;

    private String advanceAmount;

    public ProviderBookingDetailsDTO(Long bookingId, String serviceName, String category,
            String location, String customerName, String customerEmail,
            String customerPhone, String bookingDate, String price, String advanceAmount, String status,
            String paymentStatus, Integer rating, String feedbackComment) {

        this.bookingId = bookingId;
        this.serviceName = serviceName;
        this.category = category;
        this.location = location;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.bookingDate = bookingDate;
        this.price = price;
        this.advanceAmount = advanceAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.rating = rating;
        this.feedbackComment = feedbackComment;
    }

    // Getters
    public Long getBookingId() {
        return bookingId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getPrice() {
        return price;
    }

    public String getAdvanceAmount() {
        return advanceAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Integer getRating() {
        return rating;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }
}
