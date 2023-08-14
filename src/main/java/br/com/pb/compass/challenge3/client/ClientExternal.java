package br.com.pb.compass.challenge3.client;

import br.com.pb.compass.challenge3.dto.CommentDto;
import br.com.pb.compass.challenge3.dto.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ClientExternal {

    private final RestTemplate restTemplate;

    @Autowired
    public ClientExternal(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
    }

    public PostDto getExternalPosts(){
        String url = "https://jsonplaceholder.typicode.com/posts";
        return restTemplate.getForObject(url, PostDto.class);
    }


    public PostDto getExternalPost(Long id) {
        String url = "https://jsonplaceholder.typicode.com/posts/{postId}";
        return restTemplate.getForObject(url, PostDto.class, id);
    }

    public List<CommentDto> getExternalCommentsForPost(Long postId){
        String url = "https://jsonplaceholder.typicode.com/posts/{postId}/comments";
        CommentDto[] comments = restTemplate.getForObject(url, CommentDto[].class, postId);
        return Arrays.asList(comments);
    }
}
