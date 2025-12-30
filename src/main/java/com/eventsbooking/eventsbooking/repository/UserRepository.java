package com.eventsbooking.eventsbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventsbooking.eventsbooking.model.User;

@Repository
public interface UserRepository extends JpaRepository <User, Long>{

	Optional<User> findByEmailId(String emailId);
    Optional<User> findByOtp(String otp);
    Optional<User> findByUserId(Long userId);
    List<User> findByRole(String role);
}
