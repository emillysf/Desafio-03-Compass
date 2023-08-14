package br.com.pb.compass.challenge3.dto;

import br.com.pb.compass.challenge3.entity.Enum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryDto {
    private Long id;
    private Date dateTime;
    private Enum.PostState state;
}
