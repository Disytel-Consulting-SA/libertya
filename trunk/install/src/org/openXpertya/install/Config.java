/*
 * @(#)Config.java   11.jun 2007  Versión 2.2
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

import org.compiere.swing.CCheckBox;

import org.openXpertya.util.CLogger;

/**
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public abstract class Config {

    /** Descripción de Campo */
    static CLogger	log	= CLogger.getCLogger(Config.class);

    /** Descripción de Campo */
    protected ConfigurationData	p_data	= null;

    /**
     * Constructor ...
     *
     *
     * @param data
     */
    public Config(ConfigurationData data) {

        super();
        p_data	= data;

    }		// Config

    /**
     * Descripción de Método
     *
     *
     * @param selected
     *
     * @return
     */
    public String[] discoverDatabases(String selected) {
        return new String[] {};
    }		// discoverDatabases

    /**
     * Descripción de Método
     *
     */
    abstract void init();

    /**
     * Descripción de Método
     *
     *
     * @param cb
     * @param resString
     * @param pass
     * @param critical
     * @param errorMsg
     */
    void signalOK(CCheckBox cb, String resString, boolean pass, boolean critical, String errorMsg) {
        if (p_data.p_panel != null) {
        	p_data.p_panel.signalOK(cb, resString, pass, critical, errorMsg);
        }
    }		// signalOK

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    abstract String test();

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    protected ConfigurationPanel getPanel() {
    	return p_data.p_panel;
    }		// getPanel

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     */
    protected void setProperty(String key, String value) {
        p_data.p_properties.setProperty(key, value);
    }		// setProperty
}	// Config



/*
 * @(#)Config.java   11.jun 2007
 * 
 *  Fin del fichero Config.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
