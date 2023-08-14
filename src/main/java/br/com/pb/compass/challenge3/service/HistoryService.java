package br.com.pb.compass.challenge3.service;

import br.com.pb.compass.challenge3.dto.HistoryDto;
import br.com.pb.compass.challenge3.entity.Enum;
import br.com.pb.compass.challenge3.entity.History;
import br.com.pb.compass.challenge3.entity.Post;

public interface HistoryService {

    void updateHistory(Post post, Enum.PostState newState);
    HistoryDto convertToDto(History history);
    History convertToEntity(HistoryDto historyDto);

    History save(History history);


}
