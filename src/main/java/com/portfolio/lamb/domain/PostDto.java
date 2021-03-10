package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.MembersContent;
import com.portfolio.lamb.domain.content.MembersContentDtoImpl;

public class PostDto extends MembersContentDtoImpl {
    public PostDto(Long memberId) {
        super(memberId);
    }

    public PostDto(MembersContent membersContent) {
        super(membersContent);
    }
}
