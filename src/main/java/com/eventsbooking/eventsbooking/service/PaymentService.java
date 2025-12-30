package com.eventsbooking.eventsbooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.model.Payment;
import com.eventsbooking.eventsbooking.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment savePayment(Booking booking, String method, String amount) {

        Payment p = new Payment();
        p.setBooking(booking);
        p.setPaymentMethod(method);
        p.setAmountPaid(amount);

        p.setPaymentStatus("SUCCESS");
        p.setTransactionId("TXN" + System.currentTimeMillis());
        p.setCreateAt(LocalDateTime.now());
        return paymentRepository.save(p);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @SuppressWarnings("null")
    public Payment getById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }
}
