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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.swing.CButton;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MField;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.NamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VButton extends CButton implements VEditor {

    /**
     * Constructor de la clase ...
     *
     *
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param text
     * @param description
     * @param help
     * @param AD_Process_ID
     */

    public VButton( String columnName,boolean mandatory,boolean isReadOnly,boolean isUpdateable,String text,String description,String help,int AD_Process_ID, int columnReferenceID ) {
        super( text );
        super.setName( columnName );
        super.setActionCommand( columnName );
        m_text       = text;
        m_columnName = columnName;

        //

        setMandatory( mandatory );

        if( isReadOnly ||!isUpdateable ) {
            setReadWrite( false );
        } else {
            setReadWrite( true );
        }

        // Special Buttons

        if( columnName.equals( "PaymentRule" )) {
            // Mantiene el ID harcoded para columas que no tienen Reference ID.
        	if (columnReferenceID == 0) {
            	columnReferenceID = 195;
            }
            this.setForeground( Color.blue );
            setIcon( Env.getImageIcon( "Payment16.gif" ));        // 29*14
        } else if( columnName.equals( "DocAction" )) {
            // Mantiene el ID harcoded para columas que no tienen Reference ID.
        	if (columnReferenceID == 0) {
            	columnReferenceID = 135;
            }
            this.setForeground( Color.blue );
            setIcon( Env.getImageIcon( "Process16.gif" ));        // 16*16
        } else if( columnName.equals( "CreateFrom" )) {
            setIcon( Env.getImageIcon( "Copy16.gif" ));           // 16*16
        } else if( columnName.equals( "Record_ID" )) {
            setIcon( Env.getImageIcon( "Zoom16.gif" ));           // 16*16
            this.setText( Msg.getMsg( Env.getCtx(),"ZoomDocument" ));
        } else if( columnName.equals( "Posted" )) {
            // Mantiene el ID harcoded para columas que no tienen Reference ID.
        	if (columnReferenceID == 0) {
            	columnReferenceID = 234;
            }
            this.setForeground( Color.magenta );
            setIcon( Env.getImageIcon( "InfoAccount16.gif" ));    // 16*16
        } else if(columnName.equals( "StartDevelopment" )){
            // Mantiene el ID harcoded para columas que no tienen Reference ID.
        	if (columnReferenceID == 0) {
            	columnReferenceID = MComponentVersion.STARTDEVELOPMENT_AD_Reference_ID;
            }
        }
        
        if (columnReferenceID > 0) {
        	readReference(columnReferenceID);
        }

        // Deescription & Help

        m_description = description;

        if( (description == null) || (description.length() == 0) ) {
            m_description = "";
        } else {
            setToolTipText( m_description );
        }

        //

        m_help = help;

        if( help == null ) {
            m_help = "";
        }

        m_AD_Process_ID = AD_Process_ID;
        
        addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					fireActionPerformed(new ActionEvent(VButton.this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
				}
			}
        	
        });
    }    // VButton

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_actionListener = null;

        if( m_values != null ) {
            m_values.clear();
        }

        m_values = null;
    }    // dispose

    /** Descripción de Campos */

    private String m_columnName;

    /** Descripción de Campos */

    private String m_text;

    /** Descripción de Campos */

    private boolean m_mandatory;

    /** Descripción de Campos */

    private Object m_value;

    /** Descripción de Campos */

    private ActionListener m_actionListener;

    /** Descripción de Campos */

    private HashMap m_values = null;

    /** Descripción de Campos */

    private String m_description = "";

    /** Descripción de Campos */

    private String m_help;

    /** Descripción de Campos */

    private int m_AD_Process_ID;

    /** Descripción de Campos */

    private MLookup m_lookup;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VButton.class );

    private int m_referenceID = 0;
    
    /**
     * Descripción de Método
     *
     *
     * @param value
     */

    public void setValue( Object value ) {
        m_value = value;

        String text = m_text;

        // Nothing to show or Record_ID

        if( (value == null) || m_columnName.equals( "Record_ID" )) {
            ;
        } else if( m_values != null ) {
            text = ( String )m_values.get( value );
        } else if( m_lookup != null ) {
            NamePair pp = m_lookup.get( value );

            if( pp != null ) {
                text = pp.getName();
            }
        }

        // Display it

        setText( (text != null)
                 ?text
                 :"" );
    }    // setValue

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
        return m_value;
    }    // getValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDisplay() {
        return m_value.toString();
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param mandatory
     */

    public void setMandatory( boolean mandatory ) {
        m_mandatory = mandatory;
    }    // setMandatory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMandatory() {
        return m_mandatory;
    }    // isMandatory

    /**
     * Descripción de Método
     *
     *
     * @param error
     */

    public void setBackground( boolean error ) {}    // setBackground

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getColumnName() {
        return m_columnName;
    }    // getColumnName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getHelp() {
        return m_help;
    }    // getHelp

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getProcess_ID() {
        return m_AD_Process_ID;
    }    // getProcess_ID

    /**
     * Descripción de Método
     *
     *
     * @param aListener
     */

    public void addActionListener( ActionListener aListener ) {
        m_actionListener = aListener;
        super.addActionListener( aListener );
    }    // addActionListener

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "VButton[" );

        sb.append( m_columnName );
        sb.append( "=" ).append( m_value ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @param AD_Reference_ID
     */

    private void readReference( int AD_Reference_ID ) {
        m_values = new HashMap();
        m_referenceID = AD_Reference_ID;
        
        String SQL;

        if( Env.isBaseLanguage( Env.getCtx(),"AD_Ref_List" )) {
            SQL = "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=?";
        } else {
            SQL = "SELECT l.Value, t.Name FROM AD_Ref_List l, AD_Ref_List_Trl t " + "WHERE l.AD_Ref_List_ID=t.AD_Ref_List_ID" + " AND t.AD_Language='" + Env.getAD_Language( Env.getCtx()) + "'" + " AND l.AD_Reference_ID=?";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,AD_Reference_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String value = rs.getString( 1 );
                String name  = rs.getString( 2 );

                m_values.put( value,name );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,SQL,e );
        }
    }    // readReference

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public HashMap getValues() {
        return m_values;
    }    // getValues

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField ) {
        if( mField.getColumnName().endsWith( "_ID" ) &&!mField.getColumnName().equals( "Record_ID" )) {
            m_lookup = MLookupFactory.get( Env.getCtx(),mField.getWindowNo(),0,mField.getAD_Column_ID(),DisplayType.Search );
        }
    }    // setField
    
    public int getReferenceID() {
    	return m_referenceID;
    }
}    // VButton



/*
 *  @(#)VButton.java   02.07.07
 * 
 *  Fin del fichero VButton.java
 *  
 *  Versión 2.2
 *
 */
