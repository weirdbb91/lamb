package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.MemberContentDto;
import com.portfolio.lamb.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class BaseController<D extends MemberContentDto, ID, S extends BaseService> {

    S baseService;

    public abstract String getContentTypeName();


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public void list(@PageableDefault(size = 4) Pageable pageable, Model model,
                     @RequestParam(required = false, defaultValue = "") String searchText) {

        Page page = baseService.getFilteredPage(searchText, pageable);

        if (page.isEmpty()) {
            model.addAttribute("error",
                    getContentTypeName() + " search : No results found");
            return;
        }

        int pageStartIdx = Math.max(1, page.getPageable().getPageNumber() - 4);
        int pageEndIdx = Math.min(page.getTotalPages(), page.getPageable().getPageNumber() + 4);

        model.addAttribute(getContentTypeName() + "PageStartIdx", pageStartIdx);
        model.addAttribute(getContentTypeName() + "PageEndIdx", pageEndIdx);
        model.addAttribute(getContentTypeName() + "Page", page);
    }


}
