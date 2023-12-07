package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.Goal;
import dev.globalgoals.domain.StampCard;
import dev.globalgoals.dto.DonationDTO;
import dev.globalgoals.dto.StampCardWithGoalDTO;
import dev.globalgoals.repository.StampCardRepository;
import dev.globalgoals.security.UserDetailsConfig;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final StampCardRepository stampCardRepository;

    /**
     * 회원가입
     */
    @Transactional
    @Override
    public String join(UserDTO userDto) {

        userDto.setCountDonation(0L); //Use에 Default값 써줬으나 dto로 가져오는 과정 중에 null 일 수 있으니 0으로 세팅
        userDto.setDonatedPoints(0L); //Use에 Default값 써줬으나 dto로 가져오는 과정 중에 null 일 수 있으니 0으로 세팅
        User user = dtoToUserEntity(userDto, passwordEncoder);
        userRepository.save(user);

        // 스탬프 판 17개 목표, 체크 0 으로 insert
        List<Goal> goals = userRepository.findAllGoals();
        goals.forEach(goal -> {
            StampCard stampCard = StampCard.builder()
                    .user(userRepository.findById(user.getId()).orElseThrow()) // 영속 상태의 User를 참조
                    .goal(goal)
                    .checkNum(0)
                    .build();
            stampCardRepository.save(stampCard);
        });;
        return user.getId();
    }

    /**
     * 회원가입 검증
     */
    @Override
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

    /**
     * 마이페이지 스탬프판
     */
    @Override
    public List<StampCardWithGoalDTO> getStampCardWithGoal(Principal principal) {
        List<Object[]> result = userRepository.stampFindById(principal.getName());

        List<StampCardWithGoalDTO> stampCardWithGoals = result.stream()
                .map(en -> entityToStampCardWithGoalDTO((Goal) en[0], (StampCard) en[1]))
                .collect(Collectors.toList());
        return stampCardWithGoals;
    }

    /**
     * 마이페이지 유저 정보 + 스탬프 갯수 + 17개 모두 채울 시 포인트 1700점 환급
     */
    @Override
    @Transactional
    public UserDTO getUserAndStampCount(Principal principal) {
        Optional<User> byId = userRepository.findById(principal.getName());
        return entityToDto(stampCardRepository.countByCheckNum(principal.getName()), byId.get());
    }

    /**
     * 관리자 포인트 충전
     */
    @Override
    @Transactional
    public String chargeAdminPoint(DonationDTO donationDTO) {
        Optional<User> byId = userRepository.findById(donationDTO.getUserId());
        if (byId.isPresent()) {
            byId.get().plusDonatedPoints(donationDTO.getDonatedPoints());
            return "success";
        }
        return "fail";
    }

}
