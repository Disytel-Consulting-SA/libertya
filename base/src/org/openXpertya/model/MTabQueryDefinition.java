package org.openXpertya.model;

import java.io.Serializable;

public final class MTabQueryDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String tableName;
    private final String whereClause;
    private final String orderByClause;
    private final boolean detail;
    private final String linkColumnName;

    public MTabQueryDefinition(
            String tableName,
            String whereClause,
            String orderByClause,
            boolean detail,
            String linkColumnName) {

        this.tableName = tableName;
        this.whereClause = whereClause != null ? whereClause : "";
        this.orderByClause = orderByClause != null ? orderByClause : "";
        this.detail = detail;
        this.linkColumnName =
                linkColumnName != null ? linkColumnName : "";
    }

    public String getTableName() {
        return tableName;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public boolean isDetail() {
        return detail;
    }

    public String getLinkColumnName() {
        return linkColumnName;
    }

    @Override
    public String toString() {
        return "MTabQueryDefinition[tableName=" + tableName
                + ", whereClause=" + whereClause
                + ", orderByClause=" + orderByClause
                + ", detail=" + detail
                + ", linkColumnName=" + linkColumnName + "]";
    }
}