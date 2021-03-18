package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.MembersContentImpl;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
public class Board extends MembersContentImpl {
    public Board(Member member) {
        super(member);
    }
}
