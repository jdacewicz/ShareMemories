package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.MessageGroup;
import com.sharememories.sharememories.repository.MessageGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class MessageGroupService {

    private MessageGroupRepository repository;

    @Autowired
    public MessageGroupService(MessageGroupRepository repository) {
        this.repository = repository;
    }

    public Optional<MessageGroup> getGroup(long id) {
        return repository.findById(id);
    }

    public Set<MessageGroup> getRandomGroups() {
        return repository.getRandomGroups();
    }

    public MessageGroup createGroup(MessageGroup group) {
        return repository.save(group);
    }

    public void deleteGroup(long id) {
        repository.deleteById(id);
    }
}
