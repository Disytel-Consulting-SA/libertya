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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectTypePhase extends X_C_Phase {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Phase_ID
     * @param trxName
     */

    public MProjectTypePhase( Properties ctx,int C_Phase_ID,String trxName ) {
        super( ctx,C_Phase_ID,trxName );

        if( C_Phase_ID == 0 ) {

            // setC_Phase_ID (0);                      //      PK
            // setC_ProjectType_ID (0);        //      Parent
            // setName (null);

            setSeqNo( 0 );
            setStandardQty( Env.ZERO );
        }
    }    // MProjectTypePhase

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectTypePhase( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectTypePhase

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProjectTypeTask[] getTasks() {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_Task WHERE C_Phase_ID=? ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Phase_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectTypeTask( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getTasks",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectTypeTask[] retValue = new MProjectTypeTask[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getPhases
}    // MProjectTypePhase



/*
 *  @(#)MProjectTypePhase.java   02.07.07
 * 
 *  Fin del fichero MProjectTypePhase.java
 *  
 *  Versión 2.2
 *
 */
