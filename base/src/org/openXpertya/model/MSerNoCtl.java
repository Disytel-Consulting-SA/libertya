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

public class MSerNoCtl extends X_M_SerNoCtl {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_SerNoCtl_ID
     * @param trxName
     */

    public MSerNoCtl( Properties ctx,int M_SerNoCtl_ID,String trxName ) {
        super( ctx,M_SerNoCtl_ID,trxName );

        if( M_SerNoCtl_ID == 0 ) {

            // setM_SerNoCtl_ID (0);

            setStartNo( 1 );
            setCurrentNext( 1 );
            setIncrementNo( 1 );

            // setName (null);

        }
    }    // MSerNoCtl

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MSerNoCtl( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MSerNoCtl

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String createSerNo() {
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

        return name.toString();
    }    // createSerNo
}    // MSerNoCtl



/*
 *  @(#)MSerNoCtl.java   02.07.07
 * 
 *  Fin del fichero MSerNoCtl.java
 *  
 *  Versión 2.2
 *
 */
