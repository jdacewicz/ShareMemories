package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.ReactionCounter;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private ReactionRepository reactionRepository;

    @Autowired
    public PostService(PostRepository postRepository, ReactionRepository reactionRepository) {
        this.postRepository = postRepository;
        this.reactionRepository = reactionRepository;
    }

    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
        postRepository.flush();
    }

    public Post createPost(Post newPost) {
        newPost.setReactionsCounters(reactionRepository.findAll()
                .stream()
                .map(r -> new ReactionCounter(r))
                .toList());
        return postRepository.save(newPost);
    }
}
