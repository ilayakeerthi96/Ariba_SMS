package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores documents uploaded by suppliers during or after registration.
 * Supported types: gst, pan, tan (can be extended).
 * Binary content is stored as LONGBLOB.
 */
@Entity
@Table(name = "supplier_documents")
public class SupplierDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK to the Supplier entity.
     * Fetched lazily — we rarely need full supplier when fetching docs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /**
     * Type of document: "gst", "pan", "tan"
     * Lower-cased and trimmed on save.
     */
    @Column(name = "document_type", nullable = false, length = 20)
    private String documentType;

    /** Original filename as uploaded by the user. */
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    /** MIME type of the file, e.g. "application/pdf", "image/jpeg". */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /** Binary file content stored directly in the DB. */
    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    /** File size in bytes — stored for quick inspection. */
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    // ── Constructors ────────────────────────────────────────────

    public SupplierDocument() {}

    public SupplierDocument(Supplier supplier, String documentType,
                            String filename, String contentType,
                            byte[] fileData) {
        this.supplier     = supplier;
        this.documentType = documentType.trim().toLowerCase();
        this.filename     = filename;
        this.contentType  = contentType;
        this.fileData     = fileData;
        this.fileSize     = (long) fileData.length;
        this.uploadedAt   = LocalDateTime.now();
    }

    // ── JPA callbacks ───────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) this.uploadedAt = LocalDateTime.now();
        if (this.documentType != null) this.documentType = this.documentType.trim().toLowerCase();
    }

    // ── Getters & Setters ───────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) {
        this.documentType = documentType != null ? documentType.trim().toLowerCase() : null;
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
        this.fileSize = fileData != null ? (long) fileData.length : 0L;
    }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}