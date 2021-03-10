package com.portfolio.lamb.domain.content;

public interface IContent extends IBaseContainer {

    Long getAuthorId();

    IBaseContainer update(IContentDto dto);
}
