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

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRMALine extends X_M_RMALine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_RMALine_ID
     * @param trxName
     */

    public MRMALine( Properties ctx,int M_RMALine_ID,String trxName ) {
        super( ctx,M_RMALine_ID,trxName );

        if( M_RMALine_ID == 0 ) {
            setQty( Env.ONE );
        }
    }    // MRMALine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRMALine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRMALine

    /** Descripción de Campos */

    private MInOutLine m_ioLine = null;

    /**
     * Descripción de Método
     *
     *
     * @param M_InOutLine_ID
     */

    public void setM_InOutLine_ID( int M_InOutLine_ID ) {
        super.setM_InOutLine_ID( M_InOutLine_ID );
        m_ioLine = null;
    }    // setM_InOutLine_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInOutLine getShipLine() {
        if( (m_ioLine == null) && (getM_InOutLine_ID() != 0) ) {
            m_ioLine = new MInOutLine( getCtx(),getM_InOutLine_ID(),get_TrxName());
        }

        return m_ioLine;
    }    // getShipLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAmt() {
        BigDecimal amt = Env.ZERO;

        getShipLine();

        if( m_ioLine != null ) {
            if( m_ioLine.getC_OrderLine_ID() != 0 ) {
                MOrderLine ol = new MOrderLine( getCtx(),m_ioLine.getC_OrderLine_ID(),get_TrxName());

                amt = ol.getPriceActual();
            }
        }

        //

        return amt.multiply( getQty());
    }    // getAmt
}    // MRMALine



/*
 *  @(#)MRMALine.java   02.07.07
 * 
 *  Fin del fichero MRMALine.java
 *  
 *  Versión 2.2
 *
 */
