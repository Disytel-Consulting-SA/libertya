/*
 * @(#)XML2AD.java   14.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



/*package ;*/

// TODO: add package.
package org.openXpertya.mfg.util;

import java.io.*;

import java.math.BigDecimal;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.db.*;
import org.openXpertya.mfg.util.*;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

/**
 *  XML2AD Compilo tool.
 *
 *  @author Marco LOMBARDO,Victor Perez , lombardo@mayking.com , victor.perez@e-evolution.com
 */
public class XML2AD {

    /** Logger */

    // private Logger log = Logger.getCLogger(getClass());

    /** Static Logger */
    private static CLogger	log	= CLogger.getCLogger(XML2AD.class);

    /**
     *  Uses XML2ADHandler to update AD.
     *  @param fileName xml file to read
     *  @return status message
     */
    public String importXML(String fileName) {

        log.info("importXML:" + fileName);

        File	in	= new File(fileName);

        if (!in.exists()) {

            String	msg	= "File does not exist: " + fileName;

            log.log(Level.SEVERE, "importXML:" + msg);

            return msg;
        }

        try {

            XML2ADHandler	handler	= new XML2ADHandler();
            SAXParserFactory	factory	= SAXParserFactory.newInstance();
            SAXParser		parser	= factory.newSAXParser();

            parser.parse(in, handler);

            return "OK.";

        } catch (Exception e) {

            log.log(Level.SEVERE, "importXML:", e);

            return e.toString();
        }
    }

    /**
     *
     *  @param args XMLfile host port db username password
     */
    public static void main(String[] args) {

        if (args.length < 1) {

            System.out.println("Please give the file name to read as first parameter.");
            System.exit(1);
        }

        String	file	= args[0];

        // org.openXpertya.OpenXpertya.startupClient();
        org.openXpertya.OpenXpertya.startupEnvironment(true);

        // Force connection if there are enough parameters. Else we work with OpenXpertya.properties
        if (args.length >= 6) {

            CConnection	cc	= CConnection.get(Database.DB_ORACLE, args[1], Integer.valueOf(args[2]).intValue(), args[3], args[4], args[5]);

            System.out.println("DB UserID:" + cc.getDbUid());
            DB.setDBTarget(cc);
        }

        // Adjust trace level. Or it will be taken from OpenXpertya.properties
        // TODO: have a parameter for the trace level.
        // Log.setTraceLevel(2);
        // Ini.setProperty(Ini.P_DEBUGLEVEL, String.valueOf(2));
        XML2AD	impXML	= new XML2AD();

        impXML.importXML(file);
        System.exit(0);

    }		// main
}	// XML2AD



//Marco LOMBARDO, 2004-08-20, Italy.
//lombardo@mayking.com


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
