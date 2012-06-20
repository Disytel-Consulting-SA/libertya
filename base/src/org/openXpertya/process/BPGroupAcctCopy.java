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

import javax.swing.JOptionPane;

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

public class BPGroupAcctCopy extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_BP_Group_ID = 0;

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
            //JOptionPane.showMessageDialog( null,"En Prepare del CbpgroupAcctCopy con = "+"name= "+name+"\n"+"y el numero de parametros es"+ para.length,"..", JOptionPane.INFORMATION_MESSAGE );

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_BP_Group_ID" )) {
                p_C_BP_Group_ID = para[ i ].getParameterAsInt();
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

        //

        String sql          = null;
        int    updated      = 0;
        int    created      = 0;
        int    updatedTotal = 0;
        int    createdTotal = 0;

        // Update existing Customers

     
       // c_salesprevision_acct y c_receivable_acct y C_PrePayment_Acct
        
        sql = "UPDATE C_BP_Customer_Acct SET  C_PrePayment_Acct=bp.C_PrePayment_Acct,  c_salesprevision_acct= bp.c_salesprevision_acct,C_Receivable_Acct = bp.C_Receivable_Acct, Updated=CURRENT_TIMESTAMP, UpdatedBy=0";
        sql +=" FROM C_BP_Group_Acct bp WHERE C_BP_Group_ID=" + p_C_BP_Group_ID + " AND C_BP_Customer_Acct.C_AcctSchema_ID=" + p_C_AcctSchema_ID ;
        sql += " and bp.C_AcctSchema_ID=" + p_C_AcctSchema_ID + " AND  EXISTS (SELECT * FROM C_BPartner p WHERE p.C_BPartner_ID=C_BP_Customer_Acct.C_BPartner_ID AND p.C_BP_Group_ID=" + p_C_BP_Group_ID+ ")";
        
        //sql = "UPDATE C_BP_Customer_Acct ca " + "SET ( c_salesprevision_acct)=" + " (SELECT  c_salesprevision_acct FROM C_BP_Group_Acct  WHERE C_BP_Group_ID=" + p_C_BP_Group_ID + " AND C_AcctSchema_ID=" + p_C_AcctSchema_ID + "), Updated=SysDate, UpdatedBy=0 " + "WHERE ca.C_AcctSchema_ID=" + p_C_AcctSchema_ID + " AND EXISTS (SELECT * FROM C_BPartner p " + "WHERE p.C_BPartner_ID=ca.C_BPartner_ID" + " AND p.C_BP_Group_ID=" + p_C_BP_Group_ID + ")";
        updated = DB.executeUpdate( sql );
        addLog( 0,null,new BigDecimal( updated ),"@Updated@ @C_BPartner_ID@ @IsCustomer@" );
        updatedTotal += updated;
       
        
        
        // Insert new Customer

        sql = "INSERT INTO C_BP_Customer_Acct " + "(C_BPartner_ID, C_AcctSchema_ID," + " AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy," + " C_Receivable_Acct, C_PrePayment_Acct) " + "SELECT p.C_BPartner_ID, acct.C_AcctSchema_ID," + " p.AD_Client_ID, p.AD_Org_ID, 'Y', SysDate, 0, SysDate, 0," + " acct.C_Receivable_Acct, acct.C_PrePayment_Acct " + "FROM C_BPartner p" + " INNER JOIN C_BP_Group_Acct acct ON (acct.C_BP_Group_ID=p.C_BP_Group_ID)" + "WHERE acct.C_AcctSchema_ID=" + p_C_AcctSchema_ID    // #
              + " AND p.C_BP_Group_ID=" + p_C_BP_Group_ID + " AND NOT EXISTS (SELECT * FROM C_BP_Customer_Acct ca " + "WHERE ca.C_BPartner_ID=p.C_BPartner_ID" + " AND ca.C_AcctSchema_ID=acct.C_AcctSchema_ID)";
        created = DB.executeUpdate( sql );
        addLog( 0,null,new BigDecimal( created ),"@Created@ @C_BPartner_ID@ @IsCustomer@" );
        createdTotal += created;

        // Update existing Vendors

        sql = "UPDATE C_BP_Vendor_Acct va " + "SET (V_Liability_Acct)=" + " (SELECT V_Liability_Acct  FROM C_BP_Group_Acct" + " WHERE C_BP_Group_ID=" + p_C_BP_Group_ID + " AND C_AcctSchema_ID=" + p_C_AcctSchema_ID + "), Updated=SysDate, UpdatedBy=0 " + "WHERE va.C_AcctSchema_ID=" + p_C_AcctSchema_ID + " AND EXISTS (SELECT * FROM C_BPartner p " + "WHERE p.C_BPartner_ID=va.C_BPartner_ID" + " AND p.C_BP_Group_ID=" + p_C_BP_Group_ID + ")";
        updated = DB.executeUpdate( sql );
        addLog( 0,null,new BigDecimal( updated ),"@Updated@ @C_BPartner_ID@ @IsVendor@" );
        updatedTotal += updated;

        sql = "UPDATE C_BP_Vendor_Acct va " + "SET (V_Liability_Services_Acct)=" + " (SELECT V_Liability_Services_Acct FROM C_BP_Group_Acct" + " WHERE C_BP_Group_ID=" + p_C_BP_Group_ID + " AND C_AcctSchema_ID=" + p_C_AcctSchema_ID + "), Updated=SysDate, UpdatedBy=0 " + "WHERE va.C_AcctSchema_ID=" + p_C_AcctSchema_ID + " AND EXISTS (SELECT * FROM C_BPartner p " + "WHERE p.C_BPartner_ID=va.C_BPartner_ID" + " AND p.C_BP_Group_ID=" + p_C_BP_Group_ID + ")";
        updated = DB.executeUpdate( sql );
        addLog( 0,null,new BigDecimal( updated ),"@Updated@ @C_BPartner_ID@ @IsVendor@" );
        updatedTotal += updated;
        
        sql = "UPDATE C_BP_Vendor_Acct va " + "SET (V_PrePayment_Acct)=" + " (SELECT V_PrePayment_Acct " + " FROM C_BP_Group_Acct" + " WHERE C_BP_Group_ID=" + p_C_BP_Group_ID + " AND C_AcctSchema_ID=" + p_C_AcctSchema_ID + "), Updated=SysDate, UpdatedBy=0 " + "WHERE va.C_AcctSchema_ID=" + p_C_AcctSchema_ID + " AND EXISTS (SELECT * FROM C_BPartner p " + "WHERE p.C_BPartner_ID=va.C_BPartner_ID" + " AND p.C_BP_Group_ID=" + p_C_BP_Group_ID + ")";
        updated = DB.executeUpdate( sql );
        addLog( 0,null,new BigDecimal( updated ),"@Updated@ @C_BPartner_ID@ @IsVendor@" );
        updatedTotal += updated;
        
        
        
        
        
        // Insert new Vendors

        sql = "INSERT INTO C_BP_Vendor_Acct " + "(C_BPartner_ID, C_AcctSchema_ID," + " AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy," + " V_Liability_Acct, V_Liability_Services_Acct, V_PrePayment_Acct) " + "SELECT p.C_BPartner_ID, acct.C_AcctSchema_ID," + " p.AD_Client_ID, p.AD_Org_ID, 'Y', SysDate, 0, SysDate, 0," + " acct.V_Liability_Acct, acct.V_Liability_Services_Acct, acct.V_PrePayment_Acct " + "FROM C_BPartner p" + " INNER JOIN C_BP_Group_Acct acct ON (acct.C_BP_Group_ID=p.C_BP_Group_ID)" + "WHERE acct.C_AcctSchema_ID=" + p_C_AcctSchema_ID    // #
              + " AND p.C_BP_Group_ID=" + p_C_BP_Group_ID + " AND NOT EXISTS (SELECT * FROM C_BP_Vendor_Acct va " + "WHERE va.C_BPartner_ID=p.C_BPartner_ID AND va.C_AcctSchema_ID=acct.C_AcctSchema_ID)";
        created = DB.executeUpdate( sql );
        addLog( 0,null,new BigDecimal( created ),"@Created@ @C_BPartner_ID@ @IsVendor@" );
        createdTotal += created;

        return "@Created@=" + createdTotal + ", @Updated@=" + updatedTotal;
    }    // doIt
}    // BPGroupAcctCopy



/*
 *  @(#)BPGroupAcctCopy.java   02.07.07
 * 
 *  Fin del fichero BPGroupAcctCopy.java
 *  
 *  Versión 2.2
 *
 */
