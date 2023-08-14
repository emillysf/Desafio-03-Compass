package br.com.pb.compass.challenge3.service.impl;

import br.com.pb.compass.challenge3.dto.CommentDto;
import br.com.pb.compass.challenge3.dto.HistoryDto;
import br.com.pb.compass.challenge3.dto.PostDto;
import br.com.pb.compass.challenge3.client.ClientExternal;
import br.com.pb.compass.challenge3.entity.Comment;
import br.com.pb.compass.challenge3.entity.Enum;
import br.com.pb.compass.challenge3.entity.History;
import br.com.pb.compass.challenge3.entity.Post;
import br.com.pb.compass.challenge3.repository.PostRepository;
import br.com.pb.compass.challenge3.service.MessageSenderService;
import br.com.pb.compass.challenge3.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    private final ClientExternal clientExternal;
    private final PostRepository repository;
    private final MessageSenderService message;
    private final HistoryServiceImpl historyServiceImpl;
    private ModelMapper mapper;

    @Autowired
    public PostServiceImpl(ClientExternal clientExternal, PostRepository repository, ModelMapper mapper, HistoryServiceImpl historyServiceImpl, MessageSenderService message) {
        this.clientExternal = clientExternal;
        this.repository = repository;
        this.mapper = mapper;
        this.historyServiceImpl = historyServiceImpl;
        this.message = message;
    }

    @Override
    public PostDto processPost(Long postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        Post post;

        if (optionalPost.isPresent()) {
            post = optionalPost.get();
        } else {
            post = new Post();
            post.setId(postId);
            message.sendPostProcessedMessage(postId);
            historyServiceImpl.updateHistory(post, Enum.PostState.CREATED);
        }

        historyServiceImpl.updateHistory(post, Enum.PostState.POST_FIND);

        PostDto postDto = clientExternal.getExternalPost(postId);

        if (postDto == null) {
            return convertToDTO(post);
        }

        post.setTitle(postDto.getTitle());
        post.setBody(postDto.getBody());
        historyServiceImpl.updateHistory(post, Enum.PostState.POST_OK);

        List<CommentDto> commentsDto = clientExternal.getExternalCommentsForPost(postId);
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

        List<Comment> postComments = post.getComments();
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : postComments) {
            CommentDto commentDto = new CommentDto();
            commentDto.setId(comment.getId());
            commentDto.setBody(comment.getBody());
            commentDtos.add(commentDto);
        }
        postDto.setComments(commentDtos);


        List<History> postHistory = post.getHistory();
        List<HistoryDto> historyDtos = new ArrayList<>();
        for (History history : postHistory) {
            historyDtos.add(historyServiceImpl.convertToDto(history));
        }
        postDto.setHistory(historyDtos);

        repository.save(post);
        historyServiceImpl.updateHistory(post, Enum.PostState.ENABLED);
        historyServiceImpl.updateHistory(post, Enum.PostState.DISABLED);
        historyServiceImpl.updateHistory(post, Enum.PostState.UPDATING);

        return postDto;
    }



    private PostDto convertToDTO(Post post) {
        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
    }

    private Post mapToEntity(PostDto postDto){
        Post post = mapper.map(postDto, Post.class);
        return post;
    }

}
