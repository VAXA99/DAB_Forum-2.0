package com.dbp_forum.service.forum;

import com.dbp_forum.dto.CommentDto;
import com.dbp_forum.dto.PostDto;
import com.dbp_forum.dto.UserDto;
import com.dbp_forum.model.*;
import com.dbp_forum.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements ForumService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ForumServiceImpl(PostRepository postRepository,
                            CommentRepository commentRepository,
                            UserRepository userRepository,
                            LikeRepository likeRepository,
                            TagRepository tagRepository,
                            ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
    }

    public enum FeedType {
        NEWEST_TO_OLDEST,
        MOST_POPULAR_TO_LEAST,
        BY_TAGS
    }


    public List<PostDto> getFeed(String strFeedType, List<String> tagNames) {
        FeedType feedType = FeedType.valueOf(strFeedType);
        List<Post> posts = switch (feedType) {
            case NEWEST_TO_OLDEST -> postRepository.getPostsFromNewestToOldest();
            case MOST_POPULAR_TO_LEAST -> postRepository.getPostsFromMostPopularToLeast();
            case BY_TAGS -> {
                List<Tag> tags = tagNames.stream()
                        .map(tagName -> tagRepository.findByDescription(tagName)
                                .orElseThrow(() -> new EntityNotFoundException("Tag not found with name: " + tagName)))
                        .collect(Collectors.toList());
                yield postRepository.findAllByTags(tags);
            }
            default -> throw new IllegalArgumentException("Unsupported feed type: " + feedType);
        };

        return getPostDtos(posts);
    }

    @Override
    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        return convertToPostDTO(post);
    }

    @Override
    public List<Tag> getAllAvailableTags() {
        return tagRepository.findAll();
    }

    @Override
    public PostDto getPostWithComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        List<Comment> comments = commentRepository.findCommentByPostIdAndParentCommentIsNull(postId);
        PostDto postDto = convertToPostDTO(post);
        List<CommentDto> commentDtos = getCommentDtos(comments);
        postDto.setComments(commentDtos);

        return postDto;
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        return convertToCommentDTO(comment, false);
    }

    @Override
    public CommentDto getLongestCommentChain() {
        return convertToCommentDTO(commentRepository.getLongestCommentChain().get(0), true);
    }

    @Override
    public Double getAverageCommentChainLength() {
        return commentRepository.getAverageCommentChainLength();
    }

    @Override
    public CommentDto getMostQuotedComment() {
        return convertToCommentDTO(commentRepository.getMostQuotedComment(), true);
    }

    @Override
    @Async
    @Transactional
    public void likePost(Long postId, Long userId, Boolean isLike) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));

        Like existingLike = likeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike != null) {
            if (existingLike.getIsLike().equals(isLike)) {
                likeRepository.delete(existingLike);
            } else {
                existingLike.setIsLike(isLike);
            }
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            like.setIsLike(isLike);

            likeRepository.save(like);
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void commentPost(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setIsEdited(false);
        comment.setContent(content);

        commentRepository.save(comment);

    }

    @Override
    @Async
    @Transactional
    public void replyToComment(Long parentCommentId, Long userId, String content) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent comment with ID " + parentCommentId + " not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));

        Comment reply = new Comment();
        reply.setParentComment(parentComment);
        reply.setUser(user);
        reply.setPost(parentComment.getPost());
        reply.setIsEdited(false);
        reply.setContent(content);
        commentRepository.save(reply);
    }

    @Override
    @Async
    @Transactional
    public void editComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + commentId + " not found"));
        comment.setContent(newContent);
        comment.setIsEdited(true);
        commentRepository.save(comment);
    }

    @Override
    @Async
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public Long writePost(Long userId, String title, String content, List<String> tagNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID:" + userId));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUser(user);
        post.setIsEdited(false);

        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByDescription(tagName).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setDescription(tagName);
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }

        post.setTags(tags);

        postRepository.save(post);

        return post.getId();
    }

    @Override
    @Async
    @Transactional
    public void editPost(Long postId, String newTitle, String newContent, List<String> tagNames) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        post.setTitle(newTitle);
        post.setContent(newContent);
        post.setIsEdited(true);

        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByDescription(tagName).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setDescription(tagName);
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }

        post.setTags(tags);

        postRepository.save(post);
    }

    @Override
    @Async
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        post.getTags().clear();

        postRepository.deleteById(postId);
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

    private CommentDto convertToCommentDTO(Comment comment, Boolean includeReplies) {
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        if (comment.getUser() != null) {
            UserDto userDto = modelMapper.map(comment.getUser(), UserDto.class);
            commentDto.setUser(userDto);
        }

        if (includeReplies) {
            List<CommentDto> replyDtos = comment.getReplies().stream()
                    .map(reply -> convertToCommentDTO(reply, true))
                    .collect(Collectors.toList());
            commentDto.setReplies(replyDtos);
        }

        return commentDto;
    }

    private List<PostDto> getPostDtos(List<Post> posts) {
        List<PostDto> postDtos = posts.stream()
                .map(this::convertToPostDTO)
                .collect(Collectors.toList());

        return postDtos;
    }

    private List<CommentDto> getCommentDtos(List<Comment> comments) {
        return comments.stream()
                .map(comment -> convertToCommentDTO(comment, true))
                .collect(Collectors.toList());
    }
}
