package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.PostService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostService postService;

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
    @DisplayName("Given valid post id " +
            "When getting existing post by api " +
            "Then should return ok")
    void getExistingPostByValidId() throws Exception {
        long id = 1;

        when(postService.getPost(id)).thenReturn(Optional.of(new Post()));

        this.mvc.perform( get("/api/posts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid post id " +
            "When getting non existing post by api " +
            "Then should return not found")
    void getNonExistingPostByValidId() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/posts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid post id " +
            "When getting post by api " +
            "Then should return bad request")
    void getPostByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/posts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given " +
            "When getting random posts by api returns not empty list " +
            "Then should return ok")
    void getNotEmptyRandomPosts() throws Exception {
        long id = 1;

        when(postService.getRandomPosts()).thenReturn(List.of(new Post()));

        this.mvc.perform( get("/api/posts/random", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given " +
            "When getting random posts by api returns empty list " +
            "Then should return no content")
    void getEmptyRandomPosts() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/posts/random", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When getting all posts by api returns not empty list " +
            "Then should return ok")
    void getAllPostsByValidUserIdReturnsNotEmptyList() throws Exception {
        long id = 1;

        when(postService.getAllByCreatorId(id)).thenReturn(List.of(new Post()));

        this.mvc.perform( get("/api/posts/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid user id " +
            "When getting all posts by api returns empty list " +
            "Then should return no content")
    void getAllPostsByValidUserIdReturnsEmptyList() throws Exception {
        long id = 1;

        this.mvc.perform( get("/api/posts/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Given invalid user id " +
            "When getting all posts by api " +
            "Then should return bad request")
    void getAllPostsByInvalidUserId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( get("/api/posts/user/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid content and image " +
            "When creating post by api " +
            "Then should return created")
    void createPostWithValidData() throws Exception {
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image" , "".getBytes());

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        fileUtils.when(() -> FileUtils.saveFile(any(), any(), any())).thenThrow(IOException.class);
        when(postService.createPost(any(Post.class))).thenReturn(new Post());

        this.mvc.perform( multipart("/api/posts")
                        .file(image)
                        .part(content))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Given content and image " +
            "When creating post by api throws file error " +
            "Then should return internal server error")
    void createPostWithFileError() throws Exception {
        MockPart content = new MockPart("content", "content".getBytes());
        MockMultipartFile image = new MockMultipartFile("image", "file.png", "image/png" , "file".getBytes());

        userUtils.when(() -> UserUtils.getLoggedUser(any(SecurityUserDetailsService.class))).thenReturn(new User());
        fileUtils.when(() -> FileUtils.saveFile(any(), any(), any())).thenThrow(IOException.class);
        when(postService.createPost(any(Post.class))).thenReturn(new Post());

        this.mvc.perform( multipart("/api/posts")
                        .file(image)
                        .part(content))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Given valid post and reaction ids " +
            "When reacting to existing post by api " +
            "Then should return ok")
    void reactToExistingPostByItsAndReactionValidId() throws Exception {
        long postId = 1;
        int reactionId = 1;

        when(postService.reactToPost(reactionId, postId)).thenReturn(Optional.of(new Post()));

        this.mvc.perform( put("/api/posts/{postId}/react/{reactionId}", postId, reactionId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid post and reaction ids " +
            "When reacting to existing post by api " +
            "Then should return ok")
    void reactToNonExistingPostByItsAndReactionValidId() throws Exception {
        long postId = 1;
        int reactionId = 1;

        this.mvc.perform( put("/api/posts/{postId}/react/{reactionId}", postId, reactionId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid post and reaction ids " +
            "When reacting to post by api " +
            "Then should return bad request")
    void reactToPostByItsAndReactionInvalidId() throws Exception {
        float postId = 1.2f;
        float reactionId = 1.2f;

        this.mvc.perform( put("/api/posts/{postId}/react/{reactionId}", postId, reactionId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid post id " +
            "When deleting existing post by api " +
            "Then should return ok")
    void deleteExistingPostByItsValidId() throws Exception {
        long id = 1;

        when(postService.getPost(id)).thenReturn(Optional.of(new Post()));

        this.mvc.perform( delete("/api/posts/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid post id " +
            "When deleting non existing post by api " +
            "Then should return not found")
    void deleteNonExistingPostByValidId() throws Exception {
        long id = 1;

        this.mvc.perform( delete("/api/posts/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given invalid post id " +
            "When deleting post by api " +
            "Then should return bad request")
    void deletePostByInvalidId() throws Exception {
        float id = 1.2f;

        this.mvc.perform( delete("/api/posts/{id}", id))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given valid post id " +
            "When deleting existing post by api throws file error" +
            "Then should return internal server error")
    void deleteExistingPostByValidIdThrowsFileError() throws Exception {
        long id = 1;

        Post post = new Post();
        post.setImage("image.png");
        when(postService.getPost(id)).thenReturn(Optional.of(post));
        fileUtils.when(() -> FileUtils.deleteFile(any(), any())).thenThrow(IOException.class);

        this.mvc.perform( delete("/api/posts/{id}", id))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}