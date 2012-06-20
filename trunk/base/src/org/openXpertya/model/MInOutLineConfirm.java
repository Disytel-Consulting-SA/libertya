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
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInOutLineConfirm extends X_M_InOutLineConfirm {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_InOutLineConfirm_ID
     * @param trxName
     */

    public MInOutLineConfirm( Properties ctx,int M_InOutLineConfirm_ID,String trxName ) {
        super( ctx,M_InOutLineConfirm_ID,trxName );

        if( M_InOutLineConfirm_ID == 0 ) {

            // setM_InOutConfirm_ID (0);
            // setM_InOutLine_ID (0);
            // setTargetQty (Env.ZERO);
            // setConfirmedQty (Env.ZERO);

            setDifferenceQty( Env.ZERO );
            setScrappedQty( Env.ZERO );
            setProcessed( false );
        }
    }    // MInOutLineConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInOutLineConfirm( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInOutLineConfirm

    /**
     * Constructor de la clase ...
     *
     *
     * @param header
     */

    public MInOutLineConfirm( MInOutConfirm header ) {
        this( header.getCtx(),0,header.get_TrxName());
        setClientOrg( header );
        setM_InOutConfirm_ID( header.getM_InOutConfirm_ID());
    }    // MInOutLineConfirm

    /** Descripción de Campos */

    private MInOutLine m_line = null;

    /**
     * Descripción de Método
     *
     *
     * @param line
     */

    public void setInOutLine( MInOutLine line ) {
        setM_InOutLine_ID( line.getM_InOutLine_ID());
        setTargetQty( line.getMovementQty());    // Confirmations in Storage UOM
        setConfirmedQty( getTargetQty());        // suggestion
        m_line = line;
    }    // setInOutLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInOutLine getLine() {
        if( m_line == null ) {
            m_line = new MInOutLine( getCtx(),getM_InOutLine_ID(),get_TrxName());
        }

        return m_line;
    }    // getLine

    /**
     * Descripción de Método
     *
     *
     * @param isSOTrx
     * @param confirmType
     *
     * @return
     */

    public boolean processLine( boolean isSOTrx,String confirmType ) {
        MInOutLine line = getLine();

        // Customer

        if( MInOutConfirm.CONFIRMTYPE_CustomerConfirmation.equals( confirmType )) {
            line.setConfirmedQty( getConfirmedQty());
        }

        // Drop Ship

        else if( MInOutConfirm.CONFIRMTYPE_DropShipConfirm.equals( confirmType )) {}

        // Pick or QA

        else if( MInOutConfirm.CONFIRMTYPE_PickQAConfirm.equals( confirmType )) {
            line.setTargetQty( getTargetQty());
            line.setMovementQty( getConfirmedQty());    // Entered NOT changed
            line.setPickedQty( getConfirmedQty());

            //

            line.setScrappedQty( getScrappedQty());
        }

        // Ship or Receipt

        else if( MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm.equals( confirmType )) {
            line.setTargetQty( getTargetQty());

            BigDecimal qty = getConfirmedQty();

            if( !isSOTrx ) {               // In PO, we have the responsibility for scapped
                qty = qty.add( getScrappedQty());
            }

            line.setMovementQty( qty );    // Entered NOT changed

            //

            line.setScrappedQty( getScrappedQty());
        }

        // Vendor

        else if( MInOutConfirm.CONFIRMTYPE_VendorConfirmation.equals( confirmType )) {
            line.setConfirmedQty( getConfirmedQty());
        }

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
        log.saveError( "Error",Msg.getMsg( getCtx(),"CannotDelete" ));

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
}    // MInOutLineConfirm



/*
 *  @(#)MInOutLineConfirm.java   02.07.07
 * 
 *  Fin del fichero MInOutLineConfirm.java
 *  
 *  Versión 2.2
 *
 */
