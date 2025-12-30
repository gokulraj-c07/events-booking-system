package com.eventsbooking.eventsbooking.service;

import java.util.List;

import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.Services.ServiceStatus;

public interface ServicesService {

	List<Services> getServiceByUserId(Long userId);

    Services saveService(Services services);

    void deleteService(Long serviceId, Long userId);
    
    Services getById(Long serviceId);

    List<Services> getApprovedServices();  

    List<Services> findByStatus(ServiceStatus status);

    List<Services> getServicesForUserListing(String category, String location);

    void markUnavailable(Long serviceId, Long providerId);

    void markAvailable(Long serviceId, Long providerId);
    
    public List<Services> getAll();
}
