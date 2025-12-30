package com.eventsbooking.eventsbooking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "services")
public class Services {

	public enum ServiceStatus {
		PENDING,
		APPROVED,
		REJECTED,
		UNAVAILABLE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long serviceId;

	// the user who provides this service
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User provider;

	private String serviceCategory;
	private String serviceName;
	private String businessEmailId;
	private String businessPhoneNumber;
	private String address;
	private String location;
	@Column(length = 2000)
	private String description;
	private String imageUrl;
	private String price;
	private String advanceAmount;
	@Column(length = 2000)
	private String serviceImages; // Comma separated paths

	@Enumerated(EnumType.STRING)
	private ServiceStatus status = ServiceStatus.PENDING;
	private boolean available = true; // true = available, false = unavailable

	public Services(Long serviceId, User provider, String serviceCategory, String serviceName, String businessEmailId,
			String businessPhoneNumber, String address, String location, String description,
			boolean available,
			String imageUrl) {
		super();
		this.serviceId = serviceId;
		this.provider = provider;
		this.serviceCategory = serviceCategory;
		this.serviceName = serviceName;
		this.businessEmailId = businessEmailId;
		this.businessPhoneNumber = businessPhoneNumber;
		this.address = address;
		this.location = location;
		this.description = description;
		this.imageUrl = imageUrl;
		this.available = available;
	}

	public Services() {
		super();
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public User getProvider() {
		return provider;
	}

	public void setProvider(User provider) {
		this.provider = provider;
	}

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public void setStatus(ServiceStatus status) {
		this.status = status;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(String advanceAmount) {
		this.advanceAmount = advanceAmount;
	}

	public String getServiceImages() {
		return serviceImages;
	}

	public void setServiceImages(String serviceImages) {
		this.serviceImages = serviceImages;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}
