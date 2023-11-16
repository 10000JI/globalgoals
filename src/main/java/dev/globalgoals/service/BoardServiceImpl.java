package dev.globalgoals.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.QBoard;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.BoardDTO;
import dev.globalgoals.dto.PageRequestDTO;
import dev.globalgoals.dto.PageResultDTO;
import dev.globalgoals.repository.BoardRepository;
import dev.globalgoals.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    public Long register(BoardDTO dto) {
        log.info("DTO-------------------------");
        log.info(String.valueOf(dto));
        Optional<User> userOptional = userRepository.findById(dto.getWriter());
        // 만약 사용자가 존재한다면 해당 사용자 정보를 이용하여 Board 엔터티를 생성하고 저장합니다.
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Board entity = dtoToEntity(dto, user);
            log.info(String.valueOf(entity));
            boardRepository.save(entity);
            return entity.getId();
        } else {
            // 사용자가 존재하지 않으면 예외 처리 또는 다른 처리를 수행할 수 있습니다.
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + dto.getWriter());
        }
    }

    @Override
    public PageResultDTO<BoardDTO, Board> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("id").descending());
        
        BooleanBuilder booleanBuilder = getSearch(requestDTO); // 검색 조건 처리
        
        Page<Board> result = boardRepository.findAll(booleanBuilder, pageable); // Querydsl 사용
        
        Function<Board, BoardDTO> fn = (entity -> entityToDto(entity));
        
        return new PageResultDTO<>(result, fn);
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO) {

        String type = requestDTO.getType(); // PageRequestDTO 에서 타입 정보를 얻어온다.

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // BooleanBuilder 객체 생성

        if (type == null || type.trim().length() == 0) { // 검색 조건이 없는 경우
            return booleanBuilder;
        }

        QBoard qBoard = QBoard.board; // Q도메인 객체 생성

        String keyword = requestDTO.getKeyword(); // PageRequestDTO 에서 키워드 정보를 얻어온다.

        BooleanExpression expression = qBoard.id.gt(0L); // id > 0 조건만 생성

        booleanBuilder.and(expression); // BooleanBuilder에 조건 추가


        // 검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if (type.contains("t")) {
            conditionBuilder.or(qBoard.title.contains(keyword));
        }
        if (type.contains("c")) {
            conditionBuilder.or(qBoard.content.contains(keyword));
        }
        if (type.contains("w")) { //w -> writer(작성자)
            conditionBuilder.or(qBoard.member.id.contains(keyword));
        }

        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }

    @Override
    public BoardDTO read(Long id) {
        Optional<Board> result = boardRepository.findById(id);
        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    @Override
    public void modify(BoardDTO dto) {
        // 업데이트 하는 항목은 '제목', '내용'
        Optional<Board> result = boardRepository.findById(dto.getId());
        if (result.isPresent()) {
            Board entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            boardRepository.save(entity);
        }
    }

    @Override
    public void remove(Long id) {
        boardRepository.deleteById(id);
    }


}
