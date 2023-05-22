package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ReactionService service;

    private static MockedStatic<FileUtils> fileUtils;
    private static MockedStatic<UserUtils> userUtils;

    @BeforeEach
    void setUp() {
        fileUtils.reset();
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
    @DisplayName("Given " +
            "When getting all reactions by api returns not empty list " +
            "Then should return response ok")
    void getAllReactionsReturnsNotEmptyList() throws Exception {
        when(service.getAllReactions()).thenReturn(List.of(new Reaction()));

        this.mvc.perform( get("/api/reactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given " +
            "When getting all reactions by api returns empty list " +
            "Then should return response no content")
    void getAllReactionsReturnsEmptyList() throws Exception {
        this.mvc.perform( get("/api/reactions")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given valid reaction id " +
            "When getting existing reaction by api " +
            "Then should return response ok")
    void getExistingReactionByItsValidId() throws Exception {
        int id = 1;
        when(service.getReaction(id)).thenReturn(Optional.of(new Reaction()));

        this.mvc.perform( get("/api/reactions/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid reaction id " +
            "When getting non existing reaction by api " +
            "Then should return response not found")
    void getNonExistingReactionByValidId() throws Exception {
        int id = 1;

        this.mvc.perform( get("/api/reactions/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid reaction id " +
            "When getting reaction by api " +
            "Then should return response bad request")
    void getReactionByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/reactions/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given name and image " +
            "When creating reaction by api " +
            "Then should return response ok")
    void createReaction() throws Exception {
        MockPart name = new MockPart("name", "name".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "file.png", "image/png" , "file".getBytes());

        when(service.createReaction(any(Reaction.class))).thenReturn(new Reaction());
        fileUtils.when(() -> FileUtils.generateUniqueName(any(String.class))).thenReturn(image.getOriginalFilename());

        this.mvc.perform( multipart("/api/reactions")
                        .file(image)
                        .part(name))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Given name and image " +
            "When creating reaction by api throws file error" +
            "Then should return response internal server error")
    void createReactionWithFileError() throws Exception {
        MockPart name = new MockPart("name", "name".getBytes());
        MockMultipartFile image = new MockMultipartFile("image" , "".getBytes());

        when(service.createReaction(any(Reaction.class))).thenReturn(new Reaction());
        fileUtils.when(() -> FileUtils.saveFile(any(), any(), any())).thenThrow(IOException.class);

        this.mvc.perform( multipart("/api/reactions")
                        .file(image)
                        .part(name))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Given name, image and valid id " +
            "When updating existing reaction by api " +
            "Then should return response ok")
    void updateExistingReactionByValidId() throws Exception {
        int id = 1;
        MockPart name = new MockPart("name", "name".getBytes());
        MockMultipartFile image = new MockMultipartFile("image" , "".getBytes());

        when(service.getReaction(id)).thenReturn(Optional.of(new Reaction()));
        when(service.replaceReaction(any(Integer.class), any(Reaction.class))).thenReturn(new Reaction());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/reactions/{id}", id);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform( builder
                        .file(image)
                        .part(name))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given name, image and valid id " +
            "When updating non existing reaction by api " +
            "Then should return response not found")
    void updateNonExistingReactionByValidId() throws Exception {
        int id = 1;
        MockPart name = new MockPart("name", "name".getBytes());
        MockMultipartFile image = new MockMultipartFile("image" , "".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/reactions/{id}", id);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform( builder
                        .file(image)
                        .part(name))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given name, image and invalid id " +
            "When updating reaction by api " +
            "Then should return response bad request")
    void updateReactionByInvalidId() throws Exception {
        float id = 1.2f;

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/reactions/{id}", id);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform(builder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given name, image and valid id " +
            "When updating existing reaction by api throws file error " +
            "Then should return response internal server error")
    void updateExistingReactionByValidIdThrowsFileError() throws Exception {
        int id = 1;
        MockPart name = new MockPart("name", "name".getBytes());
        MockMultipartFile image = new MockMultipartFile("image" , "image.png", "image/png", "image".getBytes());

        when(service.getReaction(id)).thenReturn(Optional.of(new Reaction()));
        fileUtils.when(() -> FileUtils.generateUniqueName(any(String.class))).thenReturn(image.getOriginalFilename());
        fileUtils.when(() -> FileUtils.saveFile(any(), any(), any())).thenThrow(IOException.class);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/reactions/{id}", id);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform( builder
                        .file(image)
                        .part(name))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Given valid reaction id " +
            "When deleting existing reaction by api " +
            "Then should return response ok")
    void deleteExistingReactionByValidId() throws Exception {
        int id = 1;

        when(service.getReaction(id)).thenReturn(Optional.of(new Reaction()));

        this.mvc.perform( delete("/api/reactions/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid reaction id " +
            "When deleting non existing reaction by api " +
            "Then should return response not found")
    void deleteNonExistingReactionByValidId() throws Exception {
        int id = 1;

        this.mvc.perform( delete("/api/reactions/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid reaction id " +
            "When deleting reaction by api " +
            "Then should return response bad request")
    void deleteReactionByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( delete("/api/reactions/{id}", id))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid reaction id " +
            "When deleting existing reaction by api throws file error" +
            "Then should return response internal server error")
    void deleteExistingReactionByValidIdThrowsFileError() throws Exception {
        int id = 1;

        Reaction reaction = new Reaction();
        reaction.setImage("image.png");
        when(service.getReaction(id)).thenReturn(Optional.of(reaction));
        fileUtils.when(() -> FileUtils.deleteFile(any(), any())).thenThrow(IOException.class);

        this.mvc.perform( delete("/api/reactions/{id}", id))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}