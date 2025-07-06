package com.tomazi.streaming.application.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file, Long contentId);

    byte[] loadFileAsBytes(String filePath);

    void deleteFile(String filePath);

    String generateThumbnail(String videoFilePath);

    boolean fileExists(String filePath);

    long getFileSize(String filePath);
}
