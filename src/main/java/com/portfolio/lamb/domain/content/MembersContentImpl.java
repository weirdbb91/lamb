package com.portfolio.lamb.domain.content;

import com.portfolio.lamb.domain.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class MembersContentImpl extends BaseContainerImpl implements MembersContent {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public MembersContentImpl(Member member) {
        this.member = member;
    }

    @Override
    public MembersContent update(MembersContentDto dto) {
        if (!dto.getTitle().isEmpty()) setTitle(dto.getTitle());
        if (!dto.getContent().isEmpty()) setTitle(dto.getContent());
        return null;
    }

    @Override
    public Long getMemberId() {
        return member.getId();
    }

}
