package com.portfolio.lamb.controller;

import com.portfolio.lamb.domain.Board;
import com.portfolio.lamb.domain.BoardDto;
import com.portfolio.lamb.domain.Member;
import com.portfolio.lamb.domain.content.IContent;
import com.portfolio.lamb.service.BoardService;
import com.portfolio.lamb.service.MemberService;
import com.portfolio.lamb.validator.BoardDtoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController extends BaseController<BoardDto, BoardService, BoardDtoValidator> {

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardDtoValidator boardValidator;

    @Override
    protected String getThisTypeName() {
        return "board";
    }

    @Override
    protected IContent getNewContent(Member member) {
        return new Board(member);
    }


    private final String ERROR_BOARD_IS_DISABLED = "disabled board";
    private final String ERROR_BOARD_NOT_FOUND = "board not found";
    private final String ERROR_PERMISSION_DENIED = "permission denied : not origin writer";


    @GetMapping("/list")
    public String list(@PageableDefault(size = 2) Pageable pageable, Model model,
                       @RequestParam(required = false, defaultValue = "") String searchText) {
        Page<Board> contentPage = boardService.getFilteredPage(searchText, pageable);
        log.info("filteredBoardPage : " + contentPage.toString());
        if (contentPage.isEmpty()) {
            log.info("filteredBoardPage is null");
            contentPage = boardService.getPage(pageable);
            model.addAttribute("error", "No results found");
        }

        int PageStartIdx = Math.max(1, contentPage.getPageable().getPageNumber() - 4);
        int PageEndIdx = Math.min(contentPage.getTotalPages(), contentPage.getPageable().getPageNumber() + 4);

        model.addAttribute("PageStartIdx", PageStartIdx);
        model.addAttribute("PageEndIdx", PageEndIdx);
        model.addAttribute("Page", contentPage);
        return "board/list";
    }

    @GetMapping("/view")
    public String getBoard(@RequestParam(required = false) Long id, Model model) {
        log.info("on view request : " + id);
        Optional<Board> optionalBoard = boardService.getById(id);
        if (optionalBoard.isEmpty()) {
            model.addAttribute("error", "board not found");
            return "redirect:/board/list";
        }
        Board board = optionalBoard.get();

        if (!board.isEnabled()) {
            model.addAttribute("error", ERROR_BOARD_IS_DISABLED);
            return "redirect:/board/list";
        }

        model.addAttribute("boardDto", new BoardDto(board));
        return "board/view";
    }

    @GetMapping("/form")
    public String boardForm(Authentication authentication,
                            @RequestParam(required = false) Long id, Model model) {
        log.info("on form GET request : " + id);
        Optional<Member> optionalMember = memberService.getMemberByUsername(authentication.getName());
        if (optionalMember.isEmpty()) {
            model.addAttribute("error", "present member not found");
            return "redirect:/board/list";
        }
        Member member = optionalMember.get();
        if (id == null || id == 0L) {
            model.addAttribute("boardDto", new BoardDto(new Board(member)));
            return "board/form";
        }

        Optional<Board> optionalBoard = boardService.getById(id);
        if (optionalBoard.isEmpty()) {
            model.addAttribute("error", ERROR_BOARD_NOT_FOUND);
            return "redirect:/board/list";
        }
        Board board = optionalBoard.get();

        if (board.getMember().getId() != member.getId()) {
            model.addAttribute("error", ERROR_PERMISSION_DENIED);
            return "redirect:/board/list";
        }

        model.addAttribute("boardDto", new BoardDto(board));
        return "board/form";
    }

    @PostMapping("/form")
    public String formSubmit(Authentication authentication,
                             @ModelAttribute @Valid BoardDto boardDto, BindingResult bindingResult, Model model) {
        log.info("on form POST request : " + boardDto.toString());
        Optional<Board> optionalBoard = boardService.getById(boardDto.getId());
        Board board;
        if (optionalBoard.isEmpty()) {
            Optional<Member> optionalMember = memberService.getMemberByUsername(authentication.getName());
            if (optionalMember.isEmpty()) {
                model.addAttribute("error", "present member not found");
                return "board/form";
            }
            Member member = optionalMember.get();
            board = new Board(member);
        } else {
            board = optionalBoard.get();
        }

        boardValidator.validate(boardDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "board/form";
        }

        boardService.save((Board) board.update(boardDto));
        log.info("saved well");
        model.addAttribute("boardDto", new BoardDto(board));
        return "board/view";
    }

    @GetMapping("/delete")
    public String deleteBoard(Authentication authentication,
                              @RequestParam Long id, Model model) {
        Optional<Member> optionalMember = memberService.getMemberByUsername(authentication.getName());
        if (optionalMember.isEmpty()) {
            model.addAttribute("error", "present member not found");
            return "redirect:/board/list";
        }
        Member member = optionalMember.get();

        Optional<Board> optionalBoard = boardService.getById(id);
        if (optionalBoard.isEmpty()) {
            model.addAttribute("error", "board not found");
            return "redirect:/board/list";
        }
        Board board = optionalBoard.get();
        long writerId = board.getMember().getId();
        log.info("on delete request : " + id + " writen by " + writerId);
        if (writerId != member.getId()) {
            model.addAttribute("error", ERROR_PERMISSION_DENIED);
            log.info(ERROR_PERMISSION_DENIED);
            return "redirect:/board/list";
        }
        boardService.deleteById(id);
        log.info("deleted");
        return "redirect:/board/list";
    }
}