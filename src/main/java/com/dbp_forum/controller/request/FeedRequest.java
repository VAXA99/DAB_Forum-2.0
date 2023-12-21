package com.dbp_forum.controller.request;

import lombok.Data;

import java.util.List;

@Data
public class FeedRequest {
    String feedType;
    List<String> tagNames;
}
