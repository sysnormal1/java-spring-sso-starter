package com.sysnormal.starters.security.sso.spring.sso_starter.server.auth;

import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.dtos.*;
import com.sysnormal.starters.security.sso.spring.sso_starter.services.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller wich provide endpoints to auth processes
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * the auth service
     */
    @Autowired
    private AuthenticationService authenticationService;


    /**
     * login
     *
     * @param agentRequestDTO the agent parameters
     * @return response entity
     */
    @PostMapping("/login")
    public ResponseEntity<DefaultDataSwap> login(@RequestBody(required = false) AgentRequestDTO agentRequestDTO) {
        logger.debug("requested login {}",agentRequestDTO != null ? agentRequestDTO.getIdentifier() : null);
        return authenticationService.login(agentRequestDTO).sendHttpResponse();
    }

    /**
     * register
     *
     * @param agentRequestDTO the agent parameters
     * @return response entity
     */
    @PostMapping("/register")
    public ResponseEntity<DefaultDataSwap> register(@RequestBody(required = false) AgentRequestDTO agentRequestDTO) {
        logger.debug("requested register {}",agentRequestDTO != null ? agentRequestDTO.getIdentifier() : null);
        return authenticationService.register(agentRequestDTO).sendHttpResponse();
    }

    /**
     * check_token
     *
     * @param tokenDto the token parameters
     * @return response entity
     */
    @PostMapping("/check_token")
    public ResponseEntity<DefaultDataSwap> checkToken(@RequestBody(required = false) TokenRequestDTO tokenDto) {
        logger.debug("requested check_token {}",tokenDto != null ? tokenDto.getToken() : null);
        return authenticationService.checkTokenFromDto(tokenDto).sendHttpResponse();
    }

    /**
     * refreshToken
     *
     * @param refreshTokenDto the refresh token parameters
     * @return response entity
     */
    @PostMapping("/refresh_token")
    public ResponseEntity<DefaultDataSwap> refreshToken(@RequestBody(required = false) RefreshTokenRequestDTO refreshTokenDto) {
        logger.debug("requested refresh_token {}",refreshTokenDto != null ? refreshTokenDto.getRefreshToken() : null);
        return authenticationService.refreshTokenFromDto(refreshTokenDto).sendHttpResponse();
    }

    /**
     * sendEmailRecoverPassword
     *
     * @param passwordRecoverRequestDTO the password recover parameters
     * @return response entity
     */
    @PostMapping("/send_email_recover_password")
    public ResponseEntity<DefaultDataSwap> sendEmailRecoverPassword(@RequestBody(required = false) PasswordRecoverRequestDTO passwordRecoverRequestDTO) {
        logger.debug("requested send_email_recover_password {}",passwordRecoverRequestDTO != null ? passwordRecoverRequestDTO.getIdentifier() + "," +  passwordRecoverRequestDTO.getPasswordChangeInterfacePath() : null);
        return authenticationService.sendEmailRecoverPasswordFromDto(passwordRecoverRequestDTO).sendHttpResponse();
    }

    /**
     * password_change
     *
     * @param passwordChangeRequestDTO the password change parameters
     * @return response entity
     */
    @PostMapping("/password_change")
    public ResponseEntity<DefaultDataSwap> passwordChange(@RequestBody(required = false) PasswordChangeRequestDTO passwordChangeRequestDTO) {
        logger.debug("requested password_change {}",passwordChangeRequestDTO != null ? passwordChangeRequestDTO.getToken() : null);
        return authenticationService.passwordChangeFromDto(passwordChangeRequestDTO).sendHttpResponse();
    }
}
