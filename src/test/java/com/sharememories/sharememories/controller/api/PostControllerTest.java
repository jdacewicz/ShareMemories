package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.controller.api.PostController;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.PostService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PostControllerTest {

    @InjectMocks
    private PostController controller;
    @Mock
    private PostService postService;
    @Mock
    SecurityUserDetailsService detailsService;
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
        Mockito.when(postService.getPost(id)).thenReturn(Optional.of(post));

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
        Mockito.when(postService.getRandomPosts()).thenReturn(List.of());

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
        Mockito.when(postService.getRandomPosts()).thenReturn(posts);

        ResponseEntity response = controller.getRandomPosts();
        //Then
        assertEquals(ResponseEntity.ok(posts), response);
    }

    @Test
    void Given_Content_When_CreatingPostByAPI_Then_ReturnedResponseCreatedWithPost() {
        //Given
        String content = "content";
        MockMultipartFile file = new MockMultipartFile("image.png", "content".getBytes());
        //When
        User user = new User("user");
        Post post = new Post(content, file.getOriginalFilename(), user);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));
        Mockito.when(postService.createPost(any(Post.class))).thenReturn(post);

        ResponseEntity response = controller.createPost(content, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(post), response);
    }

    @Test
    void Given_ContentAndFile_When_CreatingPostByAPI_Then_ReturnedResponseCreatedWithPost() {
        //Given
        String content = "content";
        MockMultipartFile file = new MockMultipartFile("name", null, null, new byte[0]);
        //When
        User user = new User("user");
        Post post = new Post(content, user);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));
        Mockito.when(postService.createPost(any(Post.class))).thenReturn(post);

        ResponseEntity response = controller.createPost(content, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(post), response);
    }

    @Test
    void Given_ContentAndFile_When_ErrorWhileCreatingPostByAPI_Then_ReturnedResponseInternalServerError() {
        //Given
        String content = "content";
        MockMultipartFile file = new MockMultipartFile("image.png", "content".getBytes());
        //When
        User user = new User("user");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));
        fileUtils.when(() -> FileUtils.generateUniqueName(file.getOriginalFilename())).thenReturn(file.getOriginalFilename());
        fileUtils.when(() -> FileUtils.saveFile(Post.IMAGES_DIRECTORY_PATH, file.getOriginalFilename(), file)).thenThrow(IOException.class);

        ResponseEntity response = controller.createPost(content, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_PostIdReactionId_When_ReactingToPostByPostIdAndReactionIdByAPI_Then_ReturnedResponseOkWithPost() {
        //Given
        long postId = 1;
        int reactionId = 1;
        //When
        Post post = new Post();
        Mockito.when(postService.reactToPost(reactionId, postId)).thenReturn(Optional.of(post));

        ResponseEntity response = controller.reactToPost(reactionId, postId);
        //Then
        assertEquals(ResponseEntity.ok(post), response);
    }

    @Test
    void Given_PostIdReactionId_When_ReactingToPostByWrongPostIdOrReactionIdByAPI_Then_ReturnedResponseOkWithPost() {
        //Given
        long postId = 1;
        int reactionId = 1;
        //When
        ResponseEntity response = controller.reactToPost(reactionId, postId);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_Id_When_DeletingPostWithoutImageByIdByAPI_Then_ReturnedResponseOk() {
        //Given
        long id = 1;
        //When
        ResponseEntity response = controller.deletePost(id);
        //Then
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void Given_Id_When_DeletingPostWithImageByIdByAPI_Then_ReturnedResponseOk() {
        //Given
        long id = 1;
        //When
        String imageName = "name";
        Mockito.when(postService.getPostImageName(id)).thenReturn(Optional.of(imageName));

        ResponseEntity response = controller.deletePost(id);
        //Then
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void Given_Id_When_DeletingPostWithImageByWrongIdByAPI_Then_ReturnedResponseInternalServerError() {
        //Given
        long id = 1;
        //When
        String imageName = "name";
        Mockito.when(postService.getPostImageName(id)).thenReturn(Optional.of(imageName));
        fileUtils.when(() -> FileUtils.deleteFile(Post.IMAGES_DIRECTORY_PATH, imageName)).thenThrow(IOException.class);

        ResponseEntity response = controller.deletePost(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build().getStatusCode(), response.getStatusCode());
    }
}