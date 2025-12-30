package com.eventsbooking.eventsbooking.controller;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.eventsbooking.eventsbooking.dto.BillDTO;
import com.eventsbooking.eventsbooking.dto.ProviderBookingDTO;
import com.eventsbooking.eventsbooking.dto.ProviderBookingDetailsDTO;
import com.eventsbooking.eventsbooking.model.Bill;
import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.model.Feedback;
import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.Services.ServiceStatus;
import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.repository.BillRepository;
import com.eventsbooking.eventsbooking.repository.BookingRepository;
import com.eventsbooking.eventsbooking.service.FeedbackService;
import com.eventsbooking.eventsbooking.service.ServicesService;
import com.eventsbooking.eventsbooking.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/providerdashboard")
public class ProviderDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BillRepository billRepository;

    // ------------------------------------------------------------
    // PROVIDER DASHBOARD HOME
    // ------------------------------------------------------------
    @GetMapping
    public String showProviderDashboard(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login?role=USER";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        return "prodashboard";
    }

    // ------------------------------------------------------------
    // MANAGE SERVICE PAGE (addservices.html)
    // ------------------------------------------------------------
    @GetMapping("/addservices")
    public String manageService(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login?role=USER";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("serviceForm", new Services());
        model.addAttribute("serviceList", servicesService.getServiceByUserId(userId));

        return "addservices";
    }

    // SAVE SERVICE
    @PostMapping("/save")
    public String saveService(
            @ModelAttribute("serviceForm") Services serviceForm,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Login again.");
            return "redirect:/login?role=USER";
        }

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        serviceForm.setProvider(user);

        try {
            String uploadDir = "uploads/";

            // 1. Handle Main Logo (imageFile)
            if (serviceForm.getServiceId() != null) {
                // ... (Existing logic for editing)
                Services old = servicesService.getById(serviceForm.getServiceId());
                if (old.getStatus() == ServiceStatus.REJECTED)
                    serviceForm.setStatus(ServiceStatus.PENDING);

                if (!imageFile.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_logo_" + imageFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, imageFile.getBytes());
                    serviceForm.setImageUrl(fileName);
                } else {
                    serviceForm.setImageUrl(old.getImageUrl());
                }

                // Preserve existing additional images if not replaced (logic could be more
                // complex, but keeping simple here)
                if (additionalImages == null || additionalImages.isEmpty() || additionalImages.get(0).isEmpty()) {
                    serviceForm.setServiceImages(old.getServiceImages());
                }
            } else {
                // New Service
                serviceForm.setStatus(ServiceStatus.PENDING);
                if (!imageFile.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_logo_" + imageFile.getOriginalFilename();
                    Path filePath = Paths.get(uploadDir + fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, imageFile.getBytes());
                    serviceForm.setImageUrl(fileName);
                }
            }

            // 2. Handle Additional Images (5 Service Images)
            if (additionalImages != null && !additionalImages.isEmpty() && !additionalImages.get(0).isEmpty()) {
                List<String> imageNames = new ArrayList<>();
                for (MultipartFile file : additionalImages) {
                    if (!file.isEmpty()) {
                        String fileName = System.currentTimeMillis() + "_extra_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir + fileName);
                        Files.createDirectories(filePath.getParent());
                        Files.write(filePath, file.getBytes());
                        imageNames.add(fileName);
                    }
                }
                // Join with comma
                if (!imageNames.isEmpty()) {
                    String joined = String.join(",", imageNames);
                    serviceForm.setServiceImages(joined);
                }
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log error
            redirectAttributes.addFlashAttribute("error", "Image upload failed.");
        }

        servicesService.saveService(serviceForm);
        redirectAttributes.addFlashAttribute("success", "Service submitted. Waiting for admin approval.");

        return "redirect:/providerdashboard/addservices";
    }

    @GetMapping("/edit/{id}")
    public String editService(@PathVariable("id") Long serviceId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Please login again.");
            return "redirect:/login?role=USER";
        }

        Services service = servicesService.getById(serviceId);
        if (!service.getProvider().getUserId().equals(userId)) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/providerdashboard/addservices";
        }

        model.addAttribute("serviceForm", service);
        model.addAttribute("serviceList", servicesService.getServiceByUserId(userId));

        return "addservices";
    }

    @PostMapping("/update-availability/{id}")
    @ResponseBody
    public void updateAvailability(@PathVariable(required = true) Long id, @RequestParam boolean available,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return;

        Services service = servicesService.getById(id);

        if (!service.getProvider().getUserId().equals(userId))
            return;

        service.setAvailable(available);
        servicesService.saveService(service);
    }

    // DELETE SERVICE
    @GetMapping("/delete/{id}")
    public String deleteService(
            @PathVariable("id") Long serviceId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please log in again.");
            return "redirect:/login?role=USER";
        }

        servicesService.deleteService(serviceId, userId);
        redirectAttributes.addFlashAttribute("success", "Service deleted successfully!");

        return "redirect:/providerdashboard/addservices";
    }

    // ------------------------------------------------------------
    // CHECK STATUS PAGE (checkstatus.html)
    // ------------------------------------------------------------
    @GetMapping("/checkstatus")
    public String checkStatus(HttpSession session, Model model,
            @RequestParam(value = "category", required = false, defaultValue = "All") String filterCategory) {

        Long providerId = (Long) session.getAttribute("userId");
        if (providerId == null)
            return "redirect:/login?role=USER";

        List<Booking> bookings = bookingRepository.findByProviderUserId(providerId);

        List<ProviderBookingDTO> currentBookings = new ArrayList<>();
        List<ProviderBookingDTO> pastBookings = new ArrayList<>();

        LocalDate today = LocalDate.now();

        // Populate Categories for Filter
        List<String> categories = List.of("All", "Wedding Hall", "Catering", "Decorator", "Photographer", "DJ");
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", filterCategory);

        for (Booking b : bookings) {

            // Filter Check
            if (!"All".equalsIgnoreCase(filterCategory) &&
                    !b.getService().getServiceCategory().equalsIgnoreCase(filterCategory)) {
                continue;
            }

            Bill bill = billRepository.findByBookingBookingId(b.getBookingId());
            String paymentStatus = (bill != null && "Paid".equalsIgnoreCase(bill.getStatus())) ? "Paid" : "Pending";

            // Check feedback
            Feedback fb = feedbackService.getFeedbackByBookingId(b.getBookingId());
            boolean hasFeedback = (fb != null);

            ProviderBookingDTO dto = new ProviderBookingDTO(
                    b.getBookingId(),
                    b.getService().getServiceName(),
                    b.getService().getServiceCategory(),
                    b.getUser().getName(),
                    b.getBookingDate(),
                    b.getBookingStatus(),
                    hasFeedback,
                    paymentStatus,
                    (b.getCreatedAt() != null) ? b.getCreatedAt().toString() : "");

            LocalDate eventDate = LocalDate.parse(b.getBookingDate());

            if ("Paid".equalsIgnoreCase(paymentStatus) && !eventDate.isBefore(today)) {
                currentBookings.add(dto);
            } else {
                pastBookings.add(dto);
            }
        }

        model.addAttribute("currentBookings", currentBookings);
        model.addAttribute("pastBookings", pastBookings);

        return "checkstatus";
    }

    @GetMapping("/booking-details/{id}")
    @ResponseBody
    public ResponseEntity<ProviderBookingDetailsDTO> getBookingDetails(@PathVariable(required = true) Long id,
            HttpSession session) {

        Long providerId = (Long) session.getAttribute("userId");
        if (providerId == null)
            return ResponseEntity.status(401).build();

        Booking b = bookingRepository.findByBookingId(id);
        if (b == null || !b.getProvider().getUserId().equals(providerId)) {
            return ResponseEntity.status(403).build();
        }

        Bill bill = billRepository.findByBookingBookingId(b.getBookingId());
        String paymentStatus = (bill != null && "Paid".equalsIgnoreCase(bill.getStatus())) ? "Paid" : "Pending";

        Feedback fb = feedbackService.getFeedbackByBookingId(b.getBookingId());
        Integer rating = (fb != null) ? fb.getRating() : null;
        String comment = (fb != null) ? fb.getComment() : null;

        ProviderBookingDetailsDTO dto = new ProviderBookingDetailsDTO(
                b.getBookingId(),
                b.getService().getServiceName(),
                b.getService().getServiceCategory(),
                b.getService().getLocation(),
                b.getUser().getName(),
                b.getUser().getEmailId(),
                b.getUser().getPhoneNumber(),
                b.getBookingDate(),
                b.getService().getPrice(),
                b.getService().getAdvanceAmount(),
                b.getBookingStatus(),
                paymentStatus,
                rating,
                comment);

        return ResponseEntity.ok(dto);
    }

    // Accept or reject booking (provider action)
    @PostMapping("/booking/{id}/action")
    @ResponseBody
    public ResponseEntity<String> bookingAction(
            @PathVariable(required = true) Long id,
            @RequestParam("action") String action,
            HttpSession session) {

        Long providerId = (Long) session.getAttribute("userId");
        if (providerId == null)
            return ResponseEntity.status(401).body("Unauthorized");

        Booking b = bookingRepository.findByBookingId(id);

        if (b == null || !b.getProvider().getUserId().equals(providerId))
            return ResponseEntity.status(403).body("Forbidden");

        if ("accept".equalsIgnoreCase(action)) {
            b.setBookingStatus("ACCEPTED");
        } else if ("reject".equalsIgnoreCase(action)) {
            b.setBookingStatus("REJECTED");
        } else {
            return ResponseEntity.badRequest().body("Invalid action");
        }

        bookingRepository.save(b);

        return ResponseEntity.ok("OK");
    }

    // ------------------------------------------------------------
    // BILLS PAGE (bill.html)
    // ------------------------------------------------------------
    @GetMapping("/bills")
    public String showBillsPage(HttpSession session, Model model) {

        Long providerId = (Long) session.getAttribute("userId");
        if (providerId == null)
            return "redirect:/loginrole";

        model.addAttribute("providerId", providerId); // pass provider ID for ajax
        return "probill"; // page name
    }

    @GetMapping("/bills/data")
    @ResponseBody
    public ResponseEntity<List<BillDTO>> getBills(HttpSession session) {

        Long providerId = (Long) session.getAttribute("userId");
        if (providerId == null)
            return ResponseEntity.status(401).build();

        List<Bill> bills = billRepository.findBillsByProvider(providerId);

        List<BillDTO> dtoList = bills.stream()
                .map(b -> new BillDTO(
                        b.getId(),
                        b.getBooking().getService().getServiceName(),
                        b.getBooking().getService().getServiceCategory(),
                        b.getBooking().getBookingDate(),
                        (b.getDate() != null) ? b.getDate() : "",
                        (b.getAmount() != null) ? b.getAmount() : "0",
                        b.getStatus()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}
