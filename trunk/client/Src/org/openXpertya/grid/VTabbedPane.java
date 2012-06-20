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



package org.openXpertya.grid;

import java.awt.Component;

import javax.swing.JTabbedPane;

import org.compiere.swing.CTabbedPane;
import org.openXpertya.apps.APanel;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VTabbedPane extends CTabbedPane {

    /**
     * Constructor de la clase ...
     *
     *
     * @param isWorkbench
     */

    public VTabbedPane( boolean isWorkbench ) {
        super();

        // bug in 1.4.1 - can't be SCROLL_TAB_LAYOUT - java.lang.NullPointerException
        // at javax.swing.plaf.basic.BasicTabbedPaneUI$TabSelectionHandler.stateChanged(BasicTabbedPaneUI.java:3027)
        // setTabLayoutPolicy (JTabbedPane.SCROLL_TAB_LAYOUT);

        setWorkbench( isWorkbench );
        setFocusable( false );
    }    // workbenchTab

    /** Descripción de Campos */

    private boolean m_workbenchTab;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return( m_workbenchTab
                ?"WorkbenchTab"
                :"WindowTab" ) + " - selected " + getSelectedIndex() + " of " + getTabCount();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param isWorkbench
     */

    public void setWorkbench( boolean isWorkbench ) {
        m_workbenchTab = isWorkbench;

        if( m_workbenchTab ) {
            super.setTabPlacement( JTabbedPane.BOTTOM );
        } else {
            super.setTabPlacement( Language.getLoginLanguage().isLeftToRight()
                                   ?JTabbedPane.LEFT
                                   :JTabbedPane.RIGHT );
        }
    }    // setWorkbench

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWorkbench() {
        return m_workbenchTab;
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param notUsed
     */

    public void setTabPlacement( int notUsed ) {
        new java.lang.IllegalAccessError( "Do not use VTabbedPane.setTabPlacement directly" );
    }    // setTabPlacement

    /**
     * Descripción de Método
     *
     *
     * @param aPanel
     */

    public void dispose( APanel aPanel ) {
        Component[] comp = getComponents();

        for( int i = 0;i < comp.length;i++ ) {
            if( comp[ i ] instanceof VTabbedPane ) {
                VTabbedPane tp = ( VTabbedPane )comp[ i ];

                tp.removeChangeListener( aPanel );
                tp.dispose( aPanel );
            } else if( comp[ i ] instanceof GridController ) {
                GridController gc = ( GridController )comp[ i ];

                gc.addDataStatusListener( aPanel );
                gc.dispose();
            }
        }

        removeAll();
    }    // dispose
}    // VTabbdPane



/*
 *  @(#)VTabbedPane.java   02.07.07
 * 
 *  Fin del fichero VTabbedPane.java
 *  
 *  Versión 2.2
 *
 */
