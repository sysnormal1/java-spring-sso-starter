package com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.sso;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.BaseSsoEntityModel;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * base sso repository
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@NoRepositoryBean
public interface BaseSsoRepository<M extends BaseSsoEntityModel, ID> extends BaseRepository<M, ID> {

}