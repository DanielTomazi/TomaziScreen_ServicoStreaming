package com.tomazi.streaming.domain.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "allows_premium_content", nullable = false)
    private Boolean allowsPremiumContent = false;

    @Column(name = "max_concurrent_streams")
    private Integer maxConcurrentStreams = 1;

    @Column(name = "max_quality_resolution")
    private String maxQualityResolution = "720p";

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "subscriptionPlan", fetch = FetchType.LAZY)
    private List<UserSubscription> subscriptions = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtores
    public SubscriptionPlan() {}

    public SubscriptionPlan(String name, String description, BigDecimal price, Integer durationDays) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.createdAt = LocalDateTime.now();
    }

    // Métodos de negócio
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePlan(String name, String description, BigDecimal price, Integer durationDays) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean allowsPremiumContent() {
        return allowsPremiumContent;
    }

    public boolean allowsQuality(String resolution) {
        // Implementar lógica de comparação de qualidade
        return switch (maxQualityResolution) {
            case "1080p" -> true;
            case "720p" -> !"1080p".equals(resolution);
            case "480p" -> "480p".equals(resolution);
            default -> false;
        };
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public Boolean getAllowsPremiumContent() { return allowsPremiumContent; }
    public void setAllowsPremiumContent(Boolean allowsPremiumContent) { this.allowsPremiumContent = allowsPremiumContent; }

    public Integer getMaxConcurrentStreams() { return maxConcurrentStreams; }
    public void setMaxConcurrentStreams(Integer maxConcurrentStreams) { this.maxConcurrentStreams = maxConcurrentStreams; }

    public String getMaxQualityResolution() { return maxQualityResolution; }
    public void setMaxQualityResolution(String maxQualityResolution) { this.maxQualityResolution = maxQualityResolution; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<UserSubscription> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<UserSubscription> subscriptions) { this.subscriptions = subscriptions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionPlan that = (SubscriptionPlan) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
