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



package org.openXpertya.apps;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Date;

import org.openXpertya.print.CPaper;
import org.openXpertya.print.PrintUtil;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintScreenPainter implements Pageable,Printable {

    /**
     * Constructor de la clase ...
     *
     *
     * @param element
     */

    public PrintScreenPainter( Window element ) {
        m_element = element;
    }    // PrintScreenPainter

    /** Descripción de Campos */

    private Window m_element;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNumberOfPages() {
        return 1;
    }    // getNumberOfPages

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     *
     * @throws java.lang.IndexOutOfBoundsException
     */

    public Printable getPrintable( int pageIndex ) throws java.lang.IndexOutOfBoundsException {
        return this;
    }    // getPrintable

    /**
     * Descripción de Método
     *
     *
     * @param pageIndex
     *
     * @return
     *
     * @throws java.lang.IndexOutOfBoundsException
     */

    public PageFormat getPageFormat( int pageIndex ) throws java.lang.IndexOutOfBoundsException {
        CPaper paper = new CPaper( false );

        return paper.getPageFormat();
    }    // getPageFormat

    /**
     * Descripción de Método
     *
     *
     * @param graphics
     * @param pageFormat
     * @param pageIndex
     *
     * @return
     *
     * @throws PrinterException
     */

    public int print( Graphics graphics,PageFormat pageFormat,int pageIndex ) throws PrinterException {

        // log.config( "PrintScreenPainter.print " + pageIndex, "ClipBounds=" + graphics.getClipBounds());

        if( pageIndex > 0 ) {
            return Printable.NO_SUCH_PAGE;
        }

        //

        Graphics2D g2 = ( Graphics2D )graphics;

        // Start position - top of page

        g2.translate( pageFormat.getImageableX(),pageFormat.getImageableY());

        // Print Header

        String header = Msg.getMsg( Env.getCtx(),"PrintScreen" ) + " - " + DisplayType.getDateFormat( DisplayType.DateTime ).format( new Date());
        int y = g2.getFontMetrics().getHeight();    // leading + ascent + descent

        g2.drawString( header,0,y );

        // Leave one row free

        g2.translate( 0,2 * y );

        double xRatio = pageFormat.getImageableWidth() / m_element.getSize().width;
        double yRatio = ( pageFormat.getImageableHeight() - 2 * y ) / m_element.getSize().height;

        // Sacle evenly, but don't inflate

        double ratio = Math.min( Math.min( xRatio,yRatio ),1.0 );

        g2.scale( ratio,ratio );

        // Print Element

        m_element.printAll( g2 );

        return Printable.PAGE_EXISTS;
    }    // print

    /**
     * Descripción de Método
     *
     *
     * @param element
     */

    public static void printScreen( Window element ) {
        PrintUtil.print( new PrintScreenPainter( element ),null,"PrintScreen",1,false );
    }    // printScreen
}    // PrintScreenPainter



/*
 *  @(#)PrintScreenPainter.java   02.07.07
 * 
 *  Fin del fichero PrintScreenPainter.java
 *  
 *  Versión 2.2
 *
 */
