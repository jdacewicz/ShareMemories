package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.service.ReactionService;
import com.sharememories.sharememories.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ReactionControllerTest {

    @InjectMocks
    private ReactionController controller;
    @Mock
    private ReactionService service;
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
    void Given_ReactionsList_When_GettingAllReactionsByAPI_Then_ReturnedResponseOKWithReactionsList() {
        //Given
        Reaction reaction1 = new Reaction(1);
        Reaction reaction2 = new Reaction(2);
        List<Reaction> reactions = List.of(reaction1, reaction2);
        //When
        Mockito.when(service.getAllReactions()).thenReturn(reactions);

        ResponseEntity response = controller.getAllReactions();
        //Then
        assertEquals(ResponseEntity.ok(reactions), response);
    }

    @Test
    void Given_NoReactions_When_GettingAllReactionsByAPI_Then_ReturnedResponseNoContent(){
        //Given
        //When
        Mockito.when(service.getAllReactions()).thenReturn(List.of());

        ResponseEntity response = controller.getAllReactions();
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NO_CONTENT).build(), response);
    }

    @Test
    void Given_ReactionWithId1_When_GettingReactionById1ByAPI_Then_ReturnedResponseOkWithReaction() {
        //Given
        Reaction reaction = new Reaction(1);
        //When
        Mockito.when(service.getReaction(1)).thenReturn(Optional.of(reaction));

        ResponseEntity response = controller.getReaction(1);
        //Then
        assertEquals(ResponseEntity.ok(reaction), response);
    }

    @Test
    void Given_NoReactions_When_GettingReactionById1ByAPI_Then_ReturnedResponseNotFound() {
        //Given
        //When
        ResponseEntity response = controller.getReaction(1);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_NameAndFile_When_CreatingReactionByAPI_Then_ReturnedResponseCreated() {
        //Given
        String name = "name";
        MockMultipartFile file = new MockMultipartFile("image.png", "test".getBytes());
        Reaction reaction = new Reaction(name, file.getOriginalFilename());
        //When
        fileUtils.when(() -> FileUtils.generateUniqueName(file.getOriginalFilename())).thenReturn(file.getOriginalFilename());
        Mockito.when(service.createReaction(any(Reaction.class))).thenReturn(reaction);

        ResponseEntity response = controller.createReaction(name, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(reaction), response);
    }

    @Test
    void Given_NameAndFile_When_ErrorWhileCreatingReactionByAPI_Then_ReturnedResponseInternalServerError() {
        //Given
        String name = "name";
        MockMultipartFile file = new MockMultipartFile("image.png", "test".getBytes());
        Reaction reaction = new Reaction(name, file.getOriginalFilename());
        //When
        fileUtils.when(() -> FileUtils.generateUniqueName(file.getOriginalFilename())).thenReturn(file.getOriginalFilename());
        fileUtils.when(() -> FileUtils.saveFile(Reaction.IMAGES_DIRECTORY_PATH, file.getOriginalFilename(), file)).thenThrow(IOException.class);

        ResponseEntity response = controller.createReaction(name, file);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build().getStatusCode(), response.getStatusCode());
    }

    @Test
    void Given_NameAndId_When_ReplacingReactionByIdByAPI_Then_ReturnedResponseOkWithReaction() {
        //Given
        String name = "name";
        MockMultipartFile file = new MockMultipartFile("name", null, null, new byte[0]);
        Reaction reaction = new Reaction(1, name, "image.png");
        //When
        Mockito.when(service.replaceReaction(any(Integer.class), any(Reaction.class))).thenReturn(reaction);

        ResponseEntity response = controller.replaceReaction(1, name, file);
        //Then
        assertEquals(ResponseEntity.ok(reaction), response);
    }


}