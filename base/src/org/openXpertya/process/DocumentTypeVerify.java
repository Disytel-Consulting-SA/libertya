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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPeriodControl;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocumentTypeVerify extends SvrProcess {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( DocumentTypeVerify.class );

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {}    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        createDocumentTypes( getCtx(),getAD_Client_ID(),this,get_TrxName());
        createPeriodControls( getCtx(),getAD_Client_ID(),this,get_TrxName());

        return "OK";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param sp
     * @param trxName
     */

    public static void createDocumentTypes( Properties ctx,int AD_Client_ID,SvrProcess sp,String trxName ) {
        s_log.info( "AD_Client_ID=" + AD_Client_ID );

        String sql = "SELECT rl.Value, rl.Name " + "FROM AD_Ref_List rl " + "WHERE rl.AD_Reference_ID=183" + " AND rl.IsActive='Y' AND NOT EXISTS " + " (SELECT * FROM C_DocType dt WHERE dt.AD_Client_ID=? AND rl.Value=dt.DocBaseType)";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                //String name  = rs.getString( 2 );
                String value = rs.getString( 1 );
                // Trae la traducción como nombre.
                String name = MRefList.getListName(Env.getCtx(), 183, value);
                s_log.config( name + "=" + value );

                MDocType dt = new MDocType( ctx,value,name,trxName );

                // Agregado por Disytel - Franco Bonafine
                // Se asigna la clave unica del tipo de documento.
                // Esta clave es igual al DocBaseType dado que solo se crea
                // un tipo de documento de ese DocBase.
                dt.setDocTypeKey(value);
                //
                
                if( dt.save()) {
                    if( sp != null ) {
                        sp.addLog( 0,null,null,name );
                    } else {
                        s_log.fine( name );
                    }
                } else {
                    if( sp != null ) {
                        sp.addLog( 0,null,null,"Not created: " + name );
                    } else {
                        s_log.warning( "Not created: " + name );
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }
    }    // createDocumentTypes

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param sp
     * @param trxName
     */

    public static void createPeriodControls( Properties ctx,int AD_Client_ID,SvrProcess sp,String trxName ) {
        s_log.info( "AD_Client_ID=" + AD_Client_ID );

        String sql = "SELECT p.AD_Client_ID, p.C_Period_ID, dt.DocBaseType " + "FROM C_Period p" + " FULL JOIN C_DocType dt ON (p.AD_Client_ID=dt.AD_Client_ID) " + "WHERE p.AD_Client_ID=?" + " AND NOT EXISTS" + " (SELECT * FROM C_PeriodControl pc WHERE pc.C_Period_ID=p.C_Period_ID AND pc.DocBaseType=dt.DocBaseType)";
        PreparedStatement pstmt   = null;
        int               counter = 0;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int    Client_ID   = rs.getInt( 1 );
                int    C_Period_ID = rs.getInt( 2 );
                String DocBaseType = rs.getString( 3 );

                s_log.config( "AD_Client_ID=" + Client_ID + ", C_Period_ID=" + C_Period_ID + ", DocBaseType=" + DocBaseType );

                MPeriodControl pc = new MPeriodControl( ctx,Client_ID,C_Period_ID,DocBaseType,trxName );

                if( pc.save()) {
                    counter++;
                    s_log.fine( pc.toString());
                } else {
                    s_log.warning( "Not saved: " + pc );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( sp != null ) {
            sp.addLog( 0,null,new BigDecimal( counter ),"@C_PeriodControl_ID@ @Created@" );
        }
    }    // createPeriodControls
}    // DocumentTypeVerify



/*
 *  @(#)DocumentTypeVerify.java   02.07.07
 * 
 *  Fin del fichero DocumentTypeVerify.java
 *  
 *  Versión 2.2
 *
 */
