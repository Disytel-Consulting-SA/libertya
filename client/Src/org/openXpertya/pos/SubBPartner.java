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

import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CTextField;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerInfo;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MPriceListVersion;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SubBPartner extends PosSubPanel implements ActionListener,FocusListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubBPartner( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubCustomer

    /** Descripción de Campos */

    private CTextField f_name;

    /** Descripción de Campos */

    private CButton f_bNew;

    /** Descripción de Campos */

    private CButton f_bEdit;

    /** Descripción de Campos */

    private CButton f_bSearch;

    /** Descripción de Campos */

    private CComboBox f_location;

    /** Descripción de Campos */

    private CComboBox f_user;

    /** Descripción de Campos */

    private MBPartner m_bpartner;

    /** Descripción de Campos */

    private int m_M_PriceList_Version_ID = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubBPartner.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.translate( p_ctx,"C_BPartner_ID" ));

        setBorder( border );

        // Content

        setLayout( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = INSETS2;

        // --

        f_bNew         = createButtonAction( "New",null );
        gbc.gridx      = 0;
        gbc.gridheight = 2;
        gbc.anchor     = GridBagConstraints.WEST;
        add( f_bNew,gbc );

        //

        f_bEdit   = createButtonAction( "Edit",null );
        gbc.gridx = 1;
        add( f_bEdit,gbc );

        //

        f_name = new CTextField( "" );
        f_name.setName( "Name" );
        f_name.addActionListener( this );
        f_name.addFocusListener( this );
        gbc.gridx      = 2;
        gbc.gridy      = 0;
        gbc.gridheight = 1;
        gbc.gridwidth  = 2;
        gbc.weightx    = 0.5;
        gbc.fill       = GridBagConstraints.HORIZONTAL;
        add( f_name,gbc );

        //

        f_location  = new CComboBox();
        gbc.gridx   = 2;
        gbc.gridy   = 1;
        gbc.weightx = 0;
        gbc.fill    = GridBagConstraints.NONE;
        add( f_location,gbc );

        //

        f_user     = new CComboBox();
        gbc.gridx  = 3;
        gbc.gridy  = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add( f_user,gbc );

        //

        f_bSearch = createButtonAction( "BPartner",KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.SHIFT_MASK + Event.CTRL_MASK ));
        gbc.gridx      = 4;
        gbc.gridy      = 0;
        gbc.gridheight = 2;
        gbc.fill       = GridBagConstraints.NONE;
        add( f_bSearch,gbc );
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( f_name != null ) {
            f_name.removeFocusListener( this );
        }

        f_name = null;
        removeAll();
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

        log.info( "PosSubCustomer - actionPerformed: " + action );

        // New

        if( action.equals( "New" )) {
            setC_BPartner_ID( 0 );

            // Edit

        } else if( action.equals( "Edit" )) {
            f_bEdit.setReadWrite( false );
        }

        // BPartner

        else if( action.equals( "BPartner" )) {
            p_posPanel.openQuery( p_posPanel.f_queryBPartner );
        }

        // Name

        else if( e.getSource() == f_name ) {
            findBPartner();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusGained( FocusEvent e ) {}    // focusGained

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void focusLost( FocusEvent e ) {
        if( e.isTemporary()) {
            return;
        }

        log.info( e.toString());
        findBPartner();
    }    // focusLost

    /**
     * Descripción de Método
     *
     */

    private void findBPartner() {
        String query = f_name.getText();

        if( (query == null) || (query.length() == 0) ) {
            return;
        }

        query = query.toUpperCase();

        // Test Number

        boolean allNumber = true;
        boolean noNumber  = true;
        char[]  qq        = query.toCharArray();

        for( int i = 0;i < qq.length;i++ ) {
            if( Character.isDigit( qq[ i ] )) {
                noNumber = false;

                break;
            }
        }

        try {
            Integer.parseInt( query );
        } catch( Exception e ) {
            allNumber = false;
        }

        String Value   = query;
        String Name    = ( allNumber
                           ?null
                           :query );
        String Contact = ( allNumber
                           ?null
                           :query );
        String EMail   = ( (query.indexOf( "@" ) != -1)
                           ?query
                           :null );
        String Phone   = ( noNumber
                           ?null
                           :query );
        String City    = null;

        //

        MBPartnerInfo[] results = MBPartnerInfo.find( p_ctx,Value,Name,Contact,EMail,Phone,City );

        // Set Result

        if( results.length == 0 ) {
            setC_BPartner_ID( 0 );
        } else if( results.length == 1 ) {
            setC_BPartner_ID( results[ 0 ].getC_BPartner_ID());
            f_name.setText( results[ 0 ].getName());
        } else    // more than one
        {
            p_posPanel.f_queryBPartner.setResults( results );
            p_posPanel.openQuery( p_posPanel.f_queryBPartner );
        }
    }             // findBPartner

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    public void setC_BPartner_ID( int C_BPartner_ID ) {
        log.fine( "PosSubCustomer.setC_BPartner_ID=" + C_BPartner_ID );

        if( C_BPartner_ID == 0 ) {
            m_bpartner = null;
        } else {
            m_bpartner = new MBPartner( p_ctx,C_BPartner_ID,null );

            if( m_bpartner.getID() == 0 ) {
                m_bpartner = null;
            }
        }

        // Set Info

        if( m_bpartner != null ) {
            f_name.setText( m_bpartner.getName());
            f_bEdit.setReadWrite( false );
        } else {
            f_name.setText( null );
            f_bEdit.setReadWrite( false );
        }

        // Sets Currency

        m_M_PriceList_Version_ID = 0;
        getM_PriceList_Version_ID();
        fillCombos();
    }    // setC_BPartner_ID

    /**
     * Descripción de Método
     *
     */

    private void fillCombos() {
        Vector locationVector = new Vector();

        if( m_bpartner != null ) {
            MBPartnerLocation[] locations = m_bpartner.getLocations( false );

            for( int i = 0;i < locations.length;i++ ) {
                locationVector.add( locations[ i ].getKeyNamePair());
            }
        }

        DefaultComboBoxModel locationModel = new DefaultComboBoxModel( locationVector );

        f_location.setModel( locationModel );

        //

        Vector userVector = new Vector();

        if( m_bpartner != null ) {
            MUser[] users = m_bpartner.getContacts( false );

            for( int i = 0;i < users.length;i++ ) {
                userVector.add( users[ i ].getKeyNamePair());
            }
        }

        DefaultComboBoxModel userModel = new DefaultComboBoxModel( userVector );

        f_user.setModel( userModel );
    }    // fillCombos

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        if( m_bpartner != null ) {
            return m_bpartner.getC_BPartner_ID();
        }

        return 0;
    }    // getC_BPartner_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MBPartner getBPartner() {
        return m_bpartner;
    }    // getBPartner

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_Location_ID() {
        if( m_bpartner != null ) {
            KeyNamePair pp = ( KeyNamePair )f_location.getSelectedItem();

            if( pp != null ) {
                return pp.getKey();
            }
        }

        return 0;
    }    // getC_BPartner_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_User_ID() {
        if( m_bpartner != null ) {
            KeyNamePair pp = ( KeyNamePair )f_user.getSelectedItem();

            if( pp != null ) {
                return pp.getKey();
            }
        }

        return 0;
    }    // getC_BPartner_Location_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_Version_ID() {
        if( m_M_PriceList_Version_ID == 0 ) {
            int M_PriceList_ID = p_pos.getM_PriceList_ID();

            if( (m_bpartner != null) && (m_bpartner.getM_PriceList_ID() != 0) ) {
                M_PriceList_ID = m_bpartner.getM_PriceList_ID();
            }

            //

            MPriceList pl = MPriceList.get( p_ctx,M_PriceList_ID,null );

            p_posPanel.f_curLine.setCurrency( MCurrency.getISO_Code( p_ctx,pl.getC_Currency_ID()));
            f_name.setToolTipText( pl.getName());

            //

            MPriceListVersion plv = pl.getPriceListVersion( p_posPanel.getToday());

            if( (plv != null) && (plv.getM_PriceList_Version_ID() != 0) ) {
                m_M_PriceList_Version_ID = plv.getM_PriceList_Version_ID();
            }
        }

        return m_M_PriceList_Version_ID;
    }    // getM_PriceList_Version_ID
}    // PosSubCustomer



/*
 *  @(#)SubBPartner.java   02.07.07
 * 
 *  Fin del fichero SubBPartner.java
 *  
 *  Versión 2.2
 *
 */
