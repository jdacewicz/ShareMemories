package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.MessageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MessageGroupRepository extends JpaRepository<MessageGroup, Long> {

    @Query("FROM MessageGroup m ORDER BY random() LIMIT 10")
    Set<MessageGroup> getRandomGroups();
}
