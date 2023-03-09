package com.sharememories.sharememories.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    Reaction reactionOne = new Reaction(1);
    Reaction reactionTwo = new Reaction(2);

    @Test
    void Given_PostWith2DifferentReactions_When_ReactionsAreCounted_Then_ReferenceByReactionIdReturnsProperCount() {
        Post post = new Post(List.of(reactionOne, reactionTwo));

        Map<Integer, Long> counts = post.getReactionsCounts();

        assertEquals(1, counts.get(1));
    }

    @Test
    void Given_PostWith3ReactionsWhere2AreTheSame_When_ReactionsAreCounted_Then_ReferenceByReactionIdReturnsProperCount() {
        Post post = new Post(List.of(reactionOne, reactionOne,reactionTwo));

        Map<Integer, Long> counts = post.getReactionsCounts();

        assertEquals(2, counts.get(1));
    }

    @Test
    void Given_PostWithNoReaction_When_ReactionsAreCounted_Then_ReferenceByReactionIdReturnsNull() {
        Post post = new Post();

        Map<Integer, Long> counts = post.getReactionsCounts();

        assertNull(counts.get(1));
    }
}