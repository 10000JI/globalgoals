package dev.globalgoals.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageDTO {

    @NotBlank(message = "아이디: 필수 정보입니다.")
    private String id;

    @NotBlank(message = "이름: 필수 정보입니다.")
    private String name;

    @NotBlank(message = "이메일: 필수 정보입니다.")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

}
