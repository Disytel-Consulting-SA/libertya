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

public class MMovementLineConfirm extends X_M_MovementLineConfirm {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_MovementLineConfirm_ID
     * @param trxName
     */

    public MMovementLineConfirm( Properties ctx,int M_MovementLineConfirm_ID,String trxName ) {
        super( ctx,M_MovementLineConfirm_ID,trxName );

        if( M_MovementLineConfirm_ID == 0 ) {

            // setM_MovementConfirm_ID (0);    Parent
            // setM_MovementLine_ID (0);

            setConfirmedQty( Env.ZERO );
            setDifferenceQty( Env.ZERO );
            setScrappedQty( Env.ZERO );
            setTargetQty( Env.ZERO );
            setProcessed( false );
        }
    }    // M_MovementLineConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MMovementLineConfirm( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // M_MovementLineConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MMovementLineConfirm( MMovementConfirm parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setM_MovementConfirm_ID( parent.getM_MovementConfirm_ID());
    }    // MMovementLineConfirm

    /** Descripción de Campos */

    private MMovementLine m_line = null;

    /**
     * Descripción de Método
     *
     *
     * @param line
     */

    public void setMovementLine( MMovementLine line ) {
        setM_MovementLine_ID( line.getM_MovementLine_ID());
        setTargetQty( line.getMovementQty());
        setConfirmedQty( getTargetQty());    // suggestion
        m_line = line;
    }    // setMovementLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MMovementLine getLine() {
        if( m_line == null ) {
            m_line = new MMovementLine( getCtx(),getM_MovementLine_ID(),get_TrxName());
        }

        return m_line;
    }    // getLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean processLine() {
        MMovementLine line = getLine();

        line.setTargetQty( getTargetQty());
        line.setMovementQty( getConfirmedQty());
        line.setConfirmedQty( getConfirmedQty());
        line.setScrappedQty( getScrappedQty());

        return line.save( get_TrxName());
    }    // processConfirmation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isFullyConfirmed() {
        return getTargetQty().compareTo( getConfirmedQty()) == 0;
    }    // isFullyConfirmed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        return false;
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Calculate Difference = Target - Confirmed - Scrapped

        BigDecimal difference = getTargetQty();

        difference = difference.subtract( getConfirmedQty());
        difference = difference.subtract( getScrappedQty());
        setDifferenceQty( difference );

        //

        return true;
    }    // beforeSave
}    // M_MovementLineConfirm



/*
 *  @(#)MMovementLineConfirm.java   02.07.07
 * 
 *  Fin del fichero MMovementLineConfirm.java
 *  
 *  Versión 2.2
 *
 */
