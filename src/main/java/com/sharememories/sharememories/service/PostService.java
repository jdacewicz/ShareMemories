package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    private PostRepository repository;

    @Autowired
    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Optional<Post> getPost(Long id) {
        return repository.findById(id);
    }

    public void deletePost(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    public Post createPost(Post newPost) {
        return repository.save(newPost);
    }
}
