package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Env;

public final class MQueryPreparation {

    private MQueryPreparation() {
    }

    public static String resolveContext(
            Properties ctx,
            int windowNo,
            String clause) {

        if (clause == null || clause.length() == 0) {
            return "";
        }

        if (clause.indexOf('@') == -1) {
            return clause;
        }

        return Env.parseContext(
                ctx,
                windowNo,
                clause,
                false
        );
    }

    public static String applyReadAccess(
            Properties ctx,
            String sql,
            String tableName) {

        return MRole.getDefault(ctx, false).addAccessSQL(
                sql,
                tableName,
                MRole.SQL_FULLYQUALIFIED,
                MRole.SQL_RO
        );
    }
}