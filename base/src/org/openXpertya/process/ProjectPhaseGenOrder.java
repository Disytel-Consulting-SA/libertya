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

import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MProjectPhase;
import org.openXpertya.model.MProjectTask;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ProjectPhaseGenOrder extends SvrProcess {

    /** Descripción de Campos */

    private int m_C_ProjectPhase_ID = 0;

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
        m_C_ProjectPhase_ID = getRecord_ID();
        log.info( "doIt - C_ProjectPhase_ID=" + m_C_ProjectPhase_ID );

        if( m_C_ProjectPhase_ID == 0 ) {
            throw new IllegalArgumentException( "C_ProjectPhase_ID == 0" );
        }

        MProjectPhase fromPhase = new MProjectPhase( getCtx(),m_C_ProjectPhase_ID,get_TrxName());
        MProject fromProject = ProjectGenOrder.getProject( getCtx(),fromPhase.getC_Project_ID(),get_TrxName());
        MOrder order = new MOrder( fromProject,true,MOrder.DocSubTypeSO_OnCredit );

        order.setDescription( order.getDescription() + " - " + fromPhase.getName());

        if( !order.save()) {
            throw new Exception( "Could not create Order" );
        }

        // Create an order on Phase Level

        if( fromPhase.getM_Product_ID() != 0 ) {
            MOrderLine ol = new MOrderLine( order );

            ol.setLine( fromPhase.getSeqNo());

            StringBuffer sb = new StringBuffer( fromPhase.getName());

            if( (fromPhase.getDescription() != null) && (fromPhase.getDescription().length() > 0) ) {
                sb.append( " - " ).append( fromPhase.getDescription());
            }

            ol.setDescription( sb.toString());

            //

            ol.setM_Product_ID( fromPhase.getM_Product_ID(),true );
            ol.setQty( fromPhase.getQty());
            ol.setPrice();

            if( (fromPhase.getPriceActual() != null) && (fromPhase.getPriceActual().compareTo( Env.ZERO ) != 0) ) {
                ol.setPrice( fromPhase.getPriceActual());
            }

            ol.setTax();

            if( !ol.save()) {
                log.log( Level.SEVERE,"doIt - Lines not generated" );
            }

            return "@C_Order_ID@ " + order.getDocumentNo() + " (1)";
        }

        // Project Tasks

        int            count = 0;
        MProjectTask[] tasks = fromPhase.getTasks();

        for( int i = 0;i < tasks.length;i++ ) {
            MOrderLine ol = new MOrderLine( order );

            ol.setLine( tasks[ i ].getSeqNo());

            StringBuffer sb = new StringBuffer( tasks[ i ].getName());

            if( (tasks[ i ].getDescription() != null) && (tasks[ i ].getDescription().length() > 0) ) {
                sb.append( " - " ).append( tasks[ i ].getDescription());
            }

            ol.setDescription( sb.toString());

            //

            ol.setM_Product_ID( tasks[ i ].getM_Product_ID(),true );
            ol.setQty( tasks[ i ].getQty());
            ol.setPrice();
            ol.setTax();

            if( ol.save()) {
                count++;
            }
        }    // for all lines

        if( tasks.length != count ) {
            log.log( Level.SEVERE,"doIt - Lines difference - ProjectTasks=" + tasks.length + " <> Saved=" + count );
        }

        return "@C_Order_ID@ " + order.getDocumentNo() + " (" + count + ")";
    }    // doIt
}    // ProjectPhaseGenOrder



/*
 *  @(#)ProjectPhaseGenOrder.java   02.07.07
 * 
 *  Fin del fichero ProjectPhaseGenOrder.java
 *  
 *  Versión 2.2
 *
 */
