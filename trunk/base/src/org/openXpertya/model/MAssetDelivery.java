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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.openXpertya.util.EMail;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAssetDelivery extends X_A_Asset_Delivery {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param A_Asset_Delivery_ID
     * @param trxName
     */

    public MAssetDelivery( Properties ctx,int A_Asset_Delivery_ID,String trxName ) {
        super( ctx,A_Asset_Delivery_ID,trxName );

        if( A_Asset_Delivery_ID == 0 ) {
            setMovementDate( new Timestamp( System.currentTimeMillis()));
        }
    }    // MAssetDelivery

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAssetDelivery( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAssetDelivery

    /**
     * Constructor de la clase ...
     *
     *
     * @param asset
     * @param request
     * @param AD_User_ID
     */

    public MAssetDelivery( MAsset asset,HttpServletRequest request,int AD_User_ID ) {
        super( asset.getCtx(),0,asset.get_TrxName());
        setAD_Client_ID( asset.getAD_Client_ID());
        setAD_Org_ID( asset.getAD_Org_ID());

        // Asset Info

        setA_Asset_ID( asset.getA_Asset_ID());
        setLot( asset.getLot());
        setSerNo( asset.getSerNo());
        setVersionNo( asset.getVersionNo());

        //

        setMovementDate( new Timestamp( System.currentTimeMillis()));

        // Request

        setURL( request.getRequestURL().toString());
        setReferrer( request.getHeader( "Referer" ));
        setRemote_Addr( request.getRemoteAddr());
        setRemote_Host( request.getRemoteHost());

        // Who

        setAD_User_ID( AD_User_ID );

        //

        save();
    }    // MAssetDelivery

    /**
     * Constructor de la clase ...
     *
     *
     * @param asset
     * @param email
     * @param AD_User_ID
     */

    public MAssetDelivery( MAsset asset,EMail email,int AD_User_ID ) {
        super( asset.getCtx(),0,asset.get_TrxName());

        // Asset Info

        setA_Asset_ID( asset.getA_Asset_ID());
        setLot( asset.getLot());
        setSerNo( asset.getSerNo());
        setVersionNo( asset.getVersionNo());

        //

        setMovementDate( new Timestamp( System.currentTimeMillis()));

        // EMail

        setEMail( email.getTo().toString());
        setMessageID( email.getMessageID());

        // Who

        setAD_User_ID( AD_User_ID );

        //

        save();
    }    // MAssetDelivery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MAssetDelivery[" ).append( getID()).append( ",A_Asset_ID=" ).append( getA_Asset_ID()).append( ",MovementDate=" ).append( getMovementDate()).append( "]" );

        return sb.toString();
    }    // toString
}    // MAssetDelivery



/*
 *  @(#)MAssetDelivery.java   02.07.07
 * 
 *  Fin del fichero MAssetDelivery.java
 *  
 *  Versión 2.2
 *
 */
