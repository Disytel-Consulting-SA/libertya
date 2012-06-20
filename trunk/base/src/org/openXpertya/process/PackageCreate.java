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

import java.util.logging.Level;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MPackage;
import org.openXpertya.model.MShipper;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PackageCreate extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Shipper_ID = 0;

    /** Descripción de Campos */

    private int p_M_InOut_ID = 0;

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
            } else if( name.equals( "M_Shipper_ID" )) {
                p_M_Shipper_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_Invoice_ID" )) {
                p_M_InOut_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        if( p_M_InOut_ID == 0 ) {
            p_M_InOut_ID = getRecord_ID();
        }
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
        log.info( "doIt - M_InOut_ID=" + p_M_InOut_ID + ", M_Shipper_ID=" + p_M_Shipper_ID );

        if( p_M_InOut_ID == 0 ) {
            throw new IllegalArgumentException( "No Shipment" );
        }

        if( p_M_Shipper_ID == 0 ) {
            throw new IllegalArgumentException( "No Shipper" );
        }

        MInOut shipment = new MInOut( getCtx(),p_M_InOut_ID,null );

        if( shipment.getID() != p_M_InOut_ID ) {
            throw new IllegalArgumentException( "Cannot find Shipment ID=" + p_M_InOut_ID );
        }

        MShipper shipper = new MShipper( getCtx(),p_M_Shipper_ID,get_TrxName());

        if( shipper.getID() != p_M_Shipper_ID ) {
            throw new IllegalArgumentException( "Cannot find Shipper ID=" + p_M_InOut_ID );
        }

        //

        MPackage pack = MPackage.create( shipment,shipper,null );

        return pack.getDocumentNo();
    }    // doIt
}    // PackageCreate



/*
 *  @(#)PackageCreate.java   02.07.07
 * 
 *  Fin del fichero PackageCreate.java
 *  
 *  Versión 2.2
 *
 */
