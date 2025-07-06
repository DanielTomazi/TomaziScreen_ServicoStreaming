package com.tomazi.streaming.application.services.impl;

import com.tomazi.streaming.application.services.FileStorageService;
import com.tomazi.streaming.application.services.StreamingService;
import com.tomazi.streaming.domain.entities.Content;
import com.tomazi.streaming.domain.entities.ContentQuality;
import com.tomazi.streaming.domain.entities.ContentStatus;
import com.tomazi.streaming.domain.repositories.ContentRepository;
import com.tomazi.streaming.infrastructure.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StreamingServiceImpl implements StreamingService {

    private final ContentRepository contentRepository;
    private final FileStorageService fileStorageService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final List<QualityConfig> qualityLevels;

    public StreamingServiceImpl(ContentRepository contentRepository,
                               FileStorageService fileStorageService,
                               KafkaTemplate<String, Object> kafkaTemplate,
                               @Value("${streaming.streaming.quality-levels}") List<QualityConfig> qualityLevels) {
        this.contentRepository = contentRepository;
        this.fileStorageService = fileStorageService;
        this.kafkaTemplate = kafkaTemplate;
        this.qualityLevels = qualityLevels;
    }

    @Override
    @Async
    @Transactional
    public void processContentAsync(Content content) {
        try {
            updateProcessingStatus(content.getId(), "PROCESSING");

            generateMultipleQualities(content);
            String thumbnailPath = fileStorageService.generateThumbnail(content.getFilePath());

            content.markAsProcessed(content.getFilePath(),
                                   extractDuration(content.getFilePath()),
                                   fileStorageService.getFileSize(content.getFilePath()));
            content.setThumbnailPath(thumbnailPath);

            contentRepository.save(content);

            kafkaTemplate.send("content-processed", content.getId().toString());

        } catch (Exception e) {
            content.setStatus(ContentStatus.FAILED);
            contentRepository.save(content);
            kafkaTemplate.send("content-processing-failed", content.getId().toString());
            throw new BusinessException("Content processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getContentStream(Content content, String quality) {
        if (!isContentProcessed(content.getId())) {
            throw new BusinessException("Content is not yet processed");
        }

        String qualityFilePath = findQualityFilePath(content, quality);
        return fileStorageService.loadFileAsBytes(qualityFilePath);
    }

    @Override
    public void generateMultipleQualities(Content content) {
        for (QualityConfig config : qualityLevels) {
            try {
                String qualityFilePath = processVideoQuality(content.getFilePath(), config);

                ContentQuality quality = new ContentQuality(content, config.getResolution(),
                                                           config.getBitrate(), qualityFilePath);
                quality.setFileSizeBytes(fileStorageService.getFileSize(qualityFilePath));

                content.getQualities().add(quality);
            } catch (Exception e) {
                System.err.println("Failed to generate quality " + config.getResolution() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public boolean isContentProcessed(Long contentId) {
        return contentRepository.findById(contentId)
                .map(content -> content.getStatus() == ContentStatus.PROCESSED ||
                               content.getStatus() == ContentStatus.PUBLISHED)
                .orElse(false);
    }

    @Override
    @Transactional
    public void updateProcessingStatus(Long contentId, String status) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException("Content not found"));

        content.setStatus(ContentStatus.valueOf(status));
        contentRepository.save(content);
    }

    private String processVideoQuality(String originalPath, QualityConfig config) {
        String qualityPath = originalPath.replace(".", "_" + config.getResolution() + ".");
        return qualityPath;
    }

    private String findQualityFilePath(Content content, String requestedQuality) {
        return content.getQualities().stream()
                .filter(q -> q.getResolution().equals(requestedQuality))
                .map(ContentQuality::getFilePath)
                .findFirst()
                .orElse(content.getFilePath());
    }

    private Integer extractDuration(String filePath) {
        return 3600;
    }

    public static class QualityConfig {
        private String resolution;
        private Integer bitrate;

        public QualityConfig() {}

        public QualityConfig(String resolution, Integer bitrate) {
            this.resolution = resolution;
            this.bitrate = bitrate;
        }

        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }

        public Integer getBitrate() { return bitrate; }
        public void setBitrate(Integer bitrate) { this.bitrate = bitrate; }
    }
}
