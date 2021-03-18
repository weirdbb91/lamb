package com.portfolio.lamb.domain.content;

public interface MembersContentDto extends BaseContainer {

    MembersContentDto setMemberId(Long memberId);

    Long getMemberId();

    MembersContentDto extract(MembersContent content);
}
