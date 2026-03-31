package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.RFQItem;
import com.itti.leadcapturing.model.RFQItemAttachment;
import com.itti.leadcapturing.repo.RFQItemRepository;
import com.itti.leadcapturing.repo.RFQItemAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RFQItemAttachmentService {

    // ⚠️ CONFIGURE THIS PATH FOR YOUR SERVER
    private static final String UPLOAD_DIR = "uploads/rfq-items/";

    @Autowired
    private RFQItemAttachmentRepository attachmentRepository;

    @Autowired
    private RFQItemRepository itemRepository;

    /**
     * Upload attachment for an RFQ item
     */
    @Transactional
    public RFQItemAttachment uploadAttachment(
            Long itemId,
            MultipartFile file,
            String description,
            Long uploadedBy
    ) throws IOException {
        System.out.println("[UPLOAD ATTACHMENT] Item ID: " + itemId);
        System.out.println("  File: " + file.getOriginalFilename());
        System.out.println("  Size: " + file.getSize() + " bytes");

        // Validate item exists
        RFQItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("RFQ Item not found: " + itemId));

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Create upload directory if it doesn't exist
        File uploadDirFile = new File(UPLOAD_DIR);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file to disk
        Path filePath = Paths.get(UPLOAD_DIR + uniqueFilename);
        Files.write(filePath, file.getBytes());

        System.out.println("  [✓] File saved: " + filePath.toString());

        // Create attachment record
        RFQItemAttachment attachment = new RFQItemAttachment();
        attachment.setItem(item);
        attachment.setFileName(originalFilename);
        attachment.setFilePath(filePath.toString());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setDescription(description);
        attachment.setUploadedBy(uploadedBy);
        attachment.setCreatedAt(LocalDateTime.now());

        RFQItemAttachment saved = attachmentRepository.save(attachment);

        System.out.println("  [✓] Attachment saved with ID: " + saved.getId());
        return saved;
    }

    /**
     * Get all attachments for an item
     */
    @Transactional(readOnly = true)
    public List<RFQItemAttachment> getAttachmentsByItem(Long itemId) {
        System.out.println("[GET ATTACHMENTS] Item ID: " + itemId);
        return attachmentRepository.findByItemId(itemId);
    }

    /**
     * Delete attachment
     */
    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        System.out.println("[DELETE ATTACHMENT] ID: " + attachmentId);

        RFQItemAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));

        // Delete file from disk
        Path filePath = Paths.get(attachment.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("  [✓] File deleted from disk");
        }

        // Delete database record
        attachmentRepository.delete(attachment);
        System.out.println("  [✓] Attachment deleted from database");
    }

    /**
     * Download attachment (returns file bytes)
     */
    public byte[] downloadAttachment(Long attachmentId) throws IOException {
        System.out.println("[DOWNLOAD ATTACHMENT] ID: " + attachmentId);

        RFQItemAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));

        Path filePath = Paths.get(attachment.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found on disk: " + filePath);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Get attachment by ID
     */
    public RFQItemAttachment getAttachmentById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found: " + attachmentId));
    }
}