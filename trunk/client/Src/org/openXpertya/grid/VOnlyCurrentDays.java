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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.compiere.swing.CButton;
import org.compiere.swing.CPanel;
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

public class VOnlyCurrentDays extends JDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     * @param buttonLocation
     */

    public VOnlyCurrentDays( Frame parent,Point buttonLocation ) {

        // How long back in History?

        super( parent,Msg.getMsg( Env.getCtx(),"VOnlyCurrentDays",true ),true );

        try {
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VOnlyCurrentDays",e );
        }

        this.pack();
        buttonLocation.x -= ( int )getPreferredSize().getWidth() / 2;
        this.setLocation( buttonLocation );
        this.setVisible( true );
    }    // VOnlyCurrentDays

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private CButton bShowAll = new CButton();

    /** Descripción de Campos */

    private CButton bShowMonth = new CButton();

    /** Descripción de Campos */

    private CButton bShowWeek = new CButton();

    /** Descripción de Campos */

    private CButton bShowDay = new CButton();

    /** Descripción de Campos */

    private CButton bShowYear = new CButton();

    /** Descripción de Campos */

    private int m_days = 0;

    /** Descripción de Campos */

    private static Insets s_margin = new Insets( 2,2,2,2 );

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VOnlyCurrentDays.class );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        bShowAll.setText( Msg.getMsg( Env.getCtx(),"All" ));
        bShowAll.addActionListener( this );
        bShowAll.setMargin( s_margin );
        bShowYear.setText( Msg.getMsg( Env.getCtx(),"Year" ));
        bShowYear.addActionListener( this );
        bShowYear.setMargin( s_margin );
        bShowMonth.setText( Msg.getMsg( Env.getCtx(),"Month" ));
        bShowMonth.addActionListener( this );
        bShowMonth.setMargin( s_margin );
        bShowWeek.setText( Msg.getMsg( Env.getCtx(),"Week" ));
        bShowWeek.addActionListener( this );
        bShowWeek.setMargin( s_margin );
        bShowDay.setText( Msg.getMsg( Env.getCtx(),"Day" ));
        bShowDay.addActionListener( this );
        bShowDay.setMargin( s_margin );
        bShowDay.setDefaultCapable( true );

        //

        mainPanel.add( bShowDay,null );
        mainPanel.add( bShowWeek,null );
        mainPanel.add( bShowMonth,null );
        mainPanel.add( bShowYear,null );
        mainPanel.add( bShowAll,null );

        //

        mainPanel.setToolTipText( Msg.getMsg( Env.getCtx(),"VOnlyCurrentDays",false ));
        this.getContentPane().add( mainPanel,BorderLayout.CENTER );
        this.getRootPane().setDefaultButton( bShowDay );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == bShowDay ) {
            m_days = 1;
        } else if( e.getSource() == bShowWeek ) {
            m_days = 7;
        } else if( e.getSource() == bShowMonth ) {
            m_days = 31;
        } else if( e.getSource() == bShowYear ) {
            m_days = 356;
        } else {
            m_days = 0;    // all
        }

        dispose();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getCurrentDays() {
        return m_days;
    }    // getCurrentDays
}    // VOnlyCurrentDays



/*
 *  @(#)VOnlyCurrentDays.java   02.07.07
 * 
 *  Fin del fichero VOnlyCurrentDays.java
 *  
 *  Versión 2.2
 *
 */
