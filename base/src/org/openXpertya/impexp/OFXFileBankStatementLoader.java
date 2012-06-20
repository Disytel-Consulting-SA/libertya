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



package org.openXpertya.impexp;

import java.io.FileInputStream;

import org.openXpertya.model.MBankStatementLoader;
import org.xml.sax.SAXException;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class OFXFileBankStatementLoader extends OFXBankStatementHandler implements BankStatementLoaderInterface {

    /**
     * Descripción de Método
     *
     *
     * @param controller
     *
     * @return
     */

    public boolean init( MBankStatementLoader controller ) {
        boolean         result   = false;
        FileInputStream m_stream = null;

        try {

            // Try to open the file specified as a process parameter

            if( controller.getLocalFileName() != null ) {
                m_stream = new FileInputStream( controller.getLocalFileName());
            }

            // Try to open the file specified as part of the loader configuration

            else if( controller.getFileName() != null ) {
                m_stream = new FileInputStream( controller.getFileName());
            } else {
                return result;
            }

            if( !super.init( controller )) {
                return result;
            }

            if( m_stream == null ) {
                return result;
            }

            result = attachInput( m_stream );
        } catch( Exception e ) {
            m_errorMessage     = "ErrorReadingData";
            m_errorDescription = "";
        }

        return result;
    }    // init

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

        /*
         * There are no additional things to do when importing from file.
         * All data is handled by OFXBankStatementHandler
         */

        super.characters( ch,start,length );
    }    // characterS
}    // OFXFileBankStatementLoader



/*
 *  @(#)OFXFileBankStatementLoader.java   02.07.07
 * 
 *  Fin del fichero OFXFileBankStatementLoader.java
 *  
 *  Versión 2.2
 *
 */
