package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.Content;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Board extends Content {
    public Board(Member member) {
        super(member);
    }
}
