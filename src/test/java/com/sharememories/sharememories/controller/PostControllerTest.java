package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.PostService;
import com.sharememories.sharememories.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostControllerTest {

    @InjectMocks
    private PostController controller;
    @Mock
    private PostService service;
    static MockedStatic<FileUtils> fileUtils;

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
    void Given_Id_When_GettingPostByIdByAPIReturnsPost_Then_ReturnedResponseOkWithPost() {
        //Given
        long id = 1;
        //When
        Post post = new Post();
        Mockito.when(service.getPost(id)).thenReturn(Optional.of(post));

        ResponseEntity response = controller.getPost(id);
        //Then
        assertEquals(ResponseEntity.ok(post), response);
    }

    @Test
    void Given_Id_When_GettingPostByIdByAPINotReturnsPost_Then_ReturnedResponseNotFound() {
        //Given
        long id = 1;
        //When
        ResponseEntity response = controller.getPost(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given__When_GettingRandomPostsByAPIReturnsEmptyList_Then_ReturnedResponseNoContent() {
        //Given
        //When
        Mockito.when(service.getRandomPosts()).thenReturn(List.of());

        ResponseEntity response = controller.getRandomPosts();
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NO_CONTENT).build(), response);
    }

    @Test
    void Given__When_GettingRandomPostsByAPIReturnsNotEmptyList_Then_ReturnedResponseOKWithList() {
        //Given
        //When
        Post post = new Post();
        List<Post> posts = List.of(post);
        Mockito.when(service.getRandomPosts()).thenReturn(posts);

        ResponseEntity response = controller.getRandomPosts();
        //Then
        assertEquals(ResponseEntity.ok(posts), response);
    }

    @Test
    void createComment() {
        //Given
        //When
        //Then
    }

    @Test
    void reactToPost() {
        //Given
        //When
        //Then
    }

    @Test
    void delete() {
        //Given
        //When
        //Then
    }
}