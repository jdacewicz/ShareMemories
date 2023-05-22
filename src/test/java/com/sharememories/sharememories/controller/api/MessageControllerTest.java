package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.MessageService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import com.sharememories.sharememories.util.UserUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private SecurityUserDetailsService detailsService;

    private static MockedStatic<FileUtils> fileUtils;
    private static MockedStatic<UserUtils> userUtils;

    @BeforeAll
    static void init() {
        fileUtils = Mockito.mockStatic(FileUtils.class);
        userUtils = Mockito.mockStatic(UserUtils.class);
    }

    @AfterAll
    static void close() {
        fileUtils.close();
        userUtils.close();
    }

    @Test
    @DisplayName("Given valid message id " +
            "When getting not existing message by api " +
            "Then should return not found")
    void getExistingMessageByValidId() throws Exception {
        long id = 1;
        Message message = new Message();

        when(messageService.getMessage(id)).thenReturn(Optional.of(message));

        this.mvc.perform( get("/api/messages/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid message id " +
            "When getting not existing message by api " +
            "Then should return response not found")
    void getNotExistingMessageByValidId() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/messages/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid message id " +
            "When getting existing message by api " +
            "Then should return response bad request")
    void getExistingMessageByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/messages/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When getting all messages by api returns not empty list " +
            "Then should return response ok")
    void getNotEmptyAllMessagesByValidUserId() throws Exception {
        long id = 1;

        when(detailsService.getUserById(id)).thenReturn(Optional.of(new User()));
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        when(messageService.getAllMessagesBySenderAndReceiver(any(User.class), any(User.class))).thenReturn(List.of(new Message()));

        this.mvc.perform( get("/api/messages/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When getting all messages by api returns empty list " +
            "Then should return response no content")
    void getEmptyAllMessagesByValidUserId() throws Exception {
        long id = 1;

        when(detailsService.getUserById(id)).thenReturn(Optional.of(new User()));
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform( get("/api/messages/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When getting all messages by api returns empty list " +
            "Then should return response no content")
    void getAllMessagesByInvalidUserId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/messages/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given non existing user id " +
            "When getting all messages by api " +
            "Then should return response no content")
    void getAllMessagesByNonExistingUserId() throws Exception {
        long id = 1;

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform( get("/api/messages/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given " +
            "When getting notifications counts of unread messages by api returns not empty list " +
            "Then should return response ok")
    void getAllNotEmptyNotificationsCounts() throws Exception {
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        when(messageService.getAllNotificationsCount(any(User.class))).thenReturn(Map.of((long) 1,(long) 1));

        this.mvc.perform( get("/api/messages/notify")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given " +
            "When getting notifications counts of unread messages by api returns empty list " +
            "Then should return response ok")
    void getAllEmptyNotificationsCounts() throws Exception {
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform( get("/api/messages/notify")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given valid user id" +
            "When sending message to existing user by api " +
            "Then should return response ok")
    void sendMessageToExistingUserByItsValidId() throws Exception {
        long id = 1;
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "".getBytes());

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        when(detailsService.getUserById(id)).thenReturn(Optional.of(new User()));
        when(messageService.createMessage(any(Message.class))).thenReturn(new Message());

        this.mvc.perform(multipart("/api/messages/user/{id}", id)
                .file(image)
                .part(content))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Given invalid user id" +
            "When sending message to user by api " +
            "Then should return response bad request")
    void sendMessageToUserByItsInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform(multipart("/api/messages/user/{id}", id))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid user id" +
            "When sending message to non existing user by api " +
            "Then should return response ok")
    void sendMessageToNonExistingUserByValidId() throws Exception {
        long id = 1;
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "".getBytes());

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform(multipart("/api/messages/user/{id}", id)
                        .file(image)
                        .part(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given valid user id" +
            "When sending message to existing user by api throws file error " +
            "Then should return response internal server error")
    void sendMessageToExistingUserByItsValidIdWithFileError() throws Exception {
        long id = 1;
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "file.png", "image/png" , "file".getBytes());

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        fileUtils.when(() -> FileUtils.saveFile(any(), any(), any())).thenThrow(IOException.class);
        when(detailsService.getUserById(id)).thenReturn(Optional.of(new User()));

        this.mvc.perform(multipart("/api/messages/user/{id}", id)
                        .file(image)
                        .part(content))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Given valid user id" +
            "When setting messages seen with existing user by api " +
            "Then should return response ok")
    void setMessagesWithExistingUserSeenByItsValidId() throws Exception {
        long id = 1;

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        when(detailsService.getUserById(id)).thenReturn(Optional.of(new User()));

        this.mvc.perform(put("/api/messages/user/{id}/mark-seen", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid user id" +
            "When setting messages seen with non existing user by api " +
            "Then should return response not found")
    void setMessagesWithNonExistingUserSeenByItsValidId() throws Exception {
        long id = 1;

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform(put("/api/messages/user/{id}/mark-seen", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid user id" +
            "When setting messages seen with user by api " +
            "Then should return response not found")
    void setMessagesWithUserSeenByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform(put("/api/messages/user/{id}/mark-seen", id))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}