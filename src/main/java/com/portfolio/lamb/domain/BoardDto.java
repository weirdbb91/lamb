package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.MembersContent;
import com.portfolio.lamb.domain.content.MembersContentDtoImpl;

public class BoardDto extends MembersContentDtoImpl {
    public BoardDto(Long memberId) {
        super(memberId);
    }

    public BoardDto(MembersContent membersContent) {
        super(membersContent);
    }
}
