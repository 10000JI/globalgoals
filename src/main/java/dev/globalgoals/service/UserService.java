package dev.globalgoals.service;

import dev.globalgoals.domain.StampCard;
import dev.globalgoals.security.UserDetailsConfig;
import dev.globalgoals.domain.Authority;
import dev.globalgoals.domain.User;
import dev.globalgoals.dto.UserDTO;
import dev.globalgoals.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public String join(UserDTO userDto) {

        //Authority 객체를 생성하고, 권한 이름을 "ROLE_USER"로 설정
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .id(userDto.getId())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .name(userDto.getName())
                .authorities(Collections.singleton(authority))
                .build();
        userRepository.save(user);

        // 스탬프 판 17개 목표, 체크 0 으로 insert
        IntStream.range(0, userRepository.findAllGoals().size())
                .forEach(i -> {
                    StampCard stampCard = StampCard.builder()
                            .user(user)
                            .goal(userRepository.findAllGoals().get(i))
                            .checkNum(0)
                            .build();
                    userRepository.stampSave(stampCard);
                });
        return user.getId();
    }

    /**
     * 회원가입 검증
     */
    public boolean validateDuplicateMember(UserDTO form, BindingResult bindingResult) {

        boolean checked = false;
        //checked가 true면 검증 발견
        //checked가 false면 검증 미발견

        checked = bindingResult.hasErrors();

        //id 중복 검증
        Optional<User> byId = userRepository.findById(form.getId());
        if (!byId.isEmpty()) {
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

    /**
     * UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.allFindById(id);
        if (user != null) {
            return new UserDetailsConfig(user);
        } else {
            return null;
        }
    }

}
