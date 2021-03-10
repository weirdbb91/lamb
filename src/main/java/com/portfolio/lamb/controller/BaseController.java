package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.content.Content;
import com.portfolio.lamb.domain.content.ContentDto;
import com.portfolio.lamb.domain.content.IContent;
import com.portfolio.lamb.domain.content.IContentDto;
import com.portfolio.lamb.service.BaseService;
import com.portfolio.lamb.service.MemberService;
import com.portfolio.lamb.validator.ContentDtoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
public abstract class BaseController<T extends IContent, D extends IContentDto, S extends BaseService<T>, V extends ContentDtoValidator<D>> {

    @Autowired
    private MemberService memberService;

    @Autowired
    S service;

    @Autowired
    V validator;

    protected abstract String getThisTypeName();

    protected abstract IContent getNewContent(Member member);

    private final String ERROR_KEY = "ERROR";
    private final String ERROR_NOT_FOUND = "not found";
    private final String ERROR_DISABLED_CONTENT = "disabled content";
    private final String ERROR_PERMISSION_DENIED = "wrong creator permission denied";


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void list(@PageableDefault(size = 4) Pageable pageable, Model model,
                     @RequestParam(required = false, defaultValue = "") String searchText) {
        log.info("on {} {} request pageable : {}, searchText : {}", getThisTypeName(), "GET/list", pageable, searchText);

        Page page = service.getFilteredPage(searchText, pageable);

        if (isEmptyPage(page, model)) return;

        int pageStartIdx = Math.max(1, page.getPageable().getPageNumber() - 4);
        int pageEndIdx = Math.min(page.getTotalPages(), page.getPageable().getPageNumber() + 4);

        model.addAttribute(getThisTypeName() + "PageStartIdx", pageStartIdx);
        model.addAttribute(getThisTypeName() + "PageEndIdx", pageEndIdx);
        model.addAttribute(getThisTypeName() + "Page", page);
    }


    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public D getById(@RequestParam Long id, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "GET", id);

        Optional<T> optional = service.getById(id);
        if (!isExist(optional, model)) return null;
        if (isBanned(optional, model)) return null;

        return (D) new ContentDto(optional.get());
    }


    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public D getForm(@RequestParam(required = false) Long id, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "GET/form", id);

        if (id == null || id == 0L) return (D) new ContentDto(getThisMember().getId());

        Optional<T> optional = service.getById(id);
        if (!isExist(optional, model)) return null;

        T content = optional.get();
        if (!isOwner(content.getAuthorId(), model)) return null;

        return (D) new ContentDto(content);
    }


    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public long formSubmit(@ModelAttribute @Valid D dto, Model model, BindingResult bindingResult) {
        log.info("on {} {} request : {}", getThisTypeName(), "POST/form", dto);

        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) return 0L;
        if (!isOwner(dto.getMemberId(), model)) return 0L;

        return service.save((T) getNewContent(getThisMember()).update(dto)).getId();
    }


    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public long deleteById(@RequestParam Long id, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "DELETE", id);

        Optional<T> optional = service.getById(id);
        if (!isExist(optional, model)) return 0L;
        if (!isOwner(optional.get().getAuthorId(), model)) return 0L;

        service.deleteById(id);
        return id;
    }


    private boolean isEmptyPage(Page page, Model model) {
        if (!page.isEmpty()) return true;
        model.addAttribute(getThisTypeName().toUpperCase() + "_" + ERROR_KEY, ERROR_NOT_FOUND);
        return false;
    }

    private boolean isBanned(Optional optional, Model model) {
        if (((D) optional.get()).isEnabled()) return true;
        model.addAttribute(getThisTypeName().toUpperCase() + "_" + ERROR_KEY, ERROR_DISABLED_CONTENT);
        return false;
    }

    private boolean isExist(Optional optional, Model model) {
        if (optional.isPresent()) return true;
        model.addAttribute(getThisTypeName().toUpperCase() + "_" + ERROR_KEY, ERROR_NOT_FOUND);
        return false;
    }

    private boolean isOwner(Long contentMemberId, Model model) {
        Member member = getThisMember();
        if (member == null || contentMemberId != member.getId()) {
            model.addAttribute(getThisTypeName().toUpperCase() + "_" + ERROR_KEY, ERROR_PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    private Member getThisMember() {
        return memberService.getMemberByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()).orElse(null);
    }
}
