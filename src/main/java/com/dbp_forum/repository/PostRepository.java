package com.dbp_forum.repository;

import com.dbp_forum.dto.PostCommentDto;
import com.dbp_forum.model.Comment;
import com.dbp_forum.model.Post;
import com.dbp_forum.model.Tag;
import com.dbp_forum.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p " +
            "ORDER BY p.createdAt DESC ")
    List<Post> getPostsFromNewestToOldest();

    @Query("SELECT p FROM Post p " +
            "ORDER BY (SIZE(p.likes)) DESC")
    List<Post> getPostsFromMostPopularToLeast();

    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.tags t WHERE t IN :tags")
    List<Post> findAllByTags(@Param("tags") List<Tag> tags);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post = :post AND l.isLike = true")
    long getLikeCount(@Param("post") Post post);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post = :post AND l.isLike = false")
    long getDislikeCount(@Param("post") Post post);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
    long getCommentCount(@Param("post") Post post);

    @Query(value = "SELECT p " +
            "FROM Post p " +
            "WHERE p.user.id = :userId " +
            "AND SIZE(p.likes) + SIZE(p.comments) = (" +
            "SELECT MAX(SIZE(post.likes) + SIZE(post.comments)) " +
            "FROM Post post " +
            "WHERE post.user.id = :userId )"
    )
    List<Post> getMostPopularPostByUserId(@Param("userId") Long userId);

    @Query("SELECT p, COUNT(c.id) " +
            "FROM Post p " +
            "JOIN Comment c ON c.post.id = p.id " +
            "JOIN User u ON c.user.id = u.id " +
            "WHERE u.id = :userId AND p.user.id <> :userId " +
            "GROUP BY p " +
            "ORDER BY COUNT(c.id) DESC ")
    List<Tuple> getPostsByUserIdWithCommentCount(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long getUserPostCount(@Param("userId") Long userId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.user.id = :userId AND l.isLike = true")
    long getTotalLikesOnUserPosts(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.user.id = :userId")
    long getTotalCommentsOnUserPosts(@Param("userId") Long userId);
}
