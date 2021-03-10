package com.portfolio.lamb.domain.content;

public interface IContentDto extends IBaseContainer {

    Long getMemberId();

    IContentDto extract(IContent content);
}
