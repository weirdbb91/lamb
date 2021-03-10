package com.portfolio.lamb.service;

import com.portfolio.lamb.domain.Board;
import com.portfolio.lamb.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService extends BaseService<Board, BoardRepository> {
}
