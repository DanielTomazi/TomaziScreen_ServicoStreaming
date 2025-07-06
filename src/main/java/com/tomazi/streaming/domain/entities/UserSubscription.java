package com.tomazi.streaming.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "auto_renew")
    private Boolean autoRenew = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Construtores
    public UserSubscription() {}

    public UserSubscription(User user, SubscriptionPlan subscriptionPlan) {
        this.user = user;
        this.subscriptionPlan = subscriptionPlan;
        this.startDate = LocalDateTime.now();
        this.endDate = startDate.plusDays(subscriptionPlan.getDurationDays());
        this.createdAt = LocalDateTime.now();
    }

    // Métodos de negócio
    public boolean isActive() {
        return active && LocalDateTime.now().isBefore(endDate);
    }

    public void cancel() {
        this.active = false;
        this.cancelledAt = LocalDateTime.now();
    }

    public void renew() {
        if (isActive()) {
            this.endDate = endDate.plusDays(subscriptionPlan.getDurationDays());
        } else {
            this.startDate = LocalDateTime.now();
            this.endDate = startDate.plusDays(subscriptionPlan.getDurationDays());
            this.active = true;
            this.cancelledAt = null;
        }
    }

    public long getDaysRemaining() {
        if (!isActive()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getAutoRenew() { return autoRenew; }
    public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSubscription that = (UserSubscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
