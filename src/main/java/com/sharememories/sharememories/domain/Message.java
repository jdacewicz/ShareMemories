package com.sharememories.sharememories.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@Getter @Setter
public class Message {

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public static final String IMAGES_DIRECTORY_PATH = "uploads/pictures/messages";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_Id")
    private long id;

    @OneToOne
    private User sender;

    @OneToOne
    private User receiver;

    @NotBlank
    @Size(max = 255)
    private String content;

    private String image;
    private boolean messageSeen = false;

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

    @SuppressWarnings("unused")
    public String getImagePath() {
        if (image == null) return null;

        return "/" + IMAGES_DIRECTORY_PATH + "/" + image;
    }
}
