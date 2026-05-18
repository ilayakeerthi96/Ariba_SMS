package com.itti.leadcapturing.web;

import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.model.SupplierDocument;
import com.itti.leadcapturing.repo.SupplierDocumentRepository;
import com.itti.leadcapturing.repo.SupplierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for supplier document uploads (GST / PAN / TAN certificates).
 *
 * Endpoints:
 *   POST   /api/supplier/documents/upload          → upload a document
 *   GET    /api/supplier/documents/{supplierId}     → list all docs for a supplier
 *   GET    /api/supplier/documents/{supplierId}/{type}/download → download latest doc
 *   GET    /api/supplier/documents/{supplierId}/{type}/info     → metadata only
 *   DELETE /api/supplier/documents/{supplierId}/{type}          → delete a doc type
 */
@RestController
@RequestMapping("/api/supplier/documents")
@CrossOrigin(origins = "*")
@Slf4j
public class SupplierDocumentController {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L; // 5 MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "gst", "pan", "tan"
    );
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg", "image/jpg", "image/png"
    );

    @Autowired
    private SupplierDocumentRepository documentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ============================================================
    // POST /api/supplier/documents/upload
    // Multipart: file (required) + documentType (required) + supplierId (required)
    // ============================================================
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam("supplierId") Long supplierId) {

        Map<String, Object> response = new HashMap<>();

        try {
            log.info("[DOC UPLOAD] Supplier: {}, Type: {}, File: {}, Size: {} bytes",
                    supplierId, documentType, file.getOriginalFilename(), file.getSize());

            // ── Validate document type ──────────────────────────────────
            String docType = documentType.trim().toLowerCase();
            if (!ALLOWED_TYPES.contains(docType)) {
                response.put("success", false);
                response.put("message", "Invalid document type. Allowed: gst, pan, tan");
                return ResponseEntity.badRequest().body(response);
            }

            // ── Validate MIME type ──────────────────────────────────────
            String mimeType = file.getContentType();
            if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
                response.put("success", false);
                response.put("message", "Invalid file type. Please upload PDF, JPG, or PNG.");
                return ResponseEntity.badRequest().body(response);
            }

            // ── Validate file size ──────────────────────────────────────
            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("success", false);
                response.put("message", "File too large. Maximum allowed size is 5 MB.");
                return ResponseEntity.badRequest().body(response);
            }

            // ── Find supplier ───────────────────────────────────────────
            Supplier supplier = supplierRepository.findById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + supplierId));

            // ── Replace existing doc of same type (upsert) ──────────────
            if (documentRepository.existsBySupplierIdAndDocumentType(supplierId, docType)) {
                log.info("[DOC UPLOAD] Replacing existing {} document for supplier {}", docType, supplierId);
                documentRepository.deleteBySupplierIdAndDocumentType(supplierId, docType);
            }

            // ── Save document ───────────────────────────────────────────
            byte[] bytes = file.getBytes();
            SupplierDocument doc = new SupplierDocument(
                    supplier,
                    docType,
                    file.getOriginalFilename(),
                    mimeType,
                    bytes
            );
            SupplierDocument saved = documentRepository.save(doc);

            log.info("[DOC UPLOAD] Saved document ID: {} for supplier: {}", saved.getId(), supplierId);

            response.put("success", true);
            response.put("message", docType.toUpperCase() + " document uploaded successfully.");
            response.put("documentId", saved.getId());
            response.put("documentType", docType);
            response.put("filename", saved.getFilename());
            response.put("fileSize", saved.getFileSize());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("[DOC UPLOAD] RuntimeException: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("[DOC UPLOAD] Error: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Document upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================================
    // GET /api/supplier/documents/{supplierId}
    // Returns metadata list of all docs for a supplier (no binary)
    // ============================================================
    @GetMapping("/{supplierId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getDocumentsList(@PathVariable Long supplierId) {
        try {
            log.info("[DOC LIST] Supplier: {}", supplierId);

            List<SupplierDocument> docs = documentRepository
                    .findBySupplierIdOrderByUploadedAtDesc(supplierId);

            List<Map<String, Object>> metaList = docs.stream().map(d -> {
                Map<String, Object> meta = new HashMap<>();
                meta.put("id", d.getId());
                meta.put("documentType", d.getDocumentType());
                meta.put("filename", d.getFilename());
                meta.put("contentType", d.getContentType());
                meta.put("fileSize", d.getFileSize());
                meta.put("uploadedAt", d.getUploadedAt());
                return meta;
            }).toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("supplierId", supplierId);
            response.put("count", metaList.size());
            response.put("data", metaList);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[DOC LIST] Error: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================================
    // GET /api/supplier/documents/{supplierId}/{type}/info
    // Returns metadata for the latest document of a specific type
    // ============================================================
    @GetMapping("/{supplierId}/{type}/info")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getDocumentInfo(
            @PathVariable Long supplierId,
            @PathVariable String type) {
        try {
            String docType = type.trim().toLowerCase();
            Optional<SupplierDocument> docOpt = documentRepository
                    .findTopBySupplierIdAndDocumentTypeOrderByUploadedAtDesc(supplierId, docType);

            if (docOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No " + docType.toUpperCase() + " document found for this supplier");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            SupplierDocument doc = docOpt.get();
            Map<String, Object> meta = new HashMap<>();
            meta.put("id", doc.getId());
            meta.put("documentType", doc.getDocumentType());
            meta.put("filename", doc.getFilename());
            meta.put("contentType", doc.getContentType());
            meta.put("fileSize", doc.getFileSize());
            meta.put("uploadedAt", doc.getUploadedAt());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", meta);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[DOC INFO] Error: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================================
    // GET /api/supplier/documents/{supplierId}/{type}/download
    // Returns raw binary for inline view / download
    // ============================================================
    @GetMapping("/{supplierId}/{type}/download")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long supplierId,
            @PathVariable String type) {
        try {
            String docType = type.trim().toLowerCase();
            Optional<SupplierDocument> docOpt = documentRepository
                    .findTopBySupplierIdAndDocumentTypeOrderByUploadedAtDesc(supplierId, docType);

            if (docOpt.isEmpty() || docOpt.get().getFileData() == null) {
                return ResponseEntity.notFound().build();
            }

            SupplierDocument doc = docOpt.get();
            String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(doc.getFileData().length);
            // Use "inline" so PDF/image renders in browser; "attachment" forces download
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + doc.getFilename() + "\"");

            log.info("[DOC DOWNLOAD] Serving {} doc for supplier {} — {} bytes",
                    docType, supplierId, doc.getFileData().length);

            return new ResponseEntity<>(doc.getFileData(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("[DOC DOWNLOAD] Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============================================================
    // DELETE /api/supplier/documents/{supplierId}/{type}
    // Removes all documents of that type for the supplier
    // ============================================================
    @DeleteMapping("/{supplierId}/{type}")
    @Transactional
    public ResponseEntity<?> deleteDocument(
            @PathVariable Long supplierId,
            @PathVariable String type) {
        try {
            String docType = type.trim().toLowerCase();
            if (!documentRepository.existsBySupplierIdAndDocumentType(supplierId, docType)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No " + docType.toUpperCase() + " document found to delete");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            documentRepository.deleteBySupplierIdAndDocumentType(supplierId, docType);
            log.info("[DOC DELETE] Deleted {} document for supplier {}", docType, supplierId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", docType.toUpperCase() + " document deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[DOC DELETE] Error: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}