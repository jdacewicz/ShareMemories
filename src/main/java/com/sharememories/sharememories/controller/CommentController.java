package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.Comment;
import com.sharememories.sharememories.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/comments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private CommentService service;

    @Autowired
    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Optional<Comment> get(@PathVariable Long id) {
        return service.getComment(id);
    }

}
