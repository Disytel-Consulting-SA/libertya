/*
 * @(#)MConversionRate.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 *      Currency Conversion Rate Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MConversionRate.java,v 1.13 2005/03/11 20:28:32 jjanke Exp $
 */
public class MConversionRate extends X_C_Conversion_Rate {

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MConversionRate.class);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_Conversion_Rate_ID id
     * @param trxName
     */
    public MConversionRate(Properties ctx, int C_Conversion_Rate_ID, String trxName) {

        super(ctx, C_Conversion_Rate_ID, trxName);

        if (C_Conversion_Rate_ID == 0) {

            // setC_Conversion_Rate_ID (0);
            // setC_Currency_ID (0);
            // setC_Currency_ID_To (null);
            super.setDivideRate(Env.ZERO);
            super.setMultiplyRate(Env.ZERO);
            setValidFrom(new Timestamp(System.currentTimeMillis()));
        }

    }		// MConversionRate

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MConversionRate(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MConversionRate

    /**
     *      New Constructor
     *      @param po parent
     *      @param C_ConversionType_ID conversion type
     *      @param C_Currency_ID currency
     *      @param C_Currency_ID_To currency to
     *      @param MultiplyRate multiply rate
     *      @param ValidFrom valid from
     */
    public MConversionRate(PO po, int C_ConversionType_ID, int C_Currency_ID, int C_Currency_ID_To, BigDecimal MultiplyRate, Timestamp ValidFrom) {

        this(po.getCtx(), 0, po.get_TrxName());
        setClientOrg(po);
        setC_ConversionType_ID(C_ConversionType_ID);
        setC_Currency_ID(C_Currency_ID);
        setC_Currency_ID_To(C_Currency_ID_To);

        //
        setMultiplyRate(MultiplyRate);
        setValidFrom(ValidFrom);

    }		// MConversionRate

    /**
     *      Before Save.
     *      - Same Currency
     *      - Date Range Check
     *      - Set To date to 2056
     *      @param newRecord new
     *      @return true if OK to save
     */
    protected boolean beforeSave(boolean newRecord) {

        // From - To is the same
        if (getC_Currency_ID() == getC_Currency_ID_To()) {

            log.saveError("Error", Msg.parseTranslation(getCtx(), "@C_Currency_ID@ = @C_Currency_ID@"));

            return false;
        }

        // Nothing to convert
        if (getMultiplyRate().compareTo(Env.ZERO) <= 0) {

            log.saveError("Error", Msg.parseTranslation(getCtx(), "@MultiplyRate@ <= 0"));

            return false;
        }

        // Date Range Check
        Timestamp	from	= getValidFrom();

        if (getValidTo() == null) {
            setValidTo(TimeUtil.getDay(2056, 1, 29));		// no exchange rates after my 100th birthday
        }

        Timestamp	to	= getValidTo();

        if (to.before(from)) {

            SimpleDateFormat	df	= DisplayType.getDateFormat(DisplayType.Date);

            log.saveError("Error", df.format(to) + " < " + df.format(from));

            return false;
        }
        
        if (!isActive())
        	return true;
        
        int overlapError = inverseConversionOverlap(); 
        if (overlapError==-1)
        {
        	log.saveError("Error", Msg.getMsg( getCtx(),"ConversionFetchException" ) );
        	return false;
        }
        if (overlapError==1)
        {
        	log.saveError("Error", Msg.getMsg( getCtx(),"ConversionAlreadyExistsError") );
        	return false;
        }
         
        if ( (!newRecord) && (busyConversion()) ) {
        	log.saveError("Error", Msg.getMsg( getCtx(),"InvalidChangedCurrency" ) );
        	return false;
        }
        
        return true;
    }		// beforeSave

    private boolean busyConversion() {
    	try
    	{
    		String sql = " SELECT c_currency_id, C_ConversionType_ID, documentdate, dateacct, AD_Client_ID, AD_Org_ID FROM " +
    					 " (SELECT c_currency_id, C_ConversionType_ID, dateordered as documentdate, dateacct, AD_Client_ID, AD_Org_ID FROM C_Order WHERE (processed = 'Y') " + 
    					 " UNION " +
    					 " SELECT c_currency_id, C_ConversionType_ID, dateinvoiced as documentdate, dateacct, AD_Client_ID, AD_Org_ID FROM C_Invoice WHERE (processed = 'Y') " + 
    					 " UNION " +
    					 " SELECT c_currency_id, C_ConversionType_ID, dateacct as documentdate, dateacct, AD_Client_ID, AD_Org_ID FROM C_Payment WHERE (processed = 'Y') " + 
    					 " ) AS tableAux " +
    					 " WHERE ((c_currency_id = ?) OR (c_currency_id = ?)) AND (C_ConversionType_ID = ?) AND ( ((TRUNC(documentdate) >= ?) AND (TRUNC(documentdate) <= ?)) OR ((TRUNC(dateacct) >= ?) AND (TRUNC(dateacct) <= ?)) ) AND (AD_Client_ID = ?) AND (AD_Org_ID = ?) " +
    					 " UNION " +
    					 " (SELECT cl.c_currency_id, 0, c.dateacct as documentdate, c.dateacct, c.AD_Client_ID, c.AD_Org_ID " + 
    					 " FROM C_CashLine cl " +
    					 " INNER JOIN C_Cash c ON (c.C_Cash_ID = cl.C_Cash_ID) " +
    					 " WHERE (cl.processed = 'Y') AND ((c_currency_id = ?) OR (c_currency_id = ?)) AND ( ((TRUNC(c.statementdate) >= ?) AND (TRUNC(c.statementdate) <= ?)) OR ((TRUNC(c.dateacct) >= ?) AND (TRUNC(c.dateacct) <= ?)) ) AND (cl.AD_Client_ID = ?) AND (cl.AD_Org_ID = ?) " + 
    					 " ) ";
    		PreparedStatement pstmt = DB.prepareStatement( sql );
    		pstmt.setInt(1, getC_Currency_ID() );
    		pstmt.setInt(2, getC_Currency_ID_To() );
    		pstmt.setInt(3, getC_ConversionType_ID() );
    		pstmt.setTimestamp(4, getValidFrom() );
    		pstmt.setTimestamp(5, getValidTo() );
    		pstmt.setTimestamp(6, getValidFrom() );
    		pstmt.setTimestamp(7, getValidTo() );
    		pstmt.setInt(8, getAD_Client_ID());
    		pstmt.setInt(9, getAD_Org_ID());
    		pstmt.setInt(10, getC_Currency_ID() );
    		pstmt.setInt(11, getC_Currency_ID_To() );
    		pstmt.setTimestamp(12, getValidFrom() );
    		pstmt.setTimestamp(13, getValidTo() );
    		pstmt.setTimestamp(14, getValidFrom() );
    		pstmt.setTimestamp(15, getValidTo() );
    		pstmt.setInt(16, getAD_Client_ID());
    		pstmt.setInt(17, getAD_Org_ID());
    		
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    			return true;
    		return false;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
	}

	/**
     *  Convert an amount with today's default rate
     *      @param ctx context
     *  @param CurFrom_ID  The C_Currency_ID FROM
     *  @param CurTo_ID    The C_Currency_ID TO
     *  @param Amt amount to be converted
     *      @param AD_Client_ID client
     *      @param AD_Org_ID organization
     *  @return converted amount
     */
    public static BigDecimal convert(Properties ctx, BigDecimal Amt, int CurFrom_ID, int CurTo_ID, int AD_Client_ID, int AD_Org_ID) {
        return convert(ctx, Amt, CurFrom_ID, CurTo_ID, null, 0, AD_Client_ID, AD_Org_ID);
    }		// convert

    /**
     *      Convert an amount
     *      @param ctx context
     *  @param CurFrom_ID  The C_Currency_ID FROM
     *  @param CurTo_ID    The C_Currency_ID TO
     *  @param ConvDate conversion date - if null - use current date
     *  @param C_ConversionType_ID conversion rate type - if 0 - use Default
     *  @param Amt amount to be converted
     *      @param AD_Client_ID client
     *      @param AD_Org_ID organization
     *  @return converted amount or null if no rate
     */
    public static BigDecimal convert(Properties ctx, BigDecimal Amt, int CurFrom_ID, int CurTo_ID, Timestamp ConvDate, int C_ConversionType_ID, int AD_Client_ID, int AD_Org_ID) {

        if (Amt == null) {
            throw new IllegalArgumentException("MConversionRate.getConvertedAmt - required parameter missing - Amt");
        }

        if ((CurFrom_ID == CurTo_ID) || Amt.equals(Env.ZERO)) {
            return Amt;
        }

        // Get Rate
        BigDecimal	retValue	= getRate(CurFrom_ID, CurTo_ID, ConvDate, C_ConversionType_ID, AD_Client_ID, AD_Org_ID);

        if (retValue == null) {
            return null;
        }

        // Get Amount in Currency Precision
        retValue	= retValue.multiply(Amt);

        int	stdPrecision	= MCurrency.getStdPrecision(ctx, CurTo_ID);

        if (retValue.scale() > stdPrecision) {
            retValue	= retValue.setScale(stdPrecision, BigDecimal.ROUND_HALF_UP);
        }

        return retValue;

    }		// convert

    /**
     *      Convert an amount to base Currency
     *      @param ctx context
     *  @param CurFrom_ID  The C_Currency_ID FROM
     *  @param ConvDate conversion date - if null - use current date
     *  @param C_ConversionType_ID conversion rate type - if 0 - use Default
     *  @param Amt amount to be converted
     *      @param AD_Client_ID client
     *      @param AD_Org_ID organization
     *  @return converted amount
     */
    public static BigDecimal convertBase(Properties ctx, BigDecimal Amt, int CurFrom_ID, Timestamp ConvDate, int C_ConversionType_ID, int AD_Client_ID, int AD_Org_ID) {
        return convert(ctx, Amt, CurFrom_ID, MClient.get(ctx).getC_Currency_ID(), ConvDate, C_ConversionType_ID, AD_Client_ID, AD_Org_ID);
    }		// convertBase

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MConversionRate[");

        sb.append(getID()).append(",Currency=").append(getC_Currency_ID()).append(",To=").append(getC_Currency_ID_To()).append(", Multiply=").append(getMultiplyRate()).append(",Divide=").append(getDivideRate()).append(", ValidFrom=").append(getValidFrom());
        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Currency Conversion Rate
     *  @param  CurFrom_ID  The C_Currency_ID FROM
     *  @param  CurTo_ID    The C_Currency_ID TO
     *  @param  ConvDate    The Conversion date - if null - use current date
     *  @param  ConversionType_ID Conversion rate type - if 0 - use Default
     *      @param  AD_Client_ID client
     *      @param  AD_Org_ID       organization
     *  @return currency Rate or null
     */
    public static BigDecimal getRate(int CurFrom_ID, int CurTo_ID, Timestamp ConvDate, int ConversionType_ID, int AD_Client_ID, int AD_Org_ID) {

        if (CurFrom_ID == CurTo_ID) {
            return Env.ONE;
        }

        // Conversion Type
        int	C_ConversionType_ID	= ConversionType_ID;

        if (C_ConversionType_ID == 0) {
            C_ConversionType_ID	= MConversionType.getDefault(AD_Client_ID);
        }

        // Conversion Date
        if (ConvDate == null) {
            ConvDate	= Env.getDate();
        }

        // Get Rate
        String	sql	= "(SELECT MultiplyRate " + "FROM C_Conversion_Rate " + "WHERE C_Currency_ID=?"		// #1
                          + " AND C_Currency_ID_To=?"			// #2
                          + " AND C_ConversionType_ID=?"		// #3
                          + " AND ? BETWEEN ValidFrom AND ValidTo"	// #4      TRUNC (?) ORA-00932: inconsistent datatypes: expected NUMBER got TIMESTAMP
                          + " AND AD_Client_ID IN (0,?)"	// #5
                          + " AND AD_Org_ID IN (0,?) "		// #6
                          + "ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC)"
					      + " UNION "		// #6
                          + "(SELECT DivideRate " + "FROM C_Conversion_Rate " + "WHERE C_Currency_ID=?"		// #1
					      + " AND C_Currency_ID_To=?"			// #2
					      + " AND C_ConversionType_ID=?"		// #3
					      + " AND ? BETWEEN ValidFrom AND ValidTo"	// #4      TRUNC (?) ORA-00932: inconsistent datatypes: expected NUMBER got TIMESTAMP
					      + " AND AD_Client_ID IN (0,?)"	// #5
					      + " AND AD_Org_ID IN (0,?) "		// #6
					      + "ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC)";
        
        BigDecimal		retValue	= null;
        PreparedStatement	pstmt		= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, CurFrom_ID);
            pstmt.setInt(2, CurTo_ID);
            pstmt.setInt(3, C_ConversionType_ID);
            pstmt.setTimestamp(4, ConvDate);
            pstmt.setInt(5, AD_Client_ID);
            pstmt.setInt(6, AD_Org_ID);
            pstmt.setInt(7, CurTo_ID);
            pstmt.setInt(8, CurFrom_ID);
            pstmt.setInt(9, C_ConversionType_ID);
            pstmt.setTimestamp(10, ConvDate);
            pstmt.setInt(11, AD_Client_ID);
            pstmt.setInt(12, AD_Org_ID);

            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {
                retValue	= rs.getBigDecimal(1);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getRate", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        if (retValue == null) {

            s_log.info("getRate - not found - CurFrom=" + CurFrom_ID + ", CurTo=" + CurTo_ID + ", " + ConvDate + ", Type=" + ConversionType_ID + ((ConversionType_ID == C_ConversionType_ID)
                    ? ""
                    : "->" + C_ConversionType_ID) + ", Client=" + AD_Client_ID + ", Org=" + AD_Org_ID);
        }

        return retValue;

    }		// getRate

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Divide Rate.
     *      Sets also Multiply Rate
     *      @param  DivideRate divide rate
     */
    public void setDivideRate(BigDecimal DivideRate) {

        if ((DivideRate == null) || (DivideRate.compareTo(Env.ZERO) == 0) || (DivideRate.compareTo(Env.ONE) == 0)) {

            super.setDivideRate(Env.ONE);
            super.setMultiplyRate(Env.ONE);

        } else {

            super.setDivideRate(DivideRate);

            double	dd	= 1 / DivideRate.doubleValue();

            super.setMultiplyRate(new BigDecimal(dd));
        }

    }		// setDivideRate

    /**
     *      Set Multiply Rate
     *      Sets also Divide Rate
     *      @param MultiplyRate multiply rate
     */
    public void setMultiplyRate(BigDecimal MultiplyRate) {

        if ((MultiplyRate == null) || (MultiplyRate.compareTo(Env.ZERO) == 0) || (MultiplyRate.compareTo(Env.ONE) == 0)) {

            super.setDivideRate(Env.ONE);
            super.setMultiplyRate(Env.ONE);

        } else {

            super.setMultiplyRate(MultiplyRate);

            double	dd	= 1 / MultiplyRate.doubleValue();

            super.setDivideRate(new BigDecimal(dd));
        }

    }		// setMultiplyRate
    
    
    // verifica si existe una superposicion entre la tasa Origen->Destino y su inversa Destino->Origen
    private int inverseConversionOverlap() 
    {
    	try
    	{
    		String sql = 
    				" SELECT 1 FROM C_Conversion_Rate WHERE (C_Currency_ID=? AND C_Currency_ID_To=? OR C_Currency_ID = ? AND C_Currency_ID_To = ?) "    
    				+ " AND C_ConversionType_ID=?"
    				+ " AND ( (TRUNC(ValidFrom) >= ? AND TRUNC(ValidFrom) <= ?)" 
    				+ " OR (TRUNC(ValidTo) >= ? AND TRUNC(ValidTo) <= ?) )"
    				+ " AND AD_Client_ID IN (0,?) AND AD_Org_ID = ? AND ISACTIVE = 'Y' "
    				+ " AND C_Conversion_Rate_ID <> ? "
    				+ " ORDER BY AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC";
	    	PreparedStatement pstmt = DB.prepareStatement( sql );
    		pstmt.setInt(1, getC_Currency_ID_To() );
    		pstmt.setInt(2, getC_Currency_ID() );    		
    		pstmt.setInt(3, getC_Currency_ID() );
    		pstmt.setInt(4, getC_Currency_ID_To() );
    		pstmt.setInt(5, getC_ConversionType_ID() );
    		pstmt.setTimestamp(6, getValidFrom() );
    		pstmt.setTimestamp(7, getValidTo() );
    		pstmt.setTimestamp(8, getValidFrom() );
    		pstmt.setTimestamp(9, getValidTo() );
    		pstmt.setInt(10, getAD_Client_ID());
    		pstmt.setInt(11, getAD_Org_ID());
    		pstmt.setInt(12, getC_Conversion_Rate_ID());
    		
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    			return 1;
    		return 0;
    	}
    	catch (Exception e)
    	{
    		return -1;
    	}
    }
    
    
}	// MConversionRate



/*
 * @(#)MConversionRate.java   02.jul 2007
 * 
 *  Fin del fichero MConversionRate.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
