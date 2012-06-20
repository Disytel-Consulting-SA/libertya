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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
import org.openXpertya.model.MPOSKey;
import org.openXpertya.model.MPOSKeyLayout;
import org.openXpertya.print.MPrintColor;
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

public class SubFunctionKeys extends PosSubPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubFunctionKeys( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubFunctionKeys

    /** Descripción de Campos */

    private MPOSKey[] m_keys;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubFunctionKeys.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.translate( Env.getCtx(),"C_POSKeyLayout_ID" ));

        setBorder( border );

        int C_POSKeyLayout_ID = p_pos.getC_POSKeyLayout_ID();

        if( C_POSKeyLayout_ID == 0 ) {
            return;
        }

        MPOSKeyLayout fKeys = MPOSKeyLayout.get( Env.getCtx(),C_POSKeyLayout_ID );

        if( fKeys.getID() == 0 ) {
            return;
        }

        int COLUMNS = 3;    // Min Columns
        int ROWS    = 3;    // Min Rows

        m_keys = fKeys.getKeys( false );

        int noKeys = m_keys.length;
        int rows   = Math.max((( noKeys - 1 ) / COLUMNS ) + 1,ROWS );
        int cols   = (( noKeys - 1 ) % COLUMNS ) + 1;

        log.fine( "PosSubFunctionKeys.init - NoKeys=" + noKeys + " - Rows=" + rows + ", Cols=" + cols );

        // Content

        CPanel content = new CPanel( new GridLayout( Math.max( rows,3 ),Math.max( cols,3 )));

        for( int i = 0;i < m_keys.length;i++ ) {
            MPOSKey      key        = m_keys[ i ];
            StringBuffer buttonHTML = new StringBuffer( "<html><p>" );

            if( key.getAD_PrintColor_ID() != 0 ) {
                MPrintColor color = MPrintColor.get( Env.getCtx(),key.getAD_PrintColor_ID());

                buttonHTML.append( "<font color=#" ).append( color.getRRGGBB()).append( ">" ).append( key.getName()).append( "</font>" );
            } else {
                buttonHTML.append( key.getName());
            }

            buttonHTML.append( "</p></html>" );
            log.fine( "#" + i + " - " + buttonHTML );

            CButton button = new CButton( buttonHTML.toString());

            button.setMargin( INSETS1 );
            button.setFocusable( false );
            button.setActionCommand( String.valueOf( key.getC_POSKey_ID()));
            button.addActionListener( this );
            content.add( button );
        }

        for( int i = m_keys.length;i < rows * COLUMNS;i++ ) {
            CButton button = new CButton( "" );

            button.setFocusable( false );
            content.add( button );
        }

        content.setPreferredSize( new Dimension( cols * 70,rows * 50 ));
        add( content );
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
        gbc.gridy = 2;

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

        if( (action == null) || (action.length() == 0) || (m_keys == null) ) {
            return;
        }

        log.info( "PosSubFunctionKeys - actionPerformed: " + action );

        try {
            int C_POSKey_ID = Integer.parseInt( action );

            for( int i = 0;i < m_keys.length;i++ ) {
                MPOSKey key = m_keys[ i ];

                if( key.getC_POSKey_ID() == C_POSKey_ID ) {
                    p_posPanel.f_product.setM_Product_ID( key.getM_Product_ID());
                    p_posPanel.f_product.setPrice();
                    p_posPanel.f_curLine.setQty( key.getQty());
                    p_posPanel.f_curLine.saveLine();

                    return;
                }
            }
        } catch( Exception ex ) {
        }
    }    // actinPerformed
}    // PosSubFunctionKeys



/*
 *  @(#)SubFunctionKeys.java   02.07.07
 * 
 *  Fin del fichero SubFunctionKeys.java
 *  
 *  Versión 2.2
 *
 */
