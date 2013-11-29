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
import java.util.logging.Level;

import org.openXpertya.model.MAttributeSet;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MInventoryLine;
import org.openXpertya.model.MInventoryLineMA;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ErrorOXPSystem;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InventoryCountCreate extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Inventory_ID = 0;

    /** Descripción de Campos */

    private MInventory m_inventory = null;

    /** Descripción de Campos */

    private int p_M_Locator_ID = 0;

    /** Descripción de Campos */

    private String p_LocatorValue = null;

    /** Descripción de Campos */

    private String p_ProductValue = null;

    /** Descripción de Campos */

    private int p_M_Product_Category_ID = 0;

    /** Familia */
    private int p_M_Product_Gamas_ID = 0;
    
    /** Línea de Artículo */
    private int p_M_Product_Lines_ID = 0;
    
    /** Descripción de Campos */

    private String p_QtyRange = null;

    /** Descripción de Campos */

    private boolean p_DeleteOld = false;
    
    /** Cantidad contada en cero */
    private boolean p_ZeroQtyCount = false;
    
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
            } else if( name.equals( "M_Locator_ID" )) {
                p_M_Locator_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "LocatorValue" )) {
                p_LocatorValue = ( String )para[ i ].getParameter();
            } else if( name.equals( "ProductValue" )) {
                p_ProductValue = ( String )para[ i ].getParameter();
            } else if( name.equals( "M_Product_Category_ID" )) {
                p_M_Product_Category_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_Product_Gamas_ID" )) {
            	p_M_Product_Gamas_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "M_Product_Lines_ID" )) {
            	p_M_Product_Lines_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "QtyRange" )) {
                p_QtyRange = ( String )para[ i ].getParameter();
            } else if( name.equals( "DeleteOld" )) {
                p_DeleteOld = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "ZeroQtyCount" )) {
                p_ZeroQtyCount = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_M_Inventory_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "M_Inventory_ID=" + p_M_Inventory_ID + ", M_Locator_ID=" + p_M_Locator_ID + ", LocatorValue=" + p_LocatorValue + ", ProductValue=" + p_ProductValue + ", M_Product_Category_ID=" + p_M_Product_Category_ID + ", QtyRange=" + p_QtyRange + ", DeleteOld=" + p_DeleteOld );
        m_inventory = new MInventory( getCtx(),p_M_Inventory_ID,get_TrxName());

        if( m_inventory.getID() == 0 ) {
            throw new ErrorOXPSystem( "Not found: M_Inventory_ID=" + p_M_Inventory_ID );
        }

        if( m_inventory.isProcessed()) {
            throw new ErrorOXPSystem( "@M_Inventory_ID@ @Processed@" );
        }

        //

        if( p_DeleteOld ) {
        	String sql = "DELETE M_InventoryLineMA WHERE M_InventoryLine_ID IN (SELECT M_InventoryLine_ID FROM M_InventoryLine WHERE M_Inventory_ID=" + p_M_Inventory_ID+")";
        	int no = DB.executeUpdate( sql,get_TrxName());
        	log.fine( "doIt - M_InventoryLineMA Deleted #" + no );
        	
            sql = "DELETE M_InventoryLine WHERE Processed='N' " + "AND M_Inventory_ID=" + p_M_Inventory_ID;
            no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "doIt - M_InventoryLine Deleted #" + no );
        }

        // Create Null Storage records

        if( (p_QtyRange != null) && p_QtyRange.equals( "=" )) {
            String sql = "INSERT INTO M_Storage " + "(AD_Client_ID, AD_Org_ID, IsActive, Created, CreatedBy, Updated, UpdatedBy," + " M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID," + " QtyOnHand, QtyReserved, QtyOrdered, DateLastInventory) " + "SELECT l.AD_CLIENT_ID, l.AD_ORG_ID, 'Y', SysDate, 0,SysDate, 0," + " l.M_Locator_ID, p.M_Product_ID, 0," + " 0,0,0,null " + "FROM M_Locator l" + " INNER JOIN M_Product p ON (l.AD_Client_ID=p.AD_Client_ID) " + "WHERE l.M_Warehouse_ID=" + m_inventory.getM_Warehouse_ID();

            if( p_M_Locator_ID != 0 ) {
                sql += " AND l.M_Locator_ID=" + p_M_Locator_ID;
            }

            sql += " AND l.IsDefault='Y'" + " AND p.IsActive='Y' AND p.IsStocked='Y' and p.ProductType='I'" + " AND NOT EXISTS (SELECT * FROM M_Storage s" + " INNER JOIN M_Locator sl ON (s.M_Locator_ID=sl.M_Locator_ID) " + "WHERE sl.M_Warehouse_ID=l.M_Warehouse_ID" + " AND s.M_Product_ID=p.M_Product_ID)";

            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "'0' Inserted #" + no );
        }

        StringBuffer sql = new StringBuffer( "SELECT p.M_Product_ID, s.M_Locator_ID, s.M_AttributeSetInstance_ID," + " s.QtyOnHand, p.M_AttributeSet_ID " 
        									+ " FROM (SELECT p.M_Product_ID, p.Value, p.Name, p.M_AttributeSet_ID "
        									+ "			FROM M_Product p "
        									+ " 		INNER JOIN M_Product_Category pc ON (pc.M_Product_Category_ID=p.M_Product_Category_ID) "
        									+ " 		LEFT JOIN M_Product_Gamas pg ON (pg.M_Product_Gamas_ID=pc.M_Product_Gamas_ID) "
        									+ " 		LEFT JOIN M_Product_Lines pl ON (pl.M_Product_Lines_ID=pg.M_Product_Lines_ID) "
        									+ "			WHERE p.IsActive='Y' AND p.IsStocked='Y' AND p.ProductType='I' ");
        
        if( (p_ProductValue != null) && ( (p_ProductValue.trim().length() == 0) || p_ProductValue.equals( "%" ))) {
            p_ProductValue = null;
        }

        if( p_ProductValue != null ) {
            sql.append( " AND UPPER(p.Value) LIKE ?" );
        }

        //

        if( p_M_Product_Category_ID != 0 ) {
            sql.append( " AND pc.M_Product_Category_ID=?" );
        }
        
        if( p_M_Product_Gamas_ID != 0 ) {
            sql.append( " AND pg.M_Product_Gamas_ID=?" );
        }
        
        if( p_M_Product_Lines_ID != 0 ) {
            sql.append( " AND pl.M_Product_Lines_ID=?" );
        }
        
        sql.append(") p "); 
        sql.append(" LEFT JOIN (SELECT s.M_Product_ID, l.M_Warehouse_ID, s.M_Locator_ID, l.Value, s.M_AttributeSetInstance_ID, sum(s.QtyOnHand) QtyOnHand " +
        						" FROM M_Storage s " +
        						" INNER JOIN M_Locator l ON (s.M_Locator_ID=l.M_Locator_ID) " +
        						" WHERE l.M_Warehouse_ID=? ");
        
        if( p_M_Locator_ID != 0 ) {
            sql.append( " AND s.M_Locator_ID=?" );
        }

        //

        if( (p_LocatorValue != null) && ( (p_LocatorValue.trim().length() == 0) || p_LocatorValue.equals( "%" ))) {
            p_LocatorValue = null;
        }

        if( p_LocatorValue != null ) {
            sql.append( " AND UPPER(l.Value) LIKE ?" );
        }
        
        sql.append(" GROUP BY s.M_Product_ID, l.M_Warehouse_ID, s.M_Locator_ID, l.Value, s.M_AttributeSetInstance_ID ");
        sql.append(" ) s ON (s.M_Product_ID=p.M_Product_ID) "); 
        
        sql.append(" WHERE 1=1 ");
        
        // Do not overwrite existing records

        if( !p_DeleteOld ) {
            sql.append( " AND NOT EXISTS (SELECT * FROM M_InventoryLine il " + "WHERE il.M_Inventory_ID=?" + " AND il.M_Product_ID=s.M_Product_ID" + " AND il.M_Locator_ID=s.M_Locator_ID" + " AND COALESCE(il.M_AttributeSetInstance_ID,0)=COALESCE(s.M_AttributeSetInstance_ID,0))" );
        }

        //

        sql.append( " ORDER BY s.Value, p.Value" );    // Locator/Product

        //

        int               count = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString());

            int index = 1;

            if( p_ProductValue != null ) {
                pstmt.setString( index++,p_ProductValue.toUpperCase());
            }

            if( p_M_Product_Category_ID != 0 ) {
                pstmt.setInt( index++,p_M_Product_Category_ID );
            }
            
            if( p_M_Product_Gamas_ID != 0 ) {
                pstmt.setInt( index++,p_M_Product_Gamas_ID );
            }
            
            if( p_M_Product_Lines_ID != 0 ) {
                pstmt.setInt( index++,p_M_Product_Lines_ID );
            }
            
            pstmt.setInt( index++,m_inventory.getM_Warehouse_ID());

            if( p_M_Locator_ID != 0 ) {
                pstmt.setInt( index++,p_M_Locator_ID );
            }

            if( p_LocatorValue != null ) {
                pstmt.setString( index++,p_LocatorValue.toUpperCase());
            }

            if( !p_DeleteOld ) {
                pstmt.setInt( index++,p_M_Inventory_ID );
            }

			Integer defaultLocatorID = MWarehouse.getDefaultLocatorID(
					m_inventory.getM_Warehouse_ID(), get_TrxName());
            
            ResultSet rs = pstmt.executeQuery();
            
            while( rs.next()) {
                int        M_Product_ID              = rs.getInt( 1 );
                int        M_Locator_ID              = rs.getInt( 2 );
                int        M_AttributeSetInstance_ID = rs.getInt( 3 );
                BigDecimal QtyOnHand                 = rs.getBigDecimal( 4 );

                if( QtyOnHand == null ) {
                    QtyOnHand = Env.ZERO;
                }

                M_Locator_ID = Util.isEmpty(M_Locator_ID, true)?defaultLocatorID:M_Locator_ID;
                
                int M_AttributeSet_ID = rs.getInt( 5 );

                //

                int compare = QtyOnHand.compareTo( Env.ZERO );

                if( (p_QtyRange == null) || (p_QtyRange.equals("")) || ( p_QtyRange.equals( ">" ) && (compare > 0) ) || ( p_QtyRange.equals( "<" ) && (compare < 0) ) || ( p_QtyRange.equals( "=" ) && (compare == 0) ) || ( p_QtyRange.equals( "N" ) && (compare != 0) ) ) {
                    count += createInventoryLine( M_Locator_ID,M_Product_ID,M_AttributeSetInstance_ID,M_AttributeSet_ID,QtyOnHand );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql.toString(),e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        return "@M_InventoryLine_ID@ - #" + count;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param M_Locator_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param M_AttributeSet_ID
     * @param QtyOnHand
     *
     * @return
     */

    private int createInventoryLine( int M_Locator_ID,int M_Product_ID,int M_AttributeSetInstance_ID,int M_AttributeSet_ID,BigDecimal QtyOnHand ) {
        int asi = M_AttributeSetInstance_ID;

        if( M_AttributeSet_ID == 0 ) {
            asi = 0;
        } else {
            MAttributeSet mas = MAttributeSet.get( getCtx(),M_AttributeSet_ID );

            if( !mas.isInstanceAttribute()) {
                asi = 0;
            }
        }

        MInventoryLine line = MInventoryLine.get( m_inventory,M_Locator_ID,M_Product_ID,asi );
        boolean added = false;

        if( line == null ) {
            line = new MInventoryLine( m_inventory,M_Locator_ID,M_Product_ID,M_AttributeSetInstance_ID,QtyOnHand,p_ZeroQtyCount?BigDecimal.ZERO:QtyOnHand );    // book/count
            added = true;
        } else {
            BigDecimal qty = line.getQtyBook().add( QtyOnHand );

            line.setQtyBook( qty );
            line.setQtyCount( p_ZeroQtyCount?BigDecimal.ZERO:qty );
        }

        boolean success = line.save();

        // Add MA

        if( success && (asi == 0) ) {
            MInventoryLineMA ma = new MInventoryLineMA( line,M_AttributeSetInstance_ID,QtyOnHand );

            if( !ma.save()) {
                ;
            }
        }

        if( success && added ) {
            return 1;
        }

        return 0;
    }    // createInventoryLine
}    // InventoryCountCreate



/*
 *  @(#)InventoryCountCreate.java   02.07.07
 * 
 *  Fin del fichero InventoryCountCreate.java
 *  
 *  Versión 2.2
 *
 */
