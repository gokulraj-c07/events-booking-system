package com.eventsbooking.eventsbooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Generate 6-digit OTP
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    @Override
    public User sendOtpForSignup(User user) {
        Optional<User> existingUser = userRepository.findByEmailId(user.getEmailId());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered: " + user.getEmailId());
        }

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmailId(), otp, user.getName());
        return user;
    }

    @Override
    public void sendOtpForLogin(String emailId) {
        Optional<User> userOpt = userRepository.findByEmailId(emailId);
        if (userOpt.isEmpty())
            return;

        User user = userOpt.get();
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmailId(), otp, user.getName());
    }

    // Verify OTP (valid for 10 minutes)
    @Override
    public boolean verifyOtp(String emailId, String otp) {
        Optional<User> userOpt = userRepository.findByEmailId(emailId);
        if (userOpt.isEmpty())
            return false;

        User user = userOpt.get();
        if (user.getOtp() == null)
            return false;

        boolean valid = user.getOtp().equals(otp)
                && user.getOtpGeneratedTime().isAfter(LocalDateTime.now().minusMinutes(10));

        if (valid) {
            user.setOtp(null); // Clear the OTP after valid
            userRepository.save(user);
        }
        return valid;
    }

    @Override
    public User activateUserAfterOtp(String emailId) {
        User user = userRepository.findByEmailId(emailId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setOtp(null);
        return userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmailId(user.getEmailId());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered: " + user.getEmailId());
        }

        user.setCreatedAt(LocalDateTime.now());
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        emailService.sendOtpEmail(savedUser.getEmailId(), otp, savedUser.getName());
        return savedUser;
    }

    @Override
    public Optional<User> getUserByEmail(String emailId) {
        return userRepository.findByEmailId(emailId);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    @Override
    public void updateUserDetails(User updatedUser) {
        Long userId = updatedUser.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setName(updatedUser.getName());
        existing.setEmailId(updatedUser.getEmailId());
        existing.setPhoneNumber(updatedUser.getPhoneNumber());
        existing.setGender(updatedUser.getGender());

        // update profile picture when uploaded
        if (updatedUser.getProfile_pic() != null && updatedUser.getProfile_pic().length > 0) {
            existing.setProfile_pic(updatedUser.getProfile_pic());
        }

        userRepository.save(existing);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findByRole("USER");
    }

    @Override
    public List<User> getAdmins() {
        return userRepository.findByRole("ADMIN");
    }

    @Override
    public User saveAdmin(User admin) {
        admin.setCreatedAt(LocalDateTime.now());
        return userRepository.save(admin);
    }
}
