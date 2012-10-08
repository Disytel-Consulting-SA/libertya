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
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MStorage extends X_M_Storage {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param trxName
     *
     * @return
     */

    public static MStorage get( Properties ctx,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,String trxName ) {
        MStorage retValue = null;
        String   sql      = "SELECT * FROM M_Storage " + "WHERE M_Locator_ID=? AND M_Product_ID=? AND M_AttributeSetInstance_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Locator_ID );
            pstmt.setInt( 2,M_Product_ID );
            pstmt.setInt( 3,M_AttributeSetInstance_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MStorage( ctx,rs,trxName );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        if( retValue == null ) {
            s_log.fine( "Not Found - M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID );
        } else {
            s_log.fine( "M_Locator_ID=" + M_Locator_ID + ", M_Product_ID=" + M_Product_ID + ", M_AttributeSetInstance_ID=" + M_AttributeSetInstance_ID );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param M_Locator_ID
     * @param FiFo
     * @param trxName
     *
     * @return
     */

    public static MStorage[] getAll( Properties ctx,int M_Product_ID,int M_Locator_ID,boolean FiFo,String trxName ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM M_Storage " + "WHERE M_Product_ID=? AND M_Locator_ID=?" + " AND M_AttributeSetInstance_ID >= 0" + " AND QtyOnHand > 0 " + "ORDER BY M_AttributeSetInstance_ID";

        if( !FiFo ) {
            sql += " DESC";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );
            pstmt.setInt( 2,M_Locator_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MStorage( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        MStorage[] retValue = new MStorage[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getAll

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Product_ID
     * @param trxName
     *
     * @return
     */

    public static MStorage[] getOfProduct( Properties ctx,int M_Product_ID,String trxName ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM M_Storage " + "WHERE M_Product_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                list.add( new MStorage( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        MStorage[] retValue = new MStorage[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfProduct

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Warehouse_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param M_AttributeSet_ID
     * @param allAttributeInstances
     * @param minGuaranteeDate
     * @param FiFo
     * @param trxName
     *
     * @return
     */

    public static MStorage[] getWarehouse( Properties ctx,int M_Warehouse_ID,int M_Product_ID,int M_AttributeSetInstance_ID,int M_AttributeSet_ID,boolean allAttributeInstances,Timestamp minGuaranteeDate,boolean FiFo,String trxName ) {
        if( (M_Warehouse_ID == 0) || (M_Product_ID == 0) ) {
            return new MStorage[ 0 ];
        }

        if( M_AttributeSet_ID == 0 ) {
            allAttributeInstances = true;
        } else {
            MAttributeSet mas = MAttributeSet.get( ctx,M_AttributeSet_ID );

            if( !mas.isInstanceAttribute()) {
                allAttributeInstances = true;
            }
        }

        ArrayList list = new ArrayList();

        // Specific Attribute Set Instance

        String sql = "SELECT s.M_Product_ID,s.M_Locator_ID,s.M_AttributeSetInstance_ID," + "s.AD_Client_ID,s.AD_Org_ID,s.IsActive,s.Created,s.CreatedBy,s.Updated,s.UpdatedBy," + "s.QtyOnHand,s.QtyReserved,s.QtyOrdered,s.DateLastInventory " + "FROM M_Storage s" + " INNER JOIN M_Locator l ON (l.M_Locator_ID=s.M_Locator_ID) " + "WHERE l.M_Warehouse_ID=?" + " AND s.M_Product_ID=?" + " AND COALESCE(s.M_AttributeSetInstance_ID,0)=? " + "ORDER BY l.PriorityNo DESC, M_AttributeSetInstance_ID";

        if( !FiFo ) {
            sql += " DESC";
        }

        // All Attribute Set Instances

        if( allAttributeInstances ) {
            sql = "SELECT s.M_Product_ID,s.M_Locator_ID,s.M_AttributeSetInstance_ID," + "s.AD_Client_ID,s.AD_Org_ID,s.IsActive,s.Created,s.CreatedBy,s.Updated,s.UpdatedBy," + "s.QtyOnHand,s.QtyReserved,s.QtyOrdered,s.DateLastInventory " + "FROM M_Storage s" + " INNER JOIN M_Locator l ON (l.M_Locator_ID=s.M_Locator_ID)" + " LEFT OUTER JOIN M_AttributeSetInstance asi ON (s.M_AttributeSetInstance_ID=asi.M_AttributeSetInstance_ID) " + "WHERE l.M_Warehouse_ID=?" + " AND s.M_Product_ID=? ";

            if( minGuaranteeDate != null ) {
                sql += "AND (asi.GuaranteeDate IS NULL OR asi.GuaranteeDate>?) " + "ORDER BY asi.GuaranteeDate, M_AttributeSetInstance_ID";

                if( !FiFo ) {
                    sql += " DESC";
                }

                sql += ",l.PriorityNo DESC";
            } else {
                sql += "ORDER BY l.PriorityNo DESC, M_AttributeSetInstance_ID";

                if( !FiFo ) {
                    sql += " DESC";
                }
            }
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Warehouse_ID );
            pstmt.setInt( 2,M_Product_ID );

            if( !allAttributeInstances ) {
                pstmt.setInt( 3,M_AttributeSetInstance_ID );
            } else if( minGuaranteeDate != null ) {
                pstmt.setTimestamp( 3,minGuaranteeDate );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MStorage( ctx,rs,trxName ));
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

        MStorage[] retValue = new MStorage[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getWarehouse

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param trxName
     *
     * @return
     */

    public static MStorage getCreate( Properties ctx,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,String trxName ) {
        if( M_Locator_ID == 0 ) {
            throw new IllegalArgumentException( "M_Locator_ID=0" );
        }

        if( M_Product_ID == 0 ) {
            throw new IllegalArgumentException( "M_Product_ID=0" );
        }

        MStorage retValue = get( ctx,M_Locator_ID,M_Product_ID,M_AttributeSetInstance_ID,trxName );

        if( retValue != null ) {
            return retValue;
        }

        // Insert row based on locator

        MLocator locator = new MLocator( ctx,M_Locator_ID,trxName );

        if( locator.getID() != M_Locator_ID ) {
            throw new IllegalArgumentException( "Not found M_Locator_ID=" + M_Locator_ID );
        }

        //

        retValue = new MStorage( locator,M_Product_ID,M_AttributeSetInstance_ID );
        retValue.save( trxName );
        s_log.fine( "New " + retValue );

        return retValue;
    }    // getCreate

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_Warehouse_ID
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param reservationAttributeSetInstance_ID
     * @param diffQtyOnHand
     * @param diffQtyReserved
     * @param diffQtyOrdered
     * @param trxName
     *
     * @return
     */

    public static boolean add( Properties ctx,int M_Warehouse_ID,int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,int reservationAttributeSetInstance_ID,BigDecimal diffQtyOnHand,BigDecimal diffQtyReserved,BigDecimal diffQtyOrdered,String trxName ) {
        MStorage     storage  = null;
        StringBuffer diffText = new StringBuffer( "(" );

        // System.out.println( " War: " + M_Warehouse_ID + " Loc: " + M_Locator_ID + " Prod: " + M_Product_ID + " MASI: " + M_AttributeSetInstance_ID + " RASI: " + reservationAttributeSetInstance_ID + " QH: " + diffQtyOnHand + " QR: " + diffQtyReserved + " QO: " + diffQtyOrdered );
        
        // Get Storage

        if( storage == null ) {
            storage = getCreate( ctx,M_Locator_ID,M_Product_ID,M_AttributeSetInstance_ID,trxName );
        }

        // Verify

        if( (storage.getM_Locator_ID() != M_Locator_ID) && (storage.getM_Product_ID() != M_Product_ID) && (storage.getM_AttributeSetInstance_ID() != M_AttributeSetInstance_ID) ) {
            s_log.severe( "No Storage found - M_Locator_ID=" + M_Locator_ID + ",M_Product_ID=" + M_Product_ID + ",ASI=" + M_AttributeSetInstance_ID );

            return false;
        }

        MStorage storage0 = null;

        if( M_AttributeSetInstance_ID != reservationAttributeSetInstance_ID ) {
            storage0 = get( ctx,M_Locator_ID,M_Product_ID,reservationAttributeSetInstance_ID,trxName );

            if( storage0 == null )    // create if not existing - should not happen
            {
                MWarehouse wh            = MWarehouse.get( ctx,M_Warehouse_ID );
                int        xM_Locator_ID = wh.getDefaultLocator().getM_Locator_ID();

                storage0 = getCreate( ctx,xM_Locator_ID,M_Product_ID,reservationAttributeSetInstance_ID,trxName );
            }
        }

        boolean changed = false;

        if( (diffQtyOnHand != null) && (diffQtyOnHand.compareTo( Env.ZERO ) != 0) ) {
            storage.setQtyOnHand( storage.getQtyOnHand().add( diffQtyOnHand ));
            diffText.append( "OnHand=" ).append( diffQtyOnHand );
            changed = true;
        }

        if( (diffQtyReserved != null) && (diffQtyReserved.compareTo( Env.ZERO ) != 0) ) {
            if( storage0 == null ) {
                storage.setQtyReserved( storage.getQtyReserved().add( diffQtyReserved ));
            } else {
                storage0.setQtyReserved( storage0.getQtyReserved().add( diffQtyReserved ));
            }

            diffText.append( " Reserved=" ).append( diffQtyReserved );
            changed = true;
        }

        if( (diffQtyOrdered != null) && (diffQtyOrdered.compareTo( Env.ZERO ) != 0) ) {
            if( storage0 == null ) {
                storage.setQtyOrdered( storage.getQtyOrdered().add( diffQtyOrdered ));
            } else {
                storage0.setQtyOrdered( storage0.getQtyOrdered().add( diffQtyOrdered ));
            }

            diffText.append( " Ordered=" ).append( diffQtyOrdered );
            changed = true;
        }

        if( changed ) {
            diffText.append( ") -> " ).append( storage.toString());
            s_log.fine( diffText.toString());

            if( storage0 != null ) {
                storage0.save( trxName );    // No AttributeSetInstance (reserved/ordered)
            }

            return storage.save( trxName );
        }

        return true;
    }    // add

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param Qty
     * @param trxName
     *
     * @return
     */

    public static int getM_Locator_ID( int M_Warehouse_ID,int M_Product_ID,int M_AttributeSetInstance_ID,BigDecimal Qty,String trxName ) {
        int    M_Locator_ID      = 0;
        int    firstM_Locator_ID = 0;
        String sql               = "SELECT s.M_Locator_ID, s.QtyOnHand " + "FROM M_Storage s" + " INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID)" + " INNER JOIN M_Product p ON (s.M_Product_ID=p.M_Product_ID)" + " LEFT OUTER JOIN M_AttributeSet mas ON (p.M_AttributeSet_ID=mas.M_AttributeSet_ID) " + "WHERE l.M_Warehouse_ID=?" + " AND s.M_Product_ID=?" + " AND (mas.IsInstanceAttribute IS NULL OR mas.IsInstanceAttribute='N' OR s.M_AttributeSetInstance_ID=?)" + " AND l.IsActive='Y' " + "ORDER BY l.PriorityNo DESC, s.QtyOnHand DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Warehouse_ID );
            pstmt.setInt( 2,M_Product_ID );
            pstmt.setInt( 3,M_AttributeSetInstance_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                BigDecimal QtyOnHand = rs.getBigDecimal( 2 );

                if( (QtyOnHand != null) && (Qty.compareTo( QtyOnHand ) <= 0) ) {
                    M_Locator_ID = rs.getInt( 1 );

                    break;
                }

                if( firstM_Locator_ID == 0 ) {
                    firstM_Locator_ID = rs.getInt( 1 );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            s_log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        if( M_Locator_ID != 0 ) {
            return M_Locator_ID;
        }

        return firstM_Locator_ID;
    }    // getM_Locator_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     * @param M_Product_ID
     * @param trxName
     *
     * @return
     */

    public static BigDecimal getQtyAvailable( int M_Warehouse_ID,int M_Product_ID,String trxName ) {
        BigDecimal        retValue = null;
        PreparedStatement pstmt    = null;
        String            sql      = "SELECT SUM(QtyOnHand-QtyReserved) " + "FROM M_Storage s" + " INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) " + "WHERE s.M_Product_ID=?" + " AND l.M_Warehouse_ID=?";

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,M_Product_ID );
            pstmt.setInt( 2,M_Warehouse_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getBigDecimal( 1 );

                if( rs.wasNull()) {
                    retValue = null;
                }
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

        s_log.fine( "M_Warehouse_ID=" + M_Warehouse_ID + ",M_Product_ID=" + M_Product_ID + " = " + retValue );

        return retValue;
    }    // getQtyAvailable

    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     * @param M_Product_ID
     *
     * @return
     */    
    
	public static BigDecimal getQtyAvailable (int M_Warehouse_ID, int M_Product_ID)
	{
		BigDecimal retValue = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT SUM(QtyOnHand-QtyReserved) "
			+ "FROM M_Storage s"
			+ " INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) "
			+ "WHERE s.M_Product_ID=?";
		if (M_Warehouse_ID !=0){sql += " AND l.M_Warehouse_ID=?";}
		try
		{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt (1, M_Product_ID);
			if (M_Warehouse_ID !=0){pstmt.setInt (2, M_Warehouse_ID);}
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				retValue = rs.getBigDecimal(1);
				if (rs.wasNull())
					retValue = null;
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			//s_log.error ("getAvailableQty", e);
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		//s_log.debug("getQtyAvailable - M_Warehouse_ID=" + M_Warehouse_ID + ",M_Product_ID=" + M_Product_ID + " = " + retValue);
		return retValue;
	}	//	getQtyAvailable    

	public static BigDecimal getQtyAvailable (int M_Warehouse_ID, int M_Product_ID, int M_AttributeSetInstance_ID, String trxName) {
		String sql =  "SELECT COALESCE(SUM(QtyOnHand-QtyReserved), 0.0) " + 
			" FROM M_Storage " + 
			" INNER JOIN M_Locator ON (M_Locator.M_Locator_ID=M_Storage.M_Locator_ID) " +
			" WHERE M_AttributeSetInstance_ID = ? AND M_Product_ID = ? AND M_Locator.M_Warehouse_ID = ? ";
		return (BigDecimal)DB.getSQLObject(trxName, sql, new Object[]{M_AttributeSetInstance_ID, M_Product_ID, M_Warehouse_ID});
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param M_Warehouse_ID
     * @param M_Product_ID
     *
     * @return
     */    	
	
	public static BigDecimal getQtyVirtual (int M_Warehouse_ID, int M_Product_ID)
	{
		BigDecimal retValue = null;
		PreparedStatement pstmt = null;
		String sql = "SELECT SUM(QtyOrdered) "
			+ "FROM M_Storage s"
			+ " INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) "
			+ "WHERE s.M_Product_ID=?";
		if (M_Warehouse_ID !=0){sql += " AND l.M_Warehouse_ID=?";}
		try
		{
			pstmt = DB.prepareStatement (sql);
			pstmt.setInt (1, M_Product_ID);
			if (M_Warehouse_ID !=0){pstmt.setInt (2, M_Warehouse_ID);}
			ResultSet rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				retValue = rs.getBigDecimal(1);
				if (rs.wasNull())
					retValue = null;
			}
			rs.close ();
			pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			//s_log.error ("getVirtualQty", e);
		}
		try
		{
			if (pstmt != null)
				pstmt.close ();
			pstmt = null;
		}
		catch (Exception e)
		{
			pstmt = null;
		}
		//s_log.debug("getQtyVirtual - M_Warehouse_ID=" + M_Warehouse_ID + ",M_Product_ID=" + M_Product_ID + " = " + retValue);
		return retValue;
	}	//	getQtyVirtual	
	
    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MStorage( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }

        //

        setQtyOnHand( Env.ZERO );
        setQtyOrdered( Env.ZERO );
        setQtyReserved( Env.ZERO );
    }    // MStorage

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MStorage( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MStorage

    /**
     * Constructor de la clase ...
     *
     *
     * @param locator
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     */

    private MStorage( MLocator locator,int M_Product_ID,int M_AttributeSetInstance_ID ) {
        this( locator.getCtx(),0,locator.get_TrxName());
        setClientOrg( locator );
        setM_Locator_ID( locator.getM_Locator_ID());
        setM_Product_ID( M_Product_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
    }    // MStorage

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MStorage.class );

    /** Descripción de Campos */

    private int m_M_Warehouse_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @param qty
     * @param add
     */

    public void changeQtyOnHand( BigDecimal qty,boolean add ) {
        if( (qty == null) || (qty.compareTo( Env.ZERO ) == 0) ) {
            return;
        }

        if( add ) {
            setQtyOnHand( getQtyOnHand().add( qty ));
        } else {
            setQtyOnHand( getQtyOnHand().subtract( qty ));
        }
    }    // changeQtyOnHand

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_Warehouse_ID() {
        if( m_M_Warehouse_ID == 0 ) {
            MLocator loc = MLocator.get( getCtx(),getM_Locator_ID());

            m_M_Warehouse_ID = loc.getM_Warehouse_ID();
        }

        return m_M_Warehouse_ID;
    }    // getM_Warehouse_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MStorage[" ).append( "M_Locator_ID=" ).append( getM_Locator_ID()).append( ",M_Product_ID=" ).append( getM_Product_ID()).append( ",M_AttributeSetInstance_ID=" ).append( getM_AttributeSetInstance_ID()).append( ": OnHand=" ).append( getQtyOnHand()).append( ",Reserved=" ).append( getQtyReserved()).append( ",Ordered=" ).append( getQtyOrdered()).append( "]" );

        return sb.toString();
    }    // toString
}    // MStorage



/*
 *  @(#)MStorage.java   02.07.07
 * 
 *  Fin del fichero MStorage.java
 *  
 *  Versión 2.2
 *
 */
