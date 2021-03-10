package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.Content;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Post extends Content {
    public Post(Member member) {
        super(member);
    }
}