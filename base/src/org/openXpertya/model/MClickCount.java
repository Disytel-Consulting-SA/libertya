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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MClickCount extends X_W_ClickCount {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param W_ClickCount_ID
     * @param trxName
     */

    public MClickCount( Properties ctx,int W_ClickCount_ID,String trxName ) {
        super( ctx,W_ClickCount_ID,trxName );

        if( W_ClickCount_ID == 0 ) {
            setCounter( 0 );

            // setName (null);
            // setTargetURL (null);

        }
    }    // MClickCount

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MClickCount( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MClickCount

    /**
     * Constructor de la clase ...
     *
     *
     * @param ad
     */

    public MClickCount( MAdvertisement ad ) {
        this( ad.getCtx(),0,ad.get_TrxName());
        setName( ad.getName());
        setTargetURL( "#" );
        setC_BPartner_ID( ad.getC_BPartner_ID());
    }    // MClickCount

    /** Descripción de Campos */

    private SimpleDateFormat m_dateFormat = DisplayType.getDateFormat( DisplayType.Date );

    /** Descripción de Campos */

    private DecimalFormat m_intFormat = DisplayType.getNumberFormat( DisplayType.Integer );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MClick[] getMClicks() {
        ArrayList list = new ArrayList();

        //

        MClick[] retValue = new MClick[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getMClicks

    /**
     * Descripción de Método
     *
     *
     * @param DateFormat
     *
     * @return
     */

    protected ValueNamePair[] getCount( String DateFormat ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT TRUNC(Created, '" + DateFormat + "'), Count(*) " + "FROM W_Click " + "WHERE W_ClickCount_ID=? " + "GROUP BY TRUNC(Created, '" + DateFormat + "')";

        //

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getW_ClickCount_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String        value = m_dateFormat.format( rs.getTimestamp( 1 ));
                String        name  = m_intFormat.format( rs.getInt( 2 ));
                ValueNamePair pp    = new ValueNamePair( value,name );

                list.add( pp );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,sql,ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        ValueNamePair[] retValue = new ValueNamePair[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getCount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ValueNamePair[] getCountQuarter() {
        return getCount( "Q" );
    }    // getCountQuarter

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ValueNamePair[] getCountMonth() {
        return getCount( "MM" );
    }    // getCountMonth

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ValueNamePair[] getCountWeek() {
        return getCount( "DY" );
    }    // getCountWeek

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ValueNamePair[] getCountDay() {
        return getCount( "J" );
    }    // getCountDay
}    // MClickCount



/*
 *  @(#)MClickCount.java   02.07.07
 * 
 *  Fin del fichero MClickCount.java
 *  
 *  Versión 2.2
 *
 */
