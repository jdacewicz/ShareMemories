package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> getAllBySender(User sender);
    List<Message> getAllByReceiver(User receiver);
    @Query("SELECT m FROM Message m WHERE (m.sender = ?1 OR m.sender = ?2) AND (m.receiver = ?1 OR m.receiver = ?2)")
    List<Message> findAllBySenderAndReceiver(User sender, User receiver);
    List<Message> getAllByReceiverAndMessageSeen(User receiver, boolean messageSeen);
    @Modifying
    @Query("UPDATE Message m SET m.messageSeen = true WHERE m.sender = ?1 AND m.receiver = ?2")
    void updateBySenderAndReceiverSetMessageSeen(User sender, User receiver, boolean messageSeen);
}
