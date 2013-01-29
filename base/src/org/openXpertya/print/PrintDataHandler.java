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



package org.openXpertya.print;

import java.util.ArrayList;
import java.util.Properties;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PrintDataHandler extends DefaultHandler {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public PrintDataHandler( Properties ctx ) {
        m_ctx = ctx;
    }    // PrintDataHandler

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /** Descripción de Campos */

    private PrintData m_pd = null;

    /** Descripción de Campos */

    private String m_curPDEname = null;

    /** Descripción de Campos */

    private StringBuffer m_curPDEvalue = null;

    /** Descripción de Campos */

    private PrintData m_curPD = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PrintData getPrintData() {
        return m_pd;
    }    // getPrintData

    /**
     * Descripción de Método
     *
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     *
     * @throws org.xml.sax.SAXException
     */

    public void startElement( String uri,String localName,String qName,Attributes attributes ) throws org.xml.sax.SAXException {
        if( qName.equals( PrintData.XML_TAG )) {
            String name = attributes.getValue( PrintData.XML_ATTRIBUTE_NAME );

            if( m_pd == null ) {
                m_pd = new PrintData( m_ctx,name );
                push( m_pd );
            } else {
                PrintData temp = new PrintData( m_ctx,name );

                m_curPD.addNode( temp );
                push( temp );
            }
        } else if( qName.equals( PrintData.XML_ROW_TAG )) {
            m_curPD.addRow( false,0 );
        } else if( qName.equals( PrintDataElement.XML_TAG )) {
            m_curPDEname = attributes.getValue( PrintDataElement.XML_ATTRIBUTE_NAME );
            m_curPDEvalue = new StringBuffer();
        }
    }    // startElement

    /**
     * Descripción de Método
     *
     *
     * @param ch
     * @param start
     * @param length
     *
     * @throws SAXException
     */

    public void characters( char ch[],int start,int length ) throws SAXException {
        m_curPDEvalue.append( ch,start,length );
    }    // characters

    /**
     * Descripción de Método
     *
     *
     * @param uri
     * @param localName
     * @param qName
     *
     * @throws SAXException
     */

    public void endElement( String uri,String localName,String qName ) throws SAXException {
        if( qName.equals( PrintData.XML_TAG )) {
            pop();
        } else if( qName.equals( PrintDataElement.XML_TAG )) {
            m_curPD.addNode( new PrintDataElement( m_curPDEname,m_curPDEvalue.toString(),0 ));
        }
    }    // endElement

    /** Descripción de Campos */

    private ArrayList m_stack = new ArrayList();

    /**
     * Descripción de Método
     *
     *
     * @param newPD
     */

    private void push( PrintData newPD ) {

        // add

        m_stack.add( newPD );
        m_curPD = newPD;
    }    // push

    /**
     * Descripción de Método
     *
     */

    private void pop() {

        // remove last

        if( m_stack.size() > 0 ) {
            m_stack.remove( m_stack.size() - 1 );
        }

        // get previous

        if( m_stack.size() > 0 ) {
            m_curPD = ( PrintData )m_stack.get( m_stack.size() - 1 );
        }
    }    // pop
}    // PrintDataHandler



/*
 *  @(#)PrintDataHandler.java   23.03.06
 * 
 *  Fin del fichero PrintDataHandler.java
 *  
 *  Versión 2.2
 *
 */
