package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.entities.TokenEntity;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.models.SingUpRequest;
import com.robomatic.core.v1.utils.RobomaticStringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthMapper {

    private static final String PREFIX = "TKN";

    public UserEntity crateUserEntity(SingUpRequest singUpRequest) {
        return UserEntity.builder()
                .email(singUpRequest.getEmail())
                .enabled(false)
                .fullName(singUpRequest.getFistName() + " " + singUpRequest.getLastName())
                .pass(singUpRequest.getPass())
                .phone(singUpRequest.getPhone())
                .roleId(RoleEnum.ANALYST.getCode())
                .build();
    }

    public TokenEntity createTokenEntity(Integer userId) {
        Date now = new Date();
        return TokenEntity.builder()
                .token(RobomaticStringUtils.createRandomId(PREFIX))
                .creationDate(now)
                .expirationDate(RobomaticStringUtils.addHoursToDate(now, 24))
                .userId(userId)
                .build();
    }

}
