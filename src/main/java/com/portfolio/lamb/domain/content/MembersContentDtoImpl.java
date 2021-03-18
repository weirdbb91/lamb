package com.portfolio.lamb.domain.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MembersContentDtoImpl extends BaseContainerImpl implements MembersContentDto {

    private Long memberId;

    @Override
    public MembersContentDto setMemberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    @Override
    public MembersContentDto extract(MembersContent content) {
        update(content);
        this.memberId = content.getMemberId();
        return this;
    }
}