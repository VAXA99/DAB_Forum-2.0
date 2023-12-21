package com.dbp_forum.controller;

import com.dbp_forum.controller.request.CommentRequest;
import com.dbp_forum.controller.request.FeedRequest;
import com.dbp_forum.controller.request.PostRequest;
import com.dbp_forum.dto.CommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.dto.UserProfileDto;
import com.dbp_forum.model.Tag;
import com.dbp_forum.service.forum.ForumService;
import com.dbp_forum.service.userProfile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService forumService;

    private final UserProfileService userProfileService;

    @Autowired
    public ForumController(ForumService forumService,
                           UserProfileService userProfileService) {
        this.forumService = forumService;
        this.userProfileService = userProfileService;
    }

    @PostMapping("/feed")
    public ResponseEntity<?> getFeed(@RequestBody FeedRequest feedRequest) {
        try {
            List<PostDto> feed = forumService.getFeed(
                    feedRequest.getFeedType(),
                    feedRequest.getTagNames());
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the feed.");

        }
    }

    @GetMapping("/allAvailableTags")
    public ResponseEntity<List<Tag>> getAllAvailableTags() {
        try {
            List<Tag> tags = forumService.getAllAvailableTags();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
        try {
            PostDto post = forumService.getPostWithComments(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/longestCommentChain")
    public CommentDto getLongestCommentChain() {
        return forumService.getLongestCommentChain();
    }

    @GetMapping("/averageCommentChainLength")
    public Double getAverageCommentChainLength() {
        return forumService.getAverageCommentChainLength();
    }

    @GetMapping("/mostQuotedComment")
    public CommentDto getMostQuotedComment() {
        return forumService.getMostQuotedComment();
    }

    @PostMapping("/like/post/{postId}")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestParam Long userId, @RequestParam Boolean isLike) {
        try {
            forumService.likePost(postId, userId, isLike);
            return ResponseEntity.ok("Post liked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error liking post");
        }
    }

    @PostMapping("/comment/post/{postId}")
    public ResponseEntity<?> commentPost(@PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
        try {
            Long commentId = forumService.commentPost(postId,
                    commentRequest.getUserId(),
                    commentRequest.getContent()
            );
            return ResponseEntity.ok(commentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding comment");
        }
    }

    @PostMapping("/reply/comment/{commentId}")
    public ResponseEntity<String> replyToComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        try {
            forumService.replyToComment(commentId,
                    commentRequest.getUserId(),
                    commentRequest.getContent()
            );
            return ResponseEntity.ok("Reply added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding reply");
        }
    }

    @PatchMapping("/edit/comment/{commentId}")
    public ResponseEntity<String> editComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        try {
            forumService.editComment(commentId,
                    commentRequest.getContent());
            return ResponseEntity.ok("Comment edited successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing comment");
        }
    }

    @DeleteMapping("/delete/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        try {
            forumService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting comment");
        }
    }

    @PostMapping("/write")
    public ResponseEntity<String> writePost(@RequestBody PostRequest postRequest) {
        try {
            forumService.writePost(postRequest.getUserId(),
                    postRequest.getTitle(),
                    postRequest.getContent(),
                    postRequest.getTagNames());
            return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating post");
        }
    }

    @PatchMapping("/edit/post/{postId}")
    public ResponseEntity<String> editPost(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
        try {
            forumService.editPost(postId,
                    postRequest.getTitle(),
                    postRequest.getContent(),
                    postRequest.getTagNames());
            return ResponseEntity.ok("Post edited successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error editing post");
        }
    }

    @DeleteMapping("/delete/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        try {
            forumService.deletePost(postId);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post");
        }
    }

    @GetMapping("/userProfile/{userId}")
    public UserProfileDto getUserProfile(@PathVariable Long userId) {
        return userProfileService.getUserProfile(userId);
    }
}
