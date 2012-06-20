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
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

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

public class MCharge extends X_C_Charge {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MCharge getDefault( Properties ctx ) {
        MCharge           retValue = null;
        String            sql      = "SELECT * FROM C_Charge WHERE AD_Client_ID=?";
        PreparedStatement pstmt    = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,Env.getAD_Client_ID( ctx ));

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MCharge( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getDefault",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // getDefault

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MCharge.class );

    /**
     * Descripción de Método
     *
     *
     * @param C_Charge_ID
     * @param as
     * @param amount
     *
     * @return
     */

    public static MAccount getAccount( int C_Charge_ID,MAcctSchema as,BigDecimal amount ) {
        if( (C_Charge_ID == 0) || (as == null) ) {
            return null;
        }

        int acct_index = 1;    // Expense (positive amt)

        if( (amount != null) && (amount.signum() < 0) ) {
            acct_index = 2;    // Revenue (negative amt)
        }

        String sql = "SELECT CH_Expense_Acct, CH_Revenue_Acct FROM C_Charge_Acct WHERE C_Charge_ID=? AND C_AcctSchema_ID=?";
        int Account_ID = 0;

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Charge_ID );
            pstmt.setInt( 2,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                Account_ID = rs.getInt( acct_index );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            s_log.log( Level.SEVERE,"getChargeAccount",e );

            return null;
        }

        // No account

        if( Account_ID == 0 ) {
            s_log.severe( "getAccount - NO account for C_Charge_ID=" + C_Charge_ID );

            return null;
        }

        // Return Account

        MAccount acct = MAccount.get( as.getCtx(),Account_ID );

        return acct;
    }    // getChargeAccount

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Charge_ID
     * @param trxName
     */

    public MCharge( Properties ctx,int C_Charge_ID,String trxName ) {
        super( ctx,C_Charge_ID,null );

        if( C_Charge_ID == 0 ) {
            setChargeAmt( Env.ZERO );
            setIsSameCurrency( false );
            setIsSameTax( false );
            setIsTaxIncluded( false );    // N

            // setName (null);
            // setC_TaxCategory_ID (0);

        }
    }                                     // MCharge

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCharge( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCharge

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
        if( newRecord && success) {
            insert_Accounting( "C_Charge_Acct","C_AcctSchema_Default",null );
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        return delete_Accounting( "C_Charge_Acct" );
    }    // beforeDelete

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Por defecto se asigna el tipo Cargo a Documento
		if (getChargeType() == null || getChargeType().length() == 0) {
			setChargeType(CHARGETYPE_DocumentCharge);
		}

		// Los Cargos con tipo diferente a "Cargo a Documento" no requieren los valores 
		// de importes.
		// Se asignan valores por defecto a estas columnas.
		if (!CHARGETYPE_DocumentCharge.equals(getChargeType())) {
			setChargeAmt(BigDecimal.ZERO);
			setIsSameCurrency(false);
			setIsSameTax(false);
			setIsTaxIncluded(false);
		}
		
		return true;
	}
    
    
}    // MCharge



/*
 *  @(#)MCharge.java   02.07.07
 * 
 *  Fin del fichero MCharge.java
 *  
 *  Versión 2.2
 *
 */
