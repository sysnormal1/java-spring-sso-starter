package com.sysnormal.starters.security.sso.spring.sso_starter.services.auth.github;

import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.User;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso.UsersRepository;
import com.sysnormal.starters.security.sso.spring.sso_starter.helpers.http.HttpUtils;
import com.sysnormal.starters.security.sso.spring.sso_starter.helpers.security.PasswordUtils;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.auth.github.GitHubAuthProperties;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.security.SecurityProperties;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.dtos.UserRequestDTO;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.github.dtos.HandleCodeDTOG;
import com.sysnormal.starters.security.sso.spring.sso_starter.services.auth.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@EnableConfigurationProperties({GitHubAuthProperties.class, SecurityProperties.class})
@ConditionalOnProperty(prefix = "sso.auth.github", name = "enabled", havingValue = "true")
public class GitHubAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAuthService.class);

    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";
    private static final String GITHUB_USER_EMAIL_URL = "https://api.github.com/user/emails";

    private final ObjectMapper objectMapper;


    private GitHubAuthProperties properties;
    private SecurityProperties securityProperties;
    private AuthenticationService authenticationService;
    private UsersRepository usersRepository;

    public GitHubAuthService(
            GitHubAuthProperties properties,
            SecurityProperties securityProperties,
            AuthenticationService authenticationService,
            UsersRepository usersRepository,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.securityProperties = securityProperties;
        this.authenticationService = authenticationService;
        this.usersRepository = usersRepository;
        this.objectMapper = objectMapper;
    }

    public DefaultDataSwap getLoginUrl() {
        DefaultDataSwap result = new DefaultDataSwap();
        result.data = GITHUB_AUTH_URL + "?"
                + "client_id=" + properties.getClientId()
                + "&scope=user:email"
                + "&redirect_uri=" + properties.getRedirectUri();
        result.success = true;
        return result;
    }

    public DefaultDataSwap handleCode(HandleCodeDTOG handleCodeDTO) {
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            logger.debug("handling GitHub code {}, redirectUri {}", handleCodeDTO.getCode(), handleCodeDTO.getRedirectUri());

            // Trocar o código pelo access token
            String accessToken = exchangeCodeForToken(handleCodeDTO);

            logger.debug("Successfully obtained access token");

            // Obter informações do usuário
            Map<String, Object> userInfo = getUserInfo(accessToken);

            logger.debug("User info: {}", userInfo);

            // Obter email (pode estar em um endpoint separado se não estiver público)
            String email = getEmail(userInfo, accessToken);

            if (StringUtils.hasText(email)) {
                Optional<User> user = usersRepository.findByEmail(email.trim().toLowerCase());
                if (user.isPresent()) {
                    result = authenticationService.getAuthDataResult(user, false, null, null, true, null);
                } else {
                    String password = PasswordUtils.generateCompliantPassword(email, securityProperties.getPasswordRules());
                    UserRequestDTO userRequestDTO = new UserRequestDTO();
                    userRequestDTO.setEmail(email);
                    userRequestDTO.setPassword(password);
                    result = authenticationService.register(userRequestDTO);
                }
            } else {
                throw new Exception("User info does not contain email");
            }
        } catch (Exception e) {
            result.setException(e);
            logger.error("Error handling GitHub code", e);
        }
        return result;
    }

    private String exchangeCodeForToken(HandleCodeDTOG handleCodeDTO) throws Exception {
        HashMap<String, String> body = new HashMap<>();
        body.put("client_id", properties.getClientId());
        body.put("client_secret", properties.getClientSecret());
        body.put("code", handleCodeDTO.getCode());
        body.put("redirect_uri", handleCodeDTO.getRedirectUri());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_TOKEN_URL))
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(HttpUtils.buildQuery(body)))
                .build();

        logger.debug("Request to GitHub token URL");

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        logger.debug("GitHub token response: {}", response.body());

        Map<String, Object> json = objectMapper.readValue(response.body(), Map.class);

        if (json.containsKey("error")) {
            throw new Exception("GitHub OAuth error: " + json.get("error_description"));
        }

        return (String) json.get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_USER_URL))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get user info: " + response.body());
        }

        return objectMapper.readValue(response.body(), Map.class);
    }

    private String getEmail(Map<String, Object> userInfo, String accessToken) throws Exception {
        // Primeiro tenta pegar o email direto do userInfo
        String email = (String) userInfo.get("email");

        if (StringUtils.hasText(email)) {
            return email;
        }

        // Se não tiver email público, busca no endpoint de emails
        logger.debug("Email not public, fetching from emails endpoint");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_USER_EMAIL_URL))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get user emails: " + response.body());
        }

        // GitHub retorna uma lista de emails
        Object[] emails = objectMapper.readValue(response.body(), Object[].class);

        // Procura pelo email primário e verificado
        for (Object emailObj : emails) {
            Map<String, Object> emailData = (Map<String, Object>) emailObj;
            Boolean primary = (Boolean) emailData.get("primary");
            Boolean verified = (Boolean) emailData.get("verified");

            if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                return (String) emailData.get("email");
            }
        }

        // Se não encontrar email primário, pega o primeiro verificado
        for (Object emailObj : emails) {
            Map<String, Object> emailData = (Map<String, Object>) emailObj;
            Boolean verified = (Boolean) emailData.get("verified");

            if (Boolean.TRUE.equals(verified)) {
                return (String) emailData.get("email");
            }
        }

        throw new Exception("No verified email found for GitHub user");
    }
}