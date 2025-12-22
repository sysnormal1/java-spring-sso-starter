package com.sysnormal.starters.security.sso.spring.sso_starter.database.migrations;

import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.IdentifierType;
import com.sysnormal.starters.security.sso.spring.sso_starter.database.entities.sso.RecordStatus;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

public class V2__Seeder extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("insert ignore into record_status(id, status_reg_id, created_at, name, is_active) values ("+ RecordStatus.ACTIVE +","+ RecordStatus.ACTIVE +", current_timestamp, 'ACTIVE',1)");
            stmt.execute("insert ignore into record_status(id, status_reg_id, created_at, name, is_active) values ("+RecordStatus.INACTIVE+","+ RecordStatus.ACTIVE +", current_timestamp, 'INACTIVE',0)");
            stmt.execute("insert ignore into identifier_types(id, status_reg_id, created_at, name) values ("+ IdentifierType.IDENTIFIER_ID +","+ RecordStatus.ACTIVE +", current_timestamp, 'IDENTIFIER')");
            stmt.execute("insert ignore into identifier_types(id, status_reg_id, created_at, name) values ("+ IdentifierType.EMAIL_ID +","+ RecordStatus.ACTIVE +", current_timestamp, 'EMAIL')");
            stmt.execute("insert ignore into identifier_types(id, status_reg_id, created_at, name) values ("+ IdentifierType.CODE_ID +","+ RecordStatus.ACTIVE +", current_timestamp, 'CODE')");
            stmt.execute("insert ignore into identifier_types(id, status_reg_id, created_at, name) values ("+ IdentifierType.CNPJ_ID +","+ RecordStatus.ACTIVE +", current_timestamp, 'CNPJ')");
            stmt.execute("insert ignore into identifier_types(id, status_reg_id, created_at, name) values ("+ IdentifierType.CPF_ID +","+ RecordStatus.ACTIVE +", current_timestamp, 'CPF')");
        }
    }
}
