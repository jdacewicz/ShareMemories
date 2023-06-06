package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.CommentRepository;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import com.sharememories.sharememories.util.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.Optional;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ReactionRepository reactionRepository;
    private final SecurityUserDetailsService detailsService;


    public Comment getComment(long id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Comment not found."));
    }

    public void deleteComment(long commentId) throws IOException {
        Comment comment = commentRepository.findById(commentId)
                .map(c -> {
                    c.getPost()
                            .getComments()
                            .remove(c);
                    return c;
                }).orElseThrow(
                        () -> new NotFoundException("Comment not found."));

        String image = comment.getImage();
        if (image != null) {
            FileUtils.deleteFile(Comment.IMAGES_DIRECTORY_PATH, image);
        }
        commentRepository.delete(comment);

    }

    public Comment reactToComment(int reactionId, long commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            Optional<Reaction> reaction = reactionRepository.findById(reactionId);
            if (reaction.isPresent()) {
                comment.addReaction(reaction.get());
                return commentRepository.save(comment);
            }
            throw new NotFoundException("Could not find reaction.");
        }).orElseThrow(
                () -> new NotFoundException("Could not find comment."));
    }

    public Comment commentPost(long postId, String commentContent, MultipartFile file) throws IOException {
        Comment comment = new Comment(commentContent, getLoggedUser(detailsService));

        if (!file.isEmpty() && file.getOriginalFilename() != null) {
            String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());

            FileUtils.saveFile(Comment.IMAGES_DIRECTORY_PATH, fileName, file);
            comment.setImage(fileName);
        }

        return postRepository.findById(postId)
                .map(post -> {
                    comment.setPost(post);
                    return commentRepository.save(comment);
                }).orElseThrow(
                        () -> new NotFoundException("Post not found."));
    }
}
