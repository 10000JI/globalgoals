package dev.globalgoals.service;

import dev.globalgoals.domain.Board;
import dev.globalgoals.domain.Goal;
import dev.globalgoals.domain.StampCard;
import dev.globalgoals.dto.*;
import dev.globalgoals.repository.StampCardRepository;
import dev.globalgoals.security.UserDetailsConfig;
import dev.globalgoals.domain.User;
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
        Optional<User> userEmail = userRepository.findByEmail(form.getEmail());
        if (userEmail.isPresent()) {
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
        Optional<User> admin = userRepository.findById(donationDTO.getUserId());
        if (admin.isPresent()) {
            admin.get().plusDonatedPoints(donationDTO.getDonatedPoints());
            return "success";
        }
        return "fail";
    }

    /**
     * 사용자 -> 관리자로 포인트 수집
     */
    @Override
    @Transactional
    public String collectionManagerPoint(DonationDTO donationDTO) {
        Optional<User> user = userRepository.findById(donationDTO.getUserId());
        if (user.isPresent()) {
            //user의 donatedPoint 필드는 1700->0 으로 만들고,  countDonation 필드를 +1 해준다
            //(기부했으니 포인트는 0이 되나, 기부횟수는 +1된다.)
            user.get().minusDonatedPoint(donationDTO.getDonatedPoints());
            user.get().plusCountDonation();

            //user의 stampCard인 checkNum 필드도 모두 1이었으나 0으로 변경하여 초기상태로 돌아간다.
            Optional.ofNullable(stampCardRepository.getStampCardByUser_Id(donationDTO.getUserId()))
                    .ifPresent(cards -> cards.stream().forEach(StampCard::minusCheckNum));

            //user의 포인트는 manager에게 전달되어 쌓이게 된다.
            userRepository.findById("manager").get().plusDonatedPoints(donationDTO.getDonatedPoints());

            return "success";
        }
        return "fail";
    }

    /**
     *마이페이지
     */
    @Override
    public UserDTO getUserInfo(Principal principal) {
        Optional<User> user = userRepository.findById(principal.getName());
        return entityToDto(null, user.get());
    }


    /**
     * 마이페이지 수정 폼 검증
     */
    @Override
    public boolean validateDuplicateMyPage(MyPageDTO dto, BindingResult bindingResult , Principal principal) {

        boolean checked = false;
        //checked가 true면 검증 발견
        //checked가 false면 검증 미발견

        checked = bindingResult.hasErrors();

        //id는 변경되면 안됨 -> dto와 principle id 일치 여부 확인
        if (!principal.getName().equals(dto.getId())) {
            bindingResult.rejectValue("id", "user.id.notEqual2");
            checked = true;
        }

        Optional<User> user = userRepository.findById(principal.getName());
        //email 중복 검증
        Optional<User> findEmail = userRepository.findByEmail(dto.getEmail());
        if (findEmail.isPresent() && !user.get().getEmail().equals(dto.getEmail())) {
            bindingResult.rejectValue("email", "user.email.notEqual");
            checked = true;
        }


        return checked;
    }

    /**
     * 먀이페이지 프로필 수정
     */
    @Override
    @Transactional
    public void userInfoModify(MyPageDTO dto) {
        Optional<User> user = userRepository.findById(dto.getId());
        user.get().changeName(dto.getName());
        user.get().changeEmail(dto.getEmail());
        // save 메서드는 생략 가능 (영속성 컨텍스트)
    }

    /**
     * 먀이페이지 비밀번호 검증
     */
    @Override
    public boolean validateDuplicatePw(MyPwDTO dto, BindingResult bindingResult, Principal principal) {

        boolean checked = false;
        //checked가 true면 검증 발견
        //checked가 false면 검증 미발견

        checked = bindingResult.hasErrors();

        //사용자가 입력한 password와 db안에 암호화된 password와 동일하지 않다면 검증 발견
        if(!passwordEncoder.matches(dto.getPassword(), userRepository.findById(principal.getName()).get().getPassword())){
            bindingResult.rejectValue("password", "user.password.notEqual2");
            checked = true;
        }

        // 새 비밀번호와 비밀번호 확인 일치 검증
        if (!dto.getPasswordCheck().equals(dto.getNewPassword())) {
            bindingResult.rejectValue("passwordCheck", "user.password.notEqual3");
            checked = true;
        }

        return checked;
    }

    /**
     * 마이페이지 비밀번호 수정
     */
    @Override
    @Transactional
    public void userPwModify(MyPwDTO dto, Principal principal) {
        Optional<User> user = userRepository.findById(principal.getName());
        user.get().changePw(passwordEncoder.encode(dto.getNewPassword()));
    }
//
//    //id는 변경되면 안됨 -> dto와 principle id 일치 여부 확인
//        if (principal.getName().equals(dto.getId())) {
//        //html에서 뿌린 dto의 id와 principal의 name(=id)와 동일할 시에 패스워드 검증 ( 검증이 성공하면 데이터 변경이 이루어지므로 검증 할때 동일 사용자인지 체크하자 )
//    }

//    /**
//     * 이메일 인증 본인 검증
//     */
//    @Override
//    public boolean validateEmail(String email) {
//
//        boolean checked = false;
//        //checked가 true면 검증 발견
//        //checked가 false면 검증 미발견
//
//        if (!userRepository.findByEmail(email).get().getEmail().equals(email)) {
//            checked = true;
//        }
//
//        return checked;
//    }

}
