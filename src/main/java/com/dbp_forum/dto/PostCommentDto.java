package com.dbp_forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostCommentDto {
    private PostDto post;
    private Long commentCount;
}
