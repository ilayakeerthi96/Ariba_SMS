package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQItemAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RFQItemAttachmentRepository extends JpaRepository<RFQItemAttachment, Long> {
    List<RFQItemAttachment> findByItemId(Long itemId);
}