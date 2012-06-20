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

import javax.servlet.http.HttpServletRequest;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAsset extends X_A_Asset {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param A_Asset_ID
     * @param trxName
     */

    public MAsset( Properties ctx,int A_Asset_ID,String trxName ) {
        super( ctx,A_Asset_ID,trxName );

        if( A_Asset_ID == 0 ) {
            setIsDepreciated( false );
            setIsFullyDepreciated( false );

            // setValue (null);
            // setName (null);

            setIsInPosession( false );
            setIsOwned( false );

            // setA_Asset_Group_ID (0);

            setIsDisposed( false );
            setM_AttributeSetInstance_ID( 0 );
            setQty( Env.ONE );
        }
    }    // MAsset

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param A_Asset_ID
     */

    public MAsset( Properties ctx,int A_Asset_ID ) {
        this( ctx,A_Asset_ID,null );
    }    // MAsset

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAsset( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAsset

    /**
     * Constructor de la clase ...
     *
     *
     * @param shipment
     * @param shipLine
     * @param deliveryCount
     */

    public MAsset( MInOut shipment,MInOutLine shipLine,int deliveryCount ) {
        this( shipment.getCtx(),0,shipment.get_TrxName());
        setClientOrg( shipment );
        setValueNameDescription( shipment,shipLine,deliveryCount );

        // Header

        setAssetServiceDate( shipment.getMovementDate());
        setIsOwned( false );
        setC_BPartner_ID( shipment.getC_BPartner_ID());
        setC_BPartner_Location_ID( shipment.getC_BPartner_Location_ID());
        setAD_User_ID( shipment.getAD_User_ID());

        // Line

        MProduct product = shipLine.getProduct();

        setM_Product_ID( product.getM_Product_ID());
        setA_Asset_Group_ID( product.getA_Asset_Group_ID());

        // Guarantee & Version

        setGuaranteeDate( TimeUtil.addDays( shipment.getMovementDate(),product.getGuaranteeDays()));
        setVersionNo( product.getVersionNo());

        if( shipLine.getM_AttributeSetInstance_ID() != 0 ) {
            MAttributeSetInstance asi = new MAttributeSetInstance( getCtx(),shipLine.getM_AttributeSetInstance_ID(),get_TrxName());

            setM_AttributeSetInstance_ID( asi.getM_AttributeSetInstance_ID());
            setLot( asi.getLot());
            setSerNo( asi.getSerNo());
        }

        setHelp( shipLine.getDescription());

        if( deliveryCount != 0 ) {
            setQty( shipLine.getMovementQty());
        }

        // Activate

        MAssetGroup ag = MAssetGroup.get( getCtx(),getA_Asset_Group_ID());

        if( !ag.isCreateAsActive()) {
            setIsActive( false );
        }
    }    // MAsset

    /** Descripción de Campos */

    private MProduct m_product = null;

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MAsset.class );

    /**
     * Descripción de Método
     *
     *
     * @param shipment
     * @param line
     * @param deliveryCount
     */

    public void setValueNameDescription( MInOut shipment,MInOutLine line,int deliveryCount ) {
        MProduct  product = line.getProduct();
        MBPartner partner = shipment.getBPartner();

        setValueNameDescription( shipment,deliveryCount,product,partner );
    }    // setValueNameDescription

    /**
     * Descripción de Método
     *
     *
     * @param shipment
     * @param deliveryCount
     * @param product
     * @param partner
     */

    public void setValueNameDescription( MInOut shipment,int deliveryCount,MProduct product,MBPartner partner ) {
        String documentNo = "_" + shipment.getDocumentNo();

        if( deliveryCount > 1 ) {
            documentNo += "_" + deliveryCount;
        }

        // Value

        String value = partner.getValue() + "_" + product.getValue();

        if( value.length() > 40 - documentNo.length()) {
            value = value.substring( 0,40 - documentNo.length()) + documentNo;
        }

        setValue( value );

        // Name            MProduct.afterSave

        String name = partner.getName() + " - " + product.getName();

        if( name.length() > 60 ) {
            name = name.substring( 0,60 );
        }

        setName( name );

        // Description

        String description = product.getDescription();

        setDescription( description );
    }    // setValueNameDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getQty() {
        BigDecimal qty = super.getQty();

        if( (qty == null) || qty.equals( Env.ZERO )) {
            setQty( Env.ONE );
        }

        return super.getQty();
    }    // getQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAsset[" ).append( getID()).append( "-" ).append( getValue()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MAssetDelivery[] getDeliveries() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM A_Asset_Delivery WHERE A_Asset_ID=? ORDER BY Created DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getA_Asset_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MAssetDelivery( getCtx(),rs,get_TrxName()));
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

        //

        MAssetDelivery[] retValue = new MAssetDelivery[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getDeliveries

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDeliveryCount() {
        String sql = "SELECT COUNT(*) FROM A_Asset_Delivery WHERE A_Asset_ID=?";

        return DB.getSQLValue( get_TrxName(),sql,getA_Asset_ID());
    }    // getDeliveries

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isDownloadable() {
        if( !isActive()) {
            return false;
        }

        // Guarantee Date

        Timestamp guarantee = getGuaranteeDate();

        if( guarantee == null ) {
            return false;
        }

        guarantee = TimeUtil.getDay( guarantee );

        Timestamp now = TimeUtil.getDay( System.currentTimeMillis());

        // valid

        if( !now.after( guarantee ))    // not after guarantee date
        {
            getProduct();

            return (m_product != null) && m_product.hasDownloads();
        }

        //

        return false;
    }    // isDownloadable

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProductVersionNo() {
        return getProduct().getVersionNo();
    }    // getProductVersionNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getProductR_MailText_ID() {
        return getProduct().getR_MailText_ID();
    }    // getProductR_MailText_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MProduct getProduct() {
        if( m_product == null ) {
            m_product = MProduct.get( getCtx(),getM_Product_ID());
        }

        return m_product;
    }    // getProductInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProductDownload[] getProductDownloads() {
        if( m_product == null ) {
            getProduct();
        }

        if( m_product != null ) {
            return m_product.getProductDownloads( false );
        }

        return null;
    }    // getProductDownloads

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getDownloadNames() {
        MProductDownload[] dls = getProductDownloads();

        if( (dls != null) && (dls.length > 0) ) {
            String[] retValue = new String[ dls.length ];

            for( int i = 0;i < retValue.length;i++ ) {
                retValue[ i ] = dls[ i ].getName();
            }

            log.fine( "#" + dls.length );

            return retValue;
        }

        return new String[]{};
    }    // addlDownloadNames

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String[] getDownloadURLs() {
        MProductDownload[] dls = getProductDownloads();

        if( (dls != null) && (dls.length > 0) ) {
            String[] retValue = new String[ dls.length ];

            for( int i = 0;i < retValue.length;i++ ) {
                String url = dls[ i ].getDownloadURL();
                int    pos = Math.max( url.lastIndexOf( '/' ),url.lastIndexOf( '\\' ));

                if( pos != -1 ) {
                    url = url.substring( pos + 1 );
                }

                retValue[ i ] = url;
            }

            return retValue;
        }

        return new String[]{};
    }    // addlDownloadURLs

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        getQty();    // set to 1

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param email
     * @param AD_User_ID
     *
     * @return
     */

    public MAssetDelivery confirmDelivery( EMail email,int AD_User_ID ) {
        setVersionNo( getProductVersionNo());

        MAssetDelivery ad = new MAssetDelivery( this,email,AD_User_ID );

        return ad;
    }    // confirmDelivery

    /**
     * Descripción de Método
     *
     *
     * @param request
     * @param AD_User_ID
     *
     * @return
     */

    public MAssetDelivery confirmDelivery( HttpServletRequest request,int AD_User_ID ) {
        setVersionNo( getProductVersionNo());

        MAssetDelivery ad = new MAssetDelivery( this,request,AD_User_ID );

        return ad;
    }    // confirmDelivery
}    // MAsset



/*
 *  @(#)MAsset.java   02.07.07
 * 
 *  Fin del fichero MAsset.java
 *  
 *  Versión 2.2
 *
 */
