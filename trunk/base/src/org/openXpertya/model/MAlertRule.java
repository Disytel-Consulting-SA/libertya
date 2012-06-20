/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAlertRule extends X_AD_AlertRule {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_AlertRule_ID
     * @param trxName
     */

    public MAlertRule( Properties ctx,int AD_AlertRule_ID,String trxName ) {
        super( ctx,AD_AlertRule_ID,trxName );
    }    // MAlertRule

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAlertRule( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAlertRule

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSql() {
        StringBuffer sql = new StringBuffer();

        sql.append( "SELECT " ).append( getSelectClause()).append( " FROM " ).append( getFromClause());

        if( (getWhereClause() != null) && (getWhereClause().length() > 0) ) {
            sql.append( " WHERE " ).append( getWhereClause());
        }

        if( (getOtherClause() != null) && (getOtherClause().length() > 0) ) {
            sql.append( " " ).append( getOtherClause());
        }

        return sql.toString();
    }    // getSql

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( newRecord ) {
            setIsValid( true );
        }

        if( isValid()) {
            setErrorMsg( null );
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAlertRule[" );

        sb.append( getID()).append( "-" ).append( getName()).append( ",Valid=" ).append( isValid()).append( "," ).append( getSql());
        sb.append( "]" );

        return sb.toString();
    }    // toString
}    // MAlertRule



/*
 *  @(#)MAlertRule.java   02.07.07
 * 
 *  Fin del fichero MAlertRule.java
 *  
 *  Versión 2.2
 *
 */
