package com.sharememories.sharememories.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reactions")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class Reaction {

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    public static String IMAGES_DIRECTORY_PATH = "uploads/pictures/reactions";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reaction_Id")
    private int id;

    @NotBlank
    @Size(min = 2, max = 34)
    private String name;

    private String image;

    @ManyToMany(mappedBy = "reactions")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(mappedBy = "reactions")
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    public Reaction(int id) {
        this.id = id;
    }

    public Reaction(String name) {
        this.name = name;
    }


    public Reaction(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Reaction(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    @SuppressWarnings("unused")
    public String getImagePath() {
        if (image == null) return null;

        return "/" + IMAGES_DIRECTORY_PATH + "/" + image;
    }
}
