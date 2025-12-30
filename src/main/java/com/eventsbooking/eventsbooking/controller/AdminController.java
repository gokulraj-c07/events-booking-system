package com.eventsbooking.eventsbooking.controller;

import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.Services.ServiceStatus;
import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.service.BillService;
import com.eventsbooking.eventsbooking.service.BookingService;
import com.eventsbooking.eventsbooking.service.FeedbackService;
import com.eventsbooking.eventsbooking.service.PaymentService;
import com.eventsbooking.eventsbooking.service.ServicesService;
import com.eventsbooking.eventsbooking.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ServicesService servicesService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BillService billService;
    @Autowired
    private FeedbackService feedbackService;

    private boolean checkLogin(HttpSession session) {
        return session.getAttribute("adminId") != null;
    }

    // ADMIN HOME DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        Object adminId = session.getAttribute("UserId");
        Object adminName = session.getAttribute("Name");

        if (adminId != null && adminName != null) {
            model.addAttribute("userId", adminId);
            model.addAttribute("name", adminName);
        }
        model.addAttribute("userCount", userService.getAllUsers().size());
        model.addAttribute("serviceCount", servicesService.getAll().size());
        model.addAttribute("bookingCount", bookingService.getAll().size());
        model.addAttribute("paymentCount", paymentService.getAll().size());
        model.addAttribute("billCount", billService.getAll().size());
        model.addAttribute("feedbackCount", feedbackService.getAll().size());
        return "admin-dashboard";
    }

    // USERS PAGE
    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("admins", userService.getAdmins());
        model.addAttribute("newAdmin", new User());
        return "admin-users";
    }

    @PostMapping("/users/add-admin")
    public String addAdmin(@ModelAttribute User adminUser) {
        adminUser.setRole("ADMIN");
        userService.saveAdmin(adminUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/services")
    public String servicesPage(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/admin/login";
        model.addAttribute("services", servicesService.getAll());
        return "admin-services";
    }

    @GetMapping("/booking")
    public String bookingPage(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/admin/login";
        model.addAttribute("bookings", bookingService.getAll());
        return "admin-booking";
    }

    @GetMapping("/payments")
    public String paymentsPage(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/admin/login";
        model.addAttribute("payments", paymentService.getAll());
        return "admin-payments";
    }

    @GetMapping("/bills")
    public String billsPage(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/admin/login";
        model.addAttribute("bills", billService.getAll());
        return "admin-bills";
    }

    @GetMapping("/feedback")
    public String feedbackPage(HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/admin/login";
        model.addAttribute("feedback", feedbackService.getAll());
        return "admin-feedback";
    }

    @GetMapping("/pending-services")
    public String pendingServices(Model model) {
        model.addAttribute("pending", servicesService.findByStatus(ServiceStatus.PENDING));
        return "admin-service-approval";
    }

    @PostMapping("/service/{id}/approve")
    public String approve(@PathVariable(required = true) Long id) {
        Services s = servicesService.getById(id);
        s.setStatus(ServiceStatus.APPROVED);
        servicesService.saveService(s);
        return "redirect:/admin/pending-services";
    }

    @PostMapping("/service/{id}/reject")
    public String reject(@PathVariable(required = true) Long id) {
        Services s = servicesService.getById(id);
        s.setStatus(ServiceStatus.REJECTED);
        servicesService.saveService(s);
        return "redirect:/admin/pending-services";
    }

    // DETAIL VIEWS
    @GetMapping("/user/{id}/view")
    public String viewUserDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("targetUser", user);
        return "admin-user-detail";
    }

    @GetMapping("/service/{id}/view")
    public String viewServiceDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        model.addAttribute("service", servicesService.getById(id));
        return "admin-service-detail";
    }

    @GetMapping("/booking/{id}/view")
    public String viewBookingDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        model.addAttribute("booking", bookingService.getById(id));
        return "admin-booking-detail";
    }

    @GetMapping("/payment/{id}/view")
    public String viewPaymentDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        model.addAttribute("payment", paymentService.getById(id));
        return "admin-payment-detail";
    }

    @GetMapping("/bill/{id}/view")
    public String viewBillDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        model.addAttribute("bill", billService.getById(id));
        return "admin-bill-detail";
    }

    @GetMapping("/feedback/{id}/view")
    public String viewFeedbackDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (!checkLogin(session))
            return "redirect:/login?role=ADMIN";
        model.addAttribute("feedback", feedbackService.getById(id));
        return "admin-feedback-detail";
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?role=ADMIN";
    }
}
