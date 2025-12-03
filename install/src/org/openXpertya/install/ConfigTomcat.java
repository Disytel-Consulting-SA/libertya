package org.openXpertya.install;

import java.io.File;
import java.net.InetAddress;

/**
 * Configuración específica para Tomcat.
 */
public class ConfigTomcat extends Config {

    /**
     * Constructor ...
     *
     * @param data
     */
    public ConfigTomcat(ConfigurationData data) {
        super(data);
    }

    /**
     * Descripción de Método
     */
    public void init() {

        p_data.setAppsServerDeployDir(getDeployDir());
        p_data.setAppsServerDeployDir(false);

        // Tomcat no utiliza JNP
        p_data.setAppsServerJNPPort("0");
        p_data.setAppsServerJNPPort(false);
        p_data.setAppsServerWebPort("8080");
        p_data.setAppsServerWebPort(true);
        p_data.setAppsServerSSLPort("8443");
        p_data.setAppsServerSSLPort(true);

    }           // init

    /**
     * Descripción de Método
     *
     * @return
     */
    public String test() {

        // AppsServer
        String  server  = p_data.getAppsServer();
        boolean pass    = (server != null) && (server.length() > 0) && (server.toLowerCase().indexOf("localhost") == -1) && !server.equals("127.0.0.1");
        InetAddress     appsServer      = null;
        String          error           = "Not correct: AppsServer = " + server;

        try {

            if (pass) {
                appsServer      = InetAddress.getByName(server);
            }

        } catch (Exception e) {

            error       += " - " + e.getMessage();
            pass        = false;
        }
        if (getPanel() != null)
                signalOK(getPanel().okAppsServer, "ErrorAppsServer", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: AppsServer = " + appsServer);
        setProperty(ConfigurationData.SERVIDOR_APPS_OXP, p_data.isUseAppsServerIP() ? appsServer.getHostAddress() : appsServer.getHostName());
        setProperty(ConfigurationData.TIPO_APPS_OXP, p_data.getAppsServerType());

        // Deployment Dir
        p_data.setAppsServerDeployDir(getDeployDir());

        File    deploy  = new File(p_data.getAppsServerDeployDir());

        pass    = deploy.exists();
        error   = "Not found: " + deploy;
        if (getPanel() != null)
                signalOK(getPanel().okOXPHome, "ErrorDeployDir", pass, true, error);

        if (!pass) {
            return error;
        }

        setProperty(ConfigurationData.DEPLOY_APPS_OXP, p_data.getAppsServerDeployDir());
        log.info("OK: Deploy Directory = " + deploy);

        // JNP Port no aplica en Tomcat
        setProperty(ConfigurationData.PUERTO_JNP_OXP, "0");
        if (getPanel() != null)
                signalOK(getPanel().okJNPPort, "ErrorJNPPort", true, false, "JNP not used in Tomcat");

        // Web Port
        int     WebPort = p_data.getAppsServerWebPort();

        pass    = !p_data.testPort("http", p_data.isUseAppsServerIP() ? appsServer.getHostAddress() : appsServer.getHostName(), WebPort, "/") && p_data.testServerPort(WebPort);
        error   = "Not correct: Web Port = " + WebPort;
        if (getPanel() != null)
                signalOK(getPanel().okWebPort, "ErrorWebPort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: Web Port = " + WebPort);
        setProperty(ConfigurationData.PUERTO_WEB_OXP, String.valueOf(WebPort));

        // SSL Port
        int     sslPort = p_data.getAppsServerSSLPort();

        pass    = !p_data.testPort("https", p_data.isUseAppsServerIP() ? appsServer.getHostAddress() : appsServer.getHostName(), sslPort, "/") && p_data.testServerPort(sslPort);
        error   = "Not correct: SSL Port = " + sslPort;
        if (getPanel() != null)
                signalOK(getPanel().okSSLPort, "ErrorWebPort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: SSL Port = " + sslPort);
        setProperty(ConfigurationData.PUERTO_SSL_OXP, String.valueOf(sslPort));

        //
        return null;
    }           // test

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     * @return
     */
    private String getDeployDir() {
        return p_data.getOXPHome() + File.separator + "tomcat" + File.separator + "webapps";
    }           // getDeployDir
}       // ConfigTomcat

