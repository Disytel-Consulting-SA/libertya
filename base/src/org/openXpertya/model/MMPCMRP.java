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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.wf.MWorkflow;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MMPCMRP extends X_MPC_MRP {

    // private static CCache s_cache = new CCache ("M_Product_Costing", 20);

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param MPC_MRP_ID
     * @param trxName
     */

    public MMPCMRP( Properties ctx,int MPC_MRP_ID,String trxName ) {
        super( ctx,MPC_MRP_ID,trxName );

        if( MPC_MRP_ID == 0 ) {

            /*
             * setC_AcctSchema_ID(0);
             * setCostCumAmt();
             * setCostCumQty();
             * setCostLLAmt();
             * setCostTLAmt();
             * setM_Product_ID();
             * setM_Warehouse_ID();
             * setMPC_Cost_Element_ID();
             * stS_Resource_ID();
             */

        }
    }    // MPCCostElement

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMPCMRP( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MMPCMRP.class );

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
        if( !newRecord ) {
            return success;
        }

        /*
         * MMPCProductPlanning pp = MMPCProductPlanning.getSupplyWarehouse( Env.getCtx() , getAD_Org_ID() , getM_Product_ID() , getM_Warehouse_ID());
         *
         * if(pp != null)
         * {
         *   setS_Resource_ID(pp.getS_Resource_ID());
         *   setPlanner_ID(pp.getPlanner_ID());
         * }
         */

        return true;
    }

    /**
     * Descripción de Método
     *
     *
     * @param fl
     * @param trxName
     * @param delete
     *
     * @return
     */

    public static int M_ForecastLine( MForecastLine fl,String trxName,boolean delete ) {
    	log.fine("En M_foreCastLine");
        String sql = null;

        if( delete ) {
            sql = "DELETE FROM MPC_MRP WHERE M_ForecastLine_ID = " + fl.getM_ForecastLine_ID() + " AND AD_Client_ID = " + fl.getAD_Client_ID();
            DB.executeUpdate( sql );

            return 0;
        }

        MMPCMRP      mrp    = null;
        MPeriod      period = new MPeriod( Env.getCtx(),fl.getC_Period_ID(),null );
        MWarehouse[] w      = MWarehouse.getForOrg( Env.getCtx(),fl.getAD_Org_ID());
        X_M_Forecast f      = new X_M_Forecast( Env.getCtx(),fl.getM_Forecast_ID(),null );

        sql = new String( "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.M_ForecastLine_ID = ? " );

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,fl.getM_ForecastLine_ID());

            ResultSet rs      = pstmt.executeQuery();
            boolean   records = false;

            while( rs.next()) {
                records = true;
                mrp     = new MMPCMRP( Env.getCtx(),rs.getInt( 1 ),null );
                mrp.setDescription( f.getDescription());
                mrp.setName( "MRP" );
                mrp.setQty( fl.getQty());
                log.fine("Ennnn M_forecast_line1, setQty="+fl.getQty());
                mrp.setDatePromised( period.getStartDate());
                mrp.setDateStartSchedule( period.getStartDate());
                mrp.setDateFinishSchedule( period.getStartDate());
                mrp.setDateOrdered( period.getStartDate());
                mrp.setM_Product_ID( fl.getM_Product_ID());

                int M_Warehouse_ID = DB.getSQLValue( null,"SELECT M_Warehouse_ID FROM ",period.getAD_Org_ID());

                mrp.setM_Warehouse_ID( w[ 0 ].getM_Warehouse_ID());
                mrp.setDocStatus( "IP" );
                mrp.save( trxName );
            }

            if( !records ) {
                mrp = new MMPCMRP( Env.getCtx(),0,null );
                mrp.setM_ForecastLine_ID( fl.getM_ForecastLine_ID());
                mrp.setDescription( f.getDescription());
                mrp.setM_Forecast_ID( f.getM_Forecast_ID());
                mrp.setQty( fl.getQty());
                log.fine("Ennnn M_forecast_line2, setQty="+fl.getQty());
                mrp.setDatePromised( period.getStartDate());
                mrp.setDateStartSchedule( period.getStartDate());
                mrp.setDateFinishSchedule( period.getStartDate());
                mrp.setDateOrdered( period.getStartDate());
                mrp.setM_Product_ID( fl.getM_Product_ID());
                mrp.setM_Warehouse_ID( w[ 0 ].getM_Warehouse_ID());
                mrp.setDocStatus( "IP" );
                mrp.setType( "D" );
                mrp.setTypeMRP( "FCT" );
                mrp.save( trxName );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        return mrp.getMPC_MRP_ID();
    }

    /*
     * public static int C_Order(MOrder o, String trxName)
     * {
     *
     *       MOrderLine[] lines = o.getLines();
     *       for (int i = 0 ;  i < lines.length  ; i++)
     *       {
     *        MMPCMRP.C_OrderLine(lines[i],null,false);
     *       }
     *       MMPCMRP mrp = null;
     *       int retval=0;
     *       String sql =  new String("SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.C_Order_ID = ? ");
     *
     *
     *           PreparedStatement pstmt = null;
     *           try
     *           {
     *               pstmt = DB.prepareStatement (sql);
     *               pstmt.setInt(1, o.getC_Order_ID());
     *               ResultSet rs = pstmt.executeQuery ();
     *               boolean records = false;
     *
     *               while (rs.next())
     *               {
     *                       records = true;
     *                       mrp = new MMPCMRP(Env.getCtx(), rs.getInt(1),null);
     *                       mrp.setDocStatus(o.getDocStatus());
     *                   mrp.save(trxName);
     *
     *               }
     *               rs.close();
     *               pstmt.close();
     *
     *               //if (!records)
     *               if (records)
     *                   retval=mrp.getMPC_MRP_ID();
     *
     *           }
     *           catch (SQLException ex)
     *           {
     *               log.log(Level.SEVERE, "doIt - " + sql , ex);
     *           }
     *
     *
     *
     *
     *           return retval;
     * }
     */

    /**
     * Descripción de Método
     *
     *
     * @param ol
     * @param trxName
     * @param delete
     *
     * @return
     */

    public static int C_OrderLine( MOrderLine ol,String trxName,boolean delete ) {
    	log.fine("En C_OrderLine con trxName= " + trxName+ " delete= "+ delete);
        String sql = null;

        if( delete ) {
            sql = "DELETE FROM MPC_MRP WHERE C_OrderLine_ID = " + ol.getC_OrderLine_ID() + " AND AD_Client_ID = " + ol.getAD_Client_ID();
            DB.executeUpdate( sql );

            return 0;
        }

        MMPCMRP mrp = null;

        sql = new String( "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.C_OrderLine_ID = ? " );

        MOrder            o     = new MOrder( Env.getCtx(),ol.getC_Order_ID(),ol.get_TrxName() );
        PreparedStatement pstmt = null;
        int orderline=ol.getC_OrderLine_ID();
        log.fine("******************** la c_order_line es = "+ orderline+"*********************");
        

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,ol.getC_OrderLine_ID());

            ResultSet rs      = pstmt.executeQuery();
            boolean   records = false;

            while( rs.next()) {
            	log.fine("En recors = true");
                records = true;
                mrp     = new MMPCMRP( Env.getCtx(),rs.getInt( 1 ),ol.get_TrxName() );
                mrp.setDescription( ol.getDescription());
                mrp.setName( "MRP" );
                //Modificado por ConSerTi, para corregir el error que deja las lineas a "0", cuando se cierra un pedido de proveedor.
                //mrp.setQty( ol.getQtyOrdered().subtract( ol.getQtyDelivered())); Original
                mrp.setQty( ol.getQtyOrdered());
                log.fine("Ennnn C_OrderLine1, setQty="+ol.getQtyOrdered());
                //Fin modificacion.
                mrp.setDatePromised( ol.getDatePromised());
                mrp.setDateStartSchedule( ol.getDatePromised());
                mrp.setDateFinishSchedule( ol.getDatePromised());
                mrp.setDateOrdered( ol.getDateOrdered());
                mrp.setM_Product_ID( ol.getM_Product_ID());
                mrp.setM_Warehouse_ID( ol.getM_Warehouse_ID());
                mrp.setDocStatus( o.getDocStatus());

                // mrp.setIsAvailable(true);
                log.fine("guardando datos" + trxName);

                //mrp.save( trxName ); Original
                mrp.saveUpdate();
            }

            if( !records ) {
            	log.fine("En recors = false");
                mrp = new MMPCMRP( Env.getCtx(),0,ol.get_TrxName() );
                mrp.setC_OrderLine_ID( ol.getC_OrderLine_ID());
                mrp.setDescription( ol.getDescription());
                mrp.setC_Order_ID( ol.getC_Order_ID());
                //Modificado por ConSerTi, para corregir el error que deja las lineas a "0", cuando se cierra un pedido de proveedor.
                //mrp.setQty( ol.getQtyOrdered().subtract( ol.getQtyDelivered())); Original
                mrp.setQty( ol.getQtyOrdered());
                log.fine("Ennnn C_OrderLine1, setQty="+ol.getQtyOrdered());
                //Fin Modificacion.
                mrp.setDatePromised( ol.getDatePromised());
                mrp.setDateStartSchedule( ol.getDatePromised());
                mrp.setDateFinishSchedule( ol.getDatePromised());
                mrp.setDateOrdered( ol.getDateOrdered());
                mrp.setM_Product_ID( ol.getM_Product_ID());
                mrp.setM_Warehouse_ID( ol.getM_Warehouse_ID());
                mrp.setDocStatus( o.getDocStatus());

                // mrp.setS_Resource_ID();

                // String isSoTrx = Env.getContext(Env.getCtx(), "isSOTrx");

                if( o.isSOTrx()) {
                    mrp.setType( "D" );
                    mrp.setTypeMRP( "SOO" );
                } else {
                    mrp.setType( "S" );
                    mrp.setTypeMRP( "POO" );
                }

                mrp.save( trxName );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        return mrp.getMPC_MRP_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param o
     * @param trxName
     *
     * @return
     */

    public static int MPC_Order( X_MPC_Order o,String trxName ) {
    	log.fine("En Mpc_Order");
        String sql = new String( "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.Type = 'S' AND mrp.TypeMRP='MOP' AND mrp.MPC_Order_ID = ? " );

        // MMPCOrder o = new MMPCOrder(Env.getCtx(), MPC_Order_ID);

        MMPCMRP           mrp   = null;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,o.getMPC_Order_ID());

            ResultSet rs      = pstmt.executeQuery();
            boolean   records = false;

            while( rs.next()) {
                records = true;
                mrp     = new MMPCMRP( Env.getCtx(),rs.getInt( 1 ),null );
                mrp.setDescription( o.getDescription());
                mrp.setQty( o.getQtyOrdered().subtract( o.getQtyDelivered()));
                log.fine("Ennnn MPC_Order1, setQty="+o.getQtyOrdered().subtract( o.getQtyDelivered())+", getQtyOrdered solo="+o.getQtyOrdered());
                mrp.setDatePromised( o.getDatePromised());
                mrp.setDateOrdered( o.getDateOrdered());
                mrp.setDateStartSchedule( o.getDateStartSchedule());
                mrp.setDateFinishSchedule( o.getDateFinishSchedule());
                mrp.setM_Product_ID( o.getM_Product_ID());
                mrp.setM_Warehouse_ID( o.getM_Warehouse_ID());
                mrp.setS_Resource_ID( o.getS_Resource_ID());
                mrp.setDocStatus( o.getDocStatus());
                mrp.save( trxName );
            }

            if( !records ) {
                mrp = new MMPCMRP( Env.getCtx(),0,null );
                mrp.setMPC_Order_ID( o.getMPC_Order_ID());
                mrp.setDescription( o.getDescription());
                mrp.setName( "MRP" );

                // mrp.setC_Order_ID(o.getC_Order_ID());

                mrp.setQty( o.getQtyOrdered().subtract( o.getQtyDelivered()));
                log.fine("Ennnn MPC_Order2, setQty="+o.getQtyOrdered().subtract( o.getQtyDelivered())+", getQtyOrdered solo="+o.getQtyOrdered());
                mrp.setDatePromised( o.getDatePromised());
                mrp.setDateOrdered( o.getDateOrdered());
                mrp.setDateStartSchedule( o.getDateStartSchedule());
                mrp.setDateFinishSchedule( o.getDateStartSchedule());
                mrp.setM_Product_ID( o.getM_Product_ID());
                mrp.setM_Warehouse_ID( o.getM_Warehouse_ID());
                mrp.setS_Resource_ID( o.getS_Resource_ID());

                // mrp.setS_Resource_ID();

                mrp.setType( "S" );
                mrp.setTypeMRP( "MOP" );
                mrp.setDocStatus( o.getDocStatus());
                mrp.save( trxName );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        sql = new String( "SELECT ol.MPC_Order_BOMLine_ID FROM MPC_Order o INNER JOIN MPC_Order_BOMLine ol ON (ol.MPC_Order_ID=o.MPC_Order_ID) WHERE o.MPC_Order_ID = ? " );
        pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,o.getMPC_Order_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                X_MPC_Order_BOMLine ol = new X_MPC_Order_BOMLine( Env.getCtx(),rs.getInt( 1 ),null );

                MPC_Order_BOMLine( ol,null );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        return mrp.getMPC_MRP_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param obl
     * @param trxName
     *
     * @return
     */

    public static int MPC_Order_BOMLine( X_MPC_Order_BOMLine obl,String trxName ) {
    	log.fine("En MPC_Order_BOMLINE");
        String sql = new String( "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.Type = 'D' AND mrp.TypeMRP='MOP' AND mrp.MPC_Order_BOMLine_ID = ? " );

        // String sql =  new String("SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.Type = 'D' AND mrp.MPC_Order_BOMLine_ID = ? ");

        MMPCMRP mrp = null;

        // MMPCOrderBOMLine ol = new MMPCOrderBOMLine(Env.getCtx(), MPC_Order_BOMLine_ID);

        X_MPC_Order o = new X_MPC_Order( Env.getCtx(),obl.getMPC_Order_ID(),null );
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,obl.getMPC_Order_BOMLine_ID());

            ResultSet rs      = pstmt.executeQuery();
            boolean   records = false;

            while( rs.next()) {
                records = true;
                mrp     = new MMPCMRP( Env.getCtx(),rs.getInt( 1 ),null );
                mrp.setName( "MRP" );
                mrp.setDescription( o.getDescription());
                mrp.setQty( obl.getQtyRequiered().subtract( obl.getQtyDelivered()));
                log.fine("Ennnn MPC_Order_BOMLINE1, setQty="+obl.getQtyRequiered().subtract( obl.getQtyDelivered())+", getQtyOrdered solo="+obl.getQtyRequiered());
                mrp.setDatePromised( o.getDatePromised());
                mrp.setDateOrdered( o.getDateOrdered());
                mrp.setDateStartSchedule( o.getDateStartSchedule());
                mrp.setDateFinishSchedule( o.getDateFinishSchedule());
                mrp.setM_Product_ID( obl.getM_Product_ID());
                mrp.setM_Warehouse_ID( obl.getM_Warehouse_ID());
                mrp.setS_Resource_ID( o.getS_Resource_ID());
                mrp.setDocStatus( o.getDocStatus());
                mrp.save( trxName );
            }

            if( !records ) {
                mrp = new MMPCMRP( Env.getCtx(),0,null );

                // MOrder o = new MOrder(Env.getCtx(), ol.getC_Order_ID());

                mrp.setMPC_Order_BOMLine_ID( obl.getMPC_Order_BOMLine_ID());
                mrp.setName( "MRP" );
                mrp.setDescription( o.getDescription());
                mrp.setMPC_Order_ID( o.getMPC_Order_ID());
                mrp.setQty( obl.getQtyRequiered().subtract( obl.getQtyDelivered()));
                log.fine("Ennnn MPC_Order_BOMLINE1, setQty="+obl.getQtyRequiered().subtract( obl.getQtyDelivered())+", getQtyOrdered solo="+obl.getQtyRequiered());
                mrp.setDatePromised( o.getDatePromised());
                mrp.setDateOrdered( o.getDateOrdered());
                mrp.setDateStartSchedule( o.getDateStartSchedule());
                mrp.setDateFinishSchedule( o.getDateFinishSchedule());
                mrp.setM_Product_ID( obl.getM_Product_ID());
                mrp.setM_Warehouse_ID( obl.getM_Warehouse_ID());
                mrp.setS_Resource_ID( o.getS_Resource_ID());
                mrp.setDocStatus( o.getDocStatus());
                mrp.setType( "D" );
                mrp.setTypeMRP( "MOP" );
                mrp.save( trxName );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        return mrp.getMPC_MRP_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param rl
     * @param trxName
     * @param delete
     *
     * @return
     */

    public static int M_RequisitionLine( MRequisitionLine rl,String trxName,boolean delete ) {
    	log.fine("En M_RequisitionLine");
        String sql = null;

        if( delete ) {
            sql = "DELETE FROM MPC_MRP WHERE M_RequisitionLine_ID = " + rl.getM_RequisitionLine_ID() + " AND AD_Client_ID = " + rl.getAD_Client_ID();
            DB.executeUpdate( sql,trxName );

            return 0;
        }

        sql = new String( "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.M_RequisitionLine_ID = ? " );

        MRequisition r = new MRequisition( Env.getCtx(),rl.getM_Requisition_ID(),"M_Requisition" );
        MMPCMRP mrp = null;

        // MMPCOrder o = new MMPCOrder(Env.getCtx(), ol.getMPC_Order_ID());

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,rl.getM_RequisitionLine_ID());

            ResultSet rs      = pstmt.executeQuery();
            boolean   records = false;

            while( rs.next()) {
                records = true;
                mrp     = new MMPCMRP( Env.getCtx(),rs.getInt( 1 ),null );
                mrp.setDescription( rl.getDescription());
                mrp.setQty( rl.getQty());
                log.fine("Ennnn M_Requisition_Line1, setQty="+rl.getQty());
                mrp.setDatePromised( r.getDateRequired());
                mrp.setDateStartSchedule( r.getDateRequired());
                mrp.setDateFinishSchedule( r.getDateRequired());
                mrp.setDateOrdered( r.getDateRequired());
                mrp.setM_Product_ID( rl.getM_Product_ID());
                mrp.setM_Warehouse_ID( r.getM_Warehouse_ID());
                mrp.setDocStatus( r.getDocStatus());
                mrp.save( trxName );
            }

            if( !records ) {
                mrp = new MMPCMRP( Env.getCtx(),0,null );
                mrp.setM_Requisition_ID( rl.getM_Requisition_ID());
                mrp.setM_RequisitionLine_ID( rl.getM_RequisitionLine_ID());
                mrp.setDescription( rl.getDescription());
                mrp.setQty( rl.getQty());
                log.fine("Ennnn M_Requisition_Line2, setQty="+rl.getQty());
                mrp.setDatePromised( r.getDateRequired());
                mrp.setDateOrdered( r.getDateRequired());
                mrp.setDateStartSchedule( r.getDateRequired());
                mrp.setDateFinishSchedule( r.getDateRequired());
                mrp.setM_Product_ID( rl.getM_Product_ID());
                mrp.setM_Warehouse_ID( r.getM_Warehouse_ID());
                mrp.setDocStatus( r.getDocStatus());
                mrp.setType( "S" );
                mrp.setTypeMRP( "POR" );
                mrp.setIsAvailable( true );
                mrp.save( trxName );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        return mrp.getMPC_MRP_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param r
     * @param trxName
     *
     * @return
     */

    public static int M_Requisition( MRequisition r,String trxName ) {
    	log.fine("En M_Requisition");
        String sql = new String( "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.M_Requisition_ID = ? " );

        // MRequisition r = new MRequisition(Env.getCtx(), rl.getM_Requisition_ID());

        MMPCMRP mrp = null;

        // MMPCOrder o = new MMPCOrder(Env.getCtx(), ol.getMPC_Order_ID());

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,r.getM_Requisition_ID());

            ResultSet rs      = pstmt.executeQuery();
            boolean   records = false;

            while( rs.next()) {
                records = true;
                mrp     = new MMPCMRP( Env.getCtx(),rs.getInt( 1 ),null );
                mrp.setDocStatus( r.getDocStatus());
                mrp.save( trxName );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );
        }

        return mrp.getMPC_MRP_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param DocBaseType
     * @param IsDefault
     *
     * @return
     */

    public static int getDocType( String DocBaseType,boolean IsDefault ) {
        MDocType[] Doc          = MDocType.getOfDocBaseType( Env.getCtx(),DocBaseType );
        int        C_DocType_ID = 0;

        if( (Doc != null) &&!IsDefault ) {
            C_DocType_ID = Doc[ 0 ].getC_DocType_ID();
        } else if( (Doc != null) && IsDefault ) {
            for( int i = 0;i <= Doc.length;i++ ) {
                if( Doc[ i ].isDefault()) {
                    C_DocType_ID = Doc[ i ].getC_DocType_ID();

                    break;
                }
            }
        }

        return C_DocType_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     *
     * @return
     */

    public static BigDecimal getOnHand( int M_Product_ID ) {
        BigDecimal OnHand = Env.ZERO;

        // e-evolution migracion 252b
        // String sql = "SELECT SUM(BOM_Qty_OnHand (M_Product_ID, M_Warehouse_ID)) AS OnHand FROM MPC_Product_Planning pp WHERE pp.M_Product_ID = " + M_Product_ID ;

        String sql = "SELECT SUM(bomQtyOnHand (M_Product_ID, M_Warehouse_ID,0)) AS OnHand FROM MPC_Product_Planning pp WHERE pp.M_Product_ID = " + M_Product_ID;

        // end e-evolution

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            // pstmt.setInt(1, p_M_Warehouse_ID);

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                OnHand = rs.getBigDecimal( "OnHand" );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"doIt - " + sql,ex );

            return null;
        }

        if( OnHand == null ) {
            OnHand = Env.ZERO;
        }

        return OnHand;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static int getMaxLowLevel() {
        int LowLevel     = 0;
        int AD_Client_ID = Integer.parseInt( Env.getContext( Env.getCtx(),"#AD_Client_ID" ));

        try {
            String sql = "SELECT Max(LowLevel) FROM M_Product WHERE AD_Client_ID = " + AD_Client_ID + " AND LowLevel IS NOT NULL";

            System.out.println( "MaxLowLevel SQL:" + sql );

            PreparedStatement pstmt = null;

            // pstmt.setInt(1, AD_Client_ID);

            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            rs.next();
            LowLevel = rs.getInt( 1 );
            log.info( "MaxLowLevel" + LowLevel );
            rs.close();
            pstmt.close();

            return LowLevel + 1;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"not found MaxLowLevel",ex );

            return LowLevel;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param S_Resource_ID
     * @param AD_Workflow_ID
     * @param QtyOrdered
     *
     * @return
     */

    public static BigDecimal getDays( int S_Resource_ID,int AD_Workflow_ID,BigDecimal QtyOrdered ) {
        if( S_Resource_ID == 0 ) {
            return Env.ZERO;
        }

        MResource S_Resource = new MResource( Env.getCtx(),S_Resource_ID,null );
        MResourceType S_ResourceType = new MResourceType( Env.getCtx(),S_Resource.getS_ResourceType_ID(),null );
        BigDecimal AvailableDayTime = Env.ZERO;
        int        AvailableDays    = 0;
        long       hours            = 0;

        if( S_ResourceType.isDateSlot()) {
            AvailableDayTime = new BigDecimal( getHoursAvailable( S_ResourceType.getTimeSlotStart(),S_ResourceType.getTimeSlotEnd()));
        } else {
            AvailableDayTime = new BigDecimal( 24 );
        }

        if( S_ResourceType.isOnMonday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnTuesday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnThursday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnTuesday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnWednesday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnFriday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnSaturday()) {
            AvailableDays = +1;
        }

        if( S_ResourceType.isOnSunday()) {
            AvailableDays = +1;
        }

        MWorkflow  wf           = new MWorkflow( Env.getCtx(),AD_Workflow_ID,null );
        BigDecimal RequiredTime = Env.ZERO;    // wf.getQueuingTime().add(wf.getSetupTime()).add(wf.getDuration().multiply(QtyOrdered)).add(wf.getWaitingTime()).add(wf.getMovingTime());

        // Weekly Factor

        BigDecimal WeeklyFactor = new BigDecimal( 7 ).divide( new BigDecimal( AvailableDays ),BigDecimal.ROUND_UNNECESSARY );

        return( RequiredTime.multiply( WeeklyFactor )).divide( AvailableDayTime,BigDecimal.ROUND_UP );
    }

    /**
     * Descripción de Método
     *
     *
     * @param time1
     * @param time2
     *
     * @return
     */

    public static long getHoursAvailable( Timestamp time1,Timestamp time2 ) {

        // System.out.println("Start" +  time1);
        // System.out.println("end" +  time2);

        GregorianCalendar g1 = new GregorianCalendar();

        g1.setTimeInMillis( time1.getTime());
        g1.set( Calendar.HOUR_OF_DAY,0 );
        g1.set( Calendar.MINUTE,0 );
        g1.set( Calendar.SECOND,0 );
        g1.set( Calendar.MILLISECOND,0 );

        GregorianCalendar g2 = new GregorianCalendar();

        g2.set( Calendar.HOUR_OF_DAY,0 );
        g2.set( Calendar.MINUTE,0 );
        g2.set( Calendar.SECOND,0 );
        g2.set( Calendar.MILLISECOND,0 );
        g2.setTimeInMillis( time2.getTime());

        // System.out.println("start"+ g1.getTimeInMillis());
        // System.out.println("end"+ g2.getTimeInMillis());

        long difference = g2.getTimeInMillis() - g1.getTimeInMillis();

        // System.out.println("Elapsed milliseconds: " + difference);

        return difference / 6750000;
    }
}    // MPC_MRP



/*
 *  @(#)MMPCMRP.java   02.07.07
 * 
 *  Fin del fichero MMPCMRP.java
 *  
 *  Versión 2.1
 *
 */
