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



package org.openXpertya.wstore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MAdvertisement;
import org.openXpertya.model.MAsset;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCommissionRun;
import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInterestArea;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoicePaySchedule;
import org.openXpertya.model.MNote;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MRegistration;
import org.openXpertya.model.MRequest;
import org.openXpertya.model.MRequestType;
import org.openXpertya.model.MRfQ;
import org.openXpertya.model.MRfQResponse;
import org.openXpertya.model.MTimeExpense;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.wf.MWFActivity;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Info {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static Info getGeneral() {
        if( m_general == null ) {
            m_general = new Info( new Properties(),null );
        }

        return m_general;
    }    // getGeneral

    /** Descripción de Campos */

    private static Info m_general = null;

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param wu
     */

    public Info( Properties ctx,WebUser wu ) {
        m_ctx = ctx;
        m_wu  = wu;
        
    }    // Info

    /** Descripción de Campos */

    static public final String NAME = "info";

    /** Descripción de Campos */

    private CLogger		log = CLogger.getCLogger(getClass());

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /** Descripción de Campos */

    private WebUser m_wu = null;

    /** Descripción de Campos */

    private String m_infoMessage = null;

    /** Descripción de Campos */

    private int m_id = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "Info[" );

        sb.append( getC_BPartner_ID());
        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMessage() {
        String retValue = m_infoMessage;

        m_infoMessage = null;

        return retValue;
    }    // getMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo() {
        return m_infoMessage;
    }    // getInfo

    /**
     * Descripción de Método
     *
     *
     * @param msg
     */

    public void setMessage( String msg ) {
        m_infoMessage = msg;
    }    // setMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getId() {
        return m_id;
    }    // getId

    /**
     * Descripción de Método
     *
     *
     * @param id
     */

    public void setId( String id ) {
        try {
            setId( Integer.parseInt( id ));
        } catch( NumberFormatException ex ) {
            log.log( Level.SEVERE,"setId - " + id + " - " + ex.toString());
        }
    }    // setId

    /**
     * Descripción de Método
     *
     *
     * @param id
     */

    public void setId( int id ) {
        log.info( "setID = " + id );
        m_id = id;
    }    // setId

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Client_ID() {
        if( m_wu == null ) {
            return -1;
        }

        return m_wu.getAD_Client_ID();
    }    // getC_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        if( m_wu == null ) {
            return -1;
        }

        return m_wu.getC_BPartner_ID();
    }    // getC_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_User_ID() {
        if( m_wu == null ) {
            return -1;
        }

        return m_wu.getAD_User_ID();
    }    // getAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getUser_ID() {
        return getAD_User_ID();
    }    // getAD_User_ID

    /**
     * Descripción de Método
     * 
     * Obtiene el listado de presupuestos acorde a los parámetros de búsqueda incluidos.
     *
     * @return
     */

    public ArrayList getProposals() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        
		String minDate = m_ctx.getProperty( ProposalsServlet.P_FECHA_MINIMA );
		String maxDate = m_ctx.getProperty( ProposalsServlet.P_FECHA_MAXIMA );
        
        // String    sql  = "SELECT * FROM C_Order WHERE C_BPartner_ID=?" + " AND DocStatus NOT IN ('DR','IN') " + "ORDER BY DocumentNo DESC";
        String sql = "SELECT * "
        	+ " FROM C_Order "
        	+ " WHERE C_BPartner_ID=? "
        	+ " AND C_DocType_ID='1000335' ";
        
	    if( (minDate != null) && (minDate != "") ) {
	    	sql += " AND DateOrdered > TO_DATE('" + minDate + "','yyyy-MM-dd') - cast(cast(1 as text)|| 'days' as interval) ";
	    }
	    else {
	    	sql += " AND DateOrdered > CURRENT_DATE - 61 ";
	    }
	    
		if( (maxDate != null) && (maxDate != "") ) {
			sql += " AND DateOrdered < TO_DATE('" + maxDate + "','yyyy-MM-dd') + cast(cast(1 as text)|| 'days' as interval) ";
		}
		else { 
	    	sql += " AND DateOrdered < CURRENT_DATE + 91 ";
		}
        
        sql += "ORDER BY created DESC";
        
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MOrder( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getProposals",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getProposals #" + list.size());

        return list;
    }    // getProposals    

    /**
     * Descripción de Método
     *
     * Obtiene el listado de solicitudes acorde a los parámetros de búsqueda incluidos.
     *
     * @return
     */

    public ArrayList getRequisitions() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        
		String minDate = m_ctx.getProperty( RequisitionsServlet.P_FECHA_MINIMA );
		String maxDate = m_ctx.getProperty( RequisitionsServlet.P_FECHA_MAXIMA );	
        
        // String    sql  = "SELECT * FROM C_Order WHERE C_BPartner_ID=?" + " AND DocStatus NOT IN ('DR','IN') " + "ORDER BY DocumentNo DESC";
        String sql = "SELECT * "
        	+ " FROM C_Order "
        	+ " WHERE C_BPartner_ID=?"
        	+ " AND C_DocType_ID='1000334' ";
        
	    if( (minDate != null) && (minDate != "") ) {
	    	sql += " AND DateOrdered > TO_DATE('" + minDate + "','yyyy-MM-dd') - cast(cast(1 as text)|| 'days' as interval) ";
	    }
	    else {
	    	sql += " AND DateOrdered > CURRENT_DATE - 61 ";
	    }
	    
		if( (maxDate != null) && (maxDate != "") ) {
			sql += " AND DateOrdered < TO_DATE('" + maxDate + "','yyyy-MM-dd') + cast(cast(1 as text)|| 'days' as interval) ";
		}
		else { 
	    	sql += " AND DateOrdered < CURRENT_DATE + 91 ";
		}	
        
        sql += " ORDER BY created DESC ";
        
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MOrder( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRequisitions",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getRequisitions #" + list.size());

        return list;
    }    // getRequisitions  
    
    /**
     * Descripción de Método
     *
     * Obtiene el listado de pedidos acorde a los parámetros de búsqueda incluidos.
     *
     * @return
     */

    public ArrayList getOrders() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        
        String minDate = m_ctx.getProperty( OrdersServlet.P_FECHA_MINIMA );
		String maxDate = m_ctx.getProperty( OrdersServlet.P_FECHA_MAXIMA );
        
        // String    sql  = "SELECT * FROM C_Order WHERE C_BPartner_ID=?" + " AND DocStatus NOT IN ('DR','IN') " + "ORDER BY DocumentNo DESC";
        
		String sql = "SELECT * "
        	+ " FROM C_Order "
        	+ " WHERE C_BPartner_ID=? ";
        
		String DocStatus = "";
		String statusVar = "";
		int count = 0;
		
        if( OrdersServlet.P_VARIABLES.length > 0 ) {
			for( int i = 0; i < OrdersServlet.P_VARIABLES.length; i++ ) {
				statusVar = m_ctx.getProperty( OrdersServlet.P_VARIABLES[ i ] );
				if( (statusVar != null ) && (statusVar != "") ) {
					if( count == 0 ) {
						DocStatus += "'" + statusVar + "'";
						count = 1;
					}
					else {
						DocStatus += ",'" + statusVar + "'";
					}
				}
			}
		}
        
        if( DocStatus != "" ) {
        	sql += " AND DocStatus IN (" + DocStatus + ") ";
        }
        
	    if( (minDate != null) && (minDate != "") ) {
	    	sql += " AND DateOrdered > TO_DATE('" + minDate + "','yyyy-MM-dd') - cast(cast(1 as text)|| 'days' as interval) ";
	    }
	    else {
	    	sql += " AND DateOrdered > CURRENT_DATE - 61 ";
	    }
	    
		if( (maxDate != null) && (maxDate != "") ) {
			sql += " AND DateOrdered < TO_DATE('" + maxDate + "','yyyy-MM-dd') + cast(cast(1 as text)|| 'days' as interval) ";
		}
		else { 
	    	sql += " AND DateOrdered < CURRENT_DATE + 91 ";
		}
        
        sql += "ORDER BY created DESC";
        //sql += "ORDER BY DocumentNo DESC";
        
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MOrder( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getOrders",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getOrders #" + list.size());

        return list;
    }    // getOrders

    /**
     * Descripción de Método
     *
     * Obtiene los datos del pedido con la Id dada.
     *
     * @return
     */

    public MOrder getOrder() {
        MOrder retValue = null;
        String sql      = "SELECT * FROM C_Order WHERE C_BPartner_ID=? AND C_Order_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());
            pstmt.setInt( 2,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MOrder( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getOrder ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getOrder ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getOrder

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getShipments() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_InOut WHERE C_BPartner_ID=?" + " AND DocStatus NOT IN ('DR','IN') " + " ORDER BY DocumentNo DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInOut( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getShipments",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getShipments #" + list.size());

        return list;
    }    // getShipments

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getRequestsOwn() {
        return getRequests( true );
    }    // getRequestsOwn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getRequestsAssigned() {
        return getRequests( false );
    }    // getRequestsAssigned

    /**
     * Descripción de Método
     *
     *
     * @param own
     *
     * @return
     */

    public ArrayList getRequests( boolean own ) {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        String    sql  = own
                         ?"SELECT * FROM R_Request WHERE C_BPartner_ID=? ORDER BY DocumentNo DESC"
                         :"SELECT * FROM R_Request WHERE SalesRep_ID IN (SELECT AD_User_ID FROM AD_User WHERE C_BPartner_ID=?) " + "ORDER BY DocumentNo DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequest( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Own=" + own,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "Own=" + own + " #" + list.size());

        return list;
    }    // getRequests

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequest getRequest() {
        MRequest retValue = null;
        String   sql      = "SELECT * FROM R_Request " + "WHERE R_Request_ID=?" + " AND (C_BPartner_ID=?" + " OR SalesRep_ID IN (SELECT AD_User_ID FROM AD_User WHERE C_BPartner_ID=?))";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_id );
            pstmt.setInt( 2,getC_BPartner_ID());
            pstmt.setInt( 3,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MRequest( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getRequest

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getRequestTypes() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM R_RequestType " + "WHERE IsSelfService='Y' AND AD_Client_ID=? ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_Client_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequestType( m_ctx,rs,null ));
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

        log.fine( "#" + list.size());

        return list;
    }    // getRequestTypes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequestType getRequestType() {
        m_infoMessage = null;

        MRequestType retValue = null;
        String       sql      = "SELECT * FROM R_RequestType WHERE IsSelfService='Y' AND R_RequestType_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MRequestType( m_ctx,rs,null );
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

        log.fine( "ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getRequestType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getInvoices() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Invoice WHERE C_BPartner_ID=?" + " AND DocStatus NOT IN ('DR','IN') " + "ORDER BY DocumentNo DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInvoice( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getInvoices",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getInvoices #" + list.size());

        return list;
    }    // getInvoices

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInvoice getInvoice() {
        MInvoice retValue = null;
        String   sql      = "SELECT * FROM C_Invoice WHERE C_BPartner_ID=? AND C_Invoice_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());
            pstmt.setInt( 2,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MInvoice( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getInvoice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getPayments() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Payment WHERE C_BPartner_ID=?" + " AND DocStatus NOT IN ('DR','IN') " + "ORDER BY DocumentNo DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPayment( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPayments",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getPayments #" + list.size());

        return list;
    }    // getPayments

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getAssets() {
        ArrayList list = new ArrayList();

        if( m_wu != null ) {
            if( m_wu.isCreditStopHold()) {
                return list;
            }

            if( !m_wu.isEMailVerified()) {
                return list;
            }
        }

        String sql = "SELECT * FROM A_Asset WHERE C_BPartner_ID=? AND IsActive='Y' ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAsset( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAssets",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getAssets #" + list.size());

        return list;
    }    // getAssets

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getInterests() {
        int AD_Client_ID = Env.getAD_Client_ID( m_ctx );

        //

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM R_InterestArea " + "WHERE IsActive='Y' AND IsSelfService='Y'" + " AND AD_Client_ID=? " + "ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MInterestArea ia = new MInterestArea( m_ctx,rs,null );

                ia.setSubscriptionInfo( getAD_User_ID());
                list.add( ia );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getInterests",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getInterests #" + list.size());

        return list;
    }    // getInterests

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getAdvertisements() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM W_Advertisement WHERE C_BPartner_ID=? ORDER BY ValidFrom DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAdvertisement( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAdvertisement",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getAdvertisement #" + list.size());

        return list;
    }    // getAdvertisement

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getAllAds() {
        m_infoMessage = null;

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM W_Advertisement WHERE IsActive='Y' ORDER BY Description";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAdvertisement( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAllAds",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getAllAds #" + list.size());

        return list;
    }    // getAllAds

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getCommissionedInvoices() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Invoice " + "WHERE (C_Invoice.SalesRep_ID=?"    // #1
                         + " OR EXISTS (SELECT * FROM C_BPartner bp WHERE C_Invoice.C_BPartner_ID=bp.C_BPartner_ID AND bp.SalesRep_ID=?)" + " OR EXISTS (SELECT * FROM C_InvoiceLine il INNER JOIN M_Product p ON (il.M_Product_ID=p.M_Product_ID)  WHERE C_Invoice.C_Invoice_ID=il.C_Invoice_ID AND p.SalesRep_ID=?))" + " AND DocStatus NOT IN ('DR','IN') " + "ORDER BY DocumentNo DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_User_ID());
            pstmt.setInt( 2,getAD_User_ID());
            pstmt.setInt( 3,getAD_User_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MInvoice( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getCommissionedInvoices",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getCommissionedInvoices #" + list.size());

        return list;
    }    // getCommissionedInvoices

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getCommissionRuns() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_CommissionRun " + "WHERE EXISTS (SELECT * FROM C_Commission c " + "WHERE C_CommissionRun.C_Commission_ID=c.C_Commission_ID AND c.C_BPartner_ID=?) " + "ORDER BY DocumentNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MCommissionRun( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getCommissionRuns",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getCommissionRuns #" + list.size());

        return list;
    }    // getCommissionRuns

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getNotes() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_Note " + "WHERE AD_User_ID=?" + " AND (Processed='N' OR Processed IS NULL) " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_User_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MNote( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getNotes",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getNotes #" + list.size());

        return list;
    }    // getNotes

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MNote getNote() {
        MNote  retValue = null;
        String sql      = "SELECT * FROM AD_Note WHERE AD_User_ID=? AND AD_Note_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_User_ID());
            pstmt.setInt( 2,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MNote( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getNote ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getNote ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getNote

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getActivities() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_WF_Activity " + "WHERE AD_User_ID=?" + " AND Processed='N' " + "ORDER BY Priority DESC, Created";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_User_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWFActivity( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getActivities",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getActivities #" + list.size());

        return list;
    }    // getActivities

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MWFActivity getActivity() {
        MWFActivity retValue = null;
        String      sql      = "SELECT * FROM AD_WF_Activity WHERE AD_User_ID=? AND AD_WF_Activity_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_User_ID());
            pstmt.setInt( 2,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MWFActivity( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getActivity ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getActivity ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getActivity

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getExpenses() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM S_TimeExpense " + "WHERE C_BPartner_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MTimeExpense( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getExpenses",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getExpenses #" + list.size());

        return list;
    }    // getExpenses

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTimeExpense getExpense() {
        MTimeExpense retValue = null;
        String       sql      = "SELECT * FROM S_TimeExpense WHERE C_BPartner_ID=? AND S_TimeExpense_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());
            pstmt.setInt( 2,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MTimeExpense( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getExpense ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        if( retValue == null ) {
            retValue = new MTimeExpense( m_ctx,0,null );
        }

        log.fine( "getExpense ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getExpense

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getRegistrations() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM A_Registration " + "WHERE AD_Client_ID=? " + "ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_Client_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRegistration( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRegistrations",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getRegistrations #" + list.size());

        return list;
    }    // getRegistrations

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRegistration getRegistration() {
        MRegistration retValue = null;
        String        sql      = "SELECT * FROM A_Registration WHERE AD_Client_ID=? AND A_Registration_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getAD_Client_ID());
            pstmt.setInt( 2,m_id );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MRegistration( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRegistration ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        // new registration

        if( retValue == null ) {
            retValue = new MRegistration( m_ctx,0,null );
        }

        log.fine( "getRegistration ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getRegistration

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ArrayList getRfQs() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * " + "FROM C_RfQ r " + "WHERE r.IsRfQResponseAccepted='Y'" + " AND r.IsSelfService='Y' AND r.IsActive='Y' AND r.Processed='N'" + " AND (r.IsInvitedVendorsOnly='N'" + " OR EXISTS (SELECT * FROM C_RfQResponse rr " + " WHERE r.C_RfQ_ID=rr.C_RfQ_ID AND rr.C_BPartner_ID=?)) " + "ORDER BY r.Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRfQ( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRfQs",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        log.fine( "getRfQs #" + list.size());

        return list;
    }    // getRfQs

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRfQResponse getRfQResponse() {
        MRfQResponse retValue = null;
        String       sql      = "SELECT * FROM C_RfQResponse " + "WHERE C_RfQ_ID=?" + " AND C_BPartner_ID=? AND IsActive='Y'";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_id );
            pstmt.setInt( 2,getC_BPartner_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MRfQResponse( m_ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getRfQ ID=" + m_id,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        // No Response existing

        if( retValue == null ) {
            MRfQ rfq = new MRfQ( m_ctx,m_id,null );

            // We can create a Response ?

            if( (rfq.getID() != 0) && rfq.isSelfService() && rfq.isRfQResponseAccepted() &&!rfq.isInvitedVendorsOnly() && (getC_BPartner_ID() > 0) && (getAD_User_ID() > 0) ) {
                MBPartner bp = new MBPartner( m_ctx,getC_BPartner_ID(),null );

                bp.setPrimaryAD_User_ID( getAD_User_ID());
                retValue = new MRfQResponse( rfq,bp );    // may have no lines
                retValue.save();
            }
        }

        //

        log.fine( "getRfQResponse ID=" + m_id + " - " + retValue );

        return retValue;
    }    // getRfQResponse

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public MProduct getProducto()
    {
       MProduct retValue = null;
       String sql = "SELECT * FROM M_Product WHERE M_Product_ID=?";
       PreparedStatement pstmt = null;
       try
       {
          pstmt = DB.prepareStatement(sql);
          pstmt.setInt(1, m_id);
          ResultSet rs = pstmt.executeQuery();
          if (rs.next())
             retValue = new MProduct (m_ctx, rs, null);
          rs.close();
          pstmt.close();
          pstmt = null;
       }
       catch (Exception e)
       {
          log.saveError("getProducto ID=" + m_id, e);
       }
       finally
       {
          try
          {
             if (pstmt != null)
                pstmt.close ();
          }
          catch (Exception e)
          {}
          pstmt = null;
       }
       log.fine ("getProducto ID=" + m_id + " - " + retValue);
       return retValue;
    }   //   getProducto   

    /**
     * Descripción de Método
     *
     *
     * @return
     */    
    
	public ArrayList getVencimientos()
	{
		ArrayList list = new ArrayList();
		String formaPago = m_ctx.getProperty(InvoicesServlet.P_FORMA_PAGO);
		String fechaMinima = m_ctx.getProperty(InvoicesServlet.P_FECHA_MINIMA);
		String fechaMaxima = m_ctx.getProperty(InvoicesServlet.P_FECHA_MAXIMA);		    

		String sql = "SELECT * FROM C_InvoicePaySchedule " +
				"WHERE (C_INVOICE_ID IN " +
				"(SELECT C_Invoice_ID FROM C_Invoice " +
				"WHERE C_BPARTNER_ID=?";
				if (formaPago != null && formaPago.equals("-2") != true){
					sql += " AND C_PaymentTerm_ID=";
					sql += formaPago;					
				}  
				else if (formaPago != null && formaPago.equals("-2") == true){ 
					sql += " AND C_PaymentTerm_ID IN (SELECT C_PaymentTerm_ID"
						+ " FROM C_PaymentTerm)";
				}					
				sql += ")) ";	
			    if (fechaMinima != null){
			    	sql += " AND DueDate >= TO_DATE('";
			    	sql += fechaMinima;
			    	sql += "','yyyy-MM-dd') ";
			    }
			    else
			    {
			    	sql += " AND DueDate >= SYSDATE - 60 ";
			    }
			    
				if (fechaMaxima != null){
					sql += " AND DueDate <= TO_DATE('";
					sql += fechaMaxima;
					sql += "','yyyy-MM-dd') ";
				}
				else
				{ 
			    	sql += " AND DueDate <= SYSDATE + 91 ";
				} 
			    sql += "ORDER BY DueDate";
			    
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1, getC_BPartner_ID());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new MInvoicePaySchedule (m_ctx, rs, null));
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			//log.error("getVencimientos", e);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close ();
			}
			catch (Exception e)
			{}
			pstmt = null;
		}
		//log.debug ("getVencimientos #" + list.size());
		return list;
	}
	
	public MInOut getEnvio()
	{
		MInOut retValue = null;
		String sql = "SELECT * FROM M_InOut WHERE C_BPartner_ID=? AND M_InOut_ID=?"
			+ " AND DocStatus NOT IN ('DR','IN') ";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			pstmt.setInt(1, getC_BPartner_ID());
			pstmt.setInt(2, m_id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				retValue = new MInOut (m_ctx, rs,null);
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{
			log.saveError("getEnvio ID=" + m_id, e);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close ();
			}
			catch (Exception e)
			{}
			pstmt = null;
		}
		//log.debug ("getEnvio ID=" + m_id + " - " + retValue);
		return retValue;
	}	//	getEnvio
	
	
}    // Info



/*
 *  @(#)Info.java   12.10.07
 * 
 *  Fin del fichero Info.java
 *  
 *  Versión 2.2
 *
 */
