package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.MessageGroup;
import com.sharememories.sharememories.service.MessageGroupService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MessageGroupControllerTest {

    @InjectMocks
    private MessageGroupController controller;
    @Mock
    private MessageGroupService groupService;
    @Mock
    private SecurityUserDetailsService detailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Given_Id_When_GettingGroupByProperIdByAPI_Then_ReturnedResponseOkWithGroup() {
        //Given
        long id = 1;
        //When
        MessageGroup group = new MessageGroup();

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

    @Test
    void Given__When_GettingRandomGroupsByAPIReturnsNotEmptySetOfGroups_Then_ReturnedResponseOkWithGroups() {
        //Given
        //When
        MessageGroup group = new MessageGroup();
        Set<MessageGroup> groupSet = Set.of(group);

        when(groupService.getRandomGroups()).thenReturn(groupSet);

        ResponseEntity response = controller.getRandomGroups();
        //Then
        assertEquals(ResponseEntity.ok(groupSet), response);
    }

    @Test
    void Given__When_GettingRandomGroupsByAPIReturnsEmptySetOfGroups_Then_ReturnedResponseNoContent() {
        //Given
        //When
        ResponseEntity response = controller.getRandomGroups();
        //Then
        assertEquals(ResponseEntity.noContent().build(), response);
    }
}