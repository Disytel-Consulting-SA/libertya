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
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.compiere.plaf.CompiereColor;
import org.compiere.plaf.CompiereColorEditor;
import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;
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

public class VColor extends CButton implements VEditor,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param mTab
     * @param mandatory
     * @param isReadOnly
     */

    public VColor( MTab mTab,boolean mandatory,boolean isReadOnly ) {
        m_mTab = mTab;
        setMandatory( mandatory );
        setReadWrite( !isReadOnly );
        addActionListener( this );
    }    // VColor

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        m_mTab = null;
    }    // dispose

    /** Descripción de Campos */

    private MTab m_mTab;

    /** Descripción de Campos */

    private boolean m_mandatory;

//      private int             m_AD_Color_ID = 0;

    /** Descripción de Campos */

    private CompiereColor m_cc = null;

    /** Descripción de Campos */

    private Object m_value;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VColor.class );

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
     * @param value
     */

    public void setValue( Object value ) {
        log.config( "Value=" + value );
        m_value = value;
        m_cc    = getCompiereColor();

        // Display It

        setText( getDisplay());

        if( m_cc != null ) {
            setBackgroundColor( m_cc );
        } else {
            setOpaque( false );
            putClientProperty( CompierePLAF.BACKGROUND,null );
        }

        repaint();
    }    // setValue

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
        if( m_cc == null ) {
            return "-/-";
        }

        return " ";
    }    // getDisplay

    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    public void propertyChange( PropertyChangeEvent evt ) {

        // log.config( "VColor.propertyChange", evt);

        if( evt.getPropertyName().equals( org.openXpertya.model.MField.PROPERTY )) {
            setValue( evt.getNewValue());
            setBackground( false );
        }
    }    // propertyChange

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField ) {
        mField.setValueNoFire( false );    // fire every time
    }                                      // setField

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private CompiereColor getCompiereColor() {
        Integer AD_Color_ID = ( Integer )m_mTab.getValue( "AD_Color_ID" );

        log.fine( "AD_Color_ID=" + AD_Color_ID );

        CompiereColor cc = null;

        // Color Type

        String ColorType = ( String )m_mTab.getValue( "ColorType" );

        if( ColorType == null ) {
            log.fine( "No ColorType" );

            return null;
        }

        //

        if( ColorType.equals( CompiereColor.TYPE_FLAT )) {
            cc = new CompiereColor( getColor( true ),true );
        } else if( ColorType.equals( CompiereColor.TYPE_GRADIENT )) {
            Integer RepeatDistance = ( Integer )m_mTab.getValue( "RepeatDistance" );
            String StartPoint     = ( String )m_mTab.getValue( "StartPoint" );
            int    repeatDistance = (RepeatDistance == null)
                                    ?0
                                    :RepeatDistance.intValue();
            int    startPoint     = (StartPoint == null)
                                    ?0
                                    :Integer.parseInt( StartPoint );

            cc = new CompiereColor( getColor( true ),getColor( false ),startPoint,repeatDistance );
        } else if( ColorType.equals( CompiereColor.TYPE_LINES )) {
            BigDecimal LineWidth = ( BigDecimal )m_mTab.getValue( "LineWidth" );
            BigDecimal LineDistance = ( BigDecimal )m_mTab.getValue( "LineDistance" );
            int lineWidth    = (LineWidth == null)
                               ?0
                               :LineWidth.intValue();
            int lineDistance = (LineDistance == null)
                               ?0
                               :LineDistance.intValue();

            cc = new CompiereColor( getColor( false ),getColor( true ),lineWidth,lineDistance );
        } else if( ColorType.equals( CompiereColor.TYPE_TEXTURE )) {
            Integer AD_Image_ID = ( Integer )m_mTab.getValue( "AD_Image_ID" );
            String  url         = getURL( AD_Image_ID );

            if( url == null ) {
                return null;
            }

            BigDecimal ImageAlpha = ( BigDecimal )m_mTab.getValue( "ImageAlpha" );
            float compositeAlpha = (ImageAlpha == null)
                                   ?0.7f
                                   :ImageAlpha.floatValue();

            cc = new CompiereColor( url,getColor( true ),compositeAlpha );
        } else {
            return null;
        }

        log.fine( "CompiereColor=" + cc );

        return cc;
    }    // getCompiereColor

    /**
     * Descripción de Método
     *
     *
     * @param primary
     *
     * @return
     */

    private Color getColor( boolean primary ) {
        String add = primary
                     ?""
                     :"_1";

        // is either BD or Int

        Integer Red   = ( Integer )m_mTab.getValue( "Red" + add );
        Integer Green = ( Integer )m_mTab.getValue( "Green" + add );
        Integer Blue  = ( Integer )m_mTab.getValue( "Blue" + add );

        //

        int red   = (Red == null)
                    ?0
                    :Red.intValue();
        int green = (Green == null)
                    ?0
                    :Green.intValue();
        int blue  = (Blue == null)
                    ?0
                    :Blue.intValue();

        //

        return new Color( red,green,blue );
    }    // getColor

    /**
     * Descripción de Método
     *
     *
     * @param AD_Image_ID
     *
     * @return
     */

    private String getURL( Integer AD_Image_ID ) {
        if( (AD_Image_ID == null) || (AD_Image_ID.intValue() == 0) ) {
            return null;
        }

        //

        String retValue = null;
        String sql      = "SELECT ImageURL FROM AD_Image WHERE AD_Image_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Image_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VColor.getURL",e );
        }

        return retValue;
    }    // getURL

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Show Dialog

        CompiereColor cc = CompiereColorEditor.showDialog(( JFrame )Env.getParent( this ),m_cc );

        if( cc == null ) {
            log.info( "VColor.actionPerformed - no color" );

            return;
        }

        setBackgroundColor( cc );    // set Button
        repaint();

        // Update Values

        m_mTab.setValue( "ColorType",cc.getType());

        if( cc.isFlat()) {
            setColor( cc.getFlatColor(),true );
        } else if( cc.isGradient()) {
            setColor( cc.getGradientUpperColor(),true );
            setColor( cc.getGradientLowerColor(),false );
            m_mTab.setValue( "RepeatDistance",new BigDecimal( cc.getGradientRepeatDistance()));
            m_mTab.setValue( "StartPoint",String.valueOf( cc.getGradientStartPoint()));
        } else if( cc.isLine()) {
            setColor( cc.getLineBackColor(),true );
            setColor( cc.getLineColor(),false );
            m_mTab.getValue( "LineWidth" );
            m_mTab.getValue( "LineDistance" );
        } else if( cc.isTexture()) {
            setColor( cc.getTextureTaintColor(),true );

            // URL url = cc.getTextureURL();
            // m_mTab.setValue("AD_Image_ID");

            m_mTab.setValue( "ImageAlpha",new BigDecimal( cc.getTextureCompositeAlpha()));
        }

        m_cc = cc;
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param c
     * @param primary
     */

    private void setColor( Color c,boolean primary ) {
        String add = primary
                     ?""
                     :"_1";

        m_mTab.setValue( "Red" + add,new BigDecimal( c.getRed()));
        m_mTab.setValue( "Green" + add,new BigDecimal( c.getGreen()));
        m_mTab.setValue( "Blue" + add,new BigDecimal( c.getBlue()));
    }    // setColor
}    // VColor



/*
 *  @(#)VColor.java   02.07.07
 * 
 *  Fin del fichero VColor.java
 *  
 *  Versión 2.2
 *
 */
