package com.sysnormal.starters.security.sso.spring.sso_starter.server.auth.dtos;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.IdentifierType;
import lombok.Getter;
import lombok.Setter;

/**
 * the agent dto
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Getter
@Setter
public class AgentRequestDTO {
    private Long identifierTypeId = IdentifierType.EMAIL_ID;
    private String identifier;
    private String email;
    private String password;
}