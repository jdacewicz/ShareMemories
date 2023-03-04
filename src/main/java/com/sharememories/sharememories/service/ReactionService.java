package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Reaction;
import com.sharememories.sharememories.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {

    private ReactionRepository repository;

    @Autowired
    public ReactionService(ReactionRepository repository) {
        this.repository = repository;
    }

    public Reaction saveReaction(Reaction newReaction) {
        return repository.save(newReaction);
    }

    public Optional<Reaction> getReaction(Integer id) {
        return repository.findById(id);
    }

    public List<Reaction> getAllReactions() {
        return repository.findAll();
    }

    public void deleteReaction(Integer id) {
        repository.deleteById(id);
        repository.flush();
    }
}
