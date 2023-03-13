package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
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

    private static final String UPLOAD_DIR = "uploads/pictures";
    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    public void deletePost(Long id) {
        Optional<Post> foundPost = postRepository.findById(id);

        if (foundPost.isPresent()) {
            Post post = foundPost.get();
            try {
                FileUtils.deleteFile(UPLOAD_DIR, post.getImage());
                postRepository.delete(post);
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
        postJson.setImage(uploadImage(file));
        postJson.setContent(content);

        return postRepository.save(postJson);
    }

    public String uploadImage(MultipartFile file) {
        String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
        try {
            FileUtils.saveFile(UPLOAD_DIR, fileName, file);
            return fileName;
        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }
}
