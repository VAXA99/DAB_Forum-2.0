package com.dbp_forum.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private UserDto userDto;
    private Boolean isEdited;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentDto> replies;
}
