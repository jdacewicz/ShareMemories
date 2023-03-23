package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {

    private ReactionRepository reactionRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public Optional<Reaction> getReaction(int id) {
        return reactionRepository.findById(id);
    }

    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    public Optional<String> getReactionImageName(int id) {
        return reactionRepository.findById(id)
                .map(r -> r.getImage());
    }

    public Reaction createReaction(Reaction reaction) {
        return reactionRepository.save(reaction);
    }

    public Reaction replaceReaction(int id, Reaction reaction) {
        return reactionRepository.findById(id)
                .map(r -> {
                    r.setName(reaction.getName());
                    if (reaction.getImage() != null)
                        r.setImage(reaction.getImage());

                    return reactionRepository.save(r);
                }).orElseGet(() -> reactionRepository.save(reaction));
    }

    public void deleteReaction(Integer id) {
        reactionRepository.deleteById(id);
    }
}
