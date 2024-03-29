package dev.globalgoals.dto;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @NotBlank(message = "아이디: 필수 정보입니다.")
    private String id;

    @NotBlank(message = "이름: 필수 정보입니다.")
    private String name;

    @NotBlank(message = "비밀번호: 필수 정보입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인: 필수 정보입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String passwordCheck;

    @NotBlank(message = "이메일: 필수 정보입니다.")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "이메일체크: 필수 정보입니다.")
    private String emailCheck;


    // 스탬프 카드 checknum이 1일때 몇개인지 count
    private Long stampCardCount;

    private Long countDonation;
    private Long donatedPoints;
}
