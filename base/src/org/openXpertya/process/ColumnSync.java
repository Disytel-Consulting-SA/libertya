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



package org.openXpertya.process;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ColumnSync extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Column_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_AD_Column_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "C_Column_ID=" + p_AD_Column_ID );

        if( p_AD_Column_ID == 0 ) {
            throw new ErrorUsuarioOXP( "@No@ @AD_Column_ID@" );
        }

        M_Column column = new M_Column( getCtx(),p_AD_Column_ID,get_TrxName());

        if( column.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ @AD_Column_ID@ " + p_AD_Column_ID );
        }

        M_Table table = M_Table.get( getCtx(),column.getAD_Table_ID());

        if( table.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ @AD_Table_ID@ " + column.getAD_Table_ID());
        }

        // Find Column in Database

        DatabaseMetaData md        = DB.getConnectionRO().getMetaData();
        String           catalog   = DB.getDatabase().getCatalog();
        String           schema    = DB.getDatabase().getSchema();
        String           tableName = table.getTableName();

        if( DB.isOracle()) {
            tableName = tableName.toUpperCase();
        } else if (DB.isPostgreSQL()) {
        	tableName = tableName.toLowerCase();
        }

        int    noColumns = 0;
        String sql       = null;

        //

        ResultSet rs = md.getColumns( catalog,schema,tableName,null );

        while( rs.next()) {
            noColumns++;

            String columnName = rs.getString( "COLUMN_NAME" );

            if( !columnName.equalsIgnoreCase( column.getColumnName())) {
                continue;
            }

            // update existing column

            sql = column.getSQLModify( table );

            break;
        }

        rs.close();
        rs = null;

        // No Table

        if( noColumns == 0 ) {
            sql = table.getSQLCreate();

            // No existing column

        } else if( sql == null ) {
            sql = column.getSQLAdd( table );
        }

        int no = DB.executeUpdate( sql,false,get_TrxName());

        if( no == -1 ) {
            String        msg = "@Error@ ";
            ValueNamePair pp  = CLogger.retrieveError();

            if( pp != null ) {
                msg = pp.getName() + " - ";
            }

            msg += sql;

            throw new ErrorUsuarioOXP( msg );
        }

        return sql;
    }    // doIt
}    // ColumnSync



/*
 *  @(#)ColumnSync.java   02.07.07
 * 
 *  Fin del fichero ColumnSync.java
 *  
 *  Versión 2.1
 *
 */
