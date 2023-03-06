package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("FROM Post p ORDER BY random() LIMIT 10")
    List<Post> getRandomPosts();
}
