package com.dbp_forum.service.userProfile;

import com.dbp_forum.dto.PostCommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.dto.UserDto;
import com.dbp_forum.dto.UserProfileDto;
import com.dbp_forum.model.Post;
import com.dbp_forum.model.User;
import com.dbp_forum.repository.CommentRepository;
import com.dbp_forum.repository.PostRepository;
import com.dbp_forum.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserProfileServiceImpl(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private String getUserUsername(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));
        return user.getUsername();
    }

    private PostDto getMostPopularPostByUserId(Long userId) {
        if (postRepository.getMostPopularPostByUserId(userId).get(0) == null) {
            return null;
        } else {
            return convertToPostDTO(postRepository.getMostPopularPostByUserId(userId).get(0));
        }
    }

    private List<PostCommentDto> getPostsByUserIdWithCommentCount(Long userId) {
        List<PostCommentDto> postCommentDtos = new ArrayList<>();
        List<Tuple> tuples = postRepository.getPostsByUserIdWithCommentCount(userId);

        for (Tuple tuple : tuples) {
            Post post = tuple.get(0, Post.class);
            Long commentCount = tuple.get(1, Long.class);

            PostDto postDto = convertToPostDTO(post);

            PostCommentDto postCommentDto = new PostCommentDto(postDto, commentCount);
            postCommentDtos.add(postCommentDto);
        }

        return postCommentDtos;
    }


    private long getUserPostCount(Long userId) {
        return postRepository.getUserPostCount(userId);
    }

    private long getTotalLikesOnUserPosts(Long userId) {
        return postRepository.getTotalLikesOnUserPosts(userId);
    }

    private long getTotalComments(Long userId) {
        return commentRepository.getTotalComments(userId);
    }

    private long getTotalCommentsOnUserPosts(Long userId) {
        return postRepository.getTotalCommentsOnUserPosts(userId);
    }

    private long getTotalRepliesOnUserComments(Long userId) {
        return commentRepository.getTotalRepliesOnUserComments(userId);
    }

    @Override
    public UserProfileDto getUserProfile(Long userId) {

        String username = getUserUsername(userId);
        PostDto mostPopularPost = getMostPopularPostByUserId(userId);
        List<PostCommentDto> postCommentDtos = getPostsByUserIdWithCommentCount(userId);
        long totalPosts = getUserPostCount(userId);
        long totalComments = getTotalComments(userId);
        long totalCommentsOnUserPosts = getTotalCommentsOnUserPosts(userId);
        long totalRepliesOnComments = getTotalRepliesOnUserComments(userId);
        long totalLikes = getTotalLikesOnUserPosts(userId);

        return new UserProfileDto(
                userId,
                username,
                mostPopularPost,
                postCommentDtos,
                totalPosts,
                totalComments,
                totalCommentsOnUserPosts,
                totalRepliesOnComments,
                totalLikes);
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        Long userId = userRepository.findUserIdByUsername(username);
        return getUserProfile(userId);
    }

    private PostDto convertToPostDTO(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);

        if (post.getUser() != null) {
            UserDto userDto = modelMapper.map(post.getUser(), UserDto.class);
            postDto.setUser(userDto);
        }
        postDto.setLikeCount(postRepository.getLikeCount(post));
        postDto.setDislikeCount(postRepository.getDislikeCount(post));
        postDto.setCommentCount(postRepository.getCommentCount(post));

        return postDto;
    }
}
