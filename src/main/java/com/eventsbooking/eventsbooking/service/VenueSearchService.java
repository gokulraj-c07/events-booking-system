package com.eventsbooking.eventsbooking.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.Services.ServiceStatus;
import com.eventsbooking.eventsbooking.repository.BookingRepository;
import com.eventsbooking.eventsbooking.repository.ServicesRepository;

@Service
public class VenueSearchService {

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // DTO (represents one service + its availability)
    public static class ServicesAvailability {
        private Services service;
        private boolean available;

        public ServicesAvailability(Services service, boolean available) {
            this.service = service;
            this.available = available;
        }

        public Services getService() {
            return service;
        }

        public boolean isAvailable() {
            return available;
        }
    }

    /**
     * Search services by category + location and check availability for a given
     * date.
     *
     * @param serviceCategory vendor type string (e.g. "Wedding Hall")
     * @param location        place string (e.g. "Adyar")
     * @param bookingDate     date string in the same format as stored in
     *                        Booking.bookingDate
     * @return list of ServicesAvailability DTOs (service + available boolean)
     */
    public List<ServicesAvailability> findServices(String serviceCategory, String location, String bookingDate) {

        List<Services> services = servicesRepository.findByServiceCategoryIgnoreCaseAndLocationIgnoreCase(
                serviceCategory.trim(),
                location.trim());
        List<ServicesAvailability> result = new ArrayList<>();
        for (Services s : services) {
            // Skip non-approved services
            if (s.getStatus() != ServiceStatus.APPROVED)
                continue;
            // Skip provider-disabled services
            if (!s.isAvailable()) {
                result.add(new ServicesAvailability(s, false));
                continue;
            }

            boolean booked = bookingRepository
                    .countByServiceServiceIdAndBookingDateAndBookingStatusNot(
                            s.getServiceId(), bookingDate, "Cancelled") > 0;

            result.add(new ServicesAvailability(s, !booked));
        }

        return result;
    }
}
