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



package org.openXpertya.report;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.X_PA_ReportSource;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReportSource extends X_PA_ReportSource {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param PA_ReportSource_ID
     * @param trxName
     */

    public MReportSource( Properties ctx,int PA_ReportSource_ID,String trxName ) {
        super( ctx,PA_ReportSource_ID,trxName );

        if( PA_ReportSource_ID == 0 ) {}
    }    // MReportSource

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MReportSource( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MReportSource

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWhereClause() {
        String et = getElementType();

        // ID for Tree

        int ID = 0;

        //

        if( MAcctSchemaElement.ELEMENTTYPE_Account.equals( et )) {
            ID = getC_ElementValue_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_Activity.equals( et )) {
            ID = getC_Activity_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_BPartner.equals( et )) {
            ID = getC_BPartner_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_Campaign.equals( et )) {
            ID = getC_Campaign_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_LocationFrom.equals( et )) {
            ID = getC_Location_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_LocationTo.equals( et )) {
            ID = getC_Location_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_Org.equals( et )) {
            ID = getOrg_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_Product.equals( et )) {
            ID = getM_Product_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_Project.equals( et )) {
            ID = getC_Project_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_SalesRegion.equals( et )) {
            ID = getC_SalesRegion_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_OrgTrx.equals( et )) {
            ID = getOrg_ID();    // (re)uses Org_ID
        } else if( MAcctSchemaElement.ELEMENTTYPE_User1.equals( et )) {
            ID = getC_ElementValue_ID();
        } else if( MAcctSchemaElement.ELEMENTTYPE_User2.equals( et )) {
            ID = getC_ElementValue_ID();
        }

        //

        return MReportTree.getWhereClause( getCtx(),et,ID );
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReportSource[" ).append( getID()).append( " - " ).append( getDescription()).append( " - " ).append( getElementType()).append( " - " ).append( getWhereClause());

        sb.append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param PA_ReportLine_ID
     * @param source
     * @param trxName
     *
     * @return
     */

    public static MReportSource copy( Properties ctx,int AD_Client_ID,int AD_Org_ID,int PA_ReportLine_ID,MReportSource source,String trxName ) {
        MReportSource retValue = new MReportSource( ctx,0,trxName );

        MReportSource.copyValues( source,retValue,AD_Client_ID,AD_Org_ID );
        retValue.setPA_ReportLine_ID( PA_ReportLine_ID );

        return retValue;
    }    // copy
}    // MReportSource



/*
 *  @(#)MReportSource.java   02.07.07
 * 
 *  Fin del fichero MReportSource.java
 *  
 *  Versión 2.2
 *
 */
