package com.portfolio.lamb.domain.board;

import com.portfolio.lamb.domain.Timestamped;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Board extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 1, max = 15)
    private String title;

    @Size(max = 1000)
    private String content;

    private long writerId;

    private boolean enabled = true;

    public Board(long writerId) {
        this.writerId = writerId;
    }

    public Board update(BoardDto boardDto) {
        this.title = !boardDto.getTitle().isEmpty() ? boardDto.getTitle() : this.title;
        this.content = !boardDto.getContent().isEmpty() ? boardDto.getContent() : this.content;
        return this;
    }
}
