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
public class BoardCommentDTO {

    private Long rno;
    private String comments;
    private String replyer;
    private Long bno; //게시글 번호
    private LocalDateTime regDate, modDate;
}
