package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "reactions_comments",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "reaction_id")
    )
    @OrderBy("id ASC")
    private List<Reaction> reactions = new ArrayList<>();
    @ManyToMany(mappedBy = "comments")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    public Comment() {
    }

    public void addReaction(Reaction reaction) {
        this.reactions.add(reaction);
        reaction.getComments().add(this);
    }

    public void removeReaction(Reaction reaction) {
        this.reactions.remove(reaction);
        reaction.getComments().remove(this);
    }

    public Map<Integer, Long> getReactionsCounts() {
        return reactions.stream()
                .collect(Collectors.groupingBy(e -> e.getId(), Collectors.counting()));
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

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
