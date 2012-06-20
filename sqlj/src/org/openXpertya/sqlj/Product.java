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



package org.openXpertya.sqlj;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Product {

    /**
     * Descripción de Método
     *
     *
     * @param p_M_AttributeSetInstance_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static String attributeName( int p_M_AttributeSetInstance_ID ) throws SQLException {
        if( p_M_AttributeSetInstance_ID == 0 ) {
            return "";
        }

        //

        StringBuffer sb = new StringBuffer();

        // Get Base Info

        String sql = "SELECT asi.Lot, asi.SerNo, asi.GuaranteeDate " + "FROM M_AttributeSetInstance asi " + "WHERE asi.M_AttributeSetInstance_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_M_AttributeSetInstance_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            String lot = rs.getString( 1 );

            if( (lot != null) && (lot.length() > 0) ) {
                sb.append( lot ).append( " " );
            }

            String serNo = rs.getString( 1 );

            if( (serNo != null) && (serNo.length() > 0) ) {
                sb.append( "#" ).append( serNo ).append( " " );
            }

            Timestamp guarantee = rs.getTimestamp( 3 );

            if( guarantee != null ) {
                sb.append( guarantee ).append( " " );
            }
        }

        rs.close();
        pstmt.close();

        // Get Instance Info

        sql = "SELECT ai.Value, a.Name " + "FROM M_AttributeInstance ai" + " INNER JOIN M_Attribute a ON (ai.M_Attribute_ID=a.M_Attribute_ID AND a.IsInstanceAttribute='Y') " + "WHERE ai.M_AttributeSetInstance_ID=?";
        pstmt = OpenXpertya.prepareStatement( sql );
        pstmt.setInt( 1,p_M_AttributeSetInstance_ID );
        rs = pstmt.executeQuery();

        while( rs.next()) {
            sb.append( rs.getString( 1 )).append( ":" ).append( rs.getString( 2 )).append( " " );
        }

        rs.close();
        pstmt.close();

        if( sb.length() == 0 ) {
            return "";
        }

        sb.insert( 0," (" );
        sb.append( ")" );

        return sb.toString();
    }    // getAttributeName

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomPriceLimit( int p_M_Product_ID,int p_M_PriceList_Version_ID ) throws SQLException {
        return bomPrice( p_M_Product_ID,p_M_PriceList_Version_ID,"PriceLimit" );
    }    // bomPriceLimit

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomPriceList( int p_M_Product_ID,int p_M_PriceList_Version_ID ) throws SQLException {
        return bomPrice( p_M_Product_ID,p_M_PriceList_Version_ID,"PriceList" );
    }    // bomPriceList

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomPriceStd( int p_M_Product_ID,int p_M_PriceList_Version_ID ) throws SQLException {
        return bomPrice( p_M_Product_ID,p_M_PriceList_Version_ID,"PriceStd" );
    }    // bomPriceStd

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomPriceLimit( int p_M_Product_ID,int p_M_PriceList_Version_ID, int p_M_AttributeSetInstance_ID ) throws SQLException {
        return bomPrice( p_M_Product_ID,p_M_PriceList_Version_ID,p_M_AttributeSetInstance_ID,"PriceLimit" );
    }    // bomPriceLimit

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomPriceList( int p_M_Product_ID,int p_M_PriceList_Version_ID, int p_M_AttributeSetInstance_ID ) throws SQLException {
        return bomPrice( p_M_Product_ID,p_M_PriceList_Version_ID,p_M_AttributeSetInstance_ID,"PriceList" );
    }    // bomPriceList

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomPriceStd( int p_M_Product_ID,int p_M_PriceList_Version_ID, int p_M_AttributeSetInstance_ID ) throws SQLException {
        return bomPrice( p_M_Product_ID,p_M_PriceList_Version_ID,p_M_AttributeSetInstance_ID,"PriceStd" );
    }    // bomPriceStd

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_PriceList_Version_ID
     * @param p_what
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal bomPrice( int p_M_Product_ID, int p_M_PriceList_Version_ID, String p_what ) throws SQLException {
    	return bomPrice(p_M_Product_ID, p_M_PriceList_Version_ID, 0, p_what);
    }
    
    static BigDecimal bomPrice( int p_M_Product_ID, int p_M_PriceList_Version_ID, int p_M_AttributeSetInstance_ID, String p_what ) throws SQLException {
        BigDecimal price = null;

        // Try to get price from PriceList directly
        
        String sql;
        
        if (p_M_AttributeSetInstance_ID != 0)
        	sql = "SELECT " + p_what + " FROM M_ProductPriceInstance WHERE M_PriceList_Version_ID=? AND M_Product_ID=? AND M_AttributeSetInstance_ID=?";
        else
        	sql = "SELECT " + p_what + " FROM M_ProductPrice WHERE M_PriceList_Version_ID=? AND M_Product_ID=? ";
        
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        int pn = 1;
        
        pstmt.setInt( pn++, p_M_PriceList_Version_ID );
        pstmt.setInt( pn++, p_M_Product_ID );
        
        if (p_M_AttributeSetInstance_ID != 0)
        	pstmt.setInt( pn++, p_M_AttributeSetInstance_ID );
        
        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            price = rs.getBigDecimal( 1 );
        }

        rs.close();
        pstmt.close();

        // Loop through BOM

        if( (price == null) || (price.signum() == 0) ) {
            price = OpenXpertya.ZERO;
            sql   = "SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM " + "FROM M_Product_BOM b, M_Product p " + "WHERE b.M_ProductBOM_ID=p.M_Product_ID" + " AND b.M_Product_ID=?";
            pstmt = OpenXpertya.prepareStatement( sql );
            pstmt.setInt( 1,p_M_Product_ID );
            rs = pstmt.executeQuery();

            while( rs.next()) {
                int        M_ProductBOM_ID = rs.getInt( 1 );
                BigDecimal qty             = rs.getBigDecimal( 2 );
                BigDecimal productPrice    = bomPrice( M_ProductBOM_ID,p_M_PriceList_Version_ID,p_M_AttributeSetInstance_ID,p_what );

                productPrice = productPrice.multiply( qty );
                price        = price.add( productPrice );
            }

            rs.close();
            pstmt.close();
        }

        // If there's no price in the list wich matches the attribute set instance, try looking one without it. 
        
        if( ( (price == null) || (price.signum() == 0) ) && p_M_AttributeSetInstance_ID != 0 ) {
        	price = bomPrice(p_M_Product_ID, p_M_PriceList_Version_ID, 0, p_what);
        }
        
        return price;
    }    // bomPrice

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_Warehouse_ID
     * @param p_M_Locator_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomQtyAvailable( int p_M_Product_ID,int p_M_Warehouse_ID,int p_M_Locator_ID ) throws SQLException {
        return bomQty( p_M_Product_ID,p_M_Warehouse_ID,p_M_Locator_ID,"QtyOnHand" ).subtract( bomQty( p_M_Product_ID,p_M_Warehouse_ID,p_M_Locator_ID,"QtyReserved" ));
    }    // bomQtyAvailable

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_Warehouse_ID
     * @param p_M_Locator_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomQtyOnHand( int p_M_Product_ID,int p_M_Warehouse_ID,int p_M_Locator_ID ) throws SQLException {
        return bomQty( p_M_Product_ID,p_M_Warehouse_ID,p_M_Locator_ID,"QtyOnHand" );
    }    // bomQtyOnHand

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_Warehouse_ID
     * @param p_M_Locator_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomQtyOrdered( int p_M_Product_ID,int p_M_Warehouse_ID,int p_M_Locator_ID ) throws SQLException {
        return bomQty( p_M_Product_ID,p_M_Warehouse_ID,p_M_Locator_ID,"QtyOrdered" );
    }    // bomQtyOrdered

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_Warehouse_ID
     * @param p_M_Locator_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal bomQtyReserved( int p_M_Product_ID,int p_M_Warehouse_ID,int p_M_Locator_ID ) throws SQLException {
        return bomQty( p_M_Product_ID,p_M_Warehouse_ID,p_M_Locator_ID,"QtyReserved" );
    }    // bomQtyReserved

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param p_M_Warehouse_ID
     * @param p_M_Locator_ID
     * @param p_what
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal bomQty( int p_M_Product_ID,int p_M_Warehouse_ID,int p_M_Locator_ID,String p_what ) throws SQLException {
    	
        // Check Parameters

        int M_Warehouse_ID = p_M_Warehouse_ID;

        if( M_Warehouse_ID == 0 )
        {
            if( p_M_Locator_ID == 0 )
            {
                return OpenXpertya.ZERO;
            }
            else 
            {
                String sql = "SELECT M_Warehouse_ID " + "FROM M_Locator " + "WHERE M_Locator_ID=" + p_M_Locator_ID;
                M_Warehouse_ID = OpenXpertya.getSQLValue( sql,p_M_Locator_ID );
            }
        }

        if( M_Warehouse_ID == 0 ) {
            return OpenXpertya.ZERO;
        }

        // Check, if product exists and if it is stocked

        boolean isBOM       = false;
        String  ProductType = null;
        boolean isStocked   = false;
        String  sql         = "SELECT IsBOM, ProductType, IsStocked " + "FROM M_Product " + "WHERE M_Product_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_M_Product_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            isBOM       = "Y".equals( rs.getString( 1 ));
            ProductType = rs.getString( 2 );
            isStocked   = "Y".equals( rs.getString( 3 ));
        }

        rs.close();
        pstmt.close();

        // No Product

        if( ProductType == null ) {
            return OpenXpertya.ZERO;
        }

        // Unlimited capacity if no item

        if( !isBOM && ( !ProductType.equals( "I" ) ||!isStocked )) {
            return UNLIMITED;
        }

        // Get Qty

        if( isStocked ) {
            return getStorageQty( p_M_Product_ID,M_Warehouse_ID,p_M_Locator_ID,p_what );
        }

        // Go through BOM

        BigDecimal quantity        = UNLIMITED;
        BigDecimal productQuantity = null;

        sql = "SELECT b.M_ProductBOM_ID, b.BOMQty, p.IsBOM, p.IsStocked, p.ProductType " + "FROM M_Product_BOM b, M_Product p " + "WHERE b.M_ProductBOM_ID=p.M_Product_ID" + " AND b.M_Product_ID=?";
        pstmt = OpenXpertya.prepareStatement( sql );
        pstmt.setInt( 1,p_M_Product_ID );
        rs = pstmt.executeQuery();

        while( rs.next()) {
            int        M_ProductBOM_ID = rs.getInt( 1 );
            BigDecimal bomQty          = rs.getBigDecimal( 2 );

            isBOM       = "Y".equals( rs.getString( 3 ));
            isStocked   = "Y".equals( rs.getString( 4 ));
            ProductType = rs.getString( 5 );

            // Stocked Items "leaf node"

            if( ProductType.equals( "I" ) && isStocked ) {

                // Get ProductQty

                productQuantity = getStorageQty( M_ProductBOM_ID,M_Warehouse_ID,p_M_Locator_ID,p_what );

                // Get Rounding Precision

                int StdPrecision = getUOMPrecision( M_ProductBOM_ID );

                // How much can we make with this product

                productQuantity = productQuantity.setScale( StdPrecision ).divide( bomQty,BigDecimal.ROUND_HALF_UP );

                // How much can we make overall

                if( productQuantity.compareTo( quantity ) < 0 ) {
                    quantity = productQuantity;
                }
            } else if( isBOM )    // Another BOM
            {
                productQuantity = bomQty( M_ProductBOM_ID,M_Warehouse_ID,p_M_Locator_ID,p_what );

                // How much can we make overall

                if( productQuantity.compareTo( quantity ) < 0 ) {
                    quantity = productQuantity;
                }
            }
        }

        rs.close();
        pstmt.close();

        if( quantity.signum() > 0 ) {
            int StdPrecision = getUOMPrecision( p_M_Product_ID );

            return quantity.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
        }

        return OpenXpertya.ZERO;
    }    // bomQtyOnHand

    /** Descripción de Campos */

    private static final BigDecimal UNLIMITED = new BigDecimal( 99999.0 );

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     * @param M_Warehouse_ID
     * @param p_M_Locator_ID
     * @param p_what
     *
     * @return
     *
     * @throws SQLException
     */

    static BigDecimal getStorageQty( int p_M_Product_ID,int M_Warehouse_ID,int p_M_Locator_ID,String p_what ) throws SQLException {
        BigDecimal quantity = null;
        String     sql      = "SELECT SUM(" + p_what + ") " + "FROM M_Storage s " + "WHERE M_Product_ID=?";

        if( p_M_Locator_ID != 0 ) {
            sql += " AND s.M_Locator_ID=?";
        } else {
            sql += " AND EXISTS (SELECT * FROM M_Locator l WHERE s.M_Locator_ID=l.M_Locator_ID" + " AND l.M_Warehouse_ID=?)";
        }

        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_M_Product_ID );

        if( p_M_Locator_ID != 0 ) {
            pstmt.setInt( 2,p_M_Locator_ID );
        } else {
            pstmt.setInt( 2,M_Warehouse_ID );
        }

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            quantity = rs.getBigDecimal( 1 );
        }

        rs.close();
        pstmt.close();

        // Not found

        if( quantity == null ) {
            return OpenXpertya.ZERO;
        }

        return quantity;
    }    // getStorageQty

    /**
     * Descripción de Método
     *
     *
     * @param p_M_Product_ID
     *
     * @return
     *
     * @throws SQLException
     */

    static int getUOMPrecision( int p_M_Product_ID ) throws SQLException {
        int    precision = 0;
        String sql       = "SELECT u.StdPrecision " + "FROM C_UOM u" + " INNER JOIN M_Product p ON (u.C_UOM_ID=p.C_UOM_ID) " + "WHERE p.M_Product_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_M_Product_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            precision = rs.getInt( 1 );
        }

        rs.close();
        pstmt.close();

        return precision;
    }    // getStdPrecision
}    // Product



/*
 *  @(#)Product.java   23.03.06
 * 
 *  Fin del fichero Product.java
 *  
 *  Versión 2.2
 *
 */
