package com.sysnormal.starters.security.sso.spring.sso_starter.database.migrations;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;

public class V2__RecordStatusSeeder extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (Statement stmt = context.getConnection().createStatement()) {
            stmt.execute("insert ignore into record_status(id, status_reg_id, created_at, name, is_active) values (1, 1, current_timestamp, 'ACTIVE',1)");
            stmt.execute("insert ignore into record_status(id, status_reg_id, created_at, name, is_active) values (2, 1, current_timestamp, 'INACTIVE',0)");
        }
    }
}
