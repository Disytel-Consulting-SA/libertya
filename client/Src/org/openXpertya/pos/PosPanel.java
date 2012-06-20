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

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.model.MPOS;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PosPanel extends CPanel implements FormPanel {

    /**
     * Constructor de la clase ...
     *
     */

    public PosPanel() {
        super( new GridBagLayout());
        anteriorKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        m_focusMgr = new PosKeyboardFocusManager();
        KeyboardFocusManager.setCurrentKeyboardFocusManager( m_focusMgr );
    }    // PosPanel

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Properties m_ctx = Env.getCtx();

    /** Descripción de Campos */

    private int m_SalesRep_ID = 0;

    /** Descripción de Campos */

    protected MPOS p_pos = null;

    /** Descripción de Campos */

    private PosKeyboardFocusManager m_focusMgr = null;

    /** Descripción de Campos */

    protected StatusBar f_status = new StatusBar();

    /** Descripción de Campos */

    protected SubBPartner f_bpartner = null;

    /** Descripción de Campos */

    protected SubSalesRep f_salesRep = null;

    /** Descripción de Campos */

    protected SubCurrentLine f_curLine = null;

    /** Descripción de Campos */

    protected SubProduct f_product = null;

    /** Descripción de Campos */

    protected SubLines f_lines = null;

    /** Descripción de Campos */

    protected SubFunctionKeys f_functionKeys = null;

    /** Descripción de Campos */

    protected SubCheckout f_checkout = null;

//      protected SubBasicKeys          f_basicKeys = null;

    /** Descripción de Campos */

    protected QueryProduct f_queryProduct = null;

    /** Descripción de Campos */

    protected QueryBPartner f_queryBPartner = null;

    /** Descripción de Campos */

    protected QueryTicket f_queryTicket = null;

    /** Descripción de Campos */

    protected SubFuncionesCaja f_funcionesCaja;

    // Today's (login) date            */

    /** Descripción de Campos */

    private Timestamp m_today = Env.getContextAsDate( m_ctx,"#Date" );

    /** Descripción de Campos */

    private KeyboardFocusManager anteriorKeyboardFocusManager;

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        frame.setMaximize( true );
        m_SalesRep_ID = Env.getAD_User_ID( m_ctx );
        log.info( "init - SalesRep_ID=" + m_SalesRep_ID );
        m_WindowNo = WindowNo;
        m_frame    = frame;

        //

        try {
            if( !dynInit()) {
                dispose();
                frame.dispose();

                return;
            }

            frame.getContentPane().add( this,BorderLayout.CENTER );
            frame.getContentPane().add( f_status,BorderLayout.SOUTH );

            // this.setPreferredSize(new Dimension (800-20,600-20));

        } catch( Exception e ) {
            log.log( Level.SEVERE,"init",e );
        }

        log.config( "PosPanel.init - " + getPreferredSize());
        m_focusMgr.start();
    }    // init

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_focusMgr != null ) {
            m_focusMgr.stop();
        }

        m_focusMgr = null;
        KeyboardFocusManager.setCurrentKeyboardFocusManager( anteriorKeyboardFocusManager );

        //

        if( f_bpartner != null ) {
            f_bpartner.dispose();
        }

        f_bpartner = null;

        if( f_salesRep != null ) {
            f_salesRep.dispose();
        }

        f_salesRep = null;

        if( f_curLine != null ) {
            f_curLine.deleteOrder();
            f_curLine.dispose();
        }

        f_curLine = null;

        if( f_product != null ) {
            f_product.dispose();
        }

        f_product = null;

        if( f_lines != null ) {
            f_lines.dispose();
        }

        f_lines = null;

        if( f_functionKeys != null ) {
            f_functionKeys.dispose();
        }

        f_functionKeys = null;

        if( f_checkout != null ) {
            f_checkout.dispose();
        }

        f_checkout = null;

/*              if (f_basicKeys != null)
                        f_basicKeys.dispose();                  quitado por ConSerTi al no apreciar su utilidad
                f_basicKeys = null;
*/

        //

        if( f_queryProduct != null ) {
            f_queryProduct.dispose();
        }

        f_queryProduct = null;

        if( f_queryBPartner != null ) {
            f_queryBPartner.dispose();
        }

        f_queryBPartner = null;

        if( f_queryTicket != null ) {
            f_queryTicket.dispose();
        }

        f_queryTicket = null;

        //

        if( f_funcionesCaja != null ) {
            f_funcionesCaja.dispose();
        }

        f_funcionesCaja = null;

        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
        m_ctx   = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean dynInit() {
        if( !setMPOS()) {
            return false;
        }

        // Create Sub Panels

        f_bpartner = new SubBPartner( this );
        add( f_bpartner,f_bpartner.getGridBagConstraints());

        //

        f_salesRep = new SubSalesRep( this );
        add( f_salesRep,f_salesRep.getGridBagConstraints());

        //

        f_curLine = new SubCurrentLine( this );
        add( f_curLine,f_curLine.getGridBagConstraints());

        //

        f_product = new SubProduct( this );
        add( f_product,f_product.getGridBagConstraints());

        //

        f_lines = new SubLines( this );
        add( f_lines,f_lines.getGridBagConstraints());

        //

        f_functionKeys = new SubFunctionKeys( this );
        add( f_functionKeys,f_functionKeys.getGridBagConstraints());

        //

        f_checkout = new SubCheckout( this );
        add( f_checkout,f_checkout.getGridBagConstraints());

        //

/*              f_basicKeys = new SubBasicKeys (this);
                add (f_basicKeys, f_basicKeys.getGridBagConstraints());  quitado por ConSerTi al no apreciar su utilidad
*/

        // --      Query

        f_queryProduct = new QueryProduct( this );
        add( f_queryProduct,f_queryProduct.getGridBagConstraints());

        //

        f_queryBPartner = new QueryBPartner( this );
        add( f_queryBPartner,f_queryBPartner.getGridBagConstraints());

        //

        f_queryTicket = new QueryTicket( this );
        add( f_queryTicket,f_queryTicket.getGridBagConstraints());

        //

        f_funcionesCaja = new SubFuncionesCaja( this );
        add( f_funcionesCaja,f_funcionesCaja.getGridBagConstraints());
        newOrder();

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean setMPOS() {
        MPOS[] poss = null;

        if( m_SalesRep_ID == 100 ) {    // superUser
            poss = getPOSs( 0 );
        } else {
            poss = getPOSs( m_SalesRep_ID );
        }

        //

        if( poss.length == 0 ) {
            ADialog.error( m_WindowNo,m_frame,"NoPOSForUser" );

            return false;
        } else if( poss.length == 1 ) {
            p_pos = poss[ 0 ];

            return true;
        }

        // Select POS

        String msg       = Msg.getMsg( m_ctx,"SelectPOS" );
        String title     = Env.getHeader( m_ctx,m_WindowNo );
        Object selection = JOptionPane.showInputDialog( m_frame,msg,title,JOptionPane.QUESTION_MESSAGE,null,poss,poss[ 0 ] );

        if( selection != null ) {
            p_pos = ( MPOS )selection;;

            return true;
        }

        return false;
    }    // setMPOS

    /**
     * Descripción de Método
     *
     *
     * @param SalesRep_ID
     *
     * @return
     */

    private MPOS[] getPOSs( int SalesRep_ID ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_POS WHERE SalesRep_ID=?";

        if( SalesRep_ID == 0 ) {
            sql = "SELECT * FROM C_POS WHERE AD_Client_ID=?";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            if( SalesRep_ID != 0 ) {
                pstmt.setInt( 1,m_SalesRep_ID );
            } else {
                pstmt.setInt( 1,Env.getAD_Client_ID( m_ctx ));
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPOS( m_ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPOSs",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MPOS[] retValue = new MPOS[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getPOSs

    /**
     * Descripción de Método
     *
     *
     * @param aFlag
     */

    public void setVisible( boolean aFlag ) {
        super.setVisible( aFlag );
        f_product.f_name.requestFocus();
    }    // setVisible

    /**
     * Descripción de Método
     *
     *
     * @param panel
     */

    public void openQuery( CPanel panel ) {
        if( panel.equals( f_funcionesCaja )) {
            f_bpartner.setVisible( false );
            f_salesRep.setVisible( false );
            f_curLine.setVisible( false );
            f_product.setVisible( false );
        }

        f_checkout.setVisible( false );

//              f_basicKeys.setVisible(false);  quitado por ConSerTi por no apreciar su utilidad

        f_lines.setVisible( false );
        f_functionKeys.setVisible( false );
        panel.setVisible( true );
    }    // closeQuery

    /**
     * Descripción de Método
     *
     *
     * @param panel
     */

    public void closeQuery( CPanel panel ) {
        panel.setVisible( false );
        f_bpartner.setVisible( true );
        f_salesRep.setVisible( true );
        f_curLine.setVisible( true );
        f_product.setVisible( true );

//              f_basicKeys.setVisible(true);   quitado por ConSerTi por no apreciar su utilidad

        f_lines.setVisible( true );
        f_functionKeys.setVisible( true );
        f_checkout.setVisible( true );
    }    // closeQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getToday() {
        return m_today;
    }    // getToday

    /**
     * Descripción de Método
     *
     */

    public void newOrder() {
        log.info( "PosPabel.newOrder" );
        f_bpartner.setC_BPartner_ID( 0 );
        f_curLine.nuevoPedido();
        f_curLine.newLine();
        f_product.f_name.requestFocus();
        actualizarInfo();
    }    // newOrder

    /**
     * Descripción de Método
     *
     */

    public void actualizarInfo() {
        if( f_lines != null ) {
            f_lines.updateTable( f_curLine.getOrder());
        }

        if( f_checkout != null ) {
            f_checkout.mostrarVuelta();
        }
    }
}    // PosPanel



/*
 *  @(#)PosPanel.java   02.07.07
 * 
 *  Fin del fichero PosPanel.java
 *  
 *  Versión 2.2
 *
 */
