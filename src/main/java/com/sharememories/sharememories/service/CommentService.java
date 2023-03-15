package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    private CommentRepository repository;

    @Autowired
    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public Optional<Comment> getComment(Long id) {
        return repository.findById(id);
    }
}
