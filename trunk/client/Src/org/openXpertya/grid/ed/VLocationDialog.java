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



package org.openXpertya.grid.ed;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.model.MCountry;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MRegion;
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

public class VLocationDialog extends CDialog implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param location
     */

    public VLocationDialog( Frame frame,String title,MLocation location ) {
        super( frame,title,true );

        try {
            jbInit();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,ex.getMessage());
        }

        m_location = location;

        if( m_location == null ) {
            m_location = new MLocation( Env.getCtx(),0,null );
        }

        // Overwrite title

        if( m_location.getC_Location_ID() == 0 ) {
            setTitle( Msg.getMsg( Env.getCtx(),"LocationNew" ));
        } else {
            setTitle( Msg.getMsg( Env.getCtx(),"LocationUpdate" ));
        }

        // Current Country

        MCountry.setDisplayLanguage( Env.getAD_Language( Env.getCtx()));
        fCountry = new CComboBox( MCountry.getCountries( Env.getCtx()));
        fCountry.setSelectedItem( m_location.getCountry());
        m_origCountry_ID = m_location.getC_Country_ID();

        // Current Region

        fRegion = new CComboBox( MRegion.getRegions( Env.getCtx(),m_origCountry_ID ));

        if( m_location.getCountry().isHasRegion()) {
            lRegion.setText( m_location.getCountry().getRegionName());    // name for region
        }

        fRegion.setSelectedItem( m_location.getRegion());

        //

        initLocation();
        fCountry.addActionListener( this );
        AEnv.positionCenterWindow( frame,this );
    }    // VLocationDialog

    /** Descripción de Campos */

    private boolean m_change = false;

    /** Descripción de Campos */

    private MLocation m_location;

    /** Descripción de Campos */

    private int m_origCountry_ID;

    /** Descripción de Campos */

    private int s_oldCountry_ID = 0;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VLocationDialog.class );

    /** Descripción de Campos */

    private CPanel panel = new CPanel();

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout panelLayout = new BorderLayout();

    /** Descripción de Campos */

    private GridBagLayout gridBagLayout = new GridBagLayout();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    //

    /** Descripción de Campos */

    private CLabel lAddress1 = new CLabel( Msg.getMsg( Env.getCtx(),"Address" ) + " 1" );

    /** Descripción de Campos */

    private CLabel lAddress2 = new CLabel( Msg.getMsg( Env.getCtx(),"Address" ) + " 2" );

    /** Descripción de Campos */

    private CLabel lAddress3 = new CLabel( Msg.getMsg( Env.getCtx(),"Address" ) + " 3" );

    /** Descripción de Campos */

    private CLabel lAddress4 = new CLabel( Msg.getMsg( Env.getCtx(),"Address" ) + " 4" );

    /** Descripción de Campos */

    private CLabel lCity = new CLabel( Msg.getMsg( Env.getCtx(),"City" ));

    /** Descripción de Campos */

    private CLabel lCountry = new CLabel( Msg.getMsg( Env.getCtx(),"Country" ));

    /** Descripción de Campos */

    private CLabel lRegion = new CLabel( Msg.getMsg( Env.getCtx(),"Region" ));

    /** Descripción de Campos */

    private CLabel lPostal = new CLabel( Msg.getMsg( Env.getCtx(),"Postal" ));

    /** Descripción de Campos */

    private CLabel lPostalAdd = new CLabel( Msg.getMsg( Env.getCtx(),"PostalAdd" ));
    private CLabel lPlaza = new CLabel("Plaza");
    /** Descripción de Campos */
    private CTextField fPlaza = new CTextField( 20 );
    private CTextField fAddress1 = new CTextField( 20 );    // length=60

    /** Descripción de Campos */

    private CTextField fAddress2 = new CTextField( 20 );    // length=60

    /** Descripción de Campos */

    private CTextField fAddress3 = new CTextField( 20 );    // length=60

    /** Descripción de Campos */

    private CTextField fAddress4 = new CTextField( 20 );    // length=60

    /** Descripción de Campos */

    private CTextField fCity = new CTextField( 15 );    // length=60

    /** Descripción de Campos */

    private CComboBox fCountry;

    /** Descripción de Campos */

    private CComboBox fRegion;

    /** Descripción de Campos */

    private VString fPostal = new VString("Postal", false, false, true, 10, 10, "", null);    // length=10

    /** Descripción de Campos */

    private CTextField fPostalAdd = new VString("PostalAdd", false, false, true, 10, 10, "", null);    // length=10

    //

    /** Descripción de Campos */

    private GridBagConstraints gbc = new GridBagConstraints();

    /** Descripción de Campos */

    private Insets labelInsets = new Insets( 2,15,2,0 );    // top,left,bottom,right

    /** Descripción de Campos */

    private Insets fieldInsets = new Insets( 2,5,2,10 );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        CompiereColor.setBackground( this );
        panel.setLayout( panelLayout );
        southPanel.setLayout( southLayout );
        mainPanel.setLayout( gridBagLayout );
        panelLayout.setHgap( 5 );
        panelLayout.setVgap( 10 );
        getContentPane().add( panel );
        panel.add( mainPanel,BorderLayout.CENTER );
        panel.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( confirmPanel,BorderLayout.NORTH );

        //

        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void initLocation() {
        MCountry country = m_location.getCountry();

        log.fine( country.getName() + ", Region=" + country.isHasRegion() + " " + country.getDisplaySequence() + ", C_Location_ID=" + m_location.getC_Location_ID());

        // new Region

        if( (m_location.getC_Country_ID() != s_oldCountry_ID) && country.isHasRegion()) {
            fRegion = new CComboBox( MRegion.getRegions( Env.getCtx(),country.getC_Country_ID()));

            if( m_location.getRegion() != null ) {
                fRegion.setSelectedItem( m_location.getRegion());
            }

            lRegion.setText( country.getRegionName());
            s_oldCountry_ID = m_location.getC_Country_ID();
        }

        gbc.anchor    = GridBagConstraints.NORTHWEST;
        gbc.gridy     = 0;    // line
        gbc.gridx     = 0;
        gbc.gridwidth = 1;
        gbc.insets    = fieldInsets;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.weightx   = 0;
        gbc.weighty   = 0;
        mainPanel.add( Box.createVerticalStrut( 5 ),gbc );    // top gap

        int line = 1;

        addLine( line++,lAddress1,fAddress1 );
        addLine( line++,lAddress2,fAddress2 );
        addLine( line++,lAddress3,fAddress3 );
        addLine( line++,lAddress4,fAddress4 );
        addLine( line++,lPlaza,fPlaza);

        // sequence of City Postal Region - @P@ @C@ - @C@, @R@ @P@

        String ds = country.getDisplaySequence();

        if( (ds == null) || (ds.length() == 0) ) {
            log.log( Level.SEVERE,"DisplaySequence empty - " + country );
            ds = "";    // @C@,  @P@
        }

        StringTokenizer st = new StringTokenizer( ds,"@",false );
        while( st.hasMoreTokens()) {
            String s = st.nextToken();
            if( s.startsWith( "C" )) {
                addLine( line++,lCity,fCity );
            } else if( s.startsWith( "P" )) {
                addLine( line++,lPostal,fPostal );
            } else if( s.startsWith( "A" )) {
                addLine( line++,lPostalAdd,fPostalAdd );
            } else if( s.startsWith( "R" ) && m_location.getCountry().isHasRegion()) {
                addLine( line++,lRegion,fRegion );
            }
        }

        // Country Last

        addLine( line++,lCountry,fCountry );

        // Fill it

        if( m_location.getC_Location_ID() != 0 ) {
            fAddress1.setText( m_location.getAddress1());
            fAddress2.setText( m_location.getAddress2());
            fAddress3.setText( m_location.getAddress3());
            fAddress4.setText( m_location.getAddress4());
            fPlaza.setText(m_location.getPlaza());
            fCity.setText( m_location.getCity());
            fPostal.setText( m_location.getPostal());
            fPostalAdd.setText( m_location.getPostal_Add());

            if( m_location.getCountry().isHasRegion()) {
                lRegion.setText( m_location.getCountry().getRegionName());
                fRegion.setSelectedItem( m_location.getRegion());
            }

            fCountry.setSelectedItem( country );
        }

        // Update UI

        pack();
    }    // initLocation

    /**
     * Descripción de Método
     *
     *
     * @param line
     * @param label
     * @param field
     */

    private void addLine( int line,JLabel label,JComponent field ) {
        gbc.gridy = line;

        // label

        gbc.insets  = labelInsets;
        gbc.gridx   = 0;
        gbc.weightx = 0.0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        label.setHorizontalAlignment( SwingConstants.RIGHT );
        mainPanel.add( label,gbc );

        // Field

        gbc.insets  = fieldInsets;
        gbc.gridx   = 1;
        gbc.weightx = 1.0;
        gbc.fill    = GridBagConstraints.NONE;
        mainPanel.add( field,gbc );
    }    // addLine

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            action_OK();
            m_change = true;
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            m_change = false;
            dispose();
        }

        // Country Changed - display in new Format

        else if( e.getSource() == fCountry ) {

            // Modifier for Mouse selection is 16  - for any key selection 0

            MCountry c = ( MCountry )fCountry.getSelectedItem();

            m_location.setCountry( c );

            // refrseh

            mainPanel.removeAll();
            initLocation();
            fCountry.requestFocus();    // allows to use Keybord selection
        }
    }                                   // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void action_OK() {
        m_location.setAddress1( fAddress1.getText());
        m_location.setAddress2( fAddress2.getText());
        m_location.setAddress3( fAddress3.getText());
        m_location.setAddress4( fAddress4.getText());
        m_location.setPlaza(fPlaza.getText());
        m_location.setCity( fCity.getText());
        m_location.setPostal( fPostal.getText());
        m_location.setPostal_Add( fPostalAdd.getText());

        // Country/Region

        MCountry c = ( MCountry )fCountry.getSelectedItem();

        m_location.setCountry( c );

        if( m_location.getCountry().isHasRegion()) {
            MRegion r = ( MRegion )fRegion.getSelectedItem();

            m_location.setRegion( r );
        } else {
            m_location.setC_Region_ID( 0 );
        }

        // Save chnages

        m_location.save();
    }    // actionOK

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isChanged() {
        return m_change;
    }    // getChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MLocation getValue() {
        return m_location;
    }    // getValue
}    // VLocationDialog



/*
 *  @(#)VLocationDialog.java   02.07.07
 * 
 *  Fin del fichero VLocationDialog.java
 *  
 *  Versión 2.2
 *
 */
