package dev.globalgoals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private Long hit;
    private LocalDateTime regDate, modDate;

    //댓글 추가
    private int commentCount; //해당 게시글의 댓글 수
}
