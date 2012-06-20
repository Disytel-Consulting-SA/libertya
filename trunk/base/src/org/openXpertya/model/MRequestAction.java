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

public class MRequestAction extends X_R_RequestAction {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_RequestAction_ID
     * @param trxName
     */

    public MRequestAction( Properties ctx,int R_RequestAction_ID,String trxName ) {
        super( ctx,R_RequestAction_ID,trxName );
    }    // MRequestAction

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequestAction( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequestAction

    /**
     * Constructor de la clase ...
     *
     *
     * @param request
     * @param newRecord
     */

    public MRequestAction( MRequest request,boolean newRecord ) {
        this( request.getCtx(),0,request.get_TrxName());
        setClientOrg( request );
        setR_Request_ID( request.getR_Request_ID());
        set_ValueNoCheck( "Created",request.getUpdated());
        set_ValueNoCheck( "CreatedBy",new Integer( request.getUpdatedBy()));
        set_ValueNoCheck( "Updated",request.getUpdated());
        set_ValueNoCheck( "UpdatedBy",new Integer( request.getUpdatedBy()));
    }    // MRequestAction

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     */

    public void addNullColumn( String columnName ) {
        String nc = getNullColumns();

        if( nc == null ) {
            setNullColumns( columnName );
        } else {
            setNullColumns( nc + ";" + columnName );
        }
    }    // addNullColumn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCreatedByName() {
        MUser user = MUser.get( getCtx(),getCreatedBy());

        return user.getName();
    }    // getCreatedByName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getChangesHTML() {
        StringBuffer sb = new StringBuffer();

        getChangeHTML( sb,"Priority" );
        getChangeHTML( sb,"PriorityUser" );
        getChangeHTML( sb,"R_Category_ID" );
        getChangeHTML( sb,"R_Group_ID" );
        getChangeHTML( sb,"R_RequestType_ID" );
        getChangeHTML( sb,"R_Resolution_ID" );
        getChangeHTML( sb,"R_Status_ID" );
        getChangeHTML( sb,"SalesRep_ID" );
        getChangeHTML( sb,"Summary" );

        //

        getChangeHTML( sb,"AD_Org_ID" );
        getChangeHTML( sb,"AD_Role_ID" );
        getChangeHTML( sb,"AD_User_ID" );
        getChangeHTML( sb,"C_Activity_ID" );
        getChangeHTML( sb,"C_BPartner_ID" );
        getChangeHTML( sb,"C_Invoice_ID" );
        getChangeHTML( sb,"C_Order_ID" );
        getChangeHTML( sb,"C_Payment_ID" );
        getChangeHTML( sb,"C_Project_ID" );
        getChangeHTML( sb,"DateNextAction" );
        getChangeHTML( sb,"IsEscalated" );
        getChangeHTML( sb,"IsInvoiced" );
        getChangeHTML( sb,"IsSelfService" );
        getChangeHTML( sb,"M_InOut_ID" );
        getChangeHTML( sb,"M_Product_ID" );
        getChangeHTML( sb,"M_RMA_ID" );
        getChangeHTML( sb,"A_Asset_ID" );

        if( sb.length() == 0 ) {
            sb.append( "./." );
        }

        return sb.toString();
    }    // getChangesHTML

    /**
     * Descripción de Método
     *
     *
     * @param sb
     * @param columnName
     */

    private void getChangeHTML( StringBuffer sb,String columnName ) {
        if( get_Value( columnName ) != null ) {
            if( sb.length() > 0 ) {
                sb.append( "<br>" );
            }

            sb.append( Msg.getElement( getCtx(),columnName )).append( ": " ).append( get_DisplayValue( columnName,true ));
        } else {
            String nc = getNullColumns();

            if( (nc != null) && (nc.indexOf( columnName ) != -1) ) {
                if( sb.length() > 0 ) {
                    sb.append( "<br>(" );
                }

                sb.append( Msg.getElement( getCtx(),columnName )).append( ")" );
            }
        }
    }    // getChangeHTML

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        return true;
    }    // beforeSave
}    // MRequestAction



/*
 *  @(#)MRequestAction.java   02.07.07
 * 
 *  Fin del fichero MRequestAction.java
 *  
 *  Versión 2.2
 *
 */
