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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.APanel;
import org.openXpertya.model.GeneralPO;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
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

public class VSortTab extends CPanel implements APanelTab,ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param WindowNo
     * @param AD_Table_ID
     * @param AD_ColumnSortOrder_ID
     * @param AD_ColumnSortYesNo_ID
     */

    public VSortTab( int WindowNo,int AD_Table_ID,int AD_ColumnSortOrder_ID,int AD_ColumnSortYesNo_ID ) {
        log.config( "VSortTab" );
        m_WindowNo = WindowNo;

        try {
            jbInit();
            dynInit( AD_Table_ID,AD_ColumnSortOrder_ID,AD_ColumnSortYesNo_ID );
        } catch( Exception e ) {
            log.log( Level.SEVERE,"VSortTab",e );
        }
    }    // VSortTab

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private String m_TableName = null;

    /** Descripción de Campos */

    private String m_ColumnSortName = null;

    /** Descripción de Campos */

    private String m_ColumnYesNoName = null;

    /** Descripción de Campos */

    private String m_KeyColumnName = null;

    /** Descripción de Campos */

    private String m_IdentifierColumnName = null;

    /** Descripción de Campos */

    private boolean m_IdentifierTranslated = false;

    /** Descripción de Campos */

    private String m_ParentColumnName = null;

    /** Descripción de Campos */

    private boolean m_saveSequence = false;

    /** Descripción de Campos */

    private APanel m_aPanel = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VSortTab.class );

    // UI variables

    /** Descripción de Campos */

    private GridBagLayout mainLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel noLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel yesLabel = new CLabel();

    /** Descripción de Campos */

    private CButton bAdd = new CButton();

    /** Descripción de Campos */

    private CButton bRemove = new CButton();

    /** Descripción de Campos */

    private CButton bUp = new CButton();

    /** Descripción de Campos */

    private CButton bDown = new CButton();

    //

    /** Descripción de Campos */

    private DefaultListModel noModel = new DefaultListModel();

    /** Descripción de Campos */

    private DefaultListModel yesModel = new DefaultListModel();

    /** Descripción de Campos */

    private JList noList = new JList( noModel );

    /** Descripción de Campos */

    private JList yesList = new JList( yesModel );

    /** Descripción de Campos */

    private JScrollPane noPane = new JScrollPane( noList );

    /** Descripción de Campos */

    private JScrollPane yesPane = new JScrollPane( yesList );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.setLayout( mainLayout );

        //

        noLabel.setText( "No" );
        yesLabel.setText( "Yes" );

        //

        bAdd.setIcon( Env.getImageIcon( "Detail24.gif" ));
        bAdd.setMargin( new Insets( 2,2,2,2 ));
        bAdd.addActionListener( this );
        bRemove.setIcon( Env.getImageIcon( "Parent24.gif" ));
        bRemove.setMargin( new Insets( 2,2,2,2 ));
        bRemove.addActionListener( this );
        bUp.setIcon( Env.getImageIcon( "Previous24.gif" ));
        bUp.setMargin( new Insets( 2,2,2,2 ));
        bUp.addActionListener( this );
        bDown.setIcon( Env.getImageIcon( "Next24.gif" ));
        bDown.setMargin( new Insets( 2,2,2,2 ));
        bDown.addActionListener( this );

        //
        // yesList.setBorder(BorderFactory.createLoweredBevelBorder());

        yesPane.setPreferredSize( new Dimension( 300,400 ));
        yesList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );

        // noList.setBorder(BorderFactory.createLoweredBevelBorder());

        noPane.setPreferredSize( new Dimension( 300,400 ));
        noList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );

        //

        this.add( noLabel,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        this.add( yesLabel,new GridBagConstraints( 2,0,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 0,0,0,0 ),0,0 ));
        this.add( bDown,new GridBagConstraints( 3,2,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 4,4,4,4 ),0,0 ));
        this.add( noPane,new GridBagConstraints( 0,1,1,3,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 4,4,4,4 ),0,0 ));
        this.add( yesPane,new GridBagConstraints( 2,1,1,3,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 4,4,4,4 ),0,0 ));
        this.add( bUp,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 4,4,4,4 ),0,0 ));
        this.add( bRemove,new GridBagConstraints( 1,2,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 4,4,4,4 ),0,0 ));
        this.add( bAdd,new GridBagConstraints( 1,1,1,1,0.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets( 4,4,4,4 ),0,0 ));
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param AD_ColumnSortOrder_ID
     * @param AD_ColumnSortYesNo_ID
     */

    private void dynInit( int AD_Table_ID,int AD_ColumnSortOrder_ID,int AD_ColumnSortYesNo_ID ) {
        String sql = "SELECT t.TableName, c.AD_Column_ID, c.ColumnName, e.Name,"    // 1..4
                     + "c.IsParent, c.IsKey, c.IsIdentifier, c.IsTranslated "                     // 4..8
                     + "FROM AD_Table t, AD_Column c, AD_Element e " + "WHERE t.AD_Table_ID=?"    // #1
                     + " AND t.AD_Table_ID=c.AD_Table_ID" + " AND (c.AD_Column_ID=? OR AD_Column_ID=?"    // #2..3
                     + " OR c.IsParent='Y' OR c.IsKey='Y' OR c.IsIdentifier='Y')" + " AND c.AD_Element_ID=e.AD_Element_ID";
        boolean trl = !Env.isBaseLanguage( Env.getCtx(),"AD_Element" );

        if( trl ) {
            sql = "SELECT t.TableName, c.AD_Column_ID, c.ColumnName, et.Name,"    // 1..4
                  + "c.IsParent, c.IsKey, c.IsIdentifier, c.IsTranslated "                          // 4..8
                  + "FROM AD_Table t, AD_Column c, AD_Element_Trl et " + "WHERE t.AD_Table_ID=?"    // #1
                  + " AND t.AD_Table_ID=c.AD_Table_ID" + " AND (c.AD_Column_ID=? OR AD_Column_ID=?"    // #2..3
                  + "     OR c.IsParent='Y' OR c.IsKey='Y' OR c.IsIdentifier='Y')" + " AND c.AD_Element_ID=et.AD_Element_ID" + " AND et.AD_Language=?";    // #4
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );
            pstmt.setInt( 2,AD_ColumnSortOrder_ID );
            pstmt.setInt( 3,AD_ColumnSortYesNo_ID );

            if( trl ) {
                pstmt.setString( 4,Env.getAD_Language( Env.getCtx()));
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                m_TableName = rs.getString( 1 );

                // Sort Column

                if( AD_ColumnSortOrder_ID == rs.getInt( 2 )) {
                    log.fine( "Sort=" + rs.getString( 1 ) + "." + rs.getString( 3 ));
                    m_ColumnSortName = rs.getString( 3 );
                    yesLabel.setText( rs.getString( 4 ));
                }

                // Optional YesNo

                else if( AD_ColumnSortYesNo_ID == rs.getInt( 2 )) {
                    log.fine( "YesNo=" + rs.getString( 1 ) + "." + rs.getString( 3 ));
                    m_ColumnYesNoName = rs.getString( 3 );
                }

                // Parent2

                else if( rs.getString( 5 ).equals( "Y" )) {
                    log.fine( "Parent=" + rs.getString( 1 ) + "." + rs.getString( 3 ));
                    m_ParentColumnName = rs.getString( 3 );
                }

                // KeyColumn

                else if( rs.getString( 6 ).equals( "Y" )) {
                    log.fine( "Key=" + rs.getString( 1 ) + "." + rs.getString( 3 ));
                    m_KeyColumnName = rs.getString( 3 );
                }

                // Identifier

                else if( rs.getString( 7 ).equals( "Y" )) {
                    log.fine( "Identifier=" + rs.getString( 1 ) + "." + rs.getString( 3 ));
                    m_IdentifierColumnName = rs.getString( 3 );

                    if( trl ) {
                        m_IdentifierTranslated = "Y".equals( rs.getString( 8 ));
                    }
                } else {
                    log.fine( "??NotUsed??=" + rs.getString( 1 ) + "." + rs.getString( 3 ));
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VSortTab.dynInit\nSQL=" + sql.toString(),e );
        }

        noLabel.setText( Msg.getMsg( Env.getCtx(),"Available" ));
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param panel
     */

    public void registerAPanel( APanel panel ) {
        m_aPanel = panel;
    }    // registerAPanel

    /**
     * Descripción de Método
     *
     */

    public void unregisterPanel() {
        saveData();
        m_aPanel = null;
    }    // dispoase

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // ADD     ->

        if( e.getSource() == bAdd ) {
            Object objects[] = noList.getSelectedValues();

            for( int i = 0;i < objects.length;i++ ) {
                if( (objects[ i ] != null) && noModel.removeElement( objects[ i ] )) {
                    log.config( "Add=" + objects[ i ] );
                    yesModel.addElement( objects[ i ] );
                    yesList.setSelectedValue( objects[ i ],true );
                    m_saveSequence = true;
                }
            }
        }

        // REMOVE  <-

        else if( e.getSource() == bRemove ) {
            Object objects[] = yesList.getSelectedValues();

            for( int i = 0;i < objects.length;i++ ) {
                if( (objects[ i ] != null) && yesModel.removeElement( objects[ i ] )) {
                    log.config( "Remove=" + objects[ i ] );
                    noModel.addElement( objects[ i ] );
                    m_saveSequence = true;
                }
            }
        }

        // UP      |

        else if( e.getSource() == bUp ) {
            int    indexes[] = yesList.getSelectedIndices();
            Object objects[] = yesList.getSelectedValues();

            for( int i = 0;i < indexes.length;i++ ) {
                if( indexes[ i ] > 0 ) {
                    Object obj = yesList.getSelectedValue();

                    log.config( "Up=" + obj );

                    if( yesModel.removeElement( obj )) {
                        yesModel.insertElementAt( obj,indexes[ i ] - 1 );
                    }

                    m_saveSequence = true;
                    indexes[ i ]--;
                }
            }

            yesList.setSelectedIndices( indexes );
        }

        // DOWN    |

        else if( e.getSource() == bDown ) {
            int indexes[] = yesList.getSelectedIndices();

            for( int i = 0;i < indexes.length;i++ ) {
                if( (indexes[ i ] != -1) && (indexes[ i ] < yesModel.size() - 1) ) {
                    Object obj = yesList.getSelectedValue();

                    log.config( "Down=" + obj );

                    if( yesModel.removeElement( obj )) {
                        yesModel.insertElementAt( obj,indexes[ i ] + 1 );
                    }

                    m_saveSequence = true;
                    indexes[ i ]++;
                }
            }

            yesList.setSelectedIndices( indexes );
        }

        // Enable explicit Save

        if( m_saveSequence && (m_aPanel != null) ) {
            m_aPanel.aSave.setEnabled( true );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void loadData() {
        yesModel.removeAllElements();
        noModel.removeAllElements();

        // SELECT t.AD_Field_ID,t.Name,t.SeqNo,t.IsDisplayed FROM AD_Field t WHERE t.AD_Tab_ID=? ORDER BY 4 DESC,3,2
        // SELECT t.AD_PrintFormatItem_ID,t.Name,t.SeqNo,t.IsPrinted FROM AD_PrintFormatItem t WHERE t.AD_PrintFormat_ID=? ORDER BY 4 DESC,3,2
        // SELECT t.AD_PrintFormatItem_ID,t.Name,t.SortNo,t.IsOrderBy FROM AD_PrintFormatItem t WHERE t.AD_PrintFormat_ID=? ORDER BY 4 DESC,3,2

        StringBuffer sql = new StringBuffer();

        // Columns

        sql.append( "SELECT t." ).append( m_KeyColumnName )              // 1
            .append( m_IdentifierTranslated
                     ?",tt."
                     :",t." ).append( m_IdentifierColumnName )           // 2
                         .append( ",t." ).append( m_ColumnSortName );    // 3

        if( m_ColumnYesNoName != null ) {
            sql.append( ",t." ).append( m_ColumnYesNoName );    // 4
        }

        // Tables

        sql.append( " FROM " ).append( m_TableName ).append( " t" );

        if( m_IdentifierTranslated ) {
            sql.append( ", " ).append( m_TableName ).append( "_Trl tt" );
        }

        // Where

        sql.append( " WHERE t." ).append( m_ParentColumnName ).append( "=?" );

        if( m_IdentifierTranslated ) {
            sql.append( " AND t." ).append( m_KeyColumnName ).append( "=tt." ).append( m_KeyColumnName ).append( " AND tt.AD_Language=?" );
        }

        // Order

        sql.append( " ORDER BY " );

        if( m_ColumnYesNoName != null ) {
            sql.append( "4 DESC," );    // t.IsDisplayed DESC
        }

        sql.append( "3,2" );    // t.SeqNo, tt.Name

        int ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,m_ParentColumnName );

        log.config( sql.toString() + " - ID=" + ID );

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql.toString());

            pstmt.setInt( 1,ID );

            if( m_IdentifierTranslated ) {
                pstmt.setString( 2,Env.getAD_Language( Env.getCtx()));
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int     key   = rs.getInt( 1 );
                String  name  = rs.getString( 2 );
                int     seq   = rs.getInt( 3 );
                boolean isYes = seq != 0;

                if( m_ColumnYesNoName != null ) {
                    isYes = rs.getString( 4 ).equals( "Y" );
                }

                //

                KeyNamePair pp = new KeyNamePair( key,name );

                if( isYes ) {
                    yesModel.addElement( pp );
                } else {
                    noModel.addElement( pp );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"VSortTab.loadData",e );
        }

        m_saveSequence = false;
    }    // loadData

    /**
     * Descripción de Método
     *
     */

    public void saveData() {
        if( !m_saveSequence ) {
            return;
        }

        log.info( "VSortTab.saveData" );

        StringBuffer sql = null;

        // noList - Set SortColumn to null and optional YesNo Column to 'N'

        for( int i = 0;i < noModel.getSize();i++ ) {
            KeyNamePair pp = ( KeyNamePair )noModel.getElementAt( i );
           
            GeneralPO po = new GeneralPO(Env.getCtx(), pp.getKey(), null, M_Table.getTableID(m_TableName), null);
            po.set_Value(m_ColumnSortName, new BigDecimal(0));
            po.set_Value(m_ColumnYesNoName, false);
            if (!po.save())
            	log.log( Level.SEVERE,"VSortTab.saveData - NoModel - Not updated: " + m_KeyColumnName + "=" + pp.getKey());
            
        }

        // yesList - Set SortColumn to value and optional YesNo Column to 'Y'

        for( int i = 0;i < yesModel.getSize();i++ ) {
            KeyNamePair pp = ( KeyNamePair )yesModel.getElementAt( i );

            GeneralPO po = new GeneralPO(Env.getCtx(), pp.getKey(), null, M_Table.getTableID(m_TableName), null);
            po.set_Value(m_ColumnSortName, new BigDecimal((i + 1) * 10));
            po.set_Value(m_ColumnYesNoName, true);
            if (!po.save())
            	log.log( Level.SEVERE,"VSortTab.saveData - NoModel - Not updated: " + m_KeyColumnName + "=" + pp.getKey());
        }
    }    // saveData
}    // VSortTab



/*
 *  @(#)VSortTab.java   02.07.07
 * 
 *  Fin del fichero VSortTab.java
 *  
 *  Versión 2.2
 *
 */
