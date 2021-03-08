package com.portfolio.lamb.domain.board;


import com.portfolio.lamb.domain.user.MemberService;
import com.portfolio.lamb.validator.BoardValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardValidator boardValidator;

    @Autowired
    private HttpSession session;

    private final String ERROR_BOARD_IS_DISABLED = "disabled board";
    private final String ERROR_BOARD_NOT_FOUND = "board not found";
    private final String ERROR_PERMISSION_DENIED = "permission denied : not origin writer";


    @GetMapping("/list")
    public String list(@PageableDefault(size = 2) Pageable pageable, Model model,
                       @RequestParam(required = false, defaultValue = "") String searchText) {
        Page<Board> boardPage = boardService.filteredBoardPage(searchText, pageable);
        log.info("filteredBoardPage : " + boardPage.toString());
        if (boardPage.isEmpty()) {
            log.info("filteredBoardPage is null");
            boardPage = boardService.getBoardPage(pageable);
            model.addAttribute("error", "No results found");
        }

        int startPageIdx = Math.max(1, boardPage.getPageable().getPageNumber() - 4);
        int endPageIdx = Math.min(boardPage.getTotalPages(), boardPage.getPageable().getPageNumber() + 4);

        model.addAttribute("startPageIdx", startPageIdx);
        model.addAttribute("endPageIdx", endPageIdx);
        model.addAttribute("boardPage", boardPage);
        return "board/list";
    }

    @GetMapping("/view")
    public String getBoard(@RequestParam Long id, Model model) {
        log.info("on view request : " + id);
        Board board = boardService.getBoard(id);
        try {
            Assert.isTrue(board != null, ERROR_BOARD_NOT_FOUND);
            Assert.isTrue(board.isEnabled(), ERROR_BOARD_IS_DISABLED);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.info(e.getMessage());
            return "redirect:/board/list";
        }
        model.addAttribute("boardDto", new BoardDto(board));
        return "board/view";
    }

    @GetMapping("/form")
    public String boardForm(@RequestParam(required = false) Long id, Principal principal, Model model) {
        log.info("on form GET request : " + id);
        if (id == null) {
            BoardDto boardDto = new BoardDto();
            boardDto.setWriterId(memberService.getMemberByUsername(principal.getName()).getId());
            model.addAttribute("boardDto", boardDto);
            return "board/form";
        }
        Board board = boardService.getBoard(id);
        try {
            Assert.isTrue(board != null, ERROR_BOARD_NOT_FOUND);
            Assert.isTrue(isMemberId(board.getWriterId()), ERROR_PERMISSION_DENIED);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/board/list";
        }
        model.addAttribute("boardDto", new BoardDto(board));
        return "board/form";
    }

    @PostMapping("/form")
    public String formSubmit(Principal principal,
                             @ModelAttribute @Valid BoardDto boardDto, BindingResult bindingResult, Model model) {
        log.info("on form POST request : " + boardDto.toString());
        Board board = boardService.getBoard(boardDto.getId());
        if (board == null) {
            board = new Board(memberService.getMemberByUsername(
                    principal.getName()).getId());
        }
        boardDto.setCompareTitle(board.getTitle());
        boardValidator.validate(boardDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "board/form";
        }
        boardService.save(board.update(boardDto));
        log.info("saved well");
        return "board/view";
    }

    @GetMapping("/delete")
    public String deleteBoard(Principal principal,
                              @RequestParam Long id, Model model) {
        long writerId = boardService.getBoard(id).getWriterId();
        log.info("on delete request : " + id + " writen by " + writerId);
        if (!isMemberId(writerId)) {
            model.addAttribute("error", ERROR_PERMISSION_DENIED);
            log.info(ERROR_PERMISSION_DENIED);
            return "redirect:/board/list";
        }
        boardService.deleteBoard(id);
        log.info("deleted");
        return "redirect:/board/list";
    }


    private boolean isMemberId(Long boardWriterId) {
        String memberId = session.getAttribute("member_id").toString();
        String writerId = boardWriterId.toString();
        log.info("The Board Author [" + writerId + "] is that You? : I am [" + memberId + "]");
        return writerId.equals(memberId);
    }
}