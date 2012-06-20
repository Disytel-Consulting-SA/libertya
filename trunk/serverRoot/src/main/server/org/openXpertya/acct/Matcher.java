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



package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Matcher {

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Client_ID
     * @param trxName
     */

    public Matcher( int AD_Client_ID,String trxName ) {
        m_AD_Client_ID = AD_Client_ID;
        m_trxName      = trxName;
    }    // Matcher

    /** Descripción de Campos */

    private int m_AD_Client_ID;

    /** Descripción de Campos */

    private String m_trxName = null;

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int match() {
        int counter = 0;

        // (a) Direct Matches

        String sql = "SELECT m1.AD_Client_ID,m2.AD_Org_ID, "                                                                                                                                                                                           // 1..2
                     + "m1.C_InvoiceLine_ID,m2.M_InOutLine_ID,m1.M_Product_ID, "                                                                                                                                                                       // 3..5
                     + "m1.DateTrx,m2.DateTrx, m1.Qty, m2.Qty "                                                                                                                                                                                        // 6..9
                     + "FROM M_MatchPO m1, M_MatchPO m2 " + "WHERE m1.C_OrderLine_ID=m2.C_OrderLine_ID" + " AND m1.M_InOutLine_ID IS NULL" + " AND m2.C_InvoiceLine_ID IS NULL" + " AND m1.M_Product_ID=m2.M_Product_ID" + " AND m1.AD_Client_ID=?"    // #1

        // Not existing Inv Matches

        + "     AND NOT EXISTS (SELECT * FROM M_MatchInv mi " + "WHERE mi.C_InvoiceLine_ID=m1.C_InvoiceLine_ID AND mi.M_InOutLine_ID=m2.M_InOutLine_ID)";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,m_AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                BigDecimal qty1 = rs.getBigDecimal( 8 );
                BigDecimal qty2 = rs.getBigDecimal( 9 );
                BigDecimal Qty  = qty1.min( qty2 );

                if( Qty.equals( Env.ZERO )) {
                    continue;
                }

                Timestamp dateTrx1 = rs.getTimestamp( 6 );
                Timestamp dateTrx2 = rs.getTimestamp( 7 );
                Timestamp DateTrx  = dateTrx1;

                if( dateTrx1.before( dateTrx2 )) {
                    DateTrx = dateTrx2;
                }

                //

                int AD_Client_ID     = rs.getInt( 1 );
                int AD_Org_ID        = rs.getInt( 2 );
                int C_InvoiceLine_ID = rs.getInt( 3 );
                int M_InOutLine_ID   = rs.getInt( 4 );
                int M_Product_ID     = rs.getInt( 5 );

                //

                if( createMatchInv( AD_Client_ID,AD_Org_ID,M_InOutLine_ID,C_InvoiceLine_ID,M_Product_ID,DateTrx,Qty )) {
                    counter++;
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"match",e );
        }

        log.fine( "Matcher.match - Client_ID=" + m_AD_Client_ID + ", Records created=" + counter );

        return counter;
    }    // match

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param M_InOutLine_ID
     * @param C_InvoiceLine_ID
     * @param M_Product_ID
     * @param DateTrx
     * @param Qty
     *
     * @return
     */

    private boolean createMatchInv( int AD_Client_ID,int AD_Org_ID,int M_InOutLine_ID,int C_InvoiceLine_ID,int M_Product_ID,Timestamp DateTrx,BigDecimal Qty ) {
        log.fine( "InvLine=" + C_InvoiceLine_ID + ",Rec=" + M_InOutLine_ID + ", Qty=" + Qty + ", " + DateTrx );

        // MMatchInv inv = new MMatchInv ();

        int M_MatchInv_ID = DB.getNextID( AD_Client_ID,"M_MatchInv",m_trxName );

        //

        StringBuffer sql = new StringBuffer( "INSERT INTO M_MatchInv (" + "M_MatchInv_ID, " + "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy, " + "M_InOutLine_ID,C_InvoiceLine_ID, " + "M_Product_ID,DateTrx,Qty, " + "Processing,Processed,Posted) VALUES (" ).append( M_MatchInv_ID ).append( ", " ).append( AD_Client_ID ).append( "," ).append( AD_Org_ID ).append( ",'Y',SysDate,0,SysDate,0, " ).append( M_InOutLine_ID ).append( "," ).append( C_InvoiceLine_ID ).append( ", " ).append( M_Product_ID ).append( "," ).append( DB.TO_DATE( DateTrx,true )).append( "," ).append( Qty ).append( ", 'N','Y','N')" );
        int no = DB.executeUpdate( sql.toString(),m_trxName );

        return no == 1;
    }    // createMatchInv
}    // Matcher



/*
 *  @(#)Matcher.java   24.03.06
 * 
 *  Fin del fichero Matcher.java
 *  
 *  Versión 2.2
 *
 */
