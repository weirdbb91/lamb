package com.portfolio.lamb.domain.content;

public interface IContent extends IBaseContainer {

    Long getMemberId();

    IBaseContainer update(IContentDto dto);
}
