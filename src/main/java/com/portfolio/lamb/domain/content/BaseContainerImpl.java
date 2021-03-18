package com.portfolio.lamb.domain.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@RequiredArgsConstructor
public class BaseContainerImpl extends Timestamped implements BaseContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 15)
    private String title;

    @Size(max = 1000)
    private String content;

    private boolean enabled = true;

    @Override
    public BaseContainer update(BaseContainer baseContainer) {
        this.id = baseContainer.getId();
        this.title = baseContainer.getTitle();
        this.content = baseContainer.getContent();
        this.enabled = baseContainer.isEnabled();
        return this;
    }
}
