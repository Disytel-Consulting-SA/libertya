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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MJournal;
import org.openXpertya.model.MMatchInv;
import org.openXpertya.model.MMatchPO;
import org.openXpertya.model.MMovement;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPeriodControl;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MRequisition;
import org.openXpertya.model.X_M_Production;
import org.openXpertya.report.FinBalance;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FactAcctReset extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int p_AD_Table_ID = 0;

    /** Descripción de Campos */

    private boolean p_DeletePosting = false;

    /** Descripción de Campos */

    private int m_countReset = 0;

    /** Descripción de Campos */

    private int m_countDelete = 0;

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
            } else if( name.equals( "AD_Client_ID" )) {
                p_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "AD_Table_ID" )) {
                p_AD_Table_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DeletePosting" )) {
                p_DeletePosting = "Y".equals( para[ i ].getParameter());
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
        log.info( "AD_Client_ID=" + p_AD_Client_ID + ", AD_Table_ID=" + p_AD_Table_ID + ", DeletePosting=" + p_DeletePosting );

        //

        String sql = "SELECT AD_Table_ID, TableName " + "FROM AD_Table t " + "WHERE t.IsView='N'";

        if( p_AD_Table_ID > 0 ) {
            sql += " AND t.AD_Table_ID=" + p_AD_Table_ID;
        }

        sql += " AND EXISTS (SELECT * FROM AD_Column c " + "WHERE t.AD_Table_ID=c.AD_Table_ID AND c.ColumnName='Posted' AND c.IsActive='Y')";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int    AD_Table_ID = rs.getInt( 1 );
                String TableName   = rs.getString( 2 );

                if( p_DeletePosting ) {
                    delete( TableName,AD_Table_ID );
                } else {
                    reset( TableName );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "@Updated@ = " + m_countReset + ", @Deleted@ = " + m_countDelete;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     */

    private void reset( String TableName ) {
        String sql = "UPDATE " + TableName + " SET Processing='N' WHERE AD_Client_ID=" + p_AD_Client_ID + " AND (Processing<>'N' OR Processing IS NULL)";
        int unlocked = DB.executeUpdate( sql,get_TrxName());

        //

        sql = "UPDATE " + TableName + " SET Posted='N' WHERE AD_Client_ID=" + p_AD_Client_ID + " AND (Posted NOT IN ('Y','N') OR Posted IS NULL) AND Processed='Y'";

        int invalid = DB.executeUpdate( sql,get_TrxName());

        //

        if( unlocked + invalid != 0 ) {
            log.fine( TableName + " - Unlocked=" + unlocked + " - Invalid=" + invalid );
        }

        m_countReset += unlocked + invalid;
    }    // reset

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     * @param AD_Table_ID
     */

    private void delete( String TableName,int AD_Table_ID ) {
        reset( TableName );
        m_countReset = 0;

        //

        String docBaseType = null;

        if( AD_Table_ID == MInvoice.Table_ID ) {
            docBaseType = "IN ('" + MPeriodControl.DOCBASETYPE_APInvoice + "','" + MPeriodControl.DOCBASETYPE_ARInvoice + "','" + MPeriodControl.DOCBASETYPE_APCreditMemo + "','" + MPeriodControl.DOCBASETYPE_ARCreditMemo + "','" + MPeriodControl.DOCBASETYPE_ARProFormaInvoice + "')";
        } else if( AD_Table_ID == MInOut.Table_ID ) {
            docBaseType = "IN ('" + MPeriodControl.DOCBASETYPE_MaterialDelivery + "','" + MPeriodControl.DOCBASETYPE_MaterialReceipt + "')";
        } else if( AD_Table_ID == MPayment.Table_ID ) {
            docBaseType = "IN ('" + MPeriodControl.DOCBASETYPE_APPayment + "','" + MPeriodControl.DOCBASETYPE_ARReceipt + "')";
        } else if( AD_Table_ID == MOrder.Table_ID ) {
            docBaseType = "IN ('" + MPeriodControl.DOCBASETYPE_SalesOrder + "','" + MPeriodControl.DOCBASETYPE_PurchaseOrder + "')";
        } else if( AD_Table_ID == MProject.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_ProjectIssue + "'";
        } else if( AD_Table_ID == MBankStatement.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_BankStatement + "'";
        } else if( AD_Table_ID == MCash.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_CashJournal + "'";
        } else if( AD_Table_ID == MAllocationHdr.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_PaymentAllocation + "'";
        } else if( AD_Table_ID == MJournal.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_GLJournal + "'";

            // else if (AD_Table_ID == M.Table_ID)
            // docBaseType = "= '" + MPeriodControl.DOCBASETYPE_GLDocument + "'";

        } else if( AD_Table_ID == MMovement.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MaterialMovement + "'";
        } else if( AD_Table_ID == MRequisition.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_PurchaseRequisition + "'";
        } else if( AD_Table_ID == MInventory.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MaterialPhysicalInventory + "'";
        } else if( AD_Table_ID == X_M_Production.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MaterialProduction + "'";
        } else if( AD_Table_ID == MMatchInv.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MatchInvoice + "'";
        } else if( AD_Table_ID == MMatchPO.Table_ID ) {
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MatchPO + "'";
        }

        //

        if( docBaseType == null ) {
            log.severe( "Unknown DocBaseType for Table=" + TableName );
            docBaseType = "";
        } else {
            docBaseType = " AND pc.DocBaseType " + docBaseType;
        }

        // Doc

        String sql = "UPDATE " + TableName + " doc" + " SET Posted='N', Processing='N' " + "WHERE AD_Client_ID=" + p_AD_Client_ID + " AND (Posted<>'N' OR Posted IS NULL OR Processing<>'N' OR Processing IS NULL)" + " AND EXISTS (SELECT * FROM C_PeriodControl pc" + " INNER JOIN Fact_Acct fact ON (fact.C_Period_ID=pc.C_Period_ID) " + "WHERE pc.PeriodStatus = 'O'" + docBaseType + " AND fact.AD_Table_ID=" + AD_Table_ID + " AND fact.Record_ID=doc." + TableName + "_ID)";
        int reset = DB.executeUpdate( sql,get_TrxName());

        // Fact

        sql = "DELETE Fact_Acct fact " + "WHERE AD_Client_ID=" + p_AD_Client_ID + " AND AD_Table_ID=" + AD_Table_ID + " AND EXISTS (SELECT * FROM C_PeriodControl pc " + "WHERE pc.PeriodStatus = 'O'" + docBaseType + " AND fact.C_Period_ID=pc.C_Period_ID)";

        int deleted = DB.executeUpdate( sql,get_TrxName());

        // Balances

        FinBalance.updateBalanceClient( getCtx(),p_AD_Client_ID,true );    // delete

        //

        log.info( TableName + "(" + AD_Table_ID + ") - Reset=" + reset + " - Deleted=" + deleted );
        m_countReset  += reset;
        m_countDelete += deleted;
    }    // delete
}    // FactAcctReset



/*
 *  @(#)FactAcctReset.java   02.07.07
 * 
 *  Fin del fichero FactAcctReset.java
 *  
 *  Versión 2.2
 *
 */
