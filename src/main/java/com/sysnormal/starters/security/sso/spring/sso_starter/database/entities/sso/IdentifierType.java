package com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "identifier_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "identifier_types_u1",
                        columnNames = {
                                "name"
                        }
                )
        }
)
public class IdentifierType extends BaseSsoEntityModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "process_to_validate", length = 1000)


    public static final long IDENTIFIER_ID = 1;
    public static final long EMAIL_ID = 2;
    public static final long CODE_ID = 3;
    public static final long CNPJ_ID = 4;
    public static final long CPF_ID = 5;


    public static final IdentifierType IDENTIFIER = new IdentifierType(){{
        setId(IDENTIFIER_ID);
        setName("IDENTIFIER");
    }};
    public static final IdentifierType EMAIL = new IdentifierType(){{
        setId(EMAIL_ID);
        setName("EMAIL");
    }};
    public static final IdentifierType CODE = new IdentifierType(){{
        setId(CODE_ID);
        setName("CODE");
    }};
    public static final IdentifierType CNPJ = new IdentifierType(){{
        setId(CNPJ_ID);
        setName("CNPJ");
    }};
    public static final IdentifierType CPF = new IdentifierType(){{
        setId(CPF_ID);
        setName("CPF");
    }};


}


