package com.portfolio.lamb.domain.board;

import com.portfolio.lamb.domain.Timestamped;
import com.portfolio.lamb.domain.user.Member;
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

    private boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Member member;

    public Board(Member member) {
        this.member = member;
    }

    public Board update(BoardDto boardDto) {
        this.title = !boardDto.getTitle().isEmpty() ? boardDto.getTitle() : this.title;
        this.content = !boardDto.getContent().isEmpty() ? boardDto.getContent() : this.content;
        return this;
    }
}
