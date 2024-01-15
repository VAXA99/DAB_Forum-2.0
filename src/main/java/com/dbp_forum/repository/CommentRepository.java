package com.dbp_forum.repository;

import com.dbp_forum.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentByPostIdAndParentCommentIsNull(Long postId);

    @Query("SELECT c FROM Comment c " +
            "ORDER BY SIZE(c.replies) DESC ")
    List<Comment> getLongestCommentChain();

    @Query(value =
            "WITH RECURSIVE CommentHierarchy AS (" +
                    "    SELECT id, parent_comment_id, 1 AS chain_length " +
                    "    FROM Comment " +
                    "    WHERE parent_comment_id IS NULL " +
                    "    UNION ALL " +
                    "    SELECT c.id, c.parent_comment_id, ch.chain_length + 1 " +
                    "    FROM Comment c " +
                    "    JOIN CommentHierarchy ch ON c.parent_comment_id = ch.id" +
                    ") " +
                    "SELECT AVG(chain_length) " +
                    "FROM CommentHierarchy", nativeQuery = true)
    Double getAverageCommentChainLength();

    @Query(value =
            "SELECT c " +
                    "FROM Comment c " +
                    "WHERE SIZE(c.replies) = (" +
                    "    SELECT MAX(SIZE(cr.replies)) " +
                    "    FROM Comment cr" +
                    ") " +
                    "AND c.id = (" +
                    "    SELECT MIN(cc.id) " +
                    "    FROM Comment cc " +
                    "    WHERE SIZE(cc.replies) = (" +
                    "        SELECT MAX(SIZE(crr.replies)) " +
                    "        FROM Comment crr" +
                    "    )" +
                    ")"
    )
    Comment getMostQuotedComment();

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId ")
    Long getTotalComments(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId AND c.parentComment IS NOT NULL")
    Long getTotalRepliesOnUserComments(@Param("userId")Long userId);
}
