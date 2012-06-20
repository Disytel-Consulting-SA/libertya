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

public class MPaySelection extends X_C_PaySelection {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaySelection_ID
     * @param trxName
     */

    public MPaySelection( Properties ctx,int C_PaySelection_ID,String trxName ) {
        super( ctx,C_PaySelection_ID,trxName );

        if( C_PaySelection_ID == 0 ) {

            // setC_BankAccount_ID (0);
            // setName (null); // @#Date@
            // setPayDate (new Timestamp(System.currentTimeMillis())); // @#Date@

            setTotalAmt( Env.ZERO );
            setIsApproved( false );
            setProcessed( false );
            setProcessing( false );
        }
    }    // MPaySelection

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaySelection( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaySelection

    /** Descripción de Campos */

    private MPaySelectionLine[] m_lines = null;

    /** Descripción de Campos */

    private int m_C_Currency_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MPaySelectionLine[] getLines( boolean requery ) {
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_PaySelectionLine WHERE C_PaySelection_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_PaySelection_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPaySelectionLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        m_lines = new MPaySelectionLine[ list.size()];
        list.toArray( m_lines );

        return m_lines;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        if( m_C_Currency_ID == 0 ) {
            String sql = "SELECT C_Currency_ID FROM C_BankAccount " + "WHERE C_BankAccount_ID=?";

            m_C_Currency_ID = DB.getSQLValue( null,sql,getC_BankAccount_ID());
        }

        return m_C_Currency_ID;
    }    // getC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPaySelection[" );

        sb.append( getID()).append( "," ).append( getName()).append( "]" );

        return sb.toString();
    }    // toString
}    // MPaySelection



/*
 *  @(#)MPaySelection.java   02.07.07
 * 
 *  Fin del fichero MPaySelection.java
 *  
 *  Versión 2.2
 *
 */
