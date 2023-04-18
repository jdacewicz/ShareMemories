package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
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
    void Given_User_When_GettingNotificationsCountByProperMessageReceiver_Then_ReturnedProperMapOfCountsOfMessagesFromContacts() {
        //Given
        User receiver = new User();
        receiver.setId((long) 1);
        //When
        User sender = new User();
        receiver.getContacts().add(sender);
        sender.setId((long) 2);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        when(repository.getAllByReceiverAndMessageSeen(receiver, false)).thenReturn(messageList);

        Map<Long, Long> output = service.getAllNotificationsCount(receiver);
        //Then
        assertEquals(2, output.get((long) 2));
    }

    @Test
    void Given_User_When_GettingNotificationsCountByProperMessageReceiver_Then_ReturnedProperMapOfCountsOfMessagesNotFromContacts() {
        //Given
        User receiver = new User();
        receiver.setId((long) 1);
        //When
        User sender = new User();
        sender.setId((long) 2);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        when(repository.getAllByReceiverAndMessageSeen(receiver, false)).thenReturn(messageList);

        Map<Long, Long> output = service.getAllNotificationsCount(receiver);
        //Then
        assertEquals(2, output.get((long) -1));
    }

    @Test
    void Given_User_When_GettingNotificationsCountByWrongMessageReceiver_Then_ReturnedEmptyMap() {
        //Given
        User receiver = new User();
        //When
        Map<Long, Long> output = service.getAllNotificationsCount(receiver);
        //Then
        assert(output.isEmpty());
    }

    @Test
    void Given_User_When_GettingUnknownSenderNotificationsCountByProperMessageReceiver_Then_ReturnedProperMapOfCountsOfMessages() {
        //Given
        User receiver = new User();
        //When
        User sender = new User();
        sender.setId((long) 1);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        when(repository.findAllByReceiverAndSenderNotInContactsAndMessageSeen(receiver, receiver.getContacts(), false))
                .thenReturn(messageList);

        Map<Long, Long> output = service.getUnknownSenderNotificationsCount(receiver);
        //Then
        assertFalse(output.isEmpty());
    }

    @Test
    void Given_User_When_GettingUnknownSenderNotificationsCountByWrongMessageReceiver_Then_ReturnedEmptyMap() {
        //Given
        User receiver = new User();
        //When
        Map<Long, Long> output = service.getUnknownSenderNotificationsCount(receiver);
        //Then
        assert(output.isEmpty());
    }
}