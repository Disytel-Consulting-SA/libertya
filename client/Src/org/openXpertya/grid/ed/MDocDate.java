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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MDocDate extends PlainDocument implements CaretListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param displayType
     * @param format
     * @param tc
     * @param title
     */

    public MDocDate( int displayType,SimpleDateFormat format,JTextComponent tc,String title ) {
        super();
        m_displayType = displayType;
        m_tc          = tc;
        m_tc.addCaretListener( this );

        //

        m_format = format;

        if( m_format == null ) {
            m_format = new SimpleDateFormat();
        }

        m_format.setLenient( false );

        // Mark delimiters as '^' in Pattern

        char[] pattern = m_format.toPattern().toCharArray();

        for( int i = 0;i < pattern.length;i++ ) {

            // do we have a delimiter?

            if( "Mdy".indexOf( pattern[ i ] ) == -1 ) {
                pattern[ i ] = DELIMITER;
            }
        }

        m_mask = new String( pattern );

        //

        m_title = title;

        if( m_title == null ) {
            m_title = "";
        }
    }    // MDocDate

    /** Descripción de Campos */

    private JTextComponent m_tc;

    /** Descripción de Campos */

    private SimpleDateFormat m_format;

    /** Descripción de Campos */

    private String m_mask;

    /** Descripción de Campos */

    private static final char DELIMITER = '^';

    // for Calendar

    /** Descripción de Campos */

    private String m_title;

    /** Descripción de Campos */

    private int m_displayType;

    /** Descripción de Campos */

    private int m_lastDot = 0;    // last dot position

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MDocDate.class );

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

        // log.fine( "MDocDate.insertString - Offset=" + offset
        // + " String=" + string + " Attr=" + attr
        // + " Text=" + getText() + " Length=" + getText().length());

        // manual entry
        // DBTextDataBinder.updateText sends stuff at once - length=8

        if( (string != null) && (string.length() == 1) ) {

            // ignore if too long

            if( offset >= m_mask.length()) {
                return;
            }

            // is it an empty field?

            int length = getText().length();

            if( (offset == 0) && (length == 0) ) {
                Date   today   = new Date( System.currentTimeMillis());
                String dateStr = m_format.format( today );

                super.insertString( 0,string + dateStr.substring( 1 ),attr );
                m_tc.setCaretPosition( 1 );

                return;
            }

            // is it a digit ?

            try {
                Integer.parseInt( string );
            } catch( Exception pe ) {
                startDateDialog();

                return;
            }

            // try to get date in field, if invalid, get today's

            /*
             * try
             * {
             *       char[] cc = getText().toCharArray();
             *       cc[offset] = string.charAt(0);
             *       m_format.parse(new String(cc));
             * }
             * catch (ParseException pe)
             * {
             *       startDateDialog();
             *       return;
             * }
             */

            // positioned before the delimiter - jump over delimiter

            if( (offset != m_mask.length() - 1) && (m_mask.charAt( offset + 1 ) == DELIMITER) ) {
                m_tc.setCaretPosition( offset + 2 );
            }

            // positioned at the delimiter

            if( m_mask.charAt( offset ) == DELIMITER ) {
                offset++;
                m_tc.setCaretPosition( offset + 1 );
            }

            super.remove( offset,1 );    // replace current position
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

        // log.fine( "MDocDate.remove - Offset=" + offset
        // + " Length=" + length);

        // begin of string

        if( (offset == 0) || (length == 0) ) {

            // empty the field

            if( (length == m_mask.length()) || (length == 0) ) {
                super.remove( offset,length );
            }

            return;
        }

        // one position behind delimiter

        if( (offset - 1 >= 0) && (offset - 1 < m_mask.length()) && (m_mask.charAt( offset - 1 ) == DELIMITER) ) {
            if( offset - 2 >= 0 ) {
                m_tc.setCaretPosition( offset - 2 );
            } else {
                return;
            }
        } else {
            m_tc.setCaretPosition( offset - 1 );
        }
    }    // deleteString

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void caretUpdate( CaretEvent e ) {

        // Selection

        if( e.getDot() != e.getMark()) {
            m_lastDot = e.getDot();

            return;
        }

        //
        // log.fine( "MDocDate.caretUpdate - Dot=" + e.getDot()
        // + ", Mark=" + e.getMark());

        // Is the current position a fixed character?

        if( (e.getDot() + 1 > m_mask.length()) || (m_mask.charAt( e.getDot()) != DELIMITER) ) {
            m_lastDot = e.getDot();

            return;
        }

        // Direction?

        int newDot = -1;

        if( m_lastDot > e.getDot()) {    // <-
            newDot = e.getDot() - 1;
        } else {                         // -> (or same)
            newDot = e.getDot() + 1;
        }

        if( e.getDot() == 0 ) {                             // first
            newDot = 1;
        } else if( e.getDot() == m_mask.length() - 1 ) {    // last
            newDot = e.getDot() - 1;
        }

        //
        // log.fine( "OnFixedChar=" + m_mask.charAt(e.getDot())
        // + ", newDot=" + newDot + ", last=" + m_lastDot);
        //

        m_lastDot = e.getDot();

        if( (newDot >= 0) && (newDot < getText().length())) {
            m_tc.setCaretPosition( newDot );
        }
    }    // caretUpdate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getText() {
        String str = "";

        try {
            str = getContent().getString( 0,getContent().length() - 1 );    // cr at end
        } catch( Exception e ) {
            str = "";
        }

        return str;
    }    // getString

    /**
     * Descripción de Método
     *
     */

    private void startDateDialog() {
        log.config( "MDocDate.startDateDialog" );

        // Date Dialog

        String    result = getText();
        Timestamp ts     = null;

        try {
            ts = new Timestamp( m_format.parse( result ).getTime());
        } catch( Exception pe ) {
            ts = new Timestamp( System.currentTimeMillis());
        }

        ts     = VDate.startCalendar( m_tc,ts,m_format,m_displayType,m_title );
        result = m_format.format( ts );

        // move to field

        try {
            super.remove( 0,getText().length());
            super.insertString( 0,result,null );
        } catch( BadLocationException ble ) {
            log.log( Level.SEVERE,"MDocDate.startDateDialog",ble );
        }
    }    // startDateDialog
}    // MDocDate



/*
 *  @(#)MDocDate.java   02.07.07
 * 
 *  Fin del fichero MDocDate.java
 *  
 *  Versión 2.2
 *
 */
