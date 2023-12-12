package dev.globalgoals.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.*;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.CertifyDTO;
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
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private final UserRepository userRepository;

    private final StampCardRepository stampCardRepository;

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
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO, String param, Principal principal) {

        Function<Object[], BoardDTO> fn;

        if (param.equals("comment")) { //내가 쓴 댓글 리스트 볼 시에 불러올 dto
            fn = (en -> entityToDtoBC((Board) en[0], (BoardComment) en[1],(Long)en[2], (BoardCategory)en[3]));
        } else if (param.equals("scrap")) { //스크랩한 게시물 볼 시에 불러올 dto
            fn = (en -> entityToDtoSC((Board)en[0]));
        }else{ //그 외의 경우
            fn = (en -> entityToDto((Board)en[0],(User)en[1],(Long)en[2],(BoardCategory)en[3]));
            //Fuction은 함수이기 때문에 순서와는 무관함
        }

        // Pageable 및 Sort 생성
        Pageable pageable;
        if (param.equals("popularity")) { //인기글
            pageable = pageRequestDTO.getPageable(Sort.by(
                    Sort.Order.desc("hit"),
                    Sort.Order.desc("id")
            ));
        } else { //인기글 외 다른 글
            pageable = pageRequestDTO.getPageable(Sort.by("id").descending());
        }

        Page<Object[]> result = boardRepository.searchPage(
                pageRequestDTO.getType(),
                pageRequestDTO.getKeyword(),
                pageable,
                param,
                principal
        );

        return new PageResultDTO<>(result, fn);
    }

    @Override
    @Transactional
    public BoardDTO get(Long id) {

        Object result = boardRepository.getBoardById(id);

        Object[] arr = (Object[])result;

        Board board = (Board) arr[0];

        board.plusHit();

        return entityToDto(board, (User)arr[1], (Long)arr[2],(BoardCategory) arr[3]);
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

        //게시물 제목, 내용 수정
        Board board = boardRepository.getOne(boardDTO.getId());

        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());

        //게시물 카테고리 수정
        BoardCategory category = boardCategoryRepository.getBoardCategoriesByCategoryName(boardDTO.getCategory());

        board.changeBoardCategory(category);

        if (storeFileNames != null) {
            for (String storeFileName : storeFileNames) {
                boardImageRepository.deleteBySaveName(storeFileName);
            }
        }

        // repository.save(board);

        extracted(boardDTO, board);
    }

    @Transactional
    @Override
    public String saveScrap(Long id, Principal principal) {

        // 기존 사용자 정보 가져오기
        User existingUser = userRepository.findById(principal.getName()).orElseThrow();

        // 새로운 Board 생성
        Board board = Board.builder().id(id).build();

        // 이미 스크랩된 게시물인지 확인
        if (!existingUser.getScrap().contains(board)) { //contains 메서드가 호출되면, Set은 내부적으로 equals 메서드를 사용하여 중복 여부를 판단
            // 스크랩 정보 추가
            existingUser.getScrap().add(board);

            // 사용자 정보 저장 (기존 정보는 변경되지 않음)
            userRepository.save(existingUser);

            return "success";
        } else {
            return "fail";
        }
    }

    @Transactional
    @Override
    public String saveCertify(CertifyDTO certifyDTO) {

        StampCard stampCard = stampCardRepository.findByUser_IdAndGoal_GoalId(certifyDTO.getWriter(), certifyDTO.getGoalNum()); // 인증하려고 하는 게시판 유저의 stamp_card 정보 가져오기

        // 해당 목표번호가 스탬프에 찍혔는지 확인 (0이면 미인증 상태)
        if (stampCard.getCheckNum() == 0) {
            stampCard.plusCheckNum(); // checkNum 을 1로 변경
            if(stampCardRepository.countByCheckNum(certifyDTO.getWriter()) == 17L){
                Optional<User> admin = userRepository.findById("admin");
                admin.get().minusDonatedPoint(1700L);

                Optional<User> byId = userRepository.findById(certifyDTO.getWriter());
                byId.get().changeDonatedPoint(1700L);
            }

            return "success";
        } else {
            return "fail";
        }
    }

}
