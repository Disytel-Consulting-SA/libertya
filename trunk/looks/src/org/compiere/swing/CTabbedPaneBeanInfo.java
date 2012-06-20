/*
 * @(#)CTabbedPaneBeanInfo.java   12.oct 2007  Versión 2.2
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



package org.compiere.swing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Generated
 * @version $Id: CTabbedPaneBeanInfo.java,v 1.2 2003/09/27 11:08:52 jjanke Exp $
 */
public class CTabbedPaneBeanInfo extends SimpleBeanInfo {

    /** Descripción de Campo */
    private Class	beanClass	= CTabbedPane.class;

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
    public CTabbedPaneBeanInfo() {}

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public BeanInfo[] getAdditionalBeanInfo() {

        Class	superclass	= beanClass.getSuperclass();

        try {

            BeanInfo	superBeanInfo	= Introspector.getBeanInfo(superclass);

            return new BeanInfo[] { superBeanInfo };

        } catch (IntrospectionException ex) {

            ex.printStackTrace();

            return null;
        }
    }

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

            PropertyDescriptor	_background	= new PropertyDescriptor("background", beanClass, null, "setBackground");
            PropertyDescriptor	_backgroundColor	= new PropertyDescriptor("backgroundColor", beanClass, "getBackgroundColor", "setBackgroundColor");
            PropertyDescriptor[]	pds	= new PropertyDescriptor[] { _background, _backgroundColor };

            return pds;

        } catch (IntrospectionException ex) {

            ex.printStackTrace();

            return null;
        }
    }
}



/*
 * @(#)CTabbedPaneBeanInfo.java   02.jul 2007
 * 
 *  Fin del fichero CTabbedPaneBeanInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
