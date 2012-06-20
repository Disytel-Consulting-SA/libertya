/*
 * @(#)CompiereViewportUI.java   12.oct 2007  Versión 2.2
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

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;
import javax.swing.plaf.basic.BasicViewportUI;

/**
 *  OpenXpertya View Point
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereViewportUI.java,v 1.6 2005/03/11 20:34:36 jjanke Exp $
 */
public class CompiereViewportUI extends BasicViewportUI {

    /** Shared UI object */
    private static ViewportUI	viewportUI;

    /**
     *  Create UI
     *  @param c
     *  @return CompiereViewpointUI
     */
    public static ComponentUI createUI(JComponent c) {

        if (viewportUI == null) {
            viewportUI	= new CompiereViewportUI();
        }

        return viewportUI;

    }		// createUI

    /**
     *  Install UI
     *  @param c
     */
    public void installUI(JComponent c) {

        super.installUI(c);

        // will be ignored as set in constructor after updateUI - Sun bug: 4677611
        c.setOpaque(false);

    }		// installUI
}	// CompiereViewpointUI



/*
 * @(#)CompiereViewportUI.java   02.jul 2007
 * 
 *  Fin del fichero CompiereViewportUI.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
