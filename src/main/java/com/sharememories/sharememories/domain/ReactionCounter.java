package com.sharememories.sharememories.domain;

import jakarta.persistence.*;

@Entity
public class ReactionCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reactionCounterId")
    private long id;
    @OneToOne
    private Reaction reaction;
    private long count = 0;

    public ReactionCounter() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
