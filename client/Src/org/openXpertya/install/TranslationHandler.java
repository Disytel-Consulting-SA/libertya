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



package org.openXpertya.install;

import java.sql.Timestamp;

import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Language;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TranslationHandler extends DefaultHandler {

    /**
     * Constructor de la clase ...
     *
     *
     * @param AD_Client_ID
     */

    public TranslationHandler( int AD_Client_ID ) {
        m_AD_Client_ID = AD_Client_ID;
    }    // TranslationHandler

    /** Descripción de Campos */

    private int m_AD_Client_ID = -1;

    /** Descripción de Campos */

    private String m_AD_Language = null;

    /** Descripción de Campos */

    private boolean m_isBaseLanguage = false;

    /** Descripción de Campos */

    private String m_TableName = null;

    /** Descripción de Campos */

    private String m_updateSQL = null;

    /** Descripción de Campos */

    private String m_curID = null;

    /** Descripción de Campos */

    private String m_trl = null;

    /** Descripción de Campos */

    private String m_curColumnName = null;

    /** Descripción de Campos */

    private StringBuffer m_curValue = null;

    /** Descripción de Campos */

    private StringBuffer m_sql = null;

    /** Descripción de Campos */

    private Timestamp m_time = new Timestamp( System.currentTimeMillis());

    /** Descripción de Campos */

    private int m_updateCount = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( TranslationHandler.class );
    
    /** Variables para pasar el procesamiento por PO */
    
    M_Table table;
    PO trlPO;
    int count = 0;

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

        // log.fine( "TranslationHandler.startElement", qName);    // + " - " + uri + " - " + localName);

        if( qName.equals( Translation.XML_TAG )) {
            m_AD_Language = attributes.getValue( Translation.XML_ATTRIBUTE_LANGUAGE );
            m_isBaseLanguage = Language.isBaseLanguage( m_AD_Language );
            m_TableName      = attributes.getValue( Translation.XML_ATTRIBUTE_TABLE );
            m_updateSQL = "UPDATE " + m_TableName;

            if( !m_isBaseLanguage ) {
                m_updateSQL += "_Trl";
            }
            
            // Nombre de la tabla de traduccion
            table = new M_Table(Env.getCtx(), M_Table.getTableID(m_TableName + "_Trl"), null);
            
            m_updateSQL += " SET ";
            log.fine( "AD_Language=" + m_AD_Language + ", Base=" + m_isBaseLanguage + ", TableName=" + m_TableName );
        } else if( qName.equals( Translation.XML_ROW_TAG )) {
            m_curID = attributes.getValue( Translation.XML_ROW_ATTRIBUTE_ID );
            m_trl   = attributes.getValue( Translation.XML_ROW_ATTRIBUTE_TRANSLATED );


            // Recuperar el registro a modificar
            trlPO = table.getPO(m_TableName + "_ID = " + m_curID + " AND ad_language = '" + m_AD_Language + "'" , null);
            trlPO.set_Value("IsTranslated", m_trl);
            
            // log.finest( "ID=" + m_curID);

            m_sql = new StringBuffer();
        } else if( qName.equals( Translation.XML_VALUE_TAG )) {
            m_curColumnName = attributes.getValue( Translation.XML_VALUE_ATTRIBUTE_COLUMN );

            // log.finest( "ColumnName=" + m_curColName);

        } else {
            log.severe( "UNKNOWN TAG: " + qName );
        }

        m_curValue = new StringBuffer();
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
        m_curValue.append( ch,start,length );

        // Log.trace(Log.l6_Database+1, "TranslationHandler.characters", m_curValue.toString());

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

        // Log.trace(Log.l6_Database+1, "TranslationHandler.endElement", qName);

        if( qName.equals( Translation.XML_TAG )) {}
        else if( qName.equals( Translation.XML_ROW_TAG )) {

            // Set section

            if( m_sql.length() > 0 ) {
                m_sql.append( "," );
            }

            m_sql.append( "Updated=" ).append( DB.TO_DATE( m_time,false ));

            if( !m_isBaseLanguage ) {
                if( (m_trl != null) && ( "Y".equals( m_trl ) || "N".equals( m_trl ))) {
                    m_sql.append( ",IsTranslated='" ).append( m_trl ).append( "'" );
                } else {
                    m_sql.append( ",IsTranslated='Y'" );
                }
            }

            // Where section

            m_sql.append( " WHERE " ).append( m_TableName ).append( "_ID=" ).append( m_curID );

            if( !m_isBaseLanguage ) {
                m_sql.append( " AND AD_Language='" ).append( m_AD_Language ).append( "'" );
            }

            if( m_AD_Client_ID >= 0 ) {
                m_sql.append( " AND AD_Client_ID=" ).append( m_AD_Client_ID );
            }

            // Update section

            m_sql.insert( 0,m_updateSQL );

            // Execute

            // COMENTADO, NO PASABA POR PO!!!
           // int no = DB.executeUpdate( m_sql.toString());
            int no = 0;

            // Persistir la entrada
            if (trlPO.save() == true)
            {
            	no = 1;
            	count++;
				if (count % 1000 == 0)
					System.out.print(count);
				else if (count % 100 == 0)
					System.out.print(".");
            }

            if( no == 1 ) {
                if( CLogMgt.isLevelFinest()) {
                    log.fine( m_sql.toString());
                }

                m_updateCount++;
            } else if( no == 0 ) {
                log.warning( "Not Found - " + m_sql.toString());
            } else {
                log.severe( "Update Rows=" + no + " (Should be 1) - " + m_sql.toString());
            }
        } else if( qName.equals( Translation.XML_VALUE_TAG )) {
            if( m_sql.length() > 0 ) {
                m_sql.append( "," );
            }

            m_sql.append( m_curColumnName ).append( "=" ).append( DB.TO_STRING( m_curValue.toString()));
            
            // Setear el valor de la columna
            trlPO.set_Value(m_curColumnName, m_curValue.toString());
        }
    }    // endElement

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getUpdateCount() {
        return m_updateCount;
    }    // getUpdateCount
}    // TranslationHandler



/*
 *  @(#)TranslationHandler.java   02.07.07
 * 
 *  Fin del fichero TranslationHandler.java
 *  
 *  Versión 2.2
 *
 */
