package com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.google;

import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.google.dtos.HandleCodeDTO;
import com.sysnormal.starters.security.sso.spring.sso_starter.services.auth.google.GoogleAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
@ConditionalOnProperty(prefix = "sso.auth.google", name = "enabled", havingValue = "true")
public class GoogleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);


    @Autowired
    private GoogleAuthService googleAuthService;


    /**
     * register
     *
     * @return response entity
     */
    @PostMapping("/get_login_url")
    public ResponseEntity<DefaultDataSwap> getLoginUrl() {
        logger.debug("requested get_login_url");
        return googleAuthService.getLoginUrl().sendHttpResponse();
    }

    @PostMapping("/handle_code")
    public ResponseEntity<?> handleCode(@RequestBody(required = false) HandleCodeDTO handlecodeDto) {
        logger.debug("requested handle_code");
        return googleAuthService.handleCode(handlecodeDto).sendHttpResponse();
    }
}
