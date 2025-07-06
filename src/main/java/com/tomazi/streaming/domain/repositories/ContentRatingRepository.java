package com.tomazi.streaming.domain.repositories;

import com.tomazi.streaming.domain.entities.ContentRating;
import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.domain.entities.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRatingRepository extends JpaRepository<ContentRating, Long> {

    Optional<ContentRating> findByUserAndContent(User user, Content content);

    List<ContentRating> findByContent(Content content);

    Page<ContentRating> findByUser(User user, Pageable pageable);

    @Query("SELECT AVG(cr.rating) FROM ContentRating cr WHERE cr.content = :content")
    Double getAverageRatingForContent(@Param("content") Content content);

    @Query("SELECT COUNT(cr) FROM ContentRating cr WHERE cr.content = :content")
    Long getRatingCountForContent(@Param("content") Content content);

    @Query("SELECT cr.rating, COUNT(cr) FROM ContentRating cr WHERE cr.content = :content GROUP BY cr.rating")
    List<Object[]> getRatingDistributionForContent(@Param("content") Content content);
}
