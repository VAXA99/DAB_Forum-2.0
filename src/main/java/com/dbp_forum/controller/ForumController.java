package com.dbp_forum.controller;

import com.dbp_forum.controller.request.PostRequest;
import com.dbp_forum.dto.CommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.model.Comment;
import com.dbp_forum.model.Tag;
import com.dbp_forum.service.forum.ForumService;
import com.dbp_forum.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumController {

    private final ForumService forumService;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public ForumController(ForumService forumService, UserDetailsServiceImpl userDetailsService) {
        this.forumService = forumService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping
    public String getFeed(@RequestParam(value = "feedType", required = true) String feedType,
                          @RequestParam(value = "tags", required = false) List<String> tags,
                          Model model) {
        List<PostDto> feed = forumService.getFeed(feedType, tags);
        List<Tag> availableTags = forumService.getAllAvailableTags();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("posts", feed);
        model.addAttribute("availableTags", availableTags);
        model.addAttribute("username", username);

        return "feed";
    }

    @GetMapping("/post/{postId}")
    public String getPostWithComments(@PathVariable Long postId, Model model) {
        PostDto post = forumService.getPostWithComments(postId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("post", post);
        model.addAttribute("username", username);

        return "post";
    }

    @GetMapping("/post/like")
    public String likePost(@RequestParam Long postId, @RequestParam Boolean isLike) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userDetailsService.findUserIdByUsername(username);

        forumService.likePost(postId, userId, isLike);

        return "redirect:/forum/post/" + postId;
    }

    @PostMapping("/post/comment")
    public String commentPost(@RequestParam Long postId, @RequestParam String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userDetailsService.findUserIdByUsername(username);

        forumService.commentPost(postId, userId, content);

        return "redirect:/forum/post/" + postId;
    }

    @PostMapping("/post/reply/{parentCommentId}")
    public String replyToComment(@PathVariable Long parentCommentId, @RequestParam Long postId, @RequestParam String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userDetailsService.findUserIdByUsername(username);

        forumService.replyToComment(parentCommentId, userId, content);


        return "redirect:/forum/post/" + postId;
    }

    @GetMapping("/post/comment/edit/{commentId}")
    public String getEditCommentPage(@PathVariable Long commentId, Model model) {
        CommentDto comment = forumService.getCommentById(commentId);
        model.addAttribute("comment", comment);

        return "edit_comment";
    }

    @PostMapping("/{postId}/comment/edit/{commentId}")
    public String editComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              @RequestParam String content) {
        forumService.editComment(commentId, content);

        return "redirect:/forum/post/" + postId;
    }

    @PostMapping("{postId}/comment/delete/{commentId}")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId) {
        forumService.deleteComment(commentId);

        return "redirect:/forum/post/" + postId;
    }

    @GetMapping("/statistics")
    public String getStatistics(Model model) {
        CommentDto longestCommentChain = forumService.getLongestCommentChain();
        CommentDto mostQuotedComment = forumService.getMostQuotedComment();
        Double averageCommentChainLength = forumService.getAverageCommentChainLength();

        model.addAttribute("longestCommentChain", longestCommentChain);
        model.addAttribute("mostQuotedComment", mostQuotedComment);
        model.addAttribute("averageCommentChainLength", averageCommentChainLength);

        return "statistics";
    }

    @GetMapping("/post/write")
    public String getWritePostPage(Model model) {
        List<Tag> availableTags = forumService.getAllAvailableTags();
        model.addAttribute("availableTags", availableTags);

        return "write_post";
    }

    @PostMapping("/post/write")
    public String writePost(@ModelAttribute PostRequest postRequest,
                            @RequestParam(name = "customTags", required = false) String customTags) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userDetailsService.findUserIdByUsername(username);

        List<String> tags = new ArrayList<>();

        if (postRequest.getTagNames() != null) {
            tags.addAll(postRequest.getTagNames());
        }

        if (customTags != null && !customTags.isEmpty()) {
            tags.addAll(Arrays.asList(customTags.split(",\\s*")));
        }

        Long postId = forumService.writePost(
                userId,
                postRequest.getTitle(),
                postRequest.getContent(),
                tags
        );

        return "redirect:/forum/post/" + postId;
    }

    @GetMapping("/post/edit/{postId}")
    public String getEditPostPage(@PathVariable Long postId, Model model) {
        PostDto post = forumService.getPostById(postId);
        List<Tag> availableTags = forumService.getAllAvailableTags();

        model.addAttribute("availableTags", availableTags);
        model.addAttribute("post", post);

        return "edit_post";
    }

    @PostMapping("/post/edit/{postId}")
    public String editPost(@PathVariable Long postId,
                           @ModelAttribute PostRequest postRequest,
                           @RequestParam(name = "customTags", required = false) String customTags) {

        List<String> tags = new ArrayList<>();

        if (postRequest.getTagNames() != null) {
            tags.addAll(postRequest.getTagNames());
        }

        if (customTags != null && !customTags.isEmpty()) {
            tags.addAll(Arrays.asList(customTags.split(",\\s*")));
        }

        forumService.editPost(postId,
                postRequest.getTitle(),
                postRequest.getContent(),
                tags);

        return "redirect:/forum/post/" + postId;
    }


    @PostMapping("/post/delete/{postId}")
    public String deletePost(@PathVariable Long postId) {
        forumService.deletePost(postId);

        return "redirect:/forum?feedType=NEWEST_TO_OLDEST";
    }
}
