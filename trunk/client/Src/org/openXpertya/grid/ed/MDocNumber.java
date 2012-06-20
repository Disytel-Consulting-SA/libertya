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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MDocNumber extends PlainDocument {

    /**
     * Constructor de la clase ...
     *
     *
     * @param displayType
     * @param format
     * @param tc
     * @param title
     */

    public MDocNumber( int displayType,DecimalFormat format,JTextComponent tc,String title ) {
        super();

        if( (format == null) || (tc == null) || (title == null) ) {
            throw new IllegalArgumentException( "MDocNumber - invalid argument" );
        }

        //

        m_displayType = displayType;
        m_format      = format;
        m_sym         = m_format.getDecimalFormatSymbols();
        m_tc          = tc;
        m_title       = title;
    }    // MDocNumber

    /** Descripción de Campos */

    private int m_displayType = 0;

    /** Descripción de Campos */

    private DecimalFormat m_format = null;

    /** Descripción de Campos */

    private DecimalFormatSymbols m_sym = null;

    /** Descripción de Campos */

    private JTextComponent m_tc = null;

    /** Descripción de Campos */

    private String m_title = null;

    /**
     * Descripción de Método
     *
     *
     * @param origOffset
     * @param string
     * @param attr
     *
     * @throws BadLocationException
     */

    public void insertString( int origOffset,String string,AttributeSet attr ) throws BadLocationException {

        // ADebug.trace(ADebug.l5_DData, "MDocNumber.insert - O=" + origOffset + " S=" + string + " L=" + string.length());

        if( (origOffset < 0) || (string == null) ) {
            throw new IllegalArgumentException( "MDocNumber.insertString - invalid argument" );
        }

        int offset = origOffset;
        int length = string.length();

        // From DataBinder (assuming correct format)

        if( length != 1 ) {
            super.insertString( offset,string,attr );

            return;
        }

        String content = getText();

        // remove all Thousands

        if( content.indexOf( m_sym.getGroupingSeparator()) != -1 ) {
            StringBuffer result = new StringBuffer();

            for( int i = 0;i < content.length();i++ ) {
                if( content.charAt( i ) == m_sym.getGroupingSeparator()) {
                    if( i < offset ) {
                        offset--;
                    }
                } else {
                    result.append( content.charAt( i ));
                }
            }

            super.remove( 0,content.length());
            super.insertString( 0,result.toString(),attr );

            //

            m_tc.setCaretPosition( offset );

            // ADebug.trace(ADebug.l6_Database, "Clear Thousands (" + m_format.toPattern() + ")" + content + " -> " + result.toString());

            content = result.toString();
        }    // remove Thousands

        char c = string.charAt( 0 );

        if( Character.isDigit( c ))    // c >= '0' && c <= '9')
        {

            // ADebug.trace(ADebug.l6_Database, "Digit=" + c);

            super.insertString( offset,string,attr );

            return;
        }

        // Plus - remove minus sign

        if( c == '+' ) {

            // ADebug.trace(ADebug.l6_Database, "Plus=" + c);
            // only positive numbers

            if( m_displayType == DisplayType.Integer ) {
                return;
            }

            if( content.charAt( 0 ) == '-' ) {
                super.remove( 0,1 );
            }
        }

        // Toggle Minus - put minus on start of string

        else if( (c == '-') || (c == m_sym.getMinusSign())) {

            // ADebug.trace(ADebug.l6_Database, "Minus=" + c);
            // no minus possible

            if( m_displayType == DisplayType.Integer ) {
                return;
            }

            // remove or add

            if( (content.length() > 0) && (content.charAt( 0 ) == '-') ) {
                super.remove( 0,1 );
            } else {
                super.insertString( 0,"-",attr );
            }
        }

        // Decimal - remove other decimals
        // Thousand - treat as Decimal

        else if( (c == m_sym.getDecimalSeparator()) || (c == m_sym.getGroupingSeparator())) {

            // ADebug.trace(ADebug.l6_Database, "decimal=" + c + " (ds=" + m_sym.getDecimalSeparator() + "; gs=" + m_sym.getGroupingSeparator() + ")");
            // no decimals on integers

            if( m_displayType == DisplayType.Integer ) {
                return;
            }

            int pos = content.indexOf( m_sym.getDecimalSeparator());

            // put decimal in

            String decimal = String.valueOf( m_sym.getDecimalSeparator());

            super.insertString( offset,decimal,attr );

            // remove other decimals

            if( pos != 0 ) {
                content = getText();

                StringBuffer result     = new StringBuffer();
                int          correction = 0;

                for( int i = 0;i < content.length();i++ ) {
                    if( content.charAt( i ) == m_sym.getDecimalSeparator()) {
                        if( i == offset ) {
                            result.append( content.charAt( i ));
                        } else if( i < offset ) {
                            correction++;
                        }
                    } else {
                        result.append( content.charAt( i ));
                    }
                }

                super.remove( 0,content.length());
                super.insertString( 0,result.toString(),attr );
                m_tc.setCaretPosition( offset - correction + 1 );
            }    // remove other decimals
        }        // decial or thousand

        // something else

        else {
            String result = VNumber.startCalculator( m_tc,getText(),m_format,m_displayType,m_title );

            super.remove( 0,content.length());
            super.insertString( 0,result,attr );
        }
    }    // insertString

    /**
     * Descripción de Método
     *
     *
     * @param origOffset
     * @param length
     *
     * @throws BadLocationException
     */

    public void remove( int origOffset,int length ) throws BadLocationException {

        // ADebug.trace(ADebug.l5_DData, "MDocNumber.remove - Offset=" + offset + " Length=" + length);

        if( (origOffset < 0) || (length < 0) ) {
            throw new IllegalArgumentException( "MDocNumber.remove - invalid argument" );
        }

        int offset = origOffset;

        if( length != 1 ) {
            super.remove( offset,length );

            return;
        }

        String content = getText();

        // remove all Thousands

        if( content.indexOf( m_sym.getGroupingSeparator()) != -1 ) {
            StringBuffer result = new StringBuffer();

            for( int i = 0;i < content.length();i++ ) {
                if( (content.charAt( i ) == m_sym.getGroupingSeparator()) && (i != origOffset) ) {
                    if( i < offset ) {
                        offset--;
                    }
                } else {
                    result.append( content.charAt( i ));
                }
            }

            super.remove( 0,content.length());
            super.insertString( 0,result.toString(),null );
            m_tc.setCaretPosition( offset );
        }    // remove Thousands

        super.remove( offset,length );
    }    // remove

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String getText() {
        Content c   = getContent();
        String  str = "";

        try {
            str = c.getString( 0,c.length() - 1 );    // cr at end
        } catch( Exception e ) {
        }

        return str;
    }    // getString
}    // MDocNumber



/*
 *  @(#)MDocNumber.java   02.07.07
 * 
 *  Fin del fichero MDocNumber.java
 *  
 *  Versión 2.2
 *
 */
