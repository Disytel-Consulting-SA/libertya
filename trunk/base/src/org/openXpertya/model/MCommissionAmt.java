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

public class MCommissionAmt extends X_C_CommissionAmt {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_CommissionAmt_ID
     * @param trxName
     */

    public MCommissionAmt( Properties ctx,int C_CommissionAmt_ID,String trxName ) {
        super( ctx,C_CommissionAmt_ID,trxName );

        if( C_CommissionAmt_ID == 0 ) {

            // setC_CommissionRun_ID (0);
            // setC_CommissionLine_ID (0);

            setActualQty( Env.ZERO );
            setCommissionAmt( Env.ZERO );
            setConvertedAmt( Env.ZERO );
        }
    }    // MCommissionAmt

    /**
     * Constructor de la clase ...
     *
     *
     * @param run
     * @param C_CommissionLine_ID
     */

    public MCommissionAmt( MCommissionRun run,int C_CommissionLine_ID ) {
        this( run.getCtx(),0,run.get_TrxName());
        setClientOrg( run );
        setC_CommissionRun_ID( run.getC_CommissionRun_ID());
        setC_CommissionLine_ID( C_CommissionLine_ID );
    }    // MCommissionAmt

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCommissionAmt( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCommissionAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MCommissionDetail[] getDetails() {
        String sql = "SELECT * FROM C_CommissionDetail WHERE C_CommissionAmt_ID=?";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_CommissionAmt_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MCommissionDetail( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getDetails",e );
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

        MCommissionDetail[] retValue = new MCommissionDetail[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getDetails

    /**
     * Descripción de Método
     *
     */

    public void calculateCommission() {
        MCommissionDetail[] details      = getDetails();
        BigDecimal          ConvertedAmt = Env.ZERO;
        BigDecimal          ActualQty    = Env.ZERO;

        for( int i = 0;i < details.length;i++ ) {
            MCommissionDetail detail = details[ i ];
            BigDecimal        amt    = detail.getConvertedAmt();

            if( amt == null ) {
                amt = Env.ZERO;
            }

            ConvertedAmt = ConvertedAmt.add( amt );
            ActualQty    = ActualQty.add( detail.getActualQty());
        }

        setConvertedAmt( ConvertedAmt );
        setActualQty( ActualQty );

        //

        MCommissionLine cl = new MCommissionLine( getCtx(),getC_CommissionLine_ID(),get_TrxName());

        // Qty

        BigDecimal qty = getActualQty().subtract( cl.getQtySubtract());

        if( cl.isPositiveOnly() && (qty.compareTo( Env.ZERO ) < 0) ) {
            qty = Env.ZERO;
        }

        qty = qty.multiply( cl.getQtyMultiplier());

        // Amt

        BigDecimal amt = getConvertedAmt().subtract( cl.getAmtSubtract());

        if( cl.isPositiveOnly() && (amt.compareTo( Env.ZERO ) < 0) ) {
            amt = Env.ZERO;
        }

        amt = amt.multiply( cl.getAmtMultiplier());

        //

        setCommissionAmt( amt.add( qty ));
    }    // calculateCommission

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( !newRecord ) {
            updateRunHeader();
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( success ) {
            updateRunHeader();
        }

        return success;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     */

    private void updateRunHeader() {
        MCommissionRun run = new MCommissionRun( getCtx(),getC_CommissionRun_ID(),get_TrxName());

        run.updateFromAmt();
        run.save();
    }    // updateRunHeader
}    // MCommissionAmt



/*
 *  @(#)MCommissionAmt.java   02.07.07
 * 
 *  Fin del fichero MCommissionAmt.java
 *  
 *  Versión 2.2
 *
 */
