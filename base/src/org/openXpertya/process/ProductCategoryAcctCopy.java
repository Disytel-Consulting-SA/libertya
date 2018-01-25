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
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProductCategoryAcctCopy extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Product_Category_ID = 0;

    /** Descripción de Campos */

    private int p_C_AcctSchema_ID = 0;

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
            } else if( name.equals( "M_Product_Category_ID" )) {
                p_M_Product_Category_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = para[ i ].getParameterAsInt();
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
        log.info( "C_AcctSchema_ID=" + p_C_AcctSchema_ID );
        
        if( p_C_AcctSchema_ID == 0 ) {
            throw new ErrorOXPSystem( "C_AcctSchema_ID=0" );
        }

        MAcctSchema as = MAcctSchema.get( getCtx(),p_C_AcctSchema_ID );

        if( as.getID() == 0 ) {
            throw new ErrorOXPSystem( "Not Found - C_AcctSchema_ID=" + p_C_AcctSchema_ID );
        }

        // Update

        String sql = 	" UPDATE M_Product_Acct pa " + 
        				" SET 	updated=now(), " +
        				" 		updatedby = " + getAD_User_ID() + ", " +
        				" 		P_Revenue_Acct = foo.P_Revenue_Acct, " +
        				" 		P_Expense_Acct = foo.P_Expense_Acct, " +
        				"		P_Asset_Acct = foo.P_Asset_Acct, " +
        				"		P_COGS_Acct = foo.P_COGS_Acct, " + 
        				" 		P_PurchasePriceVariance_Acct = foo.P_PurchasePriceVariance_Acct, " +
        				"		P_InvoicePriceVariance_Acct = foo.P_InvoicePriceVariance_Acct, " + 
        				" 		P_TradeDiscountRec_Acct = foo.P_TradeDiscountRec_Acct, " +
        				"		P_TradeDiscountGrant_Acct = foo.P_TradeDiscountGrant_Acct" + 
        				" FROM " +
        				" 	(SELECT P_Revenue_Acct,P_Expense_Acct,P_Asset_Acct,P_COGS_Acct," + " P_PurchasePriceVariance_Acct,P_InvoicePriceVariance_Acct," + " P_TradeDiscountRec_Acct,P_TradeDiscountGrant_Acct" + " " +
        				"	 FROM M_Product_Category_Acct pca" + 
        				" 	 WHERE pca.M_Product_Category_ID=" + p_M_Product_Category_ID + 
        				" 	 AND pca.C_AcctSchema_ID=" + p_C_AcctSchema_ID + ") as foo " +
        				" WHERE pa.C_AcctSchema_ID=" + p_C_AcctSchema_ID + 
        				" AND EXISTS (SELECT * FROM M_Product p " + "WHERE p.M_Product_ID=pa.M_Product_ID" + " AND p.M_Product_Category_ID=" + p_M_Product_Category_ID + ")";
        int updated = DB.executeUpdate( sql );

        addLog( 0,null,new BigDecimal( updated ),"@Updated@" );

        // Insert new Products

        sql = "INSERT INTO M_Product_Acct " + "(M_Product_ID, C_AcctSchema_ID," + " AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy," + " P_Revenue_Acct, P_Expense_Acct, P_Asset_Acct, P_CoGs_Acct," + " P_PurchasePriceVariance_Acct, P_InvoicePriceVariance_Acct," + " P_TradeDiscountRec_Acct, P_TradeDiscountGrant_Acct, p_revenueexchange_acct) " + "SELECT p.M_Product_ID, acct.C_AcctSchema_ID," + " p.AD_Client_ID, p.AD_Org_ID, 'Y', now(), "+getAD_User_ID()+", now(), "+getAD_User_ID()+"," + " acct.P_Revenue_Acct, acct.P_Expense_Acct, acct.P_Asset_Acct, acct.P_CoGs_Acct," + " acct.P_PurchasePriceVariance_Acct, acct.P_InvoicePriceVariance_Acct," + " acct.P_TradeDiscountRec_Acct, acct.P_TradeDiscountGrant_Acct, acct.p_revenueexchange_acct " + "FROM M_Product p" + " INNER JOIN M_Product_Category_Acct acct ON (acct.M_Product_Category_ID=p.M_Product_Category_ID)" + "WHERE acct.C_AcctSchema_ID=" + p_C_AcctSchema_ID    // #
              + " AND p.M_Product_Category_ID=" + p_M_Product_Category_ID    // #
              + " AND NOT EXISTS (SELECT * FROM M_Product_Acct pa " + "WHERE pa.M_Product_ID=p.M_Product_ID" + " AND pa.C_AcctSchema_ID=acct.C_AcctSchema_ID)";

        int created = DB.executeUpdate( sql );

        addLog( 0,null,new BigDecimal( created ),"@Created@" );

        return "@Created@=" + created + ", @Updated@=" + updated;
    }    // doIt
}    // ProductCategoryAcctCopy



/*
 *  @(#)ProductCategoryAcctCopy.java   02.07.07
 * 
 *  Fin del fichero ProductCategoryAcctCopy.java
 *  
 *  Versión 2.2
 *
 */
