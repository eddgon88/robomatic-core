package com.robomatic.core.v1.mappers;

import com.robomatic.core.v1.entities.TokenEntity;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.enums.TokenStatusEnum;
import com.robomatic.core.v1.models.SingUpRequest;
import com.robomatic.core.v1.services.PasswordEncryptionService;
import com.robomatic.core.v1.utils.RobomaticStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthMapper {

    private static final String PREFIX = "TKN";

    @Autowired
    private PasswordEncryptionService passwordEncryptionService;

    public UserEntity crateUserEntity(SingUpRequest singUpRequest) {
        return UserEntity.builder()
                .email(singUpRequest.getEmail())
                .enabled(false)
                .fullName(singUpRequest.getFistName() + " " + singUpRequest.getLastName())
                .encryptedPass(passwordEncryptionService.encryptPassword(singUpRequest.getPass()))
                .phone(singUpRequest.getPhone())
                .roleId(RoleEnum.VIEWER.getCode())
                .build();
    }

    public TokenEntity createTokenEntity(Integer userId) {
        return TokenEntity.builder()
                .token(RobomaticStringUtils.createRandomId(PREFIX))
                .creationDate(new Date())
                .expirationDate(RobomaticStringUtils.addHoursToDate(new Date(), 24))
                .userId(userId)
                .status(TokenStatusEnum.OPEN.getCode())
                .build();
    }

    /**
     * Crea un token para recuperación de contraseña (expira en 1 hora)
     */
    public TokenEntity createPasswordResetToken(Integer userId) {
        return TokenEntity.builder()
                .token(RobomaticStringUtils.createRandomId("RST"))
                .creationDate(new Date())
                .expirationDate(RobomaticStringUtils.addHoursToDate(new Date(), 1)) // 1 hora
                .userId(userId)
                .status(TokenStatusEnum.OPEN.getCode())
                .build();
    }

}
