package dev.globalgoals.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.*;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;
import dev.globalgoals.file.FileStore;
import dev.globalgoals.file.UploadFile;
import dev.globalgoals.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;

    private final BoardCommentRepository commentRepository;

    private final BoardImageRepository boardImageRepository;

    private final BoardCategoryRepository boardCategoryRepository;

    private final FileStore fileStore;

    @Override
    @Transactional
    public Long register(BoardDTO dto) throws IOException {
        BoardCategory category = boardCategoryRepository.getBoardCategoriesByCategoryName(dto.getCategory());
        dto.setCateNum(category.getId());

        Board entity = dtoToEntity(dto);

        boardRepository.save(entity);

        extracted(dto, entity);


        return entity.getId();
    }

    //dto->entity 이미지 저장 로직
    private void extracted(BoardDTO dto, Board entity) throws IOException {
        //파일은 데이터베이스가 아닌 스토리지에 저장
        //aws s3 같은 곳에 저장 클라우드에 올린 것을 어느 대의 컴퓨터건 볼 수 있게 할 수도 있다.
        List<UploadFile> storeImageFiles = fileStore.storeFiles(dto.getImageFiles());

        if(!storeImageFiles.isEmpty()){
            for (UploadFile storeImageFile : storeImageFiles) {

                BoardImage boardImage = BoardImage.builder()
                        .id(dto.getImgId())
                        .board(entity)
                        .oriName(storeImageFile.getUploadFileName())
                        .saveName(storeImageFile.getStoreFileName())
                        .build();

                boardImageRepository.save(boardImage);
            }
        }
    }

    @Override
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO, String param) {

        Function<Object[], BoardDTO> fn = (en -> entityToDto((Board)en[0],(User)en[1],(Long)en[2],(BoardCategory)en[3]));
        //Fuction은 함수이기 때문에 순서와는 무관함

//        Page<Object[]> result = boardRepository.getBoardWithCommentCount(
//                pageRequestDTO.getPageable(Sort.by("id").descending())  );
        Page<Object[]> result = boardRepository.searchPage(
                pageRequestDTO.getType(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getPageable(Sort.by("id").descending()),
                param
        );

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public BoardDTO get(Long id) {
        Object result = boardRepository.getBoardById(id);

        Object[] arr = (Object[])result;

        return entityToDto((Board)arr[0], (User)arr[1], (Long)arr[2],(BoardCategory) arr[3]);
    }

    @Override
    public List<UploadFile> getImage(Long id) {
        List<BoardImage> boardImageByBoard = boardImageRepository.getBoardImageByBoard(Board.builder().id(id).build());
        List<UploadFile> uploadFileList = boardImageByBoard.stream()
                .map(boardImage -> UploadFile.builder()
                        .uploadFileName(boardImage.getOriName())
                        .storeFileName(boardImage.getSaveName()).build())
                .collect(Collectors.toList());
        return uploadFileList;
   }

    @Transactional
    @Override
    public void removeWithComments(Long id) {
        //댓글부터 삭제
        commentRepository.deleteByBno(id);//게시판의 댓글 삭제는 JPQL 사용

        //이미지부터 삭제
        boardImageRepository.deleteByBno(id); //게시판의 이미지 삭제는 JPQL 사용

        boardRepository.deleteById(id); //게시글은 Spring data jpa 사용

    }

    @Transactional
    @Override
    public void modify(BoardDTO boardDTO, String[] storeFileNames) throws IOException {

        Board board = boardRepository.getOne(boardDTO.getId());

        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        if (storeFileNames != null) {
            for (String storeFileName : storeFileNames) {
                boardImageRepository.deleteBySaveName(storeFileName);
            }
        }

        // repository.save(board);

        extracted(boardDTO, board);
    }



}
