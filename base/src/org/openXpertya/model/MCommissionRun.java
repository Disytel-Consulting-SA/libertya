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

import java.math.BigDecimal;
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

public class MCommissionRun extends X_C_CommissionRun {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_CommissionRun_ID
     * @param trxName
     */

    public MCommissionRun( Properties ctx,int C_CommissionRun_ID,String trxName ) {
        super( ctx,C_CommissionRun_ID,trxName );

        if( C_CommissionRun_ID == 0 ) {

            // setC_Commission_ID (0);
            // setDocumentNo (null);
            // setStartDate (new Timestamp(System.currentTimeMillis()));

            setGrandTotal( Env.ZERO );
            setProcessed( false );
        }
    }    // MCommissionRun

    /**
     * Constructor de la clase ...
     *
     *
     * @param commission
     */

    public MCommissionRun( MCommission commission ) {
        this( commission.getCtx(),0,commission.get_TrxName());
        setClientOrg( commission );
        setC_Commission_ID( commission.getC_Commission_ID());
    }    // MCommissionRun

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCommissionRun( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCommissionRun

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MCommissionAmt[] getAmts() {
        String sql = "SELECT * FROM C_CommissionAmt WHERE C_CommissionRun_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_CommissionRun_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MCommissionAmt( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAmts",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Convert

        MCommissionAmt[] retValue = new MCommissionAmt[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getAmts

    /**
     * Descripción de Método
     *
     */

    public void updateFromAmt() {
        MCommissionAmt[] amts       = getAmts();
        BigDecimal       GrandTotal = Env.ZERO;

        for( int i = 0;i < amts.length;i++ ) {
            MCommissionAmt amt = amts[ i ];

            GrandTotal = GrandTotal.add( amt.getCommissionAmt());
        }

        setGrandTotal( GrandTotal );
    }    // updateFromAmt
}    // MCommissionRun



/*
 *  @(#)MCommissionRun.java   02.07.07
 * 
 *  Fin del fichero MCommissionRun.java
 *  
 *  Versión 2.2
 *
 */
