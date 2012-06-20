/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.print.layout;

import java.awt.Font;
import java.awt.Paint;
import java.util.Properties;
import java.util.regex.Pattern;

import org.openXpertya.model.MLocation;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class LocationElement extends GridElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Location_ID
     * @param font
     * @param color
     */

    public LocationElement( Properties ctx,int C_Location_ID,Font font,Paint color ) {
        super( 10,1 );    // max
        setGap( 0,0 );

        MLocation ml = MLocation.get( ctx,C_Location_ID,null );

        // log.fine("C_Location_ID=" + C_Location_ID);

        if( ml != null ) {
            int index = 0;

            if( ml.isAddressLinesReverse()) {
                setData( index++,0,ml.getCountry( true ),font,color );

                String[] lines = Pattern.compile( "$",Pattern.MULTILINE ).split( ml.getCityRegionPostal());

                for( int i = 0;i < lines.length;i++ ) {
                    setData( index++,0,lines[ i ],font,color );
                }

                if( (ml.getAddress4() != null) && (ml.getAddress4().length() > 0) ) {
                    setData( index++,0,ml.getAddress4(),font,color );
                }

                if( (ml.getAddress3() != null) && (ml.getAddress3().length() > 0) ) {
                    setData( index++,0,ml.getAddress3(),font,color );
                }

                if( (ml.getAddress2() != null) && (ml.getAddress2().length() > 0) ) {
                    setData( index++,0,ml.getAddress2(),font,color );
                }

                if( (ml.getAddress1() != null) && (ml.getAddress1().length() > 0) ) {
                    setData( index++,0,ml.getAddress1(),font,color );
                }
            } else {
                if( (ml.getAddress1() != null) && (ml.getAddress1().length() > 0) ) {
                    setData( index++,0,ml.getAddress1(),font,color );
                }

                if( (ml.getAddress2() != null) && (ml.getAddress2().length() > 0) ) {
                    setData( index++,0,ml.getAddress2(),font,color );
                }

                if( (ml.getAddress3() != null) && (ml.getAddress3().length() > 0) ) {
                    setData( index++,0,ml.getAddress3(),font,color );
                }

                if( (ml.getAddress4() != null) && (ml.getAddress4().length() > 0) ) {
                    setData( index++,0,ml.getAddress4(),font,color );
                }

                String[] lines = Pattern.compile( "$",Pattern.MULTILINE ).split( ml.getCityRegionPostal());

                for( int i = 0;i < lines.length;i++ ) {
                    setData( index++,0,lines[ i ],font,color );
                }

                setData( index++,0,ml.getCountry( true ),font,color );
            }
        }
    }    // LocationElement
}    // LocationElement



/*
 *  @(#)LocationElement.java   12.10.07
 * 
 *  Fin del fichero LocationElement.java
 *  
 *  Versión 2.2
 *
 */
