/*
 * @(#)CScrollPane.java   12.oct 2007  Versión 2.2
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

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.plaf.CompierePanelUI;

//~--- Importaciones JDK ------------------------------------------------------

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 *      OpenXpertya Srcoll Pane.
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: CScrollPane.java,v 1.3 2005/03/11 20:34:38 jjanke Exp $
 */
public class CScrollPane extends JScrollPane {

    /**
     *      OpenXpertya ScollPane
     */
    public CScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }		// CScollPane

    /**
     *      OpenXpertya ScollPane
     *      @param view view
     */
    public CScrollPane(Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }		// CScollPane

    /**
     *      OpenXpertya ScollPane
     *      @param vsbPolicy vertical policy
     *      @param hsbPolicy horizontal policy
     */
    public CScrollPane(int vsbPolicy, int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }		// CScollPane

    /**
     *      OpenXpertya ScollPane
     *      @param view view
     *      @param vsbPolicy vertical policy
     *      @param hsbPolicy horizontal policy
     */
    public CScrollPane(Component view, int vsbPolicy, int hsbPolicy) {

        super(view, vsbPolicy, hsbPolicy);
        setBackgroundColor(null);

        // setOpaque(false);
        // getViewport().setOpaque(false);

    }		// CScollPane

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background
     *  @param bg CompiereColor for Background, if null set standard background
     */
    public void setBackgroundColor(CompiereColor bg) {

        if (bg == null) {
            bg	= CompierePanelUI.getDefaultBackground();
        }

        putClientProperty(CompierePLAF.BACKGROUND, bg);

        // super.setBackground(bg.getFlatColor());
        // getViewport().putClientProperty(CompierePLAF.BACKGROUND, bg);
        // getViewport().setBackground(bg.getFlatColor());

    }		// setBackground
}	// CScollPane



/*
 * @(#)CScrollPane.java   02.jul 2007
 * 
 *  Fin del fichero CScrollPane.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
