package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.RFQAttachment;
import com.itti.leadcapturing.model.AttachmentType;
import com.itti.leadcapturing.service.RFQAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}