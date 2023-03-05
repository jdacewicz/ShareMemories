package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.ReactionCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionCounterRepository extends JpaRepository<ReactionCounter, Long> {

}
