package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    ReactionRepository reactionRepository;
    @Mock
    PostRepository postRepository;
    @InjectMocks
    PostService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Given_NewPost_When_PostIsCreated_Then_ProperPostReactionsCountersCount() {
        //Given
        Post post = new Post();

        List<Reaction> reactions = new ArrayList<>();
        Reaction reactionOne = new Reaction();
        Reaction reactionTwo = new Reaction();
        Reaction reactionThree = new Reaction();

        reactions.add(reactionOne);
        reactions.add(reactionTwo);
        reactions.add(reactionThree);
        when(reactionRepository.findAll()).thenReturn(reactions);

        //When
        service.createPost(post);

        //Then
        Assertions.assertEquals(3, post.getReactionsCounters().size());
        verify(reactionRepository, times(1)).findAll();
    }
}