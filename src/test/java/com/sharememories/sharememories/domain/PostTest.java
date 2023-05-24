package com.sharememories.sharememories.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    Reaction reactionOne = new Reaction(1);
    Reaction reactionTwo = new Reaction(2);

    @Test
    @DisplayName("Given post with 2 different reactions " +
            "When getting reactions counts " +
            "Then proper map of counts should be returned")
    void getPostWithDifferentReactionsReactionCounts() {
        Post post = new Post(List.of(reactionOne, reactionTwo));

        Map<Integer, Long> counts = post.getReactionsCounts();

        assertEquals(1, counts.get(1));
    }

    @Test
    @DisplayName("Given post with 2 same reactions " +
            "When getting reactions counts " +
            "Then proper map of counts should be returned")
    void getPostWithSameReactionsReactionCounts() {
        Post post = new Post(List.of(reactionOne, reactionOne,reactionTwo));

        Map<Integer, Long> counts = post.getReactionsCounts();

        assertEquals(2, counts.get(1));
    }

    @Test
    @DisplayName("Given post with no reactions " +
            "When getting reactions counts " +
            "Then empty map should be returned")
    void getPostWithNoReactionsReactionCounts() {
        Post post = new Post();

        Map<Integer, Long> counts = post.getReactionsCounts();

        assertNull(counts.get(1));
    }
}