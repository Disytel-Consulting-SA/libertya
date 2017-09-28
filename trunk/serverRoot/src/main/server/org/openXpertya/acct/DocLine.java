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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MCharge;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DocumentType
     * @param TrxHeader_ID
     * @param TrxLine_ID
     * @param trxName
     */

    public DocLine( String DocumentType,int TrxHeader_ID,int TrxLine_ID,String trxName ) {
        if( DocumentType == null ) {
            throw new IllegalArgumentException( "DocLine - DocumentType is null" );
        }

        p_DocumentType = DocumentType;
        m_TrxHeader_ID = TrxHeader_ID;
        p_TrxLine_ID   = TrxLine_ID;
        m_trxName      = trxName;
    }    // DocLine

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    protected String p_DocumentType = null;

    /** Descripción de Campos */

    private int m_TrxHeader_ID = 0;

    /** Descripción de Campos */

    protected int p_TrxLine_ID = 0;

    /** Descripción de Campos */

    private int m_Line = 0;

    /** Descripción de Campos */

    private String m_trxName = null;

    /** Descripción de Campos */

    private int m_C_UOM_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_qty = null;

    /** Descripción de Campos */

    private int m_C_Currency_ID = 0;

    /** Descripción de Campos */

    private int m_C_ConversionType_ID = 0;

    // -- GL Amounts

    /** Descripción de Campos */

    private BigDecimal m_AmtSourceDr = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_AmtSourceCr = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_AmtAcctDr = null;

    /** Descripción de Campos */

    private BigDecimal m_AmtAcctCr = null;

    /** Descripción de Campos */

    private int m_C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    protected ProductInfo p_productInfo = null;

    /** Descripción de Campos */

    private MAccount m_account = null;

    // Dimensions

    /** Descripción de Campos */

    private int m_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int m_M_Product_ID = 0;

    /** Descripción de Campos */

    private int m_AD_OrgTrx_ID = 0;

    /** Descripción de Campos */

    private int m_C_SalesRegion_ID = 0;

    /** Descripción de Campos */

    private int m_C_Project_ID = 0;

    /** Descripción de Campos */

    private int m_C_Campaign_ID = 0;

    /** Descripción de Campos */

    private int m_C_Activity_ID = 0;

    /** Descripción de Campos */

    private int m_C_LocFrom_ID = 0;

    /** Descripción de Campos */

    private int m_C_LocTo_ID = 0;

    /** Descripción de Campos */

    private int m_User1_ID = 0;

    /** Descripción de Campos */

    private int m_User2_ID = 0;

    //

    /** Descripción de Campos */

    private int m_C_Charge_ID = 0;

    /** Descripción de Campos */

    private BigDecimal m_ChargeAmt = Env.ZERO;

    /** Descripción de Campos */

    private String m_description = null;

    /** Descripción de Campos */

    private int m_C_Tax_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateAcct = null;

    /** Descripción de Campos */

    private int m_C_Period_ID = 0;

    /** Descripción de Campos */

    private Timestamp m_DateDoc = null;

    /** Descripción de Campos */

    private int m_M_AttributSetInstance_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @param rs
     * @param vo
     */

    public void loadAttributes( ResultSet rs,DocVO vo ) {

        // Log.trace(this,Log.l4_Data, "DocLine.loadAttributes");

        try {
            ResultSetMetaData rsmd = rs.getMetaData();

            for( int i = 1;i <= rsmd.getColumnCount();i++ ) {
                String col = rsmd.getColumnName( i );

                if( col.equalsIgnoreCase( "AD_Org_ID" )) {
                    m_AD_Org_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_BPartner_ID" )) {
                    m_C_BPartner_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "M_Product_ID" )) {
                    m_M_Product_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "AD_OrgTrx_ID" )) {
                    m_AD_OrgTrx_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_SalesRegion_ID" )) {
                    m_C_SalesRegion_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Project_ID" )) {
                    m_C_Project_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Campaign_ID" )) {
                    m_C_Campaign_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Activity_ID" )) {
                    m_C_Activity_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_LocFrom_ID" )) {
                    m_C_LocFrom_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_LocTo_ID" )) {
                    m_C_LocTo_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "User1_ID" )) {
                    m_User1_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "User2_ID" )) {
                    m_User2_ID = rs.getInt( i );

                    // Line, Description, Currency

                } else if( col.equalsIgnoreCase( "Line" )) {
                    m_Line = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "Description" )) {
                    m_description = rs.getString( i );
                } else if( col.equalsIgnoreCase( "C_Currency_ID" )) {
                    m_C_Currency_ID = rs.getInt( i );

                    // Qty

                } else if( col.equalsIgnoreCase( "C_UOM_ID" )) {
                    m_C_UOM_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "Qty" )) {
                    m_qty = rs.getBigDecimal( i );

                    //

                } else if( col.equalsIgnoreCase( "C_Tax_ID" )) {
                    m_C_Tax_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "C_Charge_ID" )) {
                    m_C_Charge_ID = rs.getInt( i );
                } else if( col.equalsIgnoreCase( "ChargeAmt" )) {
                    m_ChargeAmt = rs.getBigDecimal( i );

                    //

                } else if( col.equalsIgnoreCase( "DateAcct" )) {
                    m_DateAcct = rs.getTimestamp( i );
                } else if( col.equalsIgnoreCase( "DateDoc" )) {
                    m_DateDoc = rs.getTimestamp( i );

                    //

                } else if( col.equalsIgnoreCase( "M_AttributSetInstance_ID" )) {
                    m_M_AttributSetInstance_ID = rs.getInt( i );
                }
            }    // for all columns
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadAttributes",e );
        }

        // Product Info

        p_productInfo = new ProductInfo( m_M_Product_ID,getTrxName());

        // Document Consistency

        if( m_AD_Org_ID == 0 ) {
            m_AD_Org_ID = vo.AD_Org_ID;
        }

        if( m_C_Currency_ID == 0 ) {
            m_C_Currency_ID = vo.C_Currency_ID;
        }
    }    // loadAttributes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String getTrxName() {
        return m_trxName;
    }    // getTrxName

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     */

    public void setC_Currency_ID( int C_Currency_ID ) {
        m_C_Currency_ID = C_Currency_ID;
    }    // setC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID() {
        return m_C_Currency_ID;
    }    // getC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_ConversionType_ID
     */

    public void setC_ConversionType_ID( int C_ConversionType_ID ) {
        m_C_ConversionType_ID = C_ConversionType_ID;
    }    // setC_ConversionType_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_ConversionType_ID() {
        return m_C_ConversionType_ID;
    }    // getC_ConversionType_ID

    /**
     * Descripción de Método
     *
     *
     * @param sourceAmt
     */

    public void setAmount( BigDecimal sourceAmt ) {
        m_AmtSourceDr = (sourceAmt == null)
                        ?Env.ZERO
                        :sourceAmt;
        m_AmtSourceCr = Env.ZERO;
    }    // setAmounts

    /**
     * Descripción de Método
     *
     *
     * @param amtSourceDr
     * @param amtSourceCr
     */

    public void setAmount( BigDecimal amtSourceDr,BigDecimal amtSourceCr ) {
        m_AmtSourceDr = (amtSourceDr == null)
                        ?Env.ZERO
                        :amtSourceDr;
        m_AmtSourceCr = (amtSourceCr == null)
                        ?Env.ZERO
                        :amtSourceCr;
    }    // setAmounts

    /**
     * Descripción de Método
     *
     *
     * @param C_AcctSchema_ID
     * @param amtAcctDr
     * @param amtAcctCr
     */

    public void setConvertedAmt( int C_AcctSchema_ID,BigDecimal amtAcctDr,BigDecimal amtAcctCr ) {
        m_C_AcctSchema_ID = C_AcctSchema_ID;
        m_AmtAcctDr       = amtAcctDr;
        m_AmtAcctCr       = amtAcctCr;
    }    // setConvertedAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmount() {
        return m_AmtSourceDr.subtract( m_AmtSourceCr );
    }    // getAmount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmtSourceDr() {
        return m_AmtSourceDr;
    }    // getAmtSourceDr

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmtSourceCr() {
        return m_AmtSourceCr;
    }    // getAmtSourceCr

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmtAcctDr() {
        return m_AmtAcctDr;
    }    // getAmtAcctDr

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmtAcctCr() {
        return m_AmtAcctCr;
    }    // getAmtAccrCr

    /**
     * Descripción de Método
     *
     *
     * @param chargeAmt
     */

    public void setChargeAmt( BigDecimal chargeAmt ) {
        m_ChargeAmt = (chargeAmt == null)
                      ?Env.ZERO
                      :chargeAmt;
    }    // setChargeAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getChargeAmt() {
        return m_ChargeAmt;
    }    // getChargeAmt

    /**
     * Descripción de Método
     *
     *
     * @param dateAcct
     */

    public void setDateAcct( Timestamp dateAcct ) {
        m_DateAcct = dateAcct;
    }    // setDateAcct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateAcct() {
        return m_DateAcct;
    }    // getDateAcct

    /**
     * Descripción de Método
     *
     *
     * @param C_Period_ID
     */

    public void setC_Period_ID( int C_Period_ID ) {
        m_C_Period_ID = C_Period_ID;
    }    // setC_Period_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Period_ID() {
        return m_C_Period_ID;
    }    // getC_Period_ID

    /**
     * Descripción de Método
     *
     *
     * @param dateDoc
     */

    public void setDateDoc( Timestamp dateDoc ) {
        m_DateDoc = dateDoc;
    }    // setDateDoc

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateDoc() {
        return m_DateDoc;
    }    // getDateDoc

    /**
     * Descripción de Método
     *
     *
     * @param acct
     */

    public void setAccount( MAccount acct ) {
        m_account = acct;
    }    // setAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAccount getAccount() {
        return m_account;
    }    // getAccount

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param amount
     *
     * @return
     */

    public MAccount getChargeAccount( MAcctSchema as,BigDecimal amount ) {
        if( m_C_Charge_ID == 0 ) {
            return null;
        }

        return MCharge.getAccount( m_C_Charge_ID,as,amount );
    }    // getChargeAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_AcctSchema_ID() {
        return m_C_AcctSchema_ID;
    }    // getC_AcctSchema_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Product_ID() {
        return m_M_Product_ID;
    }    // getM_Product_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_LocFrom_ID() {
        return m_C_LocFrom_ID;
    }    // getC_LocFrom_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_LocTo_ID() {
        return m_C_LocTo_ID;
    }    // getC_LocTo_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isProduct() {
        return p_productInfo.isProduct();
    }    // isProduct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isBOM() {
        return p_productInfo.isBOM();
    }    // isProduct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isService() {
        return !p_productInfo.isProduct();
    }    // isService

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProductCategory() {
        return p_productInfo.getProductCategory();
    }    // getProductCategpry

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isRevenueRecognition() {
        return p_productInfo.isRevenueRecognition();
    }    // isRevenueRecognition

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_RevenueRecognition_ID() {
        return p_productInfo.getC_RevenueRecognition_ID();
    }    // getC_RevenueRecognition_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_UOM_ID() {
        return m_C_UOM_ID;
    }    // getC_UOM

    /**
     * Descripción de Método
     *
     *
     * @param qty
     * @param isSOTrx
     */

    public void setQty( BigDecimal qty,boolean isSOTrx ) {
        if( qty == null ) {
            m_qty = Env.ZERO;
        } else if( isSOTrx ) {
            m_qty = qty.negate();
        } else {
            m_qty = qty;
        }
    }    // setQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQty() {
        return m_qty;
    }    // getQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Tax_ID() {
        return m_C_Tax_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLine() {
        return m_Line;
    }    // getLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Org_ID() {
        return m_AD_Org_ID;
    }    // getAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     */

    protected void setAD_Org_ID( int AD_Org_ID ) {
        m_AD_Org_ID = AD_Org_ID;
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        return m_C_BPartner_ID;
    }    // getC_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_OrgTrx_ID() {
        return m_AD_OrgTrx_ID;
    }    // getAD_OrgTrx_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_SalesRegion_ID() {
        return m_C_SalesRegion_ID;
    }    // getC_SalesRegion_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Project_ID() {
        return m_C_Project_ID;
    }    // getC_Project_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Campaign_ID() {
        return m_C_Campaign_ID;
    }    // getC_Campaign_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Activity_ID() {
        return m_C_Activity_ID;
    }    // getC_Activity_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getUser1_ID() {
        return m_User1_ID;
    }    // getUser1_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getUser2_ID() {
        return m_User2_ID;
    }    // getUser2_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getTrxLine_ID() {
        return p_TrxLine_ID;
    }    // getTrxLine_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected int getC_Charge_ID() {
        return m_C_Charge_ID;
    }    // getC_Charge_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_Charge_ID
     */

    protected void setC_Charge_ID( int C_Charge_ID ) {
        m_C_Charge_ID = C_Charge_ID;
    }    // setC_Charge_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "DocLine=[" );

        sb.append( p_TrxLine_ID ).append( "," ).append( m_description ).append( ",Qty=" ).append( m_qty ).append( ",Amt=" ).append( getAmount()).append( "]" );

        return sb.toString();
    }    // toString
    
    
    /**
	 * Obtiene la cuenta contable del artículo relacionado a la línea
	 * 
	 * @param AcctType
	 *            tipo de cuenta
	 * @param as
	 *            esquema contable
	 * @return cuenta contable
	 */
    public MAccount getProductInfoAccount( int AcctType,MAcctSchema as ) {
        return p_productInfo.getAccount( AcctType,as );
    }    // getAccount
    
}    // DocumentLine



/*
 *  @(#)DocLine.java   24.03.06
 * 
 *  Fin del fichero DocLine.java
 *  
 *  Versión 2.2
 *
 */
