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
        BoardComment comment = dtoToEntity(commentDTO); //dto 받아 entity로 변경 (화면 -> DB)

        commentRepository.save(comment); //해당 entity DB에 저장

        return comment.getId();
    }

    @Override
    public List<BoardCommentDTO> getList(Long bno) {
        List<BoardComment> result = commentRepository
                .getBoardCommentByBoardOrderById(Board.builder().id(bno).build()); //받은 bno를 이용해 Board 엔티티를 생성해 게시물 댓글 리스트를 조회

        //조회한 댓글 리스트를 스트림으로 변환한 후
        //.map(reply -> entityToDTO(reply)): 각각의 댓글을 entityToDTO를 통해 BoardComment엔티티를 BoardCommentDTO로 변환 (DB -> 화면)
        //.collect(Collectors.toList()): 변환된 DTO들을 리스트로 수집하여 반환
        return result.stream().map(comment -> entityToDTO(comment)).collect(Collectors.toList());
    }

    @Override
    public void modify(BoardCommentDTO commentDTO) {
        BoardComment comment = dtoToEntity(commentDTO); //dto 받아 entity로 변경 (화면 -> DB)

        commentRepository.save(comment); //해당 entity DB에 저장 (자바코드를 수정하여 MODIFY(update)할 수 있다.)

    }

    @Override
    public void remove(Long rno) {

        commentRepository.deleteById(rno); // 받은 rno를 이용해 해당 댓글 삭제

    }
}
