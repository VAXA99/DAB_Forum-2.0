package com.dbp_forum.service.forum;

import com.dbp_forum.dto.CommentDto;
import com.dbp_forum.dto.PostCommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.model.Tag;

import java.util.List;

public interface ForumService {
    List<PostDto> getFeed(String feedType, List<String> tagNames);
    List<Tag> getAllAvailableTags();
    PostDto getPostWithComments(Long postId);
    CommentDto getLongestCommentChain();
    Double getAverageCommentChainLength();
    CommentDto getMostQuotedComment();
    void likePost(Long postId, Long userId, Boolean isLike);
    Long commentPost(Long postId, Long userId, String content);
    void replyToComment(Long parentCommentId, Long userId, String content);
    void editComment(Long commentId, String newContent);
    void deleteComment(Long commentId);
    void writePost(Long userId, String title, String content, List<String> tagNames);
    void editPost(Long postId, String newTitle, String newContent, List<String> tagNames);
    void deletePost(Long postId);
}
