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



package org.openXpertya.apps;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.LayoutFocusTraversalPolicy;

import org.compiere.swing.CEditor;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static AFocusTraversalPolicy get() {
        if( s_policy == null ) {
            s_policy = new AFocusTraversalPolicy();
        }

        return s_policy;
    }    // get

    /** Descripción de Campos */

    private static AFocusTraversalPolicy s_policy = new AFocusTraversalPolicy();

    /**
     * Constructor de la clase ...
     *
     */

    public AFocusTraversalPolicy() {
        super();
    }    // AFocusTraversalPolicy

    /**
     * Descripción de Método
     *
     *
     * @param focusCycleRoot
     *
     * @return
     */

    public Component getFirstComponent( Container focusCycleRoot ) {
        Component c = super.getFirstComponent( focusCycleRoot );

        // info ("Root: ", focusCycleRoot);
        // info ("  First: ", c);

        return c;
    }    // getFirstComponent

    /**
     * Descripción de Método
     *
     *
     * @param focusCycleRoot
     * @param aComponent
     *
     * @return
     */

    public Component getComponentAfter( Container focusCycleRoot,Component aComponent ) {
        Component c = super.getComponentAfter( focusCycleRoot,aComponent );

        return c;
    }

    /**
     * Descripción de Método
     *
     *
     * @param focusCycleRoot
     * @param aComponent
     *
     * @return
     */

    public Component getComponentBefore( Container focusCycleRoot,Component aComponent ) {
        Component c = super.getComponentBefore( focusCycleRoot,aComponent );

        return c;
    }

    /**
     * Descripción de Método
     *
     *
     * @param focusCycleRoot
     *
     * @return
     */

    public Component getLastComponent( Container focusCycleRoot ) {
        Component c = super.getLastComponent( focusCycleRoot );

        return c;
    }

    /**
     * Descripción de Método
     *
     *
     * @param aComponent
     *
     * @return
     */

    protected boolean accept( Component aComponent ) {
        if( !super.accept( aComponent )) {
            return false;
        }

        // TabbedPane

        if( aComponent instanceof JTabbedPane ) {
            return false;
        }

        // R/O Editors

        if( aComponent instanceof CEditor ) {
            CEditor ed = ( CEditor )aComponent;

            if( !ed.isReadWrite()) {
                return false;
            }
        }

        // Toolbar Buttons

        if( aComponent.getParent() instanceof JToolBar ) {
            return false;
        }

        //

        return true;
    }    // accept

    /**
     * Descripción de Método
     *
     *
     * @param title
     * @param c
     */

    private void info( String title,Component c ) {
        System.out.print( title );
        System.out.print( c.getClass().getName());
        System.out.println( " - " + c.getName());
    }    // info
}    // AFocusTraversalPolicy



/*
 *  @(#)AFocusTraversalPolicy.java   02.07.07
 * 
 *  Fin del fichero AFocusTraversalPolicy.java
 *  
 *  Versión 2.2
 *
 */
