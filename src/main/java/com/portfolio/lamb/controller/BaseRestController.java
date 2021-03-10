package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.content.MembersContent;
import com.portfolio.lamb.domain.content.MembersContentDto;
import com.portfolio.lamb.domain.content.MembersContentDtoImpl;
import com.portfolio.lamb.service.BaseService;
import com.portfolio.lamb.service.MemberService;
import com.portfolio.lamb.validator.ContentDtoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
public abstract class BaseRestController<D extends MembersContentDto, S extends BaseService, V extends ContentDtoValidator> {

    @Autowired
    private MemberService memberService;

    @Autowired
    S service;

    @Autowired
    V validator;

    protected abstract String getThisTypeName();

    protected abstract MembersContent getNewContent(Member member);

    private final String ERROR_KEY = "ERROR";
    private final String ERROR_NOT_FOUND = "not found";
    private final String ERROR_DISABLED_CONTENT = "disabled content";
    private final String ERROR_PERMISSION_DENIED = "wrong creator permission denied";


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page list(@PageableDefault(size = 4) Pageable pageable, Model model,
                     @RequestParam(required = false, defaultValue = "") String searchText) {
        log.info("on {} {} request pageable : {}, searchText : {}", getThisTypeName(), "GET/list", pageable, searchText);

        Page page = service.getFilteredPage(searchText, pageable);

        if (page.isEmpty()) model.addAttribute(getThisTypeName() + "_" + ERROR_KEY, ERROR_NOT_FOUND);
//        int pageStartIdx = Math.max(1, page.getPageable().getPageNumber() - 4);
//        int pageEndIdx = Math.min(page.getTotalPages(), page.getPageable().getPageNumber() + 4);
//
//        model.addAttribute(getThisTypeName() + "PageStartIdx", pageStartIdx);
//        model.addAttribute(getThisTypeName() + "PageEndIdx", pageEndIdx);
//        model.addAttribute(getThisTypeName() + "Page", page);
        return page.isEmpty() ? null : page;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public long post(@ModelAttribute @Valid D dto, BindingResult bindingResult, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "POST", dto);
        validator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) return 0L;
        if (service.getById(dto.getId()).isPresent()) return 0L;
        if (!isOwner(dto.getMemberId(), model)) return 0L;

        return service.save(getNewContent(getThisMember()).update(dto)).getId();
    }


    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public long update(@ModelAttribute @Valid D dto, BindingResult bindingResult, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "PUT", dto);
        validator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) return 0L;
        if (service.getById(dto.getId()).isEmpty()) return 0L;
        if (!isOwner(dto.getMemberId(), model)) return 0L;

        return service.save(getNewContent(getThisMember()).update(dto)).getId();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public D getById(Long id, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "GET", id);
        Optional<MembersContent> optionalContent = service.getById(id);

        if (!isExist(optionalContent, model)) return null;
        if (isBanned(optionalContent, model)) return null;

        return (D) new MembersContentDtoImpl(optionalContent.get());
    }



    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public long deleteById(Long id, Model model) {
        log.info("on {} {} request : {}", getThisTypeName(), "DELETE", id);
        Optional<MembersContent> optionalContent = service.getById(id);

        if (!isExist(optionalContent, model)) return 0L;
        if (!isOwner(optionalContent.get().getMemberId(), model)) return 0L;

        return service.deleteById(id);
    }



    private boolean isBanned(Optional<MembersContent> optional, Model model) {
        if ((optional.get()).isEnabled()) return true;
        model.addAttribute(getThisTypeName().toUpperCase() + "_" + ERROR_KEY, ERROR_DISABLED_CONTENT);
        return false;
    }

    private boolean isExist(Optional<MembersContent> optional, Model model) {
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
