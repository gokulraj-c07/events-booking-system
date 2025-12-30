package com.eventsbooking.eventsbooking.service;

import java.util.List;
import java.util.Optional;

import com.eventsbooking.eventsbooking.model.User;

public interface UserService {

	User sendOtpForSignup(User user);
    void sendOtpForLogin(String emailId);
    boolean verifyOtp(String emailId, String otp);
    User activateUserAfterOtp(String emailId);
    User saveUser(User user);
    Optional<User> getUserByEmail(String emailId);
    Optional<User> getUserById(Long userId);
    void updateUserDetails(User updatedUser);
    
    List<User> getAllUsers();
    List<User> getAdmins();
    User saveAdmin(User admin);
}
