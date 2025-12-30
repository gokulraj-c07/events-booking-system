package com.eventsbooking.eventsbooking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.Bill;
import com.eventsbooking.eventsbooking.model.Booking;
import com.eventsbooking.eventsbooking.repository.BillRepository;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public Bill generateBill(Booking booking) {

        // 1. If totalAmount is null, calculate it
        String amount = booking.getTotalAmount();
        if (amount == null || amount.isEmpty()) {
            amount = booking.getService().getPrice(); // fallback
            booking.setTotalAmount(amount);
        }

        // 2. Status should never be null
        String status = booking.getBookingStatus();
        if (status == null)
            status = "Paid";

        // 3. Create bill with all valid values
        Bill bill = new Bill(
                "INV-" + booking.getBookingId(),
                amount,
                LocalDate.now().toString(),
                status,
                booking);

        return billRepository.save(bill);
    }

    public Bill getBillByBookingId(Long bookingId) {
        return billRepository.findByBookingBookingId(bookingId);
    }

    public List<Bill> getBillsForProvider(Long providerId) {
        return billRepository.findBillsByProvider(providerId);
    }

    public List<Bill> getAll() {
        return billRepository.findAll();
    }

    @SuppressWarnings("null")
    public Bill getById(Long id) {
        return billRepository.findById(id).orElse(null);
    }
}
