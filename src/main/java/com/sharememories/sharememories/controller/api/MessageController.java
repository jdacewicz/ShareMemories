package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.MessageService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.*;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageController {

    private final MessageService messageService;
    private final SecurityUserDetailsService userDetailsService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(@PathVariable long id) {
        Optional<Message> message = messageService.getMessage(id);
        if (message.isEmpty()) {
            throw new NotFoundException("Message not found.");
        }
        return ResponseEntity.ok(message.get());
    }

    @GetMapping("/user/{contactId}")
    public ResponseEntity<?> getAllMessagesWithUser(@PathVariable long contactId) {
        Optional<User> contact = userDetailsService.getUserById(contactId);
        if (contact.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        User loggedUser = getLoggedUser(userDetailsService);
        List<Message> messages = messageService.getAllMessagesBySenderAndReceiver(loggedUser, contact.get());
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/notify")
    public ResponseEntity<?> getAllNotificationsCount() {
        User loggedUser = getLoggedUser(userDetailsService);
        Map<Long, Long> notifyCounts = messageService.getAllNotificationsCount(loggedUser);
        if (notifyCounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifyCounts);
    }

    @GetMapping("/notify/unknown")
    public ResponseEntity<?> getAllNotificationsFromUnknownSenderCount() {
        User loggedUser = getLoggedUser(userDetailsService);
        Map<Long, Long> notifyCounts = messageService.getUnknownSenderNotificationsCount(loggedUser);
        if (notifyCounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifyCounts);
    }

    @PostMapping("/user/{contactId}")
    public ResponseEntity<?> sendMessageToUser(@PathVariable long contactId,
                                               @RequestPart String content,
                                               @RequestPart(value = "image", required = false) MultipartFile file) throws IOException {
        User loggedUser = getLoggedUser(userDetailsService);
        Optional<User> contact = userDetailsService.getUserById(contactId);
        if(contact.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Message message = new Message(loggedUser, contact.get(), content);
        if(!file.isEmpty() && file.getOriginalFilename() != null) {
            String image = FileUtils.generateUniqueName(file.getOriginalFilename());
            message.setImage(image);

            FileUtils.saveFile(Message.IMAGES_DIRECTORY_PATH, image, file);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(message));
    }

    @PutMapping("/user/{contactId}/mark-seen")
    public ResponseEntity<?> setMessagesSeen(@PathVariable long contactId) {
        Optional<User> contact = userDetailsService.getUserById(contactId);
        User loggedUser = getLoggedUser(userDetailsService);
        if (contact.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        messageService.setMessagesSeen(contact.get(), loggedUser);
        return ResponseEntity.ok().build();
    }
}
