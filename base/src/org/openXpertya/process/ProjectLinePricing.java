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

import org.openXpertya.model.MProductPricing;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectLine;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProjectLinePricing extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_ProjectLine_ID = 0;

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
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        m_C_ProjectLine_ID = getRecord_ID();
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
        if( m_C_ProjectLine_ID == 0 ) {
            throw new IllegalArgumentException( "No Project Line" );
        }

        MProjectLine projectLine = new MProjectLine( getCtx(),m_C_ProjectLine_ID,get_TrxName());

        log.info( "doIt - " + projectLine );

        if( projectLine.getM_Product_ID() == 0 ) {
            throw new IllegalArgumentException( "No Product" );
        }

        //

        MProject project = new MProject( getCtx(),projectLine.getC_Project_ID(),get_TrxName());

        if( project.getM_PriceList_ID() == 0 ) {
            throw new IllegalArgumentException( "No PriceList" );
        }

        //

        boolean         isSOTrx = true;
        MProductPricing pp      = new MProductPricing( projectLine.getM_Product_ID(),project.getC_BPartner_ID(),projectLine.getPlannedQty(),isSOTrx );

        pp.setM_PriceList_ID( project.getM_PriceList_ID());
        pp.setPriceDate( project.getDateContract());

        //

        projectLine.setPlannedPrice( pp.getPriceStd());
        projectLine.setPlannedMarginAmt( pp.getPriceStd().subtract( pp.getPriceLimit()));
        projectLine.save();

        //

        String retValue = Msg.getElement( getCtx(),"PriceList" ) + pp.getPriceList() + " - " + Msg.getElement( getCtx(),"PriceStd" ) + pp.getPriceStd() + " - " + Msg.getElement( getCtx(),"PriceLimit" ) + pp.getPriceLimit();

        return retValue;
    }    // doIt
}    // ProjectLinePricing



/*
 *  @(#)ProjectLinePricing.java   02.07.07
 * 
 *  Fin del fichero ProjectLinePricing.java
 *  
 *  Versión 2.2
 *
 */
