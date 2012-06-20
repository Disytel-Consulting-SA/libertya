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



package org.openXpertya.grid.ed;

import java.util.logging.Level;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MDocTime extends PlainDocument {

    /**
     * Constructor de la clase ...
     *
     *
     * @param isHour
     * @param is12Hour
     */

    public MDocTime( boolean isHour,boolean is12Hour ) {
        super();
        m_isHour   = isHour;
        m_is12Hour = is12Hour;
    }    // MDocTime

    /** Descripción de Campos */

    private boolean m_isHour;

    /** Descripción de Campos */

    private boolean m_is12Hour;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MDocTime.class );

    /**
     * Descripción de Método
     *
     *
     * @param offset
     * @param string
     * @param attr
     *
     * @throws BadLocationException
     */

    public void insertString( int offset,String string,AttributeSet attr ) throws BadLocationException {

        // log.fine( "MDocTime.insertString - Offset=" + offset
        // + ", String=" + string + ", Attr=" + attr       + ", Text=" + getText() + ", Length=" + getText().length());

        // manual entry
        // DBTextDataBinder.updateText sends stuff at once

        if( (string != null) && (string.length() == 1) ) {

            // ignore if too long

            if( offset > 2 ) {
                return;
            }

            // is it a digit ?

            if( !Character.isDigit( string.charAt( 0 ))) {
                log.config( "No Digit=" + string );

                return;
            }

            // resulting string

            char[] cc = getText().toCharArray();

            cc[ offset ] = string.charAt( 0 );

            String result = new String( cc );
            int    i      = 0;

            try {
                i = Integer.parseInt( result.trim());
            } catch( Exception e ) {
                log.log( Level.SEVERE,e.toString());
            }

            if( i < 0 ) {
                log.config( "Invalid value: " + i );

                return;
            }

            // Minutes

            if( !m_isHour && (i > 59) ) {
                log.config( "Invalid minute value: " + i );

                return;
            }

            // Hour

            if( m_isHour && m_is12Hour && (i > 12) ) {
                log.config( "Invalid 12 hour value: " + i );

                return;
            }

            if( m_isHour &&!m_is12Hour && (i > 24) ) {
                log.config( "Invalid 24 hour value: " + i );

                return;
            }

            //
            // super.remove(offset, 1);        //      replace current position

        }

        // Set new character

        super.insertString( offset,string,attr );
    }    // insertString

    /**
     * Descripción de Método
     *
     *
     * @param offset
     * @param length
     *
     * @throws BadLocationException
     */

    public void remove( int offset,int length ) throws BadLocationException {

        // log.fine( "MDocTime.remove - Offset=" + offset + ", Length=" + length);

        super.remove( offset,length );
    }    // deleteString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getText() {
        StringBuffer sb = new StringBuffer();

        try {
            sb.append( getContent().getString( 0,getContent().length() - 1 ));    // cr at end
        } catch( Exception e ) {
        }

        while( sb.length() < 2 ) {
            sb.insert( 0,' ' );
        }

        return sb.toString();
    }    // getString
}    // MDocTime



/*
 *  @(#)MDocTime.java   02.07.07
 * 
 *  Fin del fichero MDocTime.java
 *  
 *  Versión 2.2
 *
 */
