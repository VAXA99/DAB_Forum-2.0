package com.dbp_forum.controller.request;

import lombok.Data;

@Data
public class CommentRequest {
    private Long userId;
    private String content;
}
