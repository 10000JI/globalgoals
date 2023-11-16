package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;

public interface BoardService {
    Long register(BoardDTO boardDto);

    PageResultDTO<BoardDTO, Board> getList(PageRequestDTO requestDTO);

    BoardDTO read(Long id);

    void modify(BoardDTO dto);

    void remove(Long id);

    default Board dtoToEntity(BoardDTO dto, User user) {
        Board entity = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .hit(dto.getHit())
                .user(user)
                .build();
        return entity;
    }

    default BoardDTO entityToDto(Board entity) {
        BoardDTO dto = BoardDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .hit(entity.getHit())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .writer(entity.getUser().getId())
                .build();
        return dto;
    }
}
