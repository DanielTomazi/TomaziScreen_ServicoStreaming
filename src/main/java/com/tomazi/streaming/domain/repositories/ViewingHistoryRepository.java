package com.tomazi.streaming.domain.repositories;

import com.tomazi.streaming.domain.entities.ViewingHistory;
import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.domain.entities.Content;
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
public interface ViewingHistoryRepository extends JpaRepository<ViewingHistory, Long> {

    Optional<ViewingHistory> findByUserAndContent(User user, Content content);

    Page<ViewingHistory> findByUser(User user, Pageable pageable);

    Page<ViewingHistory> findByUserOrderByWatchedAtDesc(User user, Pageable pageable);

    List<ViewingHistory> findByUserAndCompletedTrue(User user);

    @Query("SELECT vh FROM ViewingHistory vh WHERE vh.user = :user AND vh.watchedAt >= :since")
    List<ViewingHistory> findUserHistorySince(@Param("user") User user, @Param("since") LocalDateTime since);

    @Query("SELECT vh.content, COUNT(vh) as viewCount FROM ViewingHistory vh " +
           "WHERE vh.watchedAt >= :since GROUP BY vh.content ORDER BY COUNT(vh) DESC")
    List<Object[]> findMostWatchedContentSince(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT AVG(vh.watchTimeSeconds) FROM ViewingHistory vh WHERE vh.content = :content")
    Double getAverageWatchTimeForContent(@Param("content") Content content);
}
