package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.repository.CommentRepository;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    public void deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);

        if (post.isPresent()) {
            try {
                FileUtils.deleteFile(Post.IMAGES_DIRECTORY_PATH,post.get().getImage());
                postRepository.delete(post.get());
                postRepository.flush();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public List<Post> getRandomPosts() {
        return postRepository.getRandomPosts();
    }

    public Post createPost(String content, MultipartFile file) {
        Post postJson = new Post();
        postJson.setImage(uploadImage(Post.IMAGES_DIRECTORY_PATH, file));
        postJson.setContent(content);

        return postRepository.save(postJson);
    }

    public Optional<Post> commentPost(Long id, String content, MultipartFile file) {
        return postRepository.findById(id).map(post -> {
            Comment newComment = new Comment();
            newComment.setContent(content);
            newComment.setImage(uploadImage(Comment.IMAGES_DIRECTORY_PATH,file));

            post.getComments().add(newComment);
            return postRepository.save(post);
        });
    }

    public void deletePostComment(Long postId, Long commentId) {
        Optional<Post> post = postRepository.findById(postId);
        Optional<Comment> comment = commentRepository.findById(commentId);

        if(post.isPresent() && comment.isPresent()) {
            try {
                FileUtils.deleteFile(Comment.IMAGES_DIRECTORY_PATH, comment.get().getImage());
            } catch (IOException e) {
                System.out.println(e);
            }
            post.get()
                    .getComments()
                    .remove(comment);
            postRepository.save(post.get());
        }
    }

    public String uploadImage(String uploadDir, MultipartFile file) {
        String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
        try {
            FileUtils.saveFile(uploadDir, fileName, file);
            return fileName;
        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }
}
