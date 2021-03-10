package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.content.IContent;
import com.portfolio.lamb.domain.content.ContentDto;

public class PostDto extends ContentDto {
    public PostDto(Long memberId) {
        super(memberId);
    }

    public PostDto(IContent content) {
        super(content);
    }
}
