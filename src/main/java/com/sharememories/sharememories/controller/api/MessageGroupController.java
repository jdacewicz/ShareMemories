package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.MessageGroup;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.MessageGroupService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/messages/groups/", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageGroupController {

    private MessageGroupService groupService;
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    public MessageGroupController(MessageGroupService groupService, SecurityUserDetailsService userDetailsService) {
        this.groupService = groupService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(@PathVariable long id) {
        Optional<MessageGroup> group = groupService.getGroup(id);
        if (!group.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Message group not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        } else {
            return ResponseEntity.ok(group.get());
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomGroups() {
        Set<MessageGroup> groups = groupService.getRandomGroups();
        if (groups.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(groups);
    }

    @PostMapping()
    public ResponseEntity<?> createGroup(@RequestPart("name") String name,
                                         @RequestPart("members") Set<User> members) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        MessageGroup group = groupService.createGroup(new MessageGroup(name, user, members));
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}
