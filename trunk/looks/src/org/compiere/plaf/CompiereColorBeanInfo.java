/*
 * @(#)CompiereColorBeanInfo.java   12.oct 2007  Versión 2.2
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



package org.compiere.plaf;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * Generated
 * @version $Id: CompiereColorBeanInfo.java,v 1.3 2003/09/27 11:08:52 jjanke Exp $
 */
public class CompiereColorBeanInfo extends SimpleBeanInfo {

    /** Descripción de Campo */
    private Class	beanClass	= CompiereColor.class;

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
    public CompiereColorBeanInfo() {}

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

            PropertyDescriptor	_flat	= new PropertyDescriptor("flat", beanClass, "isFlat", null);
            PropertyDescriptor	_flatColor	= new PropertyDescriptor("flatColor", beanClass, "getFlatColor", "setFlatColor");
            PropertyDescriptor	_gradient	= new PropertyDescriptor("gradient", beanClass, "isGradient", null);
            PropertyDescriptor	_gradientLowerColor	= new PropertyDescriptor("gradientLowerColor", beanClass, "getGradientLowerColor", "setGradientLowerColor");
            PropertyDescriptor	_gradientRepeatDistance	= new PropertyDescriptor("gradientRepeatDistance", beanClass, "getGradientRepeatDistance", "setGradientRepeatDistance");
            PropertyDescriptor	_gradientStartPoint	= new PropertyDescriptor("gradientStartPoint", beanClass, "getGradientStartPoint", "setGradientStartPoint");
            PropertyDescriptor	_gradientUpperColor	= new PropertyDescriptor("gradientUpperColor", beanClass, "getGradientUpperColor", "setGradientUpperColor");
            PropertyDescriptor	_line	= new PropertyDescriptor("line", beanClass, "isLine", null);
            PropertyDescriptor	_lineBackColor	= new PropertyDescriptor("lineBackColor", beanClass, "getLineBackColor", "setLineBackColor");
            PropertyDescriptor	_lineColor	= new PropertyDescriptor("lineColor", beanClass, "getLineColor", "setLineColor");
            PropertyDescriptor	_lineDistance	= new PropertyDescriptor("lineDistance", beanClass, "getLineDistance", "setLineDistance");
            PropertyDescriptor	_lineWidth	= new PropertyDescriptor("lineWidth", beanClass, "getLineWidth", "setLineWidth");
            PropertyDescriptor	_texture	= new PropertyDescriptor("texture", beanClass, "isTexture", null);
            PropertyDescriptor	_textureCompositeAlpha	= new PropertyDescriptor("textureCompositeAlpha", beanClass, "getTextureCompositeAlpha", "setTextureCompositeAlpha");
            PropertyDescriptor	_textureImage	= new PropertyDescriptor("textureImage", beanClass, "getTextureImage", null);
            PropertyDescriptor	_textureTaintColor	= new PropertyDescriptor("textureTaintColor", beanClass, "getTextureTaintColor", "setTextureTaintColor");
            PropertyDescriptor	_textureURL	= new PropertyDescriptor("textureURL", beanClass, "getTextureURL", "setTextureURL");
            PropertyDescriptor	_type	= new PropertyDescriptor("type", beanClass, "getType", null);
            PropertyDescriptor[]	pds	= new PropertyDescriptor[] {
                _flat, _flatColor, _gradient, _gradientLowerColor, _gradientRepeatDistance, _gradientStartPoint, _gradientUpperColor, _line, _lineBackColor, _lineColor, _lineDistance, _lineWidth, _texture, _textureCompositeAlpha, _textureImage, _textureTaintColor, _textureURL, _type
            };

            return pds;

        } catch (IntrospectionException ex) {

            ex.printStackTrace();

            return null;
        }
    }
}



/*
 * @(#)CompiereColorBeanInfo.java   02.jul 2007
 * 
 *  Fin del fichero CompiereColorBeanInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
