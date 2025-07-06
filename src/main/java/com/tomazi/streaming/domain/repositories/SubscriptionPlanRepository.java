package com.tomazi.streaming.domain.repositories;

import com.tomazi.streaming.domain.entities.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByName(String name);

    List<SubscriptionPlan> findByActiveTrue();

    Page<SubscriptionPlan> findByActiveTrue(Pageable pageable);

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.active = true ORDER BY sp.price ASC")
    List<SubscriptionPlan> findActivePlansOrderedByPrice();

    List<SubscriptionPlan> findByAllowsPremiumContentTrue();
}
