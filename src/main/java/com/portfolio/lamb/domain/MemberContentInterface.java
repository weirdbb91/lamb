package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.user.Member;

public interface MemberContentInterface {

    long getId();

    String getTitle();

    String getContent();

    Member getMember();

    boolean isEnabled();

    long getMemberId();

    MemberContentInterface update(MemberContentInterface dto);
}
