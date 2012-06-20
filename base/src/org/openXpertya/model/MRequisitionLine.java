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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRequisitionLine extends X_M_RequisitionLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_RequisitionLine_ID
     * @param trxName
     */

    public MRequisitionLine( Properties ctx,int M_RequisitionLine_ID,String trxName ) {
        super( ctx,M_RequisitionLine_ID,trxName );

        if( M_RequisitionLine_ID == 0 ) {

            // setM_Requisition_ID (0);

            setLine( 0 );    // @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM M_RequisitionLine WHERE M_Requisition_ID=@M_Requisition_ID@
            setLineNetAmt( Env.ZERO );
            setPriceActual( Env.ZERO );
            setQty( Env.ONE );    // 1
        }
    }                             // MRequisitionLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequisitionLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequisitionLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param req
     */

    public MRequisitionLine( MRequisition req ) {
        this( req.getCtx(),0,req.get_TrxName());
        setClientOrg( req );
        setM_Requisition_ID( req.getM_Requisition_ID());
        m_M_PriceList_ID = req.getM_PriceList_ID();
    }    // MRequisitionLine

    /** Descripción de Campos */

    private int m_M_PriceList_ID = 0;

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        return m_C_BPartner_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param partner_ID
     */

    public void setC_BPartner_ID( int partner_ID ) {
        m_C_BPartner_ID = partner_ID;
    }

    /**
     * Descripción de Método
     *
     */

    public void setPrice() {
        if( getM_Product_ID() == 0 ) {
            return;
        }

        if( m_M_PriceList_ID == 0 ) {
            log.log( Level.SEVERE,"setPrice - PriceList unknown!" );

            return;
        }

        setPrice( m_M_PriceList_ID );
    }    // setPrice

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_ID
     */

    public void setPrice( int M_PriceList_ID ) {
        if( getM_Product_ID() == 0 ) {
            return;
        }

        //

        log.fine( "M_PriceList_ID=" + M_PriceList_ID );

        boolean         isSOTrx = false;
        MProductPricing pp      = new MProductPricing( getM_Product_ID(),getC_BPartner_ID(),getQty(),isSOTrx );

        pp.setM_PriceList_ID( M_PriceList_ID );

        // pp.setPriceDate(getDateOrdered());
        //

        setPriceActual( pp.getPriceStd());
    }    // setPrice

    /**
     * Descripción de Método
     *
     */

    public void setLineNetAmt() {
        BigDecimal lineNetAmt = getQty().multiply( getPriceActual());

        super.setLineNetAmt( lineNetAmt );
    }    // setLineNetAmt

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( getLine() == 0 ) {
            String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM M_RequisitionLine WHERE M_Requisition_ID=?";
            int ii = DB.getSQLValue( get_TrxName(),sql,getM_Requisition_ID());

            setLine( ii );
        }

        if( getPriceActual().compareTo( Env.ZERO ) == 0 ) {
            setPrice();
        }

        setLineNetAmt();

        return true;
    }    // beforeSave

    // begin e-evolution vpj-cd 10/30/2004

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        MMPCMRP.M_RequisitionLine( this,get_TrxName(),true );

        return true;
    }

    // end e-evolution vpj-cd 10/30/2004

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        if( !success ) {
            return success;
        }

        // begin e-evolution vpj-cd 10/30/2004

        MMPCMRP.M_RequisitionLine( this,get_TrxName(),false );

        // end e-evolution vpj-cd 10/30/2004

        return updateHeader();
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( !success ) {
            return success;
        }

        // start e-evolution vpj-cd 10/30/2004
        // return updateHeader();

        return true;

        // end e-evolution vpj-cd 10/30/2004

    }    // afterDelete

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean updateHeader() {
        log.fine( "updateHeader" );

        String sql = "UPDATE M_Requisition r" + " SET TotalLines=" + "(SELECT SUM(LineNetAmt) FROM M_RequisitionLine rl " + "WHERE r.M_Requisition_ID=rl.M_Requisition_ID) " + "WHERE M_Requisition_ID=" + getM_Requisition_ID();
        int no = DB.executeUpdate( sql,get_TrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"afterSave - Header update #" + no );
        }

        return no == 1;
    }    // updateHeader
}    // MRequisitionLine



/*
 *  @(#)MRequisitionLine.java   02.07.07
 * 
 *  Fin del fichero MRequisitionLine.java
 *  
 *  Versión 2.2
 *
 */
