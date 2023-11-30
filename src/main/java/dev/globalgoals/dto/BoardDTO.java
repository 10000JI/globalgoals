package dev.globalgoals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private String writerEmail;
    private Long hit;
    private LocalDateTime regDate, modDate;

    //댓글 추가
    private int commentCount; //해당 게시글의 댓글 수

    //이미지 다중 업로드
    private Long imgId;
    private List<MultipartFile> imageFiles;

    //카테고리
    private String category;
    private Long cateNum;
}
