package com.itti.leadcapturing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rfq_id", nullable = false, unique = true)
    private Long rfqId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "rfq_number", length = 100)
    private String rfqNumber;

    @Column(name = "rfq_title", length = 500)
    private String rfqTitle;

    @Column(name = "buyer_name", length = 300)
    private String buyerName;

    @Column(name = "supplier_name", length = 300)
    private String supplierName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }
}
