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
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MFactAcct extends X_Fact_Acct {

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     * @param trxName
     *
     * @return
     */

    public static int delete( int AD_Table_ID,int Record_ID,String trxName ) {
        StringBuffer sb = new StringBuffer();

        sb.append( "DELETE Fact_Acct WHERE AD_Table_ID=" ).append( AD_Table_ID ).append( " AND Record_ID=" ).append( Record_ID );

        int no = DB.executeUpdate( sb.toString(),trxName );

        if( no == -1 ) {
            s_log.log( Level.SEVERE,"failed: AD_Table_ID=" + AD_Table_ID + ", Record_ID" + Record_ID );
        } else {
            s_log.fine( "delete - AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + Record_ID + " - #" + no );
        }

        return no;
    }    // delete

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MFactAcct.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param Fact_Acct_ID
     * @param trxName
     */

    public MFactAcct( Properties ctx,int Fact_Acct_ID,String trxName ) {
        super( ctx,Fact_Acct_ID,trxName );
    }    // MFactAcct

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MFactAcct( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MFactAcct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MFactAcct[" );

        sb.append( getID()).append( "-Acct=" ).append( getAccount_ID()).append( ",Dr=" ).append( getAmtSourceDr()).append( "|" ).append( getAmtAcctDr()).append( ",Cr=" ).append( getAmtSourceCr()).append( "|" ).append( getAmtAcctCr()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAccount getMAccount() {
        MAccount acct = MAccount.get( getCtx(),getAD_Client_ID(),getAD_Org_ID(),getC_AcctSchema_ID(),getAccount_ID(),getM_Product_ID(),getC_BPartner_ID(),getAD_OrgTrx_ID(),getC_LocFrom_ID(),getC_LocTo_ID(),getC_SalesRegion_ID(),getC_Project_ID(),getC_Campaign_ID(),getC_Activity_ID(),getUser1_ID(),getUser2_ID());

        if( (acct != null) && (acct.getID() == 0) ) {
            acct.save();
        }

        return acct;
    }    // getMAccount
}    // MFactAcct



/*
 *  @(#)MFactAcct.java   02.07.07
 * 
 *  Fin del fichero MFactAcct.java
 *  
 *  Versión 2.2
 *
 */
