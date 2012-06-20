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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPackage extends X_M_Package {

    /**
     * Descripción de Método
     *
     *
     * @param shipment
     * @param shipper
     * @param shipDate
     *
     * @return
     */

    public static MPackage create( MInOut shipment,MShipper shipper,Timestamp shipDate ) {
        MPackage retValue = new MPackage( shipment,shipper );

        if( shipDate != null ) {
            retValue.setShipDate( shipDate );
        }

        retValue.save();

        // Lines

        MInOutLine[] lines = shipment.getLines( false );

        for( int i = 0;i < lines.length;i++ ) {
            MInOutLine   sLine = lines[ i ];
            MPackageLine pLine = new MPackageLine( retValue );

            pLine.setInOutLine( sLine );
            pLine.save();
        }    // lines

        return retValue;
    }    // create

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_Package_ID
     * @param trxName
     */

    public MPackage( Properties ctx,int M_Package_ID,String trxName ) {
        super( ctx,M_Package_ID,trxName );

        if( M_Package_ID == 0 ) {

            // setM_Shipper_ID (0);
            // setDocumentNo (null);
            // setM_InOut_ID (0);

            setShipDate( new Timestamp( System.currentTimeMillis()));
        }
    }    // MPackage

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPackage( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPackage

    /**
     * Constructor de la clase ...
     *
     *
     * @param shipment
     * @param shipper
     */

    public MPackage( MInOut shipment,MShipper shipper ) {
        this( shipment.getCtx(),0,shipment.get_TrxName());
        setClientOrg( shipment );
        setM_InOut_ID( shipment.getM_InOut_ID());
        setM_Shipper_ID( shipper.getM_Shipper_ID());
    }    // MPackage
    
    //Añadido por ConSerTi para la creacion desde EnvioCreate
    public MPackage (Properties ctx, int M_Package_ID)
	{
		super (ctx, M_Package_ID,null);
		if (M_Package_ID == 0)
		{
		//	setM_Shipper_ID (0);
		//	setDocumentNo (null);
		//	setM_InOut_ID (0);
			setShipDate (new Timestamp(System.currentTimeMillis()));
		}
	}	//	MPackage

    public MPackage (MEnvio envio, MInOut shipment) //, MShipper shipper)
	{
		this (shipment.getCtx(), 0);
		setClientOrg(shipment);
		setM_InOut_ID(shipment.getM_InOut_ID());
		setM_Envio_ID(envio.getM_Envio_ID());
	}	//	MPackage
    
    //Fin Añadido
}    // MPackage

	

/*
 *  @(#)MPackage.java   02.07.07
 * 
 *  Fin del fichero MPackage.java
 *  
 *  Versión 2.2
 *
 */
