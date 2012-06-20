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

import java.math.BigDecimal;
import java.util.logging.Level;

import org.openXpertya.model.M_Table;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportDelete extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Table_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( name.equals( "AD_Table_ID" )) {
                p_AD_Table_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
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
        log.info( "doIt - AD_Table_ID=" + p_AD_Table_ID );

        // get Table Info

        M_Table table = new M_Table( getCtx(),p_AD_Table_ID,get_TrxName());

        if( table.getID() == 0 ) {
            throw new IllegalArgumentException( "ImportDelete - No AD_Table_ID=" + p_AD_Table_ID );
        }

        String tableName = table.getTableName();

        if( !tableName.startsWith( "I" )) {
            throw new IllegalArgumentException( "ImportDelete - Not an import table = " + tableName );
        }

        // Delete

        String sql = "DELETE FROM " + tableName + " WHERE AD_Client_ID=" + getAD_Client_ID();
        int    no  = DB.executeUpdate( sql,get_TrxName());
        String msg = Msg.translate( getCtx(),tableName + "_ID" ) + " #" + no;

        return msg;
    }    // ImportDelete
}    // ImportDelete



/*
 *  @(#)ImportDelete.java   02.07.07
 * 
 *  Fin del fichero ImportDelete.java
 *  
 *  Versión 2.2
 *
 */
