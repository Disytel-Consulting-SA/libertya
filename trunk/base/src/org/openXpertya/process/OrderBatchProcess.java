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
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MOrder;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class OrderBatchProcess extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_DocTypeTarget_ID = 0;

    /** Descripción de Campos */

    private String p_DocStatus = null;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private String p_IsSelfService = null;

    /** Descripción de Campos */

    private Timestamp p_DateOrdered_From = null;

    /** Descripción de Campos */

    private Timestamp p_DateOrdered_To = null;

    /** Descripción de Campos */

    private String p_DocAction = null;

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
            } else if( name.equals( "C_DocTypeTarget_ID" )) {
                p_C_DocTypeTarget_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "DocStatus" )) {
                p_DocStatus = ( String )para[ i ].getParameter();
            } else if( name.equals( "IsSelfService" )) {
                p_IsSelfService = ( String )para[ i ].getParameter();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "DateOrdered" )) {
                p_DateOrdered_From = ( Timestamp )para[ i ].getParameter();
                p_DateOrdered_To   = ( Timestamp )para[ i ].getParameter_To();
            } else if( name.equals( "DocAction" )) {
                p_DocAction = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
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
        log.info( "C_DocTypeTarget_ID=" + p_C_DocTypeTarget_ID + ", DocStatus=" + p_DocStatus + ", IsSelfService=" + p_IsSelfService + ", C_BPartner_ID=" + p_C_BPartner_ID + ", DateOrdered=" + p_DateOrdered_From + "->" + p_DateOrdered_To + ", DocAction=" + p_DocAction );

        if( p_C_DocTypeTarget_ID == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @C_DocTypeTarget_ID@" );
        }

        if( (p_DocStatus == null) || (p_DocStatus.length() != 2) ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @DocStatus@" );
        }

        if( (p_DocAction == null) || (p_DocAction.length() != 2) ) {
            throw new ErrorUsuarioOXP( "@NotFound@: @DocAction@" );
        }

        //

        StringBuffer sql = new StringBuffer( "SELECT * FROM C_Order " + "WHERE C_DocTypeTarget_ID=? AND DocStatus=?" );

        if( (p_IsSelfService != null) && (p_IsSelfService.length() == 1) ) {
            sql.append( " AND IsSelfService='" ).append( p_IsSelfService ).append( "'" );
        }

        if( p_C_BPartner_ID != 0 ) {
            sql.append( " AND C_BPartner_ID=" ).append( p_C_BPartner_ID );
        }

        if( p_DateOrdered_From != null ) {
            sql.append( " AND TRUNC(DateOrdered) >= " ).append( DB.TO_DATE( p_DateOrdered_From,true ));
        }

        if( p_DateOrdered_To != null ) {
            sql.append( " AND TRUNC(DateOrdered) <= " ).append( DB.TO_DATE( p_DateOrdered_To,true ));
        }

        int               counter    = 0;
        int               errCounter = 0;
        PreparedStatement pstmt      = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,p_C_DocTypeTarget_ID );
            pstmt.setString( 2,p_DocStatus );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( process( new MOrder( getCtx(),rs,get_TrxName()))) {
                    counter++;
                } else {
                    errCounter++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "@Updated@=" + counter + ", @Errors@=" + errCounter;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param order
     *
     * @return
     */

    private boolean process( MOrder order ) {
        log.info( order.toString());

        //

        order.setDocAction( p_DocAction );

        if( order.processIt( p_DocAction )) {
            order.save();
            addLog( 0,null,null,order.getDocumentNo() + ": OK" );

            return true;
        }

        addLog( 0,null,null,order.getDocumentNo() + ": Error " + order.getProcessMsg());

        return false;
    }    // process
}    // OrderBatchProcess



/*
 *  @(#)OrderBatchProcess.java   02.07.07
 * 
 *  Fin del fichero OrderBatchProcess.java
 *  
 *  Versión 2.2
 *
 */
