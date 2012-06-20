/*
 * @(#)DBRes_pt.java   12.oct 2007  Versión 2.2
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
 *         *     Jesse Jr
 *  @version    $Id: DBRes_pt.java,v 1.5 2005/03/11 20:29:01 jjanke Exp $
 */
public class DBRes_pt extends ListResourceBundle {

    /** Data */
    static final Object[][]	contents	= new String[][] {
        { "CConnectionDialog", "Libertya Conexï¿½o" }, { "Name", "Nome" }, { "AppsHost", "Servidor de Aplicaï¿½ï¿½o" }, { "AppsPort", "Porta TCP da Aplicaï¿½ï¿½o" }, { "TestApps", "Testar Aplicaï¿½ï¿½o" }, { "DBHost", "Servidor do Banco de Dado" }, { "DBPort", "Porta TCP do Banco de Dados" }, { "DBName", "Nome do Banco de Dados" }, { "DBUidPwd", "Usuï¿½rio / Senha" }, { "ViaFirewall", "via Firewall" }, { "FWHost", "Servidor de Firewall" }, { "FWPort", "Porta TCP do Firewall" }, { "TestConnection", "Testar Banco de Dados" }, { "Type", "Tipo de Banco de Dados" }, { "BequeathConnection", "Conexï¿½o Bequeath" }, { "Overwrite", "Sobrescrever" }, { "RMIoverHTTP", "Tunnel Objects via HTTP" }, { "ConnectionError", "Erro de Conexï¿½o" }, { "ServerNotActive", "Servidor nï¿½o Ativo" }
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
 * @(#)DBRes_pt.java   02.jul 2007
 * 
 *  Fin del fichero DBRes_pt.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
