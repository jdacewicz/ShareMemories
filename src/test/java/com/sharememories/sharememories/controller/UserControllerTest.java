package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @InjectMocks
    private UserController controller;
    @Mock
    private SecurityUserDetailsService service;
    static MockedStatic<FileUtils> fileUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileUtils.reset();
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
    void Given_Id_When_GettingUserByIdByAPI_Then_ReturnedResponseOkWithUser() {
        //Given
        long id = 1;
        //When
        User user = new User();
        Mockito.when(service.getUserById(id)).thenReturn(Optional.of(user));

        ResponseEntity response = controller.getUser(id);
        //Then
        assertEquals(ResponseEntity.ok(user), response);
    }

    @Test
    void Given_Id_When_GettingUserByWrongIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        long id = 1;
        //When
        ResponseEntity response = controller.getUser(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_Id_When_DeletingUserByIdByAPI_Then_ReturnedResponseOk() {
        //Given
        long id = 1;
        //When
        String imageName = "name.png";
        Mockito.when(service.getUserImageName(id)).thenReturn(Optional.of(imageName));

        ResponseEntity response = controller.deleteUser(id);
        //Then
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void Given_Id_When_ErrorWhileDeletingUserByIdByAPI_Then_ReturnedResponseInternalServerError() {
        //Given
        long id = 1;
        //When
        String imageName = "name.png";
        Mockito.when(service.getUserImageName(id)).thenReturn(Optional.of(imageName));
        fileUtils.when(() -> FileUtils.deleteFile(User.IMAGES_DIRECTORY_PATH, imageName)).thenThrow(IOException.class);

        ResponseEntity response = controller.deleteUser(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build().getStatusCode(), response.getStatusCode());
    }
}