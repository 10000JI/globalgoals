package dev.globalgoals.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * (Repository -> 화면으로 전달)
 * Entity -> Dto 변환 작업
 */
@Data
public class PageResultDTO<DTO,EN>{
    // DTO 목록 저장
    private List<DTO> dtoList;

    // 총 페이지 번호
    private int totalPage;

    // 현재 페이지 번호
    private int page;

    // 목록 사이즈
    private int size;

    // 시작 페이지 번호, 끝 페이지 번호
    private int start, end;

    // 이전, 다음
    private boolean prev, next;

    // 페이지 번호 목록
    private  List<Integer> pageList;

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn){
        // 매개변수에서 전달받은 함수 (fn) 를 이용해서 (Entity -> DTO) 결과를 매핑해주고 collect 로 모아서 dtoList로 처리
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        // 총 페이지 수 설정
        totalPage = result.getTotalPages();
        // 페이지 번호 목록 생성
        makePageList(result.getPageable());

    }

    private void makePageList(Pageable pageable) {
        this.page = pageable.getPageNumber() + 1; // 0부터 시작하므로 1 추가
        this.size = pageable.getPageSize();

        int tempEnd = (int) (Math.ceil(page / 10.0)) * 10;
        start = tempEnd - 9;
        prev = start > 1;
        end = totalPage > tempEnd ? tempEnd : totalPage;
        next = totalPage > tempEnd;

        // 페이지 번호 목록 생성 (start부터 end까지)
        /// boxed(): IntStream 을 Integer 로 변환하여 List<Integer> pageList 에 수집 가능하도록 래퍼 객체로 바꿔준다
        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }
}
