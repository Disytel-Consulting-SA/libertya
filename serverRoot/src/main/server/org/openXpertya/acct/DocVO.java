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
import java.sql.Timestamp;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocVO {

    /**
     * Constructor de la clase ...
     *
     */

    protected DocVO() {

        // Amounts

        Amounts[ 0 ] = Env.ZERO;
        Amounts[ 1 ] = Env.ZERO;
        Amounts[ 2 ] = Env.ZERO;
        Amounts[ 3 ] = Env.ZERO;
    }    // DocVO

    // --- ID

    /** Descripción de Campos */

    public String DocumentType;

    // --- Mandatory

    /** Descripción de Campos */

    public int AD_Client_ID = 0;

    /** Descripción de Campos */

    public int AD_Org_ID = 0;

    // --- Optional

    /** Descripción de Campos */

    public int C_BPartner_ID = 0;

    /** Descripción de Campos */

    public int M_Product_ID = 0;

    /** Descripción de Campos */

    public int AD_OrgTrx_ID = 0;

    /** Descripción de Campos */

    public int C_SalesRegion_ID = 0;

    /** Descripción de Campos */

    public int C_Project_ID = 0;

    /** Descripción de Campos */

    public int C_Campaign_ID = 0;

    /** Descripción de Campos */

    public int C_Activity_ID = 0;

    /** Descripción de Campos */

    public int C_LocFrom_ID = 0;

    /** Descripción de Campos */

    public int C_LocTo_ID = 0;

    /** Descripción de Campos */

    public int User1_ID = 0;

    /** Descripción de Campos */

    public int User2_ID = 0;

    // Reference (to find SalesRegion from BPartner)

    /** Descripción de Campos */

    public int C_BPartner_Location_ID = 0;

    /** Descripción de Campos */

    public int BP_C_SalesRegion_ID = -1;    // set in FactLine

    // --- Attributes

    /** Descripción de Campos */

    public String DocumentNo = null;

    /** Descripción de Campos */

    public Timestamp DateAcct = null;

    /** Descripción de Campos */

    public Timestamp DateDoc = null;

    /** Descripción de Campos */

    public int C_ConversionType_ID = 0;

    /** Descripción de Campos */

    public String Description = null;

    /** Descripción de Campos */

    public int C_Period_ID = -1;

    /** Descripción de Campos */

    public boolean TaxIncluded = false;

    /** Descripción de Campos */

    public boolean MultiCurrency = false;

    // --  Reference

    /** Descripción de Campos */

    public int GL_Category_ID = 0;

    /** Descripción de Campos */

    public int GL_Budget_ID = 0;

    /** Descripción de Campos */

    public int C_BankAccount_ID = 0;

    /** Descripción de Campos */

    public int C_CashBook_ID = 0;

    /** Descripción de Campos */

    public int C_Charge_ID = 0;

    /** Descripción de Campos */

    public BigDecimal ChargeAmt = null;

    /** Descripción de Campos */

    public int M_Warehouse_ID = 0;

    /** Descripción de Campos */

    public boolean Posted = false;

    /** Descripción de Campos */

    public int UpdatedBy = 0;

    /** Descripción de Campos */

    public int C_Currency_ID = 0;

    /** Descripción de Campos */

    public BigDecimal[] Amounts = new BigDecimal[ 4 ];

    /** Descripción de Campos */

    public BigDecimal Qty = null;

    /** Descripción de Campos */

    public String Status = null;

    /** Descripción de Campos */

    public String Error = null;

    /** Descripción de Campos */

    public int C_DocType_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Doc=[" );

        sb.append( DocumentType ).append( " - DocumentNo=" ).append( DocumentNo );

        if( DateAcct != null ) {
            sb.append( ",DateAcct=" ).append( DateAcct.toString().substring( 0,10 ));
        }

        sb.append( ",Sta=" ).append( Status ).append( "]" );

        return sb.toString();
    }    // toString
}    // DocVO



/*
 *  @(#)DocVO.java   24.03.06
 * 
 *  Fin del fichero DocVO.java
 *  
 *  Versión 2.2
 *
 */
