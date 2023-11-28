package dev.globalgoals.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.*;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;
import dev.globalgoals.file.FileStore;
import dev.globalgoals.file.UploadFile;
import dev.globalgoals.repository.BoardCommentRepository;
import dev.globalgoals.repository.BoardImageRepository;
import dev.globalgoals.repository.BoardRepository;
import dev.globalgoals.repository.UserRepository;
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

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;

    private final BoardCommentRepository commentRepository;

    private final BoardImageRepository boardImageRepository;

    private final FileStore fileStore;

    @Override
    @Transactional
    public Long register(BoardDTO dto) throws IOException {
        Board entity = dtoToEntity(dto);

        boardRepository.save(entity);

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


        return entity.getId();
    }

    @Override
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {

        Function<Object[], BoardDTO> fn = (en -> entityToDto((Board)en[0],(User)en[1],(Long)en[2]));
        //Fuction은 함수이기 때문에 순서와는 무관함 (하단에 상세 내용)

//        Page<Object[]> result = boardRepository.getBoardWithCommentCount(
//                pageRequestDTO.getPageable(Sort.by("id").descending())  );
        Page<Object[]> result = boardRepository.searchPage(
                pageRequestDTO.getType(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getPageable(Sort.by("id").descending())
        );

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public BoardDTO get(Long id) {
        Object result = boardRepository.getBoardById(id);

        Object[] arr = (Object[])result;

        return entityToDto((Board)arr[0], (User)arr[1], (Long)arr[2]);
    }

    @Transactional
    @Override
    public void removeWithComments(Long id) {
        //댓글 부터 삭제
        commentRepository.deleteByBno(id);//댓글은 JPQL 사용

        boardRepository.deleteById(id); //게시글은 Spring data jpa 사용

    }

    @Transactional
    @Override
    public void modify(BoardDTO boardDTO) {

        Board board = boardRepository.getOne(boardDTO.getId());

        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        // repository.save(board);
    }



}
