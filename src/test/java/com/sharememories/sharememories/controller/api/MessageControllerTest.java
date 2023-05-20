package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Message;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("Given logged user id " +
            "When getting not existing message by api " +
            "Then should return not found")
    void getNotEmptyNotificationsCountsByLoggedUserId() throws Exception {
        long id = 1;
        Message message = new Message();

        when(messageService.getMessage(id)).thenReturn(Optional.of(message));

        this.mvc.perform( get("/api/messages/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}