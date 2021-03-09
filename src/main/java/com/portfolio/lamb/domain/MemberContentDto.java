package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.user.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberContentDto implements MemberContentInterface{

    private long id;

    private String title;

    private String content;

    private Member member;

    private boolean enabled;

    private long memberId;

    public MemberContentDto(long memberId) {
        this.memberId = memberId;
    }

    public MemberContentDto(MemberContentInterface memberContentInterface) {
        update(memberContentInterface);
    }

    @Override
    public MemberContentInterface update(MemberContentInterface memberContentInterface) {
        this.id = memberContentInterface.getId();
        this.title = memberContentInterface.getTitle();
        this.content = memberContentInterface.getContent();
        this.member = memberContentInterface.getMember();
        this.enabled = memberContentInterface.isEnabled();
        return this;
    }
}
