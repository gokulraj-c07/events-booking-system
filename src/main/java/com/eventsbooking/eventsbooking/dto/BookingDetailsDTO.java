package com.eventsbooking.eventsbooking.dto;

public class BookingDetailsDTO {

	private Long bookingId;
	private String serviceName;
	private String category;
	private String location;
	private String providerName;
	private String businessEmailId;
	private String businessPhoneNumber;
	private String bookingDate;
	private String price;
	private String status;

	public BookingDetailsDTO(Long bookingId, String serviceName, String category, String location, String providerName,
			String businessEmailId, String businessPhoneNumber, String bookingDate, String price, String status) {
		super();
		this.bookingId = bookingId;
		this.serviceName = serviceName;
		this.category = category;
		this.location = location;
		this.providerName = providerName;
		this.businessEmailId = businessEmailId;
		this.businessPhoneNumber = businessPhoneNumber;
		this.bookingDate = bookingDate;
		this.price = price;
		this.status = status;
	}

	public BookingDetailsDTO() {
		super();

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getBusinessEmailId() {
		return businessEmailId;
	}

	public void setBusinessEmailId(String businessEmailId) {
		this.businessEmailId = businessEmailId;
	}

	public String getBusinessPhoneNumber() {
		return businessPhoneNumber;
	}

	public void setBusinessPhoneNumber(String businessPhoneNumber) {
		this.businessPhoneNumber = businessPhoneNumber;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
