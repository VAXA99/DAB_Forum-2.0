package com.dbp_forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PostCommentDto {
    private String title;
    private String postContent;
    private Long commentCount;
}
