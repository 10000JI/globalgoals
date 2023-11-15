package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.BoardDto;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    public Long register(BoardDto dto) {
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
    public PageResultDTO<BoardDto, Board> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("id").descending());
        Page<Board> result = boardRepository.findAll(pageable);
        Function<Board, BoardDto> fn = (entity -> entityToDto(entity));
        return new PageResultDTO<>(result, fn);
    }
}
