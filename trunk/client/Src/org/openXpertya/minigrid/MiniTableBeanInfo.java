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



package org.openXpertya.minigrid;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MiniTableBeanInfo extends SimpleBeanInfo {

    /** Descripción de Campos */

    private Class beanClass = MiniTable.class;

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

    public MiniTableBeanInfo() {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[]{};

        return pds;
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
}



/*
 *  @(#)MiniTableBeanInfo.java   02.07.07
 * 
 *  Fin del fichero MiniTableBeanInfo.java
 *  
 *  Versión 2.2
 *
 */
