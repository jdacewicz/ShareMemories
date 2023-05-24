package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import com.sharememories.sharememories.util.UserUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SecurityUserDetailsService detailsService;

    private static MockedStatic<FileUtils> fileUtils;
    private static MockedStatic<UserUtils> userUtils;

    @BeforeEach
    void setUp() {
        userUtils.reset();
    }

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
    @DisplayName("Given valid user id " +
            "When getting existing user by api " +
            "Then should return response ok")
    void getExistingUserByValidId() throws Exception {
        long id = 1;
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("Test");

        when(detailsService.getUserById(id)).thenReturn(Optional.of(user));

        this.mvc.perform( get("/api/users/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When getting non existing user by api " +
            "Then should return response not found")
    void getNonExistingUserByValidId() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid user id " +
            "When getting user by api " +
            "Then should return response bad request")
    void getUserByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given " +
            "When getting all unknown message senders by api returns not empty list " +
            "Then should return response ok")
    void getAllUnknownMessageSendersReturnsNotEmptyList() throws Exception {
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("Test");

        User user2 = new User();
        user2.setFirstname("Test");
        user2.setLastname("Test");

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);
        when(detailsService.getAllUnknownMessageSenders(user, false)).thenReturn(Set.of(user2));

        this.mvc.perform( get("/api/users/contacts/unknown"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given " +
            "When getting all unknown message senders by api returns empty list " +
            "Then should return response no content")
    void getAllUnknownMessageSendersReturnsEmptyList() throws Exception {
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform( get("/api/users/contacts/unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given " +
            "When getting all known message senders by api returns not empty list " +
            "Then should return response ok")
    void getAllKnownMessageSendersReturnsNotEmptyList() throws Exception {
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("Test");

        User user2 = new User();
        user2.setFirstname("Test");
        user2.setLastname("Test");

        user.setContacts(Set.of(user2));
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);

        this.mvc.perform( get("/api/users/contacts"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given " +
            "When getting all known message senders by api returns empty list " +
            "Then should return response no content")
    void getAllKnownMessageSendersReturnsEmptyList() throws Exception {
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());

        this.mvc.perform( get("/api/users/contacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When adding existing user to logged user's contacts by api " +
            "Then should return response ok")
    void addExistingUserToContactsByValidId() throws Exception {
        long id = 1;
        User user = new User();

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);
        when(detailsService.addUserToFriendsList(user, id)).thenReturn(Optional.of(new User()));

        this.mvc.perform( put("/api/users/invite/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When adding non existing user to logged user's contacts by api " +
            "Then should return response not found")
    void addNonExistingUserToContactsByValidId() throws Exception {
        long id = 1;
        User user = new User();

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);

        this.mvc.perform( put("/api/users/invite/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid user id " +
            "When adding user to logged user's contacts by api " +
            "Then should return response bad request")
    void addUserToContactsByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( put("/api/users/invite/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When deleting existing user by api " +
            "Then should return response ok")
    void deleteExistingUserByValidId() throws Exception {
        long id = 1;

        when(detailsService.getUserById(id)).thenReturn(Optional.of(new User()));

        this.mvc.perform( delete("/api/users/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When deleting non existing user by api " +
            "Then should return response not found")
    void deleteNonExistingUserByValidId() throws Exception {
        long id = 1;

        this.mvc.perform( delete("/api/users/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid user id " +
            "When deleting user by api " +
            "Then should return response bad request")
    void deleteUserByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( delete("/api/users/{id}", id))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When deleting existing user by api throws file error " +
            "Then should return response internal server error")
    void deleteUserByValidIdThrowsFileError() throws Exception {
        long id = 1;
        User user = new User();
        user.setProfileImage("image.png");

        when(detailsService.getUserById(id)).thenReturn(Optional.of(user));
        fileUtils.when(() -> FileUtils.deleteFile(any(), any())).thenThrow(IOException.class);

        this.mvc.perform( delete("/api/users/{id}", id))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}