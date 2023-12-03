package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.BoardCategory;
import dev.globalgoals.domain.BoardImage;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;
import dev.globalgoals.file.UploadFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface BoardService {
    Long register(BoardDTO boardDto) throws IOException;

    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO, String param);

    BoardDTO get(Long id);

    List<UploadFile> getImage(Long id);

    void removeWithComments(Long id);

    void modify(BoardDTO dto, String[] storeFileNames) throws IOException;

    void saveScrap(Long id, Principal principal);

    default Board dtoToEntity(BoardDTO dto) {

        User user = User.builder().id(dto.getWriter()).build();

        BoardCategory category = BoardCategory.builder().id(dto.getCateNum()).build();

        Board entity = Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .hit(dto.getHit())
                .user(user)
                .boardCategory(category)
                .build();
        return entity;
    }

    default BoardDTO entityToDto(Board board, User user, Long commentCount, BoardCategory boardCategory) {
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
                .category(boardCategory.getCategoryName())
                .build();
        return dto;
    }
}
