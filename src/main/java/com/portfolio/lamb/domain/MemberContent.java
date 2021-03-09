package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.user.Member;
import lombok.Data;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@MappedSuperclass
public class MemberContent {

    @NotNull
    @Size(min = 1, max = 15)
    public String title;

    @Size(max = 1000)
    public String content;

    public boolean enabled = true;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    public Member member;

    public MemberContent(Member member) {
        this.member = member;
    }

    public MemberContent update(MemberContentDto memberContentDto) {
        this.title = !memberContentDto.getTitle().isEmpty() ? memberContentDto.getTitle() : this.title;
        this.content = !memberContentDto.getContent().isEmpty() ? memberContentDto.getContent() : this.content;
        return this;
    }

    public long getId() {
        return 0L;
    }
}
