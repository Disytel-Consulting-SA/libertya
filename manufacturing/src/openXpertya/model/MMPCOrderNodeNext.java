/*
 * @(#)MMPCOrderNodeNext.java   13.jun 2007  Versión 2.2
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



//package org.openXpertya.mfg.model;
package openXpertya.model;

import java.util.*;

import java.sql.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      Workflow Node Next - Transition
 *
 *      @author         Jorg Janke
 *      @version        $Id: MWFNodeNext.java,v 1.10 2004/05/20 05:57:47 jjanke Exp $
 */
public class MMPCOrderNodeNext extends X_MPC_Order_NodeNext {

    /**
     *      Standard Costructor
     *      @param ctx context
     *      @param MPC_Order_NodeNext_ID id
     * @param trxName
     */
    public MMPCOrderNodeNext(Properties ctx, int MPC_Order_NodeNext_ID, String trxName) {

        super(ctx, MPC_Order_NodeNext_ID, trxName);

        if (MPC_Order_NodeNext_ID == 0) {

            // setMPC_Order_Next_ID (0);
            // setMPC_Order_Node_ID (0);
            setEntityType(ENTITYTYPE_UserMaintained);		// U
            setIsStdUserWorkflow(false);
            setSeqNo(10);					// 10
        }

    }								// MWFNodeNext

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param rs result set to load info from
     * @param trxName
     */
    public MMPCOrderNodeNext(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MWFNodeNext

    /**
     *      Create new
     *      @param ctx context
     *      @param MPC_Order_Node_ID Node
     *      @param MPC_Order_Next_ID Next
     *      @param SeqNo sequence
     * @param trxName
     */
    public MMPCOrderNodeNext(Properties ctx, int MPC_Order_Node_ID, int MPC_Order_Next_ID, int SeqNo, String trxName) {

        super(ctx, 0, trxName);
        setMPC_Order_Node_ID(MPC_Order_Node_ID);
        setMPC_Order_Next_ID(MPC_Order_Next_ID);

        //
        setEntityType(ENTITYTYPE_UserMaintained);	// U
        setIsStdUserWorkflow(false);
        setSeqNo(SeqNo);
        save(get_TrxName());

    }							// MWFNodeNext

    /** Transition Conditions */

    /**
     * no apply for manufacturing
     * private MWFNextCondition[]      m_conditions = null;
     */

    /** From (Split Eleemnt) is AND */
    public Boolean	m_fromSplitAnd	= null;

    /** To (Join Element) is AND */
    public Boolean	m_toJoinAnd	= null;

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MWFNodeNext[");

        sb.append(getSeqNo()).append(":Node=").append(getMPC_Order_Node_ID()).append("->Next=").append(getMPC_Order_Next_ID());

        /*
         * if (m_conditions != null)
         *       sb.append(",#").append(m_conditions.length);
         */
        if ((getDescription() != null) && (getDescription().length() > 0)) {
            sb.append(",").append(getDescription());
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    /**
     *      Split Element is AND
     *      @return Returns the from Split And.
     */
    public boolean isFromSplitAnd() {

        if (m_fromSplitAnd != null) {
            return m_fromSplitAnd.booleanValue();
        }

        return false;

    }		// getFromSplitAnd

    /**
     *      Split Element is AND.
     *      Set by MWFNode.loadNodes
     *      @param fromSplitAnd The from Split And
     */
    public void setFromSplitAnd(boolean fromSplitAnd) {
        m_fromSplitAnd	= new Boolean(fromSplitAnd);
    }		// setFromSplitAnd

    /**
     *      Join Element is AND
     *      @return Returns the to Join And.
     */
    public boolean isToJoinAnd() {

        if ((m_toJoinAnd == null) && (getMPC_Order_Next_ID() != 0)) {

            MMPCOrderNode	next	= MMPCOrderNode.get(getCtx(), getMPC_Order_Next_ID(), "MPC_Order_Node");

            setToJoinAnd(MMPCOrderNode.JOINELEMENT_AND.equals(next.getJoinElement()));
        }

        if (m_toJoinAnd != null) {
            return m_toJoinAnd.booleanValue();
        }

        return false;

    }		// getToJoinAnd

    /**
     *      Join Element is AND.
     *      @param toJoinAnd The to Join And to set.
     */
    private void setToJoinAnd(boolean toJoinAnd) {
        m_toJoinAnd	= new Boolean(toJoinAnd);
    }		// setToJoinAnd
}	// MWFNodeNext



/*
 * @(#)MMPCOrderNodeNext.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderNodeNext.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
