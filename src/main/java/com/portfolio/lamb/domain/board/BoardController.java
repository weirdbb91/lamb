package com.portfolio.lamb.domain.board;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.tags.Param;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@Transactional
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private HttpSession session;

    private final String ERROR_BOARD_IS_DISABLED = "disabled board";
    private final String ERROR_BOARD_NOT_FOUND = "board not found board";
    private final String ERROR_PERMISSION_DENIED = "permission denied : not origin author";
    private final String ERROR_TITLE_TOO_LONG = "valid title length : 1 ~ 15";
    private final String ERROR_TITLE_EMPTY = "title empty";


    @GetMapping("/list")
    public String list(@PageableDefault(size = 2) Pageable pageable, Model model,
                       @RequestParam(required = false, defaultValue = "") String searchText) {

        Page<Board> boardPage = boardService.filteredBoardPage(searchText, pageable);
        log.info("filteredBoardPage : " + boardPage.toString());
        if (boardPage.isEmpty()) {
            log.info("filteredBoardPage is null");
            boardPage = boardService.getBoardPage(pageable);
            model.addAttribute("error", "search result error");
        }

        int startPageIdx = Math.max(1, boardPage.getPageable().getPageNumber() - 4);
        int endPageIdx = Math.min(boardPage.getTotalPages(), boardPage.getPageable().getPageNumber() + 4);

        model.addAttribute("startPageIdx", startPageIdx);
        model.addAttribute("endPageIdx", endPageIdx);
        model.addAttribute("boardPage", boardPage);
        return "board/list";
    }

    @GetMapping("/view")
    public String getBoard(@RequestParam(required = false) Long id, Model model, Param param) {
        Board board = boardService.getBoard(id);
        try {
            Assert.isTrue(board != null, ERROR_BOARD_NOT_FOUND);
            Assert.isTrue(board.isEnabled(), ERROR_BOARD_IS_DISABLED);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.info(e.getMessage());
            return "board/list";
        }
        model.addAttribute("board", board);
        return "board/view";
    }

    @GetMapping("/form")
    public String boardForm(@RequestParam(required = false) Long id, Model model, Param param) {
        if (id == null) {
            model.addAttribute("board", new Board((long) session.getAttribute("member_id")));
            return "board/form";
        }
        Board board = boardService.getBoard(id);
        try {
            Assert.isTrue(board != null, ERROR_BOARD_NOT_FOUND);
            Assert.isTrue(isMemberId(board.getAuthorId()), ERROR_PERMISSION_DENIED);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());

            param.setName("id");
            param.setValue(id.toString());
            return "board/view";
        }
        model.addAttribute("board", board);
        return "board/form";
    }

    @PostMapping("/form")
    public String formSubmit(@Valid Board form, Model model, Param param) {
        log.info("Submitted Board form : " + form.toString());
        try {
            Assert.isTrue(!form.getTitle().isEmpty(), ERROR_TITLE_EMPTY);
            Assert.isTrue(form.getTitle().length() <= 15, ERROR_TITLE_TOO_LONG);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "board/form";
        }

        Board board = boardService.getBoard(form.getId());
        if (!isMemberId(form.getAuthorId())) {
            model.addAttribute("error", ERROR_PERMISSION_DENIED);
            return "board/view";
        }
        boardService.save(form);
        log.info("saved well");
        param.setName("id");
        param.setValue(String.valueOf(board.getId()));
        return "board/view";
    }

    @DeleteMapping("/form")
    public String deleteBoard(@RequestBody Board board, Model model) {
        try {
            Assert.isTrue(isMemberId(board.getAuthorId()), ERROR_PERMISSION_DENIED);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "board/form";
        }
        boardService.deleteBoard(board.getId());
        return "board/list";
    }


    private boolean isMemberId(Long boardAuthor) {
        String memberId = session.getAttribute("member_id").toString();
        String authorId = boardAuthor.toString();
        log.info("The Board Author [" + authorId + "] is that You? : I am [" + memberId + "]");
        return authorId.equals(memberId);
    }
}