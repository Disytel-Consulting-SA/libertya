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

public class MPaySchedule extends X_C_PaySchedule {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaySchedule_ID
     * @param trxName
     */

    public MPaySchedule( Properties ctx,int C_PaySchedule_ID,String trxName ) {
        super( ctx,C_PaySchedule_ID,trxName );

        if( C_PaySchedule_ID == 0 ) {

            // setC_PaymentTerm_ID (0);        //      Parent

            setPercentage( Env.ZERO );
            setDiscount( Env.ZERO );
            setDiscountDays( 0 );
            setGraceDays( 0 );
            setNetDays( 0 );
            setIsValid( false );
        }
    }    // MPaySchedule

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaySchedule( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaySchedule

    /** Descripción de Campos */

    public MPaymentTerm m_parent = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MPaymentTerm getParent() {
        if( m_parent == null ) {
            m_parent = new MPaymentTerm( getCtx(),getC_PaymentTerm_ID(),get_TrxName());
        }

        return m_parent;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @param parent
     */

    public void setParent( MPaymentTerm parent ) {
        m_parent = parent;
    }    // setParent

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        if( is_ValueChanged( "Percentage" )) {
            log.fine( "beforeSave" );
            setIsValid( false );
        }

		// Si existe un descuento configurado entonces debe existir un tipo de
		// aplicación
		if (getDiscount().compareTo(BigDecimal.ZERO) != 0
				&& getDiscountApplicationType() == null) {
			log.saveError("NotExistDiscountApplicationType", "");
			return false;
        }
        
        return true;
    }    // beforeSave

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
        if( newRecord || is_ValueChanged( "Percentage" )) {
            log.fine( "afterSave" );
            getParent();
            m_parent.validate();
            m_parent.save();
        }

        return success;
    }    // afterSave
}    // MPaySchedule



/*
 *  @(#)MPaySchedule.java   02.07.07
 * 
 *  Fin del fichero MPaySchedule.java
 *  
 *  Versión 2.2
 *
 */
