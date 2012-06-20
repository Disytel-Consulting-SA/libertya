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



package org.openXpertya.pos;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.KeyStroke;

import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AppsAction;
import org.openXpertya.model.MPOS;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class PosSubPanel extends CPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public PosSubPanel( PosPanel posPanel ) {
        super();
        p_posPanel = posPanel;
        p_pos      = posPanel.p_pos;
        init();
    }    // PosSubPanel

    /** Descripción de Campos */

    protected PosPanel p_posPanel = null;

    /** Descripción de Campos */

    protected MPOS p_pos = null;

    /** Descripción de Campos */

    protected GridBagConstraints p_position = null;

    /** Descripción de Campos */

    protected Properties p_ctx = Env.getCtx();

    /** Descripción de Campos */

    private static final int WIDTH = 45;

    /** Descripción de Campos */

    private static final int HEIGHT = 35;

    /** Descripción de Campos */

    public static Insets INSETS1 = new Insets( 1,1,1,1 );

    /** Descripción de Campos */

    public static Insets INSETS2 = new Insets( 2,2,2,2 );

    /**
     * Descripción de Método
     *
     */

    protected abstract void init();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected GridBagConstraints getGridBagConstraints() {
        if( p_position == null ) {
            p_position         = new GridBagConstraints();
            p_position.anchor  = GridBagConstraints.NORTHWEST;
            p_position.fill    = GridBagConstraints.BOTH;
            p_position.weightx = 0.1;
            p_position.weighty = 0.1;
        }

        return p_position;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        p_pos = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param action
     * @param accelerator
     *
     * @return
     */

    protected CButton createButtonAction( String action,KeyStroke accelerator ) {
        AppsAction act = new AppsAction( action,accelerator,false );

        act.setDelegate( this );

        CButton button = ( CButton )act.getButton();

        button.setPreferredSize( new Dimension( WIDTH,HEIGHT ));
        button.setMinimumSize( getPreferredSize());
        button.setMaximumSize( getPreferredSize());
        button.setFocusable( false );

        return button;
    }    // getButtonAction

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    protected CButton createButton( String text ) {

        // if (text.indexOf("<html>") == -1)
        // text = "<html><h4>" + text + "</h4></html>";

        CButton button = new CButton( text );

        button.addActionListener( this );
        button.setPreferredSize( new Dimension( WIDTH,HEIGHT ));
        button.setMinimumSize( getPreferredSize());
        button.setMaximumSize( getPreferredSize());
        button.setFocusable( false );

        return button;
    }    // getButton

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {}    // actinPerformed
}    // PosSubPanel



/*
 *  @(#)PosSubPanel.java   02.07.07
 * 
 *  Fin del fichero PosSubPanel.java
 *  
 *  Versión 2.2
 *
 */
