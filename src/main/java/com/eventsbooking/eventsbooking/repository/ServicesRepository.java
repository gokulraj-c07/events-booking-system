package com.eventsbooking.eventsbooking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eventsbooking.eventsbooking.model.Services;
import com.eventsbooking.eventsbooking.model.Services.ServiceStatus;

@Repository
public interface ServicesRepository extends JpaRepository<Services, Long>{

	// Find all services created by a given user (provider)
    List<Services> findByProviderUserId(Long userId);

    List<Services> findByStatus(ServiceStatus status);
    
    // optional convenience
    Optional<Services> findByServiceId(Long serviceId);

    // search by category and location (used by VenueSearchService)
    List<Services> findByServiceCategoryIgnoreCaseAndLocationIgnoreCase(String serviceCategory, String location);

    List<Services> findByServiceCategoryIgnoreCase(String serviceCategory);
    
    @Query("""
    		SELECT s FROM Services s LEFT JOIN Feedback f ON f.booking.service = s WHERE s.status = 'APPROVED'
    		GROUP BY s ORDER BY AVG(f.rating) DESC, COUNT(f.rating) DESC """)
    List<Services> findRecommendedServices();
    
}
