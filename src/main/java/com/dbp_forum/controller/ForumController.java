package com.dbp_forum.controller;

import com.dbp_forum.dto.CommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.model.User;
import com.dbp_forum.service.forum.ForumService;
import com.dbp_forum.service.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.DocFlavor;
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
                          @RequestParam(value = "tags", required = false)List<String> tags,
                          Model model) {
        List<PostDto> feed = forumService.getFeed(feedType, tags);
        model.addAttribute("posts", feed);

        return "feed";
    }

    @GetMapping("/post/{postId}")
    public String getPostWithComments(@PathVariable Long postId, Model model) {
        PostDto post = forumService.getPostWithComments(postId);
        model.addAttribute("post", post);

        return "post";
    }

    @GetMapping("/post/like")
    public String likePost(@RequestParam Long postId, @RequestParam Boolean isLike) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userDetailsService.findUserIdByUsername(username);

        forumService.likePost(postId, userId, isLike);

        return "redirect:/forum?feedType=NEWEST_TO_OLDEST";
    }

    @GetMapping("/post/comment")
    public String commentPost(@RequestParam Long postId, @RequestParam String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Long userId = userDetailsService.findUserIdByUsername(username);

        forumService.commentPost(postId, userId, content);

        return "redirect:/forum/post/" + postId;
    }

    public String getStatistics(Model model) {
        CommentDto longestCommentChain = forumService.getLongestCommentChain();
        CommentDto mostQuotedComment =  forumService.getMostQuotedComment();
        Double averageCommentChainLength =  forumService.getAverageCommentChainLength();

        model.addAttribute("longestCommentChain", longestCommentChain);
        model.addAttribute("mostQuotedComment", mostQuotedComment);
        model.addAttribute("averageCommentChainLength", averageCommentChainLength);

        return "statistics";
    }


}
