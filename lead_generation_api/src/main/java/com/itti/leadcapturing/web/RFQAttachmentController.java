package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.RFQAttachment;
import com.itti.leadcapturing.model.AttachmentType;
import com.itti.leadcapturing.service.RFQAttachmentService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rfq-attachment")
@CrossOrigin(origins = "*")
public class RFQAttachmentController {

    @Autowired
    private RFQAttachmentService rfqAttachmentService;

    @PostMapping("/{rfqId}")
    public ResponseEntity<?> addAttachmentToRFQ(
            @PathVariable Long rfqId,
            @RequestBody RFQAttachment attachment) {
        try {
            RFQAttachment created = rfqAttachmentService.addAttachmentToRFQ(rfqId, attachment);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/rfq/{rfqId}")
    public ResponseEntity<?> getAttachmentsByRFQ(@PathVariable Long rfqId) {
        try {
            List<RFQAttachment> attachments = rfqAttachmentService.getAttachmentsByRFQ(rfqId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", attachments.size());
            response.put("data", attachments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch attachments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/rfq/{rfqId}/type/{type}")
    public ResponseEntity<?> getAttachmentsByType(
            @PathVariable Long rfqId,
            @PathVariable AttachmentType type) {
        try {
            List<RFQAttachment> attachments = rfqAttachmentService.getAttachmentsByType(rfqId, type);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", attachments.size());
            response.put("data", attachments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch attachments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAttachmentById(@PathVariable Long id) {
        try {
            RFQAttachment attachment = rfqAttachmentService.getAttachmentById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", attachment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Attachment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        try {
            rfqAttachmentService.deleteAttachment(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Attachment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== ✅ NEW: DOWNLOAD FUNCTIONALITY ====================
    
    /**
     * Download attachment file
     * GET /api/rfq-attachment/{id}/download
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        System.out.println("📥 [DOWNLOAD ATTACHMENT] ID: " + id);

        try {
            // Get attachment record
            RFQAttachment attachment = rfqAttachmentService.getAttachmentById(id);
            
            System.out.println("  File: " + attachment.getFileName());
            System.out.println("  Path: " + attachment.getFilePath());

            // Load file as resource
            Path filePath = Paths.get(attachment.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("  ❌ File not found or not readable: " + filePath);
                throw new RuntimeException("File not found or not readable: " + attachment.getFileName());
            }

            // Determine content type
            String contentType = attachment.getFileType();
            if (contentType == null) {
                contentType = Files.probeContentType(filePath);
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            System.out.println("  ✅ File found, sending download");
            System.out.println("  Content-Type: " + contentType);

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
     * Get attachment info without downloading
     * GET /api/rfq-attachment/{id}/info
     */
    @GetMapping("/{id}/info")
    public ResponseEntity<?> getAttachmentInfo(@PathVariable Long id) {
        System.out.println("ℹ️ [GET ATTACHMENT INFO] ID: " + id);

        try {
            RFQAttachment attachment = rfqAttachmentService.getAttachmentById(id);

            Map<String, Object> info = new HashMap<>();
            info.put("id", attachment.getId());
            info.put("fileName", attachment.getFileName());
            info.put("fileType", attachment.getFileType());
            info.put("fileSize", attachment.getFileSize());
            info.put("description", attachment.getDescription());
            info.put("attachmentType", attachment.getAttachmentType());
            info.put("createdAt", attachment.getCreatedAt());
            info.put("downloadUrl", "/api/rfq-attachment/" + id + "/download");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", info);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("  ❌ Error: " + e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}