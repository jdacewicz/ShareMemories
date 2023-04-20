package com.sharememories.sharememories.repository;

import com.sharememories.sharememories.domain.PostGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostGroupRepository extends JpaRepository<PostGroup, Long> {

}
