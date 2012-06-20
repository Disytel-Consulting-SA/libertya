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

import javax.swing.JOptionPane;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.ProductionSourceGenerator;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

@SuppressWarnings("serial")
public class MProductionOrder extends X_C_Production_Order implements DocAction {

    /**
     * Descripción de Método
     *
     *
     * @param from
     * @param dateDoc
     * @param C_DocTypeTarget_ID
     * @param isSOTrx
     * @param counter
     * @param copyASI
     * @param trxName
     *
     * @return
     */

    public static MProductionOrder copyFrom( MProductionOrder from,Timestamp dateDoc,int C_DocTypeTarget_ID,boolean isSOTrx,boolean counter,boolean copyASI,String trxName ) {
        MProductionOrder to = new MProductionOrder( from.getCtx(),0,trxName );

        to.set_TrxName( trxName );
        PO.copyValues( from,to,from.getAD_Client_ID(),from.getAD_Org_ID());
        to.setC_Production_Order_ID(0);
        to.set_ValueNoCheck( "DocumentNo",null );

        //

        to.setDocStatus( DOCSTATUS_Drafted );    // Draft
        to.setDocAction( DOCACTION_Complete );

        //

        to.setC_DocType_ID( 0 );
        to.setC_DocTypeTarget_ID( C_DocTypeTarget_ID );
        //

        to.setIsSelected( false );
        to.setDateOrdered( dateDoc );
        to.setDatePromised( dateDoc );    // assumption
        to.setDatePrinted( null );

        to.setIsApproved( false );


        to.setIsDelivered( false );

        to.setProcessed( false );

        if( !to.save( trxName )) {
            throw new IllegalStateException( "Could not create Order" );
        }

        return to;
    }    // copyFrom

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Order_ID
     * @param trxName
     */

    public MProductionOrder( Properties ctx,int C_ProductionOrder_ID,String trxName ) {
        super( ctx,C_ProductionOrder_ID,trxName );

        // New

        if( C_ProductionOrder_ID == 0 ) {
            setDocStatus( DOCSTATUS_Drafted );
            setDocAction( DOCACTION_Prepare );

            setPriorityRule( PRIORITYRULE_Medium );
            setIsSelected( false );


            setIsApproved( false );
            setIsDelivered( false );

            //

            super.setProcessed( false );
            setProcessing( false );
            setDatePromised( new Timestamp( System.currentTimeMillis()));
            setDateOrdered( new Timestamp( System.currentTimeMillis()));

        }
    }    // MOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param project
     * @param IsSOTrx
     * @param DocSubTypeSO
     */

    public MProductionOrder( MProject project,boolean IsSOTrx,String DocSubTypeSO ) {
        this( project.getCtx(),0,project.get_TrxName());
        setAD_Client_ID( project.getAD_Client_ID());
        setAD_Org_ID( project.getAD_Org_ID());
        setC_Campaign_ID( project.getC_Campaign_ID());

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

        setAD_User_ID( project.getAD_User_ID());

        //

        setM_Warehouse_ID( project.getM_Warehouse_ID());

        setC_DocTypeTarget_ID();
        
    }    // MOrder

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProductionOrder( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MOrder

    /** Descripción de Campos */

    private MProductionOrderline[] m_lines = null;

    /** Descripción de Campos */

 /*   no se usa 
  *  
  *  private MOrderTax[] m_taxes = null; 
  *  
  *  */

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
     * @param otherOrder
     * @param counter
     * @param copyASI
     *
     * @return
     */

  /*  public int copyLinesFrom( MOrderProduction otherOrder,boolean counter,boolean copyASI ) {
        if( isProcessed() ||  (otherOrder == null) ) {
            return 0;
        }

        MOrderProductionlines[] fromLines = otherOrder.getLines( false,null );
        int          count     = 0;

        for( int i = 0;i < fromLines.length;i++ ) {
            MOrderProductionLine line = new MOrderLine( this );

            PO.copyValues( fromLines[ i ],line,getAD_Client_ID(),getAD_Org_ID());
            line.setC_Order_ID( getC_Order_ID());
            line.setOrder( this );
            line.setC_OrderLine_ID( 0 );    // new

            // References

            if( !copyASI ) {
                line.setM_AttributeSetInstance_ID( 0 );
                line.setS_ResourceAssignment_ID( 0 );
            }

            if( counter ) {
                line.setRef_OrderLine_ID( fromLines[ i ].getC_OrderLine_ID());
            } else {
                line.setRef_OrderLine_ID( 0 );
            }

            //

            line.setQtyDelivered( Env.ZERO );
            line.setQtyInvoiced( Env.ZERO );
            line.setQtyReserved( Env.ZERO );
            line.setDateDelivered( null );
            line.setDateInvoiced( null );

            // Tax

            if( getC_BPartner_ID() != otherOrder.getC_BPartner_ID()) {
                line.setTax();    // recalculate
            }

            //
            //

            line.setProcessed( false );

            if( line.save( get_TrxName())) {
                count++;
            }

            // Cross Link

            if( counter ) {
                fromLines[ i ].setRef_OrderLine_ID( line.getC_OrderLine_ID());
                fromLines[ i ].save( get_TrxName());
            }
        }

        if( fromLines.length != count ) {
            log.log( Level.SEVERE,"Line difference - From=" + fromLines.length + " <> Saved=" + count );
        }

        return count;
    }    // copyLinesFrom
*/
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MProductionOrder[" ).append( getID()).append( "-" ).append( getDocumentNo()).append( ",C_DocType_ID=" ).append( getC_DocType_ID()).append( "]" );

        return sb.toString();
    }    // toString


    /**
     * Descripción de Método
     *
     *
     * @param whereClause
     * @param orderClause
     *
     * @return
     */

    public MProductionOrderline[] getLines( String whereClause,String orderClause ) {
    	log.fine("getLines 1");
        ArrayList    list = new ArrayList();
        StringBuffer sql  = new StringBuffer( "SELECT * FROM C_Production_OrderLine WHERE C_Production_Order_ID=? " );

        if( whereClause != null ) {
            sql.append( whereClause );
        }

        if( orderClause != null ) {
            sql.append( " " ).append( orderClause );
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,getC_Production_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MProductionOrderline ol = new MProductionOrderline( getCtx(),rs,get_TrxName());

               // ol.setHeaderInfo( this );
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

        MProductionOrderline[] lines = new MProductionOrderline[ list.size()];

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

    public MProductionOrderline[] getLines( boolean requery,String orderBy ) {
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

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProductionOrderline[] getLines() {
    	log.fine("getLines 3");
        return getLines( false,null );
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @param step
     */

    public void renumberLines( int step ) {
        int          number = step;
        MProductionOrderline[] lines  = getLines( true,null );    // Line is default

        for( int i = 0;i < lines.length;i++ ) {
            MProductionOrderline line = lines[ i ];

            line.setLine( number );
            line.save( get_TrxName());
            number += step;
        }

        m_lines = null;
    }    // renumberLines

    /**
     * Descripción de Método
     *
     *
     * @param C_OrderLine_ID
     *
     * @return
     */

    public boolean isOrderLine( int C_Production_OrderLine_ID ) {
        if( m_lines == null ) {
            getLines();
        }

        for( int i = 0;i < m_lines.length;i++ ) {
            if( m_lines[ i ].getC_Production_Orderline_ID() == C_Production_OrderLine_ID ) {
                return true;
            }
        }

        return false;
    }    // isOrderLine

    /**
     * Descripción de Método
     *
     *
     * @param requery
     *
     * @return
     */

  
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
        setM_forceCreation(forceCreation);
    }    // setDocAction

    /**
     * Descripción de Método
     *
     *
     * @param processed
     */

    public void setProcessed( boolean processed ) {
        super.setProcessed( processed );

        if( getID() == 0 ) {
            return;
        }

        String set = "SET Processed='" + ( processed
                                           ?"Y"
                                           :"N" ) + "' WHERE C_Production_Order_ID=" + getC_Production_Order_ID();
        @SuppressWarnings("unused") 
        int noLine = DB.executeUpdate( "UPDATE C_Production_OrderLine " + set,get_TrxName());
       
        m_lines = null;
      
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

    	// Pruebas...
    	// Cojemmos el usuario
    	//setSalesRep_ID(Env.getAD_User_ID(getCtx()));

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
        /*
        if( !newRecord && is_ValueChanged( "M_Warehouse_ID" )) {
            MProductionOrderline[] lines = getLines( false,null );

            for( int i = 0;i < lines.length;i++ ) {
                if( !lines[ i ].canChangeWarehouse()) {
                    return false;
                }
            }
        }
        */

        // Default Document Type

        if( getC_DocTypeTarget_ID() == 0 ) {
            setC_DocTypeTarget_ID( DocSubTypeSO_Standard );
        }

        return true;
    }    // beforeSave

   
  
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

        getLines();

        for( int i = 0;i < m_lines.length;i++ ) {
            if( !m_lines[ i ].beforeDelete()) {
                return false;
            }
        }

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

  /*  
   *
   * No se utiliza
   * 
   * private boolean m_justPrepared = false;
   * 
   */

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
      
    	MProductionOrderline[] lines;
    	
        // Lines
        lines = getLines( true, null);
        if( lines.length == 0 ) {
            m_processMsg = "@NoLines@";
            return DocAction.STATUS_Invalid;
        }

    	
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
    public boolean check_attribute(){
    	 PreparedStatement pstmt = null;
    	 StringBuffer sql = new StringBuffer("SELECT c_production_orderline_id,m_attributesetinstance_id from c_production_orderline where c_production_order_id= ?");
    	 try{
    		 pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
             pstmt.setInt( 1,getC_Production_Order_ID());

             ResultSet rs = pstmt.executeQuery();
    		 while(rs.next()){
    			 if(rs.getInt(2)==0){
    				 return false;
    			 }
    		 }
    		 rs.close();
             pstmt.close();
             pstmt = null;
    	 }catch( Exception e ) {
             log.log( Level.SEVERE,"getLines - " + sql,e );
         }
    	
    	return true;
    }
    
    public String completeIt() {
    
    	try {
			//MOrder order = createBOMProductsOrder();
    		MInOut bomInOut = createBOMProductsInout();
    		MInOut prodIncome = createProductsIncome();
    		m_processMsg = 
    			"@ProductionProductsInOut@: " + prodIncome.getDocumentNo() + ". " +
    			"@BOMProductsInOut@: " + bomInOut.getDocumentNo();
    		setProcessed(true);
    		setDocAction(DocAction.ACTION_Close);
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			return DocAction.STATUS_Invalid;
		}
    	
    	return DocAction.STATUS_Completed;
    }
    
    /**
     * Crea el remito de salida de los materiales de todos los productos
     * a producir a partir de este pedido.
     */
    private MInOut createBOMProductsInout() throws Exception {
    	// Creación del encabezado del remito de salida de los materiales
    	// que componen los productos a producir. 
    	MInOut inout = createInOutHeader(MInOut.MOVEMENTTYPE_Production_, true);
    	// Se permite stock negativo de materiales.
    	inout.setDeliveryRule(MInOut.DELIVERYRULE_Force);
    	if (!inout.save()) {
    		throw new Exception("@BOMInOutCreateError@: " + getError());
    	}
    	
    	// Líneas del remito: Cada línea del remito representa una línea de material
    	// de cada producto a producir.
    	MProductionOrderline[] lines = getLines();
    	for (int i = 0; i < lines.length; i++) {
			lines[i].createBOMInOutLines(inout);
		}

    	// Se completa el remito
    	if (!inout.processIt(DOCACTION_Complete)) {
    		throw new Exception("@BOMInOutCompleteError@: " + inout.getProcessMsg());
    	} else
    		inout.save();
    	
    	return inout;
    }
    
    private MInOut createProductsIncome() throws Exception {
    	// Creación del encabezado del remito de entrada de los artículos
    	// producidos.
    	MInOut inout = createInOutHeader(MInOut.MOVEMENTTYPE_ProductionPlus, false);
    	if (!inout.save()) {
    		throw new Exception("@ProductsIncomeCreateError@: " + getError());
    	}
    	
    	// Líneas del remito: Cada línea del remito representa una línea de material
    	// de cada producto a producir.
    	MProductionOrderline[] lines = getLines();
    	for (int i = 0; i < lines.length; i++) {
			lines[i].createProductIncomeLine(inout);
		}

    	// Se completa el remito
    	if (!inout.processIt(DOCACTION_Complete)) {
    		throw new Exception("@ProductsIncomeCompleteError@: " + inout.getProcessMsg());
    	} else
    		inout.save();
    	
    	return inout;
    }
    
    /**
     * Crea un encabezado de remito a partir de los datos de este pedido de producción.
     * @param movementType Tipo de movimiento
     * @param isSOTrx Transacción de ventas
     * @return <code>MInOut</code> instanciado, sin haberle hecho <code>save()</code>
     */
    private MInOut createInOutHeader(String movementType, boolean isSOTrx) {
    	MInOut inout = new MInOut(getCtx(), 0, get_TrxName());
    	inout.setClientOrg(this);
    	inout.setIgnoreShipmentOrder(true);
    	inout.setIsSOTrx(isSOTrx);
    	inout.setMovementType(movementType);
    	// Modified by Matías Cap - Disytel
		// Esta modificación se dió debido a que con el código anterior se setea
		// un tipo de documento en base al docbase pero con el criterio de
		// ordenación isDefault Desc. El problema es que al traer todos los
		// tipos de documento con ese docbase y éstos encontrarse ninguno sin
		// isDefault = 'Y' ordena por última creación. Esto se daba cuando
		// creabamos la salida de los productos que compone el ensamblaje, se
		// estaba creando con el tipo de documento Devoluciones de Clientes y en
		// realidad se debe usar Remito de Salida. La solución a esto es forzar
		// remito de salida y/o remito de entrada según el caso dependiendo del
		// isSoTrx. 
    	// -------------------------------------------------------
    	// Líneas comentadas
    	// -------------------------------------------------------
//    	inout.setC_DocType_ID();
    	// -------------------------------------------------------
    	MDocType docType = null;
    	// Instancio el tipo de documento dependiendo el caso
    	if(isSOTrx){
			docType = MDocType.getDocType(getCtx(),
					MDocType.DOCTYPE_MaterialDelivery, get_TrxName());
    	}
    	else{
			docType = MDocType.getDocType(getCtx(),
					MDocType.DOCTYPE_MaterialReceipt, get_TrxName());
    	}
		// Si el tipo de documento se pudo obtener, entonces lo seteo remito en
		// creación
    	if(docType != null){
    		inout.setC_DocType_ID(docType.getID());
    	}
    	// -------------------------------------------------------
    	inout.setM_Warehouse_ID(getM_Warehouse_ID());
    	inout.setBPartner(getBPartner());
    	inout.setDescription(
   			Msg.parseTranslation(getCtx(), 
   					"@C_Production_Order_ID@ " + getDocumentNo() + 
   					(getDescription() != null ? ": " + getDescription() : "")));
    	
    	return inout;
    }
    
    /**
     * Crea el pedido de salida de materiales que componen los productos que se
     * van a producir según este pedido de producción.
     */
    private MOrder createBOMProductsOrder() throws Exception {
    	// Creación del encabezado del pedido de salida de los materiales
    	// que componen los productos a producir. 
    	MOrder order = new MOrder(getCtx(), 0, get_TrxName());
    	order.setClientOrg(this);
    	order.setC_DocTypeTarget_ID(getC_DocTypeTarget_ID());
    	order.setM_Warehouse_ID(getM_Warehouse_ID());
    	order.setC_BPartner_ID(getC_BPartner_ID());
    	order.setIsSOTrx(true);
    	order.setDescription(
   			Msg.parseTranslation(getCtx(), 
   					"@C_Production_Order_ID @" + getDocumentNo() + 
   					(getDescription() != null ? ": " + getDescription() : "")));
    	
    	if (!order.save()) {
    		throw new Exception("@BOMOrderCreateError@: " + getError());
    	}
    	
    	// Líneas del pedido: Cada línea del pedido representa una línea de material
    	// de cada producto a producir.
    	MProductionOrderline[] lines = getLines();
    	for (int i = 0; i < lines.length; i++) {
			lines[i].createBOMOrderLines(order);
		}

    	// Se completa el pedido
    	if (!order.processIt(DOCACTION_Complete)) {
    		throw new Exception("@BOMOrderCompleteError@: " + order.getProcessMsg());
    	} else
    		order.save();
    	
    	return order;
    }
   
    private String completeIt_OLD() {
    	 if(check_attribute()==false){
    		 JOptionPane.showMessageDialog( null,"Por favor, introduzca los números de serie de los articulos producidos ","Número de serie", JOptionPane.INFORMATION_MESSAGE );
    		 return DocAction.ACTION_Invalidate;
    	 }
    	 PreparedStatement pstmt = null;
    	 StringBuffer sql = new StringBuffer("SELECT c_production_orderline_id, qtyordered, m_product_id from c_production_orderline where c_production_order_id = ?");
         try {
             pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
             pstmt.setInt( 1,getC_Production_Order_ID());

             ResultSet rs = pstmt.executeQuery();

             while( rs.next()) {
            	 // para cada fuente de la línea busco si hay productos, si hay lo hago la reserva, y lo coloco en el almac�n
            	 StringBuffer sql2 = new StringBuffer("SELECT m_productbom_id, bomqty from m_product_bom" +
            	 		" WHERE m_product_id=(select m_product_id from c_production_orderline_source where c_production_orderline_id = ? )");
            	 // Busco 
            	 PreparedStatement pstmt2 = null;
            	 try{
            		 pstmt2 = DB.prepareStatement( sql2.toString(),get_TrxName());
            		 pstmt2.setInt( 1,rs.getInt(1));
            		 ResultSet rs2 = pstmt2.executeQuery();
            		 while(rs2.next()){
            			 //Hay que comprobar si hay stock de cada uno de los articulos 
            			 StringBuffer sql3 = new StringBuffer("SELECT name, bomQtyOnHand("+rs2.getInt(1)+","+Env.getContext(Env.getCtx(), "#M_Warehouse_ID")+",0)" +
            			 		" from m_product where m_product_id="+rs2.getInt(1));
            			 PreparedStatement pstmt3 = null;
            			 try{
            				 pstmt3 = DB.prepareStatement( sql3.toString(),get_TrxName());
            				 ResultSet rs3 = pstmt3.executeQuery();
            				 if(rs3.next()){
            					 if(rs3.getBigDecimal(2).subtract(rs2.getBigDecimal(2).multiply(rs.getBigDecimal(2))).compareTo(BigDecimal.ZERO)<=0){
            						 // si no hay articulos en stock de ese almac�n
            						 JOptionPane.showMessageDialog( null,"El producto  "+rs3.getString(1)+" no tiene stock disponible ","No hay Stock", JOptionPane.INFORMATION_MESSAGE );
               						 return DocAction.ACTION_Invalidate;
            					 }
            						 int M_Locator_ID=0;    // si hay articulo, busco la localizaci�n dentro del almac�n
            			                    M_Locator_ID = MStorage.getM_Locator_ID( Integer.valueOf(Env.getContext(Env.getCtx(), "#M_Warehouse_ID")).intValue(),rs2.getInt(1),0,rs2.getBigDecimal(2).multiply(rs.getBigDecimal(2)),get_TrxName());
               			                // Get default Location
            			                if( M_Locator_ID == 0 ) {
            			                    MWarehouse wh = MWarehouse.get( getCtx(),Integer.valueOf(Env.getContext(Env.getCtx(), "#M_Warehouse_ID")).intValue());

            			                    M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
            			                }
            						 MStorage.add( getCtx(),Integer.valueOf(Env.getContext(Env.getCtx(), "#M_Warehouse_ID")).intValue(),M_Locator_ID,rs2.getInt(1),0,0,rs2.getBigDecimal(2).multiply(rs.getBigDecimal(2)).negate(),null,null,get_TrxName());
            						
            				//	 }
            				 }
            				 rs3.close();
                             pstmt3.close();
                             pstmt3 = null;
            			 } catch( Exception e ) {
                             log.log( Level.SEVERE,"getLines - " + sql,e );
                         }
            			 //En este punto ya sabemos que existe stock para realizar la producción
            			
            			 
            		 }
            		 rs2.close();
                     pstmt2.close();
                     pstmt2 = null;
                     this.asignarConjuntosAtributos();
            	 }catch( Exception e ) {
                     log.log( Level.SEVERE,"getLines - " + sql,e );
                 }
            	 int M_Locator_ID=0;
                 M_Locator_ID = MStorage.getM_Locator_ID( Integer.valueOf(Env.getContext(Env.getCtx(), "#M_Warehouse_ID")).intValue(),rs.getInt(3),0,rs.getBigDecimal(2),get_TrxName());
             

                 // Get default Location

                 if( M_Locator_ID == 0 ) {
                	 MWarehouse wh = MWarehouse.get( getCtx(),Integer.valueOf(Env.getContext(Env.getCtx(), "#M_Warehouse_ID")).intValue());

                	 M_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();
                 }
            	 MStorage.add( getCtx(),Integer.valueOf(Env.getContext(Env.getCtx(), "#M_Warehouse_ID")).intValue(),M_Locator_ID,rs.getInt(3),0,0,rs.getBigDecimal(2),null,null,get_TrxName());

             }
            
             rs.close();
             pstmt.close();
             pstmt = null;
         } catch( Exception e ) {
             log.log( Level.SEVERE,"getLines - " + sql,e );
         }
        setDocAction( DOCACTION_Close );
        return DocAction.STATUS_Completed;
    }    // completeIt

	/**
     * Descripción de Método: Asigna una instancia del conjunto de atributo a las lineas fuentes
     *
     * @return
     */

 
    private void asignarConjuntosAtributos() {
    	ProductionSourceGenerator gen = new ProductionSourceGenerator();
    	gen.asignarConjuntosAtributos(this.getC_Production_Order_ID());
	}

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
        MProductionOrderline[] lines = getLines( true,"M_Product_ID" );

        log.info( toString());

        for( int i = 0;i < lines.length;i++ ) {
            MProductionOrderline line = lines[ i ];
            BigDecimal old  = line.getQtyOrdered();

            if( old.compareTo( Env.ZERO ) != 0 ) {
                line.addDescription( Msg.getMsg( getCtx(),"Voided" ) + " (" + old + ")" );
                line.setQty( Env.ZERO );
                line.save( get_TrxName());
            }
        }

        addDescription( Msg.getMsg( getCtx(),"Voided" ));

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

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( toString());

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
        log.info( toString());

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

	public int getC_Currency_ID() {
		return 0;
	}

	public BigDecimal getApprovalAmt() {
		return null;
	}

	public int getDoc_User_ID() {
		return 0;
	}

	public String getSummary() {
			return null;
	}

	private void setM_forceCreation(boolean m_forceCreation) {
		this.m_forceCreation = m_forceCreation;
	}
	
	protected static String getError() {
		ValueNamePair error = CLogger.retrieveError();
		String errorMsg = "";
		if (error != null) {
			errorMsg += error.getValue() + " - " + error.getName();
		}
		return errorMsg;
	}
	
	private MBPartner m_BPartner = null; 
	
	public MBPartner getBPartner() {
		if (m_BPartner == null)
			m_BPartner = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
		return m_BPartner;
	}

}    // MOrder



/*
 *  @(#)MOrder.java   02.07.07
 * 
 *  Fin del fichero MOrder.java
 *  
 *  Versión 2.2
 *
 */
