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

import javax.swing.JOptionPane;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MEnvio extends X_M_Envio {

    /**
     * Descripción de Método
     *
     *
     * @param shipment
     * @param shipper
     * @param numPaquetes
     * @param shipDate
     *
     * @return
     */
	


    public static MEnvio create( MInOut shipment,MShipper shipper,int numPaquetes,Timestamp shipDate ) {
        MEnvio envio = new MEnvio( shipment,shipper,numPaquetes );
        
        if( shipDate != null ) {
            envio.setShipDate( shipDate );
            
        }
        envio.setM_Shipper_ID(shipper.getM_Shipper_ID());
        envio.setNo_Paquetes(numPaquetes);
        envio.save();

        return envio;
    }    // create

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Envio_ID
     * @param trxName
     */

    public MEnvio( Properties ctx,int M_Envio_ID,String trxName ) {
        super( ctx,M_Envio_ID,trxName );

        if( M_Envio_ID == 0 ) {

            // setM_Shipper_ID (0);
            // setDocumentNo (null);
            // setM_InOut_ID (0);

            setShipDate( new Timestamp( System.currentTimeMillis()));
        }
    }    // MEnvio

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MEnvio( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MEnvio

    /**
     * Constructor de la clase ...
     *
     *
     * @param shipment
     * @param shipper
     * @param numPaquetes
     */

    public MEnvio( MInOut shipment,MShipper shipper,int numPaquetes ) {
        this( shipment.getCtx(),0,null );
        setClientOrg( shipment );
        setM_Shipper_ID( shipper.getM_Shipper_ID());
        setNo_Paquetes( numPaquetes );
    }    // MEnvio
}    // MEnvio



/*
 *  @(#)MEnvio.java   02.07.07
 * 
 *  Fin del fichero MEnvio.java
 *  
 *  Versión 2.2
 *
 */
