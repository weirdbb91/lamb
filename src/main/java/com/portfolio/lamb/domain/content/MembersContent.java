package com.portfolio.lamb.domain.content;

public interface MembersContent extends BaseContainer {

    Long getMemberId();

    MembersContent update(MembersContentDto dto);
}
