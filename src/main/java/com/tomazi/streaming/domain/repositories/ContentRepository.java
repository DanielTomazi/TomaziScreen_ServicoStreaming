package com.tomazi.streaming.domain.repositories;

import com.tomazi.streaming.domain.entities.Content;
import com.tomazi.streaming.domain.entities.ContentStatus;
import com.tomazi.streaming.domain.entities.ContentType;
import com.tomazi.streaming.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Page<Content> findByStatus(ContentStatus status, Pageable pageable);

    Page<Content> findByUser(User user, Pageable pageable);

    Page<Content> findByType(ContentType type, Pageable pageable);

    Page<Content> findByIsPublicTrue(Pageable pageable);

    Page<Content> findByIsPremiumTrue(Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' AND c.isPublic = true")
    Page<Content> findPublishedPublicContent(Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Content> searchPublishedContent(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.category.id = :categoryId AND c.status = 'PUBLISHED'")
    Page<Content> findPublishedContentByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT c FROM Content c ORDER BY c.viewCount DESC")
    List<Content> findMostViewed(Pageable pageable);

    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' ORDER BY c.publishedAt DESC")
    List<Content> findRecentlyPublished(Pageable pageable);

    List<Content> findByUserAndStatus(User user, ContentStatus status);
}
