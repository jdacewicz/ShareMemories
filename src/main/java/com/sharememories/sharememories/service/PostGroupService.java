package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.PostGroup;
import com.sharememories.sharememories.repository.PostGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PostGroupService {

    private PostGroupRepository repository;


    public Optional<PostGroup> getGroup(long id) {
        return repository.findById(id);
    }

    public PostGroup createGroup(PostGroup group) {
        return repository.save(group);
    }

    public void deleteGroup(long id) {
        repository.deleteById(id);
    }
}
