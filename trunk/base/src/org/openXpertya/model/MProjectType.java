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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProjectType extends X_C_ProjectType {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_ProjectType_ID
     * @param trxName
     */

    public MProjectType( Properties ctx,int C_ProjectType_ID,String trxName ) {
        super( ctx,C_ProjectType_ID,trxName );
    }    // MProjectType

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProjectType( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProjectType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MProjectType[" ).append( getID()).append( "-" ).append( getName()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProjectTypePhase[] getPhases() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Phase WHERE C_ProjectType_ID=? ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_ProjectType_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectTypePhase( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getPhases",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectTypePhase[] retValue = new MProjectTypePhase[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getPhases
}    // MProjectType



/*
 *  @(#)MProjectType.java   02.07.07
 * 
 *  Fin del fichero MProjectType.java
 *  
 *  Versión 2.2
 *
 */
