//package br.com.pb.compass.challenge3.service;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JmsConsumer {
//
//    private final PostService postService;
//
//    public JmsConsumer(PostService postService) {
//        this.postService = postService;
//    }
//
//    @JmsListener(destination = "jms-process-mq")
//    public void receiveMessage(String message) {
//        System.out.println("Received message: " + message);
//
//        try {
//            Long postId = Long.parseLong(message); // Assuming message contains a post ID
//            postService.processPost(postId); // Process the post using your service method
//        } catch (NumberFormatException e) {
//            System.err.println("Invalid message format: " + message);
//        }
//    }
//}
