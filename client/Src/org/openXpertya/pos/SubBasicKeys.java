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
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SubBasicKeys extends PosSubPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubBasicKeys( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubBasicKeys

    /** Descripción de Campos */

    private CButton f_b1 = null;

    /** Descripción de Campos */

    private CButton f_b2 = null;

    /** Descripción de Campos */

    private CButton f_b3 = null;

    /** Descripción de Campos */

    private CButton f_b4 = null;

    /** Descripción de Campos */

    private CButton f_b5 = null;

    /** Descripción de Campos */

    private CButton f_b6 = null;

    /** Descripción de Campos */

    private CButton f_b7 = null;

    /** Descripción de Campos */

    private CButton f_b8 = null;

    /** Descripción de Campos */

    private CButton f_b9 = null;

    /** Descripción de Campos */

    private CButton f_b0 = null;

    /** Descripción de Campos */

    private CButton f_bDot = null;

    /** Descripción de Campos */

    private CButton f_reset = null;

    /** Descripción de Campos */

    private CButton f_new = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubBasicKeys.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( "#" );

        setBorder( border );

        // Content

        setLayout( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = INSETS1;

        //

        f_b7      = createButton( "7" );
        gbc.gridx = 0;
        gbc.gridy = 0;
        add( f_b7,gbc );

        //

        f_b8      = createButton( "8" );
        gbc.gridx = 1;
        gbc.gridy = 0;
        add( f_b8,gbc );

        //

        f_b9      = createButton( "9" );
        gbc.gridx = 2;
        gbc.gridy = 0;
        add( f_b9,gbc );

        // --

        f_b4      = createButton( "4" );
        gbc.gridx = 0;
        gbc.gridy = 1;
        add( f_b4,gbc );

        //

        f_b5      = createButton( "5" );
        gbc.gridx = 1;
        gbc.gridy = 1;
        add( f_b5,gbc );

        //

        f_b6      = createButton( "6" );
        gbc.gridx = 2;
        gbc.gridy = 1;
        add( f_b6,gbc );

        // --

        f_b1      = createButton( "1" );
        gbc.gridx = 0;
        gbc.gridy = 2;
        add( f_b1,gbc );

        //

        f_b2      = createButton( "2" );
        gbc.gridx = 1;
        gbc.gridy = 2;
        add( f_b2,gbc );

        //

        f_b3      = createButton( "3" );
        gbc.gridx = 2;
        gbc.gridy = 2;
        add( f_b3,gbc );

        // --

        f_b0 = createButton( "0" );

        Dimension size = f_b0.getPreferredSize();

        size.width    = ( size.width * 2 ) + 2;
        gbc.gridx     = 0;
        gbc.gridy     = 3;
        gbc.gridwidth = 2;
        gbc.fill      = GridBagConstraints.VERTICAL;
        add( f_b0,gbc );

        //

        f_bDot        = createButton( "." );
        gbc.gridx     = 2;
        gbc.gridy     = 3;
        gbc.gridwidth = 1;
        gbc.fill      = GridBagConstraints.NONE;
        add( f_bDot,gbc );

        // --

        gbc.gridx  = 4;
        gbc.insets = new Insets( 1,15,1,1 );
        gbc.gridy  = 0;
        f_reset    = createButtonAction( "Reset",null );
        add( f_reset,gbc );

        //

        f_new     = createButtonAction( "New",null );
        gbc.gridy = 3;
        add( f_new,gbc );
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 3;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        String action = e.getActionCommand();

        if( (action == null) || (action.length() == 0) ) {
            return;
        }

        log.info( "PosSubBasicKeys - actionPerformed: " + action );

        // Reset

        if( action.equals( "Reset" )) {
            ;

            // New

        } else if( action.equals( "New" )) {
            p_posPanel.newOrder();
        }
    }    // actionPerformed
}    // PosSubBasicKeys



/*
 *  @(#)SubBasicKeys.java   02.07.07
 * 
 *  Fin del fichero SubBasicKeys.java
 *  
 *  Versión 2.2
 *
 */
