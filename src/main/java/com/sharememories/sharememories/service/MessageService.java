package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MessageService {

    private final MessageRepository repository;


    public Optional<Message> getMessage(long id) {
        return repository.findById(id);
    }

    public List<Message> getAllMessagesBySenderAndReceiver(User sender, User receiver) {
        return repository.findAllBySenderAndReceiver(sender, receiver);
    }

    public Map<Long, Long> getAllNotificationsCount(User receiver) {
        List<Message> messages = repository.getAllByReceiverAndMessageSeen(receiver, false);

        if (!messages.isEmpty()) {
            Map<Long, Long> output = messages.stream()
                    .filter(m -> receiver.getContacts().contains(m.getSender()))
                    .collect(Collectors.groupingBy(m -> m.getSender().getId(), Collectors.counting()));

            long count = messages.stream()
                    .filter(m -> !receiver.getContacts().contains(m.getSender()))
                    .count();

            output.put((long) -1, count);
            return output;
        }
        return Map.of();
    }

    public Map<Long, Long> getUnknownSenderNotificationsCount(User receiver) {
        List<Message> messages = repository.findAllByReceiverAndSenderNotInContactsAndMessageSeen(receiver, receiver.getContacts(), false);

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
