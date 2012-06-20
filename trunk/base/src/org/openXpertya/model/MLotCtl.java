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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MLotCtl extends X_M_LotCtl {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_LotCtl_ID
     * @param trxName
     */

    public MLotCtl( Properties ctx,int M_LotCtl_ID,String trxName ) {
        super( ctx,M_LotCtl_ID,trxName );

        if( M_LotCtl_ID == 0 ) {

            // setM_LotCtl_ID (0);

            setStartNo( 1 );
            setCurrentNext( 1 );
            setIncrementNo( 1 );

            // setName (null);

        }
    }    // MLotCtl

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MLotCtl( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MLotCtl

    /**
     * Descripción de Método
     *
     *
     * @param M_Product_ID
     *
     * @return
     */

    public MLot createLot( int M_Product_ID ) {
        StringBuffer name = new StringBuffer();

        if( getPrefix() != null ) {
            name.append( getPrefix());
        }

        int no = getCurrentNext();

        name.append( no );

        if( getSuffix() != null ) {
            name.append( getSuffix());
        }

        //

        no += getIncrementNo();
        setCurrentNext( no );
        save();

        //

        MLot retValue = new MLot( this,M_Product_ID,name.toString());

        retValue.save();

        return retValue;
    }    // createLot
}    // MLotCtl



/*
 *  @(#)MLotCtl.java   02.07.07
 * 
 *  Fin del fichero MLotCtl.java
 *  
 *  Versión 2.2
 *
 */
