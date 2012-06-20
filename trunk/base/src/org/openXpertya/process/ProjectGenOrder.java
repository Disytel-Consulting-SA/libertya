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

import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectLine;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProjectGenOrder extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_Project_ID = 0;

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
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        m_C_Project_ID = getRecord_ID();
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
        log.info( "C_Project_ID=" + m_C_Project_ID );

        if( m_C_Project_ID == 0 ) {
            throw new IllegalArgumentException( "C_Project_ID == 0" );
        }

        MProject fromProject = getProject( getCtx(),m_C_Project_ID,get_TrxName());

        Env.setSOTrx( getCtx(),true );    // Set SO context

        MOrder order = new MOrder( fromProject,true,MOrder.DocSubTypeSO_OnCredit );

        if( !order.save()) {
            throw new Exception( "Could not create Order" );
        }

        // ***     Lines ***

        int count = 0;

        // Service Project

        if( MProject.PROJECTCATEGORY_ServiceChargeProject.equals( fromProject.getProjectCategory())) {
            throw new Exception( "Service Charge Projects are on the TODO List" );
        }               // Service Lines
                else    // Order Lines
                {
            MProjectLine[] lines = fromProject.getLines();

            for( int i = 0;i < lines.length;i++ ) {
                MOrderLine ol = new MOrderLine( order );

                ol.setLine( lines[ i ].getLine());
                ol.setDescription( lines[ i ].getDescription());

                //

                ol.setM_Product_ID( lines[ i ].getM_Product_ID(),true );
                ol.setQty( lines[ i ].getPlannedQty().subtract( lines[ i ].getInvoicedQty()));
                ol.setPrice();

                if( (lines[ i ].getPlannedPrice() != null) && (lines[ i ].getPlannedPrice().compareTo( Env.ZERO ) != 0) ) {
                    ol.setPrice( lines[ i ].getPlannedPrice());
                }

                ol.setDiscount();
                ol.setTax();

                if( ol.save()) {
                    count++;
                }
            }    // for all lines

            if( lines.length != count ) {
                log.log( Level.SEVERE,"Lines difference - ProjectLines=" + lines.length + " <> Saved=" + count );
            }
        }    // Order Lines

        return "@C_Order_ID@ " + order.getDocumentNo() + " (" + count + ")";
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Project_ID
     * @param trxName
     *
     * @return
     */

    static protected MProject getProject( Properties ctx,int C_Project_ID,String trxName ) {
        MProject fromProject = new MProject( ctx,C_Project_ID,trxName );

        if( fromProject.getC_Project_ID() == 0 ) {
            throw new IllegalArgumentException( "Project not found C_Project_ID=" + C_Project_ID );
        }

        if( fromProject.getM_PriceList_Version_ID() == 0 ) {
            throw new IllegalArgumentException( "Project has no Price List" );
        }

        if( fromProject.getM_Warehouse_ID() == 0 ) {
            throw new IllegalArgumentException( "Project has no Warehouse" );
        }

        if( (fromProject.getC_BPartner_ID() == 0) || (fromProject.getC_BPartner_Location_ID() == 0) ) {
            throw new IllegalArgumentException( "Project has no Business Partner/Location" );
        }

        return fromProject;
    }    // getProject
}    // ProjectGenOrder



/*
 *  @(#)ProjectGenOrder.java   02.07.07
 * 
 *  Fin del fichero ProjectGenOrder.java
 *  
 *  Versión 2.2
 *
 */
