package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.content.MembersContent;
import com.portfolio.lamb.domain.content.MembersContentDto;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


/**
 * RequestMapping("/api") GET, POST(PUT), DELETE for content
 * "/api/list" GET fot searched content page
 */
@Slf4j
public abstract class BaseController<T extends MembersContent, D extends MembersContentDto, S extends BaseService, V extends ContentDtoValidator<D>> {

    @Autowired
    private MemberService memberService;

    @Autowired
    S service;

    @Autowired
    V validator;

    protected abstract String getThisTypeName();

    protected abstract T getNewContent(Member member);

    protected abstract D getNewDto();

    private final String ERROR_KEY = "ERROR";
    private final String ERROR_NOT_FOUND = "not found";
    private final String ERROR_DISABLED_CONTENT = "disabled content";
    private final String ERROR_PERMISSION_DENIED = "wrong creator permission denied";

    @GetMapping
    public String main(Model model) {
        model.addAttribute("contentTypeName", getThisTypeName());
        return getThisTypeName() + "/main";
    }

    /**
     * @param id contentId
     * @return selected content; 0L -> new form
     */
    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public D get(@RequestParam Long id, Model model) {
        log.info("{} {} request :: {}", "GET", getThisTypeName(), id);

        if (id == 0L) {
            D dto = getNewDto();
            dto.setMemberId(getThisMember().getId());
            return dto;
        }

        Optional<T> optionalContent = service.getById(id);
        if (!isExist(optionalContent, model)) return null;
        if (isBanned(optionalContent, model)) return null;

        T content = optionalContent.get();
        if (!isOwner(content.getMemberId(), model)) return null;

        return (D) getNewDto().extract(optionalContent.get());
    }

    /**
     * @param dto content form
     * @return contentId; not valid -> 0L
     */
    @RequestMapping(value = "/api", method = RequestMethod.POST)
    public long post(@ModelAttribute @Valid D dto, BindingResult bindingResult, Model model) {
        log.info("{} {} request :: {}", "POST", getThisTypeName(), dto);

        validator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) return 0L;
        if (!isOwner(dto.getMemberId(), model)) return 0L;

        return service.save((T) getNewContent(getThisMember()).update(dto)).getId();
    }

    /**
     * @param id contentId
     * @return contentId; not valid -> 0L
     */
    @RequestMapping(value = "/api", method = RequestMethod.DELETE)
    public long delete(@RequestParam Long id, Model model) {
        log.info("{} {} request :: {}", "DELETE", getThisTypeName(), id);

        Optional<T> optional = service.getById(id);
        if (!isExist(optional, model)) return 0L;
        if (!isOwner(optional.get().getMemberId(), model)) return 0L;

        return service.deleteById(id);
    }


    /**
     * @param pageable   request page info
     * @param searchText text looking for
     * @return contentPage
     */
    @RequestMapping(value = "/api/list", method = RequestMethod.GET)
    public Page<T> getFilteredPage(@PageableDefault(size = 4) Pageable pageable,
                                @RequestParam(required = false, defaultValue = "") String searchText) {
        log.info("{} {} request :: pageable : {}, searchText : {}",
                "GET/list", getThisTypeName(), pageable, searchText);

        return service.getFilteredPage(searchText, pageable);
    }
//        Page page = service.getFilteredPage(searchText, pageable);
//
//        if (isEmptyPage(page, model)) return;
//
//        int pageStartIdx = Math.max(1, page.getPageable().getPageNumber() - 4);
//        int pageEndIdx = Math.min(page.getTotalPages(), page.getPageable().getPageNumber() + 4);
//
//        model.addAttribute(getThisTypeName() + "PageStartIdx", pageStartIdx);
//        model.addAttribute(getThisTypeName() + "PageEndIdx", pageEndIdx);
//        model.addAttribute(getThisTypeName() + "Page", page);
//    }


//    private boolean isEmptyPage(Page page, Model model) {
//        if (!page.isEmpty()) return true;
//        model.addAttribute(getThisTypeName().toUpperCase() + "_" + ERROR_KEY, ERROR_NOT_FOUND);
//        return false;
//    }


    private boolean isBanned(Optional<T> optional, Model model) {
        if ((optional.get()).isEnabled()) return true;
        model.addAttribute(getThisTypeName().toUpperCase()
                + "_" + ERROR_KEY, ERROR_DISABLED_CONTENT);
        return false;
    }

    private boolean isExist(Optional<T> optional, Model model) {
        if (optional.isPresent()) return true;
        model.addAttribute(getThisTypeName().toUpperCase()
                + "_" + ERROR_KEY, ERROR_NOT_FOUND);
        return false;
    }

    private boolean isOwner(Long contentMemberId, Model model) {
        Member member = getThisMember();
        if (member == null || contentMemberId != member.getId()) {
            model.addAttribute(getThisTypeName().toUpperCase()
                    + "_" + ERROR_KEY, ERROR_PERMISSION_DENIED);
            return false;
        }
        return true;
    }

    private Member getThisMember() {
        return memberService.getMemberByUsername(SecurityContextHolder.getContext()
                .getAuthentication().getName()).orElse(null);
    }
}
