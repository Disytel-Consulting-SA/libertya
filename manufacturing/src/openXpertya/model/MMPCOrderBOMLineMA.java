/*
 * @(#)MMPCOrderBOMLineMA.java   13.jun 2007  Versión 2.2
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

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 *      Shipment Material Allocation
 *
 *  @author Victor Perez
 *  @version $Id: MMPCOrderBOMLineMA.java,v 1.1 2005/04/01 05:59:48 jjanke Exp $
 */
public class MMPCOrderBOMLineMA extends X_MPC_Order_BOMLineMA {

    /**
     *      Get Material Allocations for Line
     *      @param ctx context
     *      @param MPC_Order_BOMLine_ID line
     *      @param trxName trx
     *      @return allocations
     */
    public static MMPCOrderBOMLineMA[] get(Properties ctx, int MPC_Order_BOMLine_ID, String trxName) {

        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM MPC_Order_BOMLineMA WHERE MPC_Order_BOMLine_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, trxName);
            pstmt.setInt(1, MPC_Order_BOMLine_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMPCOrderBOMLineMA(ctx, rs, trxName));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        MMPCOrderBOMLineMA[]	retValue	= new MMPCOrderBOMLineMA[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// get

    /**
     *      Delete all Material Allocation for InOut
     *      @param M_InOut_ID shipment
     *
     * @param MPC_Order_ID
     * @param trxName
     *      @return number of rows deleted or -1 for error
     */
    public static int deleteOrderBOMLineMA(int MPC_Order_ID, String trxName) {

        String	sql	= "DELETE FROM MPC_Order_BOMLineMA ma WHERE EXISTS " + "(SELECT * FROM MPC_Order_BOMLine l WHERE l.MPC_Order_BOMLine_ID=ma.MPC_Order_BOMLine_ID" + " AND MPC_Order_ID=" + MPC_Order_ID + ")";

        return DB.executeUpdate(sql, trxName);

    }		// deleteInOutMA

    /** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MMPCOrderBOMLineMA.class);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param M_InOutLineMA_ID ignored
     * @param MPC_Order_BOMLineMA_ID
     *      @param trxName trx
     */
    public MMPCOrderBOMLineMA(Properties ctx, int MPC_Order_BOMLineMA_ID, String trxName) {

        super(ctx, MPC_Order_BOMLineMA_ID, trxName);

        if (MPC_Order_BOMLineMA_ID != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MInOutLineMA

    /**
     *      Load Cosntructor
     *      @param ctx context
     *      @param rs result set
     *      @param trxName trx
     */
    public MMPCOrderBOMLineMA(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MInOutLineMA

    /**
     *      Parent Constructor
     *      @param parent parent
     *      @param M_AttributeSetInstance_ID asi
     *      @param MovementQty qty
     */
    public MMPCOrderBOMLineMA(MMPCOrderBOMLineMA parent, int M_AttributeSetInstance_ID, BigDecimal MovementQty) {

        this(parent.getCtx(), 0, parent.get_TrxName());
        setClientOrg(parent);
        setMPC_Order_BOMLine_ID(parent.getMPC_Order_BOMLine_ID());

        //
        setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);
        setMovementQty(MovementQty);

    }		// MInOutLineMA

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MMPCOrderBOMLineMA[");

        sb.append("MPC_Order_BOMLine_ID=").append(getMPC_Order_BOMLine_ID()).append(",M_AttributeSetInstance_ID=").append(getM_AttributeSetInstance_ID()).append(", Qty=").append(getMovementQty()).append("]");

        return sb.toString();

    }		// toString
}	// MPC_Order_BOMLineMA



/*
 * @(#)MMPCOrderBOMLineMA.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderBOMLineMA.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
