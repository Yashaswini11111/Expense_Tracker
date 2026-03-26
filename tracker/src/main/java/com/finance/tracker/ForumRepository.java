package com.finance.tracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ForumRepository extends JpaRepository<ForumPost, Long> {

   
    List<ForumPost> findAllByOrderByCreatedAtDesc();

    List<ForumPost> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ForumPost> findByContentContainingIgnoreCase(String keyword);

    @Query("SELECT f FROM ForumPost f ORDER BY f.likes DESC")
    List<ForumPost> findTrendingTips();
}