package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardComment;
import dev.globalgoals.dto.BoardCommentDTO;
import dev.globalgoals.repository.BoardCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardCommentImpl implements BoardCommentService{

    private final BoardCommentRepository commentRepository;

    @Override
    public Long register(BoardCommentDTO commentDTO) {
        BoardComment comment = dtoToEntity(commentDTO);

        commentRepository.save(comment);

        return comment.getId();
    }

    @Override
    public List<BoardCommentDTO> getList(Long bno) {
        List<BoardComment> result = commentRepository
                .getBoardCommentByBoardOrderById(Board.builder().id(bno).build());
        return result.stream().map(comment -> entityToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public void modify(BoardCommentDTO commentDTO) {
        BoardComment comment = dtoToEntity(commentDTO);

        commentRepository.save(comment);

    }

    @Override
    public void remove(Long rno) {

        commentRepository.deleteById(rno);

    }
}
