package com.dbp_forum.repository;

import com.dbp_forum.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByDescription(String description);
    @Query(nativeQuery = true, value = "SELECT t.description " +
            "FROM tag t " +
            "JOIN post_tags pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = :postId")
    List<String> findTagsByPostId(@Param("postId") Long postId);
}
