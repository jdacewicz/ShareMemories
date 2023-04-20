package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.PostGroup;
import com.sharememories.sharememories.service.PostGroupService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PostGroupControllerTest {

    @InjectMocks
    private PostGroupController controller;
    @Mock
    private PostGroupService groupService;
    //@Mock
    //private SecurityUserDetailsService detailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void Given_Id_When_GettingGroupByProperIdByAPI_Then_ReturnedResponseOkWithGroup() {
        //Given
        long id = 1;
        //When
        PostGroup group = new PostGroup();

        when(groupService.getGroup(id)).thenReturn(Optional.of(group));

        ResponseEntity response = controller.getGroup(id);
        //Then
        assertEquals(ResponseEntity.ok(group), response);
    }

    @Test
    void Given_Id_When_GettingGroupByWrongIdByAPI_Then_ReturnedResponseNotFound() {
        //Given
        long id = 1;
        //When
        ResponseEntity response = controller.getGroup(id);
        //Then
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build().getStatusCode(), response.getStatusCode());
    }

}