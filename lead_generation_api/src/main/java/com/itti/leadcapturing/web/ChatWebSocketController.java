package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.ChatMessageDTO;
import com.itti.leadcapturing.dto.SendMessageRequest;
import com.itti.leadcapturing.model.ChatMessage;
import com.itti.leadcapturing.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Clients publish to: /app/chat.send/{roomId}
     * Subscribers receive on: /topic/chat/{roomId}
     */
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload SendMessageRequest request) {
        try {
            ChatMessage.SenderType senderType =
                    ChatMessage.SenderType.valueOf(request.getSenderType().toUpperCase());

            ChatMessage saved = chatService.saveMessage(
                    roomId,
                    request.getSenderId(),
                    senderType,
                    request.getSenderName(),
                    request.getMessage()
            );

            ChatMessageDTO dto = ChatMessageDTO.builder()
                    .id(saved.getId())
                    .roomId(roomId)
                    .senderId(saved.getSenderId())
                    .senderType(saved.getSenderType().name())
                    .senderName(saved.getSenderName())
                    .message(saved.getMessage())
                    .sentAt(saved.getSentAt().toString())
                    .isRead(saved.getIsRead())
                    .build();

            messagingTemplate.convertAndSend("/topic/chat/" + roomId, dto);
            logger.debug("Message broadcast to /topic/chat/{}", roomId);

        } catch (Exception e) {
            logger.error("Error handling WebSocket message for room {}: {}", roomId, e.getMessage());
        }
    }
}
