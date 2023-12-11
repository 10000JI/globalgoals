package dev.globalgoals.service;

import dev.globalgoals.domain.*;
import dev.globalgoals.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

public interface UserService {
    String join (UserDTO userDto);

    List<StampCardWithGoalDTO> getStampCardWithGoal(Principal principal);

    boolean validateDuplicateMember(UserDTO form, BindingResult bindingResult);

    UserDTO getUserAndStampCount(Principal principal);

    String chargeAdminPoint(DonationDTO donationDTO);

    String collectionManagerPoint(DonationDTO donationDTO);

    UserDTO getUserInfo(Principal principal);

    boolean validateDuplicateMyPage(MyPageDTO myPageDTO, BindingResult bindingResult, Principal principal);

    void userInfoModify(MyPageDTO dto);

    boolean validateDuplicatePw(MyPwDTO myPwDTO, BindingResult bindingResult, Principal principal);

    void userPwModify(MyPwDTO dto, Principal principal);

//    boolean validateEmail(String email);

    default User dtoToUserEntity(UserDTO dto, PasswordEncoder passwordEncoder) {

        //Authority 객체를 생성하고, 권한 이름을 "ROLE_USER"로 설정
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User entity = User.builder()
                .id(dto.getId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .name(dto.getName())
                .authorities(Collections.singleton(authority))
                .countDonation(dto.getCountDonation())
                .donatedPoints(dto.getDonatedPoints())
                .build();
        return entity;
    }

    default StampCardWithGoalDTO entityToStampCardWithGoalDTO(Goal goal, StampCard stampCard) {
        StampCardWithGoalDTO dto = StampCardWithGoalDTO.builder()
                .stampId(stampCard.getId())
                .userId(stampCard.getUser().getId())
                .checkNum(stampCard.getCheckNum())
                .goalId(goal.getGoalId())
                .goalTitle(goal.getGoalTitle())
                .build();
        return dto;
    }

    default UserDTO entityToDto(Long stampCardCount, User user) {
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .stampCardCount(stampCardCount)
                .donatedPoints(user.getDonatedPoints())
                .countDonation(user.getCountDonation())
                .build();
        return dto;
    }

}
