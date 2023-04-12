package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.MessageService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class MessageControllerTest {

    @InjectMocks
    private MessageController controller;
    @Mock
    private MessageService messageService;
    @Mock
    private SecurityUserDetailsService detailsService;
    private static MockedStatic<FileUtils> fileUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    static void init() {
        fileUtils = Mockito.mockStatic(FileUtils.class);
    }

    @AfterAll
    static void close() {
        fileUtils.close();
    }

    @Test
    void Given_Id_When_GettingMessageByProperIdByAPI_Then_ReturnedResponseOkWithMessage() {
        //Given
        long id = 1;
        //When
        Message message = new Message();
        Mockito.when(messageService.getMessage(id)).thenReturn(Optional.of(message));

        ResponseEntity response = controller.getMessage(id);
        //Then
        assertEquals(ResponseEntity.ok(message), response);
    }

    @Test
    void Given_Id_When_GettingMessageByWrongIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        long id = 1;
        //When
        ResponseEntity response = controller.getMessage(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_UserId_When_GettingAllMessagesWithUserByProperUserIdByAPI_Then_ReturnedResponseOkWithListOfMessages() {
        //Given
        long contactId = 1;
        //When
        User loggedUser = new User("user");
        User contact = new User("contact");
        Message message = new Message();
        List<Message> messageList = List.of(message);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(loggedUser.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(loggedUser));
        Mockito.when(detailsService.getUserById(contactId)).thenReturn(Optional.of(contact));

        Mockito.when(messageService.getAllMessagesBySenderAndReceiver(loggedUser, contact)).thenReturn(messageList);

        ResponseEntity response = controller.getAllMessagesWithUser(contactId);
        //Then
        assertEquals(ResponseEntity.ok(messageList), response);
    }

    @Test
    void Given_UserId_When_GettingAllMessagesWithUserByProperUserIdByAPI_Then_ReturnedResponseNoContentIfListIsEmpty() {
        //Given
        long contactId = 1;
        //When
        User loggedUser = new User("user");
        User contact = new User("contact");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(loggedUser.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(loggedUser));
        Mockito.when(detailsService.getUserById(contactId)).thenReturn(Optional.of(contact));

        ResponseEntity response = controller.getAllMessagesWithUser(contactId);
        //Then
        assertEquals(ResponseEntity.noContent().build(), response);
    }

    @Test
    void Given_UserId_When_GettingAllMessagesWithUserByWrongUserIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        long contactId = 1;
        //When
        User loggedUser = new User("user");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(loggedUser.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(loggedUser));

        ResponseEntity response = controller.getAllMessagesWithUser(contactId);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given__When_GettingMessagesNotificationsCounts_Then_ReturnedResponseOkWithMapOfCounts() {
        //Given
        //When
        User loggedUser = new User("user");
        long keyAndValue = 1;
        Map<Long, Long> notifyCounts = Map.of(keyAndValue, keyAndValue);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(loggedUser.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(loggedUser));
        Mockito.when(messageService.getNotificationsCount(loggedUser)).thenReturn(notifyCounts);

        ResponseEntity response = controller.getNotificationsCount();
        //Then
        assertEquals(ResponseEntity.ok(notifyCounts), response);
    }

    @Test
    void Given__When_GettingMessagesNotificationsCounts_Then_ReturnedResponseNoContentIfMapIsEmpty() {
        //Given
        //When
        User loggedUser = new User("user");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(loggedUser.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(loggedUser));

        ResponseEntity response = controller.getNotificationsCount();
        //Then
        assertEquals(ResponseEntity.noContent().build(), response);
    }

    void Test1() {
        //Given
        //When
        //Then
    }

    void Test2() {
        //Given
        //When
        //Then
    }
}