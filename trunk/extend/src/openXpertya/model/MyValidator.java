/*
 * @(#)MyValidator.java   11.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package openXpertya.model;

import org.openXpertya.model.*;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 11.12.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MyValidator implements ModelValidator {

    /**
     * Constructor de la clase ...
     *
     */

    public MyValidator() {
        super();
    }    // MyValidator

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MyValidator.class );

    /** Descripción de Campos */

    private int m_AD_Client_ID = -1;

    /**
     * Descripción de Método
     *
     *
     * @param engine
     * @param client
     */

    public void initialize( ModelValidationEngine engine,MClient client ) {
        log.info( client.toString());
        m_AD_Client_ID = client.getAD_Client_ID();

        // We want to be informed when C_Order is created/changed

        engine.addModelChange( "C_Order",this );

        // We want to validate Order before preparing

        engine.addDocValidate( "C_Order",this );
    }    // initialize

    /**
     * Descripción de Método
     *
     *
     * @param po
     * @param type
     *
     * @return
     *
     * @throws Exception
     */

    public String modelChange( PO po,int type ) throws Exception {
        if( po.get_TableName().equals( "C_Order" ) && (type == TYPE_CHANGE) ) {
            MOrder order = ( MOrder )po;

            log.info( po.toString());
        }

        return null;
    }    // modelChange

    /**
     * Descripción de Método
     *
     *
     * @param po
     * @param timing
     *
     * @return
     */

    public String docValidate( PO po,int timing ) {

        // Ignore all after Complete events

        if( timing == TIMING_AFTER_COMPLETE ) {
            return null;
        }

        //

        if( po.get_TableName().equals( "C_Order" )) {
            MOrder order = ( MOrder )po;

            log.info( po.toString());
        }

        return null;
    }    // docValidate

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     * @param AD_Role_ID
     * @param AD_User_ID
     *
     * @return
     */

    public CallResult login( int AD_Org_ID,int AD_Role_ID,int AD_User_ID ) {
        log.info( "AD_User_ID=" + AD_User_ID );

        return null;
    }    // login

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Client_ID() {
        return m_AD_Client_ID;
    }    // getAD_Client_ID
}    // MyValidator



/*
 *  @(#)MyValidator.java   11.12.06
 * 
 *  Fin del fichero MyValidator.java
 *  
 *  Versión 2.2
 *
 */
