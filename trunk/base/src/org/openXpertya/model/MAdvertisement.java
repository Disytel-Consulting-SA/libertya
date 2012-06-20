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
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MAdvertisement extends X_W_Advertisement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param W_Advertisement_ID
     * @param trxName
     */

    public MAdvertisement( Properties ctx,int W_Advertisement_ID,String trxName ) {
        super( ctx,W_Advertisement_ID,trxName );
    }    // MAdvertisement

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MAdvertisement( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MAdvertisement

    /** Descripción de Campos */

    private MClickCount m_clickCount = null;

    /** Descripción de Campos */

    private MCounterCount m_counterCount = null;

    /** Descripción de Campos */

    private int m_SalesRep_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MClickCount getMClickCount() {
        if( getW_ClickCount_ID() == 0 ) {
            return null;
        }

        if( m_clickCount == null ) {
            m_clickCount = new MClickCount( getCtx(),getW_ClickCount_ID(),get_TrxName());
        }

        return m_clickCount;
    }    // MClickCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getClickTargetURL() {
        getMClickCount();

        if( m_clickCount == null ) {
            return "-";
        }

        return m_clickCount.getTargetURL();
    }    // getClickTargetURL

    /**
     * Descripción de Método
     *
     *
     * @param TargetURL
     */

    public void setClickTargetURL( String TargetURL ) {
        getMClickCount();

        if( m_clickCount == null ) {
            m_clickCount = new MClickCount( this );
        }

        if( m_clickCount != null ) {
            m_clickCount.setTargetURL( TargetURL );
            m_clickCount.save( get_TrxName());
        }
    }    // getClickTargetURL

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ValueNamePair[] getClickCountWeek() {
        getMClickCount();

        if( m_clickCount == null ) {
            return new ValueNamePair[ 0 ];
        }

        return m_clickCount.getCountWeek();
    }    // getClickCountWeek

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MCounterCount getMCounterCount() {
        if( getW_CounterCount_ID() == 0 ) {
            return null;
        }

        if( m_counterCount == null ) {
            m_counterCount = new MCounterCount( getCtx(),getW_CounterCount_ID(),get_TrxName());
        }

        return m_counterCount;
    }    // MCounterCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSalesRep_ID() {
        if( m_SalesRep_ID == 0 ) {
            m_SalesRep_ID = getAD_User_ID();

            if( m_SalesRep_ID == 0 ) {
                m_SalesRep_ID = DB.getSQLValue( null,"SELECT AD_User_ID FROM AD_User " + "WHERE C_BPartner_ID=? AND IsActive='Y' ORDER BY 1",getC_BPartner_ID());
            }
        }

        return m_SalesRep_ID;
    }    // getSalesRep_ID
}    // MAdvertisement



/*
 *  @(#)MAdvertisement.java   02.07.07
 * 
 *  Fin del fichero MAdvertisement.java
 *  
 *  Versión 2.2
 *
 */
