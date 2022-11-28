package com.hospital.review2.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    private String userName;
    private String password;
    private String email;

}