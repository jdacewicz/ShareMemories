package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SecurityUserDetailsServiceTest {

    @InjectMocks
    private SecurityUserDetailsService service;

    @Mock
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given added user id and logged user " +
            "When adding existing user with id not equal logged user id to logged user contacts " +
            "Then user should be added")
    void addExistingUserWithIdNotEqualLoggedUserIdToLoggedUserContacts() {
        User loggedUser = new User();
        loggedUser.setId((long) 1);

        long addedUserId = 2;

        User addedUser = new User();
        addedUser.setId((long) 2);

        when(repository.findById(addedUserId)).thenReturn(Optional.of(addedUser));
        when(repository.save(loggedUser))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<User> output = service.addUserToFriendsList(loggedUser, addedUserId);

        assertTrue(output.isPresent());
        assertTrue(output.get().getContacts().contains(addedUser));
    }

    @Test
    @DisplayName("Given added user id and logged user " +
            "When adding existing user with id equal logged user id to logged user contacts " +
            "Then user should not be added")
    void addExistingUserWithIdEqualLoggedUserIdToLoggedUserContacts() {
        User loggedUser = new User();
        loggedUser.setId((long) 1);

        long addedUserId = 1;

        when(repository.findById(addedUserId)).thenReturn(Optional.of(loggedUser));

        Optional<User> output = service.addUserToFriendsList(loggedUser, addedUserId);
        assertFalse(output.isPresent());
    }

    @Test
    @DisplayName("Given added user id and logged user " +
            "When adding non existing user to logged user contacts " +
            "Then user should not be added")
    void addNonExistingUserToLoggedUserContacts() {
        User loggedUser = new User();
        long addedUserId = 2;

        Optional<User> output = service.addUserToFriendsList(loggedUser, addedUserId);

        assertFalse(output.isPresent());
    }
}