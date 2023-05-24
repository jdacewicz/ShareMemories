package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.CommentRepository;
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
    @DisplayName("Given post and comment ids " +
            "When deleting comment form existing post " +
            "Then comment should be removed")
    void deleteCommentFromExistingPostByPostAndCommentIds() {
        long postId = 1;
        long commentId = 1;

        Post post = new Post();
        Comment comment = new Comment();
        post.getComments().add(comment);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        service.deletePostComment(postId, comment);
        assert(post.getComments().isEmpty());
    }

    @Test
    @DisplayName("Given reaction and comment ids " +
            "When adding existing reaction to existing comment " +
            "Then reaction should be added")
    void addExistingReactionToExistingCommentByReactionAndCommentIds() {
        int reactionId = 1;
        long commentId = 1;

        Reaction reaction = new Reaction();
        Comment comment = new Comment();

        when(reactionRepository.findById(reactionId)).thenReturn(Optional.of(reaction));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        service.reactToComment(reactionId, commentId);
        assertFalse(comment.getReactions().isEmpty());
    }

    @Test
    @DisplayName("Given reaction and comment ids " +
            "When adding non existing reaction to existing comment " +
            "Then reaction should not be added")
    void addNonExistingReactionToExistingCommentByReactionAndCommentIds() {
        int reactionId = 1;
        long commentId = 1;

        Comment comment = new Comment();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        service.reactToComment(reactionId, commentId);
        assert(comment.getReactions().isEmpty());
    }

    @Test
    @DisplayName("Given post id and comment " +
            "When creating comment to existing post " +
            "Then comment should be created")
    void createCommentToExistingPostByPostIdAndComment() {
        long postId = 1;
        Comment comment = new Comment();

        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(comment))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<Comment> output = service.commentPost(postId, comment);
        assertTrue(output.isPresent());
        assertTrue(output.get().getPost().equals(post));
    }

    @Test
    @DisplayName("Given post id and comment " +
            "When creating comment to non existing post " +
            "Then comment should not be created")
    void createCommentToNonExistingPostByPostIdAndComment() {
        long postId = 1;
        Comment comment = new Comment();

        Optional<Comment> output = service.commentPost(postId, comment);
        assertFalse(output.isPresent());
    }
}