

package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQAttachment;
import com.itti.leadcapturing.model.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RFQAttachmentRepository extends JpaRepository<RFQAttachment, Long> {

    @Query("SELECT a FROM RFQAttachment a WHERE a.rfq.id = :rfqId ORDER BY a.createdAt DESC")
    List<RFQAttachment> findByRfqId(@Param("rfqId") Long rfqId);

    @Query("SELECT a FROM RFQAttachment a WHERE a.rfq.id = :rfqId AND a.attachmentType = :type")
    List<RFQAttachment> findByRfqIdAndType(@Param("rfqId") Long rfqId, @Param("type") AttachmentType type);
}