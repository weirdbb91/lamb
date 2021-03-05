package com.portfolio.lamb.domain.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public Board getBoard(long id) {
        return boardRepository.findById(id).orElse(null);
    }

    public List<Board> getList() {
        return boardRepository.findAll().stream().filter(Board::isEnabled).collect(Collectors.toList());
    }

    public long deleteBoard(long id) {
        boardRepository.deleteById(id);
        return id;
    }


    public Page<Board> getBoardPage(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Page<Board> filteredBoardPage(String searchText, Pageable pageable) {
        return boardRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
    }
}
