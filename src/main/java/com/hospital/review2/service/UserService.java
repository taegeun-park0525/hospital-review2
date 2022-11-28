package com.hospital.review2.service;

import com.hospital.review2.domain.dto.UserDto;
import com.hospital.review2.domain.dto.UserJoinRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public UserDto join(UserJoinRequest request) {
        return new UserDto("", "", "");
    }
}