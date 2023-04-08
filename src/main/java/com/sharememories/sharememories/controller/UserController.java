package com.sharememories.sharememories.controller;

import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private SecurityUserDetailsService userDetailsService;

    @Autowired
    public UserController(SecurityUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id ){
        Optional<User> user = userDetailsService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    @PutMapping("/invite/{addedUserId}")
    public ResponseEntity<?> addUserToFriends(@PathVariable long addedUserId) {
        User user = userDetailsService.getUserByUsername(SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName())
                .get();
        Optional<User> output = userDetailsService.addUserToFriendsList(user, addedUserId);
        if (!output.isPresent()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", HttpStatus.NOT_FOUND.value());
            map.put("message", "Could not find user with id " + addedUserId + ".");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
        return ResponseEntity.ok(output.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        Optional<String> userImageName = userDetailsService.getUserImageName(id);
        if (userImageName.isPresent()) {
            try {
                FileUtils.deleteFile(User.IMAGES_DIRECTORY_PATH, userImageName.get());
            } catch (IOException e) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                map.put("message", "Error while deleting User image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
            }
        }
        userDetailsService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
