package com.portfolio.lamb.domain.content;

public interface BaseContainer {

    Long getId();

    String getTitle();

    String getContent();

    boolean isEnabled();

    BaseContainer update(BaseContainer baseContainer);
}
