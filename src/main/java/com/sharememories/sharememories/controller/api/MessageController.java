package com.sharememories.sharememories.controller.api;

import com.sharememories.sharememories.domain.Message;
import com.sharememories.sharememories.domain.User;
import com.sharememories.sharememories.service.MessageService;
import com.sharememories.sharememories.service.SecurityUserDetailsService;
import com.sharememories.sharememories.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sharememories.sharememories.util.UserUtils.getLoggedUser;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Message Controller")
public class MessageController {

    private final MessageService messageService;
    private final SecurityUserDetailsService userDetailsService;


    @Operation(summary = "Get a message by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the message",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Message not found",
                    content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(@PathVariable long id) {
        Optional<Message> message = messageService.getMessage(id);
        if (message.isEmpty()) {
            throw new NotFoundException("Message not found.");
        }
        return ResponseEntity.ok(message.get());
    }

    @Operation(summary = "Get all messages with user by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found messages",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "204", description = "No messages found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
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

    @Operation(summary = "Get notifications counts",
            description = "Get counts of all unread messages by contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found messages",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "204", description = "No messages found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content)})
    @GetMapping("/notify")
    public ResponseEntity<?> getAllNotificationsCount() {
        User loggedUser = getLoggedUser(userDetailsService);
        Map<Long, Long> notifyCounts = messageService.getAllNotificationsCount(loggedUser);
        if (notifyCounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifyCounts);
    }

    @Operation(summary = "Get unknown notifications counts",
            description = "Get counts of all unread messages by users not in contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found messages",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "204", description = "No messages found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content)})
    @GetMapping("/notify/unknown")
    public ResponseEntity<?> getAllNotificationsFromUnknownSenderCount() {
        User loggedUser = getLoggedUser(userDetailsService);
        Map<Long, Long> notifyCounts = messageService.getUnknownSenderNotificationsCount(loggedUser);
        if (notifyCounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifyCounts);
    }

    @Operation(summary = "Send message to user by it's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid data supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "An error occurred while saving image",
                    content = @Content)})
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

    @Operation(summary = "Mark messages as seen by user's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated messages",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
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
