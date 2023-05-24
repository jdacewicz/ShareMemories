package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MessageServiceTest {

    @InjectMocks
    private MessageService service;

    @Mock
    private MessageRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given user " +
            "When getting all counts of unread messages sent to existing user " +
            "Then should return proper map containing contacts ids with counts")
    void getAllCountsOfMessagesWithProperContactsCountsByExistingUser() {
        User receiver = new User();
        receiver.setId((long) 1);

        User sender = new User();
        receiver.getContacts().add(sender);
        sender.setId((long) 2);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        when(repository.getAllByReceiverAndMessageSeen(receiver, false)).thenReturn(messageList);

        Map<Long, Long> output = service.getAllNotificationsCount(receiver);
        assertEquals(2, output.get((long) 2));
    }

    @Test
    @DisplayName("Given user " +
            "When getting all counts of unread messages sent to existing user " +
            "Then should return proper map containing -1 id with unknown senders messages count")
    void getAllCountsOfMessagesWithProperUnknownSendersMessagesCountByNonExistingUser() {
        User receiver = new User();
        receiver.setId((long) 1);

        User sender = new User();
        sender.setId((long) 2);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        when(repository.getAllByReceiverAndMessageSeen(receiver, false)).thenReturn(messageList);

        Map<Long, Long> output = service.getAllNotificationsCount(receiver);
        assertEquals(2, output.get((long) -1));
    }

    @Test
    @DisplayName("Given user " +
            "When getting all counts of unread messages sent to non existing user " +
            "Then should return empty map")
    void getAllCountsOfUnreadMessagesByNonExistingUser() {
        User receiver = new User();

        Map<Long, Long> output = service.getAllNotificationsCount(receiver);

        assert(output.isEmpty());
    }

    @Test
    @DisplayName("Given user " +
            "When getting unknown senders counts of unread messages sent to existing user" +
            "Then should return map containing senders ids with counts ")
    void getUnknownSendersCountsOfMessagesWithProperCountsByExistingUser() {
        User receiver = new User();

        User sender = new User();
        sender.setId((long) 1);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        when(repository.findAllByReceiverAndSenderNotInContactsAndMessageSeen(receiver, receiver.getContacts(), false))
                .thenReturn(messageList);

        Map<Long, Long> output = service.getUnknownSenderNotificationsCount(receiver);
        assertFalse(output.isEmpty());
    }

    @Test
    @DisplayName("Given user " +
            "When getting unknown senders counts of unread messages sent to non existing user" +
            "Then should return empty map ")
    void getUnknownSendersCountsOfUnreadMessagesByNonExistingUser() {
        User receiver = new User();

        Map<Long, Long> output = service.getUnknownSenderNotificationsCount(receiver);

        assert(output.isEmpty());
    }
}