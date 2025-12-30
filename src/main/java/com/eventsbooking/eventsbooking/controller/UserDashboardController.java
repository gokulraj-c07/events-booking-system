package com.eventsbooking.eventsbooking.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventsbooking.eventsbooking.dto.BookingDetailsDTO;
import com.eventsbooking.eventsbooking.dto.BookingListDTO;
import com.eventsbooking.eventsbooking.model.Bill;
import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.repository.BillRepository;
import com.eventsbooking.eventsbooking.repository.BookingRepository;
import com.eventsbooking.eventsbooking.service.FeedbackService;
import com.eventsbooking.eventsbooking.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/userdashboard")
public class UserDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private com.eventsbooking.eventsbooking.service.EmailService emailService;

    @GetMapping
    public String showUserDashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login?role=USER";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUserUserId(userId);

        List<BookingListDTO> currentBookings = new ArrayList<>();
        List<BookingListDTO> pastBookings = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Booking b : bookings) {

            // Skip cancelled bookings
            if ("Cancelled".equalsIgnoreCase(b.getBookingStatus())) {
                continue;
            }

            String dateStr = b.getBookingDate();
            if (dateStr == null || dateStr.isBlank()) {
                continue;
            }

            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(dateStr);
            } catch (Exception e) {
                continue;
            }

            BookingListDTO dto = new BookingListDTO(
                    b.getBookingId(),
                    b.getService().getServiceName(),
                    dateStr,
                    b.getBookingStatus());

            dto.setHasFeedback(feedbackService.hasFeedback(b.getBookingId()));

            if (!bookingDate.isBefore(today)) {
                currentBookings.add(dto);
            } else {
                pastBookings.add(dto);
            }
        }

        model.addAttribute("user", user);

        // Mock data for bookings and bills (replace later when DB ready)
        model.addAttribute("currentBookings", currentBookings);
        model.addAttribute("pastBookings", pastBookings);
        // Fetch bills for user
        List<Bill> bills = billRepository.findByBookingUserUserId(userId);
        model.addAttribute("bills", bills);
        return "userdashboard"; // dashboard.html
    }

    @GetMapping("/view-booking/{id}")
    @ResponseBody
    public ResponseEntity<BookingDetailsDTO> getBookingDetails(@PathVariable(required = true) Long id) {

        Booking b = bookingRepository.findByBookingId(id);

        if (b == null)
            return ResponseEntity.notFound().build();

        BookingDetailsDTO dto = new BookingDetailsDTO(
                b.getBookingId(),
                b.getService().getServiceName(),
                b.getService().getServiceCategory(),
                b.getService().getLocation(),
                b.getProvider().getName(),
                b.getService().getBusinessEmailId(),
                b.getService().getBusinessPhoneNumber(),
                b.getBookingDate(),
                b.getService().getPrice(),
                b.getBookingStatus());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute User updatedUser,
            @RequestParam(value = "profileImage", required = false) MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login?role=USER";
        }

        updatedUser.setUserId(userId);
        if (file != null && !file.isEmpty()) {
            try {
                updatedUser.setProfile_pic(file.getBytes()); // Assuming you have byte[] profileImage in User entity
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Error uploading image.");
                return "redirect:/userdashboard";
            }
        }
        userService.updateUserDetails(updatedUser);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/userdashboard";
    }

    @GetMapping("/user/image/{userId}")
    public ResponseEntity<byte[]> getProfile_pic(@PathVariable(required = true) Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty() || userOpt.get().getProfile_pic() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = userOpt.get().getProfile_pic();
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg") // works for JPG/PNG
                .body(imageBytes);
    }

    @PostMapping("/feedback")
    @ResponseBody
    public ResponseEntity<?> submitFeedback(
            @RequestParam(required = false) Long bookingId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment,
            HttpSession session) {

        if (bookingId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Booking ID is missing"));
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Login required"));
        }

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid rating"));
        }

        com.eventsbooking.eventsbooking.model.Feedback fb = feedbackService.saveFeedback(bookingId, userId, rating,
                comment);

        // 5. after user give the feedback that notification mail is sent to provider.
        emailService.sendFeedbackNotificationToProvider(fb);
        return ResponseEntity.ok(Map.of("ok", true, "bookingId", bookingId));
    }

    @PostMapping("/cancel-booking/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> cancelBooking(@PathVariable(required = true) Long bookingId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Login required"));
        }

        // Find the booking
        Booking booking = bookingRepository.findByBookingId(bookingId);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Booking not found"));
        }

        // Verify the booking belongs to the logged-in user
        if (!booking.getUser().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Unauthorized"));
        }

        // Check if booking is already cancelled
        if ("Cancelled".equalsIgnoreCase(booking.getBookingStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Booking is already cancelled"));
        }

        // Check 24-hour cancellation policy
        String dateStr = booking.getBookingDate();
        if (dateStr != null && !dateStr.isBlank()) {
            try {
                LocalDate bookingDate = LocalDate.parse(dateStr);
                LocalDate today = LocalDate.now();

                // Calculate hours difference (simplified - checks if within same day or next
                // day)
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(today, bookingDate);

                if (daysDiff <= 0) {
                    return ResponseEntity.badRequest().body(
                            Map.of("success", false, "message",
                                    "Cannot cancel booking within 24 hours of the event date"));
                }
            } catch (Exception e) {
                // If date parsing fails, allow cancellation
            }
        }

        // Update booking status to Cancelled
        booking.setBookingStatus("Cancelled");
        bookingRepository.save(booking);

        return ResponseEntity.ok(Map.of("success", true, "message", "Booking cancelled successfully"));
    }
}
