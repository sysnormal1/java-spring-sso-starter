package com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.dtos;

import lombok.Getter;
import lombok.Setter;

/**
 * the password recover dto
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Getter
@Setter
public class PasswordRecoverRequestDTO {
    private String email;
    private String passwordChangeInterfacePath;
}