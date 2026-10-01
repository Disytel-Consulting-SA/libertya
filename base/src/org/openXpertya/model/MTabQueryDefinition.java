package org.openXpertya.model;

import java.io.Serializable;

/**
 * Definicion de consulta asociada a una pestaña de Libertya.
 *
 * Esta clase contiene la informacion estructural necesaria para que
 * consumidores externos a la UI tradicional puedan reproducir el
 * universo de registros definido por una MTab sin ejecutar la consulta.
 *
 * No contiene criterios de paginacion, filtros adicionales del usuario
 * ni ninguna otra consideracion propia de un consumidor particular.
 */
public final class MTabQueryDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String tableName;
    private final String whereClause;
    private final String orderByClause;

    public MTabQueryDefinition(
            String tableName,
            String whereClause,
            String orderByClause) {

        this.tableName = tableName;
        this.whereClause = whereClause != null ? whereClause : "";
        this.orderByClause = orderByClause != null ? orderByClause : "";
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

    @Override
    public String toString() {
        return "MTabQueryDefinition[tableName=" + tableName
                + ", whereClause=" + whereClause
                + ", orderByClause=" + orderByClause + "]";
    }
}