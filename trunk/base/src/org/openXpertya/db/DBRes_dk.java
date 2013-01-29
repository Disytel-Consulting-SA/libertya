/*
 * @(#)DBRes_dk.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import java.util.ListResourceBundle;

/**
 *  Connection Resource Strings
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: DBRes_dk.java,v 1.5 2005/03/11 20:29:01 jjanke Exp $
 */
public class DBRes_dk extends ListResourceBundle {

    /** Data */
    static final Object[][]	contents	= new String[][] {
        { "CConnectionDialog", "Libertya forbindelse" }, { "Name", "Navn" }, { "AppsHost", "Programvï¿½rt" }, { "AppsPort", "Programport" }, { "TestApps", "Test programserver" }, { "DBHost", "Databasevï¿½rt" }, { "DBPort", "Databaseport" }, { "DBName", "Databasenavn" }, { "DBUidPwd", "Bruger/adgangskode" }, { "ViaFirewall", "via Firewall" }, { "FWHost", "Firewall-vï¿½rt" }, { "FWPort", "Firewall-port" }, { "TestConnection", "Test database" }, { "Type", "Databasetype" }, { "BequeathConnection", "Nedarv forbindelse" }, { "Overwrite", "Overskriv" }, { "RMIoverHTTP", "Tunnel Objects via HTTP" }, { "ConnectionError", "Fejl i forbindelse" }, { "ServerNotActive", "Server er ikke aktiv" }
    };

    //~--- get methods --------------------------------------------------------

    /**
     * Get Contsnts
     * @return contents
     */
    public Object[][] getContents() {
        return contents;
    }		// getContent
}	// Res



/*
 * @(#)DBRes_dk.java   02.jul 2007
 * 
 *  Fin del fichero DBRes_dk.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
