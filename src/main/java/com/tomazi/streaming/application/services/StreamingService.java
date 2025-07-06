package com.tomazi.streaming.application.services;

import com.tomazi.streaming.domain.entities.Content;

public interface StreamingService {

    void processContentAsync(Content content);

    byte[] getContentStream(Content content, String quality);

    void generateMultipleQualities(Content content);

    boolean isContentProcessed(Long contentId);

    void updateProcessingStatus(Long contentId, String status);
}
