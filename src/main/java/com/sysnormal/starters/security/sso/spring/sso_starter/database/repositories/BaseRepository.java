package com.sysnormal.starters.security.sso.spring.sso_starter.database.repositories;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.BaseEntityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * base repository
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@NoRepositoryBean
public interface BaseRepository<M extends BaseEntityModel, ID> extends JpaRepository<M, ID>, JpaSpecificationExecutor<M> {

}