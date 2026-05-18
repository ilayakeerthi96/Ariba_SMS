package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :roomId ORDER BY m.sentAt ASC")
    List<ChatMessage> findMessagesByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :roomId AND m.senderType != :readerType AND m.isRead = false")
    long countUnreadMessages(@Param("roomId") Long roomId, @Param("readerType") ChatMessage.SenderType readerType);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.chatRoom.id = :roomId AND m.senderType != :readerType AND m.isRead = false")
    void markMessagesAsRead(@Param("roomId") Long roomId, @Param("readerType") ChatMessage.SenderType readerType);
}
