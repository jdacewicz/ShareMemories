package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReactionServiceTest {

    @InjectMocks
    private ReactionService service;
    @Mock
    private ReactionRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Given_IdAndReaction_When_ReplacingReactionByProperId_Then_ReactionDataIsReplaced() {
        //Given
        int reactionId = 1;
        Reaction reaction = new Reaction("test");
        //When
        Reaction reactionSaved = new Reaction("test2");

        when(repository.findById(reactionId)).thenReturn(Optional.of(reactionSaved));
        when(repository.save(reactionSaved))
                .thenAnswer(i -> i.getArguments()[0]);

        Reaction output = service.replaceReaction(reactionId, reaction);
        //Then
        assertEquals(reaction, output);
    }

    @Test
    void Given_IdAndReaction_When_ReplacingReactionByWrongId_Then_NewReactionIsCreated() {
        //Given
        int reactionId = 1;
        Reaction reaction = new Reaction("test");
        //When
        when(repository.save(reaction))
                .thenAnswer(i -> i.getArguments()[0]);

        Reaction output = service.replaceReaction(reactionId, reaction);
        //Then
        assertEquals(reaction, output);
    }
}