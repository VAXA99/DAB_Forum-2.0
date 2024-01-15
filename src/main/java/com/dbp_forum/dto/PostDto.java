package com.dbp_forum.dto;

import com.dbp_forum.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isEdited;
    private UserDto user;
    private List<Tag> tags;
    private List<CommentDto> comments;
    private long likeCount;
    private long dislikeCount;
    private long commentCount;
}
