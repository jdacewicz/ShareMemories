package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharememories.sharememories.util.TimeUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@Getter @Setter
public class Comment {

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public static final String IMAGES_DIRECTORY_PATH = "uploads/pictures/comments";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "commentId")
    private long id;
    @NotBlank
    @Size(max = 255)
    private String content;
    private String image;
    private LocalTime creationTime = LocalTime.now();
    private LocalDate creationDate = LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;
    @ManyToMany
    @JoinTable(
            name = "reactions_comments",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "reaction_id")
    )
    @OrderBy("id ASC")
    @JsonIgnore
    private List<Reaction> reactions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    public Comment(String content, User creator) {
        this.content = content;
        this.creator = creator;
    }

    public Comment(String content, String image, User creator) {
        this.content = content;
        this.image = image;
        this.creator = creator;
    }

    public void addReaction(Reaction reaction) {
        this.reactions.add(reaction);
        reaction.getComments().add(this);
    }

    public void removeReaction(Reaction reaction) {
        this.reactions.remove(reaction);
        reaction.getComments().remove(this);
    }

    public String getElapsedCreationTimeMessage() {
        LocalDateTime creationDateTime = LocalDateTime.of(creationDate, creationTime);

        return TimeUtils.getElapsedTimeMessage(creationDateTime, LocalDateTime.now());
    }

    public Map<Integer, Long> getReactionsCounts() {
        return reactions.stream()
                .collect(Collectors.groupingBy(Reaction::getId, Collectors.counting()));
    }

    public String getImagePath() {
        if (image == null) return null;

        return "/" + IMAGES_DIRECTORY_PATH + "/" + image;
    }
}
