package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.MembersContentImpl;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
public class Post extends MembersContentImpl {
    public Post(Member member) {
        super(member);
    }
}