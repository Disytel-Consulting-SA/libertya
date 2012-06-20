/*
 * @(#)ComponentChange.java   14.jun 2007  Versión 2.2
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

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import openXpertya.model.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      Component Change into BOM
 *
 *  @author Victor Perez
 *  @version $Id: ComponentChange.java,v 1.1 2004/01/17 05:24:03 jjanke Exp $
 */
public class ComponentChange extends SvrProcess {

    /** The Order */
    private int	p_M_Product_ID	= 0;

    /** Descripción de Campo */
    private Timestamp	p_ValidTo	= null;

    /** Descripción de Campo */
    private Timestamp	p_ValidFrom	= null;

    /** Descripción de Campo */
    private String	p_Action;

    /** Descripción de Campo */
    private int	p_New_M_Product_ID	= 0;

    /** Descripción de Campo */
    private BigDecimal	p_Qty	= null;

    /** Descripción de Campo */
    private int	morepara	= 0;

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
                if (name.equals("M_Product_ID") && morepara == 0) {

                    p_M_Product_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                    morepara	= 1;

                } else
                    if (name.equals("ValidTo"))
                        p_ValidTo	= ((Timestamp) para[i].getParameter());
                    else
                        if (name.equals("ValidFrom"))
                            p_ValidFrom	= ((Timestamp) para[i].getParameter());
                        else
                            if (name.equals("Action"))
                                p_Action	= ((String) para[i].getParameter());
                            else
                                if (name.equals("M_Product_ID"))
                                    p_New_M_Product_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                else
                                    if (name.equals("Qty"))
                                        p_Qty	= ((BigDecimal) para[i].getParameter());
                                    else
                                        log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }

    }		// prepare

    /**
     *  Perrform process.
     *  @return Message
     *  @throws Exception if not successful
     */
    protected String doIt() throws Exception {

        StringBuffer	result	= new StringBuffer("");

        /*
         * System.out.println("Existing Product" + p_M_Product_ID );
         * System.out.println("ValidTo" + p_ValidTo );
         * System.out.println("ValidFrom" + p_ValidFrom );
         * System.out.println("Action" + p_Action );
         * System.out.println("New Product" + p_New_M_Product_ID);
         * System.out.println("Qty" + p_Qty );
         */
        QueryDB	query	= new QueryDB("org.openXpertya.mfg.model.X_MPC_Product_BOMLine");
        StringBuffer	filter	= new StringBuffer("M_Product_ID = " + p_M_Product_ID);

        if (p_ValidFrom != null) {
            filter.append(" AND TRUNC(ValidFrom) >= ").append(DB.TO_DATE(p_ValidTo, true));
        }

        if (p_ValidTo != null) {
            filter.append(" AND TRUNC(ValidTo) <= ").append(DB.TO_DATE(p_ValidTo, true));
        }

        java.util.List	results	= query.execute(filter.toString());
        Iterator	select	= results.iterator();

        while (select.hasNext()) {

            X_MPC_Product_BOMLine	bomline	= (X_MPC_Product_BOMLine) select.next();

            if (p_Action.equals("A")) {

                X_MPC_Product_BOMLine	newbomline	= new X_MPC_Product_BOMLine(Env.getCtx(), 0, null);

                newbomline.setAssay(bomline.getAssay());
                newbomline.setBackflushGroup(bomline.getBackflushGroup());
                newbomline.setQtyBatch(bomline.getQtyBatch());
                newbomline.setC_UOM_ID(bomline.getC_UOM_ID());
                newbomline.setDescription(bomline.getDescription());
                newbomline.setForecast(bomline.getForecast());
                newbomline.setQtyBOM(p_Qty);
                newbomline.setComponentType(bomline.getComponentType());
                newbomline.setIsQtyPercentage(bomline.isQtyPercentage());
                newbomline.setIsCritical(bomline.isCritical());
                newbomline.setIssueMethod(bomline.getIssueMethod());
                newbomline.setLine(25);
                newbomline.setLTOffSet(bomline.getLTOffSet());
                newbomline.setM_AttributeSetInstance_ID(bomline.getM_AttributeSetInstance_ID());
                newbomline.setM_Product_ID(p_New_M_Product_ID);
                newbomline.setMPC_Product_BOM_ID(bomline.getMPC_Product_BOM_ID());
                newbomline.setScrap(bomline.getScrap());
                newbomline.setValidFrom(newbomline.getUpdated());
                newbomline.save(get_TrxName());
                result.append("Component add");

            } else
                if (p_Action.equals("D")) {

                    bomline.setIsActive(false);
                    bomline.save(get_TrxName());
                    result.append("Deactivate ");

                } else
                    if (p_Action.equals("E")) {

                        bomline.setValidTo(bomline.getUpdated());
                        bomline.save(get_TrxName());
                        result.append("Expire ");

                    } else
                        if (p_Action.equals("R")) {

                            X_MPC_Product_BOMLine	newbomline	= new X_MPC_Product_BOMLine(Env.getCtx(), 0, null);

                            newbomline.setAssay(bomline.getAssay());
                            newbomline.setBackflushGroup(bomline.getBackflushGroup());
                            newbomline.setQtyBatch(bomline.getQtyBatch());
                            newbomline.setComponentType(bomline.getComponentType());
                            newbomline.setC_UOM_ID(bomline.getC_UOM_ID());
                            newbomline.setDescription(bomline.getDescription());
                            newbomline.setForecast(bomline.getForecast());
                            newbomline.setQtyBOM(p_Qty);
                            newbomline.setIsQtyPercentage(bomline.isQtyPercentage());
                            newbomline.setIsCritical(bomline.isCritical());
                            newbomline.setIssueMethod(bomline.getIssueMethod());
                            newbomline.setLine(25);
                            newbomline.setLTOffSet(bomline.getLTOffSet());
                            newbomline.setM_AttributeSetInstance_ID(bomline.getM_AttributeSetInstance_ID());
                            newbomline.setM_Product_ID(p_New_M_Product_ID);
                            newbomline.setMPC_Product_BOM_ID(bomline.getMPC_Product_BOM_ID());
                            newbomline.setScrap(bomline.getScrap());
                            newbomline.setValidFrom(newbomline.getUpdated());
                            newbomline.save(get_TrxName());
                            bomline.setIsActive(false);
                            bomline.save(get_TrxName());
                            result.append("Replace");

                        } else
                            if (p_Action.equals("RE")) {

                                X_MPC_Product_BOMLine	newbomline	= new X_MPC_Product_BOMLine(Env.getCtx(), 0, null);

                                newbomline.setAssay(bomline.getAssay());
                                newbomline.setBackflushGroup(bomline.getBackflushGroup());
                                newbomline.setQtyBatch(bomline.getQtyBatch());
                                newbomline.setComponentType(bomline.getComponentType());
                                newbomline.setC_UOM_ID(bomline.getC_UOM_ID());
                                newbomline.setDescription(bomline.getDescription());
                                newbomline.setForecast(bomline.getForecast());
                                newbomline.setQtyBOM(p_Qty);
                                newbomline.setIsQtyPercentage(bomline.isQtyPercentage());
                                newbomline.setIsCritical(bomline.isCritical());
                                newbomline.setIssueMethod(bomline.getIssueMethod());
                                newbomline.setLine(25);
                                newbomline.setLTOffSet(bomline.getLTOffSet());
                                newbomline.setM_AttributeSetInstance_ID(bomline.getM_AttributeSetInstance_ID());
                                newbomline.setM_Product_ID(p_New_M_Product_ID);
                                newbomline.setMPC_Product_BOM_ID(bomline.getMPC_Product_BOM_ID());
                                newbomline.setScrap(bomline.getScrap());
                                newbomline.setValidFrom(newbomline.getUpdated());
                                newbomline.save(get_TrxName());
                                bomline.setValidTo(bomline.getUpdated());
                                bomline.save(get_TrxName());
                                result.append("Replace & Expire");
                            }
        }

        return result.toString();

    }		// doIt
}	// Componet Change



/*
 * @(#)ComponentChange.java   14.jun 2007
 * 
 *  Fin del fichero ComponentChange.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
