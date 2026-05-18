package com.itti.leadcapturing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private Long roomId;
    private Long senderId;
    private String senderType;
    private String senderName;
    private String message;
}
