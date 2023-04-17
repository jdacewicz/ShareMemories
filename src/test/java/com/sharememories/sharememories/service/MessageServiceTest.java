package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    void Given_User_When_GettingNotificationsCountByMessageReceiver_Then_ReturnedProperMapOfCountsOfMessagesFromContacts() {
        //Given
        User receiver = new User();
        receiver.setId((long) 1);
        //When
        User sender = new User();
        receiver.getContacts().add(sender);
        sender.setId((long) 2);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        Mockito.when(repository.getAllByReceiverAndMessageSeen(receiver, false)).thenReturn(messageList);

        Map<Long, Long> output = service.getNotificationsCount(receiver);
        //Then
        assertEquals(2, output.get((long) 2));
    }

    @Test
    void Given_User_When_GettingNotificationsCountByMessageReceiver_Then_ReturnedProperMapOfCountsOfMessagesNotFromContacts() {
        //Given
        User receiver = new User();
        receiver.setId((long) 1);
        //When
        User sender = new User();
        sender.setId((long) 2);

        Message message = new Message(sender, receiver, "test");
        List<Message> messageList = List.of(message, message);

        Mockito.when(repository.getAllByReceiverAndMessageSeen(receiver, false)).thenReturn(messageList);

        Map<Long, Long> output = service.getNotificationsCount(receiver);
        //Then
        assertEquals(2, output.get((long) -1));
    }
}