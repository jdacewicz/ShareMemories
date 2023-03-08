package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Post;
import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.PostRepository;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {

    private ReactionRepository reactionRepository;
    private PostRepository postRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository, PostRepository postRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
    }

    public Optional<Reaction> getReaction(Integer id) {
        return reactionRepository.findById(id);
    }

    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    public void deleteReaction(Integer id) {
        reactionRepository.deleteById(id);
        reactionRepository.flush();
    }

    public Reaction replaceReaction(Integer id, Reaction newReaction) {
        return reactionRepository.findById(id).map(reaction -> {
            reaction.setName(newReaction.getName());
            reaction.setImage(newReaction.getImage());
            return reactionRepository.save(reaction);
        }).orElseGet(() -> reactionRepository.save(newReaction));
    }

    public Reaction createReaction(Reaction newReaction) {
        return reactionRepository.save(newReaction);
    }

    public void reactToPost(Integer reactionId, Long postId) {
        reactionRepository.findById(reactionId).map(reaction -> {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                reaction.addPost(post.get());
            }
            return reactionRepository.save(reaction);
        });
    }
}
