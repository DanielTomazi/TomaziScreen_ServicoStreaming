package com.tomazi.streaming.application.services.impl;

import com.tomazi.streaming.application.services.FileStorageService;
import com.tomazi.streaming.infrastructure.exceptions.BusinessException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public FileStorageServiceImpl(MinioClient minioClient,
                                 @Value("${streaming.storage.minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public String storeFile(MultipartFile file, Long contentId) {
        try {
            String fileName = generateFileName(file.getOriginalFilename(), contentId);

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            return fileName;
        } catch (Exception e) {
            throw new BusinessException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] loadFileAsBytes(String filePath) {
        try {
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int bytesRead;
            byte[] data = new byte[8192];

            while ((bytesRead = stream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            return buffer.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Failed to load file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );
        } catch (Exception e) {
            throw new BusinessException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateThumbnail(String videoFilePath) {
        return "thumbnails/" + videoFilePath.replace("videos/", "").replace(".mp4", "_thumb.jpg");
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            minioClient.statObject(
                io.minio.StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getFileSize(String filePath) {
        try {
            return minioClient.statObject(
                io.minio.StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            ).size();
        } catch (Exception e) {
            throw new BusinessException("Failed to get file size: " + e.getMessage(), e);
        }
    }

    private String generateFileName(String originalFileName, Long contentId) {
        String extension = getFileExtension(originalFileName);
        return "content/" + contentId + "/" + UUID.randomUUID().toString() + "." + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
