package com.eventsbooking.eventsbooking.dto;

public class BillDTO {

	private Long billId;
    private String serviceName;
    private String category;
    private String eventDate;
    private String createdDate;
    private String amount;
    private String status;

    public BillDTO(Long billId, String serviceName, String category,
                   String eventDate, String createdDate,
                   String amount, String status) {
        this.billId = billId;
        this.serviceName = serviceName;
        this.category = category;
        this.eventDate = eventDate;
        this.createdDate = createdDate;
        this.amount = amount;
        this.status = status;
    }

    // getters only (no setters needed)
    public Long getBillId() { return billId; }
    public String getServiceName() { return serviceName; }
    public String getCategory() { return category; }
    public String getEventDate() { return eventDate; }
    public String getCreatedDate() { return createdDate; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
}
