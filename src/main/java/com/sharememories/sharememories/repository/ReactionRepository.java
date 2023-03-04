package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

}
