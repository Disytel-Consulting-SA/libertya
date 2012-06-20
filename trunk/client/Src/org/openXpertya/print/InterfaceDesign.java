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



package org.openXpertya.print;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Descripción de Interface
 *
 *
 * @version    2.2, 12.10.07
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface InterfaceDesign {

    /** Descripción de Campos */

    public static final String POSICIONRELATIVA = "Posici�n Relativa";

    /** Descripción de Campos */

    public static final String ACTIVO = "Activo";

    /** Descripción de Campos */

    public static final String IMPRESO = "Impreso";

    /** Descripción de Campos */

    public static final String SUPRIMIRNULOS = "Suprimir Nulos";

    /** Descripción de Campos */

    public static final String FIJARPOSICIONNL = "Fijar Posicion NL";

    /** Descripción de Campos */

    public static final String PROXIMALINEA = "Proxima Linea";

    /** Descripción de Campos */

    public static final String PROXIMAPAGINA = "Proxima Pagina";

    /** Descripción de Campos */

    public static final String ANCHOFIJO = "Ancho Fijo";

    /** Descripción de Campos */

    public static final String UNALINEA = "Una Linea";

    /** Descripción de Campos */

    public static final String IMAGENADJUNTA = "Imagen Adjunta";

    /**
     * Descripción de Método
     *
     *
     * @param g2D
     * @param pageNo
     * @param pageStart
     * @param ctx
     * @param isView
     */

    public abstract void paint( Graphics2D g2D,int pageNo,Point2D pageStart,Properties ctx,boolean isView );    // End paint

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public abstract void setLocation( int MARGIN );

    /**
     * Descripción de Método
     *
     */

    public abstract void setDimension();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean isSelected();

    /**
     * Descripción de Método
     *
     *
     * @param isSelected
     */

    public abstract void setSelected( boolean isSelected );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean isDragged();

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public abstract boolean isClicked( int x,int y );

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     *
     * @return
     */

    public abstract boolean isCornerClicked( int x,int y );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract int getCornerClicked();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean isChangedSize();

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public abstract void dragged( int MARGIN );

    /**
     * Descripción de Método
     *
     *
     * @param MARGIN
     */

    public abstract void changedSize( int MARGIN );

    /**
     * Descripción de Método
     *
     *
     * @param x
     * @param y
     */

    public abstract void Changes( int x,int y );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract int getPrintFormatItemID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract boolean isRelativePosition();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract int getSeqNo();

    /**
     * Descripción de Método
     *
     *
     * @param seqNo
     */

    public abstract void setSeqNo( int seqNo );

    /**
     * Descripción de Método
     *
     *
     * @param vd
     *
     * @return
     */

    public abstract ArrayList getMenuItems( ViewDesign vd );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public abstract ArrayList getFields();

    /**
     * Descripción de Método
     *
     *
     * @param areatype
     *
     * @return
     */

    public abstract boolean setHeaderFooter( String areatype );
}



/*
 *  @(#)InterfaceDesign.java   02.07.07
 * 
 *  Fin del fichero InterfaceDesign.java
 *  
 *  Versión 2.2
 *
 */
