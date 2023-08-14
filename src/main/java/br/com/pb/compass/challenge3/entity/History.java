package br.com.pb.compass.challenge3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateTime;

    @Enumerated(EnumType.STRING)
    private Enum.PostState state;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
