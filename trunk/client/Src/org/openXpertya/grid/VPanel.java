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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;

import org.compiere.swing.CPanel;
import org.openXpertya.grid.ed.VEditor;
import org.openXpertya.grid.ed.VLine;
import org.openXpertya.model.MField;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VPanel extends CPanel {

    /**
     * Constructor de la clase ...
     *
     */

    public VPanel() {
        super( new GridBagLayout());
        setName( "VPanel" );
        setBorder( null );

        // Set initial values of constraint

        m_gbc.anchor     = GridBagConstraints.NORTHWEST;
        m_gbc.gridy      = 0;    // line
        m_gbc.gridx      = 0;
        m_gbc.gridheight = 1;
        m_gbc.gridwidth  = 1;
        m_gbc.insets     = m_zeroInset;
        m_gbc.fill       = GridBagConstraints.HORIZONTAL;
        m_gbc.weightx    = 0;
        m_gbc.weighty    = 0;
        m_gbc.ipadx      = 0;
        m_gbc.ipady      = 0;
    }                            // VPanel

    /** Descripción de Campos */

    private GridBagConstraints m_gbc = new GridBagConstraints();

    /** Descripción de Campos */

    private final boolean m_leftToRight = Language.getLoginLanguage().isLeftToRight();

    /** Descripción de Campos */

    private final Insets m_labelInset = m_leftToRight
            ?new Insets( 2,12,3,0 )
            :new Insets( 2,5,3,0 );    // top,left,bottom,right

    /** Descripción de Campos */

    private final Insets m_fieldInset = m_leftToRight
            ?new Insets( 2,5,3,0 )
            :new Insets( 2,12,3,0 );    // top,left,bottom,right

    /** Descripción de Campos */

    private final Insets m_zeroInset = new Insets( 0,0,0,0 );

    //

    /** Descripción de Campos */

    private int m_line = 0;

    /** Descripción de Campos */

    private boolean m_hGapAdded = false;    // only once

    /** Descripción de Campos */

    private String m_oldFieldGroup = null;

    /**
     * Descripción de Método
     *
     *
     * @param label
     * @param editor
     * @param mField
     */

    public void addField( JLabel label,VEditor editor,MField mField ) {
        if( (label == null) && (editor == null) ) {
            return;
        }

        boolean sameLine = mField.isSameLine();

        if( addGroup( mField.getFieldGroup())) {    // sets top
            sameLine = false;
        }

        if( sameLine ) {    // Set line #
            m_gbc.gridy = m_line - 1;
        } else {
            m_gbc.gridy = m_line++;
        }

        // *** The Label ***

        if( label != null ) {
            m_gbc.gridwidth = 1;
            m_gbc.insets    = m_labelInset;
            m_gbc.fill      = GridBagConstraints.HORIZONTAL;    // required for right justified

            // Set column #

            if( m_leftToRight ) {
                m_gbc.gridx = sameLine
                              ?2
                              :0;
            } else {
                m_gbc.gridx = (sameLine | mField.isLongField())
                              ?3
                              :1;
            }

            // Weight factor for Label

            m_gbc.weightx = 0;

            // Add Label

            this.add( label,m_gbc );
        }

        // *** The Field ***

        if( editor != null ) {
            Component field = ( Component )editor;

            // Default Width

            m_gbc.gridwidth = mField.isLongField()
                              ?3
                              :1;
            m_gbc.insets    = m_fieldInset;

            // m_gbc.fill = GridBagConstraints.NONE;

            m_gbc.fill = GridBagConstraints.HORIZONTAL;

            // Set column #

            if( m_leftToRight ) {
                m_gbc.gridx = sameLine
                              ?3
                              :1;
            } else {
                m_gbc.gridx = sameLine
                              ?2
                              :0;
            }

            // Weight factor for Fields

            m_gbc.weightx = 1;

            // Add Field

            this.add( field,m_gbc );

            // Link Label to Field

            if( label != null ) {
                label.setLabelFor( field );
            }
        }

        // ADebug.trace(ADebug.l5_DData, m_cField[i].m_mField.ColumnName
        // + " \t line="+ gbc.gridy + " column="+gbc.gridx
        // + " width="+gbc.gridwidth);

    }    // addField

    /**
     * Descripción de Método
     *
     *
     * @param fieldGroup
     *
     * @return
     */

    private boolean addGroup( String fieldGroup ) {

        // First time - add top

        if( m_oldFieldGroup == null ) {
            addTop();
            m_oldFieldGroup = "";
        }

        if( (fieldGroup == null) || (fieldGroup.length() == 0) || fieldGroup.equals( m_oldFieldGroup )) {
            return false;
        }

        m_oldFieldGroup = fieldGroup;

        CPanel group = new CPanel();

        group.setBorder( new VLine( fieldGroup ));
        group.add( Box.createVerticalStrut( VLine.SPACE ));
        m_gbc.gridx     = 0;
        m_gbc.gridy     = m_line++;
        m_gbc.gridwidth = 4;
        this.add( group,m_gbc );

        // reset

        m_gbc.gridwidth = 1;

        return true;
    }    // addGroup

    /**
     * Descripción de Método
     *
     */

    private void addTop() {

        // Top Gap

        m_gbc.gridy = m_line++;
        this.add( Box.createVerticalStrut( 10 ),m_gbc );    // top gap

        // Right gap

        m_gbc.gridx     = 4;    // 5th column
        m_gbc.gridwidth = 1;
        m_gbc.weightx   = 0;
        m_gbc.insets    = m_zeroInset;
        m_gbc.fill      = GridBagConstraints.NONE;
        this.add( Box.createHorizontalStrut( 12 ),m_gbc );
    }    // addTop

    /**
     * Descripción de Método
     *
     */

    public void addEnd() {
        m_gbc.gridx     = 0;
        m_gbc.gridy     = m_line;
        m_gbc.gridwidth = 1;
        m_gbc.insets    = m_zeroInset;
        m_gbc.fill      = GridBagConstraints.HORIZONTAL;
        m_gbc.weightx   = 0;

        //

        this.add( Box.createVerticalStrut( 9 ),m_gbc );    // botton gap
    }                                                      // addEnd

    /**
     * Descripción de Método
     *
     *
     * @param AD_Color_ID
     */

    public void setBackground( int AD_Color_ID ) {}    // setBackground
}    // VPanel



/*
 *  @(#)VPanel.java   02.07.07
 * 
 *  Fin del fichero VPanel.java
 *  
 *  Versión 2.2
 *
 */
