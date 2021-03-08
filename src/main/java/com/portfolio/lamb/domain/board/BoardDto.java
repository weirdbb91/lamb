package com.portfolio.lamb.domain.board;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDto {

    private long id;

    private String compareTitle;

    private String title;

    private String content;

    private long writerId;

    public BoardDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writerId = board.getWriterId();
    }
}
