package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharememories.sharememories.util.TimeUtils;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "comments")
public class Comment {

    @Transient
    public static final String IMAGES_DIRECTORY_PATH = "uploads/pictures/comments";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "commentId")
    private long id;
    private String content;
    private String image;
    private LocalTime creationTime = LocalTime.now();
    private LocalDate creationDate = LocalDate.now();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "reactions_comments",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "reaction_id")
    )
    @OrderBy("id ASC")
    private List<Reaction> reactions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    public Comment() {
    }

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

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
