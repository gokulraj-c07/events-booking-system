package com.eventsbooking.eventsbooking.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventsbooking.eventsbooking.model.User;
import com.eventsbooking.eventsbooking.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Display index page
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Object userId = session.getAttribute("UserId");
        Object name = session.getAttribute("Name");

        if (userId != null && name != null) {
            model.addAttribute("userId", userId);
            model.addAttribute("name", name);
        }
        return "index"; // index.html
    }

    // Display login role page
    @GetMapping("/loginrole")
    public String loginRole(@RequestParam(required = false) String role,
            @RequestParam(required = false) String success,
            Model model) {
        if (role != null)
            model.addAttribute("role", role);
        if (success != null)
            model.addAttribute("success", success);
        return "loginrole"; // loginrole.html
    }

    // Process role selection from index page
    @PostMapping("/chooserole")
    public String chooseRole(@RequestParam String role) {
        return "redirect:/loginrole?role=" + role;
    }

    // Display login page
    @GetMapping("/login")
    public String login(@RequestParam String role, Model model) {
        model.addAttribute("role", role);
        return "login"; // login.html
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String emailId, @RequestParam(required = false) String password,
            @RequestParam String role,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.getUserByEmail(emailId);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email not registered. So, signup first.");
            return "redirect:/login?role=" + role;
        }
        User user = userOpt.get();
        // ------------------------------
        // ADMIN LOGIN (NO OTP)
        // ------------------------------
        if ("ADMIN".equals(role)) {
            if (user.getRole() == null || !user.getRole().equalsIgnoreCase("ADMIN")) {
                redirectAttributes.addFlashAttribute("error", "This account is not an Admin.");
                return "redirect:/login?role=ADMIN";
            }
            if (password == null || !password.equals(user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Invalid Admin password.");
                return "redirect:/login?role=ADMIN";
            }
            // SUCCESS – ADMIN LOGIN
            session.setAttribute("adminId", user.getUserId());
            session.setAttribute("adminName", user.getName());

            return "redirect:/admin/dashboard";
        }
        // ------------------------------
        // USER LOGIN (OTP)
        // ------------------------------
        userService.sendOtpForLogin(emailId);
        redirectAttributes.addFlashAttribute("emailId", emailId);
        redirectAttributes.addFlashAttribute("mode", "login");
        redirectAttributes.addFlashAttribute("success", "OTP has been sent to your registered email ID.");

        return "redirect:/login/verify-otp";
    }

    // LOGIN OTP VERIFY
    @GetMapping("/login/verify-otp")
    public String loginVerifyOtpPage(@ModelAttribute("emailId") String emailId, Model model) {
        model.addAttribute("emailId", emailId);
        return "verify-otp";
    }

    @PostMapping("/login/verify-otp")
    public String verifyLoginOtp(@RequestParam String emailId, @RequestParam String otp,
            HttpSession session, RedirectAttributes redirectAttributes) {
        boolean valid = userService.verifyOtp(emailId, otp);
        if (!valid) {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP or expired OTP.");
            redirectAttributes.addFlashAttribute("emailId", emailId);
            return "redirect:/login/verify-otp";
        }

        User user = userService.getUserByEmail(emailId).get();
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getName());
        redirectAttributes.addFlashAttribute("success", "Login successful!");
        return "redirect:/";
    }

    // Display signup page for first-time users
    @GetMapping("/signup")
    public String signup(@RequestParam String role, Model model) {
        model.addAttribute("role", role);
        model.addAttribute("user", new User());
        return "signup"; // signup.html
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.sendOtpForSignup(user);
            redirectAttributes.addFlashAttribute("emailId", user.getEmailId());
            redirectAttributes.addFlashAttribute("mode", "login");
            redirectAttributes.addFlashAttribute("success", "OTP has been sent to your registered email ID.");
            return "redirect:/signup/verify-otp";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

    // SIGNUP OTP VERIFY
    @GetMapping("/signup/verify-otp")
    public String signupVerifyOtpPage(@ModelAttribute("emailId") String emailId, Model model) {
        model.addAttribute("emailId", emailId);
        return "verify-otp";
    }

    @PostMapping("/signup/verify-otp")
    public String verifySignupOtp(@RequestParam String emailId, @RequestParam String otp,
            RedirectAttributes redirectAttributes) {

        boolean valid = userService.verifyOtp(emailId, otp);
        if (!valid) {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP or expired OTP.");
            redirectAttributes.addFlashAttribute("emailId", emailId);
            return "redirect:/signup/verify-otp";
        }

        userService.activateUserAfterOtp(emailId);

        redirectAttributes.addFlashAttribute("success", "Signup successful! Please login to continue.");
        return "redirect:/login?role=USER";
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Display about page
    @GetMapping("/about")
    public String about() {
        return "about"; // about.html
    }

    // Display events page
    @GetMapping("/events")
    public String events() {
        return "events"; // events.html
    }

    // Display venues page
    @GetMapping("/venues")
    public String venues() {
        return "venues"; // venues.html
    }

    // Display vendors page
    @GetMapping("/vendors")
    public String vendors() {
        return "vendors"; // vendors.html
    }
}
