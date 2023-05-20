package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.CommentService;
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
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

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
    @DisplayName("Given valid comment id " +
            "When getting existing comment by api " +
            "Then should return response ok")
    void getExistingCommentById() throws Exception {
        long id = 1;
        Comment comment = new Comment();
        comment.setId(id);

        when(commentService.getComment(id)).thenReturn(Optional.of(comment));

        this.mvc.perform( get("/api/comments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("Given valid comment id " +
            "When getting non existing comment by api " +
            "Then should return response not found")
    void getNonExistingCommentById() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/comments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid comment ids " +
            "When getting comment by api " +
            "Then should return response bad request")
    void getCommentByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/comments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid comment and post ids " +
            "When deleting existing comment by api " +
            "Then should return response ok")
    void deleteExistingCommentByCommentAndPostIds() throws Exception {
        long commentId = 1;
        long postId = 1;

        Comment comment = new Comment();
        comment.setId(commentId);

        when(commentService.getComment(commentId)).thenReturn(Optional.of(comment));

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid comment and post ids " +
            "When deleting non existing comment by api " +
            "Then should return response not found")
    void deleteNonExistingCommentByCommentAndPostIds() throws Exception {
        long commentId = 1;
        long postId = 1;

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid comment and post ids " +
            "When deleting comment by api " +
            "Then should return response bad request")
    void deleteCommentByInvalidCommentAndPostIds() throws Exception {
        float commentId = 1.2f;
        float postId = 1.2f;

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid comment and post ids " +
            "When deleting comment by api throws error while deleting image " +
            "Then should return response internal server error")
    void deleteCommentByCommentAndPostIdsWithFileError() throws Exception {
        long commentId = 1;
        long postId = 1;

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setImage("image.png");

        when(commentService.getComment(commentId)).thenReturn(Optional.of(comment));
        fileUtils.when(() -> FileUtils.deleteFile(any(String.class), any(String.class))).thenThrow(IOException.class);

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Given valid post id and content " +
            "When creating comment for existing post by api " +
            "Then should return response created")
    void createCommentForExistingPostByValidData() throws Exception {
        long postId = 1;
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "".getBytes());

        Comment comment = new Comment();
        User user = new User();

        when(commentService.commentPost(any(Long.class), any(Comment.class) )).thenReturn(Optional.of(comment));
        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/comments/post/{id}", postId);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform(builder
                        .file(image)
                        .part(content))
                        .andDo(print())
                        .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Given invalid post id or content " +
            "When creating comment for existing post by api " +
            "Then should return response bad request")
    void createCommentForExistingPostByNotValidData() throws Exception {
        float postId = 1.2f;

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/comments/post/{id}", postId);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform(builder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid post id and content " +
            "When creating comment for not existing post by api " +
            "Then should return response not found")
    void createCommentForNotExistingPostByValidData() throws Exception {
        long postId = 1;
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "".getBytes());

        User user = new User();

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/comments/post/{id}", postId);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform(builder
                        .file(image)
                        .part(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given valid post id, image and content " +
            "When creating comment for existing post by api saving file occurs error " +
            "Then should return response internal server error")
    void createCommentForPostByValidDataWithFileError() throws Exception {
        long postId = 1;
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png" ,"image.png".getBytes());

        User user = new User();

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(user);
        fileUtils.when(() -> FileUtils.saveFile(any(), any(), any())).thenThrow(IOException.class);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/comments/post/{id}", postId);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mvc.perform(builder
                        .file(image)
                        .part(content))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Given valid comment and reaction ids " +
            "When reacting to existing comment with existing reaction by api " +
            "Then should return response ok")
    void addExistingReactionToExistingCommentByValidCommentAndReactionIds() throws Exception {
        long commentId = 1;
        int reactionId = 1;

        Comment comment = new Comment();

        when(commentService.reactToComment(reactionId, commentId)).thenReturn(Optional.of(comment));

        this.mvc.perform(put("/api/comments/{commentId}/react/{reactionId}", commentId ,reactionId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid comment and reaction ids " +
            "When reacting to not existing comment with not existing reaction by api " +
            "Then should return response not found")
    void addNotExistingReactionToNotExistingCommentByValidCommentAndReactionIds() throws Exception {
        long commentId = 1;
        int reactionId = 1;

        this.mvc.perform(put("/api/comments/{commentId}/react/{reactionId}", commentId ,reactionId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid comment and reaction ids " +
            "When reacting to existing comment with existing reaction by api " +
            "Then should return response bad request")
    void addExistingReactionToExistingCommentByInvalidCommentAndReactionIds() throws Exception {
        float commentId = 1.2f;
        float reactionId = 1.2f;

        this.mvc.perform(put("/api/comments/{commentId}/react/{reactionId}", commentId ,reactionId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}