package dev.globalgoals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Controller -> Service로 전달
 */
@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
    private int page;
    private int size;
    private String type;
    private String keyword;
    public PageRequestDTO(){
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort) { // 정렬은 다양한 상황에 쓰기 위해 별도의 파라미터를 받아서 사용하도록 설계
        return PageRequest.of(page - 1, size, sort); // 0 부터 시작
    }
}
