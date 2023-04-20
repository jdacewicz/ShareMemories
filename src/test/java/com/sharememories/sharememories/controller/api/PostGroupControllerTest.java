package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.service.PostGroupService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class PostGroupControllerTest {

    @InjectMocks
    private PostGroupController controller;
    @Mock
    private PostGroupService groupService;
    @Mock
    private SecurityUserDetailsService detailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


}