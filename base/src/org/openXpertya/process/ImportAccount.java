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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MElementValue;
import org.openXpertya.model.X_I_ElementValue;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ImportAccount extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private int m_C_Element_ID = 0;

    /** Descripción de Campos */

    private boolean m_updateDefaultAccounts = false;

    /** Descripción de Campos */

    private boolean m_createNewCombination = true;

    /** Descripción de Campos */

    private boolean m_deleteOldImported = false;

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateValue = null;

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
                m_AD_Client_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Element_ID" )) {
                m_C_Element_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "UpdateDefaultAccounts" )) {
                m_updateDefaultAccounts = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "CreateNewCombination" )) {
                m_createNewCombination = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "DeleteOldImported" )) {
                m_deleteOldImported = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        if( m_DateValue == null ) {
            m_DateValue = new Timestamp( System.currentTimeMillis());
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
        StringBuffer sql         = null;
        int          no          = 0;
        String       clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

        // ****    Prepare ****

        // Delete Old Imported

        if( m_deleteOldImported ) {
            sql = new StringBuffer( "DELETE FROM I_ElementValue " + "WHERE I_IsImported='Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString(), get_TrxName());
            log.fine( "Delete Old Imported =" + no );
        }

        // Set Client, Org, IsActive, Created/Updated

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET AD_Client_ID = COALESCE (AD_Client_ID, " ).append( m_AD_Client_ID ).append( ")," + " AD_Org_ID = COALESCE (AD_Org_ID, 0)," + " IsActive = COALESCE (IsActive, 'Y')," + " Created = COALESCE (Created, SysDate)," + " CreatedBy = COALESCE (CreatedBy, 0)," + " Updated = COALESCE (Updated, SysDate)," + " UpdatedBy = COALESCE (UpdatedBy, 0)," + " I_ErrorMsg = NULL," + " I_IsImported = 'N' " + "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL" );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Reset=" + no );

        // ****    Prepare ****

        // Set Element

        if( m_C_Element_ID != 0 ) {
            sql = new StringBuffer( "UPDATE I_ElementValue " + "SET ElementName=(SELECT Name FROM C_Element WHERE C_Element_ID=" ).append( m_C_Element_ID ).append( ") " + "WHERE ElementName IS NULL AND C_Element_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString(), get_TrxName());
            log.fine( "Set Element Default=" + no );
        }

        //

        sql = new StringBuffer( "UPDATE I_ElementValue i " + "SET C_Element_ID = (SELECT C_Element_ID FROM C_Element e" + " WHERE i.ElementName=e.Name AND i.AD_Client_ID=e.AD_Client_ID)" + "WHERE C_Element_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Set Element=" + no );

        //

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_IsImported='E', I_ErrorMsg='ERR=Invalid Element, ' " + "WHERE C_Element_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.config( "Invalid Element=" + no );

        // Set Column

        sql = new StringBuffer( "UPDATE I_ElementValue i " + "SET AD_Column_ID = (SELECT AD_Column_ID FROM AD_Column c" + " WHERE UPPER(i.Default_Account)=UPPER(c.ColumnName)" + " AND c.AD_Table_ID IN (315,266) AND AD_Reference_ID=25) " + "WHERE Default_Account IS NOT NULL AND AD_Column_ID IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Set Column=" + no );

        //

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Column, ' " + "WHERE AD_Column_ID IS NULL AND Default_Account IS NOT NULL" + " AND UPPER(Default_Account)<>'DEFAULT_ACCT'"    // ignore default account
                                + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.config( "Invalid Column=" + no );

        // Set Post* Defaults (ignore errors)

        String[] yColumns = new String[]{ "PostActual","PostBudget","PostStatistical","PostEncumbrance" };

        for( int i = 0;i < yColumns.length;i++ ) {
            sql = new StringBuffer( "UPDATE I_ElementValue SET " ).append( yColumns[ i ] ).append( "='Y' WHERE " ).append( yColumns[ i ] ).append( " IS NULL OR " ).append( yColumns[ i ] ).append( " NOT IN ('Y','N')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
            no = DB.executeUpdate( sql.toString(), get_TrxName());
            log.fine( "Set " + yColumns[ i ] + " Default=" + no );
        }

        // Summary

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET IsSummary='N' " + "WHERE IsSummary IS NULL OR IsSummary NOT IN ('Y','N')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Set IsSummary Default=" + no );

        // Doc Controlled

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET IsDocControlled = CASE WHEN AD_Column_ID IS NOT NULL THEN 'Y' ELSE 'N' END " + "WHERE IsDocControlled IS NULL OR IsDocControlled NOT IN ('Y','N')" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Set IsDocumentControlled Default=" + no );

        // Check Account Type A (E) L M O R

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET AccountType='E' " + "WHERE AccountType IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Set AccountType Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid AccountType, ' " + "WHERE AccountType NOT IN ('A','E','L','M','O','R')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.config( "Invalid AccountType=" + no );

        // Check Account Sign (N) C B

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET AccountSign='N' " + "WHERE AccountSign IS NULL" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Set AccountSign Default=" + no );

        //

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid AccountSign, ' " + "WHERE AccountSign NOT IN ('N','C','D')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.config( "Invalid AccountSign=" + no );

        // No Value

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Key, ' " + "WHERE (Value IS NULL OR Value='')" + " AND I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.config( "Invalid Key=" + no );

        // ****    Update ElementValue from existing

        sql = new StringBuffer( "UPDATE I_ElementValue i " + "SET C_ElementValue_ID=(SELECT C_ElementValue_ID FROM C_ElementValue ev" + " INNER JOIN C_Element e ON (ev.C_Element_ID=e.C_Element_ID)" + " WHERE i.C_Element_ID=e.C_Element_ID AND i.AD_Client_ID=e.AD_Client_ID" + " AND i.Value=ev.Value) " + "WHERE C_ElementValue_ID IS NULL" + " AND I_IsImported='N'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Found ElementValue=" + no );

        // Disable Trigger Updateing Description

        no = DB.executeUpdate( "ALTER TABLE C_ValidCombination DISABLE TRIGGER ALL" , get_TrxName());
        log.fine( "Disable Description Update =" + no );

        // -------------------------------------------------------------------

        int noInsert = 0;
        int noUpdate = 0;

        // Go through Records

        sql = new StringBuffer( "SELECT * " + "FROM I_ElementValue " + "WHERE I_IsImported='N'" ).append( clientCheck ).append( " ORDER BY I_ElementValue_ID" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString(), get_TrxName());
            ResultSet         rs    = pstmt.executeQuery();

            while( rs.next()) {
                X_I_ElementValue impEV = new X_I_ElementValue( getCtx(),rs,get_TrxName());
                int C_ElementValue_ID = impEV.getC_ElementValue_ID();
                int I_ElementValue_ID = impEV.getI_ElementValue_ID();

                // ****    Create/Update ElementValue

                if( C_ElementValue_ID == 0 )    // New
                {
                    MElementValue ev = new MElementValue( impEV );

                    if( ev.save()) {
                        noInsert++;
                        impEV.setC_ElementValue_ID( ev.getC_ElementValue_ID());
                        impEV.setI_IsImported( true );
                        impEV.save();
                    } else {
                        sql = new StringBuffer( "UPDATE I_ElementValue i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Insert ElementValue " )).append( "WHERE I_ElementValue_ID=" ).append( I_ElementValue_ID );
                        DB.executeUpdate( sql.toString(), get_TrxName());
                    }
                } else    // Update existing
                {
                    MElementValue ev = new MElementValue( getCtx(),C_ElementValue_ID, get_TrxName());

                    if( ev.getID() != C_ElementValue_ID ) {}

                    ev.set( impEV );

                    if( ev.save()) {
                        noUpdate++;
                        impEV.setI_IsImported( true );
                        impEV.save();
                    } else {
                        sql = new StringBuffer( "UPDATE I_ElementValue i " + "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||" ).append( DB.TO_STRING( "Update ElementValue" )).append( "WHERE I_ElementValue_ID=" ).append( I_ElementValue_ID );
                        DB.executeUpdate( sql.toString(), get_TrxName());
                    }
                }
            }    // for all I_Product

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            throw new Exception( "create",e );
        }

        // Set Error to indicator to not imported

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_IsImported='N', Updated=SysDate " + "WHERE I_IsImported<>'Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        addLog( 0,null,new BigDecimal( no ),"@Errors@" );
        addLog( 0,null,new BigDecimal( noInsert ),"@C_ElementValue_ID@: @Inserted@" );
        addLog( 0,null,new BigDecimal( noUpdate ),"@C_ElementValue_ID@: @Updated@" );

        // *****   Set Parent

        sql = new StringBuffer( "UPDATE I_ElementValue i " + "SET ParentElementValue_ID=(SELECT C_ElementValue_ID" + " FROM C_ElementValue ev WHERE i.C_Element_ID=ev.C_Element_ID" + " AND i.ParentValue=ev.Value AND i.AD_Client_ID=ev.AD_Client_ID LIMIT 1) " + "WHERE ParentElementValue_ID IS NULL" + " AND I_IsImported='Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Found Parent ElementValue=" + no );

        //

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET I_ErrorMsg=I_ErrorMsg||'Info=ParentNotFound, ' " + "WHERE ParentElementValue_ID IS NULL AND ParentValue IS NOT NULL" + " AND I_IsImported='Y'" ).append( clientCheck );
        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.config( "Not Found Patent ElementValue=" + no );

        //

        sql = new StringBuffer( "SELECT i.ParentElementValue_ID, i.I_ElementValue_ID," + " e.AD_Tree_ID, i.C_ElementValue_ID, i.Value||'-'||i.Name AS Info " + "FROM I_ElementValue i" + " INNER JOIN C_Element e ON (i.C_Element_ID=e.C_Element_ID) " + "WHERE i.C_ElementValue_ID IS NOT NULL AND e.AD_Tree_ID IS NOT NULL" + " AND i.ParentElementValue_ID IS NOT NULL" + " AND i.I_IsImported='Y' AND i.AD_Client_ID=" ).append( m_AD_Client_ID );

        int noParentUpdate = 0;

        try {
            Statement stmt = DB.createStatement(get_TrxName());
            ResultSet rs   = stmt.executeQuery(sql.toString());

            //

            String updateSQL = "UPDATE AD_TreeNode SET Parent_ID=?, SeqNo=? " + "WHERE AD_Tree_ID=? AND Node_ID=?";
            
            // Modificacion realizada por Dataware Sistemas 16/05/2006
            PreparedStatement updateStmt = DB.prepareStatement( updateSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, get_TrxName());
            
            //Original
            //PreparedStatement updateStmt = DB.prepareStatement( updateSQL );
            //

            while( rs.next()) {
     
                updateStmt.setInt( 1,rs.getInt( 1 ));    // Parent
                updateStmt.setInt( 2,rs.getInt( 2 ));    // SeqNo (assume sequenec in import is the same)
                updateStmt.setInt( 3,rs.getInt( 3 ));    // Tree
                updateStmt.setInt( 4,rs.getInt( 4 ));    // Node

                try {
                    no             = updateStmt.executeUpdate();
                    noParentUpdate += no;
                } catch( SQLException ex ) {
                    log.log( Level.SEVERE,"ImportAccount.doIt (ParentUpdate)",ex );
                    no = 0;
                }

                if( no == 0 ) {
                    log.info( "Parent not found for " + rs.getString( 5 ));
                }
            }

            rs.close();
            stmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"ImportAccount.doIt (ParentUpdateLoop) " + sql.toString(),e );
        }

        addLog( 0,null,new BigDecimal( noParentUpdate ),"@ParentElementValue_ID@: @Updated@" );

        // Reset Processing Flag

        sql = new StringBuffer( "UPDATE I_ElementValue " + "SET Processing='-'" + "WHERE I_IsImported='Y' AND Processed='Y' AND Processing='Y'" + " AND C_ElementValue_ID IS NOT NULL" ).append( clientCheck );

        if( m_updateDefaultAccounts ) {
            sql.append( " AND AD_Column_ID IS NULL" );
        }

        no = DB.executeUpdate( sql.toString(), get_TrxName());
        log.fine( "Reset Processing Flag=" + no );

        if( m_updateDefaultAccounts ) {
            updateDefaults( clientCheck );
        }

        // Re-Enable Triggers

        no = DB.executeUpdate( "ALTER TABLE C_ValidCombination ENABLE TRIGGER ALL", get_TrxName() );
        log.fine( "Enable Description Update =" + no );

        // Update Description

        no = DB.executeUpdate( "UPDATE C_ValidCombination SET Updated=SysDate WHERE AD_Client_ID=" + m_AD_Client_ID, get_TrxName() );
        log.fine( "Update Account Description =" + no );

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param clientCheck
     */

    private void updateDefaults( String clientCheck ) {
        log.config( "CreateNewCombination=" + m_createNewCombination );

        // ****    Update Defaults

        StringBuffer sql = new StringBuffer( "SELECT C_AcctSchema_ID FROM C_AcctSchema_Element " + "WHERE C_Element_ID=?" ).append( clientCheck );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString(), get_TrxName());

            pstmt.setInt( 1,m_C_Element_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                updateDefaultAccounts( rs.getInt( 1 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"ImportAccount.updateDefaults",e );
        }

        // Default Account         DEFAULT_ACCT

        sql = new StringBuffer( "UPDATE C_AcctSchema_Element e " + "SET C_ElementValue_ID=(SELECT C_ElementValue_ID FROM I_ElementValue i" + " WHERE e.C_Element_ID=i.C_Element_ID AND i.C_ElementValue_ID IS NOT NULL" + " AND UPPER(i.Default_Account)='DEFAULT_ACCT') " + "WHERE EXISTS (SELECT * FROM I_ElementValue i" + " WHERE e.C_Element_ID=i.C_Element_ID AND i.C_ElementValue_ID IS NOT NULL" + " AND UPPER(i.Default_Account)='DEFAULT_ACCT' " + "     AND i.I_IsImported='Y')" ).append( clientCheck );

        int no = DB.executeUpdate( sql.toString(), get_TrxName());

        addLog( 0,null,new BigDecimal( no ),"@C_AcctSchema_Element_ID@: @Updated@" );
    }    // updateDefaults

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     */

    private void updateDefaultAccounts( int C_AcctSchema_ID ) {
        log.config( "C_AcctSchema_ID=" + C_AcctSchema_ID );

        MAcctSchema as = new MAcctSchema( getCtx(),C_AcctSchema_ID, get_TrxName());

        if( as.getAcctSchemaElement( "AC" ).getC_Element_ID() != m_C_Element_ID ) {
            log.log( Level.SEVERE,"C_Element_ID=" + m_C_Element_ID + " not in AcctSchema=" + as );

            return;
        }

        int[]  counts = new int[]{ 0,0,0 };
        // Aporte de Saulo Gil segun: https://sourceforge.net/p/libertya/tickets/15/  
        // #15 Importación de Cuentas Contables - No se actualizan cuentas por default 
        String sql    = "SELECT i.C_ElementValue_ID, t.TableName, c.ColumnName, i.I_ElementValue_ID " + "FROM I_ElementValue i" + " INNER JOIN AD_Column c ON (i.AD_Column_ID=c.AD_Column_ID)" + " INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID) " + "WHERE i.I_IsImported='Y' " + /*" AND i.Processed='Y' AND Processing='Y'" +*/ " AND i.C_ElementValue_ID IS NOT NULL AND C_Element_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql, get_TrxName());

            pstmt.setInt( 1,m_C_Element_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int    C_ElementValue_ID = rs.getInt( 1 );
                String TableName         = rs.getString( 2 );
                String ColumnName        = rs.getString( 3 );
                int    I_ElementValue_ID = rs.getInt( 4 );

                // Update it

                int u = updateDefaultAccount( TableName,ColumnName,C_AcctSchema_ID,C_ElementValue_ID );

                counts[ u ]++;

                if( u != UPDATE_ERROR ) {
                    sql = "UPDATE I_ElementValue SET Processing='N' " + "WHERE I_ElementValue_ID=" + I_ElementValue_ID;

                    int no = DB.executeUpdate( sql.toString(), get_TrxName());

                    if( no != 1 ) {
                        log.log( Level.SEVERE,"Updated=" + no );
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"createDefaultAccounts",e );
        }

        addLog( 0,null,new BigDecimal( counts[ UPDATE_ERROR ] ),as.toString() + ": @Errors@" );
        addLog( 0,null,new BigDecimal( counts[ UPDATE_YES ] ),as.toString() + ": @Updated@" );
        addLog( 0,null,new BigDecimal( counts[ UPDATE_SAME ] ),as.toString() + ": OK" );
    }    // createDefaultAccounts

    /** Descripción de Campos */

    private static final int UPDATE_ERROR = 0;

    /** Descripción de Campos */

    private static final int UPDATE_YES = 1;

    /** Descripción de Campos */

    private static final int UPDATE_SAME = 2;

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     * @param ColumnName
     * @param C_AcctSchema_ID
     * @param C_ElementValue_ID
     *
     * @return
     */

    private int updateDefaultAccount( String TableName,String ColumnName,int C_AcctSchema_ID,int C_ElementValue_ID ) {
        log.fine( "ImportAccount.updateDefaultAccount - " + TableName + "." + ColumnName + " - " + C_ElementValue_ID );

        int          retValue = UPDATE_ERROR;
        StringBuffer sql      = new StringBuffer( "SELECT x." ).append( ColumnName ).append( ",Account_ID FROM " ).append( TableName ).append( " x INNER JOIN C_ValidCombination vc ON (x." ).append( ColumnName ).append( "=vc.C_ValidCombination_ID) " ).append( "WHERE x.C_AcctSchema_ID=" ).append( C_AcctSchema_ID );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString(), get_TrxName());
            ResultSet         rs    = pstmt.executeQuery();

            if( rs.next()) {
                int C_ValidCombination_ID = rs.getInt( 1 );
                int Account_ID            = rs.getInt( 2 );

                // The current account value is the same

                if( Account_ID == C_ElementValue_ID ) {
                    retValue = UPDATE_SAME;
                    log.fine( "Account_ID same as new value" );
                }

                // We need to update the Account Value

                else {
                    if( m_createNewCombination ) {
                        MAccount acct = MAccount.get( getCtx(),C_ValidCombination_ID, get_TrxName() );

                        acct.setAccount_ID( C_ElementValue_ID );

                        if( acct.save()) {
                            int newC_ValidCombination_ID = acct.getC_ValidCombination_ID();

                            if( C_ValidCombination_ID != newC_ValidCombination_ID ) {
                                sql = new StringBuffer( "UPDATE " ).append( TableName ).append( " SET " ).append( ColumnName ).append( "=" ).append( newC_ValidCombination_ID ).append( " WHERE C_AcctSchema_ID=" ).append( C_AcctSchema_ID );

                                int no = DB.executeUpdate( sql.toString(), get_TrxName());

                                log.fine( "ImportAccount.updateDefaultAccount - #" + no + " - " + TableName + "." + ColumnName + " - " + C_ElementValue_ID + " -- " + C_ValidCombination_ID + " -> " + newC_ValidCombination_ID );

                                if( no == 1 ) {
                                    retValue = UPDATE_YES;
                                }
                            }
                        } else {
                            log.log( Level.SEVERE,"ImportAccount.updateDefaultAccount - Account not saved - " + acct );
                        }
                    } else    // Replace Combination
                    {

                        // Only Acct Combination directly

                        sql = new StringBuffer( "UPDATE C_ValidCombination SET Account_ID=" ).append( C_ElementValue_ID ).append( " WHERE C_ValidCombination_ID=" ).append( C_ValidCombination_ID );

                        int no = DB.executeUpdate( sql.toString(), get_TrxName());

                        log.fine( "ImportAccount.updateDefaultAccount - Replace #" + no + " - " + "C_ValidCombination_ID=" + C_ValidCombination_ID + ", New Account_ID=" + C_ElementValue_ID );

                        if( no == 1 ) {
                            retValue = UPDATE_YES;

                            // Where Acct was used

                            sql = new StringBuffer( "UPDATE C_ValidCombination SET Account_ID=" ).append( C_ElementValue_ID ).append( " WHERE Account_ID=" ).append( Account_ID );
                            no = DB.executeUpdate( sql.toString(), get_TrxName());
                            log.fine( "ImportAccount.updateDefaultAccount - Replace VC #" + no + " - " + "Account_ID=" + Account_ID + ", New Account_ID=" + C_ElementValue_ID );
                            sql = new StringBuffer( "UPDATE Fact_Acct SET Account_ID=" ).append( C_ElementValue_ID ).append( " WHERE Account_ID=" ).append( Account_ID );
                            no = DB.executeUpdate( sql.toString(), get_TrxName());
                            log.fine( "ImportAccount.updateDefaultAccount - Replace Fact #" + no + " - " + "Account_ID=" + Account_ID + ", New Account_ID=" + C_ElementValue_ID );
                        }
                    }    // replace combination
                }        // need to update
            }            // for all default accounts
                    else {
                log.log( Level.SEVERE,"ImportAccount.updateDefaultAccount - Account not found " + sql );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"ImportAccount.updateDefaultAccount " + sql,e );
        }

        return retValue;
    }    // updateDefaultAccount
}    // ImportAccount



/*
 *  @(#)ImportAccount.java   02.07.07
 * 
 *  Fin del fichero ImportAccount.java
 *  
 *  Versión 2.2
 *
 */
