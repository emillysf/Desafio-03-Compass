package br.com.pb.compass.challenge3.repository;

import br.com.pb.compass.challenge3.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
