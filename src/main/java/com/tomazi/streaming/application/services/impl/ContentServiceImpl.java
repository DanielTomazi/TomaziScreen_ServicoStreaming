package com.tomazi.streaming.application.services.impl;

import com.tomazi.streaming.application.services.ContentService;
import com.tomazi.streaming.application.services.FileStorageService;
import com.tomazi.streaming.application.services.StreamingService;
import com.tomazi.streaming.domain.entities.*;
import com.tomazi.streaming.domain.repositories.ContentRepository;
import com.tomazi.streaming.domain.repositories.ContentRatingRepository;
import com.tomazi.streaming.infrastructure.exceptions.BusinessException;
import com.tomazi.streaming.infrastructure.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final ContentRatingRepository contentRatingRepository;
    private final FileStorageService fileStorageService;
    private final StreamingService streamingService;

    public ContentServiceImpl(ContentRepository contentRepository,
                             ContentRatingRepository contentRatingRepository,
                             FileStorageService fileStorageService,
                             StreamingService streamingService) {
        this.contentRepository = contentRepository;
        this.contentRatingRepository = contentRatingRepository;
        this.fileStorageService = fileStorageService;
        this.streamingService = streamingService;
    }

    @Override
    public Content uploadContent(User user, String title, String description, ContentType type,
                                Category category, MultipartFile file, boolean isPublic, boolean isPremium) {
        validateUserCanCreateContent(user);
        validateContentData(title, file);

        Content content = new Content(title, description, type, user);
        content.setCategory(category);
        content.setIsPublic(isPublic);
        content.setIsPremium(isPremium);

        Content savedContent = contentRepository.save(content);

        try {
            String filePath = fileStorageService.storeFile(file, savedContent.getId());
            savedContent.setFilePath(filePath);
            savedContent.setFileSizeBytes(file.getSize());

            streamingService.processContentAsync(savedContent);

            return contentRepository.save(savedContent);
        } catch (Exception e) {
            contentRepository.delete(savedContent);
            throw new BusinessException("Failed to upload content: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Content> findById(Long id) {
        return contentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Content> findPublishedContent(Pageable pageable) {
        return contentRepository.findPublishedPublicContent(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Content> findContentByUser(User user, Pageable pageable) {
        return contentRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Content> findContentByCategory(Long categoryId, Pageable pageable) {
        return contentRepository.findPublishedContentByCategory(categoryId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Content> searchContent(String searchTerm, Pageable pageable) {
        return contentRepository.searchPublishedContent(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Content> findMostViewed(int limit) {
        return contentRepository.findMostViewed(PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Content> findRecentlyPublished(int limit) {
        return contentRepository.findRecentlyPublished(PageRequest.of(0, limit));
    }

    @Override
    public Content publishContent(Long contentId, User user) {
        Content content = getContentById(contentId);
        validateUserCanManageContent(user, content);

        content.publish();
        return contentRepository.save(content);
    }

    @Override
    public Content unpublishContent(Long contentId, User user) {
        Content content = getContentById(contentId);
        validateUserCanManageContent(user, content);

        content.unpublish();
        return contentRepository.save(content);
    }

    @Override
    public void deleteContent(Long contentId, User user) {
        Content content = getContentById(contentId);
        validateUserCanManageContent(user, content);

        try {
            if (content.getFilePath() != null) {
                fileStorageService.deleteFile(content.getFilePath());
            }
            contentRepository.delete(content);
        } catch (Exception e) {
            throw new BusinessException("Failed to delete content: " + e.getMessage(), e);
        }
    }

    @Override
    public Content updateContent(Long contentId, User user, String title, String description,
                                Category category, boolean isPublic, boolean isPremium) {
        Content content = getContentById(contentId);
        validateUserCanManageContent(user, content);

        content.setTitle(title);
        content.setDescription(description);
        content.setCategory(category);
        content.setIsPublic(isPublic);
        content.setIsPremium(isPremium);

        return contentRepository.save(content);
    }

    @Override
    public void incrementViewCount(Long contentId) {
        Content content = getContentById(contentId);
        content.incrementViewCount();
        contentRepository.save(content);
    }

    @Override
    public void likeContent(Long contentId, User user) {
        Content content = getContentById(contentId);

        Optional<ContentRating> existingRating = contentRatingRepository.findByUserAndContent(user, content);
        if (existingRating.isEmpty()) {
            content.incrementLikeCount();
            contentRepository.save(content);
        }
    }

    @Override
    public void unlikeContent(Long contentId, User user) {
        Content content = getContentById(contentId);

        Optional<ContentRating> existingRating = contentRatingRepository.findByUserAndContent(user, content);
        if (existingRating.isPresent()) {
            content.decrementLikeCount();
            contentRepository.save(content);
            contentRatingRepository.delete(existingRating.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getContentStream(Long contentId, User user, String quality) {
        Content content = getContentById(contentId);

        if (!canUserAccessContent(contentId, user)) {
            throw new BusinessException("User does not have access to this content");
        }

        return streamingService.getContentStream(content, quality);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessContent(Long contentId, User user) {
        Content content = getContentById(contentId);
        return content.isAccessibleBy(user);
    }

    private void validateUserCanCreateContent(User user) {
        if (!user.isContentCreator()) {
            throw new BusinessException("User does not have permission to create content");
        }
    }

    private void validateUserCanManageContent(User user, Content content) {
        if (!content.getUser().equals(user) && !user.canManageContent()) {
            throw new BusinessException("User does not have permission to manage this content");
        }
    }

    private void validateContentData(String title, MultipartFile file) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException("Content title cannot be empty");
        }

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Content file cannot be empty");
        }
    }

    private Content getContentById(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content not found with id: " + contentId));
    }
}
