package com.eventsbooking.eventsbooking.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.Services.ServiceStatus;
import com.eventsbooking.eventsbooking.repository.ServicesRepository;

@Service
public class ServicesServiceImpl implements ServicesService {

    @Autowired
    private ServicesRepository servicesRepository;

    @Override
    public List<Services> getServiceByUserId(Long userId) {
        return servicesRepository.findByProviderUserId(userId);
    }

    // Save OR Update Services
    @Override
    @SuppressWarnings("null")
    public Services saveService(Services services) {
        return servicesRepository.save(services);
    }

    @Override
    public void deleteService(Long serviceId, Long userId) {
        Services service = servicesRepository.findById(java.util.Objects.requireNonNull(serviceId))
                .orElseThrow(() -> new RuntimeException("Service not found"));
        if (service.getProvider() == null || !service.getProvider().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized deletion attempt");
        }
        servicesRepository.delete(service);
    }

    @Override
    public Services getById(Long serviceId) {
        return servicesRepository.findById(java.util.Objects.requireNonNull(serviceId))
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    // ONLY APPROVED SERVICES SHOWN IN USER LISTINGS
    @Override
    public List<Services> getApprovedServices() {
        return servicesRepository.findByStatus(ServiceStatus.APPROVED);
    }

    @Override
    public List<Services> findByStatus(ServiceStatus status) {
        return servicesRepository.findByStatus(status);
    }

    // SEARCH LOGIC FOR USERS (ONLY APPROVED OR UNAVAILABLE SERVICES)
    @Override
    public List<Services> getServicesForUserListing(String category, String location) {
        List<Services> list = servicesRepository.findByStatus(ServiceStatus.APPROVED);

        return list.stream()
                .filter(s -> category == null || s.getServiceCategory().equalsIgnoreCase(category))
                .filter(s -> location == null || s.getLocation().equalsIgnoreCase(location))
                .toList();
    }

    // PROVIDER MARKS SERVICE TEMPORARILY UNAVAILABLE
    @Override
    public void markUnavailable(Long serviceId, Long providerId) {
        Services service = getById(serviceId);

        if (!service.getProvider().getUserId().equals(providerId)) {
            throw new RuntimeException("Unauthorized operation");
        }

        service.setStatus(ServiceStatus.UNAVAILABLE);
        servicesRepository.save(service);
    }

    // PROVIDER MARKS SERVICE AVAILABLE (ONLY IF APPROVED)
    @Override
    public void markAvailable(Long serviceId, Long providerId) {
        Services service = getById(serviceId);

        if (!service.getProvider().getUserId().equals(providerId)) {
            throw new RuntimeException("Unauthorized operation");
        }

        if (service.getStatus() != ServiceStatus.UNAVAILABLE) {
            throw new RuntimeException("Service must be UNAVAILABLE before marking available");
        }

        service.setStatus(ServiceStatus.APPROVED);
        servicesRepository.save(service);
    }

    @Override
    public List<Services> getAll() {
        return servicesRepository.findAll();
    }
}
