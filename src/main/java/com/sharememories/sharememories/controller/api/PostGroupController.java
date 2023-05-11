package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.PostGroup;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.PostGroupService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/posts/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostGroupController {

    private final PostGroupService groupService;
    private final SecurityUserDetailsService detailsService;

    @Autowired
    public PostGroupController(PostGroupService groupService, SecurityUserDetailsService detailsService) {
        this.groupService = groupService;
        this.detailsService = detailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(@PathVariable long id) {
        Optional<PostGroup> group = groupService.getGroup(id);
        if (group.isEmpty()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Post group not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        } else {
            return ResponseEntity.ok(group.get());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createGroup(@RequestPart("name") String name,
                                         @RequestPart("members") Set<User> members) {
        User user = detailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        PostGroup group = groupService.createGroup(new PostGroup(name, user, members));
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}
