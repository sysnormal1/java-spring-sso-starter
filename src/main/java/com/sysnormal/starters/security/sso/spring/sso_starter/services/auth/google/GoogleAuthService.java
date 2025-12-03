package com.sysnormal.starters.security.sso.spring.sso_starter.services.auth.google;


import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.User;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso.UsersRepository;
import com.sysnormal.starters.security.sso.spring.sso_starter.helpers.http.HttpUtils;
import com.sysnormal.starters.security.sso.spring.sso_starter.helpers.security.PasswordUtils;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.auth.google.GoogleAuthProperties;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.security.SecurityProperties;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.dtos.UserRequestDTO;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.google.dtos.HandleCodeDTO;
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
@EnableConfigurationProperties({GoogleAuthProperties.class, SecurityProperties.class})
@ConditionalOnProperty(prefix = "sso.auth.google", name = "enabled", havingValue = "true")
public class GoogleAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final ObjectMapper objectMapper;



    private GoogleAuthProperties properties;
    private SecurityProperties securityProperties;
    private AuthenticationService authenticationService;
    UsersRepository usersRepository;

    public GoogleAuthService(
            GoogleAuthProperties properties,
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
        result.data = GOOGLE_AUTH_URL + "?"
                + "client_id="+properties.getClientId()
                +"&response_type=code"
                +"&scope=openid%20email%20profile"
                +"&access_type=offline";
        result.success = true;
        return result;
    }



    public DefaultDataSwap handleCode(HandleCodeDTO handleCodeDTO) {
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            logger.debug("handling google code {}, redirectUri {}", handleCodeDTO.getCode(), handleCodeDTO.getRedirectUri());

            HashMap<String, String> body = new HashMap<String, String>();
            body.put("code", handleCodeDTO.getCode());
            body.put("client_id", properties.getClientId());
            body.put("client_secret", properties.getClientSecret());
            body.put("redirect_uri", handleCodeDTO.getRedirectUri()); // o mesmo usado no passo 1
            body.put("grant_type", "authorization_code");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(HttpUtils.buildQuery(body)))
                    .build();

            logger.debug("Request Body: {} ",HttpUtils.buildQuery(body));


            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            logger.debug("Response: {}", response.body());


            Map<String, Object> json = objectMapper.readValue(response.body(), Map.class);
            String idToken = (String) json.get("id_token");

            logger.debug("id_token: {}", idToken);


            Map<String,Object> tokenInfo = getTokenInfo(idToken, properties.getClientId());

            logger.debug("token info {}",tokenInfo);
            String email = (String) tokenInfo.getOrDefault("email","");
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
                throw new Exception("token info not contains email");
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    public Map<String,Object> getTokenInfo(String idToken, String expectedClientId) throws Exception {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Invalid token: " + response.body());
        }

        return objectMapper.readValue(response.body(), Map.class);

    }
}