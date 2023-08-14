package br.com.pb.compass.challenge3.service.impl;

import br.com.pb.compass.challenge3.dto.CommentDto;
import br.com.pb.compass.challenge3.dto.HistoryDto;
import br.com.pb.compass.challenge3.dto.PostDto;
import br.com.pb.compass.challenge3.client.ClientExternal;
import br.com.pb.compass.challenge3.entity.Comment;
import br.com.pb.compass.challenge3.entity.Enum;
import br.com.pb.compass.challenge3.entity.History;
import br.com.pb.compass.challenge3.entity.Post;
import br.com.pb.compass.challenge3.exceptions.PostNotFoundException;
import br.com.pb.compass.challenge3.repository.PostRepository;
import br.com.pb.compass.challenge3.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final ClientExternal clientExternal;
    private final PostRepository repository;
    private final HistoryServiceImpl historyServiceImpl;
    private final ModelMapper mapper;
//    private final JmsTemplate jmsTemplate;
//    private final String jmsQueue;

    @Autowired
    public PostServiceImpl(ClientExternal clientExternal, PostRepository repository, ModelMapper mapper,
                           HistoryServiceImpl historyServiceImpl) {
        this.clientExternal = clientExternal;
        this.repository = repository;
        this.mapper = mapper;
        this.historyServiceImpl = historyServiceImpl;
//        this.jmsTemplate = jmsTemplate;
//        this.jmsQueue = jmsQueue;
    }

    @Async("asyncExecutor")
    @Override
    public void processPost(Long postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        Post post = optionalPost.orElseGet(() -> createNewPost(postId));

        postExternalData(postId, post);
        getComments(post);
        PostDto postDto = convertToDto(post);
        updatePostHistoryAndSave(post, postDto);
//        jmsTemplate.convertAndSend(jmsQueue, postId.toString());
    }

    private Post createNewPost(Long postId) {
        Post post = new Post();
        post.setId(postId);
        historyServiceImpl.updateHistory(post, Enum.PostState.CREATED);
        return post;
    }

    private void postExternalData(Long postId, Post post) {
        historyServiceImpl.updateHistory(post, Enum.PostState.POST_FIND);
        PostDto postDto = clientExternal.getExternalPost(postId);

        if (postDto != null) {
            post.setTitle(postDto.getTitle());
            post.setBody(postDto.getBody());
            historyServiceImpl.updateHistory(post, Enum.PostState.POST_OK);
        }
    }

    private void getComments(Post post) {
        List<CommentDto> commentsDto = clientExternal.getExternalCommentsForPost(post.getId());

        if (commentsDto != null && !commentsDto.isEmpty()) {
            historyServiceImpl.updateHistory(post, Enum.PostState.COMMENTS_FIND);

            List<Comment> comments = new ArrayList<>();
            for (CommentDto commentDto : commentsDto) {
                Comment comment = new Comment();
                comment.setId(commentDto.getId());
                comment.setBody(commentDto.getBody());
                comment.setPost(post);
                comments.add(comment);
            }

            post.setComments(comments);
            historyServiceImpl.updateHistory(post, Enum.PostState.COMMENTS_OK);
        }
    }

    private PostDto convertToDto(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);
        List<CommentDto> commentDtos = post.getComments().stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList());
        postDto.setComments(commentDtos);
        return postDto;
    }

    private CommentDto convertCommentToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setBody(comment.getBody());
        return commentDto;
    }

    private void updatePostHistoryAndSave(Post post, PostDto postDto) {
        List<History> postHistory = post.getHistory();
        List<HistoryDto> historyDtos = postHistory.stream()
                .map(historyServiceImpl::convertToDto)
                .collect(Collectors.toList());
        postDto.setHistory(historyDtos);

        repository.save(post);
        historyServiceImpl.updateHistory(post, Enum.PostState.ENABLED);
    }


    @Override
    public PostDto reprocessPost(Long postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            historyServiceImpl.updateHistory(post, Enum.PostState.UPDATING);
            postExternalData(postId, post);
            getComments(post);

            updatePostFieldsWithExternalData(postId, post);

            PostDto postDto = convertToDto(post);
            repository.save(post);

            historyServiceImpl.updateHistory(post, Enum.PostState.ENABLED);

            return postDto;
        } else {
            try {
                throw new PostNotFoundException("Post with ID " + postId + " not found");
            } catch (PostNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updatePostFieldsWithExternalData(Long postId, Post post) {
        PostDto postDto = clientExternal.getExternalPost(postId);

        if (postDto != null) {
            post.setTitle(postDto.getTitle());
            post.setBody(postDto.getBody());
        }
        List<History> postHistory = post.getHistory();
        List<HistoryDto> historyDtos = postHistory.stream()
                .map(historyServiceImpl::convertToDto)
                .collect(Collectors.toList());
        postDto.setHistory(historyDtos);
    }

    @Override
    public void togglePostStatus(Long postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Enum.PostState currentState = determineCurrentState(post);

            if (currentState == Enum.PostState.DISABLED) {
                historyServiceImpl.updateHistory(post, Enum.PostState.ENABLED);
            } else {
                historyServiceImpl.updateHistory(post, Enum.PostState.DISABLED);
            }

            repository.save(post);
        } else {
            try {
                throw new PostNotFoundException("Post with ID " + postId + " not found");
            } catch (PostNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Enum.PostState determineCurrentState(Post post) {
        List<History> history = post.getHistory();
        if (history != null && !history.isEmpty()) {
            Enum.PostState lastState = history.get(history.size() - 1).getState();
            if (lastState == Enum.PostState.DISABLED) {
                return Enum.PostState.DISABLED;
            }
        }
        return Enum.PostState.ENABLED;
    }

    @Override
    public PostDto getPostWithHistory(Long postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            PostDto postDto = convertToDto(post);
            return postDto;
        } else {
            try {
                throw new PostNotFoundException("Post with ID " + postId + " not found");
            } catch (PostNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = repository.findAll();
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void processAllPosts() {
        List<Post> posts = repository.findAll();
        for (Post post : posts) {
            processPost(post.getId());
        }
    }



}
