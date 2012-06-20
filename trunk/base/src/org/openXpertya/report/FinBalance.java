/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.report;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FinBalance extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public FinBalance() {
        super();
        log.info( " " );
    }    // FinBalance

    /** Descripción de Campos */

    protected static CLogger s_log = CLogger.getCLogger( FinBalance.class );

    /** Descripción de Campos */

    private int p_C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    private boolean p_IsRecreate = false;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {

        // Parameter

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "IsRecreate" )) {
                p_IsRecreate = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws java.lang.Exception
     */

    protected String doIt() throws java.lang.Exception {
        log.fine( "C_AcctSchema_ID=" + p_C_AcctSchema_ID + ", IsRecreate=" + p_IsRecreate );

        if( p_C_AcctSchema_ID != 0 ) {
            updateBalance( p_C_AcctSchema_ID,p_IsRecreate );
        } else {
            updateBalanceClient( getCtx(),getAD_Client_ID(),p_IsRecreate );
        }

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     *
     * @return
     */

    public static String deleteBalance( int C_AcctSchema_ID ) {
        StringBuffer sql = new StringBuffer( "DELETE FROM Fact_Acct_Balance WHERE " );

        if( C_AcctSchema_ID != 0 ) {
            sql.append( "C_AcctSchema_ID=" ).append( C_AcctSchema_ID );
        }

        //

        int    no  = DB.executeUpdate( sql.toString());
        String msg = "@Deleted@=" + no;

        s_log.fine( "C_AcctSchema_ID=" + C_AcctSchema_ID + " #=" + no );

        //

        return msg;
    }    // deleteBalance

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     * @param deleteFirst
     *
     * @return
     */

    public static String updateBalance( int C_AcctSchema_ID,boolean deleteFirst ) {
        s_log.info( "C_AcctSchema_ID=" + C_AcctSchema_ID + " - DeleteFirst=" + deleteFirst );

        long start = System.currentTimeMillis();

        if( deleteFirst ) {
            int no = DB.executeUpdate( "DELETE Fact_Acct_Balance WHERE C_AcctSchema_ID=" + C_AcctSchema_ID );

            s_log.fine( "updateBalance - deleted=" + no );
        }

        // Update existing
        // Modificado por ConSerTi para postgresql
        String sql = "UPDATE Fact_Acct_Balance ab " + "SET AmtAcctDr= " + "(SELECT COALESCE(SUM(AmtAcctDr),0) " + "FROM Fact_Acct a " + "WHERE a.AD_Client_ID=ab.AD_Client_ID AND a.AD_Org_ID=ab.AD_Org_ID" + " AND a.C_AcctSchema_ID=ab.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(ab.DateAcct)" + " AND a.Account_ID=ab.Account_ID AND a.PostingType=ab.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(ab.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(ab.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(ab.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(ab.AD_OrgTrx_ID,0)" + " AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(ab.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(ab.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(ab.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(ab.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(ab.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(ab.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(ab.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(ab.GL_Budget_ID,0) " + "GROUP BY AD_Client_ID,AD_Org_ID, C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType, M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID, C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID, User1_ID, User2_ID, GL_Budget_ID) " + "WHERE C_AcctSchema_ID=" + C_AcctSchema_ID + " AND EXISTS (SELECT a.fact_acct_id FROM Fact_Acct a " + "WHERE a.AD_Client_ID=ab.AD_Client_ID AND a.AD_Org_ID=ab.AD_Org_ID" + " AND a.C_AcctSchema_ID=ab.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(ab.DateAcct)" + " AND a.Account_ID=ab.Account_ID AND a.PostingType=ab.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(ab.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(ab.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(ab.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(ab.AD_OrgTrx_ID,0)" + "     AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(ab.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(ab.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(ab.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(ab.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(ab.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(ab.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(ab.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(ab.GL_Budget_ID,0) " + "GROUP BY AD_Client_ID,AD_Org_ID," + " C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType," + " M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID," + " C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID," + " User1_ID, User2_ID, GL_Budget_ID,a.fact_acct_id)";

        if( !deleteFirst ) {
            int no = DB.executeUpdate( sql );

            s_log.fine( "updateBalance - updates=" + no );
        }
        sql = "UPDATE Fact_Acct_Balance ab " + "SET AmtAcctCr= " + "(SELECT COALESCE(SUM(AmtAcctCr),0) " + "FROM Fact_Acct a " + "WHERE a.AD_Client_ID=ab.AD_Client_ID AND a.AD_Org_ID=ab.AD_Org_ID" + " AND a.C_AcctSchema_ID=ab.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(ab.DateAcct)" + " AND a.Account_ID=ab.Account_ID AND a.PostingType=ab.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(ab.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(ab.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(ab.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(ab.AD_OrgTrx_ID,0)" + " AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(ab.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(ab.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(ab.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(ab.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(ab.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(ab.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(ab.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(ab.GL_Budget_ID,0) " + "GROUP BY AD_Client_ID,AD_Org_ID, C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType, M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID, C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID, User1_ID, User2_ID, GL_Budget_ID) " + "WHERE C_AcctSchema_ID=" + C_AcctSchema_ID + " AND EXISTS (SELECT a.fact_acct_id FROM Fact_Acct a " + "WHERE a.AD_Client_ID=ab.AD_Client_ID AND a.AD_Org_ID=ab.AD_Org_ID" + " AND a.C_AcctSchema_ID=ab.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(ab.DateAcct)" + " AND a.Account_ID=ab.Account_ID AND a.PostingType=ab.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(ab.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(ab.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(ab.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(ab.AD_OrgTrx_ID,0)" + "     AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(ab.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(ab.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(ab.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(ab.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(ab.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(ab.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(ab.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(ab.GL_Budget_ID,0) " + "GROUP BY AD_Client_ID,AD_Org_ID," + " C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType," + " M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID," + " C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID," + " User1_ID, User2_ID, GL_Budget_ID,a.fact_acct_id)";
        if( !deleteFirst ) {
            int no = DB.executeUpdate( sql );

            s_log.fine( "updateBalance - updates=" + no );
        }
        sql = "UPDATE Fact_Acct_Balance ab " + "SET Qty= " + "(SELECT COALESCE(SUM(Qty),0) " + "FROM Fact_Acct a " + "WHERE a.AD_Client_ID=ab.AD_Client_ID AND a.AD_Org_ID=ab.AD_Org_ID" + " AND a.C_AcctSchema_ID=ab.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(ab.DateAcct)" + " AND a.Account_ID=ab.Account_ID AND a.PostingType=ab.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(ab.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(ab.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(ab.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(ab.AD_OrgTrx_ID,0)" + " AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(ab.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(ab.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(ab.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(ab.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(ab.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(ab.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(ab.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(ab.GL_Budget_ID,0) " + "GROUP BY AD_Client_ID,AD_Org_ID, C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType, M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID, C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID, User1_ID, User2_ID, GL_Budget_ID) " + "WHERE C_AcctSchema_ID=" + C_AcctSchema_ID + " AND EXISTS (SELECT a.fact_acct_id FROM Fact_Acct a " + "WHERE a.AD_Client_ID=ab.AD_Client_ID AND a.AD_Org_ID=ab.AD_Org_ID" + " AND a.C_AcctSchema_ID=ab.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(ab.DateAcct)" + " AND a.Account_ID=ab.Account_ID AND a.PostingType=ab.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(ab.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(ab.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(ab.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(ab.AD_OrgTrx_ID,0)" + "     AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(ab.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(ab.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(ab.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(ab.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(ab.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(ab.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(ab.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(ab.GL_Budget_ID,0) " + "GROUP BY AD_Client_ID,AD_Org_ID," + " C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType," + " M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID," + " C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID," + " User1_ID, User2_ID, GL_Budget_ID,a.fact_acct_id)";
        if( !deleteFirst ) {
            int no = DB.executeUpdate( sql );

            s_log.fine( "updateBalance - updates=" + no );
        }
        
        sql = "INSERT INTO Fact_Acct_Balance " + "(AD_Client_ID, AD_Org_ID, C_AcctSchema_ID, DateAcct," + " Account_ID, PostingType, M_Product_ID, C_BPartner_ID," + "     C_Project_ID, AD_OrgTrx_ID,     C_SalesRegion_ID,C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID, User1_ID, User2_ID, GL_Budget_ID," + " AmtAcctDr, AmtAcctCr, Qty) "

        //

        + "SELECT AD_Client_ID, AD_Org_ID, C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType, M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID, C_SalesRegion_ID,C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID, User1_ID, User2_ID, GL_Budget_ID," + " COALESCE(SUM(AmtAcctDr),0), COALESCE(SUM(AmtAcctCr),0), COALESCE(SUM(Qty),0) " + "FROM Fact_Acct a " + "WHERE C_AcctSchema_ID=" + C_AcctSchema_ID;

        if( !deleteFirst ) {
            sql += " AND NOT EXISTS (SELECT * " + "FROM Fact_Acct_Balance x " + "WHERE a.AD_Client_ID=x.AD_Client_ID AND a.AD_Org_ID=x.AD_Org_ID" + " AND a.C_AcctSchema_ID=x.C_AcctSchema_ID AND TRUNC(a.DateAcct)=TRUNC(x.DateAcct)" + " AND a.Account_ID=x.Account_ID AND a.PostingType=x.PostingType" + " AND COALESCE(a.M_Product_ID,0)=COALESCE(x.M_Product_ID,0) AND COALESCE(a.C_BPartner_ID,0)=COALESCE(x.C_BPartner_ID,0)" + " AND COALESCE(a.C_Project_ID,0)=COALESCE(x.C_Project_ID,0) AND COALESCE(a.AD_OrgTrx_ID,0)=COALESCE(x.AD_OrgTrx_ID,0)" + " AND COALESCE(a.C_SalesRegion_ID,0)=COALESCE(x.C_SalesRegion_ID,0) AND COALESCE(a.C_Activity_ID,0)=COALESCE(x.C_Activity_ID,0)" + " AND COALESCE(a.C_Campaign_ID,0)=COALESCE(x.C_Campaign_ID,0) AND COALESCE(a.C_LocTo_ID,0)=COALESCE(x.C_LocTo_ID,0) AND COALESCE(a.C_LocFrom_ID,0)=COALESCE(x.C_LocFrom_ID,0)" + " AND COALESCE(a.User1_ID,0)=COALESCE(x.User1_ID,0) AND COALESCE(a.User2_ID,0)=COALESCE(x.User2_ID,0) AND COALESCE(a.GL_Budget_ID,0)=COALESCE(x.GL_Budget_ID,0) )";
        }

        sql += " GROUP BY AD_Client_ID,AD_Org_ID, C_AcctSchema_ID, TRUNC(DateAcct)," + " Account_ID, PostingType, M_Product_ID, C_BPartner_ID," + " C_Project_ID, AD_OrgTrx_ID, C_SalesRegion_ID, C_Activity_ID," + " C_Campaign_ID, C_LocTo_ID, C_LocFrom_ID, User1_ID, User2_ID, GL_Budget_ID";

        int no = DB.executeUpdate( sql );

        s_log.fine( "inserts=" + no );
        start = System.currentTimeMillis() - start;
        s_log.info(( start / 1000 ) + " sec" );

        return "";
    }    // updateBalance

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param deleteFirst
     *
     * @return
     */

    public static String updateBalanceClient( Properties ctx,int AD_Client_ID,boolean deleteFirst ) {
        MAcctSchema[] ass = MAcctSchema.getClientAcctSchema( ctx,AD_Client_ID );

        for( int i = 0;i < ass.length;i++ ) {
            updateBalance( ass[ i ].getC_AcctSchema_ID(),deleteFirst );
        }

        return "";
    }    // updateBalanceClient

    /**
     * Descripción de Método
     *
     *
     * @param args
     */

    public static void main( String[] args ) {
        FinBalance finBalance1 = new FinBalance();
    }    // main
}    // FinBalance



/*
 *  @(#)FinBalance.java   22.03.06
 * 
 *  Fin del fichero FinBalance.java
 *  
 *  Versión 2.0
 *
 */
