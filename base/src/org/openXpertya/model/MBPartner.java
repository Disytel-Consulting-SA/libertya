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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MBPartner extends X_C_BPartner {

	public static final int IIBB_CONVENIO_MULTILATERAL_MINIMO = 901;
	public static final int IIBB_CONVENIO_MULTILATERAL_MAXIMO = 924;
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     *
     * @return
     */

    public static MBPartner getTemplate( Properties ctx,int AD_Client_ID ) {
        MBPartner template = getBPartnerCashTrx( ctx,AD_Client_ID );

        if( template == null ) {
            template = new MBPartner( ctx,0,null );
        }

        // Reset

        if( template != null ) {
            template.setC_BPartner_ID( 0 );
            template.setValue( "" );
            template.setName( "" );
            template.setName2( null );
            template.setDUNS( "" );
            template.setFirstSale( null );

            //

            template.setSO_CreditLimit( Env.ZERO );
            template.setSO_CreditUsed( Env.ZERO );
            template.setTotalOpenBalance( Env.ZERO );

            // s_template.setRating(null);
            //

            template.setActualLifeTimeValue( Env.ZERO );
            template.setPotentialLifeTimeValue( Env.ZERO );
            template.setAcqusitionCost( Env.ZERO );
            template.setShareOfCustomer( 0 );
            template.setSalesVolume( 0 );
        }

        return template;
    }    // getTemplate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     *
     * @return
     */

    public static MBPartner getBPartnerCashTrx( Properties ctx,int AD_Client_ID ) {
        MBPartner retValue = null;
        String    sql      = "SELECT * FROM C_BPartner " + "WHERE C_BPartner_ID=(SELECT C_BPartnerCashTrx_ID FROM AD_ClientInfo WHERE AD_Client_ID=?)";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MBPartner( ctx,rs,null );
            } else {
                s_log.log( Level.SEVERE,"Not found for AD_Client_ID=" + AD_Client_ID );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getBPartnerCashTrx

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param Value
     *
     * @return
     */

    public static MBPartner get( Properties ctx,String Value ) {
        if( (Value == null) || (Value.length() == 0) ) {
            return null;
        }

        MBPartner retValue     = null;
        int       AD_Client_ID = Env.getAD_Client_ID( ctx );
        String    sql          = "SELECT * FROM C_BPartner WHERE Value=? AND AD_Client_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setString( 1,Value );
            pstmt.setInt( 2,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MBPartner( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     *
     * @return
     */

    public static BigDecimal getNotInvoicedAmt( int C_BPartner_ID ) {
        BigDecimal retValue = null;
        String     sql      = "SELECT SUM(COALESCE(" + "currencyBase((ol.QtyDelivered-ol.QtyInvoiced)*ol.PriceActual,o.C_Currency_ID,o.DateOrdered, o.AD_Client_ID,o.AD_Org_ID) ,0)) " + "FROM C_OrderLine ol" + " INNER JOIN C_Order o ON (ol.C_Order_ID=o.C_Order_ID) " + "WHERE o.IsSOTrx='Y' AND Bill_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getBigDecimal( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // getNotInvoicedAmt

	/**
	 * @param bpartnerID
	 * @param orgPercepcionID
	 * @param dateInvoiced
	 * @param trxName
	 * @return el porcentaje de exención de la entidad comercial en la
	 *         percepcion parámetro para esa fecha. Si no posee exenciones para
	 *         esa fecha, entonces se retorna 0
	 */
    public static BigDecimal getPercepcionExencionPerc(Integer bpartnerID, Integer orgPercepcionID, Timestamp dateInvoiced, String trxName){
		String sql = "SELECT coalesce(percent,0) as porcexent FROM c_bpartner_percexenc WHERE (?::date between date_from and date_to) AND (c_bpartner_id = ?) AND (ad_org_percepcion_ID = ?) AND (isactive = 'Y') LIMIT 1";
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal exencionPerc = BigDecimal.ZERO;
		try {
			int i = 1;
			ps = DB.prepareStatement(sql, trxName);
			ps.setTimestamp(i++, dateInvoiced);
			ps.setInt(i++, bpartnerID);
			ps.setInt(i++, orgPercepcionID);
			rs = ps.executeQuery();
			if(rs.next()){
				exencionPerc = rs.getBigDecimal(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null) ps = null;
				if(rs != null) rs = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return exencionPerc;
    }

	/**
	 * @param bpartnerID
	 * @param orgPercepcionID
	 * @param dateInvoiced
	 * @param scale 
	 * @param trxName
	 * @return la tasa de exención de la entidad comercial en la percepcion
	 *         parámetro para esa fecha. Si no posee exenciones para esa fecha,
	 *         entonces se retorna 0. La tasa de exención es 1 - porcentaje de
	 *         exención/100
	 */
    public static BigDecimal getPercepcionExencionMultiplierRate(Integer bpartnerID, Integer orgPercepcionID, Timestamp dateInvoiced, Integer scale, String trxName){
		return new BigDecimal(1).subtract((MBPartner.getPercepcionExencionPerc(
				bpartnerID, orgPercepcionID, dateInvoiced, trxName).divide(
				new BigDecimal(100), scale, BigDecimal.ROUND_HALF_UP)));
    } 
    
    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MBPartner.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public MBPartner( Properties ctx ) {
        this( ctx,-1,null );
    }    // MBPartner

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MBPartner( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MBPartner

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_BPartner_ID
     * @param trxName
     */

    public MBPartner( Properties ctx,int C_BPartner_ID,String trxName ) {
        super( ctx,C_BPartner_ID,trxName );

        //

        if( C_BPartner_ID == -1 ) {
            initTemplate( Env.getContextAsInt( ctx,"AD_Client_ID" ));
            C_BPartner_ID = 0;
        }

        if( C_BPartner_ID == 0 ) {
            setValue( "" );
            setName( "" );
            setName2( null );
            setDUNS( "" );

            //

            setIsCustomer( true );
            setIsProspect( true );

            //

            setSendEMail( false );
            setIsOneTime( false );
            setIsVendor( false );
            setIsSummary( false );
            setIsEmployee( false );
            setIsSalesRep( false );
            setIsTaxExempt( false );
            setIsDiscountPrinted( false );

            //

            setSO_CreditLimit( Env.ZERO );
            setSO_CreditUsed( Env.ZERO );
            setTotalOpenBalance( Env.ZERO );
            setSOCreditStatus( SOCREDITSTATUS_NoCreditCheck );

            //

            setFirstSale( null );
            setActualLifeTimeValue( Env.ZERO );
            setPotentialLifeTimeValue( Env.ZERO );
            setAcqusitionCost( Env.ZERO );
            setShareOfCustomer( 0 );
            setSalesVolume( 0 );
        }

        log.fine( toString());
    }    // MBPartner

    /**
     * Constructor de la clase ...
     *
     *
     * @param impBP
     */

    public MBPartner( X_I_BPartner impBP ) {
        this( impBP.getCtx(),0,impBP.get_TrxName());
        setClientOrg( impBP );
        setUpdatedBy( impBP.getUpdatedBy());

        //

        setValue( impBP.getValue());
        setName( impBP.getName());
        setName2( impBP.getName2());
        setDescription( impBP.getDescription());

        // setHelp(impBP.getHelp());

        // Previene la excepción que tira la X si el DUNS es null (para Arg es siempre NULL)
        setDUNS( CalloutInvoiceExt.ComprobantesFiscalesActivos() ? "" : impBP.getDUNS());
        setTaxID( impBP.getTaxID());
        setNAICS( impBP.getNAICS());
        setC_BP_Group_ID( impBP.getC_BP_Group_ID());
        setIIBB(impBP.getIIBB());
        setC_Categoria_Iva_ID(impBP.getC_Categoria_Iva_ID());
        setTaxIdType(impBP.getTaxIdType());
        
        setIsProspect(impBP.isProspect());
        setIsCustomer(impBP.isCustomer());
        setIsVendor(impBP.isVendor());
        setIsEmployee(impBP.isEmployee());
        setIsMultiCUIT(impBP.isMultiCUIT());
        
        // Estado de crédito de cliente
        if (impBP.isCustomer()) {
        	if (impBP.getSOCreditStatus() != null) {
        		setSOCreditStatus(impBP.getSOCreditStatus());
        	}
        	setSO_CreditLimit(impBP.getSO_CreditLimit());
        	
        	// Comercial
        	setSalesRep_ID(impBP.getSalesRep_ID());
        }
    }    // MBPartner

    /** Descripción de Campos */

    private MUser[] m_contacts = null;

    /** Descripción de Campos */

    private MBPartnerLocation[] m_locations = null;

    /** Descripción de Campos */

    private MBPBankAccount[] m_accounts = null;

    /** Descripción de Campos */

    private Integer m_primaryC_BPartner_Location_ID = null;

    /** Descripción de Campos */

    private Integer m_primaryAD_User_ID = null;

    /** Descripción de Campos */

    private boolean m_TotalOpenBalanceSet = false;

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     *
     * @return
     */

    private boolean initTemplate( int AD_Client_ID ) {
        if( AD_Client_ID == 0 ) {
            throw new IllegalArgumentException( "Client_ID=0" );
        }

        boolean success = true;
        String  sql     = "SELECT * FROM C_BPartner " + "WHERE C_BPartner_ID=(SELECT C_BPartnerCashTrx_ID FROM AD_ClientInfo WHERE AD_Client_ID=?)";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                success = load( rs );
            } else {
                load( 0,null );
                success = false;
                log.severe( "None found" );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        setStandardDefaults();
        setC_BPartner_ID( 0 );
        setValue( "" );
        setName( "" );
        setName2( null );

        return success;
    }    // getTemplate

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MUser[] getContacts( boolean reload ) {
        if( reload || (m_contacts == null) || (m_contacts.length == 0) ) {
            ;
        } else {
            return m_contacts;
        }

        //

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM AD_User WHERE C_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MUser( getCtx(),rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getContacts",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        m_contacts = new MUser[ list.size()];
        list.toArray( m_contacts );

        return m_contacts;
    }    // getContacts

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     *
     * @return
     */

    public MUser getContact( int AD_User_ID ) {
        MUser[] users = getContacts( false );

        if( users.length == 0 ) {
            return null;
        }

        for( int i = 0;(AD_User_ID != 0) && (i < users.length);i++ ) {
            if( users[ i ].getAD_User_ID() == AD_User_ID ) {
                return users[ i ];
            }
        }

        return users[ 0 ];
    }    // getContact

    /**
     * Descripción de Método
     *
     *
     * @param reload
     *
     * @return
     */

    public MBPartnerLocation[] getLocations( boolean reload ) {
        if( reload || (m_locations == null) || (m_locations.length == 0) ) {
            ;
        } else {
            return m_locations;
        }

        //

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_BPartner_Location WHERE C_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBPartnerLocation( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLocations",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        m_locations = new MBPartnerLocation[ list.size()];
        list.toArray( m_locations );

        return m_locations;
    }    // getLocations

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     *
     * @return
     */

    public MBPartnerLocation getLocation( int C_BPartner_Location_ID ) {
        MBPartnerLocation[] locations = getLocations( false );

        if( locations.length == 0 ) {
            return null;
        }

        MBPartnerLocation retValue = null;

        for( int i = 0;i < locations.length;i++ ) {
            if( locations[ i ].getC_BPartner_Location_ID() == C_BPartner_Location_ID ) {
                return locations[ i ];
            }

            if( (retValue == null) && locations[ i ].isBillTo()) {
                retValue = locations[ i ];
            }
        }

        if( retValue == null ) {
            return locations[ 0 ];
        }

        return retValue;
    }    // getLocation

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

    public MBPBankAccount[] getBankAccounts( boolean requery ) {
        if( (m_accounts != null) && (m_accounts.length >= 0) &&!requery ) {    // re-load
            return m_accounts;
        }

        //

        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_BP_BankAccount WHERE C_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MBPBankAccount( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getBankAccounts",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        m_accounts = new MBPBankAccount[ list.size()];
        list.toArray( m_accounts );

        return m_accounts;
    }    // getBankAccounts

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MBPartner[ID=" ).append( getID()).append( ",Value=" ).append( getValue()).append( ",Name=" ).append( getName()).append( ",Open=" ).append( getTotalOpenBalance()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param AD_Org_ID
     */

    public void setClientOrg( int AD_Client_ID,int AD_Org_ID ) {
        super.setClientOrg( AD_Client_ID,AD_Org_ID );
    }    // setClientOrg

    /**
     * Descripción de Método
     *
     *
     * @param AD_OrgBP_ID
     */

    public void setAD_OrgBP_ID( int AD_OrgBP_ID ) {
        if( AD_OrgBP_ID == 0 ) {
            super.setAD_OrgBP_ID( null );
        } else {
            super.setAD_OrgBP_ID( String.valueOf( AD_OrgBP_ID ));
        }
    }    // setAD_OrgBP_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_OrgBP_ID_Int() {
        String org = super.getAD_OrgBP_ID();

        if( org == null ) {
            return 0;
        }

        int AD_OrgBP_ID = 0;

        try {
            AD_OrgBP_ID = Integer.parseInt( org );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,org,ex );
        }

        return AD_OrgBP_ID;
    }    // getAD_OrgBP_ID_Int

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrimaryC_BPartner_Location_ID() {
        if( m_primaryC_BPartner_Location_ID == null ) {
            MBPartnerLocation[] locs = getLocations( false );

            for( int i = 0;(m_primaryC_BPartner_Location_ID == null) && (i < locs.length);i++ ) {
                if( locs[ i ].isBillTo()) {
                    setPrimaryC_BPartner_Location_ID( locs[ i ].getC_BPartner_Location_ID());

                    break;
                }
            }

            // get first

            if( (m_primaryC_BPartner_Location_ID == null) && (locs.length > 0) ) {
                setPrimaryC_BPartner_Location_ID( locs[ 0 ].getC_BPartner_Location_ID());
            }
        }

        if( m_primaryC_BPartner_Location_ID == null ) {
            return 0;
        }

        return m_primaryC_BPartner_Location_ID.intValue();
    }    // getPrimaryC_BPartner_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrimaryAD_User_ID() {
        if( m_primaryAD_User_ID == null ) {
            MUser[] users = getContacts( false );

            // for (int i = 0; i < users.length; i++)
            // {
            // }

            if( (m_primaryAD_User_ID == null) && (users.length > 0) ) {
                setPrimaryAD_User_ID( users[ 0 ].getAD_User_ID());
            }
        }

        if( m_primaryAD_User_ID == null ) {
            return 0;
        }

        return m_primaryAD_User_ID.intValue();
    }    // getPrimaryAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     */

    public void setPrimaryC_BPartner_Location_ID( int C_BPartner_Location_ID ) {
        m_primaryC_BPartner_Location_ID = new Integer( C_BPartner_Location_ID );
    }    // setPrimaryC_BPartner_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setPrimaryAD_User_ID( int AD_User_ID ) {
        m_primaryAD_User_ID = new Integer( AD_User_ID );
    }    // setPrimaryAD_User_ID

    /**
     * Actualizo el crédito de la entidad comercial
     *
     */

    public void setTotalOpenBalance() {
    	// Obtengo el managaer actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager();
		CallResult result = new CallResult();
		try{
			result = manager.updateBalance(getCtx(), new MOrg(
					getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()), this,
					get_TrxName());
		} catch(Exception e){
			result.setMsg(e.getMessage(), true);
		} 
		// Si hubo error, obtengo el mensaje y retorno inválido
		if (result.isError()) {
			log.severe(result.getMsg());
		}
		// Actualizar el estado del crédito
		setSOCreditStatus();
    }    // setTotalOpenBalance

    /**
     * Descripción de Método
     *
     *
     * @param calculate
     *
     * @return
     */

    public BigDecimal getTotalOpenBalance( boolean calculate ) {
        if( (getTotalOpenBalance() == null) && calculate ) {
            setTotalOpenBalance();
        }

        return super.getTotalOpenBalance();
    }    // getTotalOpenBalance

    /**
     * Descripción de Método
     *
     */

    public void setSOCreditStatus() {
    	// Obtengo el managaer actual
		CurrentAccountManager manager = CurrentAccountManagerFactory
				.getManager();
    	// Seteo el estado actual del cliente y lo obtengo
		CallResult result = new CallResult();
		try{
			result = manager.setCurrentAccountStatus(getCtx(), this,
					new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()),
					get_TrxName());
		} catch(Exception e){
			result.setMsg(e.getMessage(), true);
		} 
		// Si hubo error obtengo el mensaje
		if (result.isError()) {
			log.severe(result.getMsg());
		}
    }                   // setSOCreditStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getCreditWatchRatio() {
        return new BigDecimal( 0.90 );
    }    // getCreditWatchRatio

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCreditStopHold() {
        String status = getSOCreditStatus();

        return SOCREDITSTATUS_CreditStop.equals( status ) || SOCREDITSTATUS_CreditHold.equals( status );
    }    // isCreditStopHold

    /**
     * Descripción de Método
     *
     *
     * @param TotalOpenBalance
     */

    public void setTotalOpenBalance( BigDecimal TotalOpenBalance ) {
        m_TotalOpenBalanceSet = false;
        super.setTotalOpenBalance( TotalOpenBalance );
    }    // setTotalOpenBalance

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        
    	if (!success)
    		return success;
    	
    	if( newRecord ) {

            // Trees

            insert_Tree( MTree_Base.TREETYPE_BPartner );

            // Accounting

            insert_Accounting( "C_BP_Customer_Acct","C_BP_Group_Acct","p.C_BP_Group_ID=" + getC_BP_Group_ID());
            insert_Accounting( "C_BP_Vendor_Acct","C_BP_Group_Acct","p.C_BP_Group_ID=" + getC_BP_Group_ID());
            insert_Accounting( "C_BP_Employee_Acct","C_AcctSchema_Default",null );
        }

        // Value/Name change

        if( !newRecord && ( is_ValueChanged( "Value" ) || is_ValueChanged( "Name" ))) {
            MAccount.updateValueDescription( getCtx(),"C_BPartner_ID=" + getC_BPartner_ID(),get_TrxName());
        }

        return success;
    }    // afterSave



    protected boolean beforeDelete() {
        return delete_Accounting( "C_BP_Customer_Acct" ) && delete_Accounting( "C_BP_Vendor_Acct" ) && delete_Accounting( "C_BP_Employee_Acct" );
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( success ) {
            delete_Tree( MTree_Base.TREETYPE_BPartner );
        }

        
        return success;
    }    // afterDelete
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */
    
    public Vector<MRetencionSchema> get_EsquemasRetencion(){
    	/* busco todos los esquemas de retencion asociados a ese proveedor */
    	
    	Vector<MRetencionSchema> esquemas = new Vector<MRetencionSchema>();
    	String sql =" SELECT c_bpartner_retencion_id, ad_client_id, ad_org_id, isactive, created, " +
    				"        createdby, updated, updatedby, c_retencionschema_id, c_bpartner_id, " +
    				"       description, taxid " +
    				" FROM c_bpartner_retencion " +
    				" WHERE isactive = 'Y' and c_bpartner_id = ? ";
    	
    	PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	esquemas.add( new MRetencionSchema(getCtx(),rs.getInt("c_retencionschema_id"), get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.info( "Error BPartner - get_EsquemasRetencion = " + e );
        }
    	return esquemas;
    }

	/**
	 * Calcula el porcentaje de excepción según los períodos de excepción configurados
	 * para un esquema de retención aplicado a la entidad comercial.
	 * @param retSchema Esquema de retención a utilizar.
	 * @param vfechaPago Fecha de realización de la transacción.
	 * @return Retorna un valor entre 1 y 100 que indica el porcentaje total de
	 * excepción para el esquema en la fecha indicada.
	 */
	public BigDecimal getRetencionExenPercent(int retSchema,Timestamp vfechaPago) {
	
		BigDecimal rate = Env.ZERO;
		BigDecimal totalRate = Env.ONE;
	
		// Consulta que obtiene los porcentajes del monto de retención que se tienen
		// que aplicar según los períodos de excepción de esta EC. 
		// NO son los porcentajes de Excepción, por eso se invierte la tasa
		// restando el porcentaje de Excepción a 1.
		// Ej: 
		//     - Porcentaje de Excepción: 40%
		//     - Porcentaje a retener: 60% (del monto final de la retención)
		//     - Valor en la consulta: 0.60
		/* 
		String sql =
			" SELECT (1 - percent/100) AS PercentRate " +
			" FROM c_bpartner_retexenc bp " +
			" WHERE c_bpartner_retencion_id in " + 
			"	(SELECT c_bpartner_retencion_id " +
			"	 FROM c_bpartner_retencion " +
			"	WHERE c_RetencionSchema_id = ? " +    
			"	AND C_BPartner_ID = ? ) " +
			" AND ? BETWEEN date_from and date_to ";
		*/
		String sql =
		" SELECT (1 - bp.percent/100) AS PercentRate " +  
		" FROM c_bpartner_retexenc bp INNER JOIN " +  
		"      c_bpartner_retencion r ON (bp.c_bpartner_retencion_id = r.c_bpartner_retencion_id) " +
		" WHERE r.C_BPartner_ID = ? AND " + 
		"       r.c_RetencionSchema_id = ? AND " + 
		"       ? BETWEEN bp.date_from AND bp.date_to ";
	
		

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			// Se ejecuta la consulta.
			pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1,getC_BPartner_ID());
			pstmt.setInt(2,retSchema);
			pstmt.setTimestamp(3, vfechaPago);	
			rs = pstmt.executeQuery();
			// Se multiplican las tasas para obtener el procentaje total
			// a aplicar a la retención.
			while (rs.next()) {
				
				rate = rs.getBigDecimal("PercentRate");
				totalRate = totalRate.multiply(rate);
			}  
			
		} catch (Exception ex) {
			log.info("Error al buscar exenciones para el proveedor");
			ex.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close();
			} catch (SQLException e) {
				log.log( Level.SEVERE,"Cannot close statement or resultset" );
			}
		}
		
		// Como totalRate contiene el porcentaje real a retener y este método
		// retorna el porcentaje de exepción, entonces se invierte la tasa
		// y se multiplica por 100 para obtener el valor entre 0% y 100%.
		return Env.ONE.subtract(totalRate).multiply(Env.ONEHUNDRED);
	}
	
	/**
	 * @author: Horacio Alvarez
	 * @fecha: 22-12-08
	 * @descripcion: devuelve true si el RUT  es válido
	 * @comentario: Localizacion CHILE.
	 */
	/*
	public boolean isValidRUT()
	{
		boolean valid = false;
		try
		{
			int j = 0; 
			int entero = 0;
			int suma = 0;
			String verificador;
			
			int[] productos = {2,3,4,5,6,7};
			String RUT[] = getrut().split("-"); //example: 30686957-7
			
			for(int i = RUT[0].length(); i > 0; i-- )
			{
				entero = Integer.parseInt(RUT[0].substring(i-1,i));
				if (j > 5) 
				    j = 0;
				suma += entero*productos[j];
				j++;
			}

			int obtenido = 11 - (suma % 11);
			switch(obtenido)
			{
				case 11: verificador = "0"; break;
				case 10: verificador = "K"; break;
				default: verificador = Integer.toString(obtenido); break;
			}
			if(verificador.equals(RUT[1]))
				valid = true;
		}
		catch(Exception ex)
		{
		    log.warning("Error al verificar el RUT: "+ex.toString());
		}		
		
		return valid;
	}	
*/
	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// Validaciones para la localización Argentina.
		if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
			MCategoriaIva mCategoriaIva = new MCategoriaIva(getCtx(), getC_Categoria_Iva_ID(), get_TrxName());
			
			// Se quitan espacios, puntos y guiones (no tiene sentido almacenarlos
			if (getTaxID()!=null)
				setTaxID(getTaxID().trim().replace("-", "").replace(".", ""));
			String cuit = getTaxID();
			
			// Validación del existencia de CUIT para categorías que requieren CUIT.
			if (getC_Categoria_Iva_ID() > 0 && mCategoriaIva.isRequiereCUIT()) {
				
				// Si la Categoría de IVA asignada requiere el dato de CUIT, entonce el Tipo de Identificación solo puede ser CUIT o CUIL.
				if ( (getTaxIdType() != null) && (((MBPartner.TAXIDTYPE_CUIT).compareTo(getTaxIdType()) != 0) && ((MBPartner.TAXIDTYPE_CUIL).compareTo(getTaxIdType()) != 0)) ){
					log.saveError("SaveError", Msg.translate(getCtx(), "RequiredCUITCUILTaxType"));
					return false;
				}				
				
				if (cuit == null || cuit.length() == 0) {
					log.saveError("InvalidCUIT",Msg.translate(getCtx(),"RequiredCUIT"));
					return false;
				}
				
				// Si se ingresó CUIT, se valida que sea correcto. 
				if (cuit != null && cuit.length() > 0 && !CalloutInvoiceExt.ValidarCUIT(cuit)) {
					log.saveError("InvalidCUIT", "");
					return false;
				}
				// CUIT Repetido siempre que la configuración de la compañía
				String whereClauseCUIT = newRecord ? "isactive = 'Y' AND trim(translate(taxid,'-','')) = trim(translate(?,'-','')) AND ad_client_id = "
						+ getAD_Client_ID()
						: "isactive = 'Y' AND trim(translate(taxid,'-','')) = trim(translate(?,'-','')) AND ad_client_id = " + getAD_Client_ID()
								+ " AND c_bpartner_id <> " + getID();
				MClientInfo clientInfo = MClientInfo.get(getCtx(), getAD_Client_ID());
				if (!Util.isEmpty(cuit, true)
						&& clientInfo.isUniqueCuit()
						&& !isMultiCUIT()
						&& PO.existRecordFor(getCtx(), X_C_BPartner.Table_Name,
								whereClauseCUIT, new Object[] { getTaxID() },
								get_TrxName())) {
					String sameCUITBPname = DB.getSQLValueString(get_TrxName(),
							"select name from c_bpartner where "
									+ whereClauseCUIT, getTaxID());
					log.saveError("SaveError", Msg.getMsg(getCtx(), "SameCUITInBPartner", new Object[]{cuit,sameCUITBPname}));
					return false;
				}
			// Aquí el valor es un DNI
			} else {
				
				if (getTaxID() != null && !getTaxID().isEmpty()) {
					// Se quitan los puntos "." del número.
					// Además se quitan los guiones "-" para salvar el caso en que se pasa una EC de responsable inscripto
					// a consumidor final y se quiere mantener el CUIT.
					String dni = getTaxID().trim().replace(".", "").replace("-", "");
					// Se obliga a que sea un valor numérico ya que las impresoras fiscales
					// por ejemplo requieren que el DNI enviado no contenga caracteres que
					// no sean números.
					try {
						Long.parseLong(dni);
						setTaxID(dni);
					} catch (NumberFormatException e) {
						log.saveError("SaveError", Msg.translate(getCtx(), "DNIInvalidFormat"));
						return false;
					}
				}
			}
			
			// Se valida el formato del Nro de Identificación solo si el Tipo es CUIT o CUIL.
			if ( (getTaxIdType() != null) && (((MBPartner.TAXIDTYPE_CUIT).compareTo(getTaxIdType()) == 0) || ((MBPartner.TAXIDTYPE_CUIL).compareTo(getTaxIdType()) == 0)) && !CalloutInvoiceExt.ValidarCUIT(cuit) ){
				log.saveError("InvalidCUIT", "");
				return false;
			}
			
			// Si se indica un valor en el campo Nro. Idenficación se obliga a ingresar el valor para el campo Tipo de Identificación.
			if ( !Util.isEmpty(getTaxID(), true) && Util.isEmpty(getTaxIdType(), true) ){
				log.saveError("SaveError", Msg.translate(getCtx(), "TypeIDRequired"));
				return false;
			}
		}
		
		//LOCALIZACION CHILE
/*		
		if(MPreference.GetCustomPreferenceValueBool("LOCAL_CL"))
		{
			if(!isValidRUT())
			{
				log.saveError("InvalidRUT","");
				return false;
			}			
		}
*/
		
		
		
		// Objetivo mayor o igual que cero.
		if (getGoal() != null && getGoal().compareTo(BigDecimal.ZERO) < 0){
			log.saveError("SaveError", Msg.getMsg(getCtx(), "FieldUnderZeroError", new Object[] {Msg.translate(getCtx(),"Goal")}));
			return false;
		}
		
		// Límite de crédito mayor o igual que cero.
		if (getSO_CreditLimit() != null && getSO_CreditLimit().compareTo(BigDecimal.ZERO) < 0){
			log.saveError("SaveError", Msg.getMsg(getCtx(), "FieldUnderZeroError", new Object[] {Msg.translate(getCtx(),"SO_CreditLimit")}));
			return false;
		}
		
		// Rango correcto en primeras vacaciones
		boolean firstHolidaysEntered = getStartHolidays() != null && getEndHolidays() != null;
		if (firstHolidaysEntered && getStartHolidays().compareTo(getEndHolidays()) > 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "FirstMustBeLowerThanSecond", 
					new Object[] {Msg.translate(getCtx(),"StartHolidays"),
							      Msg.translate(getCtx(),"EndHolidays")}));
			return false;
		}

		// Rango correcto en segundas vacaciones
		boolean secondHolidaysEntered = getStartHolidays2() != null && getEndHolidays2() != null;
		if (secondHolidaysEntered && getStartHolidays2().compareTo(getEndHolidays2()) > 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "FirstMustBeLowerThanSecond", 
					new Object[] {Msg.translate(getCtx(),"StartHolidays2"),
							      Msg.translate(getCtx(),"EndHolidays2")}));
			return false;
		}

		// Validación de campo Value duplicado: no se permiten EC con el mismo código.
		if (sameColumnValueValidation(get_TableName(), "Value",
				"C_BPartner_ID", getValue(), newRecord, true)) {
			return false;
		}
		
		// Si el contexto de aplicación del descuento de entidad comercial es
		// para Cobro, verificar que el saldo de la entidad comercial sea menor
		// o igual a 0
		if (!newRecord && is_ValueChanged("DiscountContext")
				&& getDiscountContext().equals(DISCOUNTCONTEXT_Receipt)
				&& !Util.isEmpty(getM_DiscountSchema_ID(), true)) {
			MDiscountSchema discountSchema = new MDiscountSchema(getCtx(),
					getM_DiscountSchema_ID(), get_TrxName());
			// El esquema de descuento debe ser de tipo financiero
			if (!discountSchema.getDiscountContextType().equals(
					MDiscountSchema.DISCOUNTCONTEXTTYPE_Financial)) {
				log.saveError("DiscountSchemaMustBeFinancial", "");
				return false;
			}
			// Le consulto al manager actual
			CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
			MOrg org = new MOrg(getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName());
			CallResult result = new CallResult();
			try{
				result = manager.hasZeroBalance(getCtx(), org, this, true, get_TrxName());
			} catch(Exception e){
				result.setMsg(e.getMessage(), true);
			}
			if(result.isError()){ 
				log.saveError("SaveError", result.getMsg());
				return false;
			}
			if(result.getResult() == null || !((Boolean)result.getResult())){
				log.saveError("BPBalanceMustBeEqualOrLessMinimum", "");
				return false;
			}
		}
		
		// Si el usuario seteado en salesrep no es un responsable de ventas
		// entonces null
		if(!Util.isEmpty(getSalesRep_ID(), true)){
			MUser userSalesRep = new MUser(getCtx(),getSalesRep_ID(), get_TrxName());
			Integer salesRepID = 0;
			if(!Util.isEmpty(userSalesRep.getC_BPartner_ID(), true)){
				MBPartner salesRep = new MBPartner(getCtx(),
						userSalesRep.getC_BPartner_ID(), get_TrxName());
				if(salesRep.isSalesRep()){
					salesRepID = userSalesRep.getID();
				}
			}
			setSalesRep_ID(salesRepID);
		}
		
		// Marcar convenio multilateral
		if(!Util.isEmpty(getIIBB(), true)){
			setIsConvenioMultilateral(isConvenioMultilateral(getIIBB()));
		}
		
		return true;
	}

	/**
	 * Valida que el estado de crédito esté en el parámetro y el estado
	 * secundario no esté en el parámetro, en ese caso seteo el secundario con
	 * el parámetro
	 * 
	 * @param SOCreditStatus
	 *            estado de crédito
	 * @param secondaryCreditStatus
	 *            estado de crédito secundario
	 */
	private void validateAndSetSecondaryCreditStatus(String SOCreditStatus, String secondaryCreditStatus){
		// Si el estado de crédito es el parámetro y el secundario no es el
		// parámetro, entonces seteo el crédito secundario con el parámetro
		if(getSOCreditStatus().equals(SOCreditStatus)
				&& !getSecondaryCreditStatus().equals(secondaryCreditStatus)) {
			setSecondaryCreditStatus(secondaryCreditStatus);			
		}
	}
	
	public static BigDecimal getRetencionSegunPadronBsAS(Timestamp date, String taxID, String trxName){
		return MBPartnerPadronBsAs.getBPartnerPerc("retencion", taxID, date,
				MBPartnerPadronBsAs.PADRONTYPE_PadrónBsAs, trxName);
	}	
	
	public boolean isConsumidorFinal(){
		MCategoriaIva categoria = new MCategoriaIva(getCtx(),getC_Categoria_Iva_ID(),get_TrxName());
		return categoria.getName().equalsIgnoreCase("Consumidor Final");
	}
	
	/**
	 * Contribuyentes Locales y en Convenio Multilateral, los contribuyentes en
	 * Convenio se identifican por su N°de inscripción cuyo inicio comprende del
	 * 901 al 924 los demás son contribuyentes locales
	 * 
	 * @param iibb
	 *            nro de IIBB
	 * @return true si está en convenio, false caso contrario
	 */
	public static boolean isConvenioMultilateral(String iibb){
		int initialIIBBInt = 0;
		if(!Util.isEmpty(iibb, true) && iibb.length() > 3){
			String initialIIBB = iibb.substring(0, 3);
			try{
				initialIIBBInt = Integer.parseInt(initialIIBB);
			} catch(Exception e){
				initialIIBBInt = 0;
			}
		}

		return initialIIBBInt >= IIBB_CONVENIO_MULTILATERAL_MINIMO
				&& initialIIBBInt <= IIBB_CONVENIO_MULTILATERAL_MAXIMO;
	}
	

}    // MBPartner



/*
 *  @(#)MBPartner.java   02.07.07
 * 
 *  Fin del fichero MBPartner.java
 *  
 *  Versión 2.2
 *
 */
