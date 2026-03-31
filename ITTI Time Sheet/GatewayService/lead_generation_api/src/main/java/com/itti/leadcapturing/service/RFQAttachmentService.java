package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RFQAttachmentService {

    @Autowired
    private RFQAttachmentRepository rfqAttachmentRepository;

    @Autowired
    private RFQRepository rfqRepository;

    @Transactional
    public RFQAttachment addAttachmentToRFQ(Long rfqId, RFQAttachment attachment) {
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found"));

        attachment.setRfq(rfq);
        attachment.setCreatedAt(LocalDateTime.now());

        return rfqAttachmentRepository.save(attachment);
    }

    public List<RFQAttachment> getAttachmentsByRFQ(Long rfqId) {
        return rfqAttachmentRepository.findByRfqId(rfqId);
    }

    public List<RFQAttachment> getAttachmentsByType(Long rfqId, AttachmentType type) {
        return rfqAttachmentRepository.findByRfqIdAndType(rfqId, type);
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) {
        RFQAttachment attachment = rfqAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        rfqAttachmentRepository.delete(attachment);
    }

    public RFQAttachment getAttachmentById(Long attachmentId) {
        return rfqAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }
}