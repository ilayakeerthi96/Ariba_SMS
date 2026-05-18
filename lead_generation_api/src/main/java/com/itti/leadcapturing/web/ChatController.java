package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.ChatMessageDTO;
import com.itti.leadcapturing.dto.SendMessageRequest;
import com.itti.leadcapturing.model.ChatMessage;
import com.itti.leadcapturing.model.ChatRoom;
import com.itti.leadcapturing.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    // ─── GET CHAT ROOM BY RFQ ────────────────────────────────────────────────

    @GetMapping("/room/rfq/{rfqId}")
    public ResponseEntity<?> getChatRoomByRfq(@PathVariable Long rfqId) {
        Optional<ChatRoom> room = chatService.getChatRoomByRfqId(rfqId);
        if (room.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "No chat room available. Supplier has not been selected for this RFQ yet."
            ));
        }
        return ResponseEntity.ok(Map.of("success", true, "data", toChatRoomMap(room.get())));
    }

    // ─── GET MESSAGES ────────────────────────────────────────────────────────

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long roomId,
                                         @RequestParam(required = false) String readerType) {
        List<ChatMessage> messages = chatService.getMessages(roomId);

        if (readerType != null && !readerType.isEmpty()) {
            try {
                chatService.markAsRead(roomId, ChatMessage.SenderType.valueOf(readerType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid readerType '{}' for room {}", readerType, roomId);
            }
        }

        List<ChatMessageDTO> dtos = messages.stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "data", dtos));
    }

    // ─── SEND MESSAGE (REST fallback when WebSocket is unavailable) ──────────

    @PostMapping("/room/{roomId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Long roomId,
                                         @RequestBody SendMessageRequest request) {
        try {
            ChatMessage.SenderType senderType =
                    ChatMessage.SenderType.valueOf(request.getSenderType().toUpperCase());
            ChatMessage saved = chatService.saveMessage(
                    roomId, request.getSenderId(), senderType, request.getSenderName(), request.getMessage());
            return ResponseEntity.ok(Map.of("success", true, "data", toMessageDTO(saved)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ─── UNREAD COUNT ────────────────────────────────────────────────────────

    @GetMapping("/room/{roomId}/unread")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long roomId,
                                            @RequestParam String readerType) {
        try {
            long count = chatService.getUnreadCount(
                    roomId, ChatMessage.SenderType.valueOf(readerType.toUpperCase()));
            return ResponseEntity.ok(Map.of("success", true, "count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ─── MARK AS READ ────────────────────────────────────────────────────────

    @PutMapping("/room/{roomId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long roomId,
                                        @RequestParam String readerType) {
        try {
            chatService.markAsRead(roomId, ChatMessage.SenderType.valueOf(readerType.toUpperCase()));
            return ResponseEntity.ok(Map.of("success", true, "message", "Messages marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ─── BUYER'S CHAT ROOMS ──────────────────────────────────────────────────

    @GetMapping("/rooms/buyer/{buyerId}")
    public ResponseEntity<?> getBuyerChatRooms(@PathVariable Long buyerId) {
        List<ChatRoom> rooms = chatService.getChatRoomsForBuyer(buyerId);
        List<Map<String, Object>> result = rooms.stream().map(r -> {
            Map<String, Object> m = new HashMap<>(toChatRoomMap(r));
            m.put("unreadCount", chatService.getUnreadCount(r.getId(), ChatMessage.SenderType.BUYER));
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    // ─── SUPPLIER'S CHAT ROOMS ───────────────────────────────────────────────

    @GetMapping("/rooms/supplier/{supplierId}")
    public ResponseEntity<?> getSupplierChatRooms(@PathVariable Long supplierId) {
        List<ChatRoom> rooms = chatService.getChatRoomsForSupplier(supplierId);
        List<Map<String, Object>> result = rooms.stream().map(r -> {
            Map<String, Object> m = new HashMap<>(toChatRoomMap(r));
            m.put("unreadCount", chatService.getUnreadCount(r.getId(), ChatMessage.SenderType.SUPPLIER));
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "data", result));
    }

    // ─── MAPPERS ─────────────────────────────────────────────────────────────

    private ChatMessageDTO toMessageDTO(ChatMessage msg) {
        return ChatMessageDTO.builder()
                .id(msg.getId())
                .roomId(msg.getChatRoom() != null ? msg.getChatRoom().getId() : null)
                .senderId(msg.getSenderId())
                .senderType(msg.getSenderType().name())
                .senderName(msg.getSenderName())
                .message(msg.getMessage())
                .sentAt(msg.getSentAt() != null ? msg.getSentAt().toString() : null)
                .isRead(msg.getIsRead())
                .build();
    }

    private Map<String, Object> toChatRoomMap(ChatRoom room) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("roomId", room.getId());
        dto.put("rfqId", room.getRfqId());
        dto.put("rfqNumber", room.getRfqNumber());
        dto.put("rfqTitle", room.getRfqTitle());
        dto.put("buyerId", room.getBuyerId());
        dto.put("supplierId", room.getSupplierId());
        dto.put("buyerName", room.getBuyerName());
        dto.put("supplierName", room.getSupplierName());
        dto.put("createdAt", room.getCreatedAt() != null ? room.getCreatedAt().toString() : null);
        dto.put("isActive", room.getIsActive());
        return dto;
    }
}
