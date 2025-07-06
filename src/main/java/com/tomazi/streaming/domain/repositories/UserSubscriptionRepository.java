package com.tomazi.streaming.domain.repositories;

import com.tomazi.streaming.domain.entities.UserSubscription;
import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.domain.entities.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    List<UserSubscription> findByUser(User user);

    Optional<UserSubscription> findByUserAndActiveTrue(User user);

    List<UserSubscription> findBySubscriptionPlan(SubscriptionPlan subscriptionPlan);

    @Query("SELECT us FROM UserSubscription us WHERE us.endDate < :now AND us.active = true")
    List<UserSubscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    @Query("SELECT us FROM UserSubscription us WHERE us.endDate BETWEEN :now AND :soon AND us.active = true")
    List<UserSubscription> findExpiringSubscriptions(@Param("now") LocalDateTime now, @Param("soon") LocalDateTime soon);

    @Query("SELECT COUNT(us) FROM UserSubscription us WHERE us.subscriptionPlan = :plan AND us.active = true")
    Long countActiveSubscriptionsByPlan(@Param("plan") SubscriptionPlan plan);
}
