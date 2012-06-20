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

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.CCache;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBankAccount extends X_C_BankAccount {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_BankAccount_ID
     *
     * @return
     */

    public static MBankAccount get( Properties ctx,int C_BankAccount_ID ) {
        Integer      key      = new Integer( C_BankAccount_ID );
        MBankAccount retValue = ( MBankAccount )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MBankAccount( ctx,C_BankAccount_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "C_BankAccount",5 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BankAccount_ID
     * @param trxName
     */

    public MBankAccount( Properties ctx,int C_BankAccount_ID,String trxName ) {
        super( ctx,C_BankAccount_ID,trxName );

        if( C_BankAccount_ID == 0 ) {
            setIsDefault( false );
            setBankAccountType( BANKACCOUNTTYPE_Checking );
            setCurrentBalance( Env.ZERO );
            setC_Currency_ID( 0 );
            setCreditLimit( Env.ZERO );
            setC_BankAccount_ID( 0 );
        }
    }    // MBankAccount

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBankAccount( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBankAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MBankAccount[" ).append( getID()).append( "-" ).append( getAccountNo()).append( "]" );

        return sb.toString();
    }    // toString

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
        if( newRecord ) {
            return insert_Accounting( "C_BankAccount_Acct","C_AcctSchema_Default",null );
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
        return delete_Accounting( "C_BankAccount_Acct" );
    }    // beforeDelete

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// Si la cuenta no es de cheques entonces tampoco es de cheques en cartera.
		if (!BANKACCOUNTTYPE_Checking.equals(getBankAccountType()))
			setIsChequesEnCartera(false);
			
		return true;
	}
    
    
}    // MBankAccount



/*
 *  @(#)MBankAccount.java   02.07.07
 * 
 *  Fin del fichero MBankAccount.java
 *  
 *  Versión 2.2
 *
 */
