package com.itti.leadcapturing.dto;

import java.time.LocalDateTime;
import com.itti.leadcapturing.model.AttachmentType;

public class RFQAttachmentDTO {
    
    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private AttachmentType attachmentType;
    private String description;
    private Long uploadedBy;
    private LocalDateTime createdAt;

    // Constructors
    public RFQAttachmentDTO() {}

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}