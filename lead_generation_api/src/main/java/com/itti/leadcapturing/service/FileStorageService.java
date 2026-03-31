package com.itti.leadcapturing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.allowed-extensions:jpg,jpeg,png,gif,svg}")
    private String allowedExtensions;

    @Value("${app.upload.max-logo-size:5242880}")
    private long maxLogoSize;

    private static final String UPLOAD_DIR = "uploads/logos/";

    public String storeLogo(MultipartFile file, Long entityId) throws IOException {
        validateLogoFile(file);

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String newFilename = "logo_" + entityId + "_" + UUID.randomUUID() + "." + extension;

        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("✅ Logo stored: {}", filePath.toString());
        return UPLOAD_DIR + newFilename;
    }

    public void deleteLogo(String logoUrl) {
        if (logoUrl == null || logoUrl.isBlank()) return;
        try {
            Path filePath = Paths.get(logoUrl);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("🗑️ Deleted logo: {}", logoUrl);
            }
        } catch (IOException e) {
            log.warn("⚠️ Could not delete logo file: {}", logoUrl, e);
        }
    }

    public byte[] readLogoAsBytes(String logoUrl) throws IOException {
        if (logoUrl == null || logoUrl.isBlank()) return null;
        Path filePath = Paths.get(logoUrl);
        if (!Files.exists(filePath)) return null;
        return Files.readAllBytes(filePath);
    }

    private void validateLogoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Logo file is empty");
        }
        if (file.getSize() > maxLogoSize) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
        String ext = getExtension(file.getOriginalFilename()).toLowerCase();
        List<String> allowed = Arrays.asList(allowedExtensions.split(","));
        if (!allowed.contains(ext)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: " + allowedExtensions);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}