package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private MessageRepository repository;

    @Autowired
    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public Optional<Message> getMessage(long id) {
        return repository.findById(id);
    }

    public List<Message> getAllMessagesBySender(User sender) {
        return repository.getAllBySender(sender);
    }

    public List<Message> getAllMessagesByReceiver(User receiver) {
        return repository.getAllByReceiver(receiver);
    }
}
