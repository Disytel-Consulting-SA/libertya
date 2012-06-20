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



package org.openXpertya.grid.ed;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VStringBeanInfo extends SimpleBeanInfo {

    /** Descripción de Campos */

    private Class beanClass = VString.class;

    /** Descripción de Campos */

    private String iconColor16x16Filename;

    /** Descripción de Campos */

    private String iconColor32x32Filename;

    /** Descripción de Campos */

    private String iconMono16x16Filename;

    /** Descripción de Campos */

    private String iconMono32x32Filename;

    /**
     * Constructor de la clase ...
     *
     */

    public VStringBeanInfo() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _display = new PropertyDescriptor( "display",beanClass,"getDisplay",null );
            PropertyDescriptor _editable = new PropertyDescriptor( "editable",beanClass,null,"setEditable" );
            PropertyDescriptor _mandatory = new PropertyDescriptor( "mandatory",beanClass,"isMandatory","setMandatory" );
            PropertyDescriptor _value = new PropertyDescriptor( "value",beanClass,"getValue","setValue" );
            PropertyDescriptor[] pds = new PropertyDescriptor[]{ _display,_editable,_mandatory,_value };

            return pds;
        } catch( IntrospectionException ex ) {
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

    public java.awt.Image getIcon( int iconKind ) {
        switch( iconKind ) {
        case BeanInfo.ICON_COLOR_16x16:
            return (iconColor16x16Filename != null)
                   ?loadImage( iconColor16x16Filename )
                   :null;
        case BeanInfo.ICON_COLOR_32x32:
            return (iconColor32x32Filename != null)
                   ?loadImage( iconColor32x32Filename )
                   :null;
        case BeanInfo.ICON_MONO_16x16:
            return (iconMono16x16Filename != null)
                   ?loadImage( iconMono16x16Filename )
                   :null;
        case BeanInfo.ICON_MONO_32x32:
            return (iconMono32x32Filename != null)
                   ?loadImage( iconMono32x32Filename )
                   :null;
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BeanInfo[] getAdditionalBeanInfo() {
        Class superclass = beanClass.getSuperclass();

        try {
            BeanInfo superBeanInfo = Introspector.getBeanInfo( superclass );

            return new BeanInfo[]{ superBeanInfo };
        } catch( IntrospectionException ex ) {
            ex.printStackTrace();

            return null;
        }
    }
}



/*
 *  @(#)VStringBeanInfo.java   02.07.07
 * 
 *  Fin del fichero VStringBeanInfo.java
 *  
 *  Versión 2.2
 *
 */
