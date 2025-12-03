package com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * users repository
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Repository
public interface UsersRepository extends BaseSsoRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
