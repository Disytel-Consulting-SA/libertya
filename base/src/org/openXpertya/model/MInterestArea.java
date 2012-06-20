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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MInterestArea extends X_R_InterestArea {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_InterestArea_ID
     * @param trxName
     */

    public MInterestArea( Properties ctx,int R_InterestArea_ID,String trxName ) {
        super( ctx,R_InterestArea_ID,trxName );

        if( R_InterestArea_ID == 0 ) {

            // setName (null);
            // setR_InterestArea_ID (0);

        }
    }    // MInterestArea

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MInterestArea( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MInterestArea

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MInterestArea[" ).append( getID()).append( " - " ).append( getName()).append( "]" );

        return sb.toString();
    }

    /** Descripción de Campos */

    private int m_AD_User_ID = -1;

    /** Descripción de Campos */

    private MContactInterest m_ci = null;

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setSubscriptionInfo( int AD_User_ID ) {
        m_AD_User_ID = AD_User_ID;
        m_ci         = MContactInterest.get( getCtx(),getR_InterestArea_ID(),AD_User_ID );
    }    // setSubscription

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    public void setAD_User_ID( int AD_User_ID ) {
        m_AD_User_ID = AD_User_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_User_ID() {
        return m_AD_User_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getSubscribeDate() {
        if( m_ci != null ) {
            return m_ci.getSubscribeDate();
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getOptOutDate() {
        if( m_ci != null ) {
            return m_ci.getOptOutDate();
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSubscribed() {
        if( (m_AD_User_ID <= 0) || (m_ci == null) ) {
            return false;
        }

        // We have a BPartner Contact

        return m_ci.isSubscribed();
    }    // isSubscribed
}    // MInterestArea



/*
 *  @(#)MInterestArea.java   02.07.07
 * 
 *  Fin del fichero MInterestArea.java
 *  
 *  Versión 2.2
 *
 */
