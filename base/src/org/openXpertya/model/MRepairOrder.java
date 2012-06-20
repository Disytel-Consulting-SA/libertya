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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRepairOrder extends X_C_Repair_Order implements DocAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Order_ID
     * @param trxName
     */

    public MRepairOrder( Properties ctx,int C_Repair_Order_ID,String trxName ) {
        super( ctx,C_Repair_Order_ID,trxName );

        // New

        if( C_Repair_Order_ID == 0 ) {
            setDocStatus( DOCSTATUS_Drafted );
            setDocAction( DOCACTION_Prepare );

            //

            setInvoiceRule( INVOICERULE_Immediate );
            setPaymentRule( PAYMENTRULE_OnCredit );
            setPriorityRule( PRIORITYRULE_Medium );

            //

            setIsDiscountPrinted( false );
            setIsTaxIncluded( false );
            setSendEMail( false );

            //

            setIsApproved( false );
            setIsPrinted( false );
            setIsCreditApproved( false );
            setIsInvoiced( false );
            setIsSelfService( false );

            //

            super.setProcessed( false );
            setProcessing( false );
            setDatePromised( new Timestamp( System.currentTimeMillis()));
            setDateOrdered( new Timestamp( System.currentTimeMillis()));
            setTotalLines( Env.ZERO );
            setGrandTotal( Env.ZERO );
        }
    }    // MRepairOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     * @param IsSOTrx
     * @param DocSubTypeSO
     */

    public MRepairOrder( MProject project,boolean IsSOTrx,String DocSubTypeSO ) {
        this( project.getCtx(),0,project.get_TrxName());
        setAD_Client_ID( project.getAD_Client_ID());
        setAD_Org_ID( project.getAD_Org_ID());
        setC_Campaign_ID( project.getC_Campaign_ID());
        setSalesRep_ID( project.getSalesRep_ID());

        //

        setC_Project_ID( project.getC_Project_ID());
        setDescription( project.getName());

        Timestamp ts = project.getDateContract();

        if( ts != null ) {
            setDateOrdered( ts );
        }

        ts = project.getDateFinish();

        if( ts != null ) {
            setDatePromised( ts );
        }

        //

        setC_BPartner_ID( project.getC_BPartner_ID());
        setC_BPartner_Location_ID( project.getC_BPartner_Location_ID());
        setAD_User_ID( project.getAD_User_ID());

        //

        setM_Warehouse_ID( project.getM_Warehouse_ID());
        setM_PriceList_ID( project.getM_PriceList_ID());
        setC_PaymentTerm_ID( project.getC_PaymentTerm_ID());

        //

        if( IsSOTrx ) {
            if( (DocSubTypeSO == null) || (DocSubTypeSO.length() == 0) ) {
                setC_DocTypeTarget_ID( DocSubTypeSO_OnCredit );
            } else {
                setC_DocTypeTarget_ID( DocSubTypeSO );
            }
        } else {
            setC_DocTypeTarget_ID();
        }
    }    // MRepairOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRepairOrder( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MOrder 

    /** Descripción de Campos */

    private MRepairOrderLine[] m_lines = null;
    
    /** Descripción de Campos */

    private boolean m_forceCreation = false;

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
     * @param AD_Org_ID
     */

    public void setAD_Org_ID( int AD_Org_ID ) {
        super.setAD_Org_ID( AD_Org_ID );
    }    // setAD_Org_ID

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    public void setC_BPartner_ID( int C_BPartner_ID ) {
        super.setC_BPartner_ID( C_BPartner_ID );
        super.setBill_BPartner_ID( C_BPartner_ID );
    }    // setC_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     */

    public void setC_BPartner_Location_ID( int C_BPartner_Location_ID ) {
        super.setC_BPartner_Location_ID( C_BPartner_Location_ID );
        super.setBill_Location_ID( C_BPartner_Location_ID );
    }    // setC_BPartner_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setAD_User_ID( int AD_User_ID ) {
        super.setAD_User_ID( AD_User_ID );
        super.setBill_User_ID( AD_User_ID );
    }    // setAD_User_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    public void setShip_BPartner_ID( int C_BPartner_ID ) {
        super.setC_BPartner_ID( C_BPartner_ID );
    }    // setShip_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_Location_ID
     */

    public void setShip_Location_ID( int C_BPartner_Location_ID ) {
        super.setC_BPartner_Location_ID( C_BPartner_Location_ID );
    }    // setShip_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setShip_User_ID( int AD_User_ID ) {
        super.setAD_User_ID( AD_User_ID );
    }    // setShip_User_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     */

    public void setM_Warehouse_ID( int M_Warehouse_ID ) {
        super.setM_Warehouse_ID( M_Warehouse_ID );
    }    // setM_Warehouse_ID

    
    /**
     * Descripción de Método
     *
     *
     * @param IsDropShip
     */
    /*
    public void setIsDropShip( boolean IsDropShip ) {
        super.setIsDropShip( IsDropShip );
    }    // setIsDropShip
    */

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Standard = "SO";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Quotation = "OB";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Proposal = "ON";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Prepay = "PR";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_POS = "WR";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_Warehouse = "WP";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_OnCredit = "WI";

    /** Descripción de Campos */

    public static final String DocSubTypeSO_RMA = "RM";

    /**
     * Descripción de Método
     *
     *
     * @param DocSubTypeSO_x
     */

    public void setC_DocTypeTarget_ID( String DocSubTypeSO_x ) {
        String sql = "SELECT C_DocType_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND AD_Org_ID IN (0," + getAD_Org_ID() + ") AND DocSubTypeSO=? " + "ORDER BY AD_Org_ID DESC, IsDefault DESC";
        int C_DocType_ID = DB.getSQLValue( null,sql,getAD_Client_ID(),DocSubTypeSO_x );

        if( C_DocType_ID <= 0 ) {
            log.severe( "Not found for AD_Client_ID=" + getAD_Client_ID() + ", SubType=" + DocSubTypeSO_x );
        } else {
            log.fine( "(SO) - " + DocSubTypeSO_x );
            setC_DocTypeTarget_ID( C_DocType_ID );
        }
    }    // setC_DocTypeTarget_ID

    /**
     * Descripción de Método
     *
     */

    public void setC_DocTypeTarget_ID() {

        // PO

        String sql = "SELECT C_DocType_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND AD_Org_ID IN (0," + getAD_Org_ID() + ") AND DocBaseType='POO' " + "ORDER BY AD_Org_ID DESC, IsDefault DESC";
        int C_DocType_ID = DB.getSQLValue( null,sql,getAD_Client_ID());

        if( C_DocType_ID <= 0 ) {
            log.severe( "No POO found for AD_Client_ID=" + getAD_Client_ID());
        } else {
            log.fine( "(PO) - " + C_DocType_ID );
            setC_DocTypeTarget_ID( C_DocType_ID );
        }
    }    // setC_DocTypeTarget_ID

    /**
     * Descripción de Método
     *
     *
     * @param bp
     */

    public void setBPartner( MBPartner bp ) {
        if( bp == null ) {
            return;
        }

        setC_BPartner_ID( bp.getC_BPartner_ID());

        // Defaults Payment Term

        int ii = 0;

        ii = bp.getPO_PaymentTerm_ID();

        if( ii != 0 ) {
            setC_PaymentTerm_ID( ii );
        }

        // Default Price List

        ii = bp.getPO_PriceList_ID();

        if( ii != 0 ) {
            setM_PriceList_ID( ii );
        }

        // Default Invoice/Payment Rule

        String ss = bp.getInvoiceRule();

        if( ss != null ) {
            setInvoiceRule( ss );
        }

        ss = bp.getPaymentRule();

        if( ss != null ) {
            setPaymentRule( ss );
        }

        // Sales Rep

        ii = bp.getSalesRep_ID();

        if( ii != 0 ) {
            setSalesRep_ID( ii );
        }

        // Set Locations

        MBPartnerLocation[] locs = bp.getLocations( false );

        if( locs != null ) {
            for( int i = 0;i < locs.length;i++ ) {
                if( locs[ i ].isShipTo()) {
                    super.setC_BPartner_Location_ID( locs[ i ].getC_BPartner_Location_ID());
                }

                if( locs[ i ].isBillTo()) {
                    setBill_Location_ID( locs[ i ].getC_BPartner_Location_ID());
                }
            }

            // set to first

            if( (getC_BPartner_Location_ID() == 0) && (locs.length > 0) ) {
                super.setC_BPartner_Location_ID( locs[ 0 ].getC_BPartner_Location_ID());
            }

            if( (getBill_Location_ID() == 0) && (locs.length > 0) ) {
                setBill_Location_ID( locs[ 0 ].getC_BPartner_Location_ID());
            }
        }

        if( getC_BPartner_Location_ID() == 0 ) {
            log.log( Level.SEVERE,"MOrder.setBPartner - Has no Ship To Address: " + bp );
        }

        if( getBill_Location_ID() == 0 ) {
            log.log( Level.SEVERE,"MOrder.setBPartner - Has no Bill To Address: " + bp );
        }

        // Set Contact

        MUser[] contacts = bp.getContacts( false );

        if( (contacts != null) && (contacts.length == 1) ) {
            setAD_User_ID( contacts[ 0 ].getAD_User_ID());
        }
    }    // setBPartner
    
    // TODO: copiar también los productos

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MRepairOrder[" ).append( getID()).append( "-" ).append( getDocumentNo()).append( ",C_DocType_ID=" ).append( getC_DocType_ID()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     */

    public void setM_PriceList_ID( int M_PriceList_ID ) {
        MPriceList pl = MPriceList.get( getCtx(),M_PriceList_ID,null );

        if( pl.getID() == M_PriceList_ID ) {
            super.setM_PriceList_ID( M_PriceList_ID );
            setC_Currency_ID( pl.getC_Currency_ID());
            setIsTaxIncluded( pl.isTaxIncluded());
        }
    }    // setM_PriceList_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrencyISO() {
        return MCurrency.getISO_Code( getCtx(),getC_Currency_ID());
    }    // getCurrencyISO

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPrecision() {
        return MCurrency.getStdPrecision( getCtx(),getC_Currency_ID());
    }    // getPrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocStatusName() {
        return MRefList.getListName( getCtx(),131,getDocStatus());
    }    // getDocStatusName

    /**
     * Descripción de Método
     *
     *
     * @param DocAction
     */

    public void setDocAction( String DocAction ) {
        setDocAction( DocAction,false );
    }    // setDocAction

    /**
     * Descripción de Método
     *
     *
     * @param DocAction
     * @param forceCreation
     */

    public void setDocAction( String DocAction,boolean forceCreation ) {
        super.setDocAction( DocAction );
        m_forceCreation = forceCreation;
    }    // setDocAction

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        /*
        if( getID() == 0 ) {
            return;
        }

        String set = "SET Processed='" + ( processed
                                           ?"Y"
                                           :"N" ) + "' WHERE C_Repair_Order_ID=" + getC_Repair_Order_ID();
        int noLine = DB.executeUpdate( "UPDATE C_Repair_Order_Line " + set,get_TrxName());

        m_lines = null;
        m_taxes = null;
        
        log.fine( "setProcessed - " + processed + " - Lines=" + noLine + ", Tax=" + noTax );
        */
    }    // setProcessed

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

    	/*
    	// Client/Org Check

        if( getAD_Org_ID() == 0 ) {
            int context_AD_Org_ID = Env.getAD_Org_ID( getCtx());

            if( context_AD_Org_ID != 0 ) {
                setAD_Org_ID( context_AD_Org_ID );
                log.warning( "Changed Org to Context=" + context_AD_Org_ID );
            }
        }

        if( getAD_Client_ID() == 0 ) {
            m_processMsg = "AD_Client_ID = 0";

            return false;
        }

        // New Record Doc Type - make sure DocType set to 0

        if( newRecord && (getC_DocType_ID() == 0) ) {
            setC_DocType_ID( 0 );
        }

        // Default Warehouse

        if( getM_Warehouse_ID() == 0 ) {
            int ii = Env.getContextAsInt( getCtx(),"#M_Warehouse_ID" );

            if( ii != 0 ) {
                setM_Warehouse_ID( ii );
            } else {
                log.severe( "No Warehouse" );

                return false;
            }
        }

        // Warehouse Org

        if( newRecord || is_ValueChanged( "AD_Org_ID" ) || is_ValueChanged( "M_Warehouse_ID" )) {
            MWarehouse wh = MWarehouse.get( getCtx(),getM_Warehouse_ID());

            if( wh.getAD_Org_ID() != getAD_Org_ID()) {
                log.saveError( "WarehouseOrgConflict","" );

                return false;
            }
        }

        // Reservations in Warehouse

        if( !newRecord && is_ValueChanged( "M_Warehouse_ID" )) {
            MRepairOrderLine[] lines = getLines( false,null );

            for( int i = 0;i < lines.length;i++ ) {
                if( !lines[ i ].canChangeWarehouse()) {
                    return false;
                }
            }
        }

        // No Partner Info - set Template

        if( getC_BPartner_ID() == 0 ) {
            setBPartner( MBPartner.getTemplate( getCtx(),getAD_Client_ID()));
        }

        if( getC_BPartner_Location_ID() == 0 ) {
            setBPartner( new MBPartner( getCtx(),getC_BPartner_ID(),null ));
        }

        // No Bill - get from Ship

        if( getBill_BPartner_ID() == 0 ) {
            setBill_BPartner_ID( getC_BPartner_ID());
            setBill_Location_ID( getC_BPartner_Location_ID());
        }

        if( getBill_Location_ID() == 0 ) {
            setBill_Location_ID( getC_BPartner_Location_ID());
        }

        // Default Price List

        if( getM_PriceList_ID() == 0 ) {
            int ii = DB.getSQLValue( null,"SELECT M_PriceList_ID FROM M_PriceList " + "WHERE AD_Client_ID=? AND IsSOPriceList=? " + "ORDER BY IsDefault DESC",getAD_Client_ID(),isSOTrx()
                    ?"Y"
                    :"N" );

            if( ii != 0 ) {
                setM_PriceList_ID( ii );
            }
        }

        // Default Currency

        if( getC_Currency_ID() == 0 ) {
            String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
            int ii = DB.getSQLValue( null,sql,getM_PriceList_ID());

            if( ii != 0 ) {
                setC_Currency_ID( ii );
            } else {
                setC_Currency_ID( Env.getContextAsInt( getCtx(),"#C_Currency_ID" ));
            }
        }

        // Default Sales Rep

        if( getSalesRep_ID() == 0 ) {
            int ii = Env.getContextAsInt( getCtx(),"#SalesRep_ID" );

            if( ii != 0 ) {
                setSalesRep_ID( ii );
            }
        }

        // Default Document Type

        if( getC_DocTypeTarget_ID() == 0 ) {
            setC_DocTypeTarget_ID( DocSubTypeSO_Standard );
        }

        // Default Payment Term

        if( getC_PaymentTerm_ID() == 0 ) {
            int ii = Env.getContextAsInt( getCtx(),"#C_PaymentTerm_ID" );

            if( ii != 0 ) {
                setC_PaymentTerm_ID( ii );
            } else {
                String sql = "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y'";

                ii = DB.getSQLValue( null,sql,getAD_Client_ID());

                if( ii != 0 ) {
                    setC_PaymentTerm_ID( ii );
                }
            }
        }
        */

        return true;
    }    // beforeSave

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
        if( !success || newRecord ) {
            return success;
        }

        /*
        // Propagate Description changes
        if( is_ValueChanged( "Description" ) || is_ValueChanged( "POReference" )) {
        	// begin DMA - Dataware - BugNo: 242
        	String sql;
        	
        	if(DB.isPostgreSQL()) {
	            sql = "UPDATE C_Invoice " +
	            			 "SET Description=o.Description, POReference=o.POReference " + 
	            			 "FROM C_Repair_Order o " +
	            			 "WHERE C_Invoice.C_Order_ID=o.C_Order_ID " +
	            			 "AND C_Invoice.DocStatus NOT IN ('RE','CL') " +
	            			 "AND C_Invoice.C_Order_ID=" + getC_Order_ID();
        	} else {
        		sql = "UPDATE C_Invoice i" + 
        			  " SET (Description,POReference)=" + 
        			  "(SELECT Description,POReference " + 
        			  "FROM C_Order o WHERE i.C_Order_ID=o.C_Order_ID) " + 
        			  "WHERE DocStatus NOT IN ('RE','CL') AND C_Order_ID=" + getC_Order_ID();
        	}
            // end DMA - Dataware - BugNo: 242
            
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Description -> #" + no );
        }
        */
        
        /*
        // Propagate Changes of Payment Info to existing (not reversed/closed) invoices
        if( is_ValueChanged( "PaymentRule" ) || is_ValueChanged( "C_PaymentTerm_ID" ) || is_ValueChanged( "DateAcct" ) || is_ValueChanged( "C_Payment_ID" ) || is_ValueChanged( "C_CashLine_ID" )) {
            // begin DMA - Dataware - BugNo: 242
        	String sql;
        	
        	if(DB.isPostgreSQL()) {
	            sql = "UPDATE C_Invoice " +
	            			 "SET PaymentRule=o.PaymentRule, C_PaymentTerm_ID=o.C_PaymentTerm_ID, DateAcct=o.DateAcct, " +
	            			 "    C_Payment_ID=o.C_Payment_ID, C_CashLine_ID=o.C_CashLine_ID " +
	            			 "FROM C_Order o " + 
	            			 "WHERE C_Invoice.C_Order_ID=o.C_Order_ID " +
	            			 "AND C_Invoice.DocStatus NOT IN ('RE','CL') " +
	            			 "AND C_Invoice.C_Order_ID=" + getC_Order_ID();
        	} else {
        		sql = "UPDATE C_Invoice i " + 
        			  "SET (PaymentRule,C_PaymentTerm_ID,DateAcct,C_Payment_ID,C_CashLine_ID)=" + 
        			  "(SELECT PaymentRule,C_PaymentTerm_ID,DateAcct,C_Payment_ID,C_CashLine_ID " + 
        			  "FROM C_Order o WHERE i.C_Order_ID=o.C_Order_ID)" + 
        			  "WHERE DocStatus NOT IN ('RE','CL') AND C_Order_ID=" + getC_Order_ID();	        	
        	}
            // end DMA - Dataware - BugNo: 242

            // Don't touch Closed/Reversed entries

            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Payment -> #" + no );
        }
        */

        // Sync Lines

        afterSaveSync( "AD_Org_ID" );
        afterSaveSync( "C_BPartner_ID" );
        afterSaveSync( "C_BPartner_Location_ID" );
        afterSaveSync( "DateOrdered" );
        afterSaveSync( "DatePromised" );
        afterSaveSync( "M_Warehouse_ID" );
        afterSaveSync( "C_Currency_ID" );

        // begin vpj-cd e-evolution 01/25/2005 CMPCS

        //afterSaveSync( "DocStatus" );

        // end vpj-cd e-evolution 01/25/2005 CMPCS

        return true;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     */

    private void afterSaveSync( String columnName ) {
    	log.fine(" En afterSaveSync con columName = " + columnName);

        // begin vpj-cd e-evolution 01/25/2005 CMPCS
    	/*
        if( is_ValueChanged( "DocStatus" ) || is_ValueChanged( "DatePromised" ) || is_ValueChanged( "M_Warehouse_ID" )) {
            if( (columnName.compareTo( "DocStatus" ) == 0) || (columnName.compareTo( "DatePromised" ) == 0) || (columnName.compareTo( "M_Warehouse_ID" ) == 0) ) {
                // begin DMA - Dataware - BugNo: 242
            	String sql;
            	
            	if(DB.isPostgreSQL()) {
            		sql = "UPDATE MPC_MRP " + 
                			 "SET " + columnName + "=o." + columnName + " " +
            				 "FROM C_Order o " +
            				 "WHERE MPC_MRP.C_Order_ID=o.C_Order_ID " +
            				 "AND MPC_MRP.C_Order_ID=" + getC_Order_ID();
            	} else {
            		sql = " UPDATE MPC_MRP m " + 
            			  "SET " + columnName + " =" + 
            			  " (SELECT " + columnName + " FROM C_Order o WHERE m.C_Order_ID=o.C_Order_ID) " + 
            			  " WHERE m.C_Order_ID = " + getC_Order_ID();            		
            	}
                // end DMA - Dataware - BugNo: 242
                int no = DB.executeUpdate( sql,get_TrxName());

                log.fine( columnName + " MPC_MRP set DocStatus --------di---" + no +"   "+ sql);
            }

            if( columnName.compareTo( "DocStatus" ) == 0 ) {
            	log.fine("Retornado en aftersavesync");
                return;
            }
        }
        */

        // end vpj-cd e-evolution 01/25/2005 CMPCS

        if( is_ValueChanged( columnName )) {
        	// begin DMA - Dataware - BugNo: 242
        	String sql;
        	
        	if(DB.isPostgreSQL()) {
	        	sql = "UPDATE C_Repair_Order_Line " +
	        				 "SET " + columnName + "=o." + columnName + " " +
	        				 "FROM C_Repair_Order o " +
	        				 "WHERE C_Repair_Order_Line.C_Repair_Order_ID=o.C_Repair_Order_ID " +
	        				 "AND C_Repair_Order_Line.C_Repair_Order_ID=" + getC_Repair_Order_ID();
        	} else {
        		sql = "UPDATE C_Repair_Order_Line ol" + 
        			" SET " + columnName + " =" + 
        			"(SELECT " + columnName + " FROM C_Repair_Order o WHERE ol.C_Repair_Order_ID=o.C_Repair_Order_ID) " + 
        			"WHERE C_Repair_Order_ID=" + getC_Repair_Order_ID();	
        	}
        	
        	// end DMA - Dataware - BugNo: 242
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( columnName + " Lines -> #" + no );
        }
    }    // afterSaveSync

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        if( isProcessed()) {
            return false;
        }
        /*

        getLines();

        for( int i = 0;i < m_lines.length;i++ ) {
            if( !m_lines[ i ].beforeDelete()) {
                return false;
            }
        }
        */

        return true;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     *
     * @return
     */

    public boolean processIt( String processAction ) {
        m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        return engine.processIt( processAction,getDocAction(),log );
    }    // processIt


    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( "unlockIt - " + toString());
        setProcessing( false );

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        log.info( toString());
        setDocAction( DOCACTION_Prepare );

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
    	/*
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }

        MDocType dt = MDocType.get( getCtx(),getC_DocTypeTarget_ID());

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),dt.getDocBaseType())) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        // Lines

        MOrderLine[] lines = getLines( true,"M_Product_ID" );

        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";

            return DocAction.STATUS_Invalid;
        }

        // Convert DocType to Target

        if( getC_DocType_ID() != getC_DocTypeTarget_ID()) {

            // Cannot change Std to anything else if different warehouses

            if( getC_DocType_ID() != 0 ) {
                MDocType dtOld = MDocType.get( getCtx(),getC_DocType_ID());

                if( MDocType.DOCSUBTYPESO_StandardOrder.equals( dtOld.getDocSubTypeSO())    // From SO
                        &&!MDocType.DOCSUBTYPESO_StandardOrder.equals( dt.getDocSubTypeSO()))    // To !SO
                        {
                    for( int i = 0;i < lines.length;i++ ) {
                        if( lines[ i ].getM_Warehouse_ID() != getM_Warehouse_ID()) {
                            log.warning( "different Warehouse " + lines[ i ] );
                            m_processMsg = "@CannotChangeDocType@";

                            return DocAction.STATUS_Invalid;
                        }
                    }
                }
            }

            // New or in Progress/Invalid

            if( DOCSTATUS_Drafted.equals( getDocStatus()) || DOCSTATUS_InProgress.equals( getDocStatus()) || DOCSTATUS_Invalid.equals( getDocStatus()) || (getC_DocType_ID() == 0) ) {
                setC_DocType_ID( getC_DocTypeTarget_ID());
            } else    // convert only if offer
            {
                if( dt.isOffer()) {
                    setC_DocType_ID( getC_DocTypeTarget_ID());
                } else {
                    m_processMsg = "@CannotChangeDocType@";

                    return DocAction.STATUS_Invalid;
                }
            }
        }             // convert DocType

        // Mandatory Product Attribute Set Instance

        String mandatoryType = "='Y'";    // IN ('Y','S')
        String sql           = "SELECT COUNT(*) " + "FROM C_OrderLine ol" + " INNER JOIN M_Product p ON (ol.M_Product_ID=p.M_Product_ID)" + " INNER JOIN M_AttributeSet pas ON (p.M_AttributeSet_ID=pas.M_AttributeSet_ID) " + "WHERE pas.MandatoryType" + mandatoryType + " AND ol.M_AttributeSetInstance_ID IS NULL" + " AND ol.C_Order_ID=?";
        int no = DB.getSQLValue( get_TrxName(),sql,getC_Order_ID());

        if( no != 0 ) {
            m_processMsg = "@LinesWithoutProductAttribute@ (" + no + ")";

            return DocAction.STATUS_Invalid;
        }

        // Lines

        explodeBOM();

        if( !reserveStock( dt,lines )) {
            m_processMsg = "Cannot reserve Stock";

            return DocAction.STATUS_Invalid;
        }

        if( !calculateTaxTotal()) {
            m_processMsg = "Error calculating tax";

            return DocAction.STATUS_Invalid;
        }

        // Credit Check

        if( isSOTrx()) {
            MBPartner bp = new MBPartner( getCtx(),getC_BPartner_ID(),null );

            if( MBPartner.SOCREDITSTATUS_CreditStop.equals( bp.getSOCreditStatus())) {
                m_processMsg = "@BPartnerCreditStop@ - @TotalOpenBalance@=" + bp.getTotalOpenBalance() + ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();

                return DocAction.STATUS_Invalid;
            }

            if( MBPartner.SOCREDITSTATUS_CreditHold.equals( bp.getSOCreditStatus())) {
                m_processMsg = "@BPartnerCreditHold@ - @TotalOpenBalance@=" + bp.getTotalOpenBalance() + ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();

                return DocAction.STATUS_Invalid;
            }

            BigDecimal grandTotal = MConversionRate.convertBase( getCtx(),getGrandTotal(),getC_Currency_ID(),getDateOrdered(),getC_ConversionType_ID(),getAD_Client_ID(),getAD_Org_ID());

            if( MBPartner.SOCREDITSTATUS_CreditHold.equals( bp.getSOCreditStatus( grandTotal ))) {
                m_processMsg = "@BPartnerOverOCreditHold@ - @TotalOpenBalance@=" + bp.getTotalOpenBalance() + ", @GrandTotal@=" + grandTotal + ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();

                return DocAction.STATUS_Invalid;
            }
        }
        */

        m_justPrepared = true;

        // if (!DOCACTION_Complete.equals(getDocAction()))         don't set for just prepare
        // setDocAction(DOCACTION_Complete);

        return DocAction.STATUS_InProgress;
    }    // prepareIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( "approveIt - " + toString());
        setIsApproved( true );

        return true;
    }    // approveIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        log.info( "rejectIt - " + toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt()
    {
    	// completar: 
    	
    	if(canCompleteOrder()==false)
    	{
    		log.fine("completar orden de reparación: no se puede completar la orden de reparación");
    		return getDocStatus();	// no modificamos el estado actual
    	}
    	
    	// comprobamos si hay que generar pedido
    	if(haveToGenerateOrder(false)==true)
    	{
    		// TODO: Hay que arreglarlo para que el dialogo salga centrado
    		MRepairOrderTypeDialog dlg=new MRepairOrderTypeDialog(null, 0);
    		if(dlg.initDialog()==false)
    		{
    			log.severe("completando orden de reparación: no se ha podido inicializar el diálogo");
    			return getDocStatus();
    		}
    		dlg.setVisible(true);
    		int C_DocTypeTarget_ID=dlg.getC_DocType_ID();
    		if(C_DocTypeTarget_ID==-1)
    		{
    			log.info("completando orden de reparación: se ha cancelado el diálogo de selección de tipo de pedido");
    			return getDocStatus();
    		}
    		
    		boolean order_ok=generateOrder(C_DocTypeTarget_ID, false, true, 0, get_TrxName());
    		if(order_ok==false)
    		{
    			log.severe("completar orden de reparación: no se ha podido crear el pedido de reparacion asociado");
    			return getDocStatus();
    		}
    	}

        //

        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
    	/*
        MOrderLine[] lines = getLines( true,"M_Product_ID" );

        log.info( toString());

        for( int i = 0;i < lines.length;i++ ) {
            MOrderLine line = lines[ i ];
            BigDecimal old  = line.getQtyOrdered();

            if( old.compareTo( Env.ZERO ) != 0 ) {
                line.addDescription( Msg.getMsg( getCtx(),"Voided" ) + " (" + old + ")" );
                line.setQty( Env.ZERO );
                line.setLineNetAmt( Env.ZERO );
                line.save( get_TrxName());
            }
        }

        addDescription( Msg.getMsg( getCtx(),"Voided" ));

        // Clear Reservations

        if( !reserveStock( null,lines )) {
            m_processMsg = "Cannot unreserve Stock (void)";

            return false;
        }

        if( !createReversals()) {
            return false;
        }
        */

        setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
    }    // voidIt

    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
    	/*
        log.info( toString());

        // Close Not delivered Qty - SO/PO

        MOrderLine[] lines = getLines( true,"M_Product_ID" );

        for( int i = 0;i < lines.length;i++ ) {
            MOrderLine line = lines[ i ];
            BigDecimal old  = line.getQtyOrdered();

            if( old.compareTo( line.getQtyDelivered()) != 0 ) {

            	//Modificado por ConSerTi por el fallo de linea a 0 al cerrar un pedido
               log.fine("Estoy en MOrder y entro en el if, para poner el valor..:"+line.getQtyDelivered()+", old="+old);
            	//line.setQtyOrdered( line.getQtyDelivered());
            	line.setQtyOrdered(old);
//            	Fin modificacion
            	// QtyEntered unchanged	
                line.addDescription( "Close (" + old + ")" );
                line.save( get_TrxName());
            }
        }

        // Clear Reservations

        if( !reserveStock( null,lines )) {
            m_processMsg = "Cannot unreserve Stock (close)";

            return false;
        }
        */

        setProcessed( true );
        setDocAction( DOCACTION_None );

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( toString());

        return voidIt();
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
    	/*
        log.info( toString());

        MDocType dt           = MDocType.get( getCtx(),getC_DocType_ID());
        String   DocSubTypeSO = dt.getDocSubTypeSO();

        // PO - just re-open

        if( !isSOTrx()) {
            log.fine( "Existing documents not modified - " + dt );
        }

        // Reverse Direct Documents

        if( MDocType.DOCSUBTYPESO_OnCreditOrder.equals( DocSubTypeSO )    // (W)illCall(I)nvoice
                || MDocType.DOCSUBTYPESO_WarehouseOrder.equals( DocSubTypeSO )    // (W)illCall(P)ickup
                || MDocType.DOCSUBTYPESO_POSOrder.equals( DocSubTypeSO ))    // (W)alkIn(R)eceipt
                {
            if( !createReversals()) {
                return false;
            }
        } else {
            log.fine( "reActivateIt - Existing documents not modified - SubType=" + DocSubTypeSO );
        }
        */

        setDocAction( DOCACTION_Complete );
        setProcessed( false );

        return true;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getDocumentNo());

        // : Grand Total = 123.00 (#1)

        /*
        sb.append( ": " ).append( Msg.translate( getCtx(),"GrandTotal" )).append( "=" ).append( getGrandTotal());

        if( m_lines != null ) {
            sb.append( " (#" ).append( m_lines.length ).append( ")" );
        }
        */

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - " ).append( getDescription());
        }

        return sb.toString();
    }    // getSummary


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getSalesRep_ID();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        return getGrandTotal();
    }    // getApprovalAmt
    
    /**
     * Descripción de Método
     *
     *
     * @param otherOrder
     * @param counter
     * @param copyASI
     *
     * @return
     */

    public int copyLinesFrom( MRepairOrder otherRepairOrder,boolean counter,boolean copyASI ) {
        if( isProcessed() || (otherRepairOrder == null) ) {
            return 0;
        }

        MRepairOrderLine[] fromLines = otherRepairOrder.getLines( false,null );
        int          count     = 0;

        for( int i = 0;i < fromLines.length;i++ ) {
            MRepairOrderLine line = new MRepairOrderLine( this );

            PO.copyValues( fromLines[ i ],line,getAD_Client_ID(),getAD_Org_ID());
            line.setC_Repair_Order_ID( getC_Repair_Order_ID());
            line.setRepairOrder( this );
            line.setC_Repair_Order_Line_ID( 0 );    // new
            line.setC_Repair_Order_Product_ID(0);	//No se relacionan los productos

            // References

            if( !copyASI ) {
                line.setM_AttributeSetInstance_ID( 0 );
                line.setS_ResourceAssignment_ID( 0 );
            }

            // Tax

            if( getC_BPartner_ID() != otherRepairOrder.getC_BPartner_ID()) {
                line.setTax();    // recalculate
            }

            //
            //

            line.setProcessed( false );

            if( line.save( get_TrxName())) {
                count++;
            }

        }

        if( fromLines.length != count ) {
            log.log( Level.SEVERE,"Line difference - From=" + fromLines.length + " <> Saved=" + count );
        }

        return count;
    }    // copyLinesFrom
    
    /**
     * Descripción de Método
     *
     *
     * @param whereClause
     * @param orderClause
     *
     * @return
     */

    public MRepairOrderLine[] getLines( String whereClause,String orderClause ) {
    	log.fine("getLines 1");
        ArrayList    list = new ArrayList();
        StringBuffer sql  = new StringBuffer( "SELECT * FROM C_Repair_Order_Line WHERE C_Repair_Order_ID=? " );

        if( whereClause != null ) {
            sql.append( whereClause );
        }

        if( orderClause != null ) {
            sql.append( " " ).append( orderClause );
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,getC_Repair_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRepairOrderLine ol = new MRepairOrderLine( getCtx(),rs,get_TrxName());

                ol.setHeaderInfo( this );
                list.add( ol );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines - " + sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        MRepairOrderLine[] lines = new MRepairOrderLine[ list.size()];

        list.toArray( lines );

        return lines;
    }    // getLines
    
    /**
     * Descripción de Método
     *
     *
     * @param requery
     * @param orderBy
     *
     * @return
     */

    public MRepairOrderLine[] getLines( boolean requery,String orderBy ) {
    	log.fine("getLines 2");
        if( (m_lines != null) &&!requery ) {
            return m_lines;
        }

        //

        String orderClause = "ORDER BY ";

        if( (orderBy != null) && (orderBy.length() > 0) ) {
            orderClause += orderBy;
        } else {
            orderClause += "Line";
        }

        m_lines = getLines( null,orderClause );

        return m_lines;
    }    // getLines
    
    /***********
     * INDEOS
     * 
     * Métodos para realizar los procesos de las ordenes de reparacion
     */
    
    /**
     * 	Genera el pedido o presupuesto de reparación que se le va a pasar al cliente
     * 
     *	@param C_DocTypeTarget_ID	Tipo de pedido que se va a generar
     *	@param generarPresupuesto	Indica si estamos generando un presupuesto que se pasará al cliente
     *								o un pedido que se va a cobrar. Si se genera un presupuesto, se
     *								guardan todas las líneas de la orden en el presupuesto. Si se genera
     *								un pedido, dependerá de los valores de guardarTodas y AD_PInstance_ID
     *	@param guardarTodas	Indica si en el pedido que generemos se deben incluir todas
     *						las líneas de la orden de reparación. En caso de que no se cojan todas las
     *						líneas, AD_PInstance_ID deberá tener un valor válido distinto de 0. Aunque
     *						se cojan todas las líneas, será includeLineInOrder() quien finalmente decida
     *						si se debe incluir en el pedido final
     *	@param AD_PInstance_ID	si no se guardan todas las líneas, AD_PInstance_ID debe ser el
     *							identificador del proceso en el cual se han elegido las líneas que
     *							se incluirán en el pedido
     *  @param trx_name	Identificador de la transaccion
     *  
     *  @return	devuelve true en caso de que se haya podido generar el pedido de reparación,
     *  		false en caso contrario
     */
    public boolean generateOrder(int C_DocTypeTarget_ID, boolean generarPresupuesto, boolean guardarTodas, int AD_PInstance_ID, String trx_name)
    {
    	boolean dev=true;
    	
    	// comprobacion de condiciones:
    	// comprobar que los parámetros son válidos
    	if(generarPresupuesto==false && guardarTodas==false && AD_PInstance_ID==0)
    	{
    		log.severe("Al generar un pedido o presupuesto de reparación: generando pedido, eligiendo líneas, sin elección");
    		return false;
    	}
    	
    	// obtener listado de líneas de reparación
    	ArrayList listado_lineas=getLineList(generarPresupuesto, guardarTodas, AD_PInstance_ID);
    	if(listado_lineas==null)
    	{
    		log.severe("Al generar un pedido o presupuesto de reparación: error obteniendo líneas a incluir");
    		return false;
    	}
    	if(listado_lineas.size()==0)
    	{
    		log.info("Al generar un pedido o presupuesto de reparación: no hay líneas para incluir en el pedido");
    		return true;
    	}
    	
    	MOrder pedido_reparacion=new MOrder(getCtx(), 0, trx_name);
    	boolean fill_ok=fillRepairOrder(pedido_reparacion, C_DocTypeTarget_ID, listado_lineas, generarPresupuesto, trx_name);
    	if(fill_ok==false)
    	{
    		log.severe("Al generar un pedido o presupuesto de reparación: no se ha podido guardar la cabecera o las líneas");
    		return false;
    	}
    	
    	return dev;
    }	// generateOrder
    
    /**
     * Obtiene una lista con las líneas que se deben incluir en el pedido o presupuesto de reparacion
     * segun los parametros pasados
     * 
     * @param generarPresupuesto	Si estamos generando un presupuesto, se guardan todas las líneas
     * @param guardarTodas	Si generamos un pedido, podemos guardar todas las líneas (dependiendo en
     * 						ultima instancia de includeLineInOrder()) o solo unas seleccionadas
     * @param AD_PInstance_ID	Si generamos un pedido, solo con algunas lineas, las obtendremos de la
     * 							tabla de procesado con esta instancia de proceso
     * @return
     */
    protected ArrayList getLineList(boolean generarPresupuesto, boolean guardarTodas, int AD_PInstance_ID)
    {
    	ArrayList	lista_lineas=new ArrayList();
    	
    	MRepairOrderLine lineas [] = getLines(false, null);
    	int ilen=lineas.length;
    	
    	if(generarPresupuesto==true)
    	{
    		// estamos generando un presupuesto, incluimos todas las lineas
    		for(int i=0;i<ilen;i++)
    		{
    			MRepairOrderLine linea=lineas[i];
    			if(linea.includeLineInOrder(generarPresupuesto)==true)	// generarPresupuesto=true
    				lista_lineas.add(linea);
    		}
    	}
    	else if(guardarTodas==true)
    	{
    		// estamos generando un pedido, incluimos las lineas que no esten marcadas
    		for(int i=0;i<ilen;i++)
    		{
    			MRepairOrderLine linea=lineas[i];
    			if(linea.includeLineInOrder(generarPresupuesto)==true)	// generarPresupuesto=false
    				lista_lineas.add(linea);
    		}
    	}
    	else
    	{
    		// estamos generando un pedido y no guardamos todas las lineas,
    		// solo guardamos las que hayan incluido en el proceso
    		
    		lista_lineas=getLinesSelected(AD_PInstance_ID);
    	}
    	
    	return lista_lineas;
    }	// getLineList
    
    /**
     * Obtiene las lineas seleccionadas en el proceso indicado por parametro
     * 
     * @param AD_PInstance_ID	identificador del proceso en el que se han marcado las líneas
     * 
     * @return	una lista de MRepairOrderLine si todo va bien, o null si hay algún problema
     */
    protected ArrayList getLinesSelected(int AD_PInstance_ID)
    {
    	ArrayList lista=new ArrayList();
    	
    	StringBuffer sql = new StringBuffer("SELECT Key_ID ")
    								.append("FROM C_Process_Selection ")
    								.append("WHERE AD_PInstance_ID = ?");
    	try
    	{
    		PreparedStatement pstmt=DB.prepareStatement(sql.toString(), get_TrxName());
    		pstmt.setInt(1, AD_PInstance_ID);
    		ResultSet rs=pstmt.executeQuery();
    		
    		while(rs.next())
    		{
    			int C_Repair_Order_Line_ID=rs.getInt("Key_ID");
    			MRepairOrderLine linea=new MRepairOrderLine(getCtx(), C_Repair_Order_Line_ID, get_TrxName());
    			if(linea==null || linea.getC_Repair_Order_Line_ID()==0)	// error cargando linea
    				return null;
    			
    			lista.add(linea);
    		}
    		
    		rs.close();
    		rs=null;
    		pstmt.close();
    		pstmt=null;
    	}
    	catch(java.sql.SQLException e)
    	{
    		log.severe("Obteniendo líneas marcadas: " + e.toString());
    		lista.clear();
    		lista=null;
    	}
    	
    	return lista;
    }	// getLinesSelected
    
    /**
     * Guarda los datos de la cabecera del pedido y graba las líneas en el pedido o presupuesto
     * 
     * @param pedido_reparacion	pedido en el que vamos a guardar los datos
     * @param C_DocTypeTarget_ID	Tipo de documento destino
     * @param listado_lineas	lista con las lineas que se deben incluir en el pedido o presupuesto
     * @param esPresupuesto	si es presupuesto no hay que reflejar la linea de pedido
     * @param trx_name	nombre de la transaccion
     * 
     * @return	true si se han podido grabar todos los datos, false en caso contrario 
     */
    protected boolean fillRepairOrder(MOrder pedido_reparacion, int C_DocTypeTarget_ID, ArrayList listado_lineas, boolean esPresupuesto, String trx_name)
    {
    	boolean dev=true;
    	
    	// tipo de documento destino
    	pedido_reparacion.setC_DocTypeTarget_ID(C_DocTypeTarget_ID);
    	
    	// referencia cruzada
    	pedido_reparacion.setC_Repair_Order_ID(getC_Repair_Order_ID());
    	
    	// datos del presupuesto/pedido:
    	// usuarios, clientes y fechas:
    	pedido_reparacion.setDateOrdered(getDateOrdered());
    	pedido_reparacion.setDatePromised(getDatePromised());
    	pedido_reparacion.setC_BPartner_ID(getC_BPartner_ID());
    	pedido_reparacion.setC_BPartner_Location_ID(getC_BPartner_Location_ID());
    	pedido_reparacion.setBill_BPartner_ID(getBill_BPartner_ID());
    	pedido_reparacion.setBill_Location_ID(getBill_Location_ID());
    	pedido_reparacion.setAD_User_ID(getAD_User_ID());
    	pedido_reparacion.setBill_User_ID(getBill_User_ID());
    	pedido_reparacion.setDescription(getDescription());
    	// entrega
    	pedido_reparacion.setM_Warehouse_ID(getM_Warehouse_ID());
    	// pedidos
    	pedido_reparacion.setInvoiceRule(getInvoiceRule());
    	pedido_reparacion.setM_PriceList_ID(getM_PriceList_ID());
    	pedido_reparacion.setC_Currency_ID(getC_Currency_ID());
    	pedido_reparacion.setSalesRep_ID(getSalesRep_ID());
    	//
    	
    	if(pedido_reparacion.save(get_TrxName())!=true)
    	{
    		log.severe("guardando presupuesto o pedido de reparación: no se ha podido guardar la cabecera del documento");
    		return false;
    	}
    	
    	// crear las lineas de pedido
    	
    	int llen=listado_lineas.size();
    	for(int i=0;i<llen;i++)
    	{
    		MRepairOrderLine linea=(MRepairOrderLine)listado_lineas.get(i);
    		
    		MOrderLine linea_reparacion=new MOrderLine(pedido_reparacion);
    		// rellenar datos:
    		// articulo
    		linea_reparacion.setM_Product_ID(linea.getM_Product_ID());
    		linea_reparacion.setM_AttributeSetInstance_ID(linea.getM_AttributeSetInstance_ID());
    		linea_reparacion.setS_ResourceAssignment_ID(linea.getS_ResourceAssignment_ID());
    		linea_reparacion.setDescription(linea.getDescription());
    		// cantidad
    		linea_reparacion.setQtyOrdered(linea.getQtyOrdered());
    		linea_reparacion.setQtyEntered(linea.getQtyEntered());
    		linea_reparacion.setC_UOM_ID(linea.getC_UOM_ID());
    		linea_reparacion.setPriceEntered(linea.getPriceEntered());
    		linea_reparacion.setPriceActual(linea.getPriceActual());
    		linea_reparacion.setPriceList(linea.getPriceList());
    		linea_reparacion.setPriceLimit(linea.getPriceLimit());
    		linea_reparacion.setC_Tax_ID(linea.getC_Tax_ID());
    		linea_reparacion.setDiscount(linea.getDiscount());
    		//
    		
    		if(linea_reparacion.save(trx_name)!=true)
    		{
    			log.severe("guardando presupuesto o pedido de reparación: no se ha podido grabar la nueva linea");
    			return false;
    		}
    		
    		// comprobamos si hay que actualizar la linea de orden de reparacion para reflejar
    		// el pedido
    		if(esPresupuesto==false)
    		{
    			// no es presupuesto, marcamos:
    			
    			linea.setC_OrderLine_ID(linea_reparacion.getC_OrderLine_ID());
    			if(linea.save(trx_name)!=true)
    			{
    				log.severe("guardando presupuesto o pedido de reparación: no se ha podido actualizar la línea de reparación");
    				return false;
    			}
    		}
    	}
    	
    	return dev;
    }	// fillRepairOrder
    
    /**
     * Realizará comprobaciones especificas para saber si se puede completar una orden de reparacion
     * 
     * @return	true si se puede completar la orden de reparacion, false en caso contrario
     */
    protected boolean canCompleteOrder()
    {
    	boolean dev=true;
    	
    	// TODO: aqui se iran comprobando las condiciones que pueden hacer que no se pueda completar
    	
    	return dev;
    }	// canCompleteOrder
    
    /**
     * Comprueba si hay que generar un pedido o presupuesto
     * 
     * @param	esPresupuesto	indica si queremos generar un presupuesto o un pedido
     *
     * @return
     */
    protected boolean haveToGenerateOrder(boolean esPresupuesto)
    {
    	boolean dev=true;
    	
    	// si la reparación está en garantía, no hay que generar pedido
    	if(isWarranty()==true)
    		dev=false;
    	
    	return dev;
    }	// generateMOrder
    
}    // MRepairOrder



/*
 *  @(#)MRepairOrder.java   02.07.07
 * 
 *  Fin del fichero MOrder.java
 *  
 *  Versión 2.2
 *
 */
