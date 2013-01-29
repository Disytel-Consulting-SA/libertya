/*
 * @(#)CConnectionBeanInfo.java   12.oct 2007  Versión 2.2
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 *  Generated
 */
public class CConnectionBeanInfo extends SimpleBeanInfo {

    /** Descripción de Campo */
    private Class	beanClass	= CConnection.class;

    /** Descripción de Campo */
    private String	iconColor16x16Filename;

    /** Descripción de Campo */
    private String	iconColor32x32Filename;

    /** Descripción de Campo */
    private String	iconMono16x16Filename;

    /** Descripción de Campo */
    private String	iconMono32x32Filename;

    /**
     * Constructor ...
     *
     */
    public CConnectionBeanInfo() {}

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param iconKind
     *
     * @return
     */
    public java.awt.Image getIcon(int iconKind) {

        switch (iconKind) {

        case BeanInfo.ICON_COLOR_16x16 :
            return (iconColor16x16Filename != null)
                   ? loadImage(iconColor16x16Filename)
                   : null;

        case BeanInfo.ICON_COLOR_32x32 :
            return (iconColor32x32Filename != null)
                   ? loadImage(iconColor32x32Filename)
                   : null;

        case BeanInfo.ICON_MONO_16x16 :
            return (iconMono16x16Filename != null)
                   ? loadImage(iconMono16x16Filename)
                   : null;

        case BeanInfo.ICON_MONO_32x32 :
            return (iconMono32x32Filename != null)
                   ? loadImage(iconMono32x32Filename)
                   : null;
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public PropertyDescriptor[] getPropertyDescriptors() {

        try {

            PropertyDescriptor	_apps_host	= new PropertyDescriptor("apps_host", beanClass, "getApps_host", "setApps_host");
            PropertyDescriptor	_apps_port	= new PropertyDescriptor("apps_port", beanClass, "getApps_port", "setApps_port");
            PropertyDescriptor	_appsServer	= new PropertyDescriptor("appsServer", beanClass, "getAppsServer", null);
            PropertyDescriptor	_appsServerException	= new PropertyDescriptor("appsServerException", beanClass, "getAppsServerException", null);
            PropertyDescriptor	_appsServerOK	= new PropertyDescriptor("appsServerOK", beanClass, "isAppsServerOK", null);
            PropertyDescriptor	_attributes	= new PropertyDescriptor("attributes", beanClass, null, "setAttributes");
            PropertyDescriptor	_bequeath	= new PropertyDescriptor("bequeath", beanClass, "isBequeath", "setBequeath");
            PropertyDescriptor	_connectionString	= new PropertyDescriptor("connectionString", beanClass, "getConnectionString", null);
            PropertyDescriptor	_database	= new PropertyDescriptor("database", beanClass, "getDatabase", null);
            PropertyDescriptor	_databaseException	= new PropertyDescriptor("databaseException", beanClass, "getDatabaseException", null);
            PropertyDescriptor	_databaseOK	= new PropertyDescriptor("databaseOK", beanClass, "isDatabaseOK", null);
            PropertyDescriptor	_db_host	= new PropertyDescriptor("db_host", beanClass, "getDb_host", "setDb_host");
            PropertyDescriptor	_db_name	= new PropertyDescriptor("db_name", beanClass, "getDb_name", "setDb_name");
            PropertyDescriptor	_db_port	= new PropertyDescriptor("db_port", beanClass, "getDb_port", "setDb_port");
            PropertyDescriptor	_db_pwd	= new PropertyDescriptor("db_pwd", beanClass, "getDb_pwd", "setDb_pwd");
            PropertyDescriptor	_db_uid	= new PropertyDescriptor("db_uid", beanClass, "getDb_uid", "setDb_uid");
            PropertyDescriptor	_fw_host	= new PropertyDescriptor("fw_host", beanClass, "getFw_host", "setFw_host");
            PropertyDescriptor	_fw_port	= new PropertyDescriptor("fw_port", beanClass, "getFw_port", "setFw_port");
            PropertyDescriptor	_name	= new PropertyDescriptor("name", beanClass, "getName", "setName");
            PropertyDescriptor	_rmiUri	= new PropertyDescriptor("rmiUri", beanClass, "getRmiUri", null);
            PropertyDescriptor	_type	= new PropertyDescriptor("type", beanClass, "getType", "setType");
            PropertyDescriptor	_viaFirewall	= new PropertyDescriptor("viaFirewall", beanClass, "isViaFirewall", "setViaFirewall");
            PropertyDescriptor[]	pds	= new PropertyDescriptor[] {
                _apps_host, _apps_port, _appsServer, _appsServerException, _appsServerOK, _attributes, _bequeath, _connectionString, _database, _databaseException, _databaseOK, _db_host, _db_name, _db_port, _db_pwd, _db_uid, _fw_host, _fw_port, _name, _rmiUri, _type, _viaFirewall
            };

            return pds;

        } catch (IntrospectionException ex) {

            ex.printStackTrace();

            return null;
        }
    }
}



/*
 * @(#)CConnectionBeanInfo.java   02.jul 2007
 * 
 *  Fin del fichero CConnectionBeanInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
