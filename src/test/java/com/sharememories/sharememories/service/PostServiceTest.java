package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
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
    void Give_ReactionIdAndPostId_When_ReactingToPostByProperReactionIdAndPostId_Then_ReactionAddedToPost() {
        //Given
        int reactionId = 1;
        long postId = 1;
        //When
        Post post = new Post();
        Reaction reaction = new Reaction();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findById(reactionId)).thenReturn(Optional.of(reaction));
        when(postRepository.save(post))
                .thenAnswer(i -> i.getArguments()[0]);

        Post output = service.reactToPost(reactionId, postId).get();
        //Then
        assertFalse(output.getReactions().isEmpty());
    }

    @Test
    void Give_ReactionIdAndPostId_When_ReactingToPostByWrongReactionIdAndProperPostId_Then_ReactionNotAddedToPost() {
        //Given
        int reactionId = 1;
        long postId = 1;
        //When
        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<Post> output = service.reactToPost(reactionId, postId);
        //Then
        assertFalse(output.isPresent());
    }
}