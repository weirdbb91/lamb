package com.portfolio.lamb.domain.content;

import com.portfolio.lamb.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class Content extends Timestamped implements IContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 15)
    private String title;

    @Size(max = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean enabled = true;

    public Content(Member member) {
        this.member = member;
    }

    public IBaseContainer update(IContentDto dto) {
        this.title = !dto.getTitle().isEmpty() ? dto.getTitle() : this.title;
        this.content = !dto.getContent().isEmpty() ? dto.getContent() : this.content;
        return null;
    }

    @Override
    public Long getAuthorId() {
        return member.getId();
    }
}
