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
import java.util.Properties;

import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MChangeRequest extends X_M_ChangeRequest {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_ChangeRequest_ID
     * @param trxName
     */

    public MChangeRequest( Properties ctx,int M_ChangeRequest_ID,String trxName ) {
        super( ctx,M_ChangeRequest_ID,trxName );

        if( M_ChangeRequest_ID == 0 ) {

            // setName (null);

            setIsApproved( false );
            setProcessed( false );
        }
    }    // MChangeRequest

    /**
     * Constructor de la clase ...
     *
     *
     * @param request
     * @param group
     */

    public MChangeRequest( MRequest request,MGroup group ) {
        this( request.getCtx(),0,request.get_TrxName());
        setClientOrg( request );
        setName( Msg.getElement( getCtx(),"R_Request_ID" ) + ": " + request.getDocumentNo());
        setHelp( request.getSummary());

        //

        setM_BOM_ID( group.getM_BOM_ID());
        setM_ChangeNotice_ID( group.getM_ChangeNotice_ID());
    }    // MChangeRequest

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MChangeRequest( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MChangeRequest

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequest[] getRequests() {
        String sql = "SELECT * FROM R_Request WHERE M_ChangeRequest_ID=?";

        return null;
    }    // getRequests

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Have at least one

        if( (getM_BOM_ID() == 0) && (getM_ChangeNotice_ID() == 0) ) {
            log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@: @M_BOM_ID@ / @M_ChangeNotice_ID@" ));

            return false;
        }

        // Derive ChangeNotice from BOM if defined

        if( newRecord && (getM_BOM_ID() != 0) && (getM_ChangeNotice_ID() == 0) ) {
            MBOM bom = new MBOM( getCtx(),getM_BOM_ID(),get_TrxName());

            if( bom.getM_ChangeNotice_ID() != 0 ) {
                setM_BOM_ID( bom.getM_ChangeNotice_ID());
            }
        }

        return true;
    }    // beforeSave
}    // MChangeRequest



/*
 *  @(#)MChangeRequest.java   02.07.07
 * 
 *  Fin del fichero MChangeRequest.java
 *  
 *  Versión 2.2
 *
 */
