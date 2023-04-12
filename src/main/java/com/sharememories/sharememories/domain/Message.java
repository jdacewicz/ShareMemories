package com.sharememories.sharememories.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {

    @Transient
    public static final String IMAGES_DIRECTORY_PATH = "uploads/pictures/messages";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "messageId")
    private long id;
    @OneToOne
    private User sender;
    @OneToOne
    private User receiver;
    private String content;
    private String image;

    public Message() {
    }

    public Message(User sender, User receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public Message(User sender, User receiver, String content, String image) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.image = image;
    }

    public String getImagePath() {
        if (image == null) return null;

        return "/" + IMAGES_DIRECTORY_PATH + "/" + image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
