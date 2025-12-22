package com.sysnormal.starters.security.sso.spring.sso_starter.services.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.IdentifierType;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.RecordStatus;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.Agent;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso.AgentsRepository;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.security.SecurityProperties;
import com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.dtos.*;
import com.sysnormal.starters.security.sso.spring.sso_starter.services.jwt.JwtService;
import com.sysnormal.starters.security.sso.spring.sso_starter.services.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * authentication service
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Service
@EnableConfigurationProperties(SecurityProperties.class)
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final SecurityProperties properties;


    @Autowired
    JwtService jwtService;

    @Autowired
    AgentsRepository agentsRepository;

    @Autowired
    MailService mailService;

    private final ObjectMapper objectMapper;


    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthenticationService(SecurityProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }


    public DefaultDataSwap getAuthDataResult(
            Optional<Agent> agent,
            Boolean checkPassword, String password,
            String token,
            Boolean returnRefreshToken,
            String refreshToken
    ) throws JsonProcessingException {
        DefaultDataSwap result = new DefaultDataSwap();
        if (agent.isPresent()) {
            if (checkPassword) {
                logger.debug("checking password {} {} {} {}",password,encoder.encode(password), agent.get().getPassword(), encoder.matches(password, agent.get().getPassword()));
            }
            if (!checkPassword || (checkPassword && encoder.matches(password, agent.get().getPassword()))) {
                if (agent.get().getDeletedAt() == null && Objects.equals(RecordStatus.ACTIVE,agent.get().getStatusRegId())) {

                    Map<String,Object> dataObject = new HashMap<>();
                    if (!StringUtils.hasText(token)) {
                        agent.get().setLastToken(jwtService.createToken(agent.get()));
                        dataObject.put("token", agent.get().getLastToken());
                    } else {
                        dataObject.put("token", token);
                    }
                    if (returnRefreshToken) {
                        agent.get().setLastRefreshToken(jwtService.createRefreshToken(agent.get()));
                        dataObject.put("refreshToken", agent.get().getLastRefreshToken());
                    }
                    if (!StringUtils.hasText(token) || returnRefreshToken) {
                        agentsRepository.save(agent.get());
                    }
                    agent.get().setPassword(null);
                    dataObject.put("agent", objectMapper.convertValue(agent, Map.class));
                    result.data = dataObject;
                    result.httpStatusCode = HttpStatus.OK.value();
                    result.success = true;
                } else {
                    result.httpStatusCode = HttpStatus.UNAUTHORIZED.value();
                    result.message = "agent is not active";
                }
            } else {
                result.httpStatusCode = HttpStatus.UNAUTHORIZED.value();
                result.message = "password not match";
            }
        } else {
            result.httpStatusCode = HttpStatus.UNAUTHORIZED.value();
            result.message = "agent not found";
        }
        logger.debug("getAuthDataResult return is {}",objectMapper.convertValue(result, Map.class));
        return result;
    }

    private DefaultDataSwap getAuthDataResult(Optional<Agent> agent, String token) throws JsonProcessingException {
        return getAuthDataResult(agent,false,null, token, false, null);
    }

    public DefaultDataSwap login(AgentRequestDTO agentRequestDTO){
        DefaultDataSwap result = new DefaultDataSwap();
        try {

            if (agentRequestDTO != null) {
                if (StringUtils.hasText(agentRequestDTO.getPassword())) {
                    String identifier = agentRequestDTO.getIdentifier();
                    Optional<Agent> optionalAgent = Optional.empty();
                    if (StringUtils.hasText(identifier)) {
                        Long identifierTypeId = agentRequestDTO.getIdentifierTypeId();
                        if (identifierTypeId == null) {
                            if (mailService.isValidEmail(identifier)) {
                                identifierTypeId = IdentifierType.EMAIL_ID;
                            } else {
                                identifierTypeId = IdentifierType.IDENTIFIER_ID;
                            }
                        }
                        optionalAgent = agentsRepository.findByIdentifierTypeIdAndIdentifier(
                                identifierTypeId,
                                identifier.trim().toLowerCase()
                        );
                    } else {
                        String email = agentRequestDTO.getEmail();
                        if (StringUtils.hasText(email)) {
                            optionalAgent = agentsRepository.findByEmail(email.trim().toLowerCase());
                        } else {
                            result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                            result.message = "missing identifier or email";
                        }
                    }
                    if (optionalAgent.isPresent()) {
                        result = getAuthDataResult(
                                optionalAgent,
                                true,
                                agentRequestDTO.getPassword(),
                                null,
                                true,
                                null
                        );
                    } else if (!StringUtils.hasText(result.message)) {
                        result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                        result.message = "agent not found";
                    }
                } else {
                    result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                    result.message = "missing password";
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "missing data";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    public DefaultDataSwap passworRulesCheck(String password) {
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            if (StringUtils.hasText(password)) {
                if (password.length() < properties.getPasswordRules().getMinLength()) {
                    result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                    result.message = "password length less than " + properties.getPasswordRules().getMinLength() + " characters";
                } else {
                    result.success = true;
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "empty password";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    public DefaultDataSwap register(AgentRequestDTO agentRequestDTO){
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            if (agentRequestDTO != null) {
                Long identifierTypeId = agentRequestDTO.getIdentifierTypeId();
                String identifier = agentRequestDTO.getIdentifier();
                String email = agentRequestDTO.getEmail();
                if (!StringUtils.hasText(identifier)) {
                    identifier = email;
                } else if (!StringUtils.hasText(email) && mailService.isValidEmail(identifier)) {
                    email = identifier;
                }
                if (StringUtils.hasText(identifier)) {
                    if (identifierTypeId == null) {
                        if (mailService.isValidEmail(identifier)) {
                            identifierTypeId = IdentifierType.EMAIL_ID;
                        } else {
                            identifierTypeId = IdentifierType.IDENTIFIER_ID;
                        }
                    }
                    if (StringUtils.hasText(agentRequestDTO.getPassword())) {
                        Optional<Agent> agent = agentsRepository.findByIdentifierTypeIdAndIdentifierOrEmail(
                                identifierTypeId,
                                StringUtils.hasText(identifier) ? identifier.trim().toLowerCase() : identifier,
                                StringUtils.hasText(email) ? email.trim().toLowerCase() : email
                        );
                        if (agent.isEmpty()) {
                            result = passworRulesCheck(agentRequestDTO.getPassword());
                            if (result.success) {
                                Agent newAgent = new Agent();
                                newAgent.setIdentifierTypeId(identifierTypeId);
                                newAgent.setIdentifier(identifier);
                                newAgent.setEmail(email);
                                newAgent.setPassword(encoder.encode(agentRequestDTO.getPassword()));
                                agentsRepository.save(newAgent);
                                result = getAuthDataResult(agentsRepository.findByIdentifierTypeIdAndIdentifier(newAgent.getIdentifierTypeId(), newAgent.getIdentifier()), false, null, null, true, null);
                            }
                        } else {
                            result.httpStatusCode = HttpStatus.CONFLICT.value();
                            result.message = "agent already exists";
                        }
                    } else {
                        result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                        result.message = "missing password";
                    }
                } else {
                    result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                    result.message = "missing identifier or email";
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "missing data";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }


    public DefaultDataSwap checkTokenFromDto(TokenRequestDTO tokenRequest) {
        return checkToken(tokenRequest != null ? tokenRequest.getToken() : null);
    }

    public DefaultDataSwap checkToken(String token){
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            if (StringUtils.hasText(token)) {
                result = jwtService.checkToken(token);
                if (result.success) {
                    result = getAuthDataResult(agentsRepository.findById(Long.valueOf(String.valueOf(result.data))),token);
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "missing data";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    public DefaultDataSwap refreshTokenFromDto(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshToken(refreshTokenRequestDTO != null ? refreshTokenRequestDTO.getRefreshToken() : null);
    }

    public DefaultDataSwap refreshToken(String refreshToken){
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            if (StringUtils.hasText(refreshToken)) {
                result = jwtService.checkToken(refreshToken);
                if (result.success) {
                    result = getAuthDataResult(agentsRepository.findById(Long.valueOf(String.valueOf(result.data))),false,null, null, true, null);
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "missing data";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    public DefaultDataSwap sendEmailRecoverPasswordFromDto(PasswordRecoverRequestDTO passwordRecoverRequestDTO){
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            if (passwordRecoverRequestDTO != null) {
                String email = passwordRecoverRequestDTO.getEmail();
                if (!StringUtils.hasText(email)) {
                    email = passwordRecoverRequestDTO.getIdentifier();
                }
                if (StringUtils.hasText(email)) {
                    if (mailService.isValidEmail(email)) {
                        Optional<Agent> agent = agentsRepository.findByEmail(email.trim().toLowerCase());
                        if (agent.isPresent()) {
                            agent.get().setLastPasswordChangeToken(jwtService.createToken(agent.get()));
                            agentsRepository.save(agent.get());
                            String subject = "Password Recover";
                            String text = "Follow this link to create a new password: " + passwordRecoverRequestDTO.getPasswordChangeInterfacePath() + "/" + agent.get().getLastPasswordChangeToken();
                            String html = "Follow this link to create a new password: <br /><a href=\"" + passwordRecoverRequestDTO.getPasswordChangeInterfacePath() + "/" + agent.get().getLastPasswordChangeToken() + "\">Change password</a>";

                            mailService.sendEmail(email, subject, text, html);

                            result.success = true; //sendMail throws exception if error
                        } else {
                            result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                            result.message = "agent not found";
                        }
                    } else {
                        result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                        result.message = "invalid email";
                    }
                } else {
                    result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                    result.message = "missing data";
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "missing data";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    public DefaultDataSwap passwordChangeFromDto(PasswordChangeRequestDTO passwordChangeRequestDTO){
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            if (passwordChangeRequestDTO != null && StringUtils.hasText(passwordChangeRequestDTO.getToken()) && StringUtils.hasText(passwordChangeRequestDTO.getPassword())) {
                result = checkToken(passwordChangeRequestDTO.getToken());
                if (result.success) {
                    result.success = false;
                    Map<String,Object> dataObject = (Map<String, Object>) result.data;
                    Map<String,Object> agentObject = (Map<String, Object>) dataObject.getOrDefault("agent",null);
                    Optional<Agent> agent = agentsRepository.findById(Long.valueOf(String.valueOf(agentObject.getOrDefault("id",null))));
                    if (agent.isPresent()) {
                        if (passwordChangeRequestDTO.getToken().equals(agent.get().getLastPasswordChangeToken())) {

                            result = passworRulesCheck(passwordChangeRequestDTO.getPassword());
                            if (result.success) {
                                agent.get().setPassword(encoder.encode(passwordChangeRequestDTO.getPassword()));
                                agentsRepository.save(agent.get());
                                result.success = true;
                            }
                        } else {
                            result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                            result.message = "token not match";
                        }
                    } else {
                        result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                        result.message = "agent not found";
                    }
                }
            } else {
                result.httpStatusCode = HttpStatus.EXPECTATION_FAILED.value();
                result.message = "missing data";
            }
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

}
