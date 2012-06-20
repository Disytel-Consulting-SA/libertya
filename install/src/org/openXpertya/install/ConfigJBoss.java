/*
 * @(#)ConfigJBoss.java   11.jun 2007  Versión 2.2
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



package org.openXpertya.install;

import java.io.File;

import java.net.InetAddress;

/**
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ConfigJBoss extends Config {

    /**
     * Constructor ...
     *
     *
     * @param data
     */
    public ConfigJBoss(ConfigurationData data) {
        super(data);
    }		// ConfigJBoss

    /**
     * Descripción de Método
     *
     */
    public void init() {

        p_data.setAppsServerDeployDir(getDeployDir());
        p_data.setAppsServerDeployDir(false);

        //
        p_data.setAppsServerJNPPort("1099");
        p_data.setAppsServerJNPPort(true);
        p_data.setAppsServerWebPort("80");
        p_data.setAppsServerWebPort(true);
        p_data.setAppsServerSSLPort("443");
        p_data.setAppsServerSSLPort(true);

    }		// init

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String test() {

        // AppsServer
        String	server	= p_data.getAppsServer();
        boolean	pass	= (server != null) && (server.length() > 0) && (server.toLowerCase().indexOf("localhost") == -1) &&!server.equals("127.0.0.1");
        InetAddress	appsServer	= null;
        String		error		= "Not correct: AppsServer = " + server;

        try {

            if (pass) {
                appsServer	= InetAddress.getByName(server);
            }

        } catch (Exception e) {

            error	+= " - " + e.getMessage();
            pass	= false;
        }
        if (getPanel() != null)
        	signalOK(getPanel().okAppsServer, "ErrorAppsServer", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: AppsServer = " + appsServer);
        setProperty(ConfigurationData.SERVIDOR_APPS_OXP, appsServer.getHostName());
        setProperty(ConfigurationData.TIPO_APPS_OXP, p_data.getAppsServerType());

        // Deployment Dir
        p_data.setAppsServerDeployDir(getDeployDir());

        File	deploy	= new File(p_data.getAppsServerDeployDir());

        pass	= deploy.exists();
        error	= "Not found: " + deploy;
        if (getPanel() != null)
        	signalOK(getPanel().okOXPHome, "ErrorDeployDir", pass, true, error);

        if (!pass) {
            return error;
        }

        setProperty(ConfigurationData.DEPLOY_APPS_OXP, p_data.getAppsServerDeployDir());
        log.info("OK: Deploy Directory = " + deploy);

        // JNP Port
        int	JNPPort	= p_data.getAppsServerJNPPort();

        pass	= !p_data.testPort(appsServer, JNPPort, false) && p_data.testServerPort(JNPPort);
        error	= "Not correct: JNP Port = " + JNPPort;
        if (getPanel() != null)
        	signalOK(getPanel().okJNPPort, "ErrorJNPPort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: JNPPort = " + JNPPort);
        setProperty(ConfigurationData.PUERTO_JNP_OXP, String.valueOf(JNPPort));

        // Web Port
        int	WebPort	= p_data.getAppsServerWebPort();

        pass	= !p_data.testPort("http", appsServer.getHostName(), WebPort, "/") && p_data.testServerPort(WebPort);
        error	= "Not correct: Web Port = " + WebPort;
        if (getPanel() != null)
        	signalOK(getPanel().okWebPort, "ErrorWebPort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: Web Port = " + WebPort);
        setProperty(ConfigurationData.PUERTO_WEB_OXP, String.valueOf(WebPort));

        // SSL Port
        int	sslPort	= p_data.getAppsServerSSLPort();

        pass	= !p_data.testPort("https", appsServer.getHostName(), sslPort, "/") && p_data.testServerPort(sslPort);
        error	= "Not correct: SSL Port = " + sslPort;
        if (getPanel() != null)
        	signalOK(getPanel().okSSLPort, "ErrorWebPort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: SSL Port = " + sslPort);
        setProperty(ConfigurationData.PUERTO_SSL_OXP, String.valueOf(sslPort));

        //
        return null;
    }		// test

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private String getDeployDir() {
        return p_data.getOXPHome() + File.separator + "jboss" + File.separator + "server" + File.separator + "openXpertya" + File.separator + "deploy";
    }		// getDeployDir
}	// ConfigJBoss



/*
 * @(#)ConfigJBoss.java   11.jun 2007
 * 
 *  Fin del fichero ConfigJBoss.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
