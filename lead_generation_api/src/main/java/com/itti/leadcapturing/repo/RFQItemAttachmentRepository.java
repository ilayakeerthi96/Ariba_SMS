package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.RFQItemAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ✅ Repository for RFQ Item Attachments
 */ 
@Repository
public interface RFQItemAttachmentRepository extends JpaRepository<RFQItemAttachment, Long> {

    /**
     * Find all attachments for a specific item
     */
    @Query("SELECT a FROM RFQItemAttachment a WHERE a.item.id = :itemId ORDER BY a.createdAt DESC")
    List<RFQItemAttachment> findByItemId(@Param("itemId") Long itemId);
}