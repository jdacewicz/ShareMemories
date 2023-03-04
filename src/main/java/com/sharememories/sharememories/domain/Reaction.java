package com.sharememories.sharememories.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reactionId")
    private int id;
    private String name;
    private String image;

    public Reaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
