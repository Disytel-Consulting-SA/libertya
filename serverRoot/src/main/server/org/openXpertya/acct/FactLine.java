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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.model.MRevenueRecognitionPlan;
import org.openXpertya.model.X_Fact_Acct;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class FactLine extends X_Fact_Acct {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_Table_ID
     * @param Record_ID
     * @param Line_ID
     * @param trxName
     */

    public FactLine( Properties ctx,int AD_Table_ID,int Record_ID,int Line_ID,String trxName ) {
        super( ctx,0,trxName );
        setAD_Client_ID( 0 );    // do not derive
        setAD_Org_ID( 0 );       // do not derive

        //

        setAmtAcctCr( Env.ZERO );
        setAmtAcctDr( Env.ZERO );
        setAmtSourceCr( Env.ZERO );
        setAmtSourceDr( Env.ZERO );

        // Log.trace(this,Log.l1_User, "FactLine " + AD_Table_ID + ":" + Record_ID);

        setAD_Table_ID( AD_Table_ID );
        setRecord_ID( Record_ID );
        setLine_ID( Line_ID );
    }    // FactLine

    /** Descripción de Campos */

    private MAccount m_acct = null;

    /** Descripción de Campos */

    private MAcctSchema m_acctSchema = null;

    /** Descripción de Campos */

    private DocVO m_docVO = null;

    /** Descripción de Campos */

    private DocLine m_docLine = null;

    /**
     * Descripción de Método
     *
     *
     * @param description
     *
     * @return
     */

    public FactLine reverse( String description ) {
        FactLine reversal = new FactLine( getCtx(),getAD_Table_ID(),getRecord_ID(),getLine_ID(),get_TrxName());

        reversal.setClientOrg( this );    // needs to be set explicitly
        reversal.setDocumentInfo( m_docVO,m_docLine );
        reversal.setAccount( m_acctSchema,m_acct );
        reversal.setPostingType( getPostingType());

        //

        reversal.setAmtSource( getC_Currency_ID(),getAmtSourceDr().negate(),getAmtSourceCr().negate());
        reversal.convert();
        reversal.setDescription( description );

        return reversal;
    }    // reverse

    /**
     * Descripción de Método
     *
     *
     * @param description
     *
     * @return
     */

    public FactLine accrue( String description ) {
        FactLine accrual = new FactLine( getCtx(),getAD_Table_ID(),getRecord_ID(),getLine_ID(),get_TrxName());

        accrual.setClientOrg( this );    // needs to be set explicitly
        accrual.setDocumentInfo( m_docVO,m_docLine );
        accrual.setAccount( m_acctSchema,m_acct );
        accrual.setPostingType( getPostingType());

        //

        accrual.setAmtSource( getC_Currency_ID(),getAmtSourceCr(),getAmtSourceDr());
        accrual.convert();
        accrual.setDescription( description );

        return accrual;
    }    // reverse

    /**
     * Descripción de Método
     *
     *
     * @param acctSchema
     * @param acct
     */

    public void setAccount( MAcctSchema acctSchema,MAccount acct ) {
        m_acctSchema = acctSchema;
        setC_AcctSchema_ID( acctSchema.getC_AcctSchema_ID());

        //

        m_acct = acct;

        if( getAD_Client_ID() == 0 ) {
            setAD_Client_ID( m_acct.getAD_Client_ID());
        }

        setAccount_ID( m_acct.getAccount_ID());
    }    // setAccount

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     * @param AmtSourceDr
     * @param AmtSourceCr
     *
     * @return
     */

    public boolean setAmtSource( int C_Currency_ID,BigDecimal AmtSourceDr,BigDecimal AmtSourceCr ) {
        setC_Currency_ID( C_Currency_ID );

        if( AmtSourceDr != null ) {
            setAmtSourceDr( AmtSourceDr );
        }

        if( AmtSourceCr != null ) {
            setAmtSourceCr( AmtSourceCr );
        }

        // *********************************************************************************
        // * Disytel - Franco Bonafine 
        // * Validación incorrecta. El método equals devuelve false si se compara 0.0 contra 0.00
        // * (Diferencia de Escalas). Se debe utilizar compareTo();
        // one needs to be non zero
        //if( getAmtSourceDr().equals( Env.ZERO ) && getAmtSourceCr().equals( Env.ZERO )) {
        //    return false;
        //}
        // * Validación correcta:
        if( getAmtSourceDr().compareTo(BigDecimal.ZERO) == 0 && getAmtSourceCr().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        // * Fin modificación FB.
        // *********************************************************************************
        return true;
    }    // setAmtSource

    /**
     * Descripción de Método
     *
     *
     * @param AmtAcctDr
     * @param AmtAcctCr
     */

    public void setAmtAcct( BigDecimal AmtAcctDr,BigDecimal AmtAcctCr ) {
        setAmtAcctDr( AmtAcctDr );
        setAmtAcctCr( AmtAcctCr );
    }    // setAmtAcct

    /**
     * Descripción de Método
     *
     *
     * @param docVO
     * @param docLine
     */

    public void setDocumentInfo( DocVO docVO,DocLine docLine ) {
        m_docVO   = docVO;
        m_docLine = docLine;

        // reset

        setAD_Org_ID( 0 );
        setC_SalesRegion_ID( 0 );

        // Client

        if( getAD_Client_ID() == 0 ) {
            setAD_Client_ID( m_docVO.AD_Client_ID );
        }

        // Date Trx

        setDateTrx( m_docVO.DateDoc );

        if( (m_docLine != null) && (m_docLine.getDateDoc() != null) ) {
            setDateTrx( m_docLine.getDateDoc());
        }

        // Date Acct

        setDateAcct( m_docVO.DateAcct );

        if( (m_docLine != null) && (m_docLine.getDateAcct() != null) ) {
            setDateAcct( m_docLine.getDateAcct());
        }

        // Period, Tax

        if( (m_docLine != null) && (m_docLine.getC_Period_ID() != 0) ) {
            setC_Period_ID( m_docLine.getC_Period_ID());
        } else {
            setC_Period_ID( m_docVO.C_Period_ID );
        }

        if( m_docLine != null ) {
            setC_Tax_ID( m_docLine.getC_Tax_ID());
        }

        // Description

        StringBuffer description = new StringBuffer( m_docVO.DocumentNo );

        if( m_docLine != null ) {
            description.append( " #" ).append( m_docLine.getLine());

            if( m_docLine.getDescription() != null ) {
                description.append( " (" ).append( m_docLine.getDescription()).append( ")" );
            } else if( (m_docVO.Description != null) && (m_docVO.Description.length() > 0) ) {
                description.append( " (" ).append( m_docVO.Description ).append( ")" );
            }
        } else if( (m_docVO.Description != null) && (m_docVO.Description.length() > 0) ) {
            description.append( " (" ).append( m_docVO.Description ).append( ")" );
        }

        setDescription( description.toString());

        // Journal Info

        setGL_Budget_ID( m_docVO.GL_Budget_ID );
        setGL_Category_ID( m_docVO.GL_Category_ID );

        // Product

        if( m_docLine != null ) {
            setM_Product_ID( m_docLine.getM_Product_ID());
        }

        if( getM_Product_ID() == 0 ) {
            setM_Product_ID( m_docVO.M_Product_ID );
        }

        // UOM

        if( m_docLine != null ) {
            setC_UOM_ID( m_docLine.getC_UOM_ID());
        }

        // Qty

        if( get_Value( "Qty" ) == null )    // not previously set
        {
            setQty( m_docVO.Qty );          // neg = outgoing

            if( m_docLine != null ) {
                setQty( m_docLine.getQty());
            }
        }

        // Loc From (maybe set earlier)

        if( (getC_LocFrom_ID() == 0) && (m_docLine != null) ) {
            setC_LocFrom_ID( m_docLine.getC_LocFrom_ID());
        }

        if( getC_LocFrom_ID() == 0 ) {
            setC_LocFrom_ID( m_docVO.C_LocFrom_ID );
        }

        // Loc To (maybe set earlier)

        if( (getC_LocTo_ID() == 0) && (m_docLine != null) ) {
            setC_LocTo_ID( m_docLine.getC_LocTo_ID());
        }

        if( getC_LocTo_ID() == 0 ) {
            setC_LocTo_ID( m_docVO.C_LocTo_ID );
        }

        // BPartner

        if( m_docLine != null ) {
            setC_BPartner_ID( m_docLine.getC_BPartner_ID());
        }

        if( getC_BPartner_ID() == 0 ) {
            setC_BPartner_ID( m_docVO.C_BPartner_ID );
        }

        // Trx Org

        if( m_docLine != null ) {
            setAD_OrgTrx_ID( m_docLine.getAD_OrgTrx_ID());
        }

        if( getAD_OrgTrx_ID() == 0 ) {
            setAD_OrgTrx_ID( m_docVO.AD_OrgTrx_ID );
        }

        // Project

        if( m_docLine != null ) {
            setC_Project_ID( m_docLine.getC_Project_ID());
        }

        if( getC_Project_ID() == 0 ) {
            setC_Project_ID( m_docVO.C_Project_ID );
        }

        // Campaign

        if( m_docLine != null ) {
            setC_Campaign_ID( m_docLine.getC_Campaign_ID());
        }

        if( getC_Campaign_ID() == 0 ) {
            setC_Campaign_ID( m_docVO.C_Campaign_ID );
        }

        // Activity

        if( m_docLine != null ) {
            setC_Activity_ID( m_docLine.getC_Activity_ID());
        }

        if( getC_Activity_ID() == 0 ) {
            setC_Activity_ID( m_docVO.C_Activity_ID );
        }

        // User 1

        if( m_docLine != null ) {
            setUser1_ID( m_docLine.getUser1_ID());
        }

        if( getUser1_ID() == 0 ) {
            setUser1_ID( m_docVO.User1_ID );
        }

        // User 2

        if( m_docLine != null ) {
            setUser2_ID( m_docLine.getUser2_ID());
        }

        if( getUser2_ID() == 0 ) {
            setUser2_ID( m_docVO.User2_ID );
        }
    }    // setDocumentInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected DocLine getDocLine() {
        return m_docLine;
    }    // getDocLine

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String original = getDescription();

        if( (original == null) || (original.trim().length() == 0) ) {
            super.setDescription( description );
        } else {
            super.setDescription( original + " - " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @param M_Locator_ID
     */

    public void setM_Locator_ID( int M_Locator_ID ) {
        super.setM_Locator_ID( M_Locator_ID );
        setAD_Org_ID( 0 );    // reset
    }                         // setM_Locator_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_Location_ID
     * @param isFrom
     */

    public void setLocation( int C_Location_ID,boolean isFrom ) {
        if( isFrom ) {
            setC_LocFrom_ID( C_Location_ID );
        } else {
            setC_LocTo_ID( C_Location_ID );
        }
    }    // setLocator

    /**
     * Descripción de Método
     *
     *
     * @param M_Locator_ID
     * @param isFrom
     */

    public void setLocationFromLocator( int M_Locator_ID,boolean isFrom ) {
        if( M_Locator_ID == 0 ) {
            return;
        }

        int    C_Location_ID = 0;
        String sql           = "SELECT w.C_Location_ID FROM M_Warehouse w, M_Locator l " + "WHERE w.M_Warehouse_ID=l.M_Warehouse_ID AND l.M_Locator_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,M_Locator_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Location_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );

            return;
        }

        if( C_Location_ID != 0 ) {
            setLocation( C_Location_ID,isFrom );
        }
    }    // setLocationFromLocator

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     * @param isFrom
     */

    public void setLocationFromBPartner( int C_BPartner_Location_ID,boolean isFrom ) {
        if( C_BPartner_Location_ID == 0 ) {
            return;
        }

        int    C_Location_ID = 0;
        String sql           = "SELECT C_Location_ID FROM C_BPartner_Location WHERE C_BPartner_Location_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,C_BPartner_Location_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Location_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );

            return;
        }

        if( C_Location_ID != 0 ) {
            setLocation( C_Location_ID,isFrom );
        }
    }    // setLocationFromBPartner

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     * @param isFrom
     */

    public void setLocationFromOrg( int AD_Org_ID,boolean isFrom ) {
        if( AD_Org_ID == 0 ) {
            return;
        }

        int    C_Location_ID = 0;
        String sql           = "SELECT C_Location_ID FROM AD_OrgInfo WHERE AD_Org_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,AD_Org_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_Location_ID = rs.getInt( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );

            return;
        }

        if( C_Location_ID != 0 ) {
            setLocation( C_Location_ID,isFrom );
        }
    }    // setLocationFromOrg

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getSourceBalance() {
        if( getAmtSourceDr() == null ) {
            setAmtSourceDr( Env.ZERO );
        }

        if( getAmtSourceCr() == null ) {
            setAmtSourceCr( Env.ZERO );
        }

        //

        return getAmtSourceDr().subtract( getAmtSourceCr());
    }    // getSourceBalance

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDrSourceBalance() {
        return getSourceBalance().compareTo( Env.ZERO ) != -1;
    }    // isDrSourceBalance

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAcctBalance() {
        if( getAmtAcctDr() == null ) {
            setAmtAcctDr( Env.ZERO );
        }

        if( getAmtAcctCr() == null ) {
            setAmtAcctCr( Env.ZERO );
        }

        return getAmtAcctDr().subtract( getAmtAcctCr());
    }    // getAcctBalance

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isBalanceSheet() {
        return m_acct.isBalanceSheet();
    }    // isBalanceSheet

    /**
     * Descripción de Método
     *
     *
     * @param deltaAmount
     */

    public void currencyCorrect( BigDecimal deltaAmount ) {
        boolean negative = deltaAmount.compareTo( Env.ZERO ) < 0;
        boolean adjustDr = getAmtAcctDr().compareTo( getAmtAcctCr()) > 0;

        log.fine( deltaAmount.toString() + "; Old-AcctDr=" + getAmtAcctDr() + ",AcctCr=" + getAmtAcctCr() + "; Negative=" + negative + "; AdjustDr=" + adjustDr );

        if( adjustDr ) {
            if( negative ) {
                setAmtAcctDr( getAmtAcctDr().subtract( deltaAmount ));
            } else {
                setAmtAcctDr( getAmtAcctDr().add( deltaAmount ));
            }
        } else if( negative ) {
            setAmtAcctCr( getAmtAcctCr().add( deltaAmount ));
        } else {
            setAmtAcctCr( getAmtAcctCr().subtract( deltaAmount ));
        }

        log.fine( "New-AcctDr=" + getAmtAcctDr() + ",AcctCr=" + getAmtAcctCr());
    }    // currencyCorrect

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean convert() {

        // Document has no currency

        if( getC_Currency_ID() == Doc.NO_CURRENCY ) {
            setC_Currency_ID( m_acctSchema.getC_Currency_ID());
        }

        if( m_acctSchema.getC_Currency_ID() == getC_Currency_ID()) {
            setAmtAcctDr( getAmtSourceDr());
            setAmtAcctCr( getAmtSourceCr());

            return true;
        }

        // Get Conversion Type from Line or Header

        int C_ConversionType_ID = 0;
        int AD_Org_ID           = 0;

        if( m_docLine != null )    // get from line
        {
            C_ConversionType_ID = m_docLine.getC_ConversionType_ID();
            AD_Org_ID           = m_docLine.getAD_Org_ID();
        }

        if( C_ConversionType_ID == 0 )    // get from header
        {
            if( m_docVO == null ) {
                log.severe( "No Document VO" );

                return false;
            }

            C_ConversionType_ID = m_docVO.C_ConversionType_ID;

            if( AD_Org_ID == 0 ) {
                AD_Org_ID = m_docVO.AD_Org_ID;
            }
        }

        setAmtAcctDr( MConversionRate.convert( getCtx(),getAmtSourceDr(),getC_Currency_ID(),m_acctSchema.getC_Currency_ID(),getDateAcct(),C_ConversionType_ID,m_docVO.AD_Client_ID,AD_Org_ID ));

        if( getAmtAcctDr() == null ) {
            return false;
        }

        setAmtAcctCr( MConversionRate.convert( getCtx(),getAmtSourceCr(),getC_Currency_ID(),m_acctSchema.getC_Currency_ID(),getDateAcct(),C_ConversionType_ID,m_docVO.AD_Client_ID,AD_Org_ID ));

        return true;
    }    // convert

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAccount getAccount() {
        return m_acct;
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "FactLine=[" );

        sb.append( getAD_Table_ID()).append( ":" ).append( getRecord_ID()).append( "," ).append( m_acct ).append( ",Cur=" ).append( getC_Currency_ID()).append( ", DR=" ).append( getAmtSourceDr()).append( "|" ).append( getAmtAcctDr()).append( ", CR=" ).append( getAmtSourceCr()).append( "|" ).append( getAmtAcctCr()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     */

    public void setAD_Org_ID( int AD_Org_ID ) {
        super.setAD_Org_ID( AD_Org_ID );
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Org_ID() {
        if( super.getAD_Org_ID() != 0 ) {    // set earlier
            return super.getAD_Org_ID();
        }

        // Prio 1 - get from locator - if exist

        if( getM_Locator_ID() != 0 ) {
            String sql = "SELECT AD_Org_ID FROM M_Locator WHERE M_Locator_ID=? AND AD_Client_ID=?";

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

                pstmt.setInt( 1,getM_Locator_ID());
                pstmt.setInt( 2,getAD_Client_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    setAD_Org_ID( rs.getInt( 1 ));
                    log.finer( "AD_Org_ID=" + super.getAD_Org_ID() + " (1 from M_Locator_ID=" + getM_Locator_ID() + ")" );
                } else {
                    log.log( Level.SEVERE,"AD_Org_ID - Did not find M_Locator_ID=" + getM_Locator_ID());
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,sql,e );
            }
        }    // M_Locator_ID != 0

        // Prio 2 - get from doc line - if exists (document context overwrites)

        if( (m_docLine != null) && (super.getAD_Org_ID() == 0) ) {
            setAD_Org_ID( m_docLine.getAD_Org_ID());
            log.finer( "AD_Org_ID=" + super.getAD_Org_ID() + " (2 from DocumentLine)" );
        }

        // Prio 3 - get from doc - if not GL

        if( (m_docVO != null) && (super.getAD_Org_ID() == 0) ) {
            if( Doc.DOCTYPE_GLJournal.equals( m_docVO.DocumentType )) {
                setAD_Org_ID( m_acct.getAD_Org_ID());    // inter-company GL
                log.finer( "AD_Org_ID=" + super.getAD_Org_ID() + " (3 from Acct)" );
            } else {
                setAD_Org_ID( m_docVO.AD_Org_ID );
                log.finer( "AD_Org_ID=" + super.getAD_Org_ID() + " (3 from Document)" );
            }
        }

        // Prio 4 - get from account - if not GL

        if( (m_docVO != null) && (super.getAD_Org_ID() == 0) ) {
            if( Doc.DOCTYPE_GLJournal.equals( m_docVO.DocumentType )) {
                setAD_Org_ID( m_docVO.AD_Org_ID );
                log.finer( "AD_Org_ID=" + super.getAD_Org_ID() + " (4 from Document)" );
            } else {
                setAD_Org_ID( m_acct.getAD_Org_ID());
                log.finer( "AD_Org_ID=" + super.getAD_Org_ID() + " (4 from Acct)" );
            }
        }

        return super.getAD_Org_ID();
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_SalesRegion_ID() {
        if( super.getC_SalesRegion_ID() != 0 ) {
            return super.getC_SalesRegion_ID();
        }

        //

        if( m_docLine != null ) {
            setC_SalesRegion_ID( m_docLine.getC_SalesRegion_ID());
        }

        if( m_docVO != null ) {
            if( super.getC_SalesRegion_ID() == 0 ) {
                setC_SalesRegion_ID( m_docVO.C_SalesRegion_ID );
            }

            if( (super.getC_SalesRegion_ID() == 0) && (m_docVO.BP_C_SalesRegion_ID > 0) ) {
                setC_SalesRegion_ID( m_docVO.BP_C_SalesRegion_ID );
            }

            // derive SalesRegion if AcctSegment

            if( (super.getC_SalesRegion_ID() == 0) && (m_docVO.C_BPartner_Location_ID != 0) && (m_docVO.BP_C_SalesRegion_ID == -1    // never tried
                    ) && m_acctSchema.isAcctSchemaElement( MAcctSchemaElement.ELEMENTTYPE_SalesRegion )) {
                String sql = "SELECT C_SalesRegion_ID FROM C_BPartner_Location WHERE C_BPartner_Location_ID=?";

                setC_SalesRegion_ID( DB.getSQLValue( null,sql,m_docVO.C_BPartner_Location_ID ));

                if( super.getC_SalesRegion_ID() != 0 ) {    // save in VO
                    m_docVO.BP_C_SalesRegion_ID = super.getC_SalesRegion_ID();
                } else {
                    m_docVO.BP_C_SalesRegion_ID = -2;       // don't try again
                }

                log.fine( "C_SalesRegion_ID=" + super.getC_SalesRegion_ID() + " (from BPL)" );
            }

            if( (m_acct != null) && (super.getC_SalesRegion_ID() == 0) ) {
                setC_SalesRegion_ID( m_acct.getC_SalesRegion_ID());
            }
        }

        //
        // log.fine("getC_SalesRegion_ID=" + super.getC_SalesRegion_ID()
        // + ", C_BPartner_Location_ID=" + m_docVO.C_BPartner_Location_ID
        // + ", BP_C_SalesRegion_ID=" + m_docVO.BP_C_SalesRegion_ID
        // + ", SR=" + m_acctSchema.isAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_SalesRegion));

        return super.getC_SalesRegion_ID();
    }    // getC_SalesRegion_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( newRecord ) {
            log.fine( toString());

            //

            getAD_Org_ID();
            getC_SalesRegion_ID();

            // Set Account Info

            if( getM_Product_ID() == 0 ) {
                setM_Product_ID( m_acct.getM_Product_ID());
            }

            if( getC_LocFrom_ID() == 0 ) {
                setC_LocFrom_ID( m_acct.getC_LocFrom_ID());
            }

            if( getC_LocTo_ID() == 0 ) {
                setC_LocTo_ID( m_acct.getC_LocTo_ID());
            }

            if( getC_BPartner_ID() == 0 ) {
                setC_BPartner_ID( m_acct.getC_BPartner_ID());
            }

            if( getAD_OrgTrx_ID() == 0 ) {
                setAD_OrgTrx_ID( m_acct.getAD_OrgTrx_ID());
            }

            if( getC_Project_ID() == 0 ) {
                setC_Project_ID( m_acct.getC_Project_ID());
            }

            if( getC_Campaign_ID() == 0 ) {
                setC_Campaign_ID( m_acct.getC_Campaign_ID());
            }

            if( getC_Activity_ID() == 0 ) {
                setC_Activity_ID( m_acct.getC_Activity_ID());
            }

            if( getUser1_ID() == 0 ) {
                setUser1_ID( m_acct.getUser1_ID());
            }

            if( getUser2_ID() == 0 ) {
                setUser2_ID( m_acct.getUser2_ID());
            }

            // Revenue Recognition for AR Invoices

            if( (m_docVO != null) && m_docVO.DocumentType.equals( Doc.DOCTYPE_ARInvoice ) && (m_docLine != null) && (m_docLine.getC_RevenueRecognition_ID() != 0) ) {
                setAccount_ID( createRevenueRecognition( m_docLine.getC_RevenueRecognition_ID(),m_docLine.getTrxLine_ID(),getAD_Client_ID(),getAD_Org_ID(),0,getAccount_ID(),getM_Product_ID(),getC_BPartner_ID(),getAD_OrgTrx_ID(),getC_LocFrom_ID(),getC_LocTo_ID(),getC_SalesRegion_ID(),getC_Project_ID(),getC_Campaign_ID(),getC_Activity_ID(),getUser1_ID(),getUser2_ID()));
            }
        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param C_RevenueRecognition_ID
     * @param C_InvoiceLine_ID
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param AD_User_ID
     * @param Account_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     * @param AD_OrgTrx_ID
     * @param C_LocFrom_ID
     * @param C_LocTo_ID
     * @param C_SRegion_ID
     * @param C_Project_ID
     * @param C_Campaign_ID
     * @param C_Activity_ID
     * @param User1_ID
     * @param User2_ID
     *
     * @return
     */

    private int createRevenueRecognition( int C_RevenueRecognition_ID,int C_InvoiceLine_ID,int AD_Client_ID,int AD_Org_ID,int AD_User_ID,int Account_ID,int M_Product_ID,int C_BPartner_ID,int AD_OrgTrx_ID,int C_LocFrom_ID,int C_LocTo_ID,int C_SRegion_ID,int C_Project_ID,int C_Campaign_ID,int C_Activity_ID,int User1_ID,int User2_ID ) {
        log.fine( "From Accout_ID=" + Account_ID );

        // get VC for P_Revenue (from Product)

        MAccount revenue = MAccount.get( getCtx(),AD_Client_ID,AD_Org_ID,getC_AcctSchema_ID(),Account_ID,M_Product_ID,C_BPartner_ID,AD_OrgTrx_ID,C_LocFrom_ID,C_LocTo_ID,C_SRegion_ID,C_Project_ID,C_Campaign_ID,C_Activity_ID,User1_ID,User2_ID );

        if( (revenue != null) && (revenue.getID() == 0) ) {
            revenue.save();
        }

        if( (revenue == null) || (revenue.getID() == 0) ) {
            log.severe( "Revenue_Acct not found" );

            return Account_ID;
        }

        int P_Revenue_Acct = revenue.getID();

        // get Unearned Revenue Acct from BPartner Group

        int    UnearnedRevenue_Acct = 0;
        int    new_Account_ID       = 0;
        String sql                  = "SELECT ga.UnearnedRevenue_Acct, vc.Account_ID " + "FROM C_BP_Group_Acct ga, C_BPartner p, C_ValidCombination vc " + "WHERE ga.C_BP_Group_ID=p.C_BP_Group_ID" + " AND ga.UnearnedRevenue_Acct=vc.C_ValidCombination_ID" + " AND ga.C_AcctSchema_ID=? AND p.C_BPartner_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,getC_AcctSchema_ID());
            pstmt.setInt( 2,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                UnearnedRevenue_Acct = rs.getInt( 1 );
                new_Account_ID       = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        if( new_Account_ID == 0 ) {
            log.severe( "UnearnedRevenue_Acct not found" );

            return Account_ID;
        }

        MRevenueRecognitionPlan plan = new MRevenueRecognitionPlan( getCtx(),0,null );

        plan.setC_RevenueRecognition_ID( C_RevenueRecognition_ID );
        plan.setC_AcctSchema_ID( getC_AcctSchema_ID());
        plan.setC_InvoiceLine_ID( C_InvoiceLine_ID );
        plan.setUnEarnedRevenue_Acct( UnearnedRevenue_Acct );
        plan.setP_Revenue_Acct( P_Revenue_Acct );
        plan.setC_Currency_ID( getC_Currency_ID());
        plan.setTotalAmt( getAcctBalance());

        if( !plan.save( get_TrxName())) {
            log.severe( "Plan NOT created" );

            return Account_ID;
        }

        log.fine( "From Acctount_ID=" + Account_ID + " to " + new_Account_ID + " - Plan from UnearnedRevenue_Acct=" + UnearnedRevenue_Acct + " to Revenue_Acct=" + P_Revenue_Acct );

        return new_Account_ID;
    }    // createRevenueRecognition

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     * @param Line_ID
     * @param multiplier
     *
     * @return
     */

    public boolean updateReverseLine( int AD_Table_ID,int Record_ID,int Line_ID,BigDecimal multiplier ) {
        boolean success = false;
        String  sql     = "SELECT AmtAcctDr,AmtAcctCr, C_Project_ID, AD_Org_ID " + "FROM Fact_Acct " + "WHERE C_AcctSchema_ID=? AND AD_Table_ID=? AND Record_ID=?" + " AND Line_ID=? AND Account_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,getC_AcctSchema_ID());
            pstmt.setInt( 2,AD_Table_ID );
            pstmt.setInt( 3,Record_ID );
            pstmt.setInt( 4,Line_ID );
            pstmt.setInt( 5,m_acct.getAccount_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Accounted Amounts - reverse

                BigDecimal dr = rs.getBigDecimal( 1 );
                BigDecimal cr = rs.getBigDecimal( 2 );

                setAmtAcctDr( cr.multiply( multiplier ));
                setAmtAcctCr( dr.multiply( multiplier ));

                // Source Amounts

                setAmtSourceDr( getAmtAcctDr());
                setAmtSourceCr( getAmtAcctCr());

                //

                success = true;
                log.fine( new StringBuffer( "(Table=" ).append( AD_Table_ID ).append( ",Record_ID=" ).append( Record_ID ).append( ",Line=" ).append( Record_ID ).append( ", Account=" ).append( m_acct ).append( ",dr=" ).append( dr ).append( ",cr=" ).append( cr ).append( ") - DR=" ).append( getAmtSourceDr()).append( "|" ).append( getAmtAcctDr()).append( ", CR=" ).append( getAmtSourceCr()).append( "|" ).append( getAmtAcctCr()).toString());

                // Others

                setC_Project_ID( rs.getInt( 3 ));

                // Org for cross charge

                setAD_Org_ID( rs.getInt( 4 ));
            } else {
                log.warning( new StringBuffer( "Not Found (try later) " ).append( ",C_AcctSchema_ID=" ).append( getC_AcctSchema_ID()).append( ", AD_Table_ID=" ).append( AD_Table_ID ).append( ",Record_ID=" ).append( Record_ID ).append( ",Line_ID=" ).append( Line_ID ).append( ", Account_ID=" ).append( m_acct.getAccount_ID()).toString());
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        return success;
    }    // updateReverseLine
}    // FactLine



/*
 *  @(#)FactLine.java   24.03.06
 * 
 *  Fin del fichero FactLine.java
 *  
 *  Versión 2.2
 *
 */
