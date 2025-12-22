package com.sysnormal.starters.security.sso.spring.sso_starter.services.jwt;

import com.sysnormal.libs.commons.DefaultDataSwap;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.Agent;
import com.sysnormal.starters.security.sso.spring.sso_starter.properties.jwt.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


/**
 * jwt service
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);


    private final Key key;
    private final JwtProperties jwtProperties;
    private final JwtParser jwtParsaer;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.jwtParsaer = Jwts.parser().setSigningKey(key).build();
    }

    public String createToken(Agent agent, Long expiration) {
        String result = null;
        if (agent != null) {
            if (agent.getTokenExpirationTime() != null) {
                expiration = agent.getTokenExpirationTime();
            }
            logger.debug("creating token for agent {}, identifier {}, expiration ",agent.getId(),agent.getIdentifier(), expiration);
            JwtBuilder builder = Jwts.builder()
                    .signWith(key, SignatureAlgorithm.HS256) // usa a mesma key, mas novo builder
                    .setSubject(String.valueOf(agent.getId()))
                    .claim("id", agent.getId())
                    .setIssuedAt(new Date());
            if (expiration > 0) {
                builder.setExpiration(new Date(System.currentTimeMillis() + expiration));
            }
            result = builder.compact();
        }
        return result;
    }

    public String createToken(Agent agent) {
        return createToken(agent, jwtProperties.getDefaultTokenExpiration()); //1 minute
    }

    public String createRefreshToken(Agent agent) {
        return createToken(agent, 86400000L); //1 day
    }

    public DefaultDataSwap checkToken(String token){
        DefaultDataSwap result = new DefaultDataSwap();
        try {
            logger.debug("checking token {}",token);
            if (StringUtils.hasText(token)) {
                Claims claims = jwtParsaer.parseClaimsJws(token).getBody();
                String agentId = String.valueOf(claims.get("id"));
                if (StringUtils.hasText(agentId)) {
                    result.data = agentId;
                    result.success = true;
                } else {
                    result.message = "invalid token";
                }
            } else {
                result.httpStatusCode = HttpStatus.UNAUTHORIZED.value();
                result.message = "missing data";
            }
        } catch (ExpiredJwtException e) {
            result.httpStatusCode = HttpStatus.UNAUTHORIZED.value();
            result.message = "token expired";
            result.setException(e);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            result.httpStatusCode = HttpStatus.UNAUTHORIZED.value();
            result.message = "invalid signature";
            result.setException(e);
        } catch (MalformedJwtException e) {
            result.httpStatusCode = HttpStatus.BAD_REQUEST.value();
            result.message = "malformed token";
            result.setException(e);
        } catch (Exception e) {
            result.httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            result.message = "unexpected error";
            result.setException(e);
        }
        return result;
    }
}
