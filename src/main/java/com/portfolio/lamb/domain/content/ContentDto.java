package com.portfolio.lamb.domain.content;

import lombok.Data;

@Data
public class ContentDto implements IContentDto {

    private Long id;

    private String title;

    private String content;

    private boolean enabled;

    private Long memberId;

    public ContentDto(Long memberId) {
        this.memberId = memberId;
    }

    public ContentDto(IContent content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.content = content.getContent();
        this.enabled = content.isEnabled();
        this.memberId = content.getMemberId();
    }

    @Override
    public IContentDto extract(IContent content) {
        return null;
    }
}
