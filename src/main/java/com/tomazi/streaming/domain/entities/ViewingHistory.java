package com.tomazi.streaming.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "viewing_history")
public class ViewingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(name = "watch_time_seconds", nullable = false)
    private Integer watchTimeSeconds;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @Column(name = "last_position_seconds")
    private Integer lastPositionSeconds = 0;

    @Column(name = "quality_watched")
    private String qualityWatched;

    @Column(name = "watched_at", nullable = false)
    private LocalDateTime watchedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtores
    public ViewingHistory() {}

    public ViewingHistory(User user, Content content) {
        this.user = user;
        this.content = content;
        this.watchTimeSeconds = 0;
        this.watchedAt = LocalDateTime.now();
    }

    // Métodos de negócio
    public void updateProgress(Integer currentPositionSeconds, String quality) {
        this.lastPositionSeconds = currentPositionSeconds;
        this.qualityWatched = quality;
        this.watchTimeSeconds = Math.max(this.watchTimeSeconds, currentPositionSeconds);
        this.updatedAt = LocalDateTime.now();

        // Marca como completo se assistiu mais de 90% do conteúdo
        if (content.getDurationSeconds() != null &&
            currentPositionSeconds >= (content.getDurationSeconds() * 0.9)) {
            this.completed = true;
        }
    }

    public double getWatchPercentage() {
        if (content.getDurationSeconds() == null || content.getDurationSeconds() == 0) {
            return 0.0;
        }
        return (double) watchTimeSeconds / content.getDurationSeconds() * 100;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public Integer getWatchTimeSeconds() { return watchTimeSeconds; }
    public void setWatchTimeSeconds(Integer watchTimeSeconds) { this.watchTimeSeconds = watchTimeSeconds; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Integer getLastPositionSeconds() { return lastPositionSeconds; }
    public void setLastPositionSeconds(Integer lastPositionSeconds) { this.lastPositionSeconds = lastPositionSeconds; }

    public String getQualityWatched() { return qualityWatched; }
    public void setQualityWatched(String qualityWatched) { this.qualityWatched = qualityWatched; }

    public LocalDateTime getWatchedAt() { return watchedAt; }
    public void setWatchedAt(LocalDateTime watchedAt) { this.watchedAt = watchedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewingHistory that = (ViewingHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
