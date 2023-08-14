package br.com.pb.compass.challenge3.service;

import br.com.pb.compass.challenge3.dto.PostDto;

import java.util.List;


public interface PostService {
        void processPost(Long postId);
        PostDto reprocessPost(Long postId);

        PostDto getPostWithHistory(Long postId);
        void togglePostStatus(Long postId);

        List<PostDto> getAllPosts();
        void processAllPosts();




}

