package com.dbp_forum.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    private Long userId;
    private String title;
    private String content;
    private List<String> tagNames;
}

