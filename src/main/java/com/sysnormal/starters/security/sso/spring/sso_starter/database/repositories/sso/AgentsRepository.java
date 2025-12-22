package com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.Agent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * agents repository
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Repository
public interface AgentsRepository extends BaseSsoRepository<Agent, Long> {

    Optional<Agent> findByIdentifierTypeIdAndIdentifier(Long identifierTypeId, String identifier);

    Optional<Agent> findByEmail(String email);


    @Query("""
        SELECT a FROM Agent a
        WHERE a.identifierTypeId = :identifierTypeId
          AND (a.identifier = :identifier OR a.email = :email)
    """)
    Optional<Agent> findByIdentifierTypeIdAndIdentifierOrEmail(
        @Param("identifierTypeId") Long identifierTypeId,
        @Param("identifier") String identifier,
        @Param("email") String email
    );
}
