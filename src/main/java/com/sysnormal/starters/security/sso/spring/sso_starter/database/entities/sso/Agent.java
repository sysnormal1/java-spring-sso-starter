package com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

/**
 * agent
 *
 * @author aalencarvz1
 * @version 1.0.0
 */
@Getter
@Setter
@Entity
@Table(name = "agents",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "agents_u1",
                        columnNames = {
                               /*
                            people only is differenced by identifier doc type and document, not by origin
                            "(coalesce(parent_id, 0))","status_reg_id","data_origin_id","(coalesce(table_origin_id, 0))","(coalesce(id_at_origin, 0))",
                             */
                                "identifier_type_id", "identifier"
                        }
                )
        })
public class Agent extends BaseSsoEntityModel {

    @Column(name = "identifier_type_id", nullable = false)
    @ColumnDefault(IdentifierType.EMAIL_ID+"")
    private Long identifierTypeId = IdentifierType.EMAIL_ID;

    @Column(name = "identifier", nullable = false, length = 512)
    private String identifier;

    @Column(name = "email", length = 512)
    private String email;

    @JsonIgnore
    @Column(name = "password", length = 1000)
    private String password;

    @Column(name = "last_token", length = 1000)
    private String lastToken;

    @Column(name = "last_refresh_token", length = 1000)
    private String lastRefreshToken;

    @Column(name = "last_password_change_token", length = 1000)
    private String lastPasswordChangeToken;

    @Column(name = "generic_access_profile_id")
    private Long genericAccessProfileId;

    @Column(name = "token_expiration_time")
    private Long tokenExpirationTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifier_type_id", nullable = false, updatable = false, insertable = false)
    private IdentifierType identifierType;
}
