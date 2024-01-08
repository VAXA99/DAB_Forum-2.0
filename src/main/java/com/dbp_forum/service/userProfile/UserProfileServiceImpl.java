package com.dbp_forum.service.userProfile;

import com.dbp_forum.dto.PostCommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.dto.UserDto;
import com.dbp_forum.dto.UserProfileDto;
import com.dbp_forum.model.Post;
import com.dbp_forum.model.User;
import com.dbp_forum.repository.PostRepository;
import com.dbp_forum.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserProfileServiceImpl(PostRepository postRepository,
                                  UserRepository userRepository,
                                  ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    private String getUserUsername(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));
        return user.getUsername();
    }


    private String getUserInformation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));
        return user.getInformation();
    }

    private PostDto getMostPopularPostByUserId(Long userId) {
        return convertToPostDTO(postRepository.getMostPopularPostByUserId(userId).get(0));
    }

    private List<PostCommentDto> getPostsByUserIdWithCommentCount(Long userId) {
        return postRepository.getPostsByUserIdWithCommentCount(userId);
    }

    private long getUserPostCount(Long userId) {
        return postRepository.getUserPostCount(userId);
    }

    private long getTotalLikesOnUserPosts(Long userId) {
        return postRepository.getTotalLikesOnUserPosts(userId);
    }

    private long getTotalCommentsOnUserPosts(Long userId) {
        return postRepository.getTotalCommentsOnUserPosts(userId);
    }

    @Override
    public UserProfileDto getUserProfile(Long userId) {

        String username = getUserUsername(userId);
        String information = getUserInformation(userId);
        PostDto postDto = getMostPopularPostByUserId(userId);
        List<PostCommentDto> postCommentDtos = getPostsByUserIdWithCommentCount(userId);
        long totalPosts = getUserPostCount(userId);
        long totalComments = getTotalCommentsOnUserPosts(userId);
        long totalLikes = getTotalLikesOnUserPosts(userId);


        return new UserProfileDto(
                userId,
                username,
                information,
                postDto,
                postCommentDtos,
                totalPosts,
                totalComments,
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
            postDto.setUserDto(userDto);
        }
        postDto.setLikeCount(postRepository.getLikeCount(post));
        postDto.setDislikeCount(postRepository.getDislikeCount(post));
        postDto.setCommentCount(postRepository.getCommentCount(post));

        return postDto;
    }
}
