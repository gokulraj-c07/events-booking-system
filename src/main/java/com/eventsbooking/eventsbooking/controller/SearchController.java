package com.eventsbooking.eventsbooking.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.repository.BookingRepository;
import com.eventsbooking.eventsbooking.repository.ServicesRepository;
import com.eventsbooking.eventsbooking.service.BillService;
//import com.eventsbooking.eventsbooking.service.PaymentService;
import com.eventsbooking.eventsbooking.service.UserService;
import com.eventsbooking.eventsbooking.service.VenueSearchService;
import com.eventsbooking.eventsbooking.service.VenueSearchService.ServicesAvailability;

import jakarta.servlet.http.HttpSession;

@Controller
public class SearchController {

    @Autowired
    private VenueSearchService searchService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // @Autowired
    // private PaymentService paymentService;

    @Autowired
    private BillService billService;

    @Autowired
    private com.eventsbooking.eventsbooking.service.EmailService emailService;

    // GET /search?serviceCategory=...&location=...&eventDate=...
    @GetMapping("/search")
    public String searchVenues(@RequestParam(name = "serviceCategory", required = true) String serviceCategory,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "eventDate", required = false) String bookingDate,
            Model model, HttpSession session) {

        // Optionally attach logged user to model if present
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            User user = userService.getUserById(userId).orElse(null);
            model.addAttribute("user", user);
        }

        List<ServicesAvailability> list = searchService.findServices(serviceCategory, location, bookingDate);

        model.addAttribute("serviceCategory", serviceCategory);
        model.addAttribute("location", location);
        model.addAttribute("eventDate", bookingDate);
        model.addAttribute("results", list);
        model.addAttribute("hasResults", !list.isEmpty());

        return "listvenues";
    }

    @GetMapping("/service/view/{id}")
    public String viewServiceDetails(@PathVariable(required = true) Long id, Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        Long adminId = (Long) session.getAttribute("adminId");
        if (userId == null && adminId == null) {
            return "redirect:/login?role=USER";
        }

        if (id == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }

        Services service = servicesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        model.addAttribute("service", service);
        model.addAttribute("provider", service.getProvider());

        return "viewdetails";
    }

    // STEP 1 — Create booking and show bookinginfo.html
    @PostMapping("/booking/create")
    public String createBooking(@RequestParam(required = true) Long serviceId, @RequestParam String bookingDate,
            Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login?role=USER";
        }

        if (serviceId == null) {
            throw new IllegalArgumentException("Service ID cannot be null");
        }

        User user = userService.getUserById(userId).orElse(null);
        Services service = servicesRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setProvider(service.getProvider());
        booking.setService(service);
        booking.setBookingDate(bookingDate);
        booking.setBookingStatus("NOT CONFIRM");

        bookingRepository.save(booking);

        // 2. if user try to book the but did not complet the payment that sent to mail
        // to provider.
        emailService.sendBookingInitiatedNotification(booking);

        // Pricing calculation
        double amount = Double.parseDouble(service.getPrice());
        double gst = amount * 0.18;
        double total = amount + gst;

        // Add to model
        model.addAttribute("service", service);
        model.addAttribute("provider", service.getProvider());
        model.addAttribute("user", user);
        model.addAttribute("bookingDate", bookingDate);
        model.addAttribute("serviceAmount", amount);
        model.addAttribute("gstAmount", gst);
        model.addAttribute("totalAmount", total);
        model.addAttribute("bookingId", booking.getBookingId());

        return "bookinginfo";
    }

    @Autowired
    private com.eventsbooking.eventsbooking.repository.PaymentRepository paymentRepository;

    // STEP A: Receive bookingId & load payment page
    @PostMapping("/payment")
    public String paymentPage(@RequestParam(required = true) Long bookingId, Model model, HttpSession session) {

        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        model.addAttribute("booking", booking);
        model.addAttribute("service", booking.getService());
        model.addAttribute("provider", booking.getProvider());
        model.addAttribute("user", booking.getUser());

        double amount = Double.parseDouble(booking.getService().getPrice());
        double gst = amount * 0.18;
        double total = amount + gst;

        model.addAttribute("amount", amount);
        model.addAttribute("gst", gst);
        model.addAttribute("total", total);

        return "payment";
    }

    // STEP B: Process final payment submission
    @PostMapping("/payment/complete")
    public String completePayment(@RequestParam(required = true) Long bookingId, @RequestParam String paymentMethod,
            RedirectAttributes redirectAttributes) {

        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        // 🔒 Check if service already booked by someone else
        long count = bookingRepository.countByServiceServiceIdAndBookingDateAndBookingStatusNot(
                booking.getService().getServiceId(),
                booking.getBookingDate(), "Cancelled");
        if (count > 1) {
            redirectAttributes.addFlashAttribute("error", "Sorry! This service was just booked by another user.");
            return "redirect:/userdashboard";
        }

        booking.setBookingStatus("CONFIRM");
        booking.setCreatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Save Payment Details
        double amount = Double.parseDouble(booking.getService().getPrice());
        double gst = amount * 0.18;
        double total = amount + gst;

        com.eventsbooking.eventsbooking.model.Payment payment = new com.eventsbooking.eventsbooking.model.Payment();
        payment.setBooking(booking);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("SUCCESS");
        payment.setTransactionId("TXN" + System.currentTimeMillis()); // Mock Transaction ID
        payment.setAmountPaid(String.valueOf(total));
        payment.setCreateAt(LocalDateTime.now());

        paymentRepository.save(payment);

        com.eventsbooking.eventsbooking.model.Bill bill = billService.generateBill(booking);

        // 1 & 3. send payment success and invoice emails
        emailService.sendPaymentSuccessEmails(booking, payment);
        emailService.sendInvoiceEmail(bill);

        redirectAttributes.addFlashAttribute("success", "Payment successful! Booking confirmed.");
        return "redirect:/userdashboard";
    }
}
