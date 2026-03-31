package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.RFQItemAttachment;
import com.itti.leadcapturing.service.RFQItemAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ✅ Controller for Item Attachment Downloads
 */
@RestController
@RequestMapping("/api/rfq-item-attachment")
@CrossOrigin(origins = "*")
public class RFQItemAttachmentController {

    @Autowired
    private RFQItemAttachmentService service;

    /**
     * Download item attachment file
     * GET /api/rfq-item-attachment/{id}/download
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        System.out.println("📥 [DOWNLOAD ITEM ATTACHMENT] ID: " + id);

        try {
            // Get attachment record
            RFQItemAttachment attachment = service.getAttachmentById(id);
            
            System.out.println("  File: " + attachment.getFileName());
            System.out.println("  Path: " + attachment.getFilePath());

            // Load file as resource
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("  ❌ File not found or not readable: " + filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Determine content type
            String contentType = attachment.getFileType();
            if (contentType == null) {
                contentType = Files.probeContentType(filePath);
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            System.out.println("  ✅ Sending file: " + attachment.getFileName());

            // Return file as downloadable resource
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("  ❌ Error downloading attachment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get attachment info (optional - for debugging)
     * GET /api/rfq-item-attachment/{id}/info
     */
    @GetMapping("/{id}/info")
    public ResponseEntity<?> getAttachmentInfo(@PathVariable Long id) {
        System.out.println("ℹ️ [GET ATTACHMENT INFO] ID: " + id);
        
        try {
            RFQItemAttachment attachment = service.getAttachmentById(id);
            
            java.util.Map<String, Object> info = new java.util.HashMap<>();
            info.put("id", attachment.getId());
            info.put("fileName", attachment.getFileName());
            info.put("fileSize", attachment.getFileSize());
            info.put("fileType", attachment.getFileType());
            info.put("filePath", attachment.getFilePath());
            info.put("description", attachment.getDescription());
            info.put("itemId", attachment.getItem() != null ? attachment.getItem().getId() : null);
            info.put("createdAt", attachment.getCreatedAt());
            
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            System.err.println("  ❌ Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attachment not found");
        }
    }
}