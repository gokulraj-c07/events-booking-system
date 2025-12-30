package com.eventsbooking.eventsbooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    private String amount;
    private String date;
    private String status; // Paid / Pending

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    public Bill() {
        // REQUIRED BY JPA
    }

    // Constructor used by BillService
    public Bill(String invoiceNumber, String amount, String date, String status, Booking booking) {
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.booking = booking;
    }

    // Full argument constructor (optional)
    public Bill(Long id, String invoiceNumber, String amount, String date, String status, Booking booking) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.date = date;
        this.status = status;
        this.booking = booking;
    }

    // GETTERS / SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}
