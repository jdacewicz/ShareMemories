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

    private PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
        postRepository.flush();
    }

    public List<Post> getRandomPosts() {
        return postRepository.getRandomPosts();
    }

    public Post createPost(String content, MultipartFile file) {
        Post postJson = new Post();

        String fileName = FileUtils.generateUniqueName(file.getOriginalFilename());
        String uploadDir = "uploads/pictures";
        try {
            FileUtils.saveFile(uploadDir, fileName, file);
            postJson.setImage(fileName);
        } catch (IOException e) {
            System.out.println(e);
        }
        postJson.setContent(content);

        return postRepository.save(postJson);
    }
}
