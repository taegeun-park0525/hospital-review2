package com.hospital.review2.service;

import com.hospital.review2.domain.User;
import com.hospital.review2.domain.dto.UserDto;
import com.hospital.review2.domain.dto.UserJoinRequest;
import com.hospital.review2.exception.ErrorCode;
import com.hospital.review2.exception.HospitalReviewAppException;
import com.hospital.review2.repository.UserRepository;
import com.hospital.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;
    private long expireTimeMs = 1000 * 60 * 60; // 1시간

    public UserDto join(UserJoinRequest request) {
        // 비즈니스 로직 - 회원 가입
        // 회원 userName(id) 중복 Check
        // 중복이면 회원가입 x --> Exception(예외)발생
        // 있으면 에러처리
        userRepository.findByUserName(request.getUserName())
                .ifPresent(user ->{
                    throw new HospitalReviewAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("UserName:%s", request.getUserName()));
                });

        // 회원가입 .save()
        User savedUser = userRepository.save(request.toEntity(encoder.encode(request.getPassword())));
        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .email(savedUser.getEmailAddress())
                .build();
    }

    public String login(String userName, String password) {

        // userName있는지 여부 확인
        // 없으면 NOT_FOUND에러 발생
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HospitalReviewAppException(ErrorCode.NOT_FOUND, String.format("%s는 가입된 적이 없습니다.", userName)));

        // password일치 하는지 여부 확인
        if(!encoder.matches(password, user.getPassword())){
            throw new HospitalReviewAppException(ErrorCode.INVALID_PASSWORD, String.format("userName 또는 password가 잘못 됐습니다."));
        }

        // 두가지 확인중 예외 안났으면 Token발행
        return JwtTokenUtil.createToken(userName, secretKey, expireTimeMs);
    }


}