package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Message> getAllMessagesBySenderAndReceiver(User sender, User receiver) {
        return repository.findAllBySenderAndReceiver(sender, receiver);
    }

    public Map<Long, Long> getNotificationsCount(User receiver) {
        List<Message> messages = repository.getAllByReceiverAndMessageSeen(receiver, false);

        return messages.stream()
                .collect(Collectors.groupingBy(m -> m.getSender().getId(), Collectors.counting()));
    }

    public Message createMessage(Message message) {
        return repository.save(message);
    }

    @Transactional
    public void setMessagesSeen(User sender, User receiver) {
        repository.updateBySenderAndReceiverSetMessageSeen(sender, receiver, true);
    }
}
