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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SubSalesRep extends PosSubPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubSalesRep( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubSalesRep

    /** Descripción de Campos */

    private CLabel f_label = null;

    /** Descripción de Campos */

    private CButton f_button = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubSalesRep.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.translate( Env.getCtx(),"C_POS_ID" ));

        setBorder( border );

        // Content

        setLayout( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = INSETS2;

        // --

        f_label     = new CLabel( p_pos.getName(),CLabel.LEADING );
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.weightx = 0.5;
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.fill    = GridBagConstraints.BOTH;
        add( f_label,gbc );

        //

        f_button = new CButton( Msg.getMsg( Env.getCtx(),"Logout" ));
        f_button.setActionCommand( "LogOut" );
        f_button.setFocusable( false );
        f_button.addActionListener( this );
        gbc.gridx   = 1;
        gbc.gridy   = 0;
        gbc.weightx = 0;
        gbc.anchor  = GridBagConstraints.EAST;
        gbc.fill    = GridBagConstraints.NONE;
        add( f_button,gbc );
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
        gbc.gridy = 0;

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

        log.info( "PosSubSalesRep - actionPerformed: " + action );

        // Logout

        p_posPanel.dispose();
    }    // actinPerformed
}    // PosSubSalesRep



/*
 *  @(#)SubSalesRep.java   02.07.07
 * 
 *  Fin del fichero SubSalesRep.java
 *  
 *  Versión 2.2
 *
 */
