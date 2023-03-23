package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.repository.CommentRepository;
import com.sharememories.sharememories.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Optional<Comment> getComment(Long id) {
        return commentRepository.findById(id);
    }

    public Optional<String> getCommentImageName(long id) {
        return commentRepository.findById(id)
                .map(c -> c.getImage());
    }

    public void deletePostComment(long postId, long commentId) {
        postRepository.findById(postId).map(post -> {
            Optional<Comment> comment = commentRepository.findById(commentId);
            if (comment.isPresent()) {
                post.removeComment(comment.get());
            }
            return postRepository.save(post);
        });
        commentRepository.deleteById(commentId);
    }
}
