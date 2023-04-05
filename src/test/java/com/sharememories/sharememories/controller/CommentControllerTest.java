package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.CommentService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class CommentControllerTest {

    @InjectMocks
    private CommentController controller;
    @Mock
    private CommentService service;
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
    void Given_Id_When_GettingCommentByIdByAPI_Then_ReturnedResponseOkWithComment() {
        //Given
        long id = 1;
        //When
        Comment comment = new Comment();
        Mockito.when(service.getComment(id)).thenReturn(Optional.of(comment));

        ResponseEntity response = controller.getComment(id);
        //Then
        assertEquals(ResponseEntity.ok(comment), response);
    }

    @Test
    void Given_Id_When_GettingCommentByWrongIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        long id = 1;
        //When
        ResponseEntity response = controller.getComment(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_PostIdAndCommentId_When_DeletingCommentWithoutImageByPostIdAndCommentIdByAPI_Then_ReturnedResponseOk() {
        //Given
        long postId = 1;
        long commentId = 1;
        //When
        ResponseEntity response = controller.deleteComment(postId, commentId);
        //Then
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void Given_PostIdAndCommentId_When_DeletingCommentWithImageByPostIdAndCommentIdByAPI_Then_ReturnedResponseOk() {
        //Given
        long postId = 1;
        long commentId = 1;
        //When
        String image = "image.png";
        Mockito.when(service.getCommentImageName(commentId)).thenReturn(Optional.of(image));

        ResponseEntity response = controller.deleteComment(postId, commentId);
        //Then
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void Given_PostIdAndCommentId_When_ErrorWhileDeletingCommentWithImageByPostIdAndCommentIdByAPI_Then_ReturnedResponseInternalServerError() {
        //Given
        long postId = 1;
        long commentId = 1;
        //When
        String image = "image.png";
        Mockito.when(service.getCommentImageName(commentId)).thenReturn(Optional.of(image));
        fileUtils.when(() -> FileUtils.deleteFile(Comment.IMAGES_DIRECTORY_PATH, image)).thenThrow(IOException.class);

        ResponseEntity response = controller.deleteComment(postId, commentId);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_ReactionIdAndCommentId_When_ReactingToCommentByReactionIdAndCommentIdByAPI_Then_ReturnedResponseOkWithComment() {
        //Given
        int reactionId = 1;
        long commentId = 1;
        //When
        Comment comment = new Comment();
        Mockito.when(service.reactToComment(reactionId, commentId)).thenReturn(Optional.of(comment));

        ResponseEntity response = controller.reactToComment(reactionId, commentId);
        //Then
        assertEquals(ResponseEntity.ok(Optional.of(comment)), response);
    }

    @Test
    void Given_ReactionIdAndCommentId_When_ReactingToCommentByWrongReactionIdOrCommentIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        int reactionId = 1;
        long commentId = 1;
        //When
        ResponseEntity response = controller.reactToComment(reactionId, commentId);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_PostIdAndCommentContent_When_CreatingPostCommentByPostIdByAPI_Then_ReturnedResponseOkWithPost() {
        //Given
        long id = 1;
        String content = "content";
        MockMultipartFile file = new MockMultipartFile("name", null, null, new byte[0]);
        //When
        User user = new User("user");
        Comment comment = new Comment();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));
        Mockito.when(service.commentPost(any(Long.class), any(Comment.class))).thenReturn(Optional.of(comment));

        ResponseEntity response = controller.createComment(id, content, file);
        //Then
        assertEquals(ResponseEntity.ok(comment), response);
    }

    @Test
    void Given_PostIdAndCommentContentAndImage_When_CreatingPostCommentByPostIdByAPI_Then_ReturnedResponseOkWithPost() {
        //Given
        long id = 1;
        String content = "content";
        MockMultipartFile file = new MockMultipartFile("image.png", "content".getBytes());
        //When
        User user = new User("user");
        Comment comment = new Comment();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));
        fileUtils.when(() -> FileUtils.generateUniqueName(file.getOriginalFilename())).thenReturn(file.getOriginalFilename());
        Mockito.when(service.commentPost(any(Long.class), any(Comment.class))).thenReturn(Optional.of(comment));

        ResponseEntity response = controller.createComment(id, content, file);
        //Then
        assertEquals(ResponseEntity.ok(comment), response);
    }

    @Test
    void Given_PostIdAndCommentContentAndImage_When_ErrorWhileCreatingPostCommentByPostIdByAPI_Then_ReturnedResponseInternalServerError() {
        //Given
        long id = 1;
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
        fileUtils.when(() -> FileUtils.saveFile(Comment.IMAGES_DIRECTORY_PATH, file.getOriginalFilename(), file)).thenThrow(IOException.class);

        ResponseEntity response = controller.createComment(id, content, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_PostIdAndCommentContentAndImage_When_CreatingPostCommentByWrongPostIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        long id = 1;
        String content = "content";
        MockMultipartFile file = new MockMultipartFile("name", null, null, new byte[0]);
        //When
        User user = new User("user");
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(securityContext.getAuthentication().getName()).thenReturn(user.getUsername());
        Mockito.when(detailsService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));

        ResponseEntity response = controller.createComment(id, content, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }
}