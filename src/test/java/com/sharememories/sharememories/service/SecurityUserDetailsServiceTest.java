package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
    void Given_LoggedUserAndAddedUserId_When_AddingUserToFriendsListByProperAddedUserIdAndNotEqualToLoggedUserId_Then_UserIsAddedToLoggedUserContacts() {
        //Given
        User loggedUser = new User();
        loggedUser.setId((long) 1);

        long addedUserId = 2;
        //When
        User addedUser = new User();
        addedUser.setId((long) 2);

        when(repository.findById(addedUserId)).thenReturn(Optional.of(addedUser));
        when(repository.save(loggedUser))
                .thenAnswer(i -> i.getArguments()[0]);

        User output = service.addUserToFriendsList(loggedUser, addedUserId).get();
        //Then
        assert(output.getContacts().contains(addedUser));
    }

    @Test
    void Given_LoggedUserAndAddedUserId_When_AddingUserToFriendsListByProperAddedUserIdAndEqualToLoggedUserId_Then_UserIsNotAddedToLoggedUserContacts() {
        //Given
        User loggedUser = new User();
        loggedUser.setId((long) 1);

        long addedUserId = 1;
        //When
        when(repository.findById(addedUserId)).thenReturn(Optional.of(loggedUser));

        Optional<User> output = service.addUserToFriendsList(loggedUser, addedUserId);
        //Then
        assertFalse(output.isPresent());
    }

    @Test
    void Given_LoggedUserAndAddedUserId_When_AddingUserToFriendsListByWrongAddedUserId_Then_UserIsNotAddedToLoggedUserContacts() {
        //Given
        User loggedUser = new User();
        long addedUserId = 2;
        //When
        Optional<User> output = service.addUserToFriendsList(loggedUser, addedUserId);
        //Then
        assertFalse(output.isPresent());
    }
}