package com.eventsbooking.eventsbooking.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	// Role: USER (or) ADMIN
	private String role;

	@NotBlank
	private String name;

	@Email
	@Column(unique = true)
	private String emailId;

	private String phoneNumber;

	private String gender;

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] profile_pic;

	private String password;

	// OTP for signup/login verification
	private String otp;

	// When the OTP was generated
	private LocalDateTime otpGeneratedTime;

	// Account creation timestamp
	private LocalDateTime createdAt;

	public User(Long userId, String role, @NotBlank String name, @Email String emailId, String phoneNumber,
			String password,
			String gender, byte[] profile_pic, String otp, LocalDateTime otpGeneratedTime, LocalDateTime createdAt) {
		super();
		this.userId = userId;
		this.role = role;
		this.name = name;
		this.emailId = emailId;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.gender = gender;
		this.profile_pic = profile_pic;
		this.otp = otp;
		this.otpGeneratedTime = otpGeneratedTime;
		this.createdAt = createdAt;
	}

	public User() {
		super();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public byte[] getProfile_pic() {
		return profile_pic;
	}

	public void setProfile_pic(byte[] profile_pic) {
		this.profile_pic = profile_pic;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpGeneratedTime() {
		return otpGeneratedTime;
	}

	public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
		this.otpGeneratedTime = otpGeneratedTime;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
