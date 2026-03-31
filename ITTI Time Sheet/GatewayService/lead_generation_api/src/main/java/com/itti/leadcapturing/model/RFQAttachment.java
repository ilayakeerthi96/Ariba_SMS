

package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "rfq_attachments")
public class RFQAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "attachment_type")
    @Enumerated(EnumType.STRING)
    private AttachmentType attachmentType;

    @Column(name = "description")
    private String description;

    @Column(name = "uploaded_by")
    private Long uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    @JsonBackReference("rfq-attachments")
    private RFQ rfq;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public RFQAttachment() {
        this.createdAt = LocalDateTime.now();
    }

    public RFQAttachment(String fileName, String filePath, Long fileSize, String fileType, AttachmentType attachmentType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.attachmentType = attachmentType;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public AttachmentType getAttachmentType() { return attachmentType; }
    public void setAttachmentType(AttachmentType attachmentType) { this.attachmentType = attachmentType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}