package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.PostRepository;
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

class PostServiceTest {

    @InjectMocks
    private PostService service;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Given reaction and post ids " +
            "When adding existing reaction to existing post " +
            "Then reaction should be added")
    void addingExistingReactionToExistingPostByReactionAndPostIds() {
        int reactionId = 1;
        long postId = 1;

        Post post = new Post();
        Reaction reaction = new Reaction();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findById(reactionId)).thenReturn(Optional.of(reaction));
        when(postRepository.save(post))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<Post> output = service.reactToPost(reactionId, postId);
        assertTrue(output.isPresent());
        assertTrue(output.get().getReactions().contains(reaction));
    }

    @Test
    @DisplayName("Given reaction and post ids " +
            "When adding non existing reaction to existing post " +
            "Then reaction should not be added")
    void addingNonExistingReactionToExistingPostByReactionAndPostIds() {
        int reactionId = 1;
        long postId = 1;

        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<Post> output = service.reactToPost(reactionId, postId);
        assertFalse(output.isPresent());
    }

    @Test
    @DisplayName("Given reaction and post ids " +
            "When adding existing reaction to non existing post " +
            "Then reaction should not be added")
    void addingExistingReactionToNonExistingPostByReactionAndPostIds() {
        int reactionId = 1;
        long postId = 1;

        Reaction reaction = new Reaction();
        when(reactionRepository.findById(reactionId)).thenReturn(Optional.of(reaction));

        Optional<Post> output = service.reactToPost(reactionId, postId);
        assertFalse(output.isPresent());
    }
}