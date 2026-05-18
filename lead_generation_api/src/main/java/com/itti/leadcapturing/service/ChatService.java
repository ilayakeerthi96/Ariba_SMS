package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.ChatMessage;
import com.itti.leadcapturing.model.ChatRoom;
import com.itti.leadcapturing.repo.ChatMessageRepository;
import com.itti.leadcapturing.repo.ChatRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // ─── ROOM MANAGEMENT ─────────────────────────────────────────────────────

    @Transactional
    public ChatRoom createChatRoom(Long rfqId, Long buyerId, Long supplierId,
                                   String rfqNumber, String rfqTitle,
                                   String buyerName, String supplierName) {
        Optional<ChatRoom> existing = chatRoomRepository.findByRfqId(rfqId);
        if (existing.isPresent()) {
            logger.info("Chat room already exists for RFQ {}, reusing room {}", rfqId, existing.get().getId());
            return existing.get();
        }
        ChatRoom room = ChatRoom.builder()
                .rfqId(rfqId)
                .buyerId(buyerId)
                .supplierId(supplierId)
                .rfqNumber(rfqNumber)
                .rfqTitle(rfqTitle)
                .buyerName(buyerName)
                .supplierName(supplierName)
                .isActive(true)
                .build();
        ChatRoom saved = chatRoomRepository.save(room);
        logger.info("Created chat room {} for RFQ {} (buyer={}, supplier={})", saved.getId(), rfqId, buyerId, supplierId);
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> getChatRoomByRfqId(Long rfqId) {
        return chatRoomRepository.findByRfqIdAndIsActiveTrue(rfqId);
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> getChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRoomsForBuyer(Long buyerId) {
        return chatRoomRepository.findByBuyerIdAndIsActiveTrue(buyerId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRoomsForSupplier(Long supplierId) {
        return chatRoomRepository.findBySupplierIdAndIsActiveTrue(supplierId);
    }

    // ─── MESSAGE MANAGEMENT ───────────────────────────────────────────────────

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId,
                                   ChatMessage.SenderType senderType,
                                   String senderName, String message) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + roomId));

        ChatMessage msg = ChatMessage.builder()
                .chatRoom(room)
                .senderId(senderId)
                .senderType(senderType)
                .senderName(senderName)
                .message(message)
                .isRead(false)
                .build();

        return chatMessageRepository.save(msg);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(Long roomId) {
        return chatMessageRepository.findMessagesByRoomId(roomId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long roomId, ChatMessage.SenderType readerType) {
        return chatMessageRepository.countUnreadMessages(roomId, readerType);
    }

    @Transactional
    public void markAsRead(Long roomId, ChatMessage.SenderType readerType) {
        chatMessageRepository.markMessagesAsRead(roomId, readerType);
    }
}
