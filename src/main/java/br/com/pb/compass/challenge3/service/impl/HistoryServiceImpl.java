package br.com.pb.compass.challenge3.service.impl;

import br.com.pb.compass.challenge3.dto.HistoryDto;
import br.com.pb.compass.challenge3.entity.Enum;
import br.com.pb.compass.challenge3.entity.History;
import br.com.pb.compass.challenge3.entity.Post;
import br.com.pb.compass.challenge3.service.HistoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HistoryServiceImpl implements HistoryService {

    private Enum.PostState state;
    private ModelMapper mapper;

    @Autowired
    public HistoryServiceImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public History createHistory(Enum.PostState state) {
        History history = new History();
        history.getId();
        history.setDateTime(new Date());
        history.setState(state);
        return history;
    }

    public void updateHistory(Post post, Enum.PostState newState) {
        History history = createHistory(newState);
        history.setPost(post);
        post.getHistory().add(history);
    }

    public HistoryDto convertToDto(History history) {
        HistoryDto historyDto = mapper.map(history, HistoryDto.class);
        return historyDto;
    }

    public History convertToEntity(HistoryDto historyDto) {
        History history = mapper.map(historyDto, History.class);
        return history;
    }

}
