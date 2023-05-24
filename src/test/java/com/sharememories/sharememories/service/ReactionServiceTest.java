package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Given reaction id and reaction " +
            "When updating existing reaction " +
            "Then reaction should be updated")
    void updateExistingReactionByReactionIdAndReaction() {
        int reactionId = 1;
        Reaction reaction = new Reaction("test");

        Reaction reactionSaved = new Reaction("test2");

        when(repository.findById(reactionId)).thenReturn(Optional.of(reactionSaved));
        when(repository.save(reactionSaved))
                .thenAnswer(i -> i.getArguments()[0]);

        Reaction output = service.replaceReaction(reactionId, reaction);
        assertEquals(reaction, output);
    }

    @Test
    @DisplayName("Given reaction id and reaction " +
            "When updating non existing reaction " +
            "Then new reaction should be created")
    void updateNonExistingReactionByReactionIdAndReaction() {
        int reactionId = 1;
        Reaction reaction = new Reaction("test");

        when(repository.save(reaction))
                .thenAnswer(i -> i.getArguments()[0]);

        Reaction output = service.replaceReaction(reactionId, reaction);
        assertEquals(reaction, output);
    }
}