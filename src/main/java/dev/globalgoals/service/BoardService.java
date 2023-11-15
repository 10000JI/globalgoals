package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.BoardDto;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;

public interface BoardService {
    Long register(BoardDto boardDto);

    PageResultDTO<BoardDto, Board> getList(PageRequestDTO requestDTO);

    default Board dtoToEntity(BoardDto dto, User user) {
        Board entity = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .hit(dto.getHit())
                .user(user)
                .build();
        return entity;
    }

    default BoardDto boardDto(Board entity) {
        BoardDto dto = BoardDto.builder()
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
