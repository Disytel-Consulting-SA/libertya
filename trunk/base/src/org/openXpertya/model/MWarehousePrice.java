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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MWarehousePrice extends X_RV_WarehousePrice {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_PriceList_Version_ID
     * @param M_Warehouse_ID
     * @param Value
     * @param Name
     * @param UPC
     * @param SKU
     * @param trxName
     *
     * @return
     */

    public static MWarehousePrice[] find( Properties ctx,int M_PriceList_Version_ID,int M_Warehouse_ID,String Value,String Name,String UPC,String SKU,String trxName ) {
        StringBuffer sql = new StringBuffer( "SELECT * FROM RV_WarehousePrice " + "WHERE M_PriceList_Version_ID=? AND M_Warehouse_ID=?" );
        StringBuffer sb = new StringBuffer();

        Value = getFindParameter( Value );

        if( Value != null ) {
            sb.append( "UPPER(Value) LIKE ?" );
        }

        Name = getFindParameter( Name );

        if( Name != null ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPPER(Name) LIKE ?" );
        }

        if( (UPC != null) && (UPC.length() > 0) ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "UPC=?" );
        }

        if( (SKU != null) && (SKU.length() > 0) ) {
            if( sb.length() > 0 ) {
                sb.append( " OR " );
            }

            sb.append( "SKU=?" );
        }

        if( sb.length() > 0 ) {
            sql.append( " AND (" ).append( sb ).append( ")" );
        }

        sql.append( " ORDER BY Value" );

        //

        String finalSQL = MRole.getDefault().addAccessSQL( sql.toString(),"RV_WarehousePrice",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        s_log.fine( "find - M_PriceList_Version_ID=" + M_PriceList_Version_ID + ", M_Warehouse_ID=" + M_Warehouse_ID + " - " + finalSQL );

        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( finalSQL );

            int index = 1;

            pstmt.setInt( index++,M_PriceList_Version_ID );
            pstmt.setInt( index++,M_Warehouse_ID );

            if( Value != null ) {
                pstmt.setString( index++,Value );
            }

            if( Name != null ) {
                pstmt.setString( index++,Name );
            }

            if( (UPC != null) && (UPC.length() > 0) ) {
                pstmt.setString( index++,UPC );
            }

            if( (SKU != null) && (SKU.length() > 0) ) {
                pstmt.setString( index++,SKU );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWarehousePrice( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"find - " + finalSQL,e );
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

        s_log.fine( "find - #" + list.size());

        MWarehousePrice[] retValue = new MWarehousePrice[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // find

    /**
     * Descripción de Método
     *
     *
     * @param bPartner
     * @param IsSOTrx
     * @param valid
     * @param M_Warehouse_ID
     * @param Value
     * @param Name
     * @param UPC
     * @param SKU
     * @param trxName
     *
     * @return
     */

    public static MWarehousePrice[] find( MBPartner bPartner,boolean IsSOTrx,Timestamp valid,int M_Warehouse_ID,String Value,String Name,String UPC,String SKU,String trxName ) {
        int        M_PriceList_ID = IsSOTrx
                                    ?bPartner.getM_PriceList_ID()
                                    :bPartner.getPO_PriceList_ID();
        MPriceList pl             = null;

        if( M_PriceList_ID == 0 ) {
            pl = MPriceList.getDefault( bPartner.getCtx(),IsSOTrx );
        } else {
            pl = MPriceList.get( bPartner.getCtx(),M_PriceList_ID,trxName );
        }

        if( pl == null ) {
            s_log.severe( "find - No PriceList found" );

            return null;
        }

        MPriceListVersion plv = pl.getPriceListVersion( valid );

        if( plv == null ) {
            s_log.severe( "find - No PriceListVersion found for M_PriceList_ID=" + pl.getM_PriceList_ID());

            return null;
        }

        //

        return find( bPartner.getCtx(),plv.getM_PriceList_Version_ID(),M_Warehouse_ID,Value,Name,UPC,SKU,trxName );
    }    // find

    /**
     * Descripción de Método
     *
     *
     * @param product
     * @param M_PriceList_Version_ID
     * @param M_Warehouse_ID
     * @param trxName
     *
     * @return
     */

    public static MWarehousePrice get( MProduct product,int M_PriceList_Version_ID,int M_Warehouse_ID,String trxName ) {
        MWarehousePrice retValue = null;
        String          sql      = "SELECT * FROM RV_WarehousePrice " + "WHERE M_Product_ID=? AND M_PriceList_Version_ID=? AND M_Warehouse_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,product.getM_Product_ID());
            pstmt.setInt( 2,M_PriceList_Version_ID );
            pstmt.setInt( 3,M_Warehouse_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MWarehousePrice( product.getCtx(),rs,trxName );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"get",e );
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

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MWarehousePrice.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MWarehousePrice( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MWarehousePrice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAvailable() {
        return getQtyAvailable().signum() == 1;    // > 0
    }                                              // isAvailable
}    // MWarehousePrice



/*
 *  @(#)MWarehousePrice.java   02.07.07
 * 
 *  Fin del fichero MWarehousePrice.java
 *  
 *  Versión 2.2
 *
 */
