package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.CommentRepository;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CommentServiceTest {

    @InjectMocks
    private CommentService service;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ReactionRepository reactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Given_PostIdAndCommentId_When_DeletingPostCommentByProperPostIdAndCommentId_Then_CommentRemovedFromPost() {
        //Given
        long postId = 1;
        long commentId = 1;
        //When
        Post post = new Post();
        Comment comment = new Comment();
        post.getComments().add(comment);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        service.deletePostComment(postId, commentId);
        boolean commentDeleted = post.getComments().isEmpty();
        //Then
        assert(commentDeleted);
    }

    @Test
    void Given_PostIdAndCommentId_When_DeletingPostCommentByProperPostIdAndWrongCommentId_Then_CommentNotRemovedFromPost() {
        //Given
        long postId = 1;
        long commentId = 1;
        //When
        Post post = new Post();
        Comment comment = new Comment();
        post.getComments().add(comment);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        service.deletePostComment(postId, commentId);
        boolean commentDeleted = post.getComments().isEmpty();
        //Then
        assertFalse(commentDeleted);
    }

    @Test
    void Given_ReactionIdAndCommentId_When_ReactingToCommentByProperReactionIdAndCommentId_Then_ReactionAddedToComment() {
        //Given
        int reactionId = 1;
        long commentId = 1;
        //When
        Reaction reaction = new Reaction();
        Comment comment = new Comment();

        when(reactionRepository.findById(reactionId)).thenReturn(Optional.of(reaction));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        service.reactToComment(reactionId, commentId);
        boolean reactionAdded = !comment.getReactions().isEmpty();
        //Then
        assert(reactionAdded);
    }
}