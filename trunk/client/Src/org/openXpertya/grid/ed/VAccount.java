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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CTextField;
import org.openXpertya.model.MAccountLookup;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VAccount extends JComponent implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param mAccount
     * @param title
     */

    public VAccount( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,MAccountLookup mAccount,String title ) {
        super();
        super.setName( columnName );
        m_columnName = columnName;
        m_mAccount   = mAccount;
        m_title      = title;

        //

        LookAndFeel.installBorder( this,"TextField.border" );
        this.setLayout( new BorderLayout());

        // Size

        this.setPreferredSize( m_text.getPreferredSize());    // causes r/o to be the same length

        int height = m_text.getPreferredSize().height;

        // ***     Button & Text   ***

        m_text.setBorder( null );
        m_text.addActionListener( this );
        m_text.setFont( CompierePLAF.getFont_Field());
        m_text.setForeground( CompierePLAF.getTextColor_Normal());
        this.add( m_text,BorderLayout.CENTER );
        m_button.setIcon( Env.getImageIcon( "Account10.gif" ));
        m_button.setMargin( new Insets( 0,0,0,0 ));
        m_button.setPreferredSize( new Dimension( height,height ));
        m_button.addActionListener( this );
        m_button.setFocusable( false );
        this.add( m_button,BorderLayout.EAST );

        // Editable

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        setMandatory( mandatory );
    }    // VAccount

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_text     = null;
        m_button   = null;
        m_mAccount = null;
    }    // dispose

    /** Descripción de Campos */

    private CTextField m_text = new CTextField( VLookup.DISPLAY_LENGTH );

    /** Descripción de Campos */

    private CButton m_button = new CButton();

    /** Descripción de Campos */

    private MAccountLookup m_mAccount;

    /** Descripción de Campos */

    private Object m_value;

    /** Descripción de Campos */

    private String m_title;

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VAccount.class );

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setReadWrite( boolean value ) {
        m_button.setReadWrite( value );
        m_text.setReadWrite( value );

        if( m_button.isVisible() != value ) {
            m_button.setVisible( value );
        }

        setBackground( false );
    }    // setReadWrite

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReadWrite() {
        return m_button.isReadWrite();
    }    // isReadWrite

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_button.setMandatory( mandatory );
        setBackground( false );
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_button.isMandatory();
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setBackground( Color color ) {

        // if (!color.equals(m_text.getBackground()))

        m_text.setBackground( color );
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {
        if( error ) {
            setBackground( CompierePLAF.getFieldBackground_Error());
        } else if( !isReadWrite()) {
            setBackground( CompierePLAF.getFieldBackground_Inactive());
        } else if( isMandatory()) {
            setBackground( CompierePLAF.getFieldBackground_Mandatory());
        } else {
            setBackground( CompierePLAF.getFieldBackground_Normal());
        }
    }    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @param fg
     */

    public void setForeground( Color fg ) {
        m_text.setForeground( fg );
    }    // setForeground

    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        m_value = value;
        m_text.setText( m_mAccount.getDisplay( value ));    // loads value
        m_text.setToolTipText( m_mAccount.getDescription());
    }                                                       // setValue

    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {
        if( evt.getPropertyName().equals( org.openXpertya.model.MField.PROPERTY )) {
            setValue( evt.getNewValue());
        }
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Object getValue() {
        return new Integer( m_mAccount.C_ValidCombination_ID );
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return m_text.getText();
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == m_text ) {
            cmd_text();
        } else {
            cmd_button();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void cmd_button() {
        log.info( "VAccount.cmd_button" );
        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        VAccountDialog ad = new VAccountDialog( Env.getFrame( this ),m_title,m_mAccount );

        setCursor( Cursor.getDefaultCursor());

        //

        Integer newValue = ad.getValue();

        if( newValue == null ) {
            return;
        }

        // set & redisplay

        setValue( newValue );

        // Data Binding

        try {
            fireVetoableChange( m_columnName,null,newValue );
        } catch( PropertyVetoException pve ) {
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void cmd_text() {
        String text = m_text.getText();

        log.info( "VAccount.cmd_text - " + text );

        if( (text == null) || (text.length() == 0) || text.equals( "%" )) {
            cmd_button();

            return;
        }

        if( !text.endsWith( "%" )) {
            text += "%";
        }

        //

        String sql = "SELECT C_ValidCombination_ID FROM C_ValidCombination " + "WHERE UPPER(Alias) LIKE ?";

        sql = MRole.getDefault().addAccessSQL( sql,"C_ValidCombination",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        int               C_ValidCombination_ID = 0;
        PreparedStatement pstmt                 = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setString( 1,text.toUpperCase());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                C_ValidCombination_ID = rs.getInt( 1 );

                if( rs.next()) {    // only one
                    C_ValidCombination_ID = 0;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"cmd_text",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // We have a Value

        if( C_ValidCombination_ID > 0 ) {
            Integer value = new Integer( C_ValidCombination_ID );
        	setValue( value);

            try {
                fireVetoableChange( m_columnName,null, value);
            } catch( PropertyVetoException pve ) {
            }
            
            
        
        } else {
            cmd_button();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener ) {
        m_text.addActionListener( listener );
    }    // addActionListener

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( org.openXpertya.model.MField mField ) {}    // setField
}    // VAccount



/*
 *  @(#)VAccount.java   02.07.07
 * 
 *  Fin del fichero VAccount.java
 *  
 *  Versión 2.2
 *
 */
