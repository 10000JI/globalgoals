package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;

import java.io.IOException;

public interface BoardService {
    Long register(BoardDTO boardDto) throws IOException;

    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

    BoardDTO get(Long id);

    void removeWithComments(Long id);

    void modify(BoardDTO dto);

    default Board dtoToEntity(BoardDTO dto) {

        User user = User.builder().id(dto.getWriter()).build();

        Board entity = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .hit(dto.getHit())
                .user(user)
                .build();
        return entity;
    }

    default BoardDTO entityToDto(Board board, User user, Long commentCount) {
        BoardDTO dto = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writer(user.getId())
                .writerEmail(user.getEmail())
                .commentCount(commentCount.intValue())
                .build();
        return dto;
    }
}
