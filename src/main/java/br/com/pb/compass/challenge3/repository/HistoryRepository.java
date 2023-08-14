package br.com.pb.compass.challenge3.repository;

import br.com.pb.compass.challenge3.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
