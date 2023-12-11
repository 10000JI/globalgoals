package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardComment;
import dev.globalgoals.dto.BoardCommentDTO;

import java.util.List;

public interface BoardCommentService {

    Long register(BoardCommentDTO commentDTO); //댓글의 등록

    List<BoardCommentDTO> getList(Long bno); //특정 게시물의 댓글 목록

    void modify(BoardCommentDTO commentDTO); //댓글 수정

    void remove(Long rno); //댓글 삭제

    //BoardCommentDTO를 BoardComment객체로 변환 Board객체의 처리가 수반됨
    default BoardComment dtoToEntity(BoardCommentDTO commentDTO){

        Board board = Board.builder().id(commentDTO.getBno()).build();

        BoardComment comment = BoardComment.builder()
                .id(commentDTO.getRno())
                .comments(commentDTO.getComments())
                .writer(commentDTO.getReplyer())
                .board(board)
                .build();

        return comment;
    }

    //BoardComment객체를 BoardCommentDTO로 변환 Board 객체가 필요하지 않으므로 게시물 번호만
    default BoardCommentDTO entityToDTO(BoardComment comment){
        String commentWriter = (comment.getWriter() == null) ? "(알 수 없음)" : comment.getWriter();
        //탈퇴한 회원 댓글은 알수없음으로 표시

        BoardCommentDTO dto = BoardCommentDTO.builder()
                .rno(comment.getId())
                .comments(comment.getComments())
                .replyer(commentWriter)
                .regDate(comment.getRegDate())
                .modDate(comment.getModDate())
                .build();

        return dto;

    }
}
