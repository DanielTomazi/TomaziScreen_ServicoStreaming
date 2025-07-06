package com.tomazi.streaming.application.services;

import com.tomazi.streaming.domain.entities.Content;
import com.tomazi.streaming.domain.entities.ContentType;
import com.tomazi.streaming.domain.entities.User;
import com.tomazi.streaming.domain.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ContentService {

    Content uploadContent(User user, String title, String description, ContentType type,
                         Category category, MultipartFile file, boolean isPublic, boolean isPremium);

    Optional<Content> findById(Long id);

    Page<Content> findPublishedContent(Pageable pageable);

    Page<Content> findContentByUser(User user, Pageable pageable);

    Page<Content> findContentByCategory(Long categoryId, Pageable pageable);

    Page<Content> searchContent(String searchTerm, Pageable pageable);

    List<Content> findMostViewed(int limit);

    List<Content> findRecentlyPublished(int limit);

    Content publishContent(Long contentId, User user);

    Content unpublishContent(Long contentId, User user);

    void deleteContent(Long contentId, User user);

    Content updateContent(Long contentId, User user, String title, String description,
                         Category category, boolean isPublic, boolean isPremium);

    void incrementViewCount(Long contentId);

    void likeContent(Long contentId, User user);

    void unlikeContent(Long contentId, User user);

    byte[] getContentStream(Long contentId, User user, String quality);

    boolean canUserAccessContent(Long contentId, User user);
}
