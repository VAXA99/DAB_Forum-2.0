package com.dbp_forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Tuple;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private Long userId;
    private String username;
    private PostDto mostPopularPost;
    private List<PostCommentDto> postComments;
    private long totalPosts;
    private long totalComments;
    private long totalCommentsOnUserPosts;
    private long totalRepliesOnComments;
    private long totalLikes;
}
