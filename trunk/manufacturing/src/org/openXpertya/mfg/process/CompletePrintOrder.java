/*
 * @(#)CompletePrintOrder.java   14.jun 2007  Versión 2.2
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



package org.openXpertya.mfg.process;

import java.util.logging.*;

import java.math.*;

import openXpertya.model.*;

import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *  CompletePrintOrder
 *
 *      @author Victor PÃ¨rez
 *      @version $Id: CompletePrintOrder.java,v 1.4 2004/05/07 05:52:14 vpj-cd Exp $
 */
public class CompletePrintOrder extends SvrProcess {

    /** The Order */
    private int	p_MPC_Order_ID	= 0;

    /** Descripción de Campo */
    private boolean	p_IsPrintPickList	= false;

    /** Descripción de Campo */
    private boolean	p_IsPrintWorkflow	= false;

    /** Descripción de Campo */
    private boolean	p_IsPrintPackList	= false;

    /** Descripción de Campo */
    private boolean	p_IsComplete	= false;

    /** Descripción de Campo */
    boolean	IsDirectPrint	= false;

    /**
     *  Prepare - e.g., get Parameters.
     */
    protected void prepare() {

        ProcessInfoParameter[]	para	= getParameter();

        for (int i = 0; i < para.length; i++) {

            String	name	= para[i].getParameterName();

            if (para[i].getParameter() == null)
                ;
            else
                if (name.equals("MPC_Order_ID"))
                    p_MPC_Order_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                else
                    if (name.equals("IsPrintPickList"))
                        p_IsPrintPickList	= "Y".equals(para[i].getParameter());
                    else
                        if (name.equals("IsPrintWorkflow"))
                            p_IsPrintWorkflow	= "Y".equals(para[i].getParameter());
                        else
                            if (name.equals("IsPrintPackList"))
                                p_IsPrintPackList	= "Y".equals(para[i].getParameter());
                            else
                                if (name.equals("IsComplete"))
                                    p_IsComplete	= "Y".equals(para[i].getParameter());
                                else
                                    log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }

    }		// prepare

    /**
     *  Perrform process.
     *  @return Message (clear text)
     *  @throws Exception if not successful
     */
    protected String doIt() throws Exception {

        MPrintFormat	format		= null;
        Language	language	= Language.getLoginLanguage();		// Base Language

        if (p_MPC_Order_ID == 0)
            throw new IllegalArgumentException("Manufactuing Order == 0");

        if (p_IsComplete) {

            MMPCOrder	order	= new MMPCOrder(getCtx(), p_MPC_Order_ID, null);

            if (order.isAvailable()) {

                order.completeIt();
                order.save(get_TrxName());

            } else {
                return Msg.translate(Env.getCtx(), "NoQtyAvailable");
            }
        }

        if (p_IsPrintPickList) {

            // Get Format & Data
            format	= MPrintFormat.get(getCtx(), 1000099, false);
            format.setLanguage(language);
            format.setTranslationLanguage(language);

            // query
            MQuery	query	= new MQuery("MPC_Order");

            query.addRestriction("MPC_Order_ID", MQuery.EQUAL, new Integer(p_MPC_Order_ID));

            // Engine
            PrintInfo	info	= new PrintInfo("MPC_Order", X_MPC_Order.Table_ID, getRecord_ID());
            ReportEngine	re	= new ReportEngine(getCtx(), format, query, info);

            // new Viewer(re);

            if (IsDirectPrint) {

                re.print();

                // ReportEngine.printConfirm ( 1000282 , Record_ID);

            } else
                new Viewer(re);
        }

        if (p_IsPrintWorkflow) {

            // Get Format & Data
            format	= MPrintFormat.get(getCtx(), 1000177, false);
            format.setLanguage(language);
            format.setTranslationLanguage(language);

            // query
            MQuery	query	= new MQuery("MPC_Order");

            query.addRestriction("MPC_Order_ID", MQuery.EQUAL, new Integer(p_MPC_Order_ID));

            // Engine
            PrintInfo	info	= new PrintInfo("MPC_Order", X_MPC_Order.Table_ID, getRecord_ID());
            ReportEngine	re	= new ReportEngine(getCtx(), format, query, info);

            // new Viewer(re);

            if (IsDirectPrint) {
                re.print();	// prints only original
            } else
                new Viewer(re);
        }

        return Msg.translate(Env.getCtx(), "Ok");

    }		// doIt
}	// CompletePrintOrder



/*
 * @(#)CompletePrintOrder.java   14.jun 2007
 * 
 *  Fin del fichero CompletePrintOrder.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
