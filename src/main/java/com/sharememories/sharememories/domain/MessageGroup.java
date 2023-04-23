package com.sharememories.sharememories.domain;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class MessageGroup extends Group {

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<Message> messages;

    public MessageGroup() {
    }

    public MessageGroup(String name, User owner) {
        super(name, owner);
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }
}
