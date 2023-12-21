package com.dbp_forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private Long userId;
    private String username;
    private String information;
    private PostDto postDto;
    private List<PostCommentDto> postCommentDto;
    private long totalPosts;
    private long totalComments;
    private long totalLikes;
}
