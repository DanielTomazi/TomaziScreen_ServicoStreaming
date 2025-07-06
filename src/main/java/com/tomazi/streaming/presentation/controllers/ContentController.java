package com.tomazi.streaming.presentation.controllers;

import com.tomazi.streaming.application.services.ContentService;
import com.tomazi.streaming.application.services.UserService;
import com.tomazi.streaming.domain.entities.Content;
import com.tomazi.streaming.domain.entities.ContentType;
import com.tomazi.streaming.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;
    private final UserService userService;

    public ContentController(ContentService contentService, UserService userService) {
        this.contentService = contentService;
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<Content> uploadContent(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type") ContentType type,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPublic", defaultValue = "true") boolean isPublic,
            @RequestParam(value = "isPremium", defaultValue = "false") boolean isPremium,
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        Content content = contentService.uploadContent(user, title, description, type,
                                                      null, file, isPublic, isPremium);

        return ResponseEntity.status(HttpStatus.CREATED).body(content);
    }

    @GetMapping
    public ResponseEntity<Page<Content>> getPublishedContent(Pageable pageable) {
        Page<Content> content = contentService.findPublishedContent(pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> getContentById(@PathVariable Long id) {
        return contentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Content>> searchContent(
            @RequestParam String query,
            Pageable pageable) {
        Page<Content> content = contentService.searchContent(query, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Content>> getContentByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        Page<Content> content = contentService.findContentByCategory(categoryId, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Content>> getTrendingContent() {
        List<Content> content = contentService.findMostViewed(10);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Content>> getRecentContent() {
        List<Content> content = contentService.findRecentlyPublished(10);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<Content>> getMyContent(
            Pageable pageable,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        Page<Content> content = contentService.findContentByUser(user, pageable);
        return ResponseEntity.ok(content);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<Content> publishContent(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        Content content = contentService.publishContent(id, user);
        return ResponseEntity.ok(content);
    }

    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<Content> unpublishContent(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        Content content = contentService.unpublishContent(id, user);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        contentService.deleteContent(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<byte[]> streamContent(
            @PathVariable Long id,
            @RequestParam(defaultValue = "720p") String quality,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        if (!contentService.canUserAccessContent(id, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        byte[] contentData = contentService.getContentStream(id, user, quality);
        contentService.incrementViewCount(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(contentData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(contentData);
    }

    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> likeContent(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        contentService.likeContent(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeContent(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        contentService.unlikeContent(id, user);
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
