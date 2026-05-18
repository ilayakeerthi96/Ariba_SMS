package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderType;
    private String senderName;
    private String message;
    private String sentAt;
    private Boolean isRead;
}
