package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.ReactionCounter;
import com.sharememories.sharememories.repository.ReactionCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReactionCounterService {

    private ReactionCounterRepository repository;

    @Autowired
    public ReactionCounterService(ReactionCounterRepository repository) {
        this.repository = repository;
    }

    public Optional<ReactionCounter> getReactionCounter(Long id) {
        return repository.findById(id);
    }

    public void deleteReactionCounter(Long id) {
        repository.deleteById(id);
        repository.flush();
    }

    public ReactionCounter replaceReactionCounter(Long id, ReactionCounter newReactionCounter) {
        return repository.findById(id).map(counter -> {
            counter.setReaction(newReactionCounter.getReaction());
            counter.setCount(newReactionCounter.getCount());
            return repository.save(counter);
        }).orElseGet(() -> repository.save(newReactionCounter));
    }

    public ReactionCounter createReactionCounter(ReactionCounter newReactionCounter) {
        return repository.save(newReactionCounter);
    }
}
