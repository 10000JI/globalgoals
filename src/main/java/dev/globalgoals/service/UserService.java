package dev.globalgoals.service;

import dev.globalgoals.domain.Authority;
import dev.globalgoals.domain.User;
import dev.globalgoals.form.UserForm;
import dev.globalgoals.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public String join(UserForm form) {

        //Authority 객체를 생성하고, 권한 이름을 "ROLE_USER"로 설정
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .id(form.getId())
                .password(passwordEncoder.encode(form.getPassword()))
                .email(form.getEmail())
                .name(form.getName())
                .authorities(Collections.singleton(authority))
                .build();

        userRepository.save(user);
        return user.getId();
    }

    public boolean validateDuplicateMember(UserForm form, BindingResult bindingResult) {

        boolean checked = false;
        //checked가 true면 검증 발견
        //checked가 false면 검증 미발견

        checked = bindingResult.hasErrors();

        //id 중복 검증
        List<User> findId = userRepository.findById(form.getId());
        if (!findId.isEmpty()) {
            bindingResult.rejectValue("id", "user.id.notEqual");
            checked = true;
        }

        //password 일치 검증
        if (!form.getPasswordCheck().equals(form.getPassword())) {
            bindingResult.rejectValue("passwordCheck", "user.password.notEqual");
            checked = true;
        }

        //email 중복 검증
        List<User> findEmail = userRepository.findByEmail(form.getEmail());
        if (!findEmail.isEmpty()) {
            bindingResult.rejectValue("email", "user.email.notEqual");
            checked = true;
        }

        return checked;
    }
}
