package com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.github;

import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.github.dtos.HandleCodeDTOG;
import com.sysnormal.starters.security.sso.spring.sso_starter.services.auth.github.GitHubAuthService;
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
@RequestMapping("/auth/github")
@ConditionalOnProperty(prefix = "sso.auth.github", name = "enabled", havingValue = "true")
public class GitHubAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAuthController.class);

    @Autowired
    private GitHubAuthService gitHubAuthService;

    /**
     * Get GitHub login URL
     *
     * @return response entity with login URL
     */
    @PostMapping("/get_login_url")
    public ResponseEntity<DefaultDataSwap> getLoginUrl() {
        logger.debug("requested get_login_url for GitHub");
        return gitHubAuthService.getLoginUrl().sendHttpResponse();
    }

    /**
     * Handle OAuth callback code
     *
     * @param handleCodeDto DTO containing code and redirect URI
     * @return response entity with authentication result
     */
    @PostMapping("/handle_code")
    public ResponseEntity<?> handleCode(@RequestBody HandleCodeDTOG handleCodeDto) {
        logger.debug("requested handle_code for GitHub");
        return gitHubAuthService.handleCode(handleCodeDto).sendHttpResponse();
    }
}