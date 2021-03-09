package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.MemberContent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberContentDto {

    private long id;

    private String compareTitle;

    private String title;

    private String content;

    private long writerId;

    public MemberContentDto(MemberContent memberContent) {
        this.id = memberContent.getId();
        this.title = memberContent.getTitle();
        this.content = memberContent.getContent();
        this.writerId = memberContent.getMember().getId();
    }
}
