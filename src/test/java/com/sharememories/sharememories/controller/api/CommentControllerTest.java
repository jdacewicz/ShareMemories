package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.service.CommentService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
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

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @DisplayName("Given comment id When getting existing comment by api Then should return response ok")
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
    @DisplayName("Given comment id When getting non existing comment by api Then should return response not found")
    void getNonExistingCommentById() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/comments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid comment ids When getting comment by api Then should return response bad request")
    void getCommentByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/comments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given comment and post ids When deleting existing comment by api Then should return response ok")
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
    @DisplayName("Given comment and post ids When deleting non existing comment by api Then should return response not found")
    void deleteNonExistingCommentByCommentAndPostIds() throws Exception {
        long commentId = 1;
        long postId = 1;

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid comment and post ids When deleting comment by api Then should return response bad request")
    void deleteCommentByInvalidCommentAndPostIds() throws Exception {
        float commentId = 1.2f;
        float postId = 1.2f;

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given comment and post ids When deleting comment by api throws error while deleting image Then should return response internal server error")
    void deleteCommentByCommentAndPostIdsWithFileError() throws Exception {
        long commentId = 1;
        long postId = 1;

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setImage("image.png");

        when(commentService.getComment(commentId)).thenReturn(Optional.of(comment));
        try (MockedStatic<FileUtils> utils = Mockito.mockStatic(FileUtils.class)) {
            utils.when(() -> FileUtils.deleteFile(any(String.class), any(String.class))).thenThrow(IOException.class);
        }

        this.mvc.perform(delete("/api/comments/{commentId}/post/{postId}", commentId, postId))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}