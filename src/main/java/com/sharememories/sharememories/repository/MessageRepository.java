package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> getAllBySender(User sender);

    List<Message> getAllByReceiver(User receiver);
}
