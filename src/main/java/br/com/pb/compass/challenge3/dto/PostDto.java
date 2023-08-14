package br.com.pb.compass.challenge3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String body;
    private List<CommentDto> comments;
    private List<HistoryDto> history;
}
