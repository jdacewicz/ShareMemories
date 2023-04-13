package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sharememories.sharememories.util.TimeUtils;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "posts")
public class Post {

    @Transient
    @Value("${post.image.directory}")
    public static String IMAGES_DIRECTORY_PATH;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "postId")
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
            name = "reactions_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "reaction_id")
    )
    @OrderBy("id ASC")
    private List<Reaction> reactions = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public Post(String content, User creator) {
        this.content = content;
        this.creator = creator;
    }

    public Post(String content, String image, User creator) {
        this.content = content;
        this.image = image;
        this.creator = creator;
    }

    public void addReaction(Reaction reaction) {
        this.reactions.add(reaction);
        reaction.getPosts().add(this);
    }

    public Map<Integer, Long> getReactionsCounts() {
        return reactions.stream()
                .collect(Collectors.groupingBy(e -> e.getId(), Collectors.counting()));
    }

    public String getElapsedCreationTimeMessage() {
        LocalDateTime creationDateTime = LocalDateTime.of(creationDate, creationTime);

        return TimeUtils.getElapsedTimeMessage(creationDateTime, LocalDateTime.now());
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

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
