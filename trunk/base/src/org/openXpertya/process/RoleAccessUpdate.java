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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MRole;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RoleAccessUpdate extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Role_ID = 0;

    /** Descripción de Campos */

    private int p_AD_Client_ID = 0;

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
            } else if( name.equals( "AD_Role_ID" )) {
                p_AD_Role_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Client_ID" )) {
                p_AD_Client_ID = para[ i ].getParameterAsInt();
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
        log.info( "doIt - AD_Client_ID=" + p_AD_Client_ID + ", AD_Role_ID=" + p_AD_Role_ID );

        //

        if( p_AD_Role_ID != 0 ) {
            updateRole( new MRole( getCtx(),p_AD_Role_ID,get_TrxName()));
        } else {
            String sql = "SELECT * FROM AD_Role ";

            if( p_AD_Client_ID != 0 ) {
                sql += "WHERE AD_Client_ID=? ";
            }

            sql += "ORDER BY AD_Client_ID, Name";

            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());

                if( p_AD_Client_ID != 0 ) {
                    pstmt.setInt( 1,p_AD_Client_ID );
                }

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    updateRole( new MRole( getCtx(),rs,get_TrxName()));
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"doIt",e );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception e ) {
                pstmt = null;
            }
        }

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param role
     */

    private void updateRole( MRole role ) {
        addLog( 0,null,null,role.getName() + ": " + role.updateAccessRecords());
    }    // updateRole
}    // RoleAccessUpdate



/*
 *  @(#)RoleAccessUpdate.java   02.07.07
 * 
 *  Fin del fichero RoleAccessUpdate.java
 *  
 *  Versión 2.2
 *
 */
