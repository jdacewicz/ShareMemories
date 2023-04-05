package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Post> getRandomPosts() {
        return postRepository.getRandomPosts();
    }

    public List<Post> getAllByCreatorId(long creatorId) {
        return postRepository.findByCreatorId(creatorId);
    }

    public Optional<String> getPostImageName(long id) {
        return postRepository.findById(id)
                .map(p -> p.getImage());
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(long id) {
        postRepository.deleteById(id);
    }

    public Optional<Post> reactToPost(int reactionId, long postId) {
        return postRepository.findById(postId).map(post -> {
            Optional<Reaction> reaction = reactionRepository.findById(reactionId);
            if (reaction.isPresent()) {
                post.addReaction(reaction.get());
                return postRepository.save(post);
            }
            return null;
        });
    }
}
