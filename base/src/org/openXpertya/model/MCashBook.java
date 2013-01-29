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
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCashBook extends X_C_CashBook {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_CashBook_ID
     * @param trxName
     *
     * @return
     */

    public static MCashBook get( Properties ctx,int C_CashBook_ID,String trxName ) {
        Integer   key      = new Integer( C_CashBook_ID );
        MCashBook retValue = ( MCashBook )s_cache.get( key );

        if( retValue != null ) {
            return retValue;
        }

        retValue = new MCashBook( ctx,C_CashBook_ID,null );

        if( retValue.getID() != 0 ) {
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Org_ID
     * @param C_Currency_ID
     * @param trxName
     *
     * @return
     */

    public static MCashBook get( Properties ctx,int AD_Org_ID,int C_Currency_ID,String trxName ) {

        // Try from cache

        Iterator it = s_cache.values().iterator();

        while( it.hasNext()) {
            MCashBook cb = ( MCashBook )it.next();

            if( (cb.getAD_Org_ID() == AD_Org_ID) && (cb.getC_Currency_ID() == C_Currency_ID) ) {
                return cb;
            }
        }

        // Get from DB

        MCashBook retValue = null;
        String    sql      = "SELECT * FROM C_CashBook " + "WHERE AD_Org_ID=? AND C_Currency_ID=? " + "ORDER BY IsDefault DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Org_ID );
            pstmt.setInt( 2,C_Currency_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MCashBook( ctx,rs,null );

                Integer key = new Integer( retValue.getC_CashBook_ID());

                s_cache.put( key,retValue );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
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
    }    // get

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "",20 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MCashBook.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_CashBook_ID
     * @param trxName
     */

    public MCashBook( Properties ctx,int C_CashBook_ID,String trxName ) {
        super( ctx,C_CashBook_ID,trxName );
    }    // MCashBook

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCashBook( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCashBook

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
            insert_Accounting( "C_CashBook_Acct","C_AcctSchema_Default",null );
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
        return delete_Accounting( "C_Cashbook_Acct" );
    }    // beforeDelete
    
    /**
	 * 	Get MCashBook from Cache
	 *	@param ctx context
	 *	@param C_CashBook_ID id
	 *	@return MCashBook
	 */
	public static MCashBook get (Properties ctx, int C_CashBook_ID)
	{
		return get(ctx, C_CashBook_ID, null);
	}	//	get

}    // MCashBook



/*
 *  @(#)MCashBook.java   02.07.07
 * 
 *  Fin del fichero MCashBook.java
 *  
 *  Versión 2.2
 *
 */
