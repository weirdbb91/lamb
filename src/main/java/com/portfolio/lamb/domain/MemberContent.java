package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.user.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@MappedSuperclass
@NoArgsConstructor
public class MemberContent extends MemberContentDto{

    @NotNull
    @Size(min = 1, max = 15)
    public String title;

    @Size(max = 1000)
    public String content;

    public boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "member_id")
    public Member member;

    public MemberContent(Member member) {
        this.member = member;
    }

    public MemberContent(MemberContentDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.enabled = dto.isEnabled();
        this.member = dto.getMember();
    }

    @Override
    public MemberContentInterface update(MemberContentInterface dto) {
        this.title = !dto.getTitle().isEmpty() ? dto.getTitle() : this.title;
        this.content = !dto.getContent().isEmpty() ? dto.getContent() : this.content;
        return this;
    }
}
