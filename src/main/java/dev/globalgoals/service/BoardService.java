package dev.globalgoals.service;

import dev.globalgoals.domain.*;
import dev.globalgoals.dto.*;
import dev.globalgoals.file.UploadFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface BoardService {
    Long register(BoardDTO boardDto) throws IOException;

    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO, String param, Principal principal);

    BoardDTO get(Long id);

    List<UploadFile> getImage(Long id);

    void removeWithComments(Long id);

    void modify(BoardDTO dto, String[] storeFileNames) throws IOException;

    String saveScrap(Long id, Principal principal);

    String saveCertify(CertifyDTO certifyDTO);

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
        String writerName = (board.getUser() == null) ? "(알 수 없음)" : user.getId();
        String writerEmail = (board.getUser() == null) ? " " : user.getEmail();

        BoardDTO dto = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writer(writerName)
                .writerEmail(writerEmail)
                .commentCount(commentCount.intValue())
                .category(boardCategory.getCategoryName())
                .build();
        return dto;
    }

    default BoardDTO entityToDtoBC(Board board, BoardComment boardComment,  Long commentCount, BoardCategory boardCategory) {
        BoardDTO dto = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .comments(boardComment.getComments())
                .replyer(boardComment.getWriter())
                .commentModDate(boardComment.getModDate())
                .commentCount(commentCount.intValue())
                .category(boardCategory.getCategoryName())
                .build();
        return dto;
    }

    default BoardDTO entityToDtoSC(Board board,Long commentCount) {
        BoardDTO dto = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .hit(board.getHit())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writer(board.getUser().getId())
                .writerEmail(board.getUser().getEmail())
                .commentCount(commentCount.intValue())
                .category(board.getBoardCategory().getCategoryName())
                .build();
        return dto;
    }

}
