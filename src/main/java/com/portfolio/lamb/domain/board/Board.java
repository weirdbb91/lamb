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

    private long authorId;

    private boolean enabled = true;

    public Board(long authorId) {
        this.authorId = authorId;
    }

    public long update(Board request) {
        this.title = !request.getTitle().isEmpty() ? request.getTitle() : this.title;
        this.content = !request.getContent().isEmpty() ? request.getContent() : this.content;
        return this.id;
    }
}
