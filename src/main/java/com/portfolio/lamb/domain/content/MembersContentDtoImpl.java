package com.portfolio.lamb.domain.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public class MembersContentDtoImpl extends BaseContainerImpl implements MembersContentDto {

    private Long memberId;

    public MembersContentDtoImpl(Long memberId) {
        this.memberId = memberId;
    }

    public MembersContentDtoImpl(MembersContent membersContent) {
        update(membersContent);
        this.memberId = membersContent.getMemberId();
    }
}