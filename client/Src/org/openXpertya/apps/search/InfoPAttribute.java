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



package org.openXpertya.apps.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JDialog;

import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VComboBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLine;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.grid.ed.VString;
import org.openXpertya.model.MAttribute;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class InfoPAttribute extends CDialog {

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public InfoPAttribute( JDialog parent ) {
        super( parent,Msg.getMsg( Env.getCtx(),"InfoPAttribute" ),true );

        try {
            jbInit();
            dynInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"InfoPAttribute",e );
        }

        AEnv.showCenterWindow( parent,this );
    }    // InfoPAttribute

    /** Descripción de Campos */

    private String m_query = "";

    /** Descripción de Campos */

    private ArrayList m_productEditors = new ArrayList();

    /** Descripción de Campos */

    private ArrayList m_instanceEditors = new ArrayList();

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( InfoPAttribute.class );

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    //

    /** Descripción de Campos */

    private CLabel serNoLabel = new CLabel( Msg.translate( Env.getCtx(),"SerNo" ));

    /** Descripción de Campos */

    private VString serNoField = new VString( "SerNo",false,false,true,10,20,null,null );

    /** Descripción de Campos */

    private CLabel lotLabel = new CLabel( Msg.translate( Env.getCtx(),"Lot" ));

    /** Descripción de Campos */

    private VString lotField = new VString( "Lot",false,false,true,10,20,null,null );

    /** Descripción de Campos */

    private VComboBox guaranteeDateSelection = null;

    /** Descripción de Campos */

    private VDate guaranteeDateField = new VDate( "GuaranteeDate",false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),"GuaranteeDate" ));

    /** Descripción de Campos */

    private CLabel lotLabel2 = new CLabel( Msg.translate( Env.getCtx(),"M_Lot_ID" ));

    /** Descripción de Campos */

    private VComboBox lotSelection = null;

    //

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.getContentPane().add( mainPanel,BorderLayout.CENTER );
        mainPanel.setLayout( mainLayout );
        mainPanel.add( centerPanel,BorderLayout.CENTER );
        centerPanel.setLayout( new ALayout());

        // ConfirmPanel

        confirmPanel.addActionListener( this );
        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {
        int row = addAttributes();

        //

        String s = Msg.translate( Env.getCtx(),"GuaranteeDate" );

        guaranteeDateSelection = new VComboBox( new Object[]{ s + " <",s + " =",s + " >" } );

        // guaranteeDateSelection.setPreferredSize();

        initLotSelection();

        // Fixed Instance Selection Fields

        centerPanel.add( serNoLabel,new ALayoutConstraint( row++,0 ));
        centerPanel.add( serNoField,null );
        centerPanel.add( lotLabel,new ALayoutConstraint( row++,0 ));
        centerPanel.add( lotField,null );
        centerPanel.add( lotLabel2,new ALayoutConstraint( row++,0 ));
        centerPanel.add( lotSelection,null );
        centerPanel.add( guaranteeDateSelection,new ALayoutConstraint( row++,0 ));
        centerPanel.add( guaranteeDateField,null );

        //

        Dimension d = centerPanel.getPreferredSize();

        d.width = 400;
        centerPanel.setPreferredSize( d );
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int addAttributes() {
        int               row   = 0;
        PreparedStatement pstmt = null;
        String            sql   = MRole.getDefault().addAccessSQL( "SELECT M_Attribute_ID, Name, Description, AttributeValueType, IsInstanceAttribute " + "FROM M_Attribute " + "WHERE IsActive='Y' " + "ORDER BY IsInstanceAttribute, Name","M_Attribute",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs           = pstmt.executeQuery();
            boolean   instanceLine = false;

            while( rs.next()) {
                int     attribute_ID        = rs.getInt( 1 );
                String  name                = rs.getString( 2 );
                String  description         = rs.getString( 3 );
                String  attributeValueType  = rs.getString( 4 );
                boolean isInstanceAttribute = "Y".equals( rs.getString( 5 ));

                // Instance

                if( !instanceLine && isInstanceAttribute ) {
                    CPanel group = new CPanel();

                    group.setBorder( new VLine( Msg.translate( Env.getCtx(),"IsInstanceAttribute" )));
                    group.add( Box.createVerticalStrut( VLine.SPACE ));
                    centerPanel.add( group,new ALayoutConstraint( row++,0 ));
                    instanceLine = true;
                }

                //

                CLabel label = new CLabel( name );

                if( (description != null) && (description.length() > 0) ) {
                    label.setToolTipText( description );
                }

                centerPanel.add( label,new ALayoutConstraint( row++,0 ));

                Component field = null;

                if( MAttribute.ATTRIBUTEVALUETYPE_List.equals( attributeValueType )) {
                    field = new VComboBox( getAttributeList( attribute_ID ));
                } else if( MAttribute.ATTRIBUTEVALUETYPE_Number.equals( attributeValueType )) {
                    field = new VNumber( name,false,false,true,DisplayType.Number,name );
                } else {
                    field = new VString( name,false,false,true,10,40,null,null );
                }

                label.setLabelFor( field );
                centerPanel.add( field,null );

                //

                field.setName( String.valueOf( attribute_ID ));

                if( isInstanceAttribute ) {
                    m_instanceEditors.add( field );
                } else {
                    m_productEditors.add( field );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"addProductAttributes - " + sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return row;
    }    // addProductAttributes

    /**
     * Descripción de Método
     *
     *
     * @param M_Attribute_ID
     *
     * @return
     */

    private KeyNamePair[] getAttributeList( int M_Attribute_ID ) {
        ArrayList list = new ArrayList();

        list.add( new KeyNamePair( -1,"" ));

        PreparedStatement pstmt = null;
        String            sql   = MRole.getDefault().addAccessSQL( "SELECT M_AttributeValue_ID, Value, Name " + "FROM M_AttributeValue " + "WHERE M_Attribute_ID=? " + "ORDER BY 2","M_AttributeValue",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,M_Attribute_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new KeyNamePair( rs.getInt( 1 ),rs.getString( 3 )));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAttributeList",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        KeyNamePair[] retValue = new KeyNamePair[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getAttributeList

    /**
     * Descripción de Método
     *
     */

    private void initLotSelection() {
        ArrayList list = new ArrayList();

        list.add( new KeyNamePair( -1,"" ));

        String sql = MRole.getDefault().addAccessSQL( "SELECT M_Lot_ID, Name FROM M_Lot WHERE IsActive='Y' ORDER BY 2","M_Lot",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new KeyNamePair( rs.getInt( 1 ),rs.getString( 2 )));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"initLotSelection",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Create List

        KeyNamePair[] items = new KeyNamePair[ list.size()];

        list.toArray( items );
        lotSelection = new VComboBox( items );
    }    // initLotSelection

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
            createQuery();
            dispose();
        } else if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            m_query = null;
            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String createQuery() {
        StringBuffer sb = new StringBuffer();

        // Serial No

        String s = serNoField.getText();

        if( (s != null) && (s.length() > 0) ) {
            sb.append( " AND asi.SerNo" );

            if( (s.indexOf( '%' ) == -1) && (s.indexOf( '_' ) == 1) ) {
                sb.append( "=" );
            } else {
                sb.append( " LIKE " );
            }

            sb.append( DB.TO_STRING( s ));
        }

        // Lot Number

        s = lotField.getText();

        if( (s != null) && (s.length() > 0) ) {
            sb.append( " AND asi.Lot" );

            if( (s.indexOf( '%' ) == -1) && (s.indexOf( '_' ) == 1) ) {
                sb.append( "=" );
            } else {
                sb.append( " LIKE " );
            }

            sb.append( DB.TO_STRING( s ));
        }

        // Lot ID

        KeyNamePair pp = ( KeyNamePair )lotSelection.getSelectedItem();

        if( (pp != null) && (pp.getKey() != -1) ) {
            int ID = pp.getKey();

            sb.append( " AND asi.M_Lot_ID=" ).append( ID );
        }

        // Guarantee Date

        Timestamp ts = ( Timestamp )guaranteeDateField.getValue();

        if( ts != null ) {
            sb.append( " AND TRUNC(asi.GuaranteeDate)" );

            int index = guaranteeDateSelection.getSelectedIndex();    // < = >

            if( index == 0 ) {
                sb.append( "<" );
            } else if( index == 1 ) {
                sb.append( "=" );
            } else {
                sb.append( ">" );
            }

            sb.append( DB.TO_DATE( ts,true ));
        }

        // Instance Editors

        for( int i = 0;i < m_instanceEditors.size();i++ ) {
            StringBuffer iAttr          = new StringBuffer();
            Component    c              = ( Component )m_instanceEditors.get( i );
            int          M_Attribute_ID = Integer.parseInt( c.getName());

            if( c instanceof VComboBox ) {
                VComboBox field = ( VComboBox )c;

                pp = ( KeyNamePair )field.getSelectedItem();

                if( (pp != null) && (pp.getKey() != -1) ) {
                    iAttr.append( "M_Attribute_ID=" ).append( M_Attribute_ID ).append( " AND M_AttributeValue_ID=" ).append( pp.getKey());
                }
            } else {
                VString field = ( VString )c;
                String  value = field.getText();

                if( (value != null) && (value.length() > 0) ) {
                    iAttr.append( "M_Attribute_ID=" ).append( M_Attribute_ID ).append( " AND Value" );

                    if( (value.indexOf( '%' ) == -1) && (value.indexOf( '_' ) == 1) ) {
                        iAttr.append( "=" );
                    } else {
                        iAttr.append( " LIKE " );
                    }

                    iAttr.append( DB.TO_STRING( value ));
                }
            }

            // Add to where

            if( iAttr.length() > 0 ) {
                sb.append( " AND asi.M_AttributeSetInstance_ID IN " + "(SELECT M_AttributeSetInstance_ID FROM M_AttributeInstance " + "WHERE " ).append( iAttr ).append( ")" );
            }
        }

        // finish Instance Attributes

        if( sb.length() > 0 ) {
            sb.insert( 0," AND EXISTS (SELECT * FROM M_Storage s" + " INNER JOIN M_AttributeSetInstance asi ON (s.M_AttributeSetInstance_ID=asi.M_AttributeSetInstance_ID) " + "WHERE s.M_Product_ID=p.M_Product_ID" );
            sb.append( ")" );
        }

        // Product Attributes

        for( int i = 0;i < m_productEditors.size();i++ ) {
            StringBuffer pAttr          = new StringBuffer();
            Component    c              = ( Component )m_productEditors.get( i );
            int          M_Attribute_ID = Integer.parseInt( c.getName());

            if( c instanceof VComboBox ) {
                VComboBox field = ( VComboBox )c;

                pp = ( KeyNamePair )field.getSelectedItem();

                if( (pp != null) && (pp.getKey() != -1) ) {
                    pAttr.append( "M_Attribute_ID=" ).append( M_Attribute_ID ).append( " AND M_AttributeValue_ID=" ).append( pp.getKey());
                }
            } else {
                VString field = ( VString )c;
                String  value = field.getText();

                if( (value != null) && (value.length() > 0) ) {
                    pAttr.append( "M_Attribute_ID=" ).append( M_Attribute_ID ).append( " AND Value" );

                    if( (value.indexOf( '%' ) == -1) && (value.indexOf( '_' ) == -1) ) {
                        pAttr.append( "=" );
                    } else {
                        pAttr.append( " LIKE " );
                    }

                    pAttr.append( DB.TO_STRING( value ));
                }
            }

            // Add to Where

            if( pAttr.length() > 0 ) {
                sb.append( " AND p.M_AttributeSetInstance_ID IN " + "(SELECT M_AttributeSetInstance_ID " + "FROM M_AttributeInstance WHERE " ).append( pAttr ).append( ")" );
            }
        }

        //

        m_query = null;

        if( sb.length() > 0 ) {
            m_query = sb.toString();
        }

        log.config( m_query );

        return m_query;
    }    // createQuery

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getWhereClause() {
        return m_query;
    }    // getQuery
}    // InfoPAttribute



/*
 *  @(#)InfoPAttribute.java   02.07.07
 * 
 *  Fin del fichero InfoPAttribute.java
 *  
 *  Versión 2.2
 *
 */
